# ProGuard configuration for Neutron JVM Emulator
-optimizationpasses 3
-overloadaggressively
-repackageclasses 'org.neutron.internal'
-allowaccessmodification
-dontwarn
-dontnote

# Keep all public/protected microedition classes and members intact so MIDlets can find and call them
-keep public class javax.microedition.** {
    public protected *;
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

# Keep serializable members
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
