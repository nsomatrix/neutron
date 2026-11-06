package javax.microedition.lcdui;

import org.junit.Test;
import static org.junit.Assert.*;

public class DisplayTest {

    @Test
    public void testGetBestImageDimensions() {
        Display display = new Display();

        assertEquals(16, display.getBestImageWidth(Display.LIST_ELEMENT));
        assertEquals(16, display.getBestImageHeight(Display.LIST_ELEMENT));

        assertEquals(16, display.getBestImageWidth(Display.CHOICEGROUP_ELEMENT));
        assertEquals(16, display.getBestImageHeight(Display.CHOICEGROUP_ELEMENT));

        // Test alias constant backwards-compatibility
        assertEquals(16, display.getBestImageWidth(Display.CHOICE_GROUP_ELEMENT));
        assertEquals(16, display.getBestImageHeight(Display.CHOICE_GROUP_ELEMENT));

        assertEquals(32, display.getBestImageWidth(Display.ALERT));
        assertEquals(32, display.getBestImageHeight(Display.ALERT));
    }

    @Test
    public void testGetBestImageDimensionsInvalidType() {
        Display display = new Display();

        try {
            display.getBestImageWidth(0);
            fail("IllegalArgumentException expected for invalid imageType 0");
        } catch (IllegalArgumentException e) {
            // Expected
        }

        try {
            display.getBestImageHeight(99);
            fail("IllegalArgumentException expected for invalid imageType 99");
        } catch (IllegalArgumentException e) {
            // Expected
        }
    }
}

