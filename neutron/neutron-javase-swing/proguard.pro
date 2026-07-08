# ProGuard configuration for Neutron JVM Emulator
-optimizationpasses 5
-overloadaggressively
-repackageclasses 'a'
-allowaccessmodification
-dontwarn
-dontnote

# Keep all microedition APIs completely intact so MIDlets can find and call them
-keep class javax.microedition.** {
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
-keep class org.neutron.applet.CookieRecordStoreManager {
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

# Keep serializable members
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
