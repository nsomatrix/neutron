/*
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package com.motorola.multimedia;

import org.neutron.device.DeviceFactory;
import org.neutron.log.Logger;

public class Vibrator {

	public static void startVibrate() {
		try {
			if (DeviceFactory.getDevice() != null) {
				DeviceFactory.getDevice().vibrate(2000);
			}
		} catch (Throwable t) {
			Logger.debug("Motorola Vibrator.startVibrate: " + t.getMessage());
		}
	}

	public static void stopVibrate() {
		try {
			if (DeviceFactory.getDevice() != null) {
				DeviceFactory.getDevice().vibrate(0);
			}
		} catch (Throwable t) {
			Logger.debug("Motorola Vibrator.stopVibrate: " + t.getMessage());
		}
	}
}

