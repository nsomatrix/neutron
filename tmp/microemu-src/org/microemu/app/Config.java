/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import nanoxml.XMLElement;
import nanoxml.XMLParseException;
import org.microemu.EmulatorContext;
import org.microemu.app.util.DeviceEntry;
import org.microemu.app.util.IOUtils;
import org.microemu.app.util.MIDletSystemProperties;
import org.microemu.app.util.MRUList;
import org.microemu.app.util.MidletURLReference;
import org.microemu.device.impl.Rectangle;
import org.microemu.log.Logger;

public class Config {
    private static File meHome;
    private static String emulatorID;
    private static XMLElement configXml;
    private static DeviceEntry defaultDevice;
    private static DeviceEntry resizableDevice;
    private static EmulatorContext emulatorContext;
    private static MRUList urlsMRU;

    private static File initMEHomePath() {
        try {
            File meHome = new File(System.getProperty("user.home") + "/.microemulator/");
            if (emulatorID != null) {
                return new File(meHome, emulatorID);
            }
            return meHome;
        }
        catch (SecurityException e) {
            Logger.error("Cannot access user.home", e);
            return null;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void loadConfig(DeviceEntry defaultDevice, EmulatorContext emulatorContext) {
        Config.defaultDevice = defaultDevice;
        Config.emulatorContext = emulatorContext;
        File configFile = new File(Config.getConfigPath(), "config2.xml");
        try {
            if (configFile.exists()) {
                Config.loadConfigFile("config2.xml");
            } else {
                configFile = new File(Config.getConfigPath(), "config.xml");
                if (configFile.exists()) {
                    Config.loadConfigFile("config.xml");
                    Enumeration e = Config.getDeviceEntries().elements();
                    while (e.hasMoreElements()) {
                        DeviceEntry entry = (DeviceEntry)e.nextElement();
                        if (!entry.canRemove()) continue;
                        Config.removeDeviceEntry(entry);
                        File src = new File(Config.getConfigPath(), entry.getFileName());
                        File dst = File.createTempFile("dev", ".jar", Config.getConfigPath());
                        IOUtils.copyFile(src, dst);
                        entry.setFileName(dst.getName());
                        Config.addDeviceEntry(entry);
                    }
                } else {
                    Config.createDefaultConfigXml();
                }
                Config.saveConfig();
            }
        }
        catch (IOException ex) {
            Logger.error(ex);
            Config.createDefaultConfigXml();
        }
        finally {
            if (configXml == null) {
                Config.createDefaultConfigXml();
            }
        }
        urlsMRU.read(configXml.getChildOrNew("files").getChildOrNew("recent"));
        Config.initSystemProperties();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void loadConfigFile(String configFileName) throws IOException {
        File configFile = new File(Config.getConfigPath(), configFileName);
        FileInputStream is = null;
        String xml = "";
        try {
            is = new FileInputStream(configFile);
            BufferedInputStream dis = new BufferedInputStream(is);
            while (((InputStream)dis).available() > 0) {
                byte[] b = new byte[((InputStream)dis).available()];
                ((InputStream)dis).read(b);
                xml = xml + new String(b);
            }
            configXml = new XMLElement();
            configXml.parseString(xml);
        }
        catch (XMLParseException e) {
            try {
                Logger.error(e);
                Config.createDefaultConfigXml();
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(is);
                throw throwable;
            }
            IOUtils.closeQuietly(is);
        }
        IOUtils.closeQuietly(is);
    }

    private static void createDefaultConfigXml() {
        configXml = new XMLElement();
        configXml.setName("config");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void saveConfig() {
        urlsMRU.save(configXml.getChildOrNew("files").getChildOrNew("recent"));
        File configFile = new File(Config.getConfigPath(), "config2.xml");
        Config.getConfigPath().mkdirs();
        FileWriter fw = null;
        try {
            fw = new FileWriter(configFile);
            configXml.write(fw);
            fw.close();
        }
        catch (IOException ex) {
            try {
                Logger.error(ex);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(fw);
                throw throwable;
            }
            IOUtils.closeQuietly(fw);
        }
        IOUtils.closeQuietly(fw);
    }

    static Map getExtensions() {
        HashMap extensions = new HashMap();
        XMLElement extensionsXml = configXml.getChild("extensions");
        if (extensionsXml == null) {
            return extensions;
        }
        Enumeration en = extensionsXml.enumerateChildren();
        while (en.hasMoreElements()) {
            String className;
            XMLElement extension = (XMLElement)en.nextElement();
            if (!extension.getName().equals("extension") || (className = extension.getChildString("className", null)) == null) continue;
            HashMap<String, String> parameters = new HashMap<String, String>();
            parameters.put("emulatorID", Config.getEmulatorID());
            Enumeration een = extension.enumerateChildren();
            while (een.hasMoreElements()) {
                XMLElement propXml = (XMLElement)een.nextElement();
                if (!propXml.getName().equals("properties")) continue;
                Enumeration e_prop = propXml.enumerateChildren();
                while (e_prop.hasMoreElements()) {
                    XMLElement tmp_prop = (XMLElement)e_prop.nextElement();
                    if (!tmp_prop.getName().equals("property")) continue;
                    parameters.put(tmp_prop.getStringAttribute("name"), tmp_prop.getStringAttribute("value"));
                }
            }
            extensions.put(className, parameters);
        }
        return extensions;
    }

    private static void initSystemProperties() {
        Map<Object, Object> systemProperties = null;
        Enumeration e = configXml.enumerateChildren();
        while (e.hasMoreElements()) {
            XMLElement tmp = (XMLElement)e.nextElement();
            if (!tmp.getName().equals("system-properties")) continue;
            systemProperties = new HashMap();
            Enumeration e_prop = tmp.enumerateChildren();
            while (e_prop.hasMoreElements()) {
                XMLElement tmp_prop = (XMLElement)e_prop.nextElement();
                if (!tmp_prop.getName().equals("system-property")) continue;
                systemProperties.put(tmp_prop.getStringAttribute("name"), tmp_prop.getStringAttribute("value"));
            }
        }
        if (systemProperties == null) {
            systemProperties = new Properties();
            systemProperties.put("microedition.configuration", "CLDC-1.0");
            systemProperties.put("microedition.profiles", "MIDP-2.0");
            systemProperties.put("avetana.forceNativeLibrary", Boolean.TRUE.toString());
            XMLElement propertiesXml = configXml.getChildOrNew("system-properties");
            Iterator<Map.Entry<Object, Object>> i = systemProperties.entrySet().iterator();
            while (i.hasNext()) {
                Map.Entry<Object, Object> e2 = i.next();
                XMLElement xmlProperty = propertiesXml.addChild("system-property");
                xmlProperty.setAttribute("value", (String)e2.getValue());
                xmlProperty.setAttribute("name", (String)e2.getKey());
            }
            Config.saveConfig();
        }
        MIDletSystemProperties.setProperties(systemProperties);
    }

    public static File getConfigPath() {
        if (meHome == null) {
            meHome = Config.initMEHomePath();
        }
        return meHome;
    }

    public static Vector getDeviceEntries() {
        XMLElement devicesXml;
        Vector<DeviceEntry> result = new Vector<DeviceEntry>();
        if (defaultDevice == null) {
            defaultDevice = new DeviceEntry("Default device", null, "org/microemu/device/default/device.xml", true, false);
        }
        defaultDevice.setDefaultDevice(true);
        result.add(defaultDevice);
        if (resizableDevice == null) {
            resizableDevice = new DeviceEntry("Resizable device", null, "org/microemu/device/resizable/device.xml", false, false);
            Config.addDeviceEntry(resizableDevice);
        }
        if ((devicesXml = configXml.getChild("devices")) == null) {
            return result;
        }
        Enumeration e_device = devicesXml.enumerateChildren();
        while (e_device.hasMoreElements()) {
            XMLElement tmp_device = (XMLElement)e_device.nextElement();
            if (!tmp_device.getName().equals("device")) continue;
            boolean devDefault = false;
            if (tmp_device.getStringAttribute("default") != null && tmp_device.getStringAttribute("default").equals("true")) {
                devDefault = true;
                defaultDevice.setDefaultDevice(false);
            }
            String devName = tmp_device.getChildString("name", null);
            String devFile = tmp_device.getChildString("filename", null);
            String devClass = tmp_device.getChildString("class", null);
            String devDescriptor = tmp_device.getChildString("descriptor", null);
            if (devDescriptor == null) {
                result.add(new DeviceEntry(devName, devFile, devDefault, devClass, emulatorContext));
                continue;
            }
            result.add(new DeviceEntry(devName, devFile, devDescriptor, devDefault));
        }
        return result;
    }

    public static void addDeviceEntry(DeviceEntry entry) {
        Enumeration en = Config.getDeviceEntries().elements();
        while (en.hasMoreElements()) {
            DeviceEntry test = (DeviceEntry)en.nextElement();
            if (!test.getDescriptorLocation().equals(entry.getDescriptorLocation())) continue;
            return;
        }
        XMLElement devicesXml = configXml.getChildOrNew("devices");
        XMLElement deviceXml = devicesXml.addChild("device");
        if (entry.isDefaultDevice()) {
            deviceXml.setAttribute("default", "true");
        }
        deviceXml.addChild("name", entry.getName());
        deviceXml.addChild("filename", entry.getFileName());
        deviceXml.addChild("descriptor", entry.getDescriptorLocation());
        Config.saveConfig();
    }

    public static void removeDeviceEntry(DeviceEntry entry) {
        XMLElement devicesXml = configXml.getChild("devices");
        if (devicesXml == null) {
            return;
        }
        Enumeration e_device = devicesXml.enumerateChildren();
        while (e_device.hasMoreElements()) {
            XMLElement tmp_device = (XMLElement)e_device.nextElement();
            if (!tmp_device.getName().equals("device")) continue;
            String testDescriptor = tmp_device.getChildString("descriptor", null);
            if (testDescriptor == null) {
                devicesXml.removeChild(tmp_device);
                Config.saveConfig();
                continue;
            }
            if (!testDescriptor.equals(entry.getDescriptorLocation())) continue;
            devicesXml.removeChild(tmp_device);
            Config.saveConfig();
            break;
        }
    }

    public static void changeDeviceEntry(DeviceEntry entry) {
        XMLElement devicesXml = configXml.getChild("devices");
        if (devicesXml == null) {
            return;
        }
        Enumeration e_device = devicesXml.enumerateChildren();
        while (e_device.hasMoreElements()) {
            String testDescriptor;
            XMLElement tmp_device = (XMLElement)e_device.nextElement();
            if (!tmp_device.getName().equals("device") || !(testDescriptor = tmp_device.getChildString("descriptor", null)).equals(entry.getDescriptorLocation())) continue;
            if (entry.isDefaultDevice()) {
                tmp_device.setAttribute("default", "true");
            } else {
                tmp_device.removeAttribute("default");
            }
            Config.saveConfig();
            break;
        }
    }

    public static Rectangle getDeviceEntryDisplaySize(DeviceEntry entry) {
        XMLElement devicesXml = configXml.getChild("devices");
        if (devicesXml != null) {
            Enumeration e_device = devicesXml.enumerateChildren();
            while (e_device.hasMoreElements()) {
                XMLElement rectangleXml;
                String testDescriptor;
                XMLElement tmp_device = (XMLElement)e_device.nextElement();
                if (!tmp_device.getName().equals("device") || !(testDescriptor = tmp_device.getChildString("descriptor", null)).equals(entry.getDescriptorLocation()) || (rectangleXml = tmp_device.getChild("rectangle")) == null) continue;
                Rectangle result = new Rectangle();
                result.x = rectangleXml.getChildInteger("x", -1);
                result.y = rectangleXml.getChildInteger("y", -1);
                result.width = rectangleXml.getChildInteger("width", -1);
                result.height = rectangleXml.getChildInteger("height", -1);
                return result;
            }
        }
        return null;
    }

    public static void setDeviceEntryDisplaySize(DeviceEntry entry, Rectangle rect) {
        if (entry == null) {
            return;
        }
        XMLElement devicesXml = configXml.getChild("devices");
        if (devicesXml == null) {
            return;
        }
        Enumeration e_device = devicesXml.enumerateChildren();
        while (e_device.hasMoreElements()) {
            String testDescriptor;
            XMLElement tmp_device = (XMLElement)e_device.nextElement();
            if (!tmp_device.getName().equals("device") || !(testDescriptor = tmp_device.getChildString("descriptor", null)).equals(entry.getDescriptorLocation())) continue;
            XMLElement mainXml = tmp_device.getChildOrNew("rectangle");
            XMLElement xml = mainXml.getChildOrNew("x");
            xml.setContent(String.valueOf(rect.x));
            xml = mainXml.getChildOrNew("y");
            xml.setContent(String.valueOf(rect.y));
            xml = mainXml.getChildOrNew("width");
            xml.setContent(String.valueOf(rect.width));
            xml = mainXml.getChildOrNew("height");
            xml.setContent(String.valueOf(rect.height));
            Config.saveConfig();
            break;
        }
    }

    public static String getRecordStoreManagerClassName() {
        XMLElement recordStoreManagerXml = configXml.getChild("recordStoreManager");
        if (recordStoreManagerXml == null) {
            return null;
        }
        return recordStoreManagerXml.getStringAttribute("class");
    }

    public static void setRecordStoreManagerClassName(String className) {
        XMLElement recordStoreManagerXml = configXml.getChildOrNew("recordStoreManager");
        recordStoreManagerXml.setAttribute("class", className);
        Config.saveConfig();
    }

    public static boolean isLogConsoleLocationEnabled() {
        XMLElement logConsoleXml = configXml.getChild("logConsole");
        if (logConsoleXml == null) {
            return true;
        }
        return logConsoleXml.getBooleanAttribute("locationEnabled", true);
    }

    public static void setLogConsoleLocationEnabled(boolean state) {
        XMLElement logConsoleXml = configXml.getChildOrNew("logConsole");
        if (state) {
            logConsoleXml.setAttribute("locationEnabled", "true");
        } else {
            logConsoleXml.setAttribute("locationEnabled", "false");
        }
        Config.saveConfig();
    }

    public static boolean isWindowOnStart(String name) {
        XMLElement windowsXml = configXml.getChild("windows");
        if (windowsXml == null) {
            return false;
        }
        XMLElement mainXml = windowsXml.getChild(name);
        if (mainXml == null) {
            return false;
        }
        String attr = mainXml.getStringAttribute("onstart", "false");
        return attr.trim().toLowerCase().equals("true");
    }

    public static Rectangle getWindow(String name, Rectangle defaultWindow) {
        XMLElement windowsXml = configXml.getChild("windows");
        if (windowsXml == null) {
            return defaultWindow;
        }
        XMLElement mainXml = windowsXml.getChild(name);
        if (mainXml == null) {
            return defaultWindow;
        }
        Rectangle window = new Rectangle();
        window.x = mainXml.getChildInteger("x", defaultWindow.x);
        window.y = mainXml.getChildInteger("y", defaultWindow.y);
        window.width = mainXml.getChildInteger("width", defaultWindow.width);
        window.height = mainXml.getChildInteger("height", defaultWindow.height);
        return window;
    }

    public static void setWindow(String name, Rectangle window, boolean onStart) {
        XMLElement windowsXml = configXml.getChildOrNew("windows");
        XMLElement mainXml = windowsXml.getChildOrNew(name);
        if (onStart) {
            mainXml.setAttribute("onstart", "true");
        } else {
            mainXml.removeAttribute("onstart");
        }
        XMLElement xml = mainXml.getChildOrNew("x");
        xml.setContent(String.valueOf(window.x));
        xml = mainXml.getChildOrNew("y");
        xml.setContent(String.valueOf(window.y));
        xml = mainXml.getChildOrNew("width");
        xml.setContent(String.valueOf(window.width));
        xml = mainXml.getChildOrNew("height");
        xml.setContent(String.valueOf(window.height));
        Config.saveConfig();
    }

    public static String getRecentDirectory(String key) {
        String defaultResult = ".";
        XMLElement filesXml = configXml.getChild("files");
        if (filesXml == null) {
            return defaultResult;
        }
        return filesXml.getChildString(key, defaultResult);
    }

    public static void setRecentDirectory(String key, String recentJadDirectory) {
        XMLElement filesXml = configXml.getChildOrNew("files");
        XMLElement recentJadDirectoryXml = filesXml.getChildOrNew(key);
        recentJadDirectoryXml.setContent(recentJadDirectory);
        Config.saveConfig();
    }

    public static MRUList getUrlsMRU() {
        return urlsMRU;
    }

    public static String getEmulatorID() {
        return emulatorID;
    }

    public static void setEmulatorID(String emulatorID) {
        Config.emulatorID = emulatorID;
    }

    static {
        configXml = new XMLElement();
        urlsMRU = new MRUList(MidletURLReference.class, "midlet");
    }
}

