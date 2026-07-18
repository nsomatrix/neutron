/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import com.barteo.emulator.device.Device;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.microemu.EmulatorContext;
import org.microemu.app.Common;
import org.microemu.app.Config;
import org.microemu.log.Logger;

public class DeviceEntry {
    private String name;
    private String fileName;
    private String descriptorLocation;
    private boolean defaultDevice;
    private boolean canRemove;
    private String className;
    private EmulatorContext emulatorContext;

    public DeviceEntry(String name, String fileName, String descriptorLocation, boolean defaultDevice) {
        this(name, fileName, descriptorLocation, defaultDevice, true);
    }

    public DeviceEntry(String name, String fileName, String descriptorLocation, boolean defaultDevice, boolean canRemove) {
        this.name = name;
        this.fileName = fileName;
        this.descriptorLocation = descriptorLocation;
        this.defaultDevice = defaultDevice;
        this.canRemove = canRemove;
    }

    public DeviceEntry(String name, String fileName, boolean defaultDevice, String className, EmulatorContext emulatorContext) {
        this(name, fileName, null, defaultDevice, true);
        this.className = className;
        this.emulatorContext = emulatorContext;
    }

    public boolean canRemove() {
        return this.canRemove;
    }

    public String getDescriptorLocation() {
        if (this.descriptorLocation == null) {
            URL[] urls = new URL[1];
            try {
                urls[0] = new File(Config.getConfigPath(), this.fileName).toURI().toURL();
                ClassLoader classLoader = Common.createExtensionsClassLoader(urls);
                Class<?> deviceClass = Class.forName(this.className, true, classLoader);
                Device device = (Device)deviceClass.newInstance();
                com.barteo.emulator.EmulatorContext oldContext = new com.barteo.emulator.EmulatorContext(this.emulatorContext);
                device.init(oldContext);
                this.descriptorLocation = device.getDescriptorLocation();
            }
            catch (MalformedURLException ex) {
                Logger.error(ex);
            }
            catch (ClassNotFoundException ex) {
                Logger.error(ex);
            }
            catch (InstantiationException ex) {
                Logger.error(ex);
            }
            catch (IllegalAccessException ex) {
                Logger.error(ex);
            }
        }
        return this.descriptorLocation;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getName() {
        return this.name;
    }

    public boolean isDefaultDevice() {
        return this.defaultDevice;
    }

    public void setDefaultDevice(boolean b) {
        this.defaultDevice = b;
    }

    public boolean equals(DeviceEntry test) {
        if (test == null) {
            return false;
        }
        return test.getDescriptorLocation().equals(this.getDescriptorLocation());
    }

    public String toString() {
        if (this.defaultDevice) {
            return this.name + " (default)";
        }
        return this.name;
    }
}

