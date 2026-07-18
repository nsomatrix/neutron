/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl;

import java.net.URL;
import org.microemu.device.FontManager;
import org.microemu.device.impl.Font;

public interface FontManagerImpl
extends FontManager {
    public void setAntialiasing(boolean var1);

    public void setFont(String var1, String var2, String var3, Font var4);

    public Font createSystemFont(String var1, String var2, int var3, boolean var4);

    public Font createTrueTypeFont(URL var1, String var2, int var3, boolean var4);
}

