/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.midp.media.audio;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

class PCToneHelper {
    protected byte[] playBuffer;
    private SourceDataLine sourceDataLine;
    private AudioInputStream audioInputStream;
    private AudioFormat audioFormat;

    public PCToneHelper(SourceDataLine sourceDataLine, AudioInputStream audioInputStream, AudioFormat audioFormat, int size) {
        this.playBuffer = new byte[size];
        this.setSourceDataLine(sourceDataLine);
        this.setAudioInputStream(audioInputStream);
        this.setAudioFormat(audioFormat);
    }

    public SourceDataLine getSourceDataLine() {
        return this.sourceDataLine;
    }

    public void setSourceDataLine(SourceDataLine sourceDataLine) {
        this.sourceDataLine = sourceDataLine;
    }

    public AudioInputStream getAudioInputStream() {
        return this.audioInputStream;
    }

    public void setAudioInputStream(AudioInputStream audioInputStream) {
        this.audioInputStream = audioInputStream;
    }

    public AudioFormat getAudioFormat() {
        return this.audioFormat;
    }

    public void setAudioFormat(AudioFormat audioFormat) {
        this.audioFormat = audioFormat;
    }
}

