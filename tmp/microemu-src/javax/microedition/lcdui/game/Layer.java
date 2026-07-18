/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;

public abstract class Layer {
    private int width;
    private int height;
    private int x;
    private int y;
    private boolean visible;

    Layer(int x, int y, int width, int height, boolean visible) {
        this.setSize(width, height);
        this.setPosition(x, y);
        this.setVisible(visible);
    }

    void setSize(int width, int height) {
        if (width < 1 || height < 1) {
            throw new IllegalArgumentException();
        }
        this.width = width;
        this.height = height;
    }

    public final int getWidth() {
        return this.width;
    }

    public final int getHeight() {
        return this.height;
    }

    public final int getX() {
        return this.x;
    }

    public final int getY() {
        return this.y;
    }

    public final boolean isVisible() {
        return this.visible;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void move(int dx, int dy) {
        Layer layer = this;
        synchronized (layer) {
            this.x += dx;
            this.y += dy;
        }
    }

    public abstract void paint(Graphics var1);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setPosition(int x, int y) {
        Layer layer = this;
        synchronized (layer) {
            this.x = x;
            this.y = y;
        }
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}

