/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.j2se;

import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.lcdui.Font;
import org.microemu.device.impl.FontManagerImpl;
import org.microemu.device.j2se.J2SEFont;
import org.microemu.device.j2se.J2SESystemFont;
import org.microemu.device.j2se.J2SETrueTypeFont;

public class J2SEFontManager
implements FontManagerImpl {
    private static String FACE_SYSTEM_NAME = "SansSerif";
    private static String FACE_MONOSPACE_NAME = "Monospaced";
    private static String FACE_PROPORTIONAL_NAME = "SansSerif";
    private static int SIZE_SMALL = 9;
    private static int SIZE_MEDIUM = 11;
    private static int SIZE_LARGE = 13;
    private Hashtable fonts = new Hashtable();
    private boolean antialiasing;

    org.microemu.device.impl.Font getFont(Font meFont) {
        int key = 0;
        key |= meFont.getFace();
        key |= meFont.getStyle();
        org.microemu.device.impl.Font result = (org.microemu.device.impl.Font)this.fonts.get(new Integer(key |= meFont.getSize()));
        if (result == null) {
            String name = null;
            if (meFont.getFace() == 0) {
                name = FACE_SYSTEM_NAME;
            } else if (meFont.getFace() == 32) {
                name = FACE_MONOSPACE_NAME;
            } else if (meFont.getFace() == 64) {
                name = FACE_PROPORTIONAL_NAME;
            }
            String style = ",";
            if ((meFont.getStyle() & 0) != 0) {
                style = style + "plain,";
            }
            if ((meFont.getStyle() & 1) != 0) {
                style = style + "bold,";
            }
            if ((meFont.getStyle() & 2) != 0) {
                style = style + "italic,";
            }
            if ((meFont.getStyle() & 2) != 0) {
                style = style + "underlined,";
            }
            style = style.substring(0, style.length() - 1);
            int size = 0;
            if (meFont.getSize() == 8) {
                size = SIZE_SMALL;
            } else if (meFont.getSize() == 0) {
                size = SIZE_MEDIUM;
            } else if (meFont.getSize() == 16) {
                size = SIZE_LARGE;
            }
            result = new J2SESystemFont(name, style, size, this.antialiasing);
            this.fonts.put(new Integer(key), result);
        }
        return result;
    }

    public void init() {
        this.fonts.clear();
    }

    public int charWidth(Font f, char ch) {
        return this.getFont(f).charWidth(ch);
    }

    public int charsWidth(Font f, char[] ch, int offset, int length) {
        return this.getFont(f).charsWidth(ch, offset, length);
    }

    public int getBaselinePosition(Font f) {
        return this.getFont(f).getBaselinePosition();
    }

    public int getHeight(Font f) {
        return this.getFont(f).getHeight();
    }

    public int stringWidth(Font f, String str) {
        return this.getFont(f).stringWidth(str);
    }

    public boolean getAntialiasing() {
        return this.antialiasing;
    }

    public void setAntialiasing(boolean antialiasing) {
        this.antialiasing = antialiasing;
        Enumeration en = this.fonts.elements();
        while (en.hasMoreElements()) {
            J2SEFont font = (J2SEFont)en.nextElement();
            font.setAntialiasing(antialiasing);
        }
    }

    public void setFont(String face, String style, String size, org.microemu.device.impl.Font font) {
        int key = 0;
        if (face.equalsIgnoreCase("system")) {
            key |= 0;
        } else if (face.equalsIgnoreCase("monospace")) {
            key |= 0x20;
        } else if (face.equalsIgnoreCase("proportional")) {
            key |= 0x40;
        }
        String testStyle = style.toLowerCase();
        if (testStyle.indexOf("plain") != -1) {
            key |= 0;
        }
        if (testStyle.indexOf("bold") != -1) {
            key |= 1;
        }
        if (testStyle.indexOf("italic") != -1) {
            key |= 2;
        }
        if (testStyle.indexOf("underlined") != -1) {
            key |= 4;
        }
        if (size.equalsIgnoreCase("small")) {
            key |= 8;
        } else if (size.equalsIgnoreCase("medium")) {
            key |= 0;
        } else if (size.equalsIgnoreCase("large")) {
            key |= 0x10;
        }
        this.fonts.put(new Integer(key), font);
    }

    public org.microemu.device.impl.Font createSystemFont(String defName, String defStyle, int defSize, boolean antialiasing) {
        return new J2SESystemFont(defName, defStyle, defSize, antialiasing);
    }

    public org.microemu.device.impl.Font createTrueTypeFont(URL defUrl, String defStyle, int defSize, boolean antialiasing) {
        return new J2SETrueTypeFont(defUrl, defStyle, defSize, antialiasing);
    }
}

