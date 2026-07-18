/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import java.awt.image.RGBImageFilter;
import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.Color;
import org.microemu.device.j2se.J2SEDeviceDisplay;

public class GrayImageFilter
extends RGBImageFilter {
    private double Yr;
    private double Yg;
    private double Yb;
    private double Rr;
    private double Rg;
    private double Rb;

    public GrayImageFilter() {
        this(0.2126, 0.7152, 0.0722);
    }

    public GrayImageFilter(double Yr, double Yg, double Yb) {
        this.Yr = Yr;
        this.Yg = Yg;
        this.Yb = Yb;
        this.canFilterIndexColorModel = true;
        Color backgroundColor = ((J2SEDeviceDisplay)DeviceFactory.getDevice().getDeviceDisplay()).getBackgroundColor();
        Color foregroundColor = ((J2SEDeviceDisplay)DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor();
        this.Rr = (double)(backgroundColor.getRed() - foregroundColor.getRed()) / 256.0;
        this.Rg = (double)(backgroundColor.getGreen() - foregroundColor.getGreen()) / 256.0;
        this.Rb = (double)(backgroundColor.getBlue() - foregroundColor.getBlue()) / 256.0;
    }

    public int filterRGB(int x, int y, int rgb) {
        int a = rgb & 0xFF000000;
        int r = (rgb & 0xFF0000) >>> 16;
        int g = (rgb & 0xFF00) >>> 8;
        int b = rgb & 0xFF;
        int Y = (int)(this.Yr * (double)r + this.Yg * (double)g + this.Yb * (double)b) % 256;
        if (Y > 255) {
            Y = 255;
        }
        Color foregroundColor = ((J2SEDeviceDisplay)DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor();
        r = (int)(this.Rr * (double)Y) + foregroundColor.getRed();
        g = (int)(this.Rg * (double)Y) + foregroundColor.getGreen();
        b = (int)(this.Rb * (double)Y) + foregroundColor.getBlue();
        return a | r << 16 | g << 8 | b;
    }
}

