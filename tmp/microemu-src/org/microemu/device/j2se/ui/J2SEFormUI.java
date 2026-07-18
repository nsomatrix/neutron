/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se.ui;

import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import org.microemu.device.impl.ui.DisplayableImplUI;
import org.microemu.device.ui.FormUI;

public class J2SEFormUI
extends DisplayableImplUI
implements FormUI {
    public J2SEFormUI(Form form) {
        super(form);
    }

    public int append(Image img) {
        return 0;
    }

    public int append(Item item) {
        return 0;
    }

    public int append(String str) {
        return 0;
    }

    public void delete(int itemNum) {
    }

    public void deleteAll() {
    }

    public void insert(int itemNum, Item item) {
    }

    public void set(int itemNum, Item item) {
    }
}

