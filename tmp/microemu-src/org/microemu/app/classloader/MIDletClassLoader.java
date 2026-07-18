/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.classloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;
import org.microemu.app.classloader.ClassPreprocessor;
import org.microemu.app.classloader.InstrumentationConfig;
import org.microemu.app.classloader.MIDletClassLoaderConfig;
import org.microemu.app.util.IOUtils;
import org.microemu.log.Logger;

public class MIDletClassLoader
extends URLClassLoader {
    public static boolean instrumentMIDletClasses = true;
    public static boolean traceClassLoading = false;
    public static boolean traceSystemClassLoading = false;
    public static boolean enhanceCatchBlock = false;
    public static final boolean debug = false;
    private boolean delegatingToParent = false;
    private boolean findPathInParent = false;
    private InstrumentationConfig config;
    private Set noPreporcessingNames = new HashSet();
    private AccessControlContext acc = AccessController.getContext();

    public MIDletClassLoader(ClassLoader parent) {
        super(new URL[0], parent);
        this.config = new InstrumentationConfig();
        this.config.setEnhanceCatchBlock(enhanceCatchBlock);
        this.config.setEnhanceThreadCreation(true);
    }

    public void configure(MIDletClassLoaderConfig clConfig, boolean forJad) throws MalformedURLException {
        Iterator iter = clConfig.appclasspath.iterator();
        while (iter.hasNext()) {
            String path = (String)iter.next();
            StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
            while (st.hasMoreTokens()) {
                this.addURL(new URL(IOUtils.getCanonicalFileClassLoaderURL(new File(st.nextToken()))));
            }
        }
        iter = clConfig.appclasses.iterator();
        while (iter.hasNext()) {
            this.addClassURL((String)iter.next());
        }
        int delegationType = clConfig.getDelegationType(forJad);
        this.delegatingToParent = delegationType == 2;
        this.findPathInParent = delegationType == 1;
    }

    public void addClassURL(String className) throws MalformedURLException {
        String resource = MIDletClassLoader.getClassResourceName(className);
        URL url = this.getParent().getResource(resource);
        if (url == null) {
            url = this.getResource(resource);
        }
        if (url == null) {
            throw new MalformedURLException("Unable to find class " + className + " URL");
        }
        String path = url.toExternalForm();
        this.addURL(new URL(path.substring(0, path.length() - resource.length())));
    }

    static URL getClassURL(ClassLoader parent, String className) throws MalformedURLException {
        String resource = MIDletClassLoader.getClassResourceName(className);
        URL url = parent.getResource(resource);
        if (url == null) {
            throw new MalformedURLException("Unable to find class " + className + " URL");
        }
        String path = url.toExternalForm();
        return new URL(path.substring(0, path.length() - resource.length()));
    }

    public void addURL(URL url) {
        super.addURL(url);
    }

    protected synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> result;
        block5: {
            result = this.findLoadedClass(name);
            if (result == null) {
                try {
                    result = this.findClass(name);
                }
                catch (ClassNotFoundException e) {
                    if (!(e instanceof LoadClassByParentException) && !this.delegatingToParent) break block5;
                    if (traceSystemClassLoading) {
                        Logger.info("Load system class", name);
                    }
                    if ((result = super.loadClass(name, false)) != null) break block5;
                    throw new ClassNotFoundException(name);
                }
            }
        }
        if (resolve) {
            this.resolveClass(result);
        }
        return result;
    }

    public URL getResource(final String name) {
        try {
            return (URL)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() {
                    URL url = MIDletClassLoader.this.findResource(name);
                    if (url == null && MIDletClassLoader.this.delegatingToParent && MIDletClassLoader.this.getParent() != null) {
                        url = MIDletClassLoader.this.getParent().getResource(name);
                    }
                    return url;
                }
            }, this.acc);
        }
        catch (PrivilegedActionException e) {
            return null;
        }
    }

    public InputStream getResourceAsStream(String name) {
        final URL url = this.getResource(name);
        if (url == null) {
            return null;
        }
        try {
            return (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws IOException {
                    return url.openStream();
                }
            }, this.acc);
        }
        catch (PrivilegedActionException e) {
            return null;
        }
    }

    public boolean classLoadByParent(String className) {
        if (className.startsWith("java.")) {
            return true;
        }
        if (className.startsWith("sun.reflect.")) {
            return true;
        }
        if (className.startsWith("javax.microedition.")) {
            return true;
        }
        if (className.startsWith("javax.")) {
            return true;
        }
        return this.noPreporcessingNames.contains(className);
    }

    public void disableClassPreporcessing(Class klass) {
        this.disableClassPreporcessing(klass.getName());
    }

    public void disableClassPreporcessing(String className) {
        this.noPreporcessingNames.add(className);
    }

    public static String getClassResourceName(String className) {
        return className.replace('.', '/').concat(".class");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Class findClass(final String name) throws ClassNotFoundException {
        int byteCodeLength;
        byte[] byteCode;
        block23: {
            InputStream is;
            block22: {
                if (this.classLoadByParent(name)) {
                    throw new LoadClassByParentException(name);
                }
                try {
                    boolean classFound;
                    is = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                        public Object run() throws ClassNotFoundException {
                            return MIDletClassLoader.this.getResourceAsStream(MIDletClassLoader.getClassResourceName(name));
                        }
                    }, this.acc);
                    if (is != null || !this.findPathInParent) break block22;
                    try {
                        this.addClassURL(name);
                        classFound = true;
                    }
                    catch (MalformedURLException e) {
                        classFound = false;
                    }
                    if (classFound) {
                        is = (InputStream)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                            public Object run() throws ClassNotFoundException {
                                return MIDletClassLoader.this.getResourceAsStream(MIDletClassLoader.getClassResourceName(name));
                            }
                        }, this.acc);
                    }
                }
                catch (PrivilegedActionException e) {
                    throw new ClassNotFoundException(name, e.getCause());
                }
            }
            if (is == null) {
                throw new ClassNotFoundException(name);
            }
            try {
                if (traceClassLoading) {
                    Logger.info("Load MIDlet class", name);
                }
                if (instrumentMIDletClasses) {
                    byteCode = ClassPreprocessor.instrument(is, this.config);
                    byteCodeLength = byteCode.length;
                    break block23;
                }
                int chunkSize = 2048;
                int maxClassSizeSize = 16384;
                byteCode = new byte[2048];
                byteCodeLength = 0;
                while (true) {
                    int retrived;
                    try {
                        retrived = is.read(byteCode, byteCodeLength, byteCode.length - byteCodeLength);
                    }
                    catch (IOException e) {
                        throw new ClassNotFoundException(name, e);
                    }
                    if (retrived == -1) {
                        break block23;
                    }
                    if (byteCode.length + 2048 > 16384) {
                        throw new ClassNotFoundException(name, new ClassFormatError("Class object is bigger than 16 Kilobyte"));
                    }
                    if (byteCode.length == (byteCodeLength += retrived)) {
                        byte[] newData = new byte[byteCode.length + 2048];
                        System.arraycopy(byteCode, 0, newData, 0, byteCode.length);
                        byteCode = newData;
                        continue;
                    }
                    if (byteCode.length < byteCodeLength) break;
                }
                throw new ClassNotFoundException(name, new ClassFormatError("Internal read error"));
            }
            finally {
                try {
                    is.close();
                }
                catch (IOException ignore) {}
            }
        }
        return this.defineClass(name, byteCode, 0, byteCodeLength);
    }

    private static class LoadClassByParentException
    extends ClassNotFoundException {
        private static final long serialVersionUID = 1L;

        public LoadClassByParentException(String name) {
            super(name);
        }
    }
}

