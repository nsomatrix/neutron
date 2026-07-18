/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.lcdui;

import java.util.Hashtable;
import org.microemu.device.DeviceFactory;

public final class Font {
    public static final int STYLE_PLAIN = 0;
    public static final int STYLE_BOLD = 1;
    public static final int STYLE_ITALIC = 2;
    public static final int STYLE_UNDERLINED = 4;
    public static final int SIZE_SMALL = 8;
    public static final int SIZE_MEDIUM = 0;
    public static final int SIZE_LARGE = 16;
    public static final int FACE_SYSTEM = 0;
    public static final int FACE_MONOSPACE = 32;
    public static final int FACE_PROPORTIONAL = 64;
    public static final int FONT_STATIC_TEXT = 0;
    public static final int FONT_INPUT_TEXT = 1;
    private static final Font DEFAULT_FONT = new Font(0, 0, 0);
    private static Font[] fontsBySpecifier = new Font[]{DEFAULT_FONT, DEFAULT_FONT};
    private static Hashtable fonts = new Hashtable();
    private int face;
    private int style;
    private int size;
    private int baselinePosition = -1;
    private int height = -1;

    private Font(int face, int style, int size) {
        if (face != 0 && face != 32 && face != 64) {
            throw new IllegalArgumentException();
        }
        if (!(this.isPlain() || this.isBold() || this.isItalic() || this.isUnderlined())) {
            throw new IllegalArgumentException();
        }
        if (size != 8 && size != 0 && size != 16) {
            throw new IllegalArgumentException();
        }
        this.face = face;
        this.style = style;
        this.size = size;
    }

    public static Font getDefaultFont() {
        return DEFAULT_FONT;
    }

    public static Font getFont(int specifier) {
        if (specifier != 1 && specifier != 0) {
            throw new IllegalArgumentException("Bad specifier");
        }
        return fontsBySpecifier[specifier];
    }

    public static Font getFont(int face, int style, int size) {
        Integer key = new Integer(style + size + face);
        Font result = (Font)fonts.get(key);
        if (result == null) {
            result = new Font(face, style, size);
            fonts.put(key, result);
        }
        return result;
    }

    public int getStyle() {
        return this.style;
    }

    public int getSize() {
        return this.size;
    }

    public int getFace() {
        return this.face;
    }

    public boolean isPlain() {
        return this.style == 0;
    }

    public boolean isBold() {
        return (this.style & 1) != 0;
    }

    public boolean isItalic() {
        return (this.style & 2) != 0;
    }

    public boolean isUnderlined() {
        return (this.style & 4) != 0;
    }

    public int getHeight() {
        if (this.height == -1) {
            this.height = DeviceFactory.getDevice().getFontManager().getHeight(this);
        }
        return this.height;
    }

    public int getBaselinePosition() {
        if (this.baselinePosition == -1) {
            this.baselinePosition = DeviceFactory.getDevice().getFontManager().getBaselinePosition(this);
        }
        return this.baselinePosition;
    }

    public int charWidth(char ch) {
        return DeviceFactory.getDevice().getFontManager().charWidth(this, ch);
    }

    public int charsWidth(char[] ch, int offset, int length) {
        return DeviceFactory.getDevice().getFontManager().charsWidth(this, ch, offset, length);
    }

    public int stringWidth(String str) {
        return DeviceFactory.getDevice().getFontManager().stringWidth(this, str);
    }

    public int substringWidth(String str, int offset, int len) {
        return this.stringWidth(str.substring(offset, offset + len));
    }

    public int hashCode() {
        return this.style + this.size + this.face;
    }
}

