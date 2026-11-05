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

    @Test
    public void testCreatePlayerAutoDetectsAmrHeader() throws Exception {
        // AMR header: '#', '!', 'A', 'M', 'R', '\n'
        byte[] amrData = new byte[] {
            '#', '!', 'A', 'M', 'R', '\n',
            // One mode 0 frame (4.75 kbps): 1 header byte + 12 payload bytes
            0x04, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11
        };
        InputStream in = new ByteArrayInputStream(amrData);
        Player player = Manager.createPlayer(in, null);
        assertNotNull("Auto-detected AMR player should be created", player);
        assertEquals("audio/amr", player.getContentType());
        assertTrue("Duration should be at least 20000 us (1 frame)", player.getDuration() >= 20000L);

        // VolumeControl verification
        javax.microedition.media.control.VolumeControl vc = (javax.microedition.media.control.VolumeControl) player.getControl("VolumeControl");
        assertNotNull("VolumeControl should be non-null", vc);
        vc.setLevel(80);
        assertEquals(80, vc.getLevel());

        // Lifecycle verification
        player.realize();
        assertEquals(Player.REALIZED, player.getState());
        player.prefetch();
        assertEquals(Player.PREFETCHED, player.getState());
        player.start();
        assertEquals(Player.STARTED, player.getState());
        player.stop();
        assertEquals(Player.PREFETCHED, player.getState());
        player.close();
        assertEquals(Player.CLOSED, player.getState());
    }

    @Test
    public void testCreatePlayerWithAmrMimeType() throws Exception {
        byte[] dummy = new byte[] { '#', '!', 'A', 'M', 'R', '\n' };
        Player p1 = Manager.createPlayer(new ByteArrayInputStream(dummy), "audio/amr");
        assertNotNull("audio/amr player should be created", p1);

        Player p2 = Manager.createPlayer(new ByteArrayInputStream(dummy), "audio/amr-nb");
        assertNotNull("audio/amr-nb player should be created", p2);

        Player p3 = Manager.createPlayer(new ByteArrayInputStream(dummy), "audio/3gpp");
        assertNotNull("audio/3gpp player should be created", p3);
    }

    @Test
    public void testCreatePlayerAutoDetectsMp3Header() throws Exception {
        byte[] id3Header = new byte[] { 'I', 'D', '3', 3, 0, 0, 0, 0, 0, 10, 0, 0, 0, 0 };
        Player p1 = Manager.createPlayer(new ByteArrayInputStream(id3Header), null);
        assertNotNull("Auto-detected MP3 player should be created", p1);
        assertEquals("audio/mpeg", p1.getContentType());
        assertTrue(p1.getDuration() > 0);

        byte[] mpegSync = new byte[] { (byte) 0xFF, (byte) 0xFB, (byte) 0x90, 0x64, 0, 0, 0, 0 };
        Player p2 = Manager.createPlayer(new ByteArrayInputStream(mpegSync), null);
        assertNotNull("Auto-detected MPEG sync player should be created", p2);
        assertEquals("audio/mpeg", p2.getContentType());
    }

    @Test
    public void testCreatePlayerAutoDetectsSmafHeader() throws Exception {
        byte[] smafData = new byte[] { 'M', 'M', 'M', 'D', 0, 0, 0, 8, 0, 0, 0, 0 };
        Player p = Manager.createPlayer(new ByteArrayInputStream(smafData), null);
        assertNotNull("Auto-detected SMAF player should be created", p);
        assertEquals("audio/x-smaf", p.getContentType());
    }
}
