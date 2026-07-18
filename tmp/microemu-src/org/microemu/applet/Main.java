/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.applet;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Locale;
import java.util.Vector;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.swing.Timer;
import org.microemu.DisplayComponent;
import org.microemu.EmulatorContext;
import org.microemu.MIDletBridge;
import org.microemu.MIDletContext;
import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.app.launcher.Launcher;
import org.microemu.app.ui.swing.SwingDeviceComponent;
import org.microemu.app.util.MIDletResourceLoader;
import org.microemu.app.util.MIDletSystemProperties;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.DeviceFactory;
import org.microemu.device.FontManager;
import org.microemu.device.InputMethod;
import org.microemu.device.impl.DeviceDisplayImpl;
import org.microemu.device.impl.DeviceImpl;
import org.microemu.device.j2se.J2SEDevice;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEFontManager;
import org.microemu.device.j2se.J2SEInputMethod;
import org.microemu.device.ui.EventDispatcher;
import org.microemu.log.Logger;
import org.microemu.util.JadMidletEntry;
import org.microemu.util.JadProperties;
import org.microemu.util.MemoryRecordStoreManager;

public class Main
extends Applet
implements MicroEmulator {
    private static final long serialVersionUID = 1L;
    private MIDlet midlet = null;
    private RecordStoreManager recordStoreManager;
    private JadProperties manifest = new JadProperties();
    private SwingDeviceComponent devicePanel;
    private String accessibleHost;
    private EmulatorContext emulatorContext = new EmulatorContext(){
        private InputMethod inputMethod = new J2SEInputMethod();
        private DeviceDisplay deviceDisplay = new J2SEDeviceDisplay(this);
        private FontManager fontManager = new J2SEFontManager();

        public DisplayComponent getDisplayComponent() {
            return Main.this.devicePanel.getDisplayComponent();
        }

        public InputMethod getDeviceInputMethod() {
            return this.inputMethod;
        }

        public DeviceDisplay getDeviceDisplay() {
            return this.deviceDisplay;
        }

        public FontManager getDeviceFontManager() {
            return this.fontManager;
        }

        public InputStream getResourceAsStream(String name) {
            return this.getClass().getResourceAsStream(name);
        }

        public boolean platformRequest(String url) {
            try {
                Main.this.getAppletContext().showDocument(new URL(url), "mini");
            }
            catch (Exception exception) {
                // empty catch block
            }
            return false;
        }
    };

    public Main() {
        this.devicePanel = new SwingDeviceComponent();
        this.devicePanel.addKeyListener(this.devicePanel);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void init() {
        Class<?> midletClass;
        DeviceImpl device;
        if (this.midlet != null) {
            return;
        }
        MIDletSystemProperties.applyToJavaSystemProperties = false;
        MIDletBridge.setMicroEmulator(this);
        URL baseURL = this.getCodeBase();
        if (baseURL != null) {
            this.accessibleHost = baseURL.getHost();
        }
        this.recordStoreManager = new MemoryRecordStoreManager();
        this.setLayout(new BorderLayout());
        this.add((Component)this.devicePanel, "Center");
        String deviceParameter = this.getParameter("device");
        if (deviceParameter == null) {
            device = new J2SEDevice();
            DeviceFactory.setDevice(device);
            device.init(this.emulatorContext);
        } else {
            try {
                Class<?> cl = Class.forName(deviceParameter);
                device = (DeviceImpl)cl.newInstance();
                DeviceFactory.setDevice(device);
                device.init(this.emulatorContext);
            }
            catch (ClassNotFoundException ex) {
                try {
                    device = DeviceImpl.create(this.emulatorContext, Main.class.getClassLoader(), deviceParameter, J2SEDevice.class);
                    DeviceFactory.setDevice(device);
                }
                catch (IOException ex1) {
                    Logger.error(ex);
                    return;
                }
            }
            catch (IllegalAccessException ex) {
                Logger.error(ex);
                return;
            }
            catch (InstantiationException ex) {
                Logger.error(ex);
                return;
            }
        }
        this.devicePanel.init();
        this.manifest.clear();
        try {
            URL url = this.getClass().getClassLoader().getResource("META-INF/MANIFEST.MF");
            if (url != null) {
                this.manifest.read(url.openStream());
                if (this.manifest.getProperty("MIDlet-Name") == null) {
                    this.manifest.clear();
                }
            }
        }
        catch (IOException e) {
            Logger.error(e);
        }
        String midletClassName = null;
        String jadFile = this.getParameter("jad");
        if (jadFile != null) {
            InputStream jadInputStream = null;
            try {
                URL jad = new URL(this.getCodeBase(), jadFile);
                jadInputStream = jad.openStream();
                this.manifest.read(jadInputStream);
                Vector entries = this.manifest.getMidletEntries();
                if (entries.size() > 0) {
                    JadMidletEntry entry = (JadMidletEntry)entries.elementAt(0);
                    midletClassName = entry.getClassName();
                }
            }
            catch (IOException e) {
            }
            finally {
                if (jadInputStream != null) {
                    try {
                        jadInputStream.close();
                    }
                    catch (IOException e1) {}
                }
            }
        }
        if (midletClassName == null && (midletClassName = this.getParameter("midlet")) == null) {
            Logger.debug("There is no midlet parameter");
            return;
        }
        String maxFps = this.getParameter("maxfps");
        if (maxFps != null) {
            try {
                EventDispatcher.maxFps = Integer.parseInt(maxFps);
            }
            catch (NumberFormatException ex) {
                // empty catch block
            }
        }
        MIDletResourceLoader.classLoader = this.getClass().getClassLoader();
        try {
            midletClass = Class.forName(midletClassName);
        }
        catch (ClassNotFoundException ex) {
            Logger.error("Cannot find " + midletClassName + " MIDlet class");
            return;
        }
        try {
            this.midlet = (MIDlet)midletClass.newInstance();
        }
        catch (Exception ex) {
            Logger.error("Cannot initialize " + midletClass + " MIDlet class", ex);
            return;
        }
        if (((DeviceDisplayImpl)device.getDeviceDisplay()).isResizable()) {
            this.resize(device.getDeviceDisplay().getFullWidth(), device.getDeviceDisplay().getFullHeight());
        } else {
            this.resize(device.getNormalImage().getWidth(), device.getNormalImage().getHeight());
        }
    }

    public void start() {
        this.devicePanel.requestFocus();
        new Thread("midlet_starter"){

            public void run() {
                try {
                    MIDletBridge.getMIDletAccess(Main.this.midlet).startApp();
                }
                catch (MIDletStateChangeException ex) {
                    System.err.println(ex);
                }
            }
        }.start();
        Timer timer = new Timer(1000, new ActionListener(){

            public void actionPerformed(ActionEvent e) {
                Main.this.devicePanel.requestFocus();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    public void stop() {
        MIDletBridge.getMIDletAccess(this.midlet).pauseApp();
    }

    public void destroy() {
        try {
            MIDletBridge.getMIDletAccess(this.midlet).destroyApp(true);
        }
        catch (MIDletStateChangeException ex) {
            System.err.println(ex);
        }
    }

    public RecordStoreManager getRecordStoreManager() {
        return this.recordStoreManager;
    }

    public String getAppProperty(String key) {
        if (key.equals("applet")) {
            return "yes";
        }
        String value = null;
        value = key.equals("microedition.platform") ? "MicroEmulator" : (key.equals("microedition.profiles") ? "MIDP-2.0" : (key.equals("microedition.configuration") ? "CLDC-1.0" : (key.equals("microedition.locale") ? Locale.getDefault().getLanguage() : (key.equals("microedition.encoding") ? System.getProperty("file.encoding") : (key.equals("microemu.applet") ? "true" : (key.equals("microemu.accessible.host") ? this.accessibleHost : (this.getParameter(key) != null ? this.getParameter(key) : this.manifest.getProperty(key))))))));
        return value;
    }

    public InputStream getResourceAsStream(String name) {
        return this.emulatorContext.getResourceAsStream(name);
    }

    public int checkPermission(String permission) {
        return 0;
    }

    public boolean platformRequest(String url) {
        return this.emulatorContext.platformRequest(url);
    }

    public void notifyDestroyed(MIDletContext midletContext) {
    }

    public void destroyMIDletContext(MIDletContext midletContext) {
    }

    public Launcher getLauncher() {
        return null;
    }

    public String getAppletInfo() {
        return "Title: MicroEmulator \nAuthor: Bartek Teodorczyk, 2001";
    }

    public String[][] getParameterInfo() {
        String[][] info = new String[][]{{"midlet", "MIDlet class name", "The MIDlet class name. This field is mandatory."}};
        return info;
    }
}

