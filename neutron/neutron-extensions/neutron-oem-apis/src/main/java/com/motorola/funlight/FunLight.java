/*
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */

package com.motorola.funlight;

import org.neutron.device.DeviceFactory;
import org.neutron.log.Logger;

public class FunLight {

	public static final int OFF = 0;
	public static final int ON = 1;
	public static final int BLINK = 2;

	public static int[] getRegions() {
		return new int[] { 1 };
	}

	public static int getControl(int region) {
		return ON;
	}

	public static int getColor(int region) {
		return 0xFFFFFF;
	}

	public static void setColor(int region, int rgb) {
		// Mock keypad/backlight color effect
	}

	public static void setControl(int region, int control) {
		try {
			if (DeviceFactory.getDevice() != null && DeviceFactory.getDevice().getDeviceDisplay() != null) {
				DeviceFactory.getDevice().getDeviceDisplay().flashBacklight(control == OFF ? 0 : 5000);
			}
		} catch (Throwable t) {
			Logger.debug("FunLight.setControl: " + t.getMessage());
		}
	}

	public static void setControl(int region, int control, int param) {
		setControl(region, control);
	}
}

