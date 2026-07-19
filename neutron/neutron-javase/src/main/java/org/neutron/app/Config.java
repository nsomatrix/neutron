/*
 *  Neutron
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */

package org.neutron.app;

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

import org.neutron.app.util.DeviceEntry;
import org.neutron.app.util.IOUtils;
import org.neutron.app.util.MIDletSystemProperties;
import org.neutron.app.util.MRUList;
import org.neutron.app.util.MidletURLReference;
import org.neutron.device.EmulatorContext;
import org.neutron.device.impl.DeviceImpl;
import org.neutron.device.impl.Rectangle;
import org.neutron.log.Logger;
import org.neutron.microedition.ImplementationInitialization;

public class Config {

	private static File meHome;

	/**
	 * emulatorID used for multiple instance of Neutron, now redefine home
	 */
	private static String emulatorID;

	private static XMLElement configXml = new XMLElement();

	private static DeviceEntry defaultDevice;

	private static EmulatorContext emulatorContext;

	private static MRUList urlsMRU = new MRUList(MidletURLReference.class, "midlet");

	private static File initMEHomePath() {
		try {
			File meHome = new File(System.getProperty("user.home") + "/.neutron/");
			if (emulatorID != null) {
				return new File(meHome, emulatorID);
			} else {
				return meHome;
			}
		} catch (SecurityException e) {
			Logger.error("Cannot access user.home", e);
			return null;
		}
	}

	public static void loadConfig(DeviceEntry defaultDevice, EmulatorContext emulatorContext) {
		Config.defaultDevice = defaultDevice;
		Config.emulatorContext = emulatorContext;

		File configFile = new File(getConfigPath(), "config2.xml");
		try {
			if (configFile.exists()) {
				loadConfigFile("config2.xml");
			} else {
				configFile = new File(getConfigPath(), "config.xml");
				if (configFile.exists()) {
					// migrate from config.xml
					loadConfigFile("config.xml");

					for (Enumeration e = getDeviceEntries().elements(); e.hasMoreElements();) {
						DeviceEntry entry = (DeviceEntry) e.nextElement();
						if (!entry.canRemove()) {
							continue;
						}

						removeDeviceEntry(entry);
						File src = new File(getConfigPath(), entry.getFileName());
						File dst = File.createTempFile("dev", ".jar", getConfigPath());
						IOUtils.copyFile(src, dst);
						entry.setFileName(dst.getName());
						addDeviceEntry(entry);
					}
				} else {
					createDefaultConfigXml();
				}
				saveConfig();
			}
		} catch (IOException ex) {
			Logger.error(ex);
			createDefaultConfigXml();
		} finally {
			// Happens in webstart untrusted environment
			if (configXml == null) {
				createDefaultConfigXml();
			}
		}
		urlsMRU.read(configXml.getChildOrNew("files").getChildOrNew("recent"));
		initSystemProperties();
		org.neutron.app.util.MemoryManager.init();
		initProxyAuthenticator();
	}

	private static void loadConfigFile(String configFileName) throws IOException {
		File configFile = new File(getConfigPath(), configFileName);
		InputStream is = null;
		String xml = "";
		try {
			InputStream dis = new BufferedInputStream(is = new FileInputStream(configFile));
			while (dis.available() > 0) {
				byte[] b = new byte[dis.available()];
				dis.read(b);
				xml += new String(b);
			}
			configXml = new XMLElement();
			configXml.parseString(xml);
		} catch (XMLParseException e) {
			Logger.error(e);
			createDefaultConfigXml();
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	private static void createDefaultConfigXml() {
		configXml = new XMLElement();
		configXml.setName("config");
	}

	public static void saveConfig() {

		urlsMRU.save(configXml.getChildOrNew("files").getChildOrNew("recent"));

		File configFile = new File(getConfigPath(), "config2.xml");

		getConfigPath().mkdirs();
		FileWriter fw = null;
		try {
			fw = new FileWriter(configFile);
			configXml.write(fw);
			fw.close();
		} catch (IOException ex) {
			Logger.error(ex);
		} finally {
			IOUtils.closeQuietly(fw);
		}
	}

	static Map getExtensions() {
		Map extensions = new HashMap();
		XMLElement extensionsXml = configXml.getChild("extensions");
		if (extensionsXml == null) {
			return extensions;
		}
		for (Enumeration en = extensionsXml.enumerateChildren(); en.hasMoreElements();) {
			XMLElement extension = (XMLElement) en.nextElement();
			if (!extension.getName().equals("extension")) {
				continue;
			}
			String className = (String) extension.getChildString("className", null);
			if (className == null) {
				continue;
			}

			Map parameters = new HashMap();
			parameters.put(ImplementationInitialization.PARAM_EMULATOR_ID, Config.getEmulatorID());

			for (Enumeration een = extension.enumerateChildren(); een.hasMoreElements();) {
				XMLElement propXml = (XMLElement) een.nextElement();
				if (propXml.getName().equals("properties")) {
					for (Enumeration e_prop = propXml.enumerateChildren(); e_prop.hasMoreElements();) {
						XMLElement tmp_prop = (XMLElement) e_prop.nextElement();
						if (tmp_prop.getName().equals("property")) {
							parameters.put(tmp_prop.getStringAttribute("name"), tmp_prop.getStringAttribute("value"));
						}
					}
				}
			}

			extensions.put(className, parameters);
		}
		return extensions;
	}

	private static void initSystemProperties() {
		Map systemProperties = null;

		for (Enumeration e = configXml.enumerateChildren(); e.hasMoreElements();) {
			XMLElement tmp = (XMLElement) e.nextElement();
			if (tmp.getName().equals("system-properties")) {
				// Permits null values.
				systemProperties = new HashMap();
				for (Enumeration e_prop = tmp.enumerateChildren(); e_prop.hasMoreElements();) {
					XMLElement tmp_prop = (XMLElement) e_prop.nextElement();
					if (tmp_prop.getName().equals("system-property")) {
						systemProperties.put(tmp_prop.getStringAttribute("name"), tmp_prop.getStringAttribute("value"));
					}
				}
			}
		}

		// No <system-properties> in config2.xml
		if (systemProperties == null) {
			systemProperties = new Properties();
			// Ask avetana to ignore MIDP profiles and load JSR-82
			// implementation dll or so
			systemProperties.put("avetana.forceNativeLibrary", Boolean.TRUE.toString());

			XMLElement propertiesXml = configXml.getChildOrNew("system-properties");

			for (Iterator i = systemProperties.entrySet().iterator(); i.hasNext();) {
				Map.Entry e = (Map.Entry) i.next();
				XMLElement xmlProperty = propertiesXml.addChild("system-property");
				xmlProperty.setAttribute("value", (String) e.getValue());
				xmlProperty.setAttribute("name", (String) e.getKey());
			}

			saveConfig();
		}

		MIDletSystemProperties.setProperties(systemProperties);
	}

	public static File getConfigPath() {
		if (meHome == null) {
			meHome = initMEHomePath();
		}
		return meHome;
	}

	public static Vector getDeviceEntries() {
		Vector result = new Vector();

		if (defaultDevice == null) {
			defaultDevice = new DeviceEntry("Resizable device", null, DeviceImpl.DEFAULT_LOCATION, true, false);
		}
		defaultDevice.setDefaultDevice(true);
		result.add(defaultDevice);

		XMLElement devicesXml = configXml.getChild("devices");
		if (devicesXml == null) {
			return result;
		}

		for (Enumeration e_device = devicesXml.enumerateChildren(); e_device.hasMoreElements();) {
			XMLElement tmp_device = (XMLElement) e_device.nextElement();
			if (tmp_device.getName().equals("device")) {
				String defaultAttr = tmp_device.getStringAttribute("default");
				if (defaultAttr == null) {
					defaultAttr = tmp_device.getStringAttribute("DEFAULT");
				}
				boolean devDefault = (defaultAttr != null && defaultAttr.equalsIgnoreCase("true"));

				String devName = tmp_device.getChildString("name", null);
				String devFile = tmp_device.getChildString("filename", null);
				String devClass = tmp_device.getChildString("class", null);
				String devDescriptor = tmp_device.getChildString("descriptor", null);
				if (devDescriptor != null && (devDescriptor.equals("org/neutron/device/default/device.xml")
						|| devDescriptor.equals(DeviceImpl.RESIZABLE_LOCATION))) {
					devDescriptor = DeviceImpl.DEFAULT_LOCATION;
				}
				if (devDescriptor == null) {
					if (devDefault) {
						defaultDevice.setDefaultDevice(false);
						for (int i = 0; i < result.size(); i++) {
							((DeviceEntry) result.elementAt(i)).setDefaultDevice(false);
						}
					}
					result.add(new DeviceEntry(devName, devFile, devDefault, devClass, emulatorContext));
				} else {
					boolean duplicate = false;
					for (Enumeration en = result.elements(); en.hasMoreElements();) {
						DeviceEntry test = (DeviceEntry) en.nextElement();
						if (devDescriptor.equals(test.getDescriptorLocation())) {
							if (devDefault) {
								for (int i = 0; i < result.size(); i++) {
									((DeviceEntry) result.elementAt(i)).setDefaultDevice(false);
								}
								test.setDefaultDevice(true);
							}
							duplicate = true;
							break;
						}
					}
					if (!duplicate) {
						if (devDefault) {
							defaultDevice.setDefaultDevice(false);
							for (int i = 0; i < result.size(); i++) {
								((DeviceEntry) result.elementAt(i)).setDefaultDevice(false);
							}
						}
						result.add(new DeviceEntry(devName, devFile, devDescriptor, devDefault));
					}
				}
			}
		}

		return result;
	}

	public static void addDeviceEntry(DeviceEntry entry) {
		for (Enumeration en = getDeviceEntries().elements(); en.hasMoreElements();) {
			DeviceEntry test = (DeviceEntry) en.nextElement();
			if (test.getDescriptorLocation().equals(entry.getDescriptorLocation())) {
				return;
			}
		}

		XMLElement devicesXml = configXml.getChildOrNew("devices");

		XMLElement deviceXml = devicesXml.addChild("device");
		if (entry.isDefaultDevice()) {
			deviceXml.setAttribute("default", "true");
		}
		deviceXml.addChild("name", entry.getName());
		deviceXml.addChild("filename", entry.getFileName());
		deviceXml.addChild("descriptor", entry.getDescriptorLocation());

		saveConfig();
	}

	public static void removeDeviceEntry(DeviceEntry entry) {
		XMLElement devicesXml = configXml.getChild("devices");
		if (devicesXml == null) {
			return;
		}

		for (Enumeration e_device = devicesXml.enumerateChildren(); e_device.hasMoreElements();) {
			XMLElement tmp_device = (XMLElement) e_device.nextElement();
			if (tmp_device.getName().equals("device")) {
				String testDescriptor = tmp_device.getChildString("descriptor", null);
				// this is needed by migration config.xml -> config2.xml
				if (testDescriptor == null) {
					devicesXml.removeChild(tmp_device);

					saveConfig();
					continue;
				}
				if (testDescriptor.equals(entry.getDescriptorLocation())) {
					devicesXml.removeChild(tmp_device);

					saveConfig();
					break;
				}
			}
		}
	}

	public static void changeDeviceEntry(DeviceEntry entry) {
		XMLElement devicesXml = configXml.getChild("devices");
		if (devicesXml == null) {
			return;
		}

		for (Enumeration e_device = devicesXml.enumerateChildren(); e_device.hasMoreElements();) {
			XMLElement tmp_device = (XMLElement) e_device.nextElement();
			if (tmp_device.getName().equals("device")) {
				String testDescriptor = tmp_device.getChildString("descriptor", null);
				if (testDescriptor.equals(entry.getDescriptorLocation())) {
					if (entry.isDefaultDevice()) {
						tmp_device.setAttribute("default", "true");
					} else {
						tmp_device.removeAttribute("default");
					}

					saveConfig();
					break;
				}
			}
		}
	}

	public static Rectangle getDeviceEntryDisplaySize(DeviceEntry entry) {
		XMLElement devicesXml = configXml.getChild("devices");

		if (devicesXml != null) {
			for (Enumeration e_device = devicesXml.enumerateChildren(); e_device.hasMoreElements();) {
				XMLElement tmp_device = (XMLElement) e_device.nextElement();
				if (tmp_device.getName().equals("device")) {
					String testDescriptor = tmp_device.getChildString("descriptor", null);
					if (testDescriptor.equals(entry.getDescriptorLocation())) {
						XMLElement rectangleXml = tmp_device.getChild("rectangle");
						if (rectangleXml != null) {
							Rectangle result = new Rectangle();
							result.x = rectangleXml.getChildInteger("x", -1);
							result.y = rectangleXml.getChildInteger("y", -1);
							result.width = rectangleXml.getChildInteger("width", -1);
							result.height = rectangleXml.getChildInteger("height", -1);
	
							return result;
						}
					}
				}
			}
		}

		return null;
	}

	public static void setDeviceEntryDisplaySize(DeviceEntry entry, Rectangle rect) {
		if (entry == null) {
			return;
		}
		XMLElement devicesXml = configXml.getChildOrNew("devices");

		XMLElement targetDeviceXml = null;
		for (Enumeration e_device = devicesXml.enumerateChildren(); e_device.hasMoreElements();) {
			XMLElement tmp_device = (XMLElement) e_device.nextElement();
			if (tmp_device.getName().equals("device")) {
				String testDescriptor = tmp_device.getChildString("descriptor", null);
				if (testDescriptor != null && testDescriptor.equals(entry.getDescriptorLocation())) {
					targetDeviceXml = tmp_device;
					break;
				}
			}
		}

		if (targetDeviceXml == null) {
			targetDeviceXml = devicesXml.addChild("device");
			targetDeviceXml.addChild("name", entry.getName());
			if (entry.getFileName() != null) {
				targetDeviceXml.addChild("filename", entry.getFileName());
			}
			targetDeviceXml.addChild("descriptor", entry.getDescriptorLocation());
		}

		XMLElement mainXml = targetDeviceXml.getChildOrNew("rectangle");
		XMLElement xml = mainXml.getChildOrNew("x");
		xml.setContent(String.valueOf(rect.x));
		xml = mainXml.getChildOrNew("y");
		xml.setContent(String.valueOf(rect.y));
		xml = mainXml.getChildOrNew("width");
		xml.setContent(String.valueOf(rect.width));
		xml = mainXml.getChildOrNew("height");
		xml.setContent(String.valueOf(rect.height));

		saveConfig();
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

		saveConfig();
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

		saveConfig();
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
		if (attr.trim().toLowerCase().equals("true")) {
			return true;
		} else {
			return false;
		}
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

		saveConfig();
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

		saveConfig();
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

	public static String getTheme() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return "FlatLaf macOS Light";
		}
		return optionsXml.getChildString("theme", "FlatLaf macOS Light");
	}

	public static void setTheme(String theme) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement themeXml = optionsXml.getChildOrNew("theme");
		themeXml.setContent(theme);
		saveConfig();
	}

	public static String getGraphicsFilter() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return "Nearest Neighbor";
		}
		return optionsXml.getChildString("graphicsFilter", "Nearest Neighbor");
	}

	public static void setGraphicsFilter(String filter) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement filterXml = optionsXml.getChildOrNew("graphicsFilter");
		filterXml.setContent(filter);
		saveConfig();
	}

	public static int getBrightness() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) return 0;
		try {
			return Integer.parseInt(optionsXml.getChildString("brightness", "0"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static void setBrightness(int val) {
		configXml.getChildOrNew("options").getChildOrNew("brightness").setContent(String.valueOf(val));
		saveConfig();
	}

	public static int getContrast() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) return 100;
		try {
			return Integer.parseInt(optionsXml.getChildString("contrast", "100"));
		} catch (NumberFormatException e) {
			return 100;
		}
	}

	public static void setContrast(int val) {
		configXml.getChildOrNew("options").getChildOrNew("contrast").setContent(String.valueOf(val));
		saveConfig();
	}

	public static float getGamma() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) return 1.0f;
		try {
			return Float.parseFloat(optionsXml.getChildString("gamma", "1.0"));
		} catch (NumberFormatException e) {
			return 1.0f;
		}
	}

	public static void setGamma(float val) {
		configXml.getChildOrNew("options").getChildOrNew("gamma").setContent(String.valueOf(val));
		saveConfig();
	}

	public static int getSaturation() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) return 100;
		try {
			return Integer.parseInt(optionsXml.getChildString("saturation", "100"));
		} catch (NumberFormatException e) {
			return 100;
		}
	}

	public static void setSaturation(int val) {
		configXml.getChildOrNew("options").getChildOrNew("saturation").setContent(String.valueOf(val));
		saveConfig();
	}

	public static int getSharpness() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) return 0;
		try {
			return Integer.parseInt(optionsXml.getChildString("sharpness", "0"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static void setSharpness(int val) {
		configXml.getChildOrNew("options").getChildOrNew("sharpness").setContent(String.valueOf(val));
		saveConfig();
	}

	public static int getGhosting() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) return 0;
		try {
			return Integer.parseInt(optionsXml.getChildString("ghosting", "0"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static void setGhosting(int val) {
		configXml.getChildOrNew("options").getChildOrNew("ghosting").setContent(String.valueOf(val));
		saveConfig();
	}

	public static boolean isInvert() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) return false;
		return Boolean.parseBoolean(optionsXml.getChildString("invert", "false"));
	}

	public static void setInvert(boolean val) {
		configXml.getChildOrNew("options").getChildOrNew("invert").setContent(String.valueOf(val));
		saveConfig();
	}

	public static boolean isSleepModeEnabled() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return false;
		}
		String val = optionsXml.getChildString("sleepMode", "false");
		return Boolean.parseBoolean(val);
	}

	public static void setSleepModeEnabled(boolean enabled) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement sleepXml = optionsXml.getChildOrNew("sleepMode");
		sleepXml.setContent(String.valueOf(enabled));
		saveConfig();
	}

	public static int getMemoryLimit() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return 64; // Default to 64MB
		}
		String val = optionsXml.getChildString("memoryLimit", "64");
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			return 64;
		}
	}

	public static void setMemoryLimit(int limit) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement limitXml = optionsXml.getChildOrNew("memoryLimit");
		limitXml.setContent(String.valueOf(limit));
		saveConfig();
	}

	public static int getMaxFps() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return -1; // Default to -1 (unlimited)
		}
		String val = optionsXml.getChildString("maxFps", "-1");
		try {
			return Integer.parseInt(val);
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static void setMaxFps(int maxFps) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement maxFpsXml = optionsXml.getChildOrNew("maxFps");
		maxFpsXml.setContent(String.valueOf(maxFps));
		saveConfig();
	}

	public static double getSpeedMultiplier() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return 1.0;
		}
		String val = optionsXml.getChildString("speedMultiplier", "1.0");
		try {
			return Double.parseDouble(val);
		} catch (NumberFormatException e) {
			return 1.0;
		}
	}

	public static void setSpeedMultiplier(double multiplier) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement multiplierXml = optionsXml.getChildOrNew("speedMultiplier");
		multiplierXml.setContent(String.valueOf(multiplier));
		saveConfig();
	}

	public static boolean isPerfHudEnabled() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return false;
		}
		return Boolean.parseBoolean(optionsXml.getChildString("perfHudEnabled", "false"));
	}

	public static void setPerfHudEnabled(boolean enabled) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement hudXml = optionsXml.getChildOrNew("perfHudEnabled");
		hudXml.setContent(String.valueOf(enabled));
		saveConfig();
	}

	public static boolean isNetworkSnifferEnabled() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return true;
		}
		return Boolean.parseBoolean(optionsXml.getChildString("networkSnifferEnabled", "true"));
	}

	public static void setNetworkSnifferEnabled(boolean enabled) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement snifferXml = optionsXml.getChildOrNew("networkSnifferEnabled");
		snifferXml.setContent(String.valueOf(enabled));
		saveConfig();
	}

	public static boolean isShowMouseCoordinates() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return false;
		}
		return Boolean.parseBoolean(optionsXml.getChildString("showMouseCoordinates", "false"));
	}

	public static void setShowMouseCoordinates(boolean enabled) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement coordsXml = optionsXml.getChildOrNew("showMouseCoordinates");
		coordsXml.setContent(String.valueOf(enabled));
		saveConfig();
	}

	public static boolean isNetworkAccessEnabled() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return true;
		}
		return Boolean.parseBoolean(optionsXml.getChildString("networkAccess", "true"));
	}

	public static void setNetworkAccessEnabled(boolean enabled) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement accessXml = optionsXml.getChildOrNew("networkAccess");
		accessXml.setContent(String.valueOf(enabled));
		saveConfig();
	}

	public static int getScaledDisplayZoom() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return -1;
		}
		try {
			return Integer.parseInt(optionsXml.getChildString("scaledDisplayZoom", "-1"));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	public static void setScaledDisplayZoom(int zoom) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement zoomXml = optionsXml.getChildOrNew("scaledDisplayZoom");
		zoomXml.setContent(String.valueOf(zoom));
		saveConfig();
	}

	public static boolean isNetworkOverlayEnabled() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return true;
		}
		return Boolean.parseBoolean(optionsXml.getChildString("networkOverlayEnabled", "true"));
	}

	public static void setNetworkOverlayEnabled(boolean enabled) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement overlayXml = optionsXml.getChildOrNew("networkOverlayEnabled");
		overlayXml.setContent(String.valueOf(enabled));
		saveConfig();
	}

	public static boolean isAutoClickerEnabled() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return false;
		}
		return Boolean.parseBoolean(optionsXml.getChildString("autoClickerEnabled", "false"));
	}

	public static void setAutoClickerEnabled(boolean enabled) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement clickerXml = optionsXml.getChildOrNew("autoClickerEnabled");
		clickerXml.setContent(String.valueOf(enabled));
		saveConfig();
	}

	public static int getAutoClickerX() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return 0;
		}
		try {
			return Integer.parseInt(optionsXml.getChildString("autoClickerX", "0"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static void setAutoClickerX(int x) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement clickerXml = optionsXml.getChildOrNew("autoClickerX");
		clickerXml.setContent(String.valueOf(x));
		saveConfig();
	}

	public static int getAutoClickerY() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return 0;
		}
		try {
			return Integer.parseInt(optionsXml.getChildString("autoClickerY", "0"));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static void setAutoClickerY(int y) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement clickerXml = optionsXml.getChildOrNew("autoClickerY");
		clickerXml.setContent(String.valueOf(y));
		saveConfig();
	}

	public static int getAutoClickerInterval() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return 1000;
		}
		try {
			return Integer.parseInt(optionsXml.getChildString("autoClickerInterval", "1000"));
		} catch (NumberFormatException e) {
			return 1000;
		}
	}

	public static void setAutoClickerInterval(int interval) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement clickerXml = optionsXml.getChildOrNew("autoClickerInterval");
		clickerXml.setContent(String.valueOf(interval));
		saveConfig();
	}

	public static String getAutoClickerTargets() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return "";
		}
		return optionsXml.getChildString("autoClickerTargets", "");
	}

	public static void setAutoClickerTargets(String targets) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement clickerXml = optionsXml.getChildOrNew("autoClickerTargets");
		clickerXml.setContent(targets);
		saveConfig();
	}

	public static boolean isProxyEnabled() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return false;
		}
		return Boolean.parseBoolean(optionsXml.getChildString("proxyEnabled", "false"));
	}

	public static void setProxyEnabled(boolean enabled) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement enabledXml = optionsXml.getChildOrNew("proxyEnabled");
		enabledXml.setContent(String.valueOf(enabled));
		saveConfig();
	}

	public static String getProxyType() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return "HTTP";
		}
		return optionsXml.getChildString("proxyType", "HTTP");
	}

	public static void setProxyType(String type) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement typeXml = optionsXml.getChildOrNew("proxyType");
		typeXml.setContent(type);
		saveConfig();
	}

	public static String getProxyHost() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return "";
		}
		return optionsXml.getChildString("proxyHost", "");
	}

	public static void setProxyHost(String host) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement hostXml = optionsXml.getChildOrNew("proxyHost");
		hostXml.setContent(host);
		saveConfig();
	}

	public static int getProxyPort() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return 8080;
		}
		try {
			return Integer.parseInt(optionsXml.getChildString("proxyPort", "8080"));
		} catch (NumberFormatException e) {
			return 8080;
		}
	}

	public static void setProxyPort(int port) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement portXml = optionsXml.getChildOrNew("proxyPort");
		portXml.setContent(String.valueOf(port));
		saveConfig();
	}

	public static boolean isProxyAuthEnabled() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return false;
		}
		return Boolean.parseBoolean(optionsXml.getChildString("proxyAuthEnabled", "false"));
	}

	public static void setProxyAuthEnabled(boolean enabled) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement authXml = optionsXml.getChildOrNew("proxyAuthEnabled");
		authXml.setContent(String.valueOf(enabled));
		saveConfig();
	}

	public static String getProxyUsername() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return "";
		}
		return optionsXml.getChildString("proxyUsername", "");
	}

	public static void setProxyUsername(String username) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement userXml = optionsXml.getChildOrNew("proxyUsername");
		userXml.setContent(username);
		saveConfig();
	}

	public static String getProxyPassword() {
		XMLElement optionsXml = configXml.getChild("options");
		if (optionsXml == null) {
			return "";
		}
		return optionsXml.getChildString("proxyPassword", "");
	}

	public static void setProxyPassword(String password) {
		XMLElement optionsXml = configXml.getChildOrNew("options");
		XMLElement passXml = optionsXml.getChildOrNew("proxyPassword");
		passXml.setContent(password);
		saveConfig();
	}

	public static java.net.Proxy getProxyInstance() {
		if (!isProxyEnabled()) {
			return java.net.Proxy.NO_PROXY;
		}
		String host = getProxyHost();
		if (host == null || host.trim().isEmpty()) {
			return java.net.Proxy.NO_PROXY;
		}
		int port = getProxyPort();
		String typeStr = getProxyType();
		java.net.Proxy.Type type = java.net.Proxy.Type.HTTP;
		if ("SOCKS".equalsIgnoreCase(typeStr)) {
			type = java.net.Proxy.Type.SOCKS;
		}
		try {
			return new java.net.Proxy(type, new java.net.InetSocketAddress(host, port));
		} catch (Exception e) {
			Logger.error("Failed to create proxy object", e);
			return java.net.Proxy.NO_PROXY;
		}
	}

	public static void initProxyAuthenticator() {
		try {
			java.net.Authenticator.setDefault(new java.net.Authenticator() {
				protected java.net.PasswordAuthentication getPasswordAuthentication() {
					if (getRequestorType() == RequestorType.PROXY) {
						if (isProxyEnabled() && isProxyAuthEnabled()) {
							String username = getProxyUsername();
							String password = getProxyPassword();
							if (username != null && !username.isEmpty()) {
								return new java.net.PasswordAuthentication(username, password.toCharArray());
							}
						}
					}
					return null;
				}
			});
		} catch (SecurityException e) {
			Logger.error("Cannot set default Authenticator", e);
		}
	}



	public static String preLoadTheme() {
		File configFile = new File(getConfigPath(), "config2.xml");
		if (!configFile.exists()) {
			configFile = new File(getConfigPath(), "config.xml");
		}
		if (!configFile.exists()) {
			return "FlatLaf macOS Light";
		}
		try {
			java.io.InputStream is = new java.io.BufferedInputStream(new java.io.FileInputStream(configFile));
			String xml = "";
			try {
				while (is.available() > 0) {
					byte[] b = new byte[is.available()];
					is.read(b);
					xml += new String(b);
				}
				XMLElement xmlRoot = new XMLElement();
				xmlRoot.parseString(xml);
				XMLElement optionsXml = xmlRoot.getChild("options");
				if (optionsXml != null) {
					return optionsXml.getChildString("theme", "FlatLaf macOS Light");
				}
			} finally {
				is.close();
			}
		} catch (Exception ex) {
			// ignore, fallback to default
		}
		return "FlatLaf macOS Light";
	}

	public static java.util.List getConnectedDirectories() {
		java.util.List list = new java.util.ArrayList();
		XMLElement dirsXml = configXml.getChild("connectedDirectories");
		if (dirsXml == null) {
			return list;
		}
		for (Enumeration e = dirsXml.enumerateChildren(); e.hasMoreElements();) {
			XMLElement dir = (XMLElement) e.nextElement();
			if (dir.getName().equals("directory")) {
				String path = dir.getContent();
				if (path != null && !path.trim().isEmpty()) {
					list.add(path.trim());
				}
			}
		}
		return list;
	}

	public static void addConnectedDirectory(String path) {
		if (path == null || path.trim().isEmpty()) {
			return;
		}
		path = path.trim();
		java.util.List current = getConnectedDirectories();
		if (current.contains(path)) {
			return;
		}
		XMLElement dirsXml = configXml.getChildOrNew("connectedDirectories");
		XMLElement dirXml = dirsXml.addChild("directory");
		dirXml.setContent(path);
		saveConfig();
	}

	public static void removeConnectedDirectory(String path) {
		if (path == null || path.trim().isEmpty()) {
			return;
		}
		path = path.trim();
		XMLElement dirsXml = configXml.getChild("connectedDirectories");
		if (dirsXml == null) {
			return;
		}
		for (Enumeration e = dirsXml.enumerateChildren(); e.hasMoreElements();) {
			XMLElement dir = (XMLElement) e.nextElement();
			if (dir.getName().equals("directory") && path.equals(dir.getContent())) {
				dirsXml.removeChild(dir);
				saveConfig();
				break;
			}
		}
	}

}

