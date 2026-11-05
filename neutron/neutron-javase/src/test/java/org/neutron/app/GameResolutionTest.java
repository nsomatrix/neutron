package org.neutron.app;

import java.awt.Dimension;
import org.junit.Test;
import static org.junit.Assert.*;

public class GameResolutionTest {

    @Test
    public void testAutoDetectResolutionConfig() {
        boolean original = Config.isAutoDetectResolutionEnabled();
        try {
            Config.setAutoDetectResolutionEnabled(false);
            assertFalse(Config.isAutoDetectResolutionEnabled());

            Config.setAutoDetectResolutionEnabled(true);
            assertTrue(Config.isAutoDetectResolutionEnabled());
        } finally {
            Config.setAutoDetectResolutionEnabled(original);
        }
    }

    @Test
    public void testOriginalDisplaySizeParser() {
        Common common = new Common(null);
        // Without JAD, returns null safely
        assertNull(common.getOriginalDisplaySize());

        // With mock attributes in JAD
        common.jad.getMainAttributes().putValue("Nokia-MIDlet-Original-Display-Size", "240,320");
        Dimension dim = common.getOriginalDisplaySize();
        assertNotNull(dim);
        assertEquals(240, dim.width);
        assertEquals(320, dim.height);

        // Test alternate format 176x220
        common.jad.getMainAttributes().putValue("Nokia-MIDlet-Original-Display-Size", "176x220");
        Dimension dim2 = common.getOriginalDisplaySize();
        assertNotNull(dim2);
        assertEquals(176, dim2.width);
        assertEquals(220, dim2.height);
    }
}
