/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.midp.media.audio;

import java.util.EmptyStackException;
import java.util.Stack;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import org.microemu.midp.media.audio.PCToneRunnable;

public class PCToneRunnablePoolFactory {
    private static Stack pcToneRunnableStack = new Stack();

    private PCToneRunnablePoolFactory() {
    }

    public static synchronized PCToneRunnable getInstance(SourceDataLine sourceDataLine, AudioInputStream audioInputStream, AudioFormat audioFormat, int size) throws Exception {
        try {
            PCToneRunnable pcToneRunnable = (PCToneRunnable)pcToneRunnableStack.pop();
            pcToneRunnable.setSourceDataLine(sourceDataLine);
            pcToneRunnable.setAudioInputStream(audioInputStream);
            pcToneRunnable.setAudioFormat(audioFormat);
            return pcToneRunnable;
        }
        catch (EmptyStackException e) {
            return new PCToneRunnable(sourceDataLine, audioInputStream, audioFormat, size);
        }
    }

    public static synchronized void push(PCToneRunnable pcToneRunnable) {
        pcToneRunnableStack.push(pcToneRunnable);
    }
}

