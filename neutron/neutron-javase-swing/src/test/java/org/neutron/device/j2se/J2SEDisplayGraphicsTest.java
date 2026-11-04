package org.neutron.device.j2se;

import org.junit.Test;
import static org.junit.Assert.*;

public class J2SEDisplayGraphicsTest {

    @Test
    public void testDrawRGBNegativeScanlengthSuccess() {
        J2SEGraphicsSurface surface = new J2SEGraphicsSurface(10, 10, false, 0);
        J2SEDisplayGraphics g = new J2SEDisplayGraphics(surface);

        int[] rgbData = new int[100];
        // Populate 10 rows: row 0 has value 10, row 9 has value 19
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                rgbData[r * 10 + c] = 0xFF000000 | (r + 1);
            }
        }

        // Draw with negative scanlength starting from row 9 (offset 90, scanlength -10)
        // Prior to fix, this unconditionally threw ArrayIndexOutOfBoundsException
        g.drawRGB(rgbData, 90, -10, 0, 0, 10, 10, false);

        int[] surfaceData = surface.getImageData();
        // The first row on the surface should now contain row 9 data (value 10)
        assertEquals(0xFF000000 | 10, surfaceData[0]);
        // The last row on the surface should contain row 0 data (value 1)
        assertEquals(0xFF000000 | 1, surfaceData[90]);
    }

    @Test
    public void testDrawRGBNegativeScanlengthUnderflowThrows() {
        J2SEGraphicsSurface surface = new J2SEGraphicsSurface(10, 10, false, 0);
        J2SEDisplayGraphics g = new J2SEDisplayGraphics(surface);

        int[] rgbData = new int[100];
        // Starting at offset 80 with scanlength -10 for 10 rows needs index 80 - 90 = -10 (underflow)
        try {
            g.drawRGB(rgbData, 80, -10, 0, 0, 10, 10, false);
            fail("Expected ArrayIndexOutOfBoundsException on negative scanlength buffer underflow");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }

    @Test
    public void testDrawRGBPositiveScanlengthOffsetOverflowThrows() {
        J2SEGraphicsSurface surface = new J2SEGraphicsSurface(10, 10, false, 0);
        J2SEDisplayGraphics g = new J2SEDisplayGraphics(surface);

        int[] rgbData = new int[100];
        // Offset 90 + 1 * 10 + 10 - 1 = 109 >= 100
        try {
            g.drawRGB(rgbData, 90, 10, 0, 0, 10, 2, false);
            fail("Expected ArrayIndexOutOfBoundsException on positive scanlength buffer overflow");
        } catch (ArrayIndexOutOfBoundsException e) {
            // Expected
        }
    }

    @Test
    public void testFilteredGraphicsDoesNotPolluteStaticColorCache() {
        java.awt.image.RGBImageFilter dummyGrayFilter = new java.awt.image.RGBImageFilter() {
            @Override
            public int filterRGB(int x, int y, int rgb) {
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (r + g + b) / 3;
                return (rgb & 0xFF000000) | (gray << 16) | (gray << 8) | gray;
            }
        };

        J2SEGraphicsSurface surface1 = new J2SEGraphicsSurface(10, 10, false, 0);
        J2SEDisplayGraphics filteredG = new J2SEDisplayGraphics(surface1);
        filteredG.setFilter(dummyGrayFilter);

        // Set red in filtered graphics
        filteredG.setColor(0x00FF0000);
        java.awt.Color filteredColor = filteredG.getAwtGraphics().getColor();
        // Grayscale conversion should balance R, G, B channels
        assertEquals(filteredColor.getRed(), filteredColor.getGreen());
        assertEquals(filteredColor.getGreen(), filteredColor.getBlue());

        // Now set red in an unfiltered graphics instance
        J2SEGraphicsSurface surface2 = new J2SEGraphicsSurface(10, 10, false, 0);
        J2SEDisplayGraphics unfilteredG = new J2SEDisplayGraphics(surface2);
        unfilteredG.setColor(0x00FF0000);
        java.awt.Color unfilteredColor = unfilteredG.getAwtGraphics().getColor();

        // Must remain pure red, not polluted by filtered instance
        assertEquals(255, unfilteredColor.getRed());
        assertEquals(0, unfilteredColor.getGreen());
        assertEquals(0, unfilteredColor.getBlue());
    }

    @Test
    public void testStaticCacheDoesNotOverrideFilteredGraphics() {
        java.awt.image.RGBImageFilter dummyGrayFilter = new java.awt.image.RGBImageFilter() {
            @Override
            public int filterRGB(int x, int y, int rgb) {
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (r + g + b) / 3;
                return (rgb & 0xFF000000) | (gray << 16) | (gray << 8) | gray;
            }
        };

        // First populate static cache with pure green
        J2SEGraphicsSurface surface1 = new J2SEGraphicsSurface(10, 10, false, 0);
        J2SEDisplayGraphics unfilteredG = new J2SEDisplayGraphics(surface1);
        unfilteredG.setColor(0x0000FF00);
        assertEquals(255, unfilteredG.getAwtGraphics().getColor().getGreen());
        assertEquals(0, unfilteredG.getAwtGraphics().getColor().getRed());

        // Now set green on filtered graphics - should NOT pull the unfiltered color from static cache
        J2SEGraphicsSurface surface2 = new J2SEGraphicsSurface(10, 10, false, 0);
        J2SEDisplayGraphics filteredG = new J2SEDisplayGraphics(surface2);
        filteredG.setFilter(dummyGrayFilter);
        filteredG.setColor(0x0000FF00);
        java.awt.Color filteredColor = filteredG.getAwtGraphics().getColor();

        assertEquals(filteredColor.getRed(), filteredColor.getGreen());
        assertEquals(filteredColor.getGreen(), filteredColor.getBlue());
    }

    @Test
    public void testDrawRegionAndDrawImageAnchorSynchronization() {
        java.awt.image.BufferedImage awtImage = new java.awt.image.BufferedImage(4, 4, java.awt.image.BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                awtImage.setRGB(x, y, 0xFFFF0000);
            }
        }
        J2SEImmutableImage midpImage = new J2SEImmutableImage(awtImage);

        // Test BOTTOM | RIGHT
        J2SEGraphicsSurface s1 = new J2SEGraphicsSurface(20, 20, false, 0);
        J2SEDisplayGraphics g1 = new J2SEDisplayGraphics(s1);
        g1.drawImage(midpImage, 15, 15, javax.microedition.lcdui.Graphics.BOTTOM | javax.microedition.lcdui.Graphics.RIGHT);

        J2SEGraphicsSurface s2 = new J2SEGraphicsSurface(20, 20, false, 0);
        J2SEDisplayGraphics g2 = new J2SEDisplayGraphics(s2);
        g2.drawRegion(midpImage, 0, 0, 4, 4, javax.microedition.lcdui.game.Sprite.TRANS_NONE,
                15, 15, javax.microedition.lcdui.Graphics.BOTTOM | javax.microedition.lcdui.Graphics.RIGHT);

        assertArrayEquals("drawImage and drawRegion output must match with BOTTOM | RIGHT",
                s1.getImageData(), s2.getImageData());

        // Test VCENTER | HCENTER
        J2SEGraphicsSurface s3 = new J2SEGraphicsSurface(20, 20, false, 0);
        J2SEDisplayGraphics g3 = new J2SEDisplayGraphics(s3);
        g3.drawImage(midpImage, 10, 10, javax.microedition.lcdui.Graphics.VCENTER | javax.microedition.lcdui.Graphics.HCENTER);

        J2SEGraphicsSurface s4 = new J2SEGraphicsSurface(20, 20, false, 0);
        J2SEDisplayGraphics g4 = new J2SEDisplayGraphics(s4);
        g4.drawRegion(midpImage, 0, 0, 4, 4, javax.microedition.lcdui.game.Sprite.TRANS_NONE,
                10, 10, javax.microedition.lcdui.Graphics.VCENTER | javax.microedition.lcdui.Graphics.HCENTER);

        assertArrayEquals("drawImage and drawRegion output must match with VCENTER | HCENTER",
                s3.getImageData(), s4.getImageData());
    }
}

