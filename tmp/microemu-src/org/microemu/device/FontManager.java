/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device;

import javax.microedition.lcdui.Font;

public interface FontManager {
    public void init();

    public int charWidth(Font var1, char var2);

    public int charsWidth(Font var1, char[] var2, int var3, int var4);

    public int getBaselinePosition(Font var1);

    public int getHeight(Font var1);

    public int stringWidth(Font var1, String var2);
}

