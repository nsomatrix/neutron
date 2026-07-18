/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.midp.media.audio;

import javax.microedition.media.Control;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import org.microemu.midp.media.BasicPlayer;
import org.microemu.midp.media.RunnableInterface;
import org.microemu.midp.media.audio.PCTimeBase;
import org.microemu.midp.media.audio.PCToneControl;
import org.microemu.midp.media.audio.ToneInfo;

public class PCTonePlayer
extends BasicPlayer
implements RunnableInterface {
    private PCToneControl pcToneControl;
    private int sequenceIndex;
    private boolean running;
    private ToneInfo toneInfo;

    public PCTonePlayer() {
        try {
            this.pcToneControl = new PCToneControl();
            this.setTimeBase(new PCTimeBase());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
    }

    public String getContentType() {
        return null;
    }

    public synchronized void start() throws MediaException {
        try {
            new Thread(this).start();
            super.start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void stop() throws MediaException {
        try {
            this.setRunning(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Control getControl(String controlType) {
        if (controlType.equals(CONTROL_TYPE)) {
            return this.pcToneControl;
        }
        return null;
    }

    public Control[] getControls() {
        return null;
    }

    public boolean isRunning() {
        return this.running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    private byte getNext() {
        return this.pcToneControl.sequence[this.sequenceIndex++];
    }

    public void playBlock() throws Exception {
        byte currentByte = this.getNext();
        while (this.sequenceIndex < this.pcToneControl.sequence.length && (currentByte = this.getNext()) != -6) {
            if (currentByte == -1) {
                Thread.sleep(this.toneInfo.getSleepDelay());
                continue;
            }
            if (currentByte == -7) {
                this.getNext();
                continue;
            }
            double noteDelta = currentByte - 69;
            double power = noteDelta / 12.0;
            double d_frequency = 440.0 * Math.pow(2.0, power);
            this.toneInfo.setFrequency((int)d_frequency);
            Manager.playTone(this.toneInfo.getFrequency(), this.toneInfo.getLengthOfTime(), this.toneInfo.getVolume());
            Thread.sleep(this.toneInfo.getLengthOfTime() + 20);
        }
        if (currentByte == -6) {
            this.getNext();
        }
    }

    public void run() {
        try {
            this.sequenceIndex = 0;
            this.toneInfo = new ToneInfo();
            this.setRunning(true);
            while (this.isRunning() && this.sequenceIndex < this.pcToneControl.sequence.length) {
                byte currentControlCommand = this.getNext();
                if (currentControlCommand == -2) {
                    byte version = this.getNext();
                    continue;
                }
                if (currentControlCommand == -8) {
                    this.toneInfo.setVolume(this.getNext());
                    continue;
                }
                if (currentControlCommand == -3) {
                    byte tempo = this.getNext();
                    double resolutionDenominator = 64.0;
                    double durationOfNote = 240.0 / (1.0 / resolutionDenominator * (double)tempo);
                    this.toneInfo.setLengthOfTime((int)durationOfNote / 16);
                    continue;
                }
                if (currentControlCommand == -1) {
                    this.toneInfo.setSleepDelay(this.getNext());
                    Thread.sleep(this.toneInfo.getSleepDelay());
                    continue;
                }
                if (currentControlCommand != -7 && currentControlCommand != -5) continue;
                this.playBlock();
            }
            super.stop();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }
}

