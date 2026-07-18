/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device;

import org.microemu.device.InputMethodEvent;

public interface InputMethodListener {
    public void caretPositionChanged(InputMethodEvent var1);

    public void inputMethodTextChanged(InputMethodEvent var1);

    public int getCaretPosition();

    public String getText();

    public int getConstraints();
}

