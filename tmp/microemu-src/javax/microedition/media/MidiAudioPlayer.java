/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.media;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Vector;
import javax.microedition.media.Control;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import javax.microedition.media.control.VolumeControl;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.Transmitter;
import org.microemu.MIDletBridge;

class MidiAudioPlayer
implements Player,
MetaEventListener {
    private int state;
    private Sequence sequence = null;
    private Sequencer sequencer = null;
    private Vector vListeners = null;
    private int iLoopCount = 1;

    MidiAudioPlayer() {
    }

    public boolean open(InputStream stream, String type) {
        try {
            this.sequencer = MidiSystem.getSequencer();
            this.sequencer.open();
            Synthesizer synth = MidiSystem.getSynthesizer();
            synth.open();
            Transmitter transmitter = this.sequencer.getTransmitter();
            Receiver receiver = synth.getReceiver();
            transmitter.setReceiver(receiver);
            this.sequence = MidiSystem.getSequence(stream);
            this.sequencer.setSequence(this.sequence);
            this.state = 100;
        }
        catch (UnsatisfiedLinkError e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
        catch (InvalidMidiDataException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addPlayerListener(PlayerListener playerListener) {
        if (this.vListeners == null) {
            this.vListeners = new Vector();
        }
        this.vListeners.add(playerListener);
    }

    public void close() {
        if (this.state == 0) {
            return;
        }
        MIDletBridge.removeMediaPlayer(this);
        if (this.sequencer != null) {
            this.sequencer.close();
        }
        this.state = 0;
    }

    public void deallocate() {
        this.state = 200;
    }

    public String getContentType() {
        return "audio/midi";
    }

    public long getDuration() {
        return 0L;
    }

    public long getMediaTime() {
        if (this.sequencer != null) {
            return this.sequencer.getMicrosecondPosition();
        }
        return 0L;
    }

    public int getState() {
        return this.state;
    }

    public void prefetch() throws MediaException {
        this.state = 300;
    }

    public void realize() throws MediaException {
        this.state = 200;
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
        this.iLoopCount = count;
    }

    public long setMediaTime(long now) throws MediaException {
        if (this.sequencer != null) {
            this.sequencer.setMicrosecondPosition(now);
        }
        return now;
    }

    public void start() throws MediaException {
        if (this.sequencer != null) {
            this.sequencer.addMetaEventListener(this);
            this.sequencer.start();
        }
        this.state = 400;
    }

    public void stop() throws MediaException {
        if (this.sequencer != null) {
            this.sequencer.stop();
        }
        this.state = 300;
    }

    public Control getControl(String controlType) {
        if (controlType.equals("VolumeControl")) {
            return new VolumeControl(){

                public int getLevel() {
                    return 0;
                }

                public boolean isMuted() {
                    return false;
                }

                public int setLevel(int level) {
                    return 0;
                }

                public void setMute(boolean mute) {
                }
            };
        }
        return null;
    }

    public Control[] getControls() {
        return null;
    }

    public void meta(MetaMessage event) {
        if (event.getType() == 47) {
            if (this.iLoopCount > 0) {
                --this.iLoopCount;
            }
            if (this.iLoopCount > 0 || this.iLoopCount == -1) {
                this.sequencer.setMicrosecondPosition(0L);
                try {
                    this.start();
                }
                catch (MediaException e) {
                    e.printStackTrace();
                }
            } else {
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
}

