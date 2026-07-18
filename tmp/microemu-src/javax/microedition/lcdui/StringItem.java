/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;
import javax.microedition.lcdui.StringComponent;

public class StringItem
extends Item {
    StringComponent stringComponent;

    public StringItem(String label, String text) {
        this(label, text, 0);
    }

    public StringItem(String label, String text, int appearanceMode) {
        super(label);
        this.stringComponent = new StringComponent(text);
    }

    public int getAppearanceMode() {
        return 0;
    }

    public Font getFont() {
        return Font.getDefaultFont();
    }

    public void setFont(Font font) {
    }

    public void setPreferredSize(int width, int height) {
    }

    public String getText() {
        return this.stringComponent.getText();
    }

    public void setText(String text) {
        this.stringComponent.setText(text);
        this.repaint();
    }

    int getHeight() {
        return super.getHeight() + this.stringComponent.getHeight();
    }

    int paint(Graphics g) {
        super.paintContent(g);
        g.translate(0, super.getHeight());
        this.stringComponent.paint(g);
        g.translate(0, -super.getHeight());
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

