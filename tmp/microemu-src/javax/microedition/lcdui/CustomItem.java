/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Item;

public abstract class CustomItem
extends Item {
    protected static final int TRAVERSE_HORIZONTAL = 1;
    protected static final int TRAVERSE_VERTICAL = 2;
    protected static final int KEY_PRESS = 4;
    protected static final int KEY_RELEASE = 8;
    protected static final int KEY_REPEAT = 16;
    protected static final int POINTER_PRESS = 32;
    protected static final int POINTER_RELEASE = 64;
    protected static final int POINTER_DRAG = 128;
    protected static final int NONE = 0;
    int width = 0;
    int height = 0;

    protected CustomItem(String label) {
        super(label);
    }

    public int getGameAction(int keycode) {
        return 0;
    }

    protected final int getInteractionModes() {
        return 0;
    }

    protected abstract int getMinContentHeight();

    protected abstract int getMinContentWidth();

    protected abstract int getPrefContentHeight(int var1);

    protected abstract int getPrefContentWidth(int var1);

    protected void hideNotify() {
    }

    protected final void invalidate() {
        this.repaintOwner();
    }

    protected void keyPressed(int keyCode) {
    }

    protected void keyReleased(int keyCode) {
    }

    protected void keyRepeated(int keyCode) {
    }

    protected abstract void paint(Graphics var1, int var2, int var3);

    protected void pointerDragged(int x, int y) {
    }

    protected void pointerPressed(int x, int y) {
    }

    protected void pointerReleased(int x, int y) {
    }

    protected final void repaint() {
        super.repaint();
    }

    protected final void repaint(int x, int y, int w, int h) {
        this.repaint();
    }

    protected void showNotify() {
    }

    protected void sizeChanged(int w, int h) {
    }

    protected boolean traverse(int dir, int viewportWidth, int viewportHeight, int[] visRect_inout) {
        return false;
    }

    protected void traverseOut() {
    }

    int paint(Graphics g) {
        this.width = this.getPrefContentWidth(-1);
        this.height = this.getPrefContentHeight(-1);
        super.paintContent(g);
        g.translate(0, super.getHeight());
        this.paint(g, this.width, this.height);
        return this.height;
    }

    int getHeight() {
        return super.getHeight() + this.height;
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

