/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se.ui;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;
import org.microemu.device.impl.ui.DisplayableImplUI;
import org.microemu.device.ui.ListUI;

public class J2SEListUI
extends DisplayableImplUI
implements ListUI {
    public J2SEListUI(List list) {
        super(list);
    }

    public int append(String stringPart, Image imagePart) {
        return -1;
    }

    public void setSelectCommand(Command command) {
    }

    public int getSelectedIndex() {
        return 0;
    }

    public String getString(int elementNum) {
        return null;
    }
}

