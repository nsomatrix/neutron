/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringComponent;

class ImageStringItem
extends Item {
    Image img;
    StringComponent stringComponent;

    public ImageStringItem(String label, Image img, String text) {
        super(label);
        this.stringComponent = new StringComponent(text);
        this.setImage(img);
    }

    public Image getImage() {
        return this.img;
    }

    public void setImage(Image img) {
        this.img = img;
        if (this.img != null) {
            this.stringComponent.setWidthDecreaser(img.getWidth() + 2);
        }
    }

    public String getText() {
        return this.stringComponent.getText();
    }

    public void setText(String text) {
        this.stringComponent.setText(text);
    }

    int getHeight() {
        if (this.img != null && this.img.getHeight() > this.stringComponent.getHeight()) {
            return this.img.getHeight();
        }
        return this.stringComponent.getHeight();
    }

    void invertPaint(boolean state) {
        this.stringComponent.invertPaint(state);
    }

    int paint(Graphics g) {
        if (this.stringComponent == null) {
            return 0;
        }
        if (this.img != null) {
            g.drawImage(this.img, 0, 0, 20);
            g.translate(this.img.getWidth() + 2, 0);
        }
        int y = this.stringComponent.paint(g);
        if (this.img != null) {
            g.translate(-this.img.getWidth() - 2, 0);
        }
        return y;
    }
}

