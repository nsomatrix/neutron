/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import java.awt.event.KeyEvent;
import java.util.Hashtable;
import java.util.StringTokenizer;
import org.microemu.device.impl.Button;
import org.microemu.device.impl.ButtonDetaultDeviceKeyCodes;
import org.microemu.device.impl.ButtonName;
import org.microemu.device.impl.Shape;
import org.microemu.device.j2se.J2SEButtonDefaultKeyCodes;

public class J2SEButton
implements Button {
    private String name;
    private ButtonName functionalName;
    private Shape shape;
    private int[] keyboardKeys;
    private String keyboardCharCodes;
    private int keyCode;
    private Hashtable inputToChars;
    private boolean modeChange;

    J2SEButton(ButtonName functionalName) {
        this(20002, functionalName.getName(), null, Integer.MIN_VALUE, null, null, null, false);
    }

    public J2SEButton(int skinVersion, String name, Shape shape, int keyCode, String keyboardKeys, String keyboardChars, Hashtable inputToChars, boolean modeChange) {
        this.name = name;
        this.shape = shape;
        if (skinVersion >= 20002) {
            this.functionalName = ButtonName.getButtonName(name);
        } else {
            this.functionalName = J2SEButtonDefaultKeyCodes.getBackwardCompatibleName(J2SEButton.parseKeyboardKey(keyboardKeys));
            if (this.functionalName == null) {
                this.functionalName = ButtonName.getButtonName(name);
            }
        }
        this.modeChange = skinVersion >= 20002 ? modeChange : this.functionalName == ButtonName.KEY_POUND;
        this.keyCode = keyCode == Integer.MIN_VALUE ? ButtonDetaultDeviceKeyCodes.getKeyCode(this.functionalName) : keyCode;
        if (keyboardKeys != null) {
            StringTokenizer st = new StringTokenizer(keyboardKeys, " ");
            while (st.hasMoreTokens()) {
                int key = J2SEButton.parseKeyboardKey(st.nextToken());
                if (key == -1) continue;
                if (this.keyboardKeys == null) {
                    this.keyboardKeys = new int[1];
                } else {
                    int[] newKeyboardKeys = new int[this.keyboardKeys.length + 1];
                    System.arraycopy(this.keyboardKeys, 0, newKeyboardKeys, 0, this.keyboardKeys.length);
                    this.keyboardKeys = newKeyboardKeys;
                }
                this.keyboardKeys[this.keyboardKeys.length - 1] = key;
            }
        }
        if (this.keyboardKeys == null || this.keyboardKeys.length == 0) {
            this.keyboardKeys = J2SEButtonDefaultKeyCodes.getKeyCodes(this.functionalName);
        }
        this.keyboardCharCodes = keyboardChars != null ? keyboardChars : J2SEButtonDefaultKeyCodes.getCharCodes(this.functionalName);
        this.inputToChars = inputToChars;
    }

    public int getKeyboardKey() {
        if (this.keyboardKeys.length == 0) {
            return 0;
        }
        return this.keyboardKeys[0];
    }

    public int getKeyCode() {
        return this.keyCode;
    }

    public ButtonName getFunctionalName() {
        return this.functionalName;
    }

    public int[] getKeyboardKeyCodes() {
        return this.keyboardKeys;
    }

    public char[] getKeyboardCharCodes() {
        if (this.keyboardCharCodes == null) {
            return new char[0];
        }
        return this.keyboardCharCodes.toCharArray();
    }

    public boolean isModeChange() {
        return this.modeChange;
    }

    void setModeChange() {
        this.modeChange = true;
    }

    public char[] getChars(int inputMode) {
        char[] result = null;
        switch (inputMode) {
            case 1: {
                result = (char[])this.inputToChars.get("123");
                break;
            }
            case 3: {
                result = (char[])this.inputToChars.get("abc");
                break;
            }
            case 2: {
                result = (char[])this.inputToChars.get("ABC");
            }
        }
        if (result == null) {
            result = (char[])this.inputToChars.get("common");
        }
        if (result == null) {
            result = new char[]{};
        }
        return result;
    }

    public boolean isChar(char c, int inputMode) {
        if (this.inputToChars == null) {
            return false;
        }
        c = Character.toLowerCase(c);
        char[] chars = this.getChars(inputMode);
        if (chars != null) {
            for (int i = 0; i < chars.length; ++i) {
                if (c != Character.toLowerCase(chars[i])) continue;
                return true;
            }
        }
        return false;
    }

    public String getName() {
        return this.name;
    }

    public Shape getShape() {
        return this.shape;
    }

    private static int parseKeyboardKey(String keyName) {
        int key;
        try {
            key = KeyEvent.class.getField(keyName).getInt(null);
        }
        catch (Exception e) {
            try {
                key = Integer.parseInt(keyName);
            }
            catch (NumberFormatException e1) {
                key = -1;
            }
        }
        return key;
    }
}

