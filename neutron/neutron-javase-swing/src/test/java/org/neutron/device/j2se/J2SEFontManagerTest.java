package org.neutron.device.j2se;

import javax.microedition.lcdui.Font;
import org.junit.Test;
import static org.junit.Assert.*;

public class J2SEFontManagerTest {

    @Test
    public void testItalicDoesNotSetUnderlined() {
        J2SEFontManager manager = new J2SEFontManager();
        Font meFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_MEDIUM);

        J2SESystemFont sysFont = (J2SESystemFont) manager.getFont(meFont);
        assertNotNull(sysFont);
        assertTrue("Style must contain 'italic'", sysFont.getStyle().contains("italic"));
        assertFalse("Style must NOT contain 'underlined' for pure italic font", sysFont.getStyle().contains("underlined"));
    }

    @Test
    public void testUnderlinedSetsUnderlinedAndNotItalic() {
        J2SEFontManager manager = new J2SEFontManager();
        Font meFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_UNDERLINED, Font.SIZE_MEDIUM);

        J2SESystemFont sysFont = (J2SESystemFont) manager.getFont(meFont);
        assertNotNull(sysFont);
        assertTrue("Style must contain 'underlined'", sysFont.getStyle().contains("underlined"));
        assertFalse("Style must NOT contain 'italic' for pure underlined font", sysFont.getStyle().contains("italic"));
    }

    @Test
    public void testItalicAndUnderlinedCombined() {
        J2SEFontManager manager = new J2SEFontManager();
        Font meFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC | Font.STYLE_UNDERLINED, Font.SIZE_MEDIUM);

        J2SESystemFont sysFont = (J2SESystemFont) manager.getFont(meFont);
        assertNotNull(sysFont);
        assertTrue("Style must contain 'italic'", sysFont.getStyle().contains("italic"));
        assertTrue("Style must contain 'underlined'", sysFont.getStyle().contains("underlined"));
    }
}

