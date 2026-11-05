/*
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package com.samsung.util;

import org.neutron.device.DeviceFactory;
import org.neutron.log.Logger;

public class Vibration {

	public static boolean isSupported() {
		return true;
	}

	public static void start(int duration, int strength) {
		try {
			if (DeviceFactory.getDevice() != null) {
				DeviceFactory.getDevice().vibrate(duration);
			}
		} catch (Throwable t) {
			Logger.debug("Samsung Vibration.start: " + t.getMessage());
		}
	}

	public static void stop() {
		try {
			if (DeviceFactory.getDevice() != null) {
				DeviceFactory.getDevice().vibrate(0);
			}
		} catch (Throwable t) {
			Logger.debug("Samsung Vibration.stop: " + t.getMessage());
		}
	}
}

