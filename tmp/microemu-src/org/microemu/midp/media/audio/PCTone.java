/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.midp.media.audio;

import java.io.ByteArrayInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;
import org.microemu.midp.media.audio.PCToneRunnable;
import org.microemu.midp.media.audio.PCToneRunnablePoolFactory;

public class PCTone {
    private static final int MAX_TIME = 4;
    private static final int sampleSizeInBits = 16;
    private static final int channels = 1;
    private static final boolean signed = true;
    private static final boolean bigEndian = true;
    private static final int sampleRate = 16000;
    private static final AudioFormat audioFormat = new AudioFormat(16000.0f, 16, 1, true, true);
    private static final DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
    private final byte[] audioData = new byte[64000];

    public void play(double frequency, double duration, double volume) {
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.audioData);
            AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, this.audioData.length / audioFormat.getFrameSize());
            SourceDataLine sourceDataLine = (SourceDataLine)AudioSystem.getLine(dataLineInfo);
            double amplify = volume / 100.0;
            double d_sampleRate = 16000.0;
            int size = (int)(16000.0 * (duration / 1000.0) / 2.0);
            for (int index = 0; index < size; ++index) {
                double currentTime = (double)index / 16000.0;
                double sinValue = Math.sin(Math.PI * 2 * frequency * currentTime);
            }
            PCToneRunnable pcToneRunnable = PCToneRunnablePoolFactory.getInstance(sourceDataLine, audioInputStream, audioFormat, 64000);
            new Thread(pcToneRunnable).start();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

