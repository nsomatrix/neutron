/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;
import org.microemu.device.DeviceDisplay;
import org.microemu.device.impl.Button;
import org.microemu.device.impl.Color;
import org.microemu.device.impl.PositionedImage;
import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.Shape;
import org.microemu.device.impl.SoftButton;

public interface DeviceDisplayImpl
extends DeviceDisplay {
    public Image createSystemImage(URL var1) throws IOException;

    public Button createButton(int var1, String var2, Shape var3, int var4, String var5, String var6, Hashtable var7, boolean var8);

    public SoftButton createSoftButton(int var1, String var2, Shape var3, int var4, String var5, Rectangle var6, String var7, Vector var8, Font var9);

    public SoftButton createSoftButton(int var1, String var2, Rectangle var3, Image var4, Image var5);

    public void setNumColors(int var1);

    public void setIsColor(boolean var1);

    public void setNumAlphaLevels(int var1);

    public void setBackgroundColor(Color var1);

    public void setForegroundColor(Color var1);

    public void setDisplayRectangle(Rectangle var1);

    public void setDisplayPaintable(Rectangle var1);

    public void setMode123Image(PositionedImage var1);

    public void setModeAbcLowerImage(PositionedImage var1);

    public void setModeAbcUpperImage(PositionedImage var1);

    public boolean isResizable();

    public void setResizable(boolean var1);
}

