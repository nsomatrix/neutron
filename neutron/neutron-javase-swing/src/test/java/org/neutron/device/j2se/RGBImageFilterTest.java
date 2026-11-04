package org.neutron.device.j2se;

import org.junit.Test;
import org.neutron.device.impl.Color;

import static org.junit.Assert.*;

public class RGBImageFilterTest {

    @Test
    public void testIndependentChannelSignsForGreenAndBlue() {
        // Red has Rr < 0 (fg=50, bg=150 -> Rr = -100)
        // Green has Rg > 0 (fg=200, bg=100 -> Rg = +100)
        // Blue has Rb < 0 (fg=50, bg=150 -> Rb = -100)
        Color bg = new Color((150 << 16) | (100 << 8) | 150);
        Color fg = new Color((50 << 16) | (200 << 8) | 50);

        RGBImageFilter filter = new RGBImageFilter(bg, fg);

        // Test input with pure green (r=0, g=255, b=0)
        int inColor = 0xFF00FF00;
        int outColor = filter.filterRGB(0, 0, inColor);

        int outR = (outColor >> 16) & 0xFF;
        int outG = (outColor >> 8) & 0xFF;
        int outB = outColor & 0xFF;

        // With Rg > 0 and g=255: g = (255 * 100) / 255 + 100 = 200
        // Prior to bugfix, checking Rr > 0 caused else branch execution: (255 * -100) / 255 + 200 = 100
        assertEquals("Green channel must be 200", 200, outG);
        assertEquals("Red channel must match foreground red", 50, outR);
        assertEquals("Blue channel must match foreground blue", 50, outB);

        // Test when Rb > 0 and Rr < 0
        Color bg2 = new Color((150 << 16) | (150 << 8) | 100);
        Color fg2 = new Color((50 << 16) | (50 << 8) | 200);
        RGBImageFilter filter2 = new RGBImageFilter(bg2, fg2);

        int inBlue = 0xFF0000FF;
        int outBlueColor = filter2.filterRGB(0, 0, inBlue);
        int outBlueB = outBlueColor & 0xFF;

        // With Rb > 0 and b=255: b = (255 * 100) / 255 + 100 = 200
        assertEquals("Blue channel must be 200", 200, outBlueB);
    }
}

