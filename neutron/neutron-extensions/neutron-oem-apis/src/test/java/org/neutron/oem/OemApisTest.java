package org.neutron.oem;

import org.junit.Test;
import static org.junit.Assert.*;

public class OemApisTest {

    @Test
    public void testSamsungApis() {
        assertTrue(com.samsung.util.Vibration.isSupported());
        com.samsung.util.Vibration.start(500, 50);
        com.samsung.util.Vibration.stop();

        assertTrue(com.samsung.util.LCDLight.isSupported());
        com.samsung.util.LCDLight.on(1000);
        com.samsung.util.LCDLight.off();

        com.samsung.util.AudioClip clip = new com.samsung.util.AudioClip(com.samsung.util.AudioClip.TYPE_MIDI, new byte[0], 0, 0);
        clip.play(1, 100);
        clip.stop();
    }

    @Test
    public void testSonyEricssonApis() {
        com.sonyericsson.util.Vibration.start(500, 50);
        com.sonyericsson.util.Vibration.stop();

        com.sonyericsson.ui.Backlight.on();
        com.sonyericsson.ui.Backlight.off();
        com.sonyericsson.ui.Backlight.blink(1000, 200);

        com.sonyericsson.media.Sound sound = new com.sonyericsson.media.Sound(new byte[0], 0, 0);
        sound.setVolume(75);
        assertEquals(75, sound.getVolume());
        sound.play();
        sound.stop();
    }

    @Test
    public void testMotorolaApis() {
        assertNotNull(com.motorola.funlight.FunLight.getRegions());
        com.motorola.funlight.FunLight.setColor(1, 0xFF0000);
        com.motorola.funlight.FunLight.setControl(1, com.motorola.funlight.FunLight.ON);
        com.motorola.funlight.FunLight.setControl(1, com.motorola.funlight.FunLight.OFF);

        assertTrue(com.motorola.multimedia.Lighting.isSupported());
        com.motorola.multimedia.Lighting.backlightOn();
        com.motorola.multimedia.Lighting.backlightOff();

        com.motorola.multimedia.Vibrator.startVibrate();
        com.motorola.multimedia.Vibrator.stopVibrate();
    }
}

