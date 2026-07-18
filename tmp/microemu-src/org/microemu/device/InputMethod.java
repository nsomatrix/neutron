/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device;

import org.microemu.device.InputMethodListener;

public abstract class InputMethod {
    public static final int INPUT_NONE = 0;
    public static final int INPUT_123 = 1;
    public static final int INPUT_ABC_UPPER = 2;
    public static final int INPUT_ABC_LOWER = 3;
    static InputMethod inputMethod = null;
    int inputMode = 0;
    protected InputMethodListener inputMethodListener = null;
    protected int maxSize;

    public abstract void dispose();

    public abstract int getGameAction(int var1);

    public abstract int getKeyCode(int var1);

    public abstract String getKeyName(int var1) throws IllegalArgumentException;

    public void removeInputMethodListener(InputMethodListener l) {
        if (l == this.inputMethodListener) {
            this.inputMethodListener = null;
            this.setInputMode(0);
        }
    }

    public void setInputMethodListener(InputMethodListener l) {
        this.inputMethodListener = l;
        switch (l.getConstraints() & 0xFFFF) {
            case 0: 
            case 1: 
            case 4: {
                this.setInputMode(3);
                break;
            }
            case 2: 
            case 3: 
            case 5: {
                this.setInputMode(1);
            }
        }
    }

    public int getInputMode() {
        return this.inputMode;
    }

    public void setInputMode(int mode) {
        this.inputMode = mode;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    public static boolean validate(String text, int constraints) {
        switch (constraints & 0xFFFF) {
            case 0: {
                break;
            }
            case 1: {
                break;
            }
            case 2: {
                if (text == null || text.length() <= 0 || text.equals("-")) break;
                try {
                    Integer.parseInt(text);
                    break;
                }
                catch (NumberFormatException e) {
                    return false;
                }
            }
            case 3: {
                break;
            }
            case 4: {
                break;
            }
            case 5: {
                if (text == null || text.length() <= 0 || text.equals("-")) break;
                try {
                    Double.valueOf(text);
                    break;
                }
                catch (NumberFormatException e) {
                    return false;
                }
            }
        }
        return true;
    }
}

