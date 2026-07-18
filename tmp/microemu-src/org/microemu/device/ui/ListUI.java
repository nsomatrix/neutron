/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import org.microemu.device.ui.DisplayableUI;

public interface ListUI
extends DisplayableUI {
    public int append(String var1, Image var2);

    public int getSelectedIndex();

    public String getString(int var1);

    public void setSelectCommand(Command var1);
}

