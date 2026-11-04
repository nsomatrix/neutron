package com.nokia.mid.ui;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import org.junit.Test;
import static org.junit.Assert.*;

public class DirectGraphicsTest {

    static class RecordingGraphics extends Graphics {
        Image lastImg;
        int lastXSrc, lastYSrc, lastWidth, lastHeight, lastTransform, lastXDst, lastYDst, lastAnchor;
        boolean drawRegionCalled = false;

        @Override
        public void drawRegion(Image src, int x_src, int y_src, int width, int height,
                               int transform, int x_dst, int y_dst, int anchor) {
            this.lastImg = src;
            this.lastXSrc = x_src;
            this.lastYSrc = y_src;
            this.lastWidth = width;
            this.lastHeight = height;
            this.lastTransform = transform;
            this.lastXDst = x_dst;
            this.lastYDst = y_dst;
            this.lastAnchor = anchor;
            this.drawRegionCalled = true;
        }
    }

    static class StubImage extends Image {
        private final int w;
        private final int h;

        StubImage(int w, int h) {
            this.w = w;
            this.h = h;
        }

        @Override
        public int getWidth() {
            return w;
        }

        @Override
        public int getHeight() {
            return h;
        }
    }

    @Test
    public void testDrawImageZeroManipulationAndCoordinates() {
        RecordingGraphics g = new RecordingGraphics();
        DirectGraphics dg = new DirectGraphicsImp(g);
        StubImage img = new StubImage(50, 60);

        int anchor = Graphics.TOP | Graphics.LEFT;
        dg.drawImage(img, 15, 25, anchor, 0);

        assertTrue("drawRegion should have been invoked", g.drawRegionCalled);
        assertSame(img, g.lastImg);
        assertEquals("Source X must be 0", 0, g.lastXSrc);
        assertEquals("Source Y must be 0", 0, g.lastYSrc);
        assertEquals("Width must match image width", 50, g.lastWidth);
        assertEquals("Height must match image height", 60, g.lastHeight);
        assertEquals("Destination X must match passed x", 15, g.lastXDst);
        assertEquals("Destination Y must match passed y", 25, g.lastYDst);
        assertEquals("Anchor must match", anchor, g.lastAnchor);
        assertEquals("Manipulation 0 must map to TRANS_NONE", Sprite.TRANS_NONE, g.lastTransform);
    }

    @Test
    public void testDrawImageManipulations() {
        RecordingGraphics g = new RecordingGraphics();
        DirectGraphics dg = new DirectGraphicsImp(g);
        StubImage img = new StubImage(40, 40);

        // FLIP_HORIZONTAL -> TRANS_MIRROR
        dg.drawImage(img, 0, 0, 0, DirectGraphics.FLIP_HORIZONTAL);
        assertEquals(Sprite.TRANS_MIRROR, g.lastTransform);

        // FLIP_VERTICAL -> TRANS_MIRROR_ROT180
        dg.drawImage(img, 0, 0, 0, DirectGraphics.FLIP_VERTICAL);
        assertEquals(Sprite.TRANS_MIRROR_ROT180, g.lastTransform);

        // ROTATE_90 -> TRANS_ROT90
        dg.drawImage(img, 0, 0, 0, DirectGraphics.ROTATE_90);
        assertEquals(Sprite.TRANS_ROT90, g.lastTransform);

        // ROTATE_180 -> TRANS_ROT180
        dg.drawImage(img, 0, 0, 0, DirectGraphics.ROTATE_180);
        assertEquals(Sprite.TRANS_ROT180, g.lastTransform);

        // ROTATE_270 -> TRANS_ROT270
        dg.drawImage(img, 0, 0, 0, DirectGraphics.ROTATE_270);
        assertEquals(Sprite.TRANS_ROT270, g.lastTransform);

        // Combination: FLIP_HORIZONTAL | ROTATE_90 -> TRANS_MIRROR_ROT90
        dg.drawImage(img, 0, 0, 0, DirectGraphics.FLIP_HORIZONTAL | DirectGraphics.ROTATE_90);
        assertEquals(Sprite.TRANS_MIRROR_ROT90, g.lastTransform);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDrawImageInvalidManipulationThrows() {
        RecordingGraphics g = new RecordingGraphics();
        DirectGraphics dg = new DirectGraphicsImp(g);
        StubImage img = new StubImage(10, 10);
        dg.drawImage(img, 0, 0, 0, 12345);
    }
}

