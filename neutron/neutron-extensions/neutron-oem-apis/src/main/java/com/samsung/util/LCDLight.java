/*
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package com.samsung.util;

import org.neutron.device.DeviceFactory;
import org.neutron.log.Logger;

public class LCDLight {

	public static boolean isSupported() {
		return true;
	}

	public static void on(int duration) {
		try {
			if (DeviceFactory.getDevice() != null && DeviceFactory.getDevice().getDeviceDisplay() != null) {
				DeviceFactory.getDevice().getDeviceDisplay().flashBacklight(duration);
			}
		} catch (Throwable t) {
			Logger.debug("Samsung LCDLight.on: " + t.getMessage());
		}
	}

	public static void off() {
		try {
			if (DeviceFactory.getDevice() != null && DeviceFactory.getDevice().getDeviceDisplay() != null) {
				DeviceFactory.getDevice().getDeviceDisplay().flashBacklight(0);
			}
		} catch (Throwable t) {
			Logger.debug("Samsung LCDLight.off: " + t.getMessage());
		}
	}
}

