/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.impl;

import org.microemu.device.impl.Rectangle;
import org.microemu.device.impl.Shape;

public class Polygon
extends Shape {
    public int npoints;
    public int[] xpoints;
    public int[] ypoints;
    private Rectangle bounds = new Rectangle();

    public Polygon() {
    }

    public Polygon(int[] xpoints, int[] ypoints, int npoints) {
        this.xpoints = new int[npoints];
        this.ypoints = new int[npoints];
        this.npoints = npoints;
        System.arraycopy(xpoints, 0, this.xpoints, 0, npoints);
        System.arraycopy(ypoints, 0, this.ypoints, 0, npoints);
        for (int i = 0; i < npoints; ++i) {
            this.bounds.add(xpoints[i], ypoints[i]);
        }
    }

    public Polygon(Polygon poly) {
        this(poly.xpoints, poly.ypoints, poly.npoints);
    }

    public void addPoint(int x, int y) {
        if (this.npoints > 0) {
            int[] xtemp = this.xpoints;
            int[] ytemp = this.ypoints;
            this.xpoints = new int[this.npoints + 1];
            System.arraycopy(xtemp, 0, this.xpoints, 0, this.npoints);
            xtemp = null;
            this.ypoints = new int[this.npoints + 1];
            System.arraycopy(ytemp, 0, this.ypoints, 0, this.npoints);
            ytemp = null;
        } else {
            this.xpoints = new int[1];
            this.ypoints = new int[1];
        }
        ++this.npoints;
        this.xpoints[this.npoints - 1] = x;
        this.ypoints[this.npoints - 1] = y;
        this.bounds.add(x, y);
    }

    public Rectangle getBounds() {
        return this.bounds;
    }

    public boolean contains(int x, int y) {
        if (this.getBounds().contains(x, y)) {
            boolean c = false;
            int i = 0;
            int j = this.npoints - 1;
            while (i < this.npoints) {
                if ((this.ypoints[i] <= y && y < this.ypoints[j] || this.ypoints[j] <= y && y < this.ypoints[i]) && (double)x < (double)(this.xpoints[j] - this.xpoints[i]) * (double)(y - this.ypoints[i]) / (double)(this.ypoints[j] - this.ypoints[i]) + (double)this.xpoints[i]) {
                    c = !c;
                }
                j = i++;
            }
            return c;
        }
        return false;
    }
}

