package org.neutron.media.audio;

import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import org.junit.Test;

import static org.junit.Assert.*;

public class ToneGenerationTest {

    @Test
    public void testToneSampleGenerationProducesNonZeroPcmSineWave() {
        PCTone tone = new PCTone();
        byte[] pcm = tone.generateTone(440.0, 100.0, 80.0);
        assertNotNull(pcm);
        assertTrue(pcm.length > 0);

        boolean foundNonZero = false;
        for (int i = 0; i < pcm.length; i++) {
            if (pcm[i] != 0) {
                foundNonZero = true;
                break;
            }
        }
        assertTrue("Synthesized PCM audio buffer must contain non-zero audio samples", foundNonZero);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayToneNegativeDurationThrowsIllegalArgumentException() throws Exception {
        Manager.playTone(60, -10, 50);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayToneInvalidNoteThrowsIllegalArgumentException() throws Exception {
        Manager.playTone(150, 100, 50);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPlayToneInvalidVolumeThrowsIllegalArgumentException() throws Exception {
        Manager.playTone(60, 100, 200);
    }

    @Test
    public void testPlayToneValidNoteDoesNotThrow() throws Exception {
        Manager.playTone(69, 50, 50);
    }
}

