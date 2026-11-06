package javax.microedition.lcdui;

import org.junit.Test;

import static org.junit.Assert.*;

public class FontTest {

    @Test
    public void testValidFontCreationAndCaching() {
        Font font1 = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        assertNotNull(font1);
        assertEquals(Font.FACE_SYSTEM, font1.getFace());
        assertEquals(Font.STYLE_PLAIN, font1.getStyle());
        assertEquals(Font.SIZE_MEDIUM, font1.getSize());

        Font font2 = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        assertSame("Cached instance should be returned for identical attributes", font1, font2);

        Font fontCombo = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_BOLD | Font.STYLE_ITALIC, Font.SIZE_LARGE);
        assertTrue(fontCombo.isBold());
        assertTrue(fontCombo.isItalic());
        assertFalse(fontCombo.isUnderlined());
        assertFalse(fontCombo.isPlain());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStyle128ThrowsIllegalArgumentException() {
        Font.getFont(Font.FACE_SYSTEM, 128, Font.SIZE_MEDIUM);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidStyle8ThrowsIllegalArgumentException() {
        Font.getFont(Font.FACE_SYSTEM, 8, Font.SIZE_MEDIUM);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNegativeStyleThrowsIllegalArgumentException() {
        Font.getFont(Font.FACE_SYSTEM, -1, Font.SIZE_MEDIUM);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidFaceThrowsIllegalArgumentException() {
        Font.getFont(999, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidSizeThrowsIllegalArgumentException() {
        Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, 999);
    }

    @Test
    public void testFontEqualsAndHashCode() {
        Font defaultFont = Font.getDefaultFont();
        Font systemPlainMedium = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);

        // Before fix, Font lacked equals() so identity comparison failed if default wasn't same ref
        assertEquals(defaultFont, systemPlainMedium);
        assertEquals(defaultFont.hashCode(), systemPlainMedium.hashCode());

        Font boldFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
        assertNotEquals(defaultFont, boldFont);
    }
}

