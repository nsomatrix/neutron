package org.neutron.app;

import org.junit.Test;
import org.neutron.app.util.MIDletSystemProperties;

import static org.junit.Assert.*;

public class DevicePlatformTest {

    @Test
    public void testDevicePlatformDefaultAndModification() {
        String original = Config.getDevicePlatform();
        try {
            // Test modification
            Config.setDevicePlatform("Nokia6300/2.0");
            assertEquals("Nokia6300/2.0", Config.getDevicePlatform());
            assertEquals("Nokia6300/2.0", MIDletSystemProperties.getProperty("microedition.platform"));
            assertEquals("Nokia6300/2.0", System.getProperty("microedition.platform"));

            Common common = new Common(null);
            assertEquals("Nokia6300/2.0", common.getAppProperty("microedition.platform"));

            // Test reset to default
            Config.setDevicePlatform("Neutron");
            assertEquals("Neutron", Config.getDevicePlatform());
            assertEquals("Neutron", MIDletSystemProperties.getProperty("microedition.platform"));
        } finally {
            Config.setDevicePlatform(original);
        }
    }
}

