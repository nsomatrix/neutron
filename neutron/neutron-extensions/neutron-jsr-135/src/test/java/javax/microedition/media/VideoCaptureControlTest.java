package javax.microedition.media;

import org.junit.Test;

import static org.junit.Assert.*;

public class VideoCaptureControlTest {

    @Test
    public void testGetDisplayWidthReturnsWidthNotHeight() throws Exception {
        VideoCaptureControl control = new VideoCaptureControl(null, "capture://video");

        // Initial defaults are width=160, height=100
        assertEquals("Initial display height should be 100", 100, control.getDisplayHeight());
        assertEquals("Initial display width should be 160", 160, control.getDisplayWidth());

        // Set distinct dimensions
        control.setDisplaySize(320, 240);
        assertEquals("Display height should be 240", 240, control.getDisplayHeight());
        assertEquals("Display width should be 320, not height", 320, control.getDisplayWidth());
    }
}

