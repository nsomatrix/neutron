/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.microemu.device.Device;
import org.microemu.log.Logger;

public class MIDletSystemProperties {
    public static boolean applyToJavaSystemProperties = true;
    private static final Map props = new HashMap();
    private static final Map permissions = new HashMap();
    private static Map systemPropertiesPreserve;
    private static List systemPropertiesDevice;
    private static boolean wanrOnce;
    private static boolean initialized;
    private static AccessControlContext acc;

    private static void initOnce() {
        if (initialized) {
            return;
        }
        initialized = true;
        MIDletSystemProperties.setProperty("microedition.platform", "MicroEmulator");
        MIDletSystemProperties.setProperty("microedition.encoding", MIDletSystemProperties.getSystemProperty("file.encoding"));
    }

    public static void initContext() {
        acc = AccessController.getContext();
    }

    public static String getProperty(String key) {
        MIDletSystemProperties.initOnce();
        if (props.containsKey(key)) {
            return (String)props.get(key);
        }
        String v = MIDletSystemProperties.getDynamicProperty(key);
        if (v != null) {
            return v;
        }
        try {
            return MIDletSystemProperties.getSystemProperty(key);
        }
        catch (SecurityException e) {
            return null;
        }
    }

    public static String getSystemProperty(String key) {
        try {
            if (acc != null) {
                return MIDletSystemProperties.getSystemPropertySecure(key);
            }
            return System.getProperty(key);
        }
        catch (SecurityException e) {
            return null;
        }
    }

    private static String getSystemPropertySecure(final String key) {
        try {
            return (String)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() {
                    return System.getProperty(key);
                }
            }, acc);
        }
        catch (Throwable e) {
            return null;
        }
    }

    private static String getDynamicProperty(String key) {
        if (key.equals("microedition.locale")) {
            return Locale.getDefault().getLanguage();
        }
        return null;
    }

    public static Set getPropertiesSet() {
        MIDletSystemProperties.initOnce();
        return props.entrySet();
    }

    public static String setProperty(String key, String value) {
        block5: {
            MIDletSystemProperties.initOnce();
            if (applyToJavaSystemProperties) {
                try {
                    if (value == null) {
                        System.getProperties().remove(key);
                    } else {
                        System.setProperty(key, value);
                    }
                }
                catch (SecurityException e) {
                    if (!wanrOnce) break block5;
                    wanrOnce = false;
                    Logger.error("Cannot update Java System.Properties", e);
                    Logger.debug("Continue ME2 operations with no updates to system Properties");
                }
            }
        }
        return props.put(key, value);
    }

    public static String clearProperty(String key) {
        block3: {
            if (applyToJavaSystemProperties) {
                try {
                    System.getProperties().remove(key);
                }
                catch (SecurityException e) {
                    if (!wanrOnce) break block3;
                    wanrOnce = false;
                    Logger.error("Cannot update Java System.Properties", e);
                }
            }
        }
        return (String)props.remove(key);
    }

    public static void setProperties(Map properties) {
        MIDletSystemProperties.initOnce();
        Iterator i = properties.entrySet().iterator();
        while (i.hasNext()) {
            Map.Entry e = i.next();
            MIDletSystemProperties.setProperty((String)e.getKey(), (String)e.getValue());
        }
    }

    public static int getPermission(String permission) {
        Integer value = (Integer)permissions.get(permission);
        if (value == null) {
            return -1;
        }
        return value;
    }

    public static void setPermission(String permission, int value) {
        permissions.put(permission, new Integer(value));
    }

    public static void setDevice(Device newDevice) {
        Map.Entry e;
        Iterator i;
        MIDletSystemProperties.initOnce();
        if (systemPropertiesDevice != null) {
            Iterator iter = systemPropertiesDevice.iterator();
            while (iter.hasNext()) {
                MIDletSystemProperties.clearProperty((String)iter.next());
            }
        }
        if (systemPropertiesPreserve != null) {
            i = systemPropertiesPreserve.entrySet().iterator();
            while (i.hasNext()) {
                e = i.next();
                MIDletSystemProperties.setProperty((String)e.getKey(), (String)e.getValue());
            }
        }
        systemPropertiesDevice = new Vector();
        systemPropertiesPreserve = new HashMap();
        i = newDevice.getSystemProperties().entrySet().iterator();
        while (i.hasNext()) {
            e = i.next();
            String key = (String)e.getKey();
            if (props.containsKey(key)) {
                systemPropertiesPreserve.put(key, props.get(key));
            } else {
                systemPropertiesDevice.add(key);
            }
            MIDletSystemProperties.setProperty(key, (String)e.getValue());
        }
    }

    static {
        wanrOnce = true;
        initialized = false;
    }
}

