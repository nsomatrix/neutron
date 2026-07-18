/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se.ui;

import javax.microedition.lcdui.TextBox;
import org.microemu.device.impl.ui.DisplayableImplUI;
import org.microemu.device.ui.TextBoxUI;

public class J2SETextBoxUI
extends DisplayableImplUI
implements TextBoxUI {
    private String text;

    public J2SETextBoxUI(TextBox textBox) {
        super(textBox);
    }

    public int getCaretPosition() {
        return -1;
    }

    public String getString() {
        return this.text;
    }

    public void setString(String text) {
        this.text = text;
    }
}

