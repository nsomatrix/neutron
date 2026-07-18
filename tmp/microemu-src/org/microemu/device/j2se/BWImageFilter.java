/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import java.awt.image.RGBImageFilter;
import org.microemu.device.DeviceFactory;
import org.microemu.device.j2se.J2SEDeviceDisplay;

public class BWImageFilter
extends RGBImageFilter {
    private double Yr;
    private double Yg;
    private double Yb;

    public BWImageFilter() {
        this(0.2126, 0.7152, 0.0722);
    }

    public BWImageFilter(double Yr, double Yg, double Yb) {
        this.Yr = Yr;
        this.Yg = Yg;
        this.Yb = Yb;
        this.canFilterIndexColorModel = true;
    }

    public int filterRGB(int x, int y, int rgb) {
        int a = rgb & 0xFF000000;
        int r = (rgb & 0xFF0000) >>> 16;
        int g = (rgb & 0xFF00) >>> 8;
        int b = rgb & 0xFF;
        int Y = (int)(this.Yr * (double)r + this.Yg * (double)g + this.Yb * (double)b);
        if (Y > 127) {
            return a | ((J2SEDeviceDisplay)DeviceFactory.getDevice().getDeviceDisplay()).getBackgroundColor().getRGB();
        }
        return a | ((J2SEDeviceDisplay)DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor().getRGB();
    }
}

