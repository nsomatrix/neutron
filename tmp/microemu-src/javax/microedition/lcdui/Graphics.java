/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Image;

public class Graphics {
    public static final int SOLID = 0;
    public static final int DOTTED = 1;
    public static final int LEFT = 4;
    public static final int RIGHT = 8;
    public static final int TOP = 16;
    public static final int BASELINE = 64;
    public static final int BOTTOM = 32;
    public static final int HCENTER = 1;
    public static final int VCENTER = 2;
    int strokeStyle = 0;
    int translateX = 0;
    int translateY = 0;

    public void clipRect(int x, int y, int width, int height) {
        this.implementationError();
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.implementationError();
    }

    public void drawChar(char character, int x, int y, int anchor) {
        char[] carr = new char[]{character};
        this.drawString(new String(carr), x, y, anchor);
    }

    public void drawChars(char[] data, int offset, int length, int x, int y, int anchor) {
        this.drawString(new String(data, offset, length), x, y, anchor);
    }

    public void drawImage(Image img, int x, int y, int anchor) {
        this.implementationError();
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        this.implementationError();
    }

    public void drawRect(int x, int y, int width, int height) {
        this.implementationError();
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.implementationError();
    }

    public void drawString(String str, int x, int y, int anchor) {
        this.implementationError();
    }

    public void drawSubstring(String str, int offset, int len, int x, int y, int anchor) {
        this.drawString(str.substring(offset, offset + len), x, y, anchor);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.implementationError();
    }

    public void fillRect(int x, int y, int width, int height) {
        this.implementationError();
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.implementationError();
    }

    public int getBlueComponent() {
        return this.getColor() & 0xFF;
    }

    public int getClipHeight() {
        this.implementationError();
        return -1;
    }

    public int getClipWidth() {
        this.implementationError();
        return -1;
    }

    public int getClipX() {
        this.implementationError();
        return -1;
    }

    public int getClipY() {
        this.implementationError();
        return -1;
    }

    public int getColor() {
        this.implementationError();
        return -1;
    }

    public Font getFont() {
        this.implementationError();
        return null;
    }

    public int getGrayScale() {
        return (this.getRedComponent() + this.getGreenComponent() + this.getBlueComponent()) / 3;
    }

    public int getGreenComponent() {
        return this.getColor() >> 8 & 0xFF;
    }

    public int getRedComponent() {
        return this.getColor() >> 16 & 0xFF;
    }

    public int getStrokeStyle() {
        return this.strokeStyle;
    }

    public int getTranslateX() {
        return this.translateX;
    }

    public int getTranslateY() {
        return this.translateY;
    }

    public void setClip(int x, int y, int width, int height) {
        this.implementationError();
    }

    public void setColor(int RGB) {
        this.implementationError();
    }

    public void setColor(int red, int green, int blue) {
        int rgb = blue;
        rgb += green << 8;
        this.setColor(rgb += red << 16);
    }

    public void setFont(Font font) {
        this.implementationError();
    }

    public void setGrayScale(int grey) {
        if (grey < 0 || grey > 255) {
            throw new IllegalArgumentException();
        }
        this.setColor(grey, grey, grey);
    }

    public void setStrokeStyle(int style) {
        if (style != 0 && style != 1) {
            throw new IllegalArgumentException();
        }
        this.strokeStyle = style;
    }

    public void translate(int x, int y) {
        this.translateX += x;
        this.translateY += y;
    }

    public void drawRegion(Image src, int x_src, int y_src, int width, int height, int transform, int x_dst, int y_dst, int anchor) {
        this.implementationError();
    }

    public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
        this.implementationError();
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        this.implementationError();
    }

    public void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor) {
        this.implementationError();
    }

    public int getDisplayColor(int color) {
        this.implementationError();
        return -1;
    }

    private void implementationError() {
        try {
            throw new RuntimeException("Must be implemented in DisplayGraphics");
        }
        catch (RuntimeException ex) {
            ex.printStackTrace();
            return;
        }
    }
}

