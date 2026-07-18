/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import org.microemu.device.Device;
import org.microemu.device.DeviceFactory;
import org.microemu.device.DisplayGraphics;
import org.microemu.device.MutableImage;
import org.microemu.device.j2se.BWImageFilter;
import org.microemu.device.j2se.GrayImageFilter;
import org.microemu.device.j2se.J2SEDeviceDisplay;
import org.microemu.device.j2se.J2SEFont;
import org.microemu.device.j2se.J2SEFontManager;
import org.microemu.device.j2se.J2SEImmutableImage;
import org.microemu.device.j2se.J2SEMutableImage;
import org.microemu.device.j2se.RGBImageFilter;

public class J2SEDisplayGraphics
extends Graphics
implements DisplayGraphics {
    private static HashMap colorCache = new HashMap();
    private Graphics2D g;
    private MutableImage image;
    private int color = 0;
    private Rectangle clip;
    private Font currentFont = Font.getDefaultFont();
    private java.awt.image.RGBImageFilter filter = null;

    public J2SEDisplayGraphics(Graphics2D a_g, MutableImage a_image) {
        this.g = a_g;
        this.image = a_image;
        this.clip = a_g.getClipBounds();
        Device device = DeviceFactory.getDevice();
        J2SEFontManager fontManager = (J2SEFontManager)device.getFontManager();
        J2SEFont tmpFont = (J2SEFont)fontManager.getFont(this.currentFont);
        this.g.setFont(tmpFont.getFont());
        if (fontManager.getAntialiasing()) {
            this.g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } else {
            this.g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        }
        J2SEDeviceDisplay display = (J2SEDeviceDisplay)device.getDeviceDisplay();
        if (display.isColor()) {
            if (display.backgroundColor.getRed() != 255 || display.backgroundColor.getGreen() != 255 || display.backgroundColor.getBlue() != 255 || display.foregroundColor.getRed() != 0 || display.foregroundColor.getGreen() != 0 || display.foregroundColor.getBlue() != 0) {
                this.filter = new RGBImageFilter();
            }
        } else {
            this.filter = display.numColors() == 2 ? new BWImageFilter() : new GrayImageFilter();
        }
    }

    public MutableImage getImage() {
        return this.image;
    }

    public int getColor() {
        return this.color;
    }

    public void setColor(int RGB) {
        this.color = RGB;
        Color awtColor = (Color)colorCache.get(new Integer(RGB));
        if (awtColor == null) {
            awtColor = this.filter != null ? new Color(this.filter.filterRGB(0, 0, this.color)) : new Color(RGB);
            colorCache.put(new Integer(RGB), awtColor);
        }
        this.g.setColor(awtColor);
    }

    public Font getFont() {
        return this.currentFont;
    }

    public void setFont(Font font) {
        this.currentFont = font;
        J2SEFont tmpFont = (J2SEFont)((J2SEFontManager)DeviceFactory.getDevice().getFontManager()).getFont(this.currentFont);
        this.g.setFont(tmpFont.getFont());
    }

    public void clipRect(int x, int y, int width, int height) {
        this.g.clipRect(x, y, width, height);
        this.clip = this.g.getClipBounds();
    }

    public void setClip(int x, int y, int width, int height) {
        this.g.setClip(x, y, width, height);
        this.clip.x = x;
        this.clip.y = y;
        this.clip.width = width;
        this.clip.height = height;
    }

    public int getClipX() {
        return this.clip.x;
    }

    public int getClipY() {
        return this.clip.y;
    }

    public int getClipHeight() {
        return this.clip.height;
    }

    public int getClipWidth() {
        return this.clip.width;
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.g.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    public void drawImage(javax.microedition.lcdui.Image img, int x, int y, int anchor) {
        int newx = x;
        int newy = y;
        if (anchor == 0) {
            anchor = 20;
        }
        if ((anchor & 8) != 0) {
            newx -= img.getWidth();
        } else if ((anchor & 1) != 0) {
            newx -= img.getWidth() / 2;
        }
        if ((anchor & 0x20) != 0) {
            newy -= img.getHeight();
        } else if ((anchor & 2) != 0) {
            newy -= img.getHeight() / 2;
        }
        if (img.isMutable()) {
            this.g.drawImage(((J2SEMutableImage)img).getImage(), newx, newy, null);
        } else {
            this.g.drawImage(((J2SEImmutableImage)img).getImage(), newx, newy, null);
        }
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        this.g.drawLine(x1, y1, x2, y2);
    }

    public void drawRect(int x, int y, int width, int height) {
        this.drawLine(x, y, x + width, y);
        this.drawLine(x + width, y, x + width, y + height);
        this.drawLine(x + width, y + height, x, y + height);
        this.drawLine(x, y + height, x, y);
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void drawString(String str, int x, int y, int anchor) {
        int newx = x;
        int newy = y;
        if (anchor == 0) {
            anchor = 20;
        }
        if ((anchor & 0x10) != 0) {
            newy += this.g.getFontMetrics().getAscent();
        } else if ((anchor & 0x20) != 0) {
            newy -= this.g.getFontMetrics().getDescent();
        }
        if ((anchor & 1) != 0) {
            newx -= this.g.getFontMetrics().stringWidth(str) / 2;
        } else if ((anchor & 8) != 0) {
            newx -= this.g.getFontMetrics().stringWidth(str);
        }
        this.g.drawString(str, newx, newy);
        if ((this.currentFont.getStyle() & 4) != 0) {
            this.g.drawLine(newx, newy + 1, newx + this.g.getFontMetrics().stringWidth(str), newy + 1);
        }
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.g.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    public void fillRect(int x, int y, int width, int height) {
        this.g.fillRect(x, y, width, height);
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void translate(int x, int y) {
        super.translate(x, y);
        this.g.translate(x, y);
        this.clip.x -= x;
        this.clip.y -= y;
    }

    public void drawRegion(javax.microedition.lcdui.Image src, int x_src, int y_src, int width, int height, int transform, int x_dst, int y_dst, int anchor) {
        if (x_src + width > src.getWidth() || y_src + height > src.getHeight() || width < 0 || height < 0 || x_src < 0 || y_src < 0) {
            throw new IllegalArgumentException("Area out of Image");
        }
        if (src.isMutable() && src.getGraphics() == this) {
            throw new IllegalArgumentException("Image is source and target");
        }
        Image img = src.isMutable() ? ((J2SEMutableImage)src).getImage() : ((J2SEImmutableImage)src).getImage();
        AffineTransform t = new AffineTransform();
        int dW = width;
        int dH = height;
        switch (transform) {
            case 0: {
                break;
            }
            case 5: {
                t.translate(height, 0.0);
                t.rotate(1.5707963267948966);
                dW = height;
                dH = width;
                break;
            }
            case 3: {
                t.translate(width, height);
                t.rotate(Math.PI);
                break;
            }
            case 6: {
                t.translate(0.0, width);
                t.rotate(4.71238898038469);
                dW = height;
                dH = width;
                break;
            }
            case 2: {
                t.translate(width, 0.0);
                t.scale(-1.0, 1.0);
                break;
            }
            case 7: {
                t.translate(height, 0.0);
                t.rotate(1.5707963267948966);
                t.translate(width, 0.0);
                t.scale(-1.0, 1.0);
                dW = height;
                dH = width;
                break;
            }
            case 1: {
                t.translate(width, 0.0);
                t.scale(-1.0, 1.0);
                t.translate(width, height);
                t.rotate(Math.PI);
                break;
            }
            case 4: {
                t.rotate(4.71238898038469);
                t.scale(-1.0, 1.0);
                dW = height;
                dH = width;
                break;
            }
            default: {
                throw new IllegalArgumentException("Bad transform");
            }
        }
        boolean badAnchor = false;
        if (anchor == 0) {
            anchor = 20;
        }
        if ((anchor & 0x7F) != anchor || (anchor & 0x40) != 0) {
            badAnchor = true;
        }
        if ((anchor & 0x10) != 0) {
            if ((anchor & 0x22) != 0) {
                badAnchor = true;
            }
        } else if ((anchor & 0x20) != 0) {
            if ((anchor & 2) != 0) {
                badAnchor = true;
            } else {
                y_dst -= dH - 1;
            }
        } else if ((anchor & 2) != 0) {
            y_dst -= dH - 1 >>> 1;
        } else {
            badAnchor = true;
        }
        if ((anchor & 4) != 0) {
            if ((anchor & 9) != 0) {
                badAnchor = true;
            }
        } else if ((anchor & 8) != 0) {
            if ((anchor & 1) != 0) {
                badAnchor = true;
            } else {
                x_dst -= dW - 1;
            }
        } else if ((anchor & 1) != 0) {
            x_dst -= dW - 1 >>> 1;
        } else {
            badAnchor = true;
        }
        if (badAnchor) {
            throw new IllegalArgumentException("Bad Anchor");
        }
        AffineTransform savedT = this.g.getTransform();
        this.g.translate(x_dst, y_dst);
        this.g.transform(t);
        this.g.drawImage(img, 0, 0, width, height, x_src, y_src, x_src + width, y_src + height, null);
        this.g.setTransform(savedT);
    }

    public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
        if (rgbData == null) {
            throw new NullPointerException();
        }
        if (width == 0 || height == 0) {
            return;
        }
        int l = rgbData.length;
        if (width < 0 || height < 0 || offset < 0 || offset >= l || scanlength < 0 && scanlength * (height - 1) < 0 || scanlength >= 0 && scanlength * (height - 1) + width - 1 >= l) {
            throw new ArrayIndexOutOfBoundsException();
        }
        BufferedImage targetImage = (BufferedImage)((J2SEMutableImage)this.image).getImage();
        if (!processAlpha) {
            int[] rgb = new int[width * height];
            for (int row = 0; row < height; ++row) {
                for (int px = 0; px < width; ++px) {
                    rgb[row * width + px] = rgbData[offset + px] | 0xFF000000;
                }
                offset += scanlength;
            }
            targetImage.setRGB(x, y, width, height, rgb, 0, width);
        } else {
            targetImage.setRGB(x, y, width, height, rgbData, offset, scanlength);
        }
    }

    public void fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3) {
        int[] xPoints = new int[3];
        int[] yPoints = new int[3];
        xPoints[0] = x1;
        xPoints[1] = x2;
        xPoints[2] = x3;
        yPoints[0] = y1;
        yPoints[1] = y2;
        yPoints[2] = y3;
        this.g.fillPolygon(xPoints, yPoints, 3);
    }

    public void copyArea(int x_src, int y_src, int width, int height, int x_dest, int y_dest, int anchor) {
        if (width <= 0 || height <= 0) {
            return;
        }
        boolean badAnchor = false;
        if ((anchor & 0x7F) != anchor || (anchor & 0x40) != 0) {
            badAnchor = true;
        }
        if ((anchor & 0x10) != 0) {
            if ((anchor & 0x22) != 0) {
                badAnchor = true;
            }
        } else if ((anchor & 0x20) != 0) {
            if ((anchor & 2) != 0) {
                badAnchor = true;
            } else {
                y_dest -= height - 1;
            }
        } else if ((anchor & 2) != 0) {
            y_dest -= height - 1 >>> 1;
        } else {
            badAnchor = true;
        }
        if ((anchor & 4) != 0) {
            if ((anchor & 9) != 0) {
                badAnchor = true;
            }
        } else if ((anchor & 8) != 0) {
            if ((anchor & 1) != 0) {
                badAnchor = true;
            } else {
                x_dest -= width;
            }
        } else if ((anchor & 1) != 0) {
            x_dest -= width - 1 >>> 1;
        } else {
            badAnchor = true;
        }
        if (badAnchor) {
            throw new IllegalArgumentException("Bad Anchor");
        }
        this.g.copyArea(x_src, y_src, width, height, x_dest - x_src, y_dest - y_src);
    }

    public Graphics2D getGraphics() {
        return this.g;
    }
}

