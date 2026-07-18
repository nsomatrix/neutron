/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import java.util.HashMap;
import java.util.Map;
import org.microemu.device.impl.ButtonName;

public class J2SEButtonDefaultKeyCodes {
    private static Map codes = new HashMap();
    private static Map backwardCompatibleNames = new HashMap();

    public static int[] getKeyCodes(ButtonName name) {
        KeyInformation info = (KeyInformation)codes.get(name);
        if (info == null) {
            return new int[0];
        }
        return info.keyCodes;
    }

    public static String getCharCodes(ButtonName name) {
        KeyInformation info = (KeyInformation)codes.get(name);
        if (info == null) {
            return "";
        }
        return info.charCodes;
    }

    private static KeyInformation code(ButtonName name, int code) {
        KeyInformation info = new KeyInformation();
        info.keyCodes = new int[]{code};
        codes.put(name, info);
        backwardCompatibleNames.put(new Integer(code), name);
        return info;
    }

    private static KeyInformation code(ButtonName name, int code1, int code2) {
        KeyInformation info = new KeyInformation();
        info.keyCodes = new int[]{code1, code2};
        codes.put(name, info);
        backwardCompatibleNames.put(new Integer(code1), name);
        return info;
    }

    public static ButtonName getBackwardCompatibleName(int keyboardKey) {
        return (ButtonName)backwardCompatibleNames.get(new Integer(keyboardKey));
    }

    static {
        J2SEButtonDefaultKeyCodes.code(ButtonName.SOFT1, 112);
        J2SEButtonDefaultKeyCodes.code(ButtonName.SOFT2, 113);
        J2SEButtonDefaultKeyCodes.code(ButtonName.SELECT, 10);
        J2SEButtonDefaultKeyCodes.code(ButtonName.UP, 38, 224);
        J2SEButtonDefaultKeyCodes.code(ButtonName.DOWN, 40, 225);
        J2SEButtonDefaultKeyCodes.code(ButtonName.LEFT, 37, 226);
        J2SEButtonDefaultKeyCodes.code(ButtonName.RIGHT, 39, 227);
        J2SEButtonDefaultKeyCodes.code(ButtonName.BACK_SPACE, 8);
        J2SEButtonDefaultKeyCodes.code(ButtonName.DELETE, 12, 127);
        J2SEButtonDefaultKeyCodes.code((ButtonName)ButtonName.KEY_NUM0, (int)48, (int)96).charCodes = "0";
        J2SEButtonDefaultKeyCodes.code((ButtonName)ButtonName.KEY_NUM1, (int)49, (int)97).charCodes = "1";
        J2SEButtonDefaultKeyCodes.code((ButtonName)ButtonName.KEY_NUM2, (int)50, (int)98).charCodes = "2";
        J2SEButtonDefaultKeyCodes.code((ButtonName)ButtonName.KEY_NUM3, (int)51, (int)99).charCodes = "3";
        J2SEButtonDefaultKeyCodes.code((ButtonName)ButtonName.KEY_NUM4, (int)52, (int)100).charCodes = "4";
        J2SEButtonDefaultKeyCodes.code((ButtonName)ButtonName.KEY_NUM5, (int)53, (int)101).charCodes = "5";
        J2SEButtonDefaultKeyCodes.code((ButtonName)ButtonName.KEY_NUM6, (int)54, (int)102).charCodes = "6";
        J2SEButtonDefaultKeyCodes.code((ButtonName)ButtonName.KEY_NUM7, (int)55, (int)103).charCodes = "7";
        J2SEButtonDefaultKeyCodes.code((ButtonName)ButtonName.KEY_NUM8, (int)56, (int)104).charCodes = "8";
        J2SEButtonDefaultKeyCodes.code((ButtonName)ButtonName.KEY_NUM9, (int)57, (int)105).charCodes = "9";
        J2SEButtonDefaultKeyCodes.code((ButtonName)ButtonName.KEY_STAR, (int)106, (int)151).charCodes = "*";
        J2SEButtonDefaultKeyCodes.code((ButtonName)ButtonName.KEY_POUND, (int)31, (int)109).charCodes = "#";
    }

    private static class KeyInformation {
        int[] keyCodes;
        String charCodes = "";

        private KeyInformation() {
        }
    }
}

