/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui.game;

import java.util.Vector;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Layer;

public class LayerManager {
    private Vector layers = new Vector();
    private int viewX = 0;
    private int viewY = 0;
    private int viewW;
    private int viewH = Integer.MAX_VALUE;

    public LayerManager() {
        this.viewW = Integer.MAX_VALUE;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void append(Layer layer) {
        LayerManager layerManager = this;
        synchronized (layerManager) {
            if (layer == null) {
                throw new NullPointerException();
            }
            this.layers.add(layer);
        }
    }

    public Layer getLayerAt(int i) {
        return (Layer)this.layers.get(i);
    }

    public int getSize() {
        return this.layers.size();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void insert(Layer layer, int i) {
        LayerManager layerManager = this;
        synchronized (layerManager) {
            if (layer == null) {
                throw new NullPointerException();
            }
            this.layers.insertElementAt(layer, i);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void remove(Layer layer) {
        LayerManager layerManager = this;
        synchronized (layerManager) {
            if (layer == null) {
                throw new NullPointerException();
            }
            this.layers.remove(layer);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setViewWindow(int x, int y, int width, int height) {
        LayerManager layerManager = this;
        synchronized (layerManager) {
            if (width < 0 || height < 0) {
                throw new IllegalArgumentException();
            }
            this.viewX = x;
            this.viewY = y;
            this.viewW = width;
            this.viewH = height;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void paint(Graphics g, int x, int y) {
        LayerManager layerManager = this;
        synchronized (layerManager) {
            if (g == null) {
                throw new NullPointerException();
            }
            int clipX = g.getClipX();
            int clipY = g.getClipY();
            int clipW = g.getClipWidth();
            int clipH = g.getClipHeight();
            g.translate(x - this.viewX, y - this.viewY);
            g.clipRect(this.viewX, this.viewY, this.viewW, this.viewH);
            int i = this.getSize();
            while (--i >= 0) {
                Layer comp = this.getLayerAt(i);
                if (!comp.isVisible()) continue;
                comp.paint(g);
            }
            g.translate(-x + this.viewX, -y + this.viewY);
            g.setClip(clipX, clipY, clipW, clipH);
        }
    }
}

