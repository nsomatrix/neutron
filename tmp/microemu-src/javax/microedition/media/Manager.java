/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.media;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.media.MediaException;
import javax.microedition.media.MidiAudioPlayer;
import javax.microedition.media.Player;
import javax.microedition.media.SampledAudioPlayer;
import org.microemu.MIDletBridge;
import org.microemu.midp.media.audio.PCTone;

public final class Manager {
    public static final String TONE_DEVICE_LOCATOR = "device://tone";
    private static final PCTone pcTone = new PCTone();

    public static String[] getSupportedContentTypes(String protocol) {
        return new String[0];
    }

    public static String[] getSupportedProtocols(String content_type) {
        return new String[0];
    }

    public static Player createPlayer(String locator) throws IOException, MediaException {
        return null;
    }

    public static Player createPlayer(InputStream stream, String type) throws IOException, MediaException {
        if (type.equals("audio/x-wav") || type.equals("audio/basic") || type.equals("audio/mpeg")) {
            SampledAudioPlayer audPlayer = new SampledAudioPlayer();
            audPlayer.open(stream, type);
            MIDletBridge.addMediaPlayer(audPlayer);
            return audPlayer;
        }
        if (type.equals("audio/midi")) {
            MidiAudioPlayer midiPlayer = new MidiAudioPlayer();
            midiPlayer.open(stream, type);
            MIDletBridge.addMediaPlayer(midiPlayer);
            return midiPlayer;
        }
        return null;
    }

    public static synchronized void playTone(int frequency, int time, int volume) throws MediaException {
        pcTone.play(frequency, time, volume);
    }
}

