/*
 * Decompiled with CFR 0.152.
 */
package org.microemu;

import org.microemu.app.ui.DisplayRepaintListener;
import org.microemu.device.MutableImage;

public interface DisplayComponent {
    public void addDisplayRepaintListener(DisplayRepaintListener var1);

    public void removeDisplayRepaintListener(DisplayRepaintListener var1);

    public MutableImage getDisplayImage();

    public void repaintRequest(int var1, int var2, int var3, int var4);
}

