/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device;

import org.microemu.device.Device;

public class DeviceFactory {
    private static Device device;

    public static Device getDevice() {
        return device;
    }

    public static void setDevice(Device device) {
        if (DeviceFactory.device != null) {
            DeviceFactory.device.destroy();
        }
        device.init();
        DeviceFactory.device = device;
    }
}

