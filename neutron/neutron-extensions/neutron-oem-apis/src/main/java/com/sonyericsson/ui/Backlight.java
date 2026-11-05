/*
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package com.sonyericsson.ui;

import org.neutron.device.DeviceFactory;
import org.neutron.log.Logger;

public class Backlight {

	public static void on() {
		try {
			if (DeviceFactory.getDevice() != null && DeviceFactory.getDevice().getDeviceDisplay() != null) {
				DeviceFactory.getDevice().getDeviceDisplay().flashBacklight(Integer.MAX_VALUE);
			}
		} catch (Throwable t) {
			Logger.debug("SonyEricsson Backlight.on: " + t.getMessage());
		}
	}

	public static void off() {
		try {
			if (DeviceFactory.getDevice() != null && DeviceFactory.getDevice().getDeviceDisplay() != null) {
				DeviceFactory.getDevice().getDeviceDisplay().flashBacklight(0);
			}
		} catch (Throwable t) {
			Logger.debug("SonyEricsson Backlight.off: " + t.getMessage());
		}
	}

	public static void blink(int duration, int period) {
		try {
			if (DeviceFactory.getDevice() != null && DeviceFactory.getDevice().getDeviceDisplay() != null) {
				DeviceFactory.getDevice().getDeviceDisplay().flashBacklight(duration);
			}
		} catch (Throwable t) {
			Logger.debug("SonyEricsson Backlight.blink: " + t.getMessage());
		}
	}
}

