package org.neutron.app.ui.swing;

import java.awt.image.BufferedImage;
import org.junit.Test;
import org.neutron.device.j2se.J2SEGraphicsSurface;

import static org.junit.Assert.*;

public class SwingDisplayComponentTest {

    @Test
    public void testGetCompatibleImageTypeForCustomImages() {
        java.awt.color.ColorSpace cs = java.awt.color.ColorSpace.getInstance(java.awt.color.ColorSpace.CS_sRGB);
        
        // Opaque custom BufferedImage
        java.awt.image.ColorModel cmOpaque = new java.awt.image.ComponentColorModel(
                cs, false, false, java.awt.Transparency.OPAQUE, java.awt.image.DataBuffer.TYPE_BYTE);
        java.awt.image.WritableRaster wrOpaque = cmOpaque.createCompatibleWritableRaster(10, 10);
        BufferedImage customOpaqueImg = new BufferedImage(cmOpaque, wrOpaque, false, null);
        assertEquals(BufferedImage.TYPE_CUSTOM, customOpaqueImg.getType());
        assertEquals("Compatible type for opaque custom image should be TYPE_INT_RGB",
                BufferedImage.TYPE_INT_RGB,
                SwingDisplayComponent.getCompatibleImageType(customOpaqueImg));

        // Translucent custom BufferedImage
        java.awt.image.ColorModel cmAlpha = new java.awt.image.ComponentColorModel(
                cs, true, false, java.awt.Transparency.TRANSLUCENT, java.awt.image.DataBuffer.TYPE_BYTE);
        java.awt.image.WritableRaster wrAlpha = cmAlpha.createCompatibleWritableRaster(10, 10);
        BufferedImage customAlphaImg = new BufferedImage(cmAlpha, wrAlpha, false, null);
        assertEquals(BufferedImage.TYPE_CUSTOM, customAlphaImg.getType());
        assertEquals("Compatible type for alpha custom image should be TYPE_INT_ARGB",
                BufferedImage.TYPE_INT_ARGB,
                SwingDisplayComponent.getCompatibleImageType(customAlphaImg));

        // Verify that custom images can be safely constructed without IllegalArgumentException
        int compatibleType = SwingDisplayComponent.getCompatibleImageType(customAlphaImg);
        BufferedImage constructed = new BufferedImage(10, 10, compatibleType);
        assertNotNull(constructed);
    }

    @Test
    public void testGetScale2xImageDoesNotCrashOnCustomImageType() {
        SwingDisplayComponent component = new SwingDisplayComponent(null);
        java.awt.color.ColorSpace cs = java.awt.color.ColorSpace.getInstance(java.awt.color.ColorSpace.CS_sRGB);
        java.awt.image.ColorModel cm = new java.awt.image.ComponentColorModel(
                cs, false, false, java.awt.Transparency.OPAQUE, java.awt.image.DataBuffer.TYPE_BYTE);
        java.awt.image.WritableRaster wr = cm.createCompatibleWritableRaster(10, 10);
        BufferedImage customImg = new BufferedImage(cm, wr, false, null);
        assertEquals(BufferedImage.TYPE_CUSTOM, customImg.getType());

        // Prior to the fix, this threw IllegalArgumentException: Unknown image type 0
        BufferedImage scale2x = component.getScale2xImage(customImg, new int[100]);

        assertNotNull("Scale2x image must not be null", scale2x);
        assertEquals(20, scale2x.getWidth());
        assertEquals(20, scale2x.getHeight());
        assertNotEquals("Target image type must not be TYPE_CUSTOM", BufferedImage.TYPE_CUSTOM, scale2x.getType());
    }
}

