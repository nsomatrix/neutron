/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.image.PixelGrabber;
import org.microemu.device.j2se.J2SEMutableImage;
import org.microemu.log.Logger;

public class J2SEImmutableImage
extends javax.microedition.lcdui.Image {
    private Image img;
    private int width;
    private int height;

    public J2SEImmutableImage(Image image) {
        this.img = image;
        this.width = -1;
        this.height = -1;
    }

    public J2SEImmutableImage(J2SEMutableImage image) {
        this.img = Toolkit.getDefaultToolkit().createImage(image.getImage().getSource());
        this.width = -1;
        this.height = -1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getHeight() {
        if (this.height == -1) {
            ImageObserver observer;
            ImageObserver imageObserver = observer = new ImageObserver(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                    if ((infoflags & 1) != 0) {
                        J2SEImmutableImage.this.width = width;
                    }
                    if ((infoflags & 2) != 0) {
                        1 var7_7 = this;
                        synchronized (var7_7) {
                            J2SEImmutableImage.this.height = height;
                            this.notify();
                        }
                        return false;
                    }
                    return true;
                }
            };
            synchronized (imageObserver) {
                try {
                    this.height = this.img.getHeight(observer);
                }
                catch (NullPointerException ex) {
                    // empty catch block
                }
                if (this.height == -1) {
                    try {
                        observer.wait();
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
            }
        }
        return this.height;
    }

    public Image getImage() {
        return this.img;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getWidth() {
        if (this.width == -1) {
            ImageObserver observer;
            ImageObserver imageObserver = observer = new ImageObserver(){

                /*
                 * WARNING - Removed try catching itself - possible behaviour change.
                 */
                public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
                    if ((infoflags & 2) != 0) {
                        J2SEImmutableImage.this.height = height;
                    }
                    if ((infoflags & 1) != 0) {
                        2 var7_7 = this;
                        synchronized (var7_7) {
                            J2SEImmutableImage.this.width = width;
                            this.notify();
                        }
                        return false;
                    }
                    return true;
                }
            };
            synchronized (imageObserver) {
                try {
                    this.width = this.img.getWidth(observer);
                }
                catch (NullPointerException ex) {
                    // empty catch block
                }
                if (this.width == -1) {
                    try {
                        observer.wait();
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
            }
        }
        return this.width;
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

