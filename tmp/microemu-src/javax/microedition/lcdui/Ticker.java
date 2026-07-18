/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import org.microemu.DisplayAccess;
import org.microemu.MIDletBridge;

public class Ticker {
    static int PAINT_TIMEOUT = 250;
    static int PAINT_MOVE = 5;
    static int PAINT_GAP = 10;
    Ticker instance = null;
    String text;
    int textPos = 0;
    int resetTextPosTo = -1;

    public Ticker(String str) {
        if (str == null) {
            throw new NullPointerException();
        }
        this.instance = this;
        this.text = str;
    }

    public String getString() {
        return this.text;
    }

    public void setString(String str) {
        if (str == null) {
            throw new NullPointerException();
        }
        this.text = str;
    }

    int getHeight() {
        return Font.getDefaultFont().getHeight();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    int paintContent(Graphics g) {
        Font f = Font.getDefaultFont();
        Ticker ticker = this.instance;
        synchronized (ticker) {
            int stringWidth = f.stringWidth(this.text) + PAINT_GAP;
            g.drawString(this.text, this.textPos, 0, 20);
            DisplayAccess da = MIDletBridge.getMIDletAccess().getDisplayAccess();
            for (int xPos = this.textPos + stringWidth; xPos < da.getCurrent().getWidth(); xPos += stringWidth) {
                g.drawString(this.text, xPos, 0, 20);
            }
            if (this.textPos + stringWidth < 0) {
                this.resetTextPosTo = this.textPos + stringWidth;
            }
        }
        return f.getHeight();
    }
}

