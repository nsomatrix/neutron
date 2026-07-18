/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl;

import javax.microedition.lcdui.Image;
import org.microemu.device.impl.Rectangle;

public class PositionedImage {
    private Image image;
    private Rectangle rectangle;

    public PositionedImage(Image img, Rectangle arectangle) {
        this.image = img;
        this.rectangle = arectangle;
    }

    public Image getImage() {
        return this.image;
    }

    public Rectangle getRectangle() {
        return this.rectangle;
    }
}

