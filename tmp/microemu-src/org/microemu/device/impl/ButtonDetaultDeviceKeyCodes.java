/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.microemu.device.impl.ButtonName;

public abstract class ButtonDetaultDeviceKeyCodes {
    private static Map codes = new HashMap();
    private static Map gameActions = new HashMap();

    public static int getKeyCode(ButtonName name) {
        Integer code = (Integer)codes.get(name);
        if (code != null) {
            return code;
        }
        return 0;
    }

    public static int getGameAction(ButtonName name) {
        Integer code = (Integer)gameActions.get(name);
        if (code != null) {
            return code;
        }
        return 0;
    }

    public static ButtonName getButtonNameByGameAction(int gameAction) {
        Integer value = new Integer(gameAction);
        if (gameActions.containsValue(value)) {
            Iterator iterator = gameActions.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry v = iterator.next();
                if (!v.getValue().equals(value)) continue;
                return (ButtonName)v.getKey();
            }
        }
        throw new IllegalArgumentException("Illegal action " + gameAction);
    }

    private static void code(ButtonName name, int code) {
        codes.put(name, new Integer(code));
    }

    private static void code(ButtonName name, int code, int gameAction) {
        ButtonDetaultDeviceKeyCodes.code(name, code);
        gameActions.put(name, new Integer(gameAction));
    }

    static {
        ButtonDetaultDeviceKeyCodes.code(ButtonName.SOFT1, -6);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.SOFT2, -7);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.SELECT, -5, 8);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.UP, -1, 1);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.DOWN, -2, 6);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.LEFT, -3, 2);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.RIGHT, -4, 5);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.BACK_SPACE, -8);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.KEY_NUM0, 48);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.KEY_NUM1, 49, 9);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.KEY_NUM2, 50);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.KEY_NUM3, 51, 10);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.KEY_NUM4, 52);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.KEY_NUM5, 53);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.KEY_NUM6, 54);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.KEY_NUM7, 55, 11);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.KEY_NUM8, 56);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.KEY_NUM9, 57, 12);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.KEY_STAR, 42);
        ButtonDetaultDeviceKeyCodes.code(ButtonName.KEY_POUND, 35);
    }
}

