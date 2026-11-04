package javax.microedition.media;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Test;

import static org.junit.Assert.*;

public class ManagerCreatePlayerTest {

    @Test
    public void testCreatePlayerWithNullTypeDoesNotThrowNPE() throws Exception {
        InputStream in = new ByteArrayInputStream(new byte[32]);
        try {
            Player p = Manager.createPlayer(in, null);
            // If undetectable, returning null is acceptable per specification, but must never throw NPE
        } catch (NullPointerException npe) {
            fail("Manager.createPlayer(stream, null) threw NullPointerException");
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCreatePlayerWithNullStreamThrowsIllegalArgumentException() throws Exception {
        Manager.createPlayer(null, "audio/x-wav");
    }

    @Test
    public void testCreatePlayerAutoDetectsMidiHeader() throws Exception {
        // Standard MIDI header: 'M', 'T', 'h', 'd' followed by length
        byte[] midiHeader = new byte[]{
                'M', 'T', 'h', 'd', 0, 0, 0, 6, 0, 0, 0, 1, 0, (byte) 120
        };
        InputStream in = new ByteArrayInputStream(midiHeader);
        try {
            Player player = Manager.createPlayer(in, null);
            assertNotNull("Auto-detected MIDI player should be created", player);
            assertEquals("audio/midi", player.getContentType());
        } catch (NullPointerException npe) {
            fail("Auto-detection threw NullPointerException");
        }
    }
}

