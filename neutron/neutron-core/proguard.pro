# Aggressive ProGuard configuration for Neutron JVM Emulator (Core)
-optimizationpasses 7
-overloadaggressively
-repackageclasses ''
-allowaccessmodification
-dontwarn
-dontnote

# Keep main entry points
-keep public class org.neutron.app.Main {
    public static void main(java.lang.String[]);
}
-keep public class org.neutron.app.Headless {
    public static void main(java.lang.String[]);
}

# Keep all MIDlet-facing J2ME & vendor extension APIs intact
-keep class javax.microedition.** {
    public protected *;
}
-keep class javax.bluetooth.** {
    public protected *;
}
-keep class javax.obex.** {
    public protected *;
}
-keep class javax.wireless.messaging.** {
    public protected *;
}
-keep class javax.xml.** {
    public protected *;
}
-keep class com.mascotcapsule.micro3d.** {
    public protected *;
}
-keep class com.motorola.** {
    public protected *;
}
-keep class com.nokia.mid.** {
    public protected *;
}
-keep class com.samsung.** {
    public protected *;
}
-keep class com.siemens.mp.** {
    public protected *;
}
-keep class com.sonyericsson.** {
    public protected *;
}
-keep class com.sun.cdc.** {
    public protected *;
}

# Keep public Neutron bridge/API interfaces & classes used by MIDlets/extensions
-keep class org.neutron.MIDlet* {
    *;
}
-keep class org.neutron.Neutron {
    *;
}
-keep class org.neutron.DisplayAccess {
    *;
}
-keep class org.neutron.DisplayComponent {
    *;
}
-keep class org.neutron.CustomItemAccess {
    *;
}
-keep class org.neutron.GameCanvasKeyAccess {
    *;
}
-keep class org.neutron.RecordStoreManager {
    *;
}

# Keep classes dynamically injected/referenced in preverified MIDlet bytecode
-keep class org.neutron.Injected {
    *;
}
-keep class org.neutron.app.util.MIDletThread {
    *;
}
-keep class org.neutron.app.util.MIDletTimer {
    *;
}
-keep class org.neutron.app.util.MIDletTimerTask {
    *;
}

# Keep reflective lookups in Neutron
-keep class org.neutron.device.impl.ButtonName {
    <fields>;
}
-keep class org.neutron.device.j2se.J2SESoftButton {
    public static int *;
}
-keep class org.neutron.device.j2se.J2SEDevice {
    <init>();
}
-keep class org.neutron.device.j2se.J2SEDisplayGraphics {
    public <init>(org.neutron.device.j2se.J2SEGraphicsSurface);
}
-keep class org.neutron.device.j2se.J2SEGraphicsSurface {
    *;
}
-keep class org.neutron.device.j2se.J2SEDeviceDisplay {
    public *** getScreenSurface();
}
-keep class org.neutron.app.util.FileRecordStoreManager {
    <init>();
}
-keep class org.neutron.cldc.**.Connection {
    *;
}
-keep interface org.neutron.microedition.ImplementationInitialization {
    *;
}
-keep class * implements org.neutron.microedition.ImplementationInitialization {
    <init>();
    public void registerImplementation(java.util.Map);
    public void notifyMIDletStart();
    public void notifyMIDletDestroyed();
}
-keep class * implements org.neutron.device.Device {
    <init>();
}
-keep class * implements org.neutron.RecordStoreManager {
    <init>();
}
-keep class org.neutron.**.*Impl {
    <init>();
}
-keep interface org.neutron.**.*Delegate {
    *;
}
-keep class * implements org.neutron.app.util.XMLItem {
    <init>();
}
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}
-keepclassmembers class org.neutron.** {
    public static *** instance();
}
-keepclassmembers class javax.microedition.media.Manager {
    public static void cleanupMedia();
}

# Strip out debug log statements to shrink bytecode size
-assumenosideeffects class org.neutron.log.Logger {
    public static void debug(java.lang.String);
    public static void debug(java.lang.String, java.lang.Throwable);
    public static void debug(java.lang.Throwable);
    public static void debug(java.lang.String, java.lang.String);
    public static void debug(java.lang.String, java.lang.Object);
    public static void debug(java.lang.String, java.lang.String, java.lang.String);
    public static void debug(java.lang.String, long);
    public static void debug0x(java.lang.String, long);
    public static void debug(java.lang.String, long, long);
    public static void debug(java.lang.String, boolean);
    public static void debugClassLoader(java.lang.String, java.lang.Object);
    public static boolean isDebugEnabled();
}

# Keep enum values()/valueOf() for Enum.valueOf() and reflection
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Keep serializable members
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

