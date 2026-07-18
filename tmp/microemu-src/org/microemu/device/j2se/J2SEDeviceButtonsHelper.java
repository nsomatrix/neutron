/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.ButtonName;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.SoftButton;
import org.microemu.device.j2se.J2SEButton;
import org.microemu.log.Logger;

public class J2SEDeviceButtonsHelper {
    private static Map devices = new WeakHashMap();

    public static SoftButton getSoftButton(MouseEvent ev) {
        Iterator it = DeviceFactory.getDevice().getSoftButtons().iterator();
        while (it.hasNext()) {
            Rectangle pb;
            SoftButton button = (SoftButton)it.next();
            if (!button.isVisible() || (pb = button.getPaintable()) == null || !pb.contains(ev.getX(), ev.getY())) continue;
            return button;
        }
        return null;
    }

    public static J2SEButton getSkinButton(MouseEvent ev) {
        Enumeration en = DeviceFactory.getDevice().getButtons().elements();
        while (en.hasMoreElements()) {
            J2SEButton button = (J2SEButton)en.nextElement();
            if (button.getShape() == null || !button.getShape().contains(ev.getX(), ev.getY())) continue;
            return button;
        }
        return null;
    }

    public static J2SEButton getButton(KeyEvent ev) {
        DeviceInformation inf = J2SEDeviceButtonsHelper.getDeviceInformation();
        J2SEButton button = (J2SEButton)inf.keyboardCharCodes.get(new Integer(ev.getKeyChar()));
        if (button != null) {
            return button;
        }
        return (J2SEButton)inf.keyboardKeyCodes.get(new Integer(ev.getKeyCode()));
    }

    public static J2SEButton getButton(ButtonName functionalName) {
        DeviceInformation inf = J2SEDeviceButtonsHelper.getDeviceInformation();
        return (J2SEButton)inf.functions.get(functionalName);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static DeviceInformation getDeviceInformation() {
        DeviceInformation inf;
        Device dev = DeviceFactory.getDevice();
        Class clazz = J2SEDeviceButtonsHelper.class;
        synchronized (clazz) {
            inf = (DeviceInformation)devices.get(dev);
            if (inf == null) {
                inf = J2SEDeviceButtonsHelper.createDeviceInformation(dev);
            }
        }
        return inf;
    }

    private static DeviceInformation createDeviceInformation(Device dev) {
        DeviceInformation inf = new DeviceInformation();
        boolean hasModeChange = false;
        Enumeration en = dev.getButtons().elements();
        while (en.hasMoreElements()) {
            J2SEButton button = (J2SEButton)en.nextElement();
            int[] keyCodes = button.getKeyboardKeyCodes();
            for (int i = 0; i < keyCodes.length; ++i) {
                inf.keyboardKeyCodes.put(new Integer(keyCodes[i]), button);
            }
            char[] charCodes = button.getKeyboardCharCodes();
            for (int i = 0; i < charCodes.length; ++i) {
                inf.keyboardCharCodes.put(new Integer(charCodes[i]), button);
            }
            inf.functions.put(button.getFunctionalName(), button);
            if (!button.isModeChange()) continue;
            hasModeChange = true;
        }
        if (!hasModeChange) {
            J2SEButton button = (J2SEButton)inf.functions.get(ButtonName.KEY_POUND);
            if (button != null) {
                button.setModeChange();
            } else {
                Logger.warn("Device has no ModeChange and POUND buttons");
            }
        }
        if (inf.functions.get(ButtonName.DELETE) == null) {
            dev.getButtons().add(new J2SEButton(ButtonName.DELETE));
        }
        if (inf.functions.get(ButtonName.BACK_SPACE) == null) {
            dev.getButtons().add(new J2SEButton(ButtonName.BACK_SPACE));
        }
        return inf;
    }

    private static class DeviceInformation {
        Map keyboardKeyCodes = new HashMap();
        Map keyboardCharCodes = new HashMap();
        Map functions = new HashMap();

        private DeviceInformation() {
        }
    }
}

