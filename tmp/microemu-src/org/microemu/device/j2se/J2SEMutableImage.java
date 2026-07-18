/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import javax.microedition.lcdui.Graphics;
import org.microemu.device.MutableImage;
import org.microemu.device.j2se.J2SEDisplayGraphics;
import org.microemu.log.Logger;

public class J2SEMutableImage
extends MutableImage {
    private BufferedImage img;
    private PixelGrabber grabber = null;
    private int[] pixels;

    public J2SEMutableImage(int width, int height) {
        this.img = new BufferedImage(width, height, 1);
        java.awt.Graphics g = this.img.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
    }

    public Graphics getGraphics() {
        Graphics2D g = (Graphics2D)this.img.getGraphics();
        g.setClip(0, 0, this.getWidth(), this.getHeight());
        J2SEDisplayGraphics displayGraphics = new J2SEDisplayGraphics(g, this);
        displayGraphics.setColor(0);
        displayGraphics.translate(-displayGraphics.getTranslateX(), -displayGraphics.getTranslateY());
        return displayGraphics;
    }

    public boolean isMutable() {
        return true;
    }

    public int getHeight() {
        return this.img.getHeight();
    }

    public Image getImage() {
        return this.img;
    }

    public int getWidth() {
        return this.img.getWidth();
    }

    public int[] getData() {
        if (this.grabber == null) {
            this.pixels = new int[this.getWidth() * this.getHeight()];
            this.grabber = new PixelGrabber(this.img, 0, 0, this.getWidth(), this.getHeight(), this.pixels, 0, this.getWidth());
        }
        try {
            this.grabber.grabPixels();
        }
        catch (InterruptedException e) {
            Logger.error(e);
        }
        return this.pixels;
    }

    public MutableImage scale(int zoom) {
        BufferedImage scaledImg = new BufferedImage(this.img.getWidth() * zoom, this.img.getHeight() * zoom, this.img.getType());
        Graphics2D imgGraphics = scaledImg.createGraphics();
        imgGraphics.scale(zoom, zoom);
        imgGraphics.drawImage((Image)this.img, 0, 0, null);
        J2SEMutableImage scaledMutableImage = new J2SEMutableImage(scaledImg.getWidth(), scaledImg.getHeight());
        scaledMutableImage.img = scaledImg;
        return scaledMutableImage;
    }

    public void getRGB(int[] argb, int offset, int scanlength, int x, int y, int width, int height) {
        if (width <= 0 || height <= 0) {
            return;
        }
        if (x < 0 || y < 0 || x + width > this.getWidth() || y + height > this.getHeight()) {
            throw new IllegalArgumentException("Specified area exceeds bounds of image");
        }
        if ((scanlength < 0 ? -scanlength : scanlength) < width) {
            throw new IllegalArgumentException("abs value of scanlength is less than width");
        }
        if (argb == null) {
            throw new NullPointerException("null rgbData");
        }
        if (offset < 0 || offset + width > argb.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        if (scanlength < 0 ? offset + scanlength * (height - 1) < 0 : offset + scanlength * (height - 1) + width > argb.length) {
            throw new ArrayIndexOutOfBoundsException();
        }
        try {
            new PixelGrabber(this.img, x, y, width, height, argb, offset, scanlength).grabPixels();
        }
        catch (InterruptedException e) {
            Logger.error(e);
        }
    }
}

