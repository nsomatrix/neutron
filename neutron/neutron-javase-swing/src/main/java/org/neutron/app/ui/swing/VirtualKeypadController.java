/**
 *  Neutron
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 */
package org.neutron.app.ui.swing;

import java.util.Enumeration;
import javax.microedition.lcdui.Command;
import org.neutron.DisplayAccess;
import org.neutron.MIDletAccess;
import org.neutron.MIDletBridge;
import org.neutron.app.util.SleepManager;
import org.neutron.device.Device;
import org.neutron.device.DeviceFactory;
import org.neutron.device.impl.ButtonDetaultDeviceKeyCodes;
import org.neutron.device.impl.ButtonName;
import org.neutron.device.impl.SoftButton;
import org.neutron.device.impl.ui.CommandManager;
import org.neutron.device.j2se.J2SEButton;
import org.neutron.device.j2se.J2SEDeviceButtonsHelper;
import org.neutron.device.j2se.J2SEInputMethod;
import org.neutron.device.j2se.J2SESoftButton;

/**
 * Controller bridging virtual button interactions with the active MIDlet/game.
 */
public class VirtualKeypadController {

	public static void pressButton(ButtonName buttonName) {
		SleepManager.notifyActivity();
		if (MIDletBridge.getCurrentMIDlet() == null) {
			return;
		}

		Device dev = DeviceFactory.getDevice();
		if (dev == null) {
			return;
		}

		// Handle softbuttons with associated commands in non-fullscreen UI
		if (buttonName == ButtonName.SOFT1 || buttonName == ButtonName.SOFT2) {
			boolean fullScreen = dev.getDeviceDisplay().isFullScreenMode();
			if (!fullScreen) {
				SoftButton sb = findSoftButton(buttonName);
				if (sb != null && sb.getCommand() != null) {
					MIDletAccess ma = MIDletBridge.getMIDletAccess();
					if (ma != null) {
						DisplayAccess da = ma.getDisplayAccess();
						if (da != null) {
							Command cmd = sb.getCommand();
							if (cmd.equals(CommandManager.CMD_MENU)) {
								CommandManager.getInstance().commandAction(cmd);
							} else {
								da.commandAction(cmd, da.getCurrent());
							}
							return;
						}
					}
				}
			}
		}

		J2SEInputMethod inputMethod = (J2SEInputMethod) dev.getInputMethod();
		J2SEButton button = findButton(buttonName);
		if (button != null) {
			inputMethod.buttonPressed(button, '\0');
		} else {
			int keyCode = ButtonDetaultDeviceKeyCodes.getKeyCode(buttonName);
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma != null && ma.getDisplayAccess() != null) {
				ma.getDisplayAccess().keyPressed(keyCode);
			}
		}
	}

	public static void releaseButton(ButtonName buttonName) {
		if (MIDletBridge.getCurrentMIDlet() == null) {
			return;
		}

		Device dev = DeviceFactory.getDevice();
		if (dev == null) {
			return;
		}

		J2SEInputMethod inputMethod = (J2SEInputMethod) dev.getInputMethod();
		J2SEButton button = findButton(buttonName);
		if (button != null) {
			inputMethod.buttonReleased(button, '\0');
		} else {
			int keyCode = ButtonDetaultDeviceKeyCodes.getKeyCode(buttonName);
			MIDletAccess ma = MIDletBridge.getMIDletAccess();
			if (ma != null && ma.getDisplayAccess() != null) {
				ma.getDisplayAccess().keyReleased(keyCode);
			}
		}
	}

	public static void repeatButton(ButtonName buttonName) {
		if (MIDletBridge.getCurrentMIDlet() == null) {
			return;
		}

		Device dev = DeviceFactory.getDevice();
		if (dev == null || !dev.hasRepeatEvents()) {
			return;
		}

		J2SEButton button = findButton(buttonName);
		int keyCode = (button != null) ? button.getKeyCode() : ButtonDetaultDeviceKeyCodes.getKeyCode(buttonName);

		MIDletAccess ma = MIDletBridge.getMIDletAccess();
		if (ma != null && ma.getDisplayAccess() != null) {
			ma.getDisplayAccess().keyRepeated(keyCode);
		}
	}

	public static J2SEButton findButton(ButtonName name) {
		Device dev = DeviceFactory.getDevice();
		if (dev == null) {
			return null;
		}

		J2SEButton b = J2SEDeviceButtonsHelper.getButton(name);
		if (b != null) {
			return b;
		}

		if (dev.getButtons() != null) {
			for (Enumeration e = dev.getButtons().elements(); e.hasMoreElements();) {
				J2SEButton btn = (J2SEButton) e.nextElement();
				if (btn.getFunctionalName() == name) {
					return btn;
				}
			}
		}

		if (name == ButtonName.SOFT1 || name == ButtonName.SOFT2) {
			SoftButton sb = findSoftButton(name);
			if (sb instanceof J2SEButton) {
				return (J2SEButton) sb;
			}
		}

		return null;
	}

	private static SoftButton findSoftButton(ButtonName buttonName) {
		Device dev = DeviceFactory.getDevice();
		if (dev == null || dev.getSoftButtons() == null) {
			return null;
		}
		for (Enumeration e = dev.getSoftButtons().elements(); e.hasMoreElements();) {
			Object obj = e.nextElement();
			if (obj instanceof SoftButton) {
				SoftButton sb = (SoftButton) obj;
				String sName = sb.getName();
				if (sName != null) {
					if (buttonName == ButtonName.SOFT1 && sName.equalsIgnoreCase("SOFT1")) {
						return sb;
					}
					if (buttonName == ButtonName.SOFT2 && sName.equalsIgnoreCase("SOFT2")) {
						return sb;
					}
				}
			}
		}
		return null;
	}
}

