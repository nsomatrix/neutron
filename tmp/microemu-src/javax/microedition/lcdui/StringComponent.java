/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import org.microemu.device.DeviceFactory;

class StringComponent {
    private String text;
    private int[] breaks = new int[4];
    private boolean invertPaint = false;
    private int numOfBreaks;
    private int width;
    private int widthDecreaser;

    public StringComponent() {
        this(null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public StringComponent(String text) {
        StringComponent stringComponent = this;
        synchronized (stringComponent) {
            this.width = -1;
            this.widthDecreaser = 0;
            this.setText(text);
        }
    }

    public int getCharHeight() {
        return Font.getDefaultFont().getHeight();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCharPositionX(int num) {
        StringComponent stringComponent = this;
        synchronized (stringComponent) {
            if (this.numOfBreaks == -1) {
                this.updateBreaks();
            }
            int prevIndex = 0;
            Font f = Font.getDefaultFont();
            for (int i = 0; i < this.numOfBreaks && num >= this.breaks[i]; ++i) {
                prevIndex = this.breaks[i];
            }
            return f.substringWidth(this.text, prevIndex, num - prevIndex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getCharPositionY(int num) {
        int y = 0;
        StringComponent stringComponent = this;
        synchronized (stringComponent) {
            if (this.numOfBreaks == -1) {
                this.updateBreaks();
            }
            Font f = Font.getDefaultFont();
            for (int i = 0; i < this.numOfBreaks && num >= this.breaks[i]; ++i) {
                y += f.getHeight();
            }
        }
        return y;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getHeight() {
        int height;
        StringComponent stringComponent = this;
        synchronized (stringComponent) {
            if (this.numOfBreaks == -1) {
                this.updateBreaks();
            }
            Font f = Font.getDefaultFont();
            if (this.text == null) {
                return 0;
            }
            if (this.numOfBreaks == 0) {
                return f.getHeight();
            }
            height = this.numOfBreaks * f.getHeight();
            if (this.breaks[this.numOfBreaks - 1] != this.text.length() - 1 || this.text.charAt(this.text.length() - 1) != '\n') {
                height += f.getHeight();
            }
        }
        return height;
    }

    public String getText() {
        return this.text;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void invertPaint(boolean state) {
        StringComponent stringComponent = this;
        synchronized (stringComponent) {
            this.invertPaint = state;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int paint(Graphics g) {
        int y;
        if (this.text == null) {
            return 0;
        }
        StringComponent stringComponent = this;
        synchronized (stringComponent) {
            if (this.numOfBreaks == -1) {
                this.updateBreaks();
            }
            Font f = Font.getDefaultFont();
            y = 0;
            int prevIndex = 0;
            for (int i = 0; i < this.numOfBreaks; ++i) {
                if (this.invertPaint) {
                    g.setGrayScale(0);
                } else {
                    g.setGrayScale(255);
                }
                g.fillRect(0, y, this.width, f.getHeight());
                if (this.invertPaint) {
                    g.setGrayScale(255);
                } else {
                    g.setGrayScale(0);
                }
                g.drawSubstring(this.text, prevIndex, this.breaks[i] - prevIndex, 0, y, 0);
                prevIndex = this.breaks[i];
                y += f.getHeight();
            }
            if (prevIndex != this.text.length() || this.text.length() == 0) {
                if (this.invertPaint) {
                    g.setGrayScale(0);
                } else {
                    g.setGrayScale(255);
                }
                g.fillRect(0, y, this.width, f.getHeight());
                if (this.invertPaint) {
                    g.setGrayScale(255);
                } else {
                    g.setGrayScale(0);
                }
                g.drawSubstring(this.text, prevIndex, this.text.length() - prevIndex, 0, y, 0);
                y += f.getHeight();
            }
        }
        return y;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setText(String text) {
        StringComponent stringComponent = this;
        synchronized (stringComponent) {
            this.text = text;
            this.numOfBreaks = -1;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setWidthDecreaser(int widthDecreaser) {
        StringComponent stringComponent = this;
        synchronized (stringComponent) {
            this.widthDecreaser = widthDecreaser;
            this.numOfBreaks = -1;
        }
    }

    private void insertBreak(int pos) {
        int i;
        for (i = 0; i < this.numOfBreaks && pos >= this.breaks[i]; ++i) {
        }
        if (this.numOfBreaks + 1 == this.breaks.length) {
            int[] newbreaks = new int[this.breaks.length + 4];
            System.arraycopy(this.breaks, 0, newbreaks, 0, this.numOfBreaks);
            this.breaks = newbreaks;
        }
        System.arraycopy(this.breaks, i, this.breaks, i + 1, this.numOfBreaks - i);
        this.breaks[i] = pos;
        ++this.numOfBreaks;
    }

    private void updateBreaks() {
        if (this.text == null) {
            return;
        }
        this.width = DeviceFactory.getDevice().getDeviceDisplay().getWidth() - this.widthDecreaser;
        int prevIndex = 0;
        int canBreak = 0;
        this.numOfBreaks = 0;
        Font f = Font.getDefaultFont();
        for (int i = 0; i < this.text.length(); ++i) {
            if (this.text.charAt(i) == ' ') {
                canBreak = i + 1;
            }
            if (this.text.charAt(i) == '\n') {
                this.insertBreak(i);
                canBreak = 0;
                prevIndex = i + 1;
                continue;
            }
            if (f.substringWidth(this.text, prevIndex, i - prevIndex + 1) <= this.width) continue;
            if (canBreak != 0) {
                this.insertBreak(canBreak);
                prevIndex = i = canBreak;
            } else {
                this.insertBreak(i);
                prevIndex = i + 1;
            }
            canBreak = 0;
        }
    }
}

