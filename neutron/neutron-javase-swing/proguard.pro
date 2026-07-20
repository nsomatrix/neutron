# ProGuard configuration for Neutron JVM Emulator
-optimizationpasses 5
-overloadaggressively
-repackageclasses 'a'
-allowaccessmodification
-dontwarn
-dontnote
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# Keep all microedition classes and members intact so MIDlets can find and call them
-keep class javax.microedition.** {
    *;
}

# Keep all org.neutron classes and members intact for compatibility with external extensions/JSRs
-keep class org.neutron.** {
    *;
}



# Keep main entry points
-keep public class org.neutron.app.Main {
    public static void main(java.lang.String[]);
}
-keep public class org.neutron.app.Headless {
    public static void main(java.lang.String[]);
}

# Keep ButtonName class completely intact since its fields are looked up reflectively by name
-keep class org.neutron.device.impl.ButtonName {
    *;
}

# Keep any public static instance() methods in emulator packages
-keepclassmembers class org.neutron.** {
    public static *** instance();
}

# Keep all public and public static fields in emulator packages for reflective lookups (e.g. LEFT/RIGHT alignments)
-keepclassmembers class org.neutron.** {
    public static <fields>;
    public <fields>;
}

# Keep device classes and managers loaded by reflection from config XML files
-keep class org.neutron.device.j2se.J2SEDevice {
    <init>();
}
-keep class org.neutron.app.util.FileRecordStoreManager {
    <init>();
}

# Keep connection classes because they are loaded dynamically by protocol name
-keep class org.neutron.cldc.**.Connection {
    *;
}

# Keep all classes ending in Impl because they are loaded dynamically by ImplFactory
-keep class *Impl {
    <init>();
}

# Keep the classes dynamically injected/referenced in preverified MIDlet bytecode completely intact
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

# Keep serializable members
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep all com.formdev.flatlaf classes and members intact for theme reflection / UI delegate mapping
-keep class com.formdev.flatlaf.** {
    *;
}

