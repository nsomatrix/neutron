package org.neutron.build.verify;

import javax.swing.UIManager;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Executes inside a clean forked JVM with the unpacked obfuscated release payload JAR
 * on the classpath to verify all ProGuard-sensitive entry points, reflection targets,
 * dynamic implementations, and FlatLaf theme initialization.
 */
public final class ReleaseVerifier {

    private ReleaseVerifier() {
    }

    public static void main(String[] args) throws Exception {
        boolean verifyFlatLaf = args.length > 0 && Boolean.parseBoolean(args[0]);

        verifyCoreRuntimeClasses();
        verifyPublicJ2meApis();
        verifyDynamicReflectiveSubsystems();
        verifyDeviceAndAppResources();

        if (verifyFlatLaf) {
            verifyAllFlatLafThemes();
        }

        System.out.println("ReleaseVerifier: all release runtime checks passed (flatlaf=" + verifyFlatLaf + ").");
    }

    private static void verifyCoreRuntimeClasses() throws Exception {
        Class<?> injected = Class.forName("org.neutron.Injected");
        if (!java.io.Serializable.class.isAssignableFrom(injected)) {
            throw new AssertionError("org.neutron.Injected must implement java.io.Serializable");
        }
        injected.getMethod("getProperty", String.class);
        injected.getMethod("getResourceAsStream", Class.class, String.class);
        injected.getMethod("handleCatchThrowable", Throwable.class);
        injected.getMethod("printStackTrace", Throwable.class);

        Class.forName("org.neutron.app.Main").getMethod("main", String[].class);
        Class.forName("org.neutron.app.Headless").getMethod("main", String[].class);
        Class.forName("org.neutron.app.util.MIDletThread");
        Class.forName("org.neutron.app.util.MIDletTimer");
        Class.forName("org.neutron.app.util.MIDletTimerTask");
    }

    private static void verifyPublicJ2meApis() throws Exception {
        Class.forName("javax.microedition.lcdui.Display");
        Class.forName("javax.microedition.lcdui.Canvas");
        Class.forName("javax.microedition.lcdui.game.GameCanvas").getDeclaredMethod("getGraphics");
        Class.forName("javax.microedition.midlet.MIDlet");
        Class.forName("javax.microedition.io.Connector");
        Class.forName("javax.microedition.io.PushRegistry");
        Class.forName("javax.microedition.media.Manager").getMethod("cleanupMedia");
        Class.forName("javax.microedition.m3g.Graphics3D");
        Class.forName("com.mascotcapsule.micro3d.v3.Graphics3D");
        Class.forName("com.nokia.mid.ui.DirectUtils");
        Class.forName("javax.bluetooth.LocalDevice");
        Class.forName("javax.wireless.messaging.MessageConnection");
        Class.forName("javax.xml.parsers.SAXParserFactory");
    }

    private static void verifyDynamicReflectiveSubsystems() throws Exception {
        // PushRegistry -> ImplFactory (*Delegate -> *Impl)
        Class<?> pushRegistry = Class.forName("javax.microedition.io.PushRegistry");
        Method listConnections = pushRegistry.getMethod("listConnections", boolean.class);
        listConnections.invoke(null, Boolean.FALSE);

        // JSR-75 FileSystem ImplementationInitialization + FileSystemRegistry (*Delegate -> *Impl)
        Class<?> fileSystemClass = Class.forName("org.neutron.cldc.file.FileSystem");
        Object fileSystem = fileSystemClass.getConstructor().newInstance();
        fileSystemClass.getMethod("registerImplementation", Map.class).invoke(fileSystem, Collections.emptyMap());
        Class<?> fileSystemRegistry = Class.forName("javax.microedition.io.file.FileSystemRegistry");
        Enumeration<?> roots = (Enumeration<?>) fileSystemRegistry.getMethod("listRoots").invoke(null);
        if (roots == null) {
            throw new AssertionError("FileSystemRegistry.listRoots() returned null");
        }

        // ConnectorImpl reflective protocols
        String[] protocols = {"http", "https", "socket", "ssl", "datagram", "file", "sms"};
        for (String protocol : protocols) {
            Class.forName("org.neutron.cldc." + protocol + ".Connection").getConstructor().newInstance();
        }

        // Device, Graphics, ButtonName, SoftButton, RecordStore, MRU XMLItem
        Class.forName("org.neutron.device.j2se.J2SEDevice").getConstructor().newInstance();
        Class<?> surfaceClass = Class.forName("org.neutron.device.j2se.J2SEGraphicsSurface");
        Class.forName("org.neutron.device.j2se.J2SEDisplayGraphics").getConstructor(surfaceClass);
        Class.forName("org.neutron.device.j2se.J2SEDeviceDisplay");

        if (Class.forName("org.neutron.device.impl.ButtonName").getFields().length == 0) {
            throw new AssertionError("ButtonName fields were stripped by ProGuard");
        }
        Class.forName("org.neutron.device.j2se.J2SESoftButton").getField("LEFT");
        Class.forName("org.neutron.app.util.FileRecordStoreManager").getConstructor().newInstance();
        Class.forName("org.neutron.app.util.MidletURLReference").getConstructor().newInstance();
    }

    private static void verifyDeviceAndAppResources() {
        String[] requiredResources = {
            "/META-INF/neutron-build.version",
            "/org/neutron/icon.png",
            "/org/neutron/app/launcher/logo_48.png",
            "/org/neutron/device/resizable/device.xml",
            "/org/neutron/device/large/device.xml",
            "/org/neutron/device/minimum/device.xml"
        };
        for (String resource : requiredResources) {
            try (InputStream is = ReleaseVerifier.class.getResourceAsStream(resource)) {
                if (is == null) {
                    throw new AssertionError("Missing required resource in release payload: " + resource);
                }
            } catch (AssertionError e) {
                throw e;
            } catch (Exception e) {
                throw new AssertionError("Failed reading resource: " + resource, e);
            }
        }
    }

    private static void verifyAllFlatLafThemes() throws Exception {
        final CopyOnWriteArrayList<String> severeErrors = new CopyOnWriteArrayList<>();
        Logger flatLafLogger = Logger.getLogger("com.formdev.flatlaf");
        Handler captureHandler = new Handler() {
            @Override
            public void publish(LogRecord record) {
                if (record != null && record.getLevel().intValue() >= Level.SEVERE.intValue()) {
                    severeErrors.add(record.getMessage() + (record.getThrown() != null ? " -> " + record.getThrown() : ""));
                }
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() {
            }
        };
        flatLafLogger.addHandler(captureHandler);

        try {
            String[] lafClasses = {
                "com.formdev.flatlaf.FlatDarkLaf",
                "com.formdev.flatlaf.FlatLightLaf",
                "com.formdev.flatlaf.FlatIntelliJLaf",
                "com.formdev.flatlaf.FlatDarculaLaf",
                "com.formdev.flatlaf.themes.FlatMacDarkLaf",
                "com.formdev.flatlaf.themes.FlatMacLightLaf"
            };
            for (String lafName : lafClasses) {
                Class<?> lafClass = Class.forName(lafName);
                Object setupResult = lafClass.getMethod("setup").invoke(null);
                if (!Boolean.TRUE.equals(setupResult)) {
                    throw new AssertionError("FlatLaf setup() returned false for " + lafName);
                }
                if (UIManager.getLookAndFeel() == null) {
                    throw new AssertionError("UIManager LookAndFeel was null after " + lafName + ".setup()");
                }
            }

            Class<?> flatLaf = Class.forName("com.formdev.flatlaf.FlatLaf");
            flatLaf.getMethod("isLafDark").invoke(null);
            flatLaf.getMethod("updateUI").invoke(null);
        } finally {
            flatLafLogger.removeHandler(captureHandler);
        }

        if (!severeErrors.isEmpty()) {
            throw new AssertionError("FlatLaf logged SEVERE parse/init errors in release JAR:\n - "
                + String.join("\n - ", severeErrors));
        }
    }
}

