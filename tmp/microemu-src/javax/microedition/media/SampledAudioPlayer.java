/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.media;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import org.microemu.MIDletBridge;

class SampledAudioPlayer
implements Player,
LineListener {
    private AudioInputStream audioInputStream = null;
    private AudioInputStream decodedStream = null;
    private Clip clip = null;
    private Vector vListeners = null;
    private String strType = null;

    SampledAudioPlayer() {
    }

    public boolean open(InputStream stream, String type) {
        this.strType = type;
        try {
            this.audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(stream));
            AudioFormat format = this.audioInputStream.getFormat();
            if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) {
                AudioFormat baseFormat = this.audioInputStream.getFormat();
                AudioFormat decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, baseFormat.getSampleRate(), 16, baseFormat.getChannels(), baseFormat.getChannels() * 2, baseFormat.getSampleRate(), false);
                this.decodedStream = AudioSystem.getAudioInputStream(decodedFormat, this.audioInputStream);
                int frameLength = (int)this.decodedStream.getFrameLength();
                int frameSize = decodedFormat.getFrameSize();
                DataLine.Info info = new DataLine.Info(Clip.class, decodedFormat, frameLength * frameSize);
                this.clip = (Clip)AudioSystem.getLine(info);
                this.clip.open(this.decodedStream);
            } else {
                DataLine.Info info2 = new DataLine.Info(Clip.class, format, -1);
                this.clip = (Clip)AudioSystem.getLine(info2);
                this.clip.open(this.audioInputStream);
            }
        }
        catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            return false;
        }
        catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        catch (LineUnavailableException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void addPlayerListener(PlayerListener playerListener) {
        if (this.vListeners == null) {
            this.vListeners = new Vector();
        }
        this.vListeners.add(playerListener);
    }

    public void close() {
        MIDletBridge.removeMediaPlayer(this);
        if (this.clip != null) {
            this.clip.flush();
            this.clip.close();
        }
        try {
            if (this.decodedStream != null) {
                this.decodedStream.close();
            }
            if (this.audioInputStream != null) {
                this.audioInputStream.close();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deallocate() {
        if (this.clip != null) {
            this.clip.flush();
        }
    }

    public String getContentType() {
        return this.strType;
    }

    public long getDuration() {
        return 0L;
    }

    public long getMediaTime() {
        if (this.clip != null) {
            return this.clip.getMicrosecondPosition();
        }
        return 0L;
    }

    public int getState() {
        return 0;
    }

    public void prefetch() throws MediaException {
    }

    public void realize() throws MediaException {
    }

    public void removePlayerListener(PlayerListener playerListener) {
        if (this.vListeners == null) {
            return;
        }
        Iterator it = this.vListeners.iterator();
        while (it.hasNext()) {
            PlayerListener listener = (PlayerListener)it.next();
            if (listener != playerListener) continue;
            this.vListeners.remove(listener);
            break;
        }
    }

    public void setLoopCount(int count) {
        if (this.clip != null) {
            this.clip.loop(count);
        }
    }

    public long setMediaTime(long now) throws MediaException {
        if (this.clip != null) {
            this.clip.setMicrosecondPosition(now);
        }
        return 0L;
    }

    public void start() throws MediaException {
        if (this.clip != null) {
            this.clip.addLineListener(this);
            this.clip.start();
        }
    }

    public void stop() throws MediaException {
        if (this.clip != null) {
            this.clip.stop();
        }
    }

    public Control getControl(String controlType) {
        return null;
    }

    public Control[] getControls() {
        return null;
    }

    public void update(LineEvent event) {
        if (event.getType().equals(LineEvent.Type.STOP)) {
            this.close();
            if (this.vListeners != null) {
                Iterator it = this.vListeners.iterator();
                while (it.hasNext()) {
                    PlayerListener listener = (PlayerListener)it.next();
                    listener.playerUpdate(this, "endOfMedia", null);
                }
            }
        }
    }
}

