/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui.game;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Layer;
import javax.microedition.lcdui.game.TiledLayer;

public class Sprite
extends Layer {
    public static final int TRANS_NONE = 0;
    public static final int TRANS_ROT90 = 5;
    public static final int TRANS_ROT180 = 3;
    public static final int TRANS_ROT270 = 6;
    public static final int TRANS_MIRROR = 2;
    public static final int TRANS_MIRROR_ROT90 = 7;
    public static final int TRANS_MIRROR_ROT180 = 1;
    public static final int TRANS_MIRROR_ROT270 = 4;
    private int frame;
    private int[] sequence;
    private int refX;
    private int refY;
    private int cols;
    private int rows;
    private int transform;
    private Image img;
    private int collX;
    private int collY;
    private int collWidth;
    private int collHeight;
    private int[] rgbData;
    private int[] rgbDataAux;

    public Sprite(Image img) {
        this(img, img.getWidth(), img.getHeight());
    }

    public Sprite(Image img, int frameWidth, int frameHeight) {
        super(0, 0, frameWidth, frameHeight, true);
        if (img.getWidth() % frameWidth != 0 || img.getHeight() % frameHeight != 0) {
            throw new IllegalArgumentException();
        }
        this.img = img;
        this.cols = img.getWidth() / frameWidth;
        this.rows = img.getHeight() / frameHeight;
        this.collY = 0;
        this.collX = 0;
        this.collWidth = frameWidth;
        this.collHeight = frameHeight;
    }

    public Sprite(Sprite otherSprite) {
        super(otherSprite.getX(), otherSprite.getY(), otherSprite.getWidth(), otherSprite.getHeight(), otherSprite.isVisible());
        this.frame = otherSprite.frame;
        this.sequence = otherSprite.sequence;
        this.refX = otherSprite.refX;
        this.refY = otherSprite.refY;
        this.cols = otherSprite.cols;
        this.rows = otherSprite.rows;
        this.transform = otherSprite.transform;
        this.img = otherSprite.img;
        this.collX = otherSprite.collX;
        this.collY = otherSprite.collY;
        this.collWidth = otherSprite.collWidth;
        this.collHeight = otherSprite.collHeight;
    }

    public final boolean collidesWith(Image image, int iX, int iY, boolean pixelLevel) {
        if (image == null) {
            throw new IllegalArgumentException();
        }
        if (!this.isVisible()) {
            return false;
        }
        if (pixelLevel) {
            return this.collidesWithPixelLevel(image, iX, iY);
        }
        return this.collidesWith(image, iX, iY);
    }

    public final boolean collidesWith(TiledLayer layer, boolean pixelLevel) {
        if (layer == null) {
            throw new NullPointerException();
        }
        if (!this.isVisible()) {
            return false;
        }
        if (!layer.isVisible() || !this.isVisible()) {
            return false;
        }
        if (pixelLevel) {
            return this.collidesWithPixelLevel(layer, 0, 0);
        }
        return this.collidesWith(layer, 0, 0);
    }

    public final boolean collidesWith(Sprite otherSprite, boolean pixelLevel) {
        if (otherSprite == null) {
            throw new NullPointerException();
        }
        if (!otherSprite.isVisible() || !this.isVisible()) {
            return false;
        }
        if (pixelLevel) {
            return this.collidesWithPixelLevel(otherSprite, 0, 0);
        }
        return this.collidesWith(otherSprite, 0, 0);
    }

    public void defineReferencePixel(int x, int y) {
        this.refX = x;
        this.refY = y;
    }

    public int getRefPixelX() {
        return this.getX() + this.refX;
    }

    public int getRefPixelY() {
        return this.getY() + this.refY;
    }

    public void setRefPixelPosition(int x, int y) {
        int curRefY;
        int curRefX;
        int width = this.getWidth();
        int height = this.getHeight();
        switch (this.transform) {
            case 0: {
                curRefX = this.refX;
                curRefY = this.refY;
                break;
            }
            case 1: {
                curRefX = width - this.refX;
                curRefY = height - this.refY;
                break;
            }
            case 2: {
                curRefX = width - this.refX;
                curRefY = this.refY;
                break;
            }
            case 3: {
                curRefX = this.refX;
                curRefY = height - this.refY;
                break;
            }
            case 4: {
                curRefX = height - this.refY;
                curRefY = this.refX;
                break;
            }
            case 5: {
                curRefX = height - this.refY;
                curRefY = width - this.refX;
                break;
            }
            case 6: {
                curRefX = this.refY;
                curRefY = this.refX;
                break;
            }
            case 7: {
                curRefX = this.refY;
                curRefY = width - this.refX;
                break;
            }
            default: {
                return;
            }
        }
        this.setPosition(x - curRefX, y - curRefY);
    }

    public void defineCollisionRectangle(int x, int y, int width, int height) {
        if (width < 0 || height < 0) {
            throw new IllegalArgumentException();
        }
        this.collX = x;
        this.collY = y;
        this.collWidth = width;
        this.collHeight = height;
    }

    public void setFrameSequence(int[] sequence) {
        if (sequence == null) {
            this.sequence = null;
            return;
        }
        int max = this.rows * this.cols - 1;
        int l = sequence.length;
        if (l == 0) {
            throw new IllegalArgumentException();
        }
        for (int i = 0; i < l; ++i) {
            int value = sequence[i];
            if (value <= max && value >= 0) continue;
            throw new ArrayIndexOutOfBoundsException();
        }
        this.sequence = sequence;
        this.frame = 0;
    }

    public final int getFrame() {
        return this.frame;
    }

    public int getFrameSequenceLength() {
        return this.sequence == null ? this.rows * this.cols : this.sequence.length;
    }

    public void setFrame(int frame) {
        int l;
        int n = l = this.sequence == null ? this.rows * this.cols : this.sequence.length;
        if (frame < 0 || frame >= l) {
            throw new IndexOutOfBoundsException();
        }
        this.frame = frame;
    }

    public void nextFrame() {
        this.frame = this.frame == (this.sequence == null ? this.rows * this.cols : this.sequence.length) - 1 ? 0 : ++this.frame;
    }

    public void prevFrame() {
        this.frame = this.frame == 0 ? (this.sequence == null ? this.rows * this.cols : this.sequence.length) - 1 : --this.frame;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setImage(Image img, int frameWidth, int frameHeight) {
        Sprite sprite = this;
        synchronized (sprite) {
            int oldW = this.getWidth();
            int oldH = this.getHeight();
            int newW = img.getWidth();
            int newH = img.getHeight();
            this.setSize(frameWidth, frameHeight);
            if (img.getWidth() % frameWidth != 0 || img.getHeight() % frameHeight != 0) {
                throw new IllegalArgumentException();
            }
            this.img = img;
            int oldFrames = this.cols * this.rows;
            this.cols = img.getWidth() / frameWidth;
            this.rows = img.getHeight() / frameHeight;
            if (this.rows * this.cols < oldFrames) {
                this.sequence = null;
                this.frame = 0;
            }
            if (frameWidth != this.getWidth() || frameHeight != this.getHeight()) {
                this.defineCollisionRectangle(0, 0, frameWidth, frameHeight);
                this.rgbDataAux = null;
                this.rgbData = null;
                if (this.transform != 0) {
                    int dy;
                    int dx;
                    switch (this.transform) {
                        case 1: {
                            dx = newW - oldW;
                            dy = newH - oldH;
                            break;
                        }
                        case 2: {
                            dx = newW - oldW;
                            dy = 0;
                            break;
                        }
                        case 3: {
                            dx = 0;
                            dy = newH - oldH;
                            break;
                        }
                        case 4: {
                            dx = newH - oldH;
                            dy = 0;
                            break;
                        }
                        case 5: {
                            dx = newH - oldH;
                            dy = newW - oldW;
                            break;
                        }
                        case 6: {
                            dx = 0;
                            dy = 0;
                            break;
                        }
                        case 7: {
                            dx = 0;
                            dy = newW - oldW;
                            break;
                        }
                        default: {
                            return;
                        }
                    }
                    this.move(dx, dy);
                }
            }
        }
    }

    public final void paint(Graphics g) {
        if (!this.isVisible()) {
            return;
        }
        int f = this.sequence == null ? this.frame : this.sequence[this.frame];
        int w = this.getWidth();
        int h = this.getHeight();
        int fx = w * (f % this.cols);
        int fy = h * (f / this.cols);
        g.drawRegion(this.img, fx, fy, w, h, this.transform, this.getX(), this.getY(), 20);
    }

    public int getRawFrameCount() {
        return this.cols * this.rows;
    }

    public void setTransform(int transform) {
        int curRefY;
        int curRefX;
        int newRefY;
        int newRefX;
        if (this.transform == transform) {
            return;
        }
        int width = this.getWidth();
        int height = this.getHeight();
        int currentTransform = this.transform;
        switch (transform) {
            case 0: {
                newRefX = this.refX;
                newRefY = this.refY;
                break;
            }
            case 1: {
                newRefX = width - this.refX;
                newRefY = height - this.refY;
                break;
            }
            case 2: {
                newRefX = width - this.refX;
                newRefY = this.refY;
                break;
            }
            case 3: {
                newRefX = this.refX;
                newRefY = height - this.refY;
                break;
            }
            case 4: {
                newRefX = height - this.refY;
                newRefY = this.refX;
                break;
            }
            case 5: {
                newRefX = height - this.refY;
                newRefY = width - this.refX;
                break;
            }
            case 6: {
                newRefX = this.refY;
                newRefY = this.refX;
                break;
            }
            case 7: {
                newRefX = this.refY;
                newRefY = width - this.refX;
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
        switch (currentTransform) {
            case 0: {
                curRefX = this.refX;
                curRefY = this.refY;
                break;
            }
            case 1: {
                curRefX = width - this.refX;
                curRefY = height - this.refY;
                break;
            }
            case 2: {
                curRefX = width - this.refX;
                curRefY = this.refY;
                break;
            }
            case 3: {
                curRefX = this.refX;
                curRefY = height - this.refY;
                break;
            }
            case 4: {
                curRefX = height - this.refY;
                curRefY = this.refX;
                break;
            }
            case 5: {
                curRefX = height - this.refY;
                curRefY = width - this.refX;
                break;
            }
            case 6: {
                curRefX = this.refY;
                curRefY = this.refX;
                break;
            }
            case 7: {
                curRefX = this.refY;
                curRefY = width - this.refX;
                break;
            }
            default: {
                return;
            }
        }
        this.move(curRefX - newRefX, curRefY - newRefY);
        this.transform = transform;
    }

    private synchronized boolean collidesWith(Object o, int oX, int oY) {
        int tX = 0;
        int tY = 0;
        int tW = 0;
        int tH = 0;
        int oW = 0;
        int oH = 0;
        Sprite t = this;
        boolean another = true;
        while (another) {
            int sH;
            int sW;
            int sY;
            int sX;
            int cX = t.collX;
            int cY = t.collY;
            int cW = t.collWidth;
            int cH = t.collHeight;
            if (cW == 0 || cH == 0) {
                return false;
            }
            switch (t.transform) {
                case 0: {
                    sX = t.getX() + cX;
                    sY = t.getY() + cY;
                    sW = cW;
                    sH = cH;
                    break;
                }
                case 1: {
                    sX = t.getX() + cX;
                    sY = t.getY() + (t.getHeight() - cY - 1) - cH;
                    sW = cW;
                    sH = cH;
                    break;
                }
                case 2: {
                    sX = t.getX() + (t.getWidth() - cX - 1) - cW;
                    sY = t.getY() + cY;
                    sW = cW;
                    sH = cH;
                    break;
                }
                case 3: {
                    sX = t.getX() + (t.getWidth() - cX - 1) - cW;
                    sY = t.getY() + (t.getHeight() - cY - 1) - cH;
                    sW = cW;
                    sH = cH;
                    break;
                }
                case 4: {
                    sX = t.getX() + cY;
                    sY = t.getY() + cX;
                    sW = cH;
                    sH = cW;
                    break;
                }
                case 5: {
                    sX = t.getX() + (t.getHeight() - cY - 1) - cH;
                    sY = t.getY() + cX;
                    sW = cH;
                    sH = cW;
                    break;
                }
                case 7: {
                    sX = t.getX() + (t.getHeight() - cY - 1) - cH;
                    sY = t.getY() + (t.getWidth() - cX - 1) - cW;
                    sW = cH;
                    sH = cW;
                    break;
                }
                case 6: {
                    sX = t.getX() + cY;
                    sY = t.getY() + (t.getWidth() - cX - 1) - cW;
                    sW = cH;
                    sH = cW;
                    break;
                }
                default: {
                    return false;
                }
            }
            if (o != t) {
                tX = sX;
                tY = sY;
                tW = sW;
                tH = sH;
                if (o instanceof Sprite) {
                    t = (Sprite)o;
                    continue;
                }
                if (o instanceof TiledLayer) {
                    another = false;
                    TiledLayer layer = (TiledLayer)o;
                    oX = layer.getX();
                    oY = layer.getY();
                    oW = layer.getWidth();
                    oH = layer.getHeight();
                    continue;
                }
                another = false;
                Image img = (Image)o;
                oW = img.getWidth();
                oH = img.getHeight();
                continue;
            }
            another = false;
            oX = sX;
            oY = sY;
            oW = sW;
            oH = sH;
        }
        if (tX > oX && tX >= oX + oW) {
            return false;
        }
        if (tX < oX && tX + tW <= oX) {
            return false;
        }
        if (tY > oY && tY >= oY + oH) {
            return false;
        }
        if (tY < oY && tY + tH <= oY) {
            return false;
        }
        if (o instanceof TiledLayer) {
            int rH;
            int rY;
            int rW;
            int rX;
            TiledLayer layer = (TiledLayer)o;
            if (oX > tX) {
                rX = oX;
                rW = (oX + oW < tX + tW ? oX + oW : tX + tW) - rX;
            } else {
                rX = tX;
                rW = (tX + tW < oX + oW ? tX + tW : oX + oW) - rX;
            }
            if (oY > tY) {
                rY = oY;
                rH = (oY + oH < tY + tH ? oY + oH : tY + tH) - rY;
            } else {
                rY = tY;
                rH = (tY + tH < oY + oH ? tY + tH : oY + oH) - rY;
            }
            Image img = layer.img;
            int lW = layer.getCellWidth();
            int lH = layer.getCellHeight();
            int minC = (rX - oX) / lW;
            int minR = (rY - oY) / lH;
            int maxC = (rX - oX + rW - 1) / lW;
            int maxR = (rY - oY + rH - 1) / lH;
            for (int row = minR; row <= maxR; ++row) {
                for (int col = minC; col <= maxC; ++col) {
                    int cell = layer.getCell(col, row);
                    if (cell < 0) {
                        cell = layer.getAnimatedTile(cell);
                    }
                    if (cell == 0) continue;
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private synchronized boolean collidesWithPixelLevel(Object o, int oX, int oY) {
        int rH;
        int rY;
        int rW;
        int rX;
        boolean another = true;
        Sprite t = this;
        int tX = 0;
        int tY = 0;
        int tW = 0;
        int tH = 0;
        int oW = 0;
        int oH = 0;
        while (another) {
            int sH;
            int sW;
            int sY;
            int sX;
            if (t.collX >= t.getWidth() || t.collX + t.collWidth <= 0 || t.collY >= t.getHeight() || t.collY + t.collHeight <= 0) {
                return false;
            }
            int cX = t.collX >= 0 ? t.collX : 0;
            int cY = t.collY >= 0 ? t.collY : 0;
            int cW = t.collX + t.collWidth < t.getWidth() ? t.collX + t.collWidth - cX : t.getWidth() - cX;
            int cH = t.collY + t.collHeight < t.getHeight() ? t.collY + t.collHeight - cY : t.getHeight() - cY;
            switch (t.transform) {
                case 0: {
                    sX = t.getX() + cX;
                    sY = t.getY() + cY;
                    sW = cW;
                    sH = cH;
                    break;
                }
                case 1: {
                    sX = t.getX() + cX;
                    sY = t.getY() + (t.getHeight() - cY - 1) - cH;
                    sW = cW;
                    sH = cH;
                    break;
                }
                case 2: {
                    sX = t.getX() + (t.getWidth() - cX - 1) - cW;
                    sY = t.getY() + cY;
                    sW = cW;
                    sH = cH;
                    break;
                }
                case 3: {
                    sX = t.getX() + (t.getWidth() - cX - 1) - cW;
                    sY = t.getY() + (t.getHeight() - cY - 1) - cH;
                    sW = cW;
                    sH = cH;
                    break;
                }
                case 4: {
                    sX = t.getX() + cY;
                    sY = t.getY() + cX;
                    sW = cH;
                    sH = cW;
                    break;
                }
                case 5: {
                    sX = t.getX() + (t.getHeight() - cY) - cH;
                    sY = t.getY() + cX;
                    sW = cH;
                    sH = cW;
                    break;
                }
                case 7: {
                    sX = t.getX() + (t.getHeight() - cY) - cH;
                    sY = t.getY() + (t.getWidth() - cX) - cW;
                    sW = cH;
                    sH = cW;
                    break;
                }
                case 6: {
                    sX = t.getX() + cY;
                    sY = t.getY() + (t.getWidth() - cX) - cW;
                    sW = cH;
                    sH = cW;
                    break;
                }
                default: {
                    return false;
                }
            }
            if (o != t) {
                tX = sX;
                tY = sY;
                tW = sW;
                tH = sH;
                if (o instanceof Sprite) {
                    t = (Sprite)o;
                    continue;
                }
                if (o instanceof TiledLayer) {
                    another = false;
                    TiledLayer layer = (TiledLayer)o;
                    oX = layer.getX();
                    oY = layer.getY();
                    oW = layer.getWidth();
                    oH = layer.getHeight();
                    continue;
                }
                another = false;
                Image img = (Image)o;
                oW = img.getWidth();
                oH = img.getHeight();
                continue;
            }
            another = false;
            oX = sX;
            oY = sY;
            oW = sW;
            oH = sH;
        }
        if (tX > oX && tX >= oX + oW) {
            return false;
        }
        if (tX < oX && tX + tW <= oX) {
            return false;
        }
        if (tY > oY && tY >= oY + oH) {
            return false;
        }
        if (tY < oY && tY + tH <= oY) {
            return false;
        }
        if (oX > tX) {
            rX = oX;
            rW = (oX + oW < tX + tW ? oX + oW : tX + tW) - rX;
        } else {
            rX = tX;
            rW = (tX + tW < oX + oW ? tX + tW : oX + oW) - rX;
        }
        if (oY > tY) {
            rY = oY;
            rH = (oY + oH < tY + tH ? oY + oH : tY + tH) - rY;
        } else {
            rY = tY;
            rH = (tY + tH < oY + oH ? tY + tH : oY + oH) - rY;
        }
        int tColIncr = 0;
        int tRowIncr = 0;
        int tOffset = 0;
        int oColIncr = 0;
        int oRowIncr = 0;
        int oOffset = 0;
        int f = this.sequence == null ? this.frame : this.sequence[this.frame];
        int fW = this.getWidth();
        int fH = this.getHeight();
        int fX = fW * (f % this.rows);
        int fY = fH * (f / this.rows);
        if (this.rgbData == null) {
            this.rgbData = new int[fW * fH];
            this.rgbDataAux = new int[fW * fH];
        }
        t = this;
        another = true;
        int[] tRgbData = this.rgbData;
        while (another) {
            int sRowIncr;
            int sColIncr;
            int sOffset;
            switch (t.transform) {
                case 0: {
                    t.img.getRGB(tRgbData, 0, rW, fX + rX - t.getX(), fY + rY - t.getY(), rW, rH);
                    sOffset = 0;
                    sColIncr = 1;
                    sRowIncr = 0;
                    break;
                }
                case 3: {
                    t.img.getRGB(tRgbData, 0, rW, fX + fW - (rX - t.getX()) - rW - 1, fY + fH - (rY - t.getY()) - rH - 1, rW, rH);
                    sOffset = rH * rW - 1;
                    sColIncr = -1;
                    sRowIncr = 0;
                    break;
                }
                case 2: {
                    t.img.getRGB(tRgbData, 0, rW, fX + fW - (rX - t.getX()) - rW - 1, fY + rY - t.getY(), rW, rH);
                    sOffset = rW - 1;
                    sColIncr = -1;
                    sRowIncr = rW << 1;
                    break;
                }
                case 1: {
                    t.img.getRGB(tRgbData, 0, rW, fX + rX - t.getX(), fY + fH - (rY - t.getY()) - rH - 1, rW, rH);
                    sOffset = (rH - 1) * rW;
                    sColIncr = 1;
                    sRowIncr = -(rW << 1);
                    break;
                }
                case 5: {
                    t.img.getRGB(tRgbData, 0, rH, fX + rY - t.getY(), fY + fH - (rX - t.getX()) - rW, rH, rW);
                    sOffset = (rW - 1) * rH;
                    sColIncr = -rH;
                    sRowIncr = rH * rW + 1;
                    break;
                }
                case 7: {
                    t.img.getRGB(tRgbData, 0, rH, fX + fW - (rY - t.getY()) - rH, fY + fH - (rX - t.getX()) - rW, rH, rW);
                    sOffset = rH * rW - 1;
                    sColIncr = -rH;
                    sRowIncr = rH * rW - 1;
                    break;
                }
                case 4: {
                    t.img.getRGB(tRgbData, 0, rH, fX + rY - t.getY(), fY + rX - t.getX(), rH, rW);
                    sOffset = 0;
                    sColIncr = rH;
                    sRowIncr = -(rH * rW) + 1;
                    break;
                }
                case 6: {
                    t.img.getRGB(tRgbData, 0, rH, fX + fW - (rY - t.getY()) - rH, fY + rX - t.getX(), rH, rW);
                    sOffset = rH - 1;
                    sColIncr = rH;
                    sRowIncr = -(rH * rW) - 1;
                    break;
                }
                default: {
                    return false;
                }
            }
            if (o != t) {
                tOffset = sOffset;
                tRowIncr = sRowIncr;
                tColIncr = sColIncr;
                if (o instanceof Sprite) {
                    t = (Sprite)o;
                    tRgbData = this.rgbDataAux;
                    f = t.sequence == null ? t.frame : t.sequence[t.frame];
                    fW = t.getWidth();
                    fH = t.getHeight();
                    fX = fW * (f % t.rows);
                    fY = fH * (f / t.rows);
                    continue;
                }
                if (o instanceof TiledLayer) {
                    another = false;
                    TiledLayer layer = (TiledLayer)o;
                    Image img = layer.img;
                    oOffset = 0;
                    oColIncr = 1;
                    oRowIncr = 0;
                    int lW = layer.getCellWidth();
                    int lH = layer.getCellHeight();
                    int minC = (rX - oX) / lW;
                    int minR = (rY - oY) / lH;
                    int maxC = (rX - oX + rW - 1) / lW;
                    int maxR = (rY - oY + rH - 1) / lH;
                    for (int row = minR; row <= maxR; ++row) {
                        for (int col = minC; col <= maxC; ++col) {
                            int cell = layer.getCell(col, row);
                            if (cell < 0) {
                                cell = layer.getAnimatedTile(cell);
                            }
                            int minX = col == minC ? (rX - oX) % lW : 0;
                            int minY = row == minR ? (rY - oY) % lH : 0;
                            int maxX = col == maxC ? (rX + rW - oX - 1) % lW : lW - 1;
                            int maxY = row == maxR ? (rY + rH - oY - 1) % lH : lH - 1;
                            int c = (row - minR) * lH * rW + (col - minC) * lW - (col == minC ? 0 : (rX - oX) % lW) - (row == minR ? 0 : (rY - oY) % lH) * rW;
                            if (cell == 0) {
                                int y = minY;
                                while (y <= maxY) {
                                    int x = minX;
                                    while (x <= maxX) {
                                        this.rgbDataAux[c] = 0;
                                        ++x;
                                        ++c;
                                    }
                                    ++y;
                                    c += rW - (maxX - minX + 1);
                                }
                                continue;
                            }
                            int imgCols = img.getWidth() / layer.getCellWidth();
                            int xSrc = lW * (--cell % imgCols);
                            int ySrc = cell / imgCols * lH;
                            img.getRGB(this.rgbDataAux, c, rW, xSrc + minX, ySrc + minY, maxX - minX + 1, maxY - minY + 1);
                        }
                    }
                    continue;
                }
                another = false;
                Image img = (Image)o;
                img.getRGB(this.rgbDataAux, 0, rW, rX - oX, rY - oY, rW, rH);
                oOffset = 0;
                oColIncr = 1;
                oRowIncr = 0;
                continue;
            }
            another = false;
            oOffset = sOffset;
            oRowIncr = sRowIncr;
            oColIncr = sColIncr;
        }
        int row = 0;
        while (row < rH) {
            int col = 0;
            while (col < rW) {
                int rgb = this.rgbData[tOffset];
                int rgbA = this.rgbDataAux[oOffset];
                if ((rgb & rgbA) >> 24 == -1) {
                    return true;
                }
                ++col;
                tOffset += tColIncr;
                oOffset += oColIncr;
            }
            ++row;
            tOffset += tRowIncr;
            oOffset += oRowIncr;
        }
        return false;
    }
}

