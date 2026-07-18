/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.ui;

import org.microemu.device.ui.DisplayableUI;

public interface TextBoxUI
extends DisplayableUI {
    public int getCaretPosition();

    public String getString();

    public void setString(String var1);
}

