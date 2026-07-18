/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import java.util.jar.Attributes;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import org.microemu.EmulatorContext;
import org.microemu.Injected;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;
import org.microemu.MIDletContext;
import org.microemu.MIDletEntry;
import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.app.CommonInterface;
import org.microemu.app.Config;
import org.microemu.app.ConfigurationException;
import org.microemu.app.classloader.ExtensionsClassLoader;
import org.microemu.app.classloader.MIDletClassLoader;
import org.microemu.app.classloader.MIDletClassLoaderConfig;
import org.microemu.app.launcher.Launcher;
import org.microemu.app.ui.Message;
import org.microemu.app.ui.ResponseInterfaceListener;
import org.microemu.app.ui.StatusBarListener;
import org.microemu.app.util.DeviceEntry;
import org.microemu.app.util.FileRecordStoreManager;
import org.microemu.app.util.IOUtils;
import org.microemu.app.util.MIDletResourceLoader;
import org.microemu.app.util.MIDletSystemProperties;
import org.microemu.app.util.MIDletThread;
import org.microemu.app.util.MIDletTimer;
import org.microemu.app.util.MIDletTimerTask;
import org.microemu.app.util.MidletURLReference;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.DeviceDisplayImpl;
import org.microemu.device.impl.DeviceImpl;
import org.microemu.device.impl.Rectangle;
import org.microemu.log.Logger;
import org.microemu.log.StdOutAppender;
import org.microemu.microedition.ImplFactory;
import org.microemu.microedition.ImplementationInitialization;
import org.microemu.microedition.io.ConnectorImpl;
import org.microemu.util.Base64Coder;
import org.microemu.util.JadMidletEntry;
import org.microemu.util.JadProperties;
import org.microemu.util.MemoryRecordStoreManager;

public class Common
implements MicroEmulator,
CommonInterface {
    protected EmulatorContext emulatorContext;
    protected JadProperties jad = new JadProperties();
    private static Common instance;
    private static Launcher launcher;
    private static StatusBarListener statusBarListener;
    private JadProperties manifest = new JadProperties();
    private RecordStoreManager recordStoreManager;
    private ResponseInterfaceListener responseInterfaceListener = null;
    private ExtensionsClassLoader extensionsClassLoader;
    private Vector extensions = new Vector();
    private MIDletClassLoaderConfig mIDletClassLoaderConfig;
    private boolean useSystemClassLoader = false;
    private boolean autoTests = false;
    private String propertiesJad = null;
    private String midletClassOrUrl = null;
    private String jadURL = null;
    private Object destroyNotify = new Object();
    private boolean exitOnMIDletDestroy = false;

    public Common(EmulatorContext context) {
        instance = this;
        this.emulatorContext = context;
        ImplFactory.instance();
        MIDletSystemProperties.initContext();
        ImplFactory.registerGCF("org.microemu.default", new ConnectorImpl());
        MIDletBridge.setMicroEmulator(this);
    }

    public RecordStoreManager getRecordStoreManager() {
        return this.recordStoreManager;
    }

    public void setRecordStoreManager(RecordStoreManager manager) {
        this.recordStoreManager = manager;
    }

    public String getAppProperty(String key) {
        if (key.equals("microedition.platform")) {
            return "MicroEmulator";
        }
        if (key.equals("microedition.profiles")) {
            return "MIDP-2.0";
        }
        if (key.equals("microedition.configuration")) {
            return "CLDC-1.0";
        }
        if (key.equals("microedition.locale")) {
            return Locale.getDefault().getLanguage();
        }
        if (key.equals("microedition.encoding")) {
            return System.getProperty("file.encoding");
        }
        String result = this.jad.getProperty(key);
        if (result == null) {
            result = this.manifest.getProperty(key);
        }
        return result;
    }

    public InputStream getResourceAsStream(String name) {
        return this.emulatorContext.getResourceAsStream(name);
    }

    public void notifyDestroyed(MIDletContext midletContext) {
        Logger.debug("notifyDestroyed");
        this.notifyImplementationMIDletDestroyed();
        this.startLauncher(midletContext);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroyMIDletContext(MIDletContext midletContext) {
        if (midletContext != null && MIDletBridge.getMIDletContext() == midletContext && !midletContext.isLauncher()) {
            Logger.debug("destroyMIDletContext");
        }
        MIDletThread.contextDestroyed(midletContext);
        Object object = this.destroyNotify;
        synchronized (object) {
            this.destroyNotify.notifyAll();
        }
    }

    public Launcher getLauncher() {
        return launcher;
    }

    public static void dispose() {
        try {
            MIDletAccess midletAccess = MIDletBridge.getMIDletAccess();
            if (midletAccess != null) {
                midletAccess.destroyApp(true);
            }
        }
        catch (MIDletStateChangeException ex) {
            Logger.error(ex);
        }
        DeviceFactory.getDevice().getInputMethod().dispose();
    }

    public static boolean isMIDletUrlExtension(String nameString) {
        int end;
        int s;
        if (nameString == null) {
            return false;
        }
        if ((nameString.startsWith("http://") || nameString.startsWith("https://")) && (s = nameString.lastIndexOf(63)) != -1) {
            nameString = nameString.substring(0, s);
        }
        if ((end = nameString.lastIndexOf(46)) == -1) {
            return false;
        }
        return nameString.substring(end + 1, nameString.length()).toLowerCase(Locale.ENGLISH).equals("jad") || nameString.substring(end + 1, nameString.length()).toLowerCase(Locale.ENGLISH).equals("jar");
    }

    public static void openMIDletUrlSafe(String urlString) {
        try {
            Common.getInstance().openMIDletUrl(urlString);
        }
        catch (IOException e) {
            Message.error("Unable to open jad " + urlString, e);
        }
    }

    protected void openMIDletUrl(String urlString) throws IOException {
        this.midletClassOrUrl = urlString;
        if (!this.autoTests) {
            this.openMIDletUrl(urlString, this.createMIDletClassLoader(true));
        } else {
            this.runAutoTests(urlString, false);
        }
    }

    private void runAutoTests(final String urlString, final boolean exitAtTheEnd) {
        final Common common = Common.getInstance();
        Thread t = new Thread("AutoTestsThread"){

            /*
             * Exception decompiling
             */
            public void run() {
                /*
                 * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                 * 
                 * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [11[UNCONDITIONALDOLOOP]], but top level block is 10[CATCHBLOCK]
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
                 *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
                 *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseInnerClassesPass1(ClassFile.java:923)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1035)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
                 *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
                 *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
                 *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
                 *     at org.benf.cfr.reader.Main.main(Main.java:54)
                 */
                throw new IllegalStateException("Decompilation failed");
            }
        };
        t.start();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected String saveJar2TmpFile(String jarUrl, boolean reportError) {
        InputStream is = null;
        try {
            URL url = new URL(this.jad.getJarURL());
            URLConnection conn = url.openConnection();
            if (url.getUserInfo() != null) {
                String userInfo = new String(Base64Coder.encode(url.getUserInfo().getBytes("UTF-8")));
                conn.setRequestProperty("Authorization", "Basic " + userInfo);
            }
            is = conn.getInputStream();
            File tmpDir = null;
            String systemTmpDir = MIDletSystemProperties.getSystemProperty("java.io.tmpdir");
            if (systemTmpDir != null && !(tmpDir = new File(systemTmpDir, "microemulator-apps-" + MIDletSystemProperties.getSystemProperty("user.name"))).exists() && !tmpDir.mkdirs()) {
                tmpDir = null;
            }
            File tmp = File.createTempFile("me2-app-", ".jar", tmpDir);
            tmp.deleteOnExit();
            IOUtils.copyToFile(is, tmp);
            String string = IOUtils.getCanonicalFileClassLoaderURL(tmp);
            IOUtils.closeQuietly(is);
            return string;
        }
        catch (IOException e) {
            if (reportError) {
                Message.error("Unable to open jar " + jarUrl, e);
            }
            String string = null;
            return string;
        }
        finally {
            IOUtils.closeQuietly(is);
        }
    }

    private void openMIDletUrl(String urlString, MIDletClassLoader midletClassLoader) throws IOException {
        try {
            Common.setStatusBar("Loading...");
            this.jad.clear();
            if (urlString.toLowerCase().endsWith(".jad")) {
                Logger.debug("openJad", urlString);
                this.jad = Common.loadJadProperties(urlString);
                this.loadJar(urlString, this.jad.getJarURL(), midletClassLoader);
            } else {
                this.jad.setCorrectedJarURL(urlString);
                this.loadJar(null, urlString, midletClassLoader);
            }
            Config.getUrlsMRU().push(new MidletURLReference(this.jad.getSuiteName(), urlString));
        }
        catch (MalformedURLException ex) {
            throw ex;
        }
        catch (ClassNotFoundException ex) {
            Logger.error(ex);
            throw new IOException(ex.getMessage());
        }
        catch (FileNotFoundException ex) {
            Message.error("File Not found", urlString, ex);
        }
        catch (NullPointerException ex) {
            Logger.error("Cannot open jad", urlString, ex);
        }
        catch (IllegalArgumentException ex) {
            Logger.error("Cannot open jad", urlString, ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private MIDletContext startMidlet(Class midletClass, MIDletAccess previousMidletAccess) {
        try {
            if (previousMidletAccess != null) {
                previousMidletAccess.destroyApp(true);
            }
        }
        catch (Throwable e) {
            Message.error("Unable to destroy MIDlet, " + Message.getCauseMessage(e), e);
        }
        MIDletContext context = new MIDletContext();
        MIDletBridge.setThreadMIDletContext(context);
        MIDletBridge.getRecordStoreManager().init(MIDletBridge.getMicroEmulator());
        try {
            Object object;
            block14: {
                String errorTitle = "Error starting MIDlet";
                try {
                    object = midletClass.newInstance();
                    if (object instanceof MIDlet) break block14;
                    Message.error("Error starting MIDlet", "Class " + midletClass.getName() + " should extend MIDlet");
                    MIDletContext mIDletContext = null;
                    return mIDletContext;
                }
                catch (Throwable e) {
                    Message.error("Error starting MIDlet", "Unable to create MIDlet, " + Message.getCauseMessage(e), e);
                    MIDletBridge.destroyMIDletContext(context);
                    MIDletContext mIDletContext = null;
                    MIDletBridge.setThreadMIDletContext(null);
                    return mIDletContext;
                }
            }
            MIDlet m = (MIDlet)object;
            if (context.getMIDlet() != m) {
                throw new Error("MIDlet Context corrupted");
            }
            context.getMIDletAccess().startApp();
            launcher.setCurrentMIDlet(m);
            this.notifyImplementationMIDletStart();
            MIDletContext e = context;
            return e;
        }
        finally {
            MIDletBridge.setThreadMIDletContext(null);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void startLauncher(MIDletContext midletContext) {
        if (midletContext != null && midletContext.isLauncher()) {
            return;
        }
        if (midletContext != null) {
            try {
                MIDletAccess previousMidletAccess = midletContext.getMIDletAccess();
                if (previousMidletAccess != null) {
                    previousMidletAccess.destroyApp(true);
                }
            }
            catch (Throwable e) {
                Logger.error("destroyApp error", e);
            }
            if (this.exitOnMIDletDestroy) {
                System.exit(0);
            }
        }
        try {
            launcher = new Launcher(this);
            MIDletBridge.getMIDletAccess(launcher).startApp();
            launcher.setCurrentMIDlet(launcher);
        }
        catch (Throwable e) {
            Message.error("Unable to start launcher MIDlet, " + Message.getCauseMessage(e), e);
            this.handleStartMidletException(e);
        }
        finally {
            MIDletBridge.setThreadMIDletContext(null);
        }
    }

    public void setStatusBarListener(StatusBarListener listener) {
        statusBarListener = listener;
    }

    public int checkPermission(String permission) {
        return MIDletSystemProperties.getPermission(permission);
    }

    public boolean platformRequest(String URL2) {
        return this.emulatorContext.platformRequest(URL2);
    }

    public void setResponseInterfaceListener(ResponseInterfaceListener listener) {
        this.responseInterfaceListener = listener;
    }

    protected void handleStartMidletException(Throwable e) {
    }

    /*
     * Exception decompiling
     */
    protected boolean describeJarProblem(URL jarUrl, MIDletClassLoader midletClassLoader) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 4 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    protected void loadJar(String jadUrl, String jarUrl, MIDletClassLoader midletClassLoader) throws ClassNotFoundException {
        block28: {
            if (jarUrl == null) {
                throw new ClassNotFoundException("Cannot find MIDlet-Jar-URL property in jad");
            }
            Logger.debug("openJar", jarUrl);
            Common.dispose();
            MIDletBridge.clear();
            this.setResponseInterface(false);
            try {
                URL url = null;
                try {
                    url = new URL(jarUrl);
                }
                catch (MalformedURLException ex) {
                    if (jadUrl != null) {
                        try {
                            url = new URL(jadUrl.substring(0, jadUrl.lastIndexOf(47) + 1) + jarUrl);
                            this.jad.setCorrectedJarURL(url.toExternalForm());
                            Logger.debug("openJar url", url);
                        }
                        catch (MalformedURLException ex1) {
                            Logger.error("Unable to find jar url", ex1);
                            this.setResponseInterface(true);
                            this.setResponseInterface(true);
                            return;
                        }
                    }
                    Logger.error("Unable to find jar url", ex);
                    this.setResponseInterface(true);
                    this.setResponseInterface(true);
                    return;
                }
                if (url.getUserInfo() != null) {
                    String tmpURL = this.saveJar2TmpFile(jarUrl, true);
                    if (tmpURL == null) {
                        return;
                    }
                    try {
                        url = new URL(tmpURL);
                    }
                    catch (MalformedURLException e) {
                        Logger.error("Unable to open tmporary jar url", e);
                    }
                }
                midletClassLoader.addURL(url);
                Launcher.removeMIDletEntries();
                this.manifest.clear();
                InputStream is = null;
                try {
                    is = midletClassLoader.getResourceAsStream("META-INF/MANIFEST.MF");
                    if (is == null) {
                        if (!this.describeJarProblem(url, midletClassLoader)) {
                            Message.error("Unable to find MANIFEST in MIDlet jar");
                        }
                        return;
                    }
                    this.manifest.read(is);
                    Attributes attributes = this.manifest.getMainAttributes();
                    Iterator<Object> it = attributes.keySet().iterator();
                    while (it.hasNext()) {
                        Attributes.Name key = (Attributes.Name)it.next();
                        String value = (String)attributes.get(key);
                        this.jad.getMainAttributes().put(key, value);
                    }
                }
                catch (IOException e) {
                    Message.error("Unable to read MANIFEST", e);
                }
                finally {
                    IOUtils.closeQuietly(is);
                }
                Launcher.setSuiteName(this.jad.getSuiteName());
                Enumeration e = this.jad.getMidletEntries().elements();
                while (e.hasMoreElements()) {
                    JadMidletEntry jadEntry = (JadMidletEntry)e.nextElement();
                    Class<?> midletClass = midletClassLoader.loadClass(jadEntry.getClassName());
                    Launcher.addMIDletEntry(new MIDletEntry(jadEntry.getName(), midletClass));
                }
                this.startLauncher(MIDletBridge.getMIDletContext());
                Common.setStatusBar("");
                break block28;
                {
                    catch (Throwable throwable) {
                        throw throwable;
                    }
                }
            }
            finally {
                this.setResponseInterface(true);
            }
        }
    }

    public Device getDevice() {
        return DeviceFactory.getDevice();
    }

    public void setDevice(Device device) {
        MIDletSystemProperties.setDevice(device);
        DeviceFactory.setDevice(device);
    }

    private static Common getInstance() {
        return instance;
    }

    public static void setStatusBar(String text) {
        if (statusBarListener != null) {
            statusBarListener.statusBarChanged(text);
        }
    }

    private void setResponseInterface(boolean state) {
        if (this.responseInterfaceListener != null) {
            this.responseInterfaceListener.stateChanged(state);
        }
    }

    public void registerImplementation(String implClassName, Map properties, boolean notFoundError) {
        block17: {
            String errorText = "Implementation initialization";
            try {
                Class<?> implClass = Common.getExtensionsClassLoader().loadClass(implClassName);
                if (ImplementationInitialization.class.isAssignableFrom(implClass)) {
                    Object inst = implClass.newInstance();
                    HashMap<String, String> parameters = new HashMap<String, String>();
                    parameters.put("emulatorID", Config.getEmulatorID());
                    if (properties != null) {
                        parameters.putAll(properties);
                    } else {
                        Map extensions = Config.getExtensions();
                        Map prop = (Map)extensions.get(implClassName);
                        if (prop != null) {
                            parameters.putAll(prop);
                        }
                    }
                    ((ImplementationInitialization)inst).registerImplementation(parameters);
                    Logger.debug("implementation registered", implClassName);
                    this.extensions.add(inst);
                    break block17;
                }
                Logger.debug("initialize implementation", implClassName);
                boolean isStatic = true;
                try {
                    Constructor<?> c = implClass.getConstructor(null);
                    if (Modifier.isPublic(c.getModifiers())) {
                        isStatic = false;
                        implClass.newInstance();
                    }
                }
                catch (NoSuchMethodException e) {
                    // empty catch block
                }
                if (!isStatic) break block17;
                try {
                    Method getinst = implClass.getMethod("instance", null);
                    if (Modifier.isStatic(getinst.getModifiers())) {
                        getinst.invoke(implClass, null);
                        break block17;
                    }
                    Logger.debug("No known way to initialize implementation class");
                }
                catch (NoSuchMethodException e) {
                    Logger.debug("No known way to initialize implementation class");
                }
                catch (InvocationTargetException e) {
                    Logger.debug("Unable to initialize Implementation", e.getCause());
                }
            }
            catch (ClassNotFoundException e) {
                if (notFoundError) {
                    Logger.error("Implementation initialization", e);
                } else {
                    Logger.warn("Implementation initialization " + e);
                }
            }
            catch (InstantiationException e) {
                Logger.error("Implementation initialization", e);
            }
            catch (IllegalAccessException e) {
                Logger.error("Implementation initialization", e);
            }
        }
    }

    public void loadImplementationsFromConfig() {
        Map extensions = Config.getExtensions();
        Iterator iterator = extensions.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = iterator.next();
            this.registerImplementation((String)entry.getKey(), (Map)entry.getValue(), false);
        }
    }

    public void notifyImplementationMIDletStart() {
        Iterator iterator = this.extensions.iterator();
        while (iterator.hasNext()) {
            ImplementationInitialization impl = (ImplementationInitialization)iterator.next();
            impl.notifyMIDletStart();
        }
    }

    public void notifyImplementationMIDletDestroyed() {
        Iterator iterator = this.extensions.iterator();
        while (iterator.hasNext()) {
            ImplementationInitialization impl = (ImplementationInitialization)iterator.next();
            impl.notifyMIDletDestroyed();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean initParams(List params, DeviceEntry defaultDevice, Class defaultDeviceClass) {
        boolean defaultDeviceSelected = false;
        MIDletClassLoaderConfig clConfig = new MIDletClassLoaderConfig();
        Class<?> deviceClass = null;
        String deviceDescriptorLocation = null;
        int overrideDeviceWidth = -1;
        int overrideDeviceHeight = -1;
        RecordStoreManager paramRecordStoreManager = null;
        Iterator argsIterator = params.iterator();
        try {
            while (argsIterator.hasNext()) {
                String arg = (String)argsIterator.next();
                argsIterator.remove();
                if (arg.equals("--help") || arg.equals("-help")) {
                    System.out.println(Common.usage());
                    System.exit(0);
                    continue;
                }
                if (arg.equals("--id")) {
                    Config.setEmulatorID((String)argsIterator.next());
                    argsIterator.remove();
                    continue;
                }
                if (arg.equals("--appclasspath") || arg.equals("-appclasspath") || arg.equals("-appcp")) {
                    if (clConfig == null) {
                        throw new ConfigurationException("Wrong command line argument order");
                    }
                    clConfig.addAppClassPath((String)argsIterator.next());
                    argsIterator.remove();
                    continue;
                }
                if (arg.equals("--appclass")) {
                    if (clConfig == null) {
                        throw new ConfigurationException("Wrong command line argument order");
                    }
                    clConfig.addAppClass((String)argsIterator.next());
                    argsIterator.remove();
                    continue;
                }
                if (arg.startsWith("-Xautotest:")) {
                    this.autoTests = true;
                    this.jadURL = arg.substring("-Xautotest:".length());
                    continue;
                }
                if (arg.equals("-Xautotest")) {
                    this.autoTests = true;
                    continue;
                }
                if (arg.equals("--propertiesjad")) {
                    File file = new File((String)argsIterator.next());
                    argsIterator.remove();
                    this.propertiesJad = file.exists() ? IOUtils.getCanonicalFileURL(file) : arg;
                    continue;
                }
                if (arg.equals("--appclassloader")) {
                    if (clConfig == null) {
                        Message.error("Error", "Wrong command line argument order");
                        break;
                    }
                    clConfig.setDelegationType((String)argsIterator.next());
                    argsIterator.remove();
                    continue;
                }
                if (arg.equals("--usesystemclassloader")) {
                    this.useSystemClassLoader = true;
                    clConfig.setDelegationType("system");
                    continue;
                }
                if (arg.equals("-d") || arg.equals("--device")) {
                    if (!argsIterator.hasNext()) continue;
                    String tmpDevice = (String)argsIterator.next();
                    argsIterator.remove();
                    if (!tmpDevice.toLowerCase().endsWith(".xml")) {
                        try {
                            deviceClass = Class.forName(tmpDevice);
                        }
                        catch (ClassNotFoundException ex) {
                            // empty catch block
                        }
                    }
                    if (deviceClass != null) continue;
                    deviceDescriptorLocation = tmpDevice;
                    continue;
                }
                if (arg.equals("--resizableDevice")) {
                    overrideDeviceWidth = Integer.parseInt((String)argsIterator.next());
                    argsIterator.remove();
                    overrideDeviceHeight = Integer.parseInt((String)argsIterator.next());
                    argsIterator.remove();
                    deviceDescriptorLocation = "org/microemu/device/resizable/device.xml";
                    continue;
                }
                if (arg.equals("--rms")) {
                    if (!argsIterator.hasNext()) continue;
                    String tmpRms = (String)argsIterator.next();
                    argsIterator.remove();
                    if (tmpRms.equals("file")) {
                        paramRecordStoreManager = new FileRecordStoreManager();
                        continue;
                    }
                    if (!tmpRms.equals("memory")) continue;
                    paramRecordStoreManager = new MemoryRecordStoreManager();
                    continue;
                }
                if (arg.equals("--classpath") || arg.equals("-classpath") || arg.equals("-cp")) {
                    Common.getExtensionsClassLoader().addClasspath((String)argsIterator.next());
                    argsIterator.remove();
                    continue;
                }
                if (arg.equals("--impl")) {
                    this.registerImplementation((String)argsIterator.next(), null, true);
                    argsIterator.remove();
                    continue;
                }
                if (arg.equals("--quit")) {
                    this.exitOnMIDletDestroy = true;
                    continue;
                }
                if (arg.equals("--logCallLocation")) {
                    Logger.setLocationEnabled(Boolean.valueOf((String)argsIterator.next()));
                    continue;
                }
                if (arg.equals("--traceClassLoading")) {
                    MIDletClassLoader.traceClassLoading = true;
                    continue;
                }
                if (arg.equals("--traceSystemClassLoading")) {
                    MIDletClassLoader.traceSystemClassLoading = true;
                    continue;
                }
                if (arg.equals("--enhanceCatchBlock")) {
                    MIDletClassLoader.enhanceCatchBlock = true;
                    continue;
                }
                if (arg.equals("--quiet")) {
                    StdOutAppender.enabled = false;
                    continue;
                }
                if (arg.equals("--headless")) continue;
                if (arg.startsWith("--")) {
                    Logger.warn("Unknown argument " + arg);
                    continue;
                }
                this.midletClassOrUrl = arg;
            }
        }
        catch (ConfigurationException e) {
            Message.error("Error", e.getMessage(), e);
            return defaultDeviceSelected;
        }
        this.mIDletClassLoaderConfig = clConfig;
        ClassLoader classLoader = Common.getExtensionsClassLoader();
        if (deviceDescriptorLocation != null) {
            try {
                this.setDevice(DeviceImpl.create(this.emulatorContext, classLoader, deviceDescriptorLocation, defaultDeviceClass));
                DeviceDisplayImpl deviceDisplay = (DeviceDisplayImpl)DeviceFactory.getDevice().getDeviceDisplay();
                if (overrideDeviceWidth != -1 && overrideDeviceHeight != -1) {
                    deviceDisplay.setDisplayRectangle(new Rectangle(0, 0, overrideDeviceWidth, overrideDeviceHeight));
                }
            }
            catch (IOException ex) {
                Logger.error(ex);
            }
        }
        if (DeviceFactory.getDevice() == null) {
            try {
                if (deviceClass == null) {
                    if (defaultDevice.getFileName() != null) {
                        URL[] urls = new URL[]{new File(Config.getConfigPath(), defaultDevice.getFileName()).toURI().toURL()};
                        classLoader = Common.createExtensionsClassLoader(urls);
                    }
                    this.setDevice(DeviceImpl.create(this.emulatorContext, classLoader, defaultDevice.getDescriptorLocation(), defaultDeviceClass));
                    defaultDeviceSelected = true;
                } else {
                    DeviceImpl device = (DeviceImpl)deviceClass.newInstance();
                    device.init(this.emulatorContext);
                    this.setDevice(device);
                }
            }
            catch (InstantiationException ex) {
                Logger.error(ex);
            }
            catch (IllegalAccessException ex) {
                Logger.error(ex);
            }
            catch (IOException ex) {
                Logger.error(ex);
            }
        }
        try {
            launcher = new Launcher(this);
            launcher.setCurrentMIDlet(launcher);
        }
        finally {
            MIDletBridge.setThreadMIDletContext(null);
        }
        if (this.getRecordStoreManager() == null) {
            if (paramRecordStoreManager == null) {
                String className = Config.getRecordStoreManagerClassName();
                if (className != null) {
                    try {
                        Class<?> clazz = Class.forName(className);
                        this.setRecordStoreManager((RecordStoreManager)clazz.newInstance());
                    }
                    catch (ClassNotFoundException ex) {
                        Logger.error(ex);
                    }
                    catch (InstantiationException ex) {
                        Logger.error(ex);
                    }
                    catch (IllegalAccessException ex) {
                        Logger.error(ex);
                    }
                }
                if (this.getRecordStoreManager() == null) {
                    this.setRecordStoreManager(new FileRecordStoreManager());
                }
            } else {
                this.setRecordStoreManager(paramRecordStoreManager);
            }
        }
        return defaultDeviceSelected;
    }

    private static ExtensionsClassLoader getExtensionsClassLoader() {
        if (Common.instance.extensionsClassLoader == null) {
            Common.instance.extensionsClassLoader = new ExtensionsClassLoader(new URL[0], instance.getClass().getClassLoader());
        }
        return Common.instance.extensionsClassLoader;
    }

    private MIDletClassLoader createMIDletClassLoader(boolean forJad) {
        MIDletClassLoader mcl = new MIDletClassLoader(Common.getExtensionsClassLoader());
        if (!Serializable.class.isAssignableFrom(Injected.class)) {
            Logger.error("classpath configuration error, Wrong Injected class detected. microemu-injected module should be after microemu-javase in eclipse");
        }
        if (this.mIDletClassLoaderConfig != null) {
            try {
                mcl.configure(this.mIDletClassLoaderConfig, forJad);
            }
            catch (MalformedURLException e) {
                Message.error("Error", "Unable to find MIDlet classes, " + Message.getCauseMessage(e), e);
            }
        }
        mcl.disableClassPreporcessing(Injected.class);
        mcl.disableClassPreporcessing(MIDletThread.class);
        mcl.disableClassPreporcessing(MIDletTimer.class);
        mcl.disableClassPreporcessing(MIDletTimerTask.class);
        MIDletResourceLoader.classLoader = mcl;
        return mcl;
    }

    public static ClassLoader createExtensionsClassLoader(URL[] urls) {
        return new ExtensionsClassLoader(urls, Common.getExtensionsClassLoader());
    }

    private static JadProperties loadJadProperties(String urlString) throws IOException {
        JadProperties properties = new JadProperties();
        URL url = new URL(urlString);
        if (url.getUserInfo() == null) {
            properties.read(url.openStream());
        } else {
            URLConnection cn = url.openConnection();
            String userInfo = new String(Base64Coder.encode(url.getUserInfo().getBytes("UTF-8")));
            cn.setRequestProperty("Authorization", "Basic " + userInfo);
            properties.read(cn.getInputStream());
        }
        return properties;
    }

    public void initMIDlet(boolean startMidlet) {
        Class<?> midletClass = null;
        if (this.midletClassOrUrl != null && Common.isMIDletUrlExtension(this.midletClassOrUrl)) {
            try {
                File file = new File(this.midletClassOrUrl);
                String url = file.exists() ? IOUtils.getCanonicalFileURL(file) : this.midletClassOrUrl;
                this.openMIDletUrl(url);
            }
            catch (IOException exception) {
                Logger.error("Cannot load " + this.midletClassOrUrl + " URL", exception);
            }
        } else if (this.midletClassOrUrl != null) {
            this.useSystemClassLoader = this.mIDletClassLoaderConfig.isClassLoaderDisabled();
            if (!this.useSystemClassLoader) {
                MIDletClassLoader classLoader = this.createMIDletClassLoader(false);
                try {
                    classLoader.addClassURL(this.midletClassOrUrl);
                    midletClass = classLoader.loadClass(this.midletClassOrUrl);
                }
                catch (MalformedURLException e) {
                    Message.error("Error", "Unable to find MIDlet class, " + Message.getCauseMessage(e), e);
                    return;
                }
                catch (NoClassDefFoundError e) {
                    Message.error("Error", "Unable to find MIDlet class, " + Message.getCauseMessage(e), e);
                    return;
                }
                catch (ClassNotFoundException e) {
                    Message.error("Error", "Unable to find MIDlet class, " + Message.getCauseMessage(e), e);
                    return;
                }
            }
            try {
                midletClass = instance.getClass().getClassLoader().loadClass(this.midletClassOrUrl);
            }
            catch (ClassNotFoundException e) {
                Message.error("Error", "Unable to find MIDlet class, " + Message.getCauseMessage(e), e);
                return;
            }
        }
        if (this.autoTests) {
            if (this.jadURL != null) {
                this.runAutoTests(this.jadURL, true);
            }
        } else {
            if (midletClass != null && this.propertiesJad != null) {
                try {
                    this.jad = Common.loadJadProperties(this.propertiesJad);
                }
                catch (IOException e) {
                    Logger.error("Cannot load " + this.propertiesJad + " URL", e);
                }
            }
            boolean started = false;
            if (midletClass == null) {
                MIDletEntry entry = launcher.getSelectedMidletEntry();
                if (startMidlet && entry != null) {
                    started = null != this.startMidlet(entry.getMIDletClass(), MIDletBridge.getMIDletAccess());
                }
            } else {
                boolean bl = started = null != this.startMidlet(midletClass, MIDletBridge.getMIDletAccess());
            }
            if (!started) {
                this.startLauncher(MIDletBridge.getMIDletContext());
            }
        }
    }

    public static String usage() {
        return "[(-d | --device) ({device descriptor} | {device class name}) ] \n[--rms (file | memory)] \n[--id EmulatorID ] \n[--impl {JSR implementation class name}]\n[(--classpath|-cp) <JSR CLASSPATH>]\n[(--appclasspath|--appcp) <MIDlet CLASSPATH>]\n[--appclass <library class name>]\n[--appclassloader strict|relaxed|delegating|system] \n[-Xautotest:<JAD file url>\n[--quit]\n[--logCallLocation true|false]\n[--traceClassLoading\n[--traceSystemClassLoading]\n[--enhanceCatchBlock]\n][--resizableDevice {width} {height}]\n(({MIDlet class name} [--propertiesjad {jad file location}]) | {jad file location} | {jar file location})";
    }

    static /* synthetic */ JadProperties access$000(String x0) throws IOException {
        return Common.loadJadProperties(x0);
    }

    static /* synthetic */ MIDletClassLoader access$100(Common x0, boolean x1) {
        return x0.createMIDletClassLoader(x1);
    }

    static /* synthetic */ MIDletContext access$200(Common x0, Class x1, MIDletAccess x2) {
        return x0.startMidlet(x1, x2);
    }

    static /* synthetic */ Object access$300(Common x0) {
        return x0.destroyNotify;
    }

    static {
        statusBarListener = null;
    }
}

