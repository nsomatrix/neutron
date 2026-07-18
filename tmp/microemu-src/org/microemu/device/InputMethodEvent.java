/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device;

public class InputMethodEvent {
    public static final int CARET_POSITION_CHANGED = 1;
    public static final int INPUT_METHOD_TEXT_CHANGED = 2;
    int type;
    int caret;
    String text;

    public InputMethodEvent(int type, int caret, String text) {
        this.type = type;
        this.caret = caret;
        this.text = text;
    }

    public int getCaret() {
        return this.caret;
    }

    public String getText() {
        return this.text;
    }
}

