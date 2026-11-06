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

        int[] lastRgbData;
        int lastRgbOffset, lastRgbScanlen, lastRgbX, lastRgbY, lastRgbWidth, lastRgbHeight;
        boolean lastRgbProcessAlpha;
        boolean drawRgbCalled = false;

        @Override
        public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
            this.lastRgbData = rgbData;
            this.lastRgbOffset = offset;
            this.lastRgbScanlen = scanlength;
            this.lastRgbX = x;
            this.lastRgbY = y;
            this.lastRgbWidth = width;
            this.lastRgbHeight = height;
            this.lastRgbProcessAlpha = processAlpha;
            this.drawRgbCalled = true;
        }

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

    @Test
    public void testDrawPixelsIntArrayARGB() {
        RecordingGraphics g = new RecordingGraphics();
        DirectGraphics dg = new DirectGraphicsImp(g);
        int[] pixels = new int[] { 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFFFF };

        dg.drawPixels(pixels, true, 0, 2, 10, 20, 2, 2, 0, DirectGraphics.TYPE_INT_8888_ARGB);

        assertTrue("drawRGB should have been called", g.drawRgbCalled);
        assertSame(pixels, g.lastRgbData);
        assertEquals(0, g.lastRgbOffset);
        assertEquals(2, g.lastRgbScanlen);
        assertEquals(10, g.lastRgbX);
        assertEquals(20, g.lastRgbY);
        assertEquals(2, g.lastRgbWidth);
        assertEquals(2, g.lastRgbHeight);
        assertTrue("Alpha processing should be enabled", g.lastRgbProcessAlpha);
    }

    @Test
    public void testDrawPixelsIntArrayRGB() {
        RecordingGraphics g = new RecordingGraphics();
        DirectGraphics dg = new DirectGraphicsImp(g);
        int[] pixels = new int[] { 0xFF0000, 0x00FF00, 0x0000FF, 0xFFFFFF };

        dg.drawPixels(pixels, false, 0, 2, 5, 5, 2, 2, 0, DirectGraphics.TYPE_INT_888_RGB);

        assertTrue("drawRGB should have been called", g.drawRgbCalled);
        assertFalse("Alpha processing should be disabled for RGB", g.lastRgbProcessAlpha);
    }

    @Test(expected = NullPointerException.class)
    public void testDrawPixelsNullArrayThrowsNPE() {
        RecordingGraphics g = new RecordingGraphics();
        DirectGraphics dg = new DirectGraphicsImp(g);
        dg.drawPixels((int[]) null, true, 0, 0, 0, 0, 10, 10, 0, DirectGraphics.TYPE_INT_8888_ARGB);
    }
}

