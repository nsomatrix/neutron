/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.Item;

public class ImageItem
extends Item {
    public static final int LAYOUT_DEFAULT = 0;
    public static final int LAYOUT_LEFT = 1;
    public static final int LAYOUT_RIGHT = 2;
    public static final int LAYOUT_CENTER = 3;
    public static final int LAYOUT_NEWLINE_BEFORE = 256;
    public static final int LAYOUT_NEWLINE_AFTER = 512;
    Image img;
    String altText;
    private int appearanceMode;

    public ImageItem(String label, Image img, int layout, String altText) {
        this(label, img, layout, altText, 0);
    }

    public ImageItem(String label, Image img, int layout, String altText, int appearanceMode) {
        super(label);
        this.setLayout(layout);
        if (appearanceMode != 0 && appearanceMode != 1 && appearanceMode != 2) {
            throw new IllegalArgumentException();
        }
        this.setImage(img);
        this.altText = altText;
        this.appearanceMode = appearanceMode;
    }

    public String getAltText() {
        return this.altText;
    }

    public int getAppearanceMode() {
        return this.appearanceMode;
    }

    public Image getImage() {
        return this.img;
    }

    public int getLayout() {
        return super.getLayout();
    }

    public void setAltText(String text) {
        this.altText = text;
    }

    public void setImage(Image img) {
        if (img != null && img.isMutable()) {
            img = Image.createImage(img);
        }
        this.img = img;
        this.repaint();
    }

    public void setLayout(int layout) {
        super.setLayout(layout);
    }

    int getHeight() {
        if (this.img == null) {
            return super.getHeight();
        }
        return super.getHeight() + this.img.getHeight();
    }

    int paint(Graphics g) {
        super.paintContent(g);
        if (this.img != null) {
            g.translate(0, super.getHeight());
            if (this.layout == 0 || this.layout == 1) {
                g.drawImage(this.img, 0, 0, 20);
            } else if (this.layout == 2) {
                g.drawImage(this.img, this.owner.getWidth(), 0, 24);
            } else if (this.layout == 3) {
                g.drawImage(this.img, this.owner.getWidth() / 2, 0, 17);
            } else {
                g.drawImage(this.img, 0, 0, 20);
            }
            g.translate(0, -super.getHeight());
        }
        return this.getHeight();
    }

    int traverse(int gameKeyCode, int top, int bottom, boolean action) {
        Font f = Font.getDefaultFont();
        if (gameKeyCode == 1) {
            if (top > 0) {
                if (top % f.getHeight() == 0) {
                    return -f.getHeight();
                }
                return -(top % f.getHeight());
            }
            return Integer.MAX_VALUE;
        }
        if (gameKeyCode == 6) {
            if (bottom < this.getHeight()) {
                if (this.getHeight() - bottom < f.getHeight()) {
                    return this.getHeight() - bottom;
                }
                return f.getHeight();
            }
            return Integer.MAX_VALUE;
        }
        return 0;
    }
}

