/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.midp.media.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import org.microemu.midp.media.RunnableInterface;
import org.microemu.midp.media.audio.PCToneHelper;

class PCToneRunnable
extends PCToneHelper
implements RunnableInterface {
    private boolean running;

    public PCToneRunnable(SourceDataLine sourceDataLine, AudioInputStream audioInputStream, AudioFormat audioFormat, int size) {
        super(sourceDataLine, audioInputStream, audioFormat, size);
    }

    public void init() throws Exception {
        this.getSourceDataLine().open(this.getAudioFormat());
        this.getSourceDataLine().start();
    }

    public void close() {
        this.getSourceDataLine().drain();
        this.getSourceDataLine().stop();
        this.getSourceDataLine().close();
    }

    public synchronized boolean isRunning() {
        return this.running;
    }

    public synchronized void setRunning(boolean running) {
        this.running = running;
    }

    public void run() {
        try {
            int cnt;
            this.setRunning(true);
            this.init();
            while ((cnt = this.getAudioInputStream().read(this.playBuffer, 0, this.playBuffer.length)) != -1) {
                if (cnt <= 0) continue;
                this.getSourceDataLine().write(this.playBuffer, 0, cnt);
            }
            this.close();
            this.setRunning(false);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

