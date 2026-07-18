/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl;

import org.microemu.device.impl.Shape;

public class Rectangle
extends Shape {
    private boolean initialized;
    public int x;
    public int y;
    public int width;
    public int height;

    public Rectangle() {
        this.initialized = false;
    }

    public Rectangle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.initialized = true;
    }

    public Rectangle(Rectangle rect) {
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
        this.initialized = false;
    }

    public void add(int newx, int newy) {
        if (this.initialized) {
            if (newx < this.x) {
                this.width += this.x - newx;
                this.x = newx;
            } else if (newx > this.x + this.width) {
                this.width = newx - this.x;
            }
            if (newy < this.y) {
                this.height += this.y - newy;
                this.y = newy;
            } else if (newy > this.y + this.height) {
                this.height = newy - this.y;
            }
        } else {
            this.x = newx;
            this.y = newy;
            this.initialized = true;
        }
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x < this.x + this.width && y >= this.y && y < this.y + this.height;
    }

    public Rectangle getBounds() {
        return this;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(this.x).append(",").append(this.y).append(" ").append(this.width).append("x").append(this.height);
        return buf.toString();
    }
}

