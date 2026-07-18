/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import org.microemu.device.j2se.J2SEFont;

public class J2SESystemFont
implements J2SEFont {
    private static final Graphics2D graphics = (Graphics2D)new BufferedImage(1, 1, 2).getGraphics();
    private String name;
    private String style;
    private int size;
    private boolean antialiasing;
    private boolean initialized;
    private FontMetrics fontMetrics;

    public J2SESystemFont(String name, String style, int size, boolean antialiasing) {
        this.name = name;
        this.style = style.toLowerCase();
        this.size = size;
        this.antialiasing = antialiasing;
        this.initialized = false;
    }

    public void setAntialiasing(boolean antialiasing) {
        if (this.antialiasing != antialiasing) {
            this.antialiasing = antialiasing;
            this.initialized = false;
        }
    }

    public int charWidth(char ch) {
        this.checkInitialized();
        return this.fontMetrics.charWidth(ch);
    }

    public int charsWidth(char[] ch, int offset, int length) {
        this.checkInitialized();
        return this.fontMetrics.charsWidth(ch, offset, length);
    }

    public int getBaselinePosition() {
        this.checkInitialized();
        return this.fontMetrics.getAscent();
    }

    public int getHeight() {
        this.checkInitialized();
        return this.fontMetrics.getHeight();
    }

    public int stringWidth(String str) {
        this.checkInitialized();
        return this.fontMetrics.stringWidth(str);
    }

    public Font getFont() {
        this.checkInitialized();
        return this.fontMetrics.getFont();
    }

    private synchronized void checkInitialized() {
        if (!this.initialized) {
            int awtStyle = 0;
            if (this.style.indexOf("plain") != -1) {
                awtStyle |= 0;
            }
            if (this.style.indexOf("bold") != -1) {
                awtStyle |= 1;
            }
            if (this.style.indexOf("italic") != -1) {
                awtStyle |= 2;
            }
            if (this.style.indexOf("underlined") != -1) {
                // empty if block
            }
            if (this.antialiasing) {
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            } else {
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
            }
            this.fontMetrics = graphics.getFontMetrics(new Font(this.name, awtStyle, this.size));
            this.initialized = true;
        }
    }
}

