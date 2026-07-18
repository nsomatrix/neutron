/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public final class ButtonName {
    private static Map altNames = new HashMap();
    public static final ButtonName SOFT1 = new ButtonName();
    public static final ButtonName SOFT2 = new ButtonName();
    public static final ButtonName SOFT3 = new ButtonName();
    public static final ButtonName SELECT = new ButtonName("SEL");
    public static final ButtonName UP = new ButtonName("U");
    public static final ButtonName DOWN = new ButtonName("D");
    public static final ButtonName LEFT = new ButtonName("L");
    public static final ButtonName RIGHT = new ButtonName("R");
    public static final ButtonName BACK_SPACE = new ButtonName();
    public static final ButtonName DELETE = new ButtonName();
    public static final ButtonName KEY_NUM0 = new ButtonName("0");
    public static final ButtonName KEY_NUM1 = new ButtonName("1");
    public static final ButtonName KEY_NUM2 = new ButtonName("2");
    public static final ButtonName KEY_NUM3 = new ButtonName("3");
    public static final ButtonName KEY_NUM4 = new ButtonName("4");
    public static final ButtonName KEY_NUM5 = new ButtonName("5");
    public static final ButtonName KEY_NUM6 = new ButtonName("6");
    public static final ButtonName KEY_NUM7 = new ButtonName("7");
    public static final ButtonName KEY_NUM8 = new ButtonName("8");
    public static final ButtonName KEY_NUM9 = new ButtonName("9");
    public static final ButtonName KEY_STAR = new ButtonName(new String[]{"*", "STAR", "ASTERISK"});
    public static final ButtonName KEY_POUND = new ButtonName(new String[]{"#", "POUND"});
    private String name = "n/a";

    public static ButtonName getButtonName(String functionName) {
        String name = functionName.toUpperCase();
        try {
            Field field = ButtonName.class.getField(name);
            if (field.getType() == ButtonName.class) {
                return (ButtonName)field.get(null);
            }
        }
        catch (NoSuchFieldException e) {
        }
        catch (IllegalAccessException e) {
            // empty catch block
        }
        ButtonName btn = (ButtonName)altNames.get(name);
        if (btn == null) {
            btn = new ButtonName();
            btn.name = functionName;
        }
        return btn;
    }

    private ButtonName() {
    }

    private ButtonName(String name) {
        altNames.put(name, this);
    }

    private ButtonName(String[] names) {
        for (int i = 0; i < names.length; ++i) {
            altNames.put(names[i], this);
        }
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name;
    }

    static {
        Field[] fields = ButtonName.class.getDeclaredFields();
        for (int i = 0; i < fields.length; ++i) {
            if (fields[i].getType() != (class$org$microemu$device$impl$ButtonName == null ? ButtonName.class$("org.microemu.device.impl.ButtonName") : class$org$microemu$device$impl$ButtonName)) continue;
            try {
                ((ButtonName)fields[i].get(null)).name = fields[i].getName();
                continue;
            }
            catch (IllegalAccessException illegalAccessException) {
                // empty catch block
            }
        }
    }
}

