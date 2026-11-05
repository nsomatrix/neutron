/*
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package com.sonyericsson.util;

import org.neutron.device.DeviceFactory;
import org.neutron.log.Logger;

public class Vibration {

	public static void start(int duration, int intensity) {
		try {
			if (DeviceFactory.getDevice() != null) {
				DeviceFactory.getDevice().vibrate(duration);
			}
		} catch (Throwable t) {
			Logger.debug("SonyEricsson Vibration.start: " + t.getMessage());
		}
	}

	public static void stop() {
		try {
			if (DeviceFactory.getDevice() != null) {
				DeviceFactory.getDevice().vibrate(0);
			}
		} catch (Throwable t) {
			Logger.debug("SonyEricsson Vibration.stop: " + t.getMessage());
		}
	}
}

