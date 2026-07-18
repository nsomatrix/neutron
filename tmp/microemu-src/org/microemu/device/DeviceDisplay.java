/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.lcdui.Image;
import org.microemu.device.MutableImage;

public interface DeviceDisplay {
    public MutableImage getDisplayImage();

    public int getWidth();

    public int getHeight();

    public int getFullWidth();

    public int getFullHeight();

    public boolean isColor();

    public boolean isFullScreenMode();

    public int numAlphaLevels();

    public int numColors();

    public void repaint(int var1, int var2, int var3, int var4);

    public void setScrollDown(boolean var1);

    public void setScrollUp(boolean var1);

    public Image createImage(int var1, int var2);

    public Image createImage(String var1) throws IOException;

    public Image createImage(Image var1);

    public Image createImage(byte[] var1, int var2, int var3);

    public Image createImage(InputStream var1) throws IOException;

    public Image createRGBImage(int[] var1, int var2, int var3, boolean var4);

    public Image createImage(Image var1, int var2, int var3, int var4, int var5, int var6);
}

