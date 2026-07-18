/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import org.microemu.device.DeviceFactory;
import org.microemu.device.impl.Color;
import org.microemu.device.j2se.J2SEDeviceDisplay;

public class RGBImageFilter
extends java.awt.image.RGBImageFilter {
    private double Rr;
    private double Rg;
    private double Rb;
    private Color backgroundColor;
    private Color foregroundColor;

    public RGBImageFilter() {
        this.canFilterIndexColorModel = true;
        this.backgroundColor = ((J2SEDeviceDisplay)DeviceFactory.getDevice().getDeviceDisplay()).getBackgroundColor();
        this.foregroundColor = ((J2SEDeviceDisplay)DeviceFactory.getDevice().getDeviceDisplay()).getForegroundColor();
        this.Rr = this.foregroundColor.getRed() - this.backgroundColor.getRed();
        this.Rg = this.foregroundColor.getGreen() - this.backgroundColor.getGreen();
        this.Rb = this.foregroundColor.getBlue() - this.backgroundColor.getBlue();
    }

    public int filterRGB(int x, int y, int rgb) {
        int a = rgb & 0xFF000000;
        int r = (rgb & 0xFF0000) >>> 16;
        int g = (rgb & 0xFF00) >>> 8;
        int b = rgb & 0xFF;
        r = this.Rr > 0.0 ? (int)((double)r * this.Rr) / 255 + this.backgroundColor.getRed() : (int)((double)r * -this.Rr) / 255 + this.foregroundColor.getRed();
        g = this.Rr > 0.0 ? (int)((double)g * this.Rg) / 255 + this.backgroundColor.getGreen() : (int)((double)g * -this.Rg) / 255 + this.foregroundColor.getGreen();
        b = this.Rr > 0.0 ? (int)((double)b * this.Rb) / 255 + this.backgroundColor.getBlue() : (int)((double)b * -this.Rb) / 255 + this.foregroundColor.getBlue();
        return a | r << 16 | g << 8 | b;
    }
}

