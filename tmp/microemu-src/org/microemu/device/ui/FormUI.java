/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.ui;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import org.microemu.device.ui.DisplayableUI;

public interface FormUI
extends DisplayableUI {
    public int append(Image var1);

    public int append(Item var1);

    public int append(String var1);

    public void delete(int var1);

    public void deleteAll();

    public void insert(int var1, Item var2);

    public void set(int var1, Item var2);
}

