/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl;

public interface Font {
    public int charWidth(char var1);

    public int charsWidth(char[] var1, int var2, int var3);

    public int getBaselinePosition();

    public int getHeight();

    public int stringWidth(String var1);
}

