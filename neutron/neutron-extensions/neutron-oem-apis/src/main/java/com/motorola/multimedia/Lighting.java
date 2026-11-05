/*
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package com.motorola.multimedia;

import org.neutron.device.DeviceFactory;
import org.neutron.log.Logger;

public class Lighting {

	public static boolean isSupported() {
		return true;
	}

	public static void backlightOn() {
		try {
			if (DeviceFactory.getDevice() != null && DeviceFactory.getDevice().getDeviceDisplay() != null) {
				DeviceFactory.getDevice().getDeviceDisplay().flashBacklight(Integer.MAX_VALUE);
			}
		} catch (Throwable t) {
			Logger.debug("Motorola Lighting.backlightOn: " + t.getMessage());
		}
	}

	public static void backlightOff() {
		try {
			if (DeviceFactory.getDevice() != null && DeviceFactory.getDevice().getDeviceDisplay() != null) {
				DeviceFactory.getDevice().getDeviceDisplay().flashBacklight(0);
			}
		} catch (Throwable t) {
			Logger.debug("Motorola Lighting.backlightOff: " + t.getMessage());
		}
	}
}

