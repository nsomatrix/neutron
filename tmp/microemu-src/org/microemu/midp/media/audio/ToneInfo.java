/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.midp.media.audio;

public class ToneInfo {
    private int sleepDelay;
    private int frequency;
    private int lengthOfTime;
    private int volume;

    public ToneInfo() {
        this.setVolume(100);
    }

    public int getSleepDelay() {
        return this.sleepDelay;
    }

    public void setSleepDelay(int sleepDelay) {
        this.sleepDelay = sleepDelay;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public int getLengthOfTime() {
        return this.lengthOfTime;
    }

    public void setLengthOfTime(int lengthOfTime) {
        this.lengthOfTime = lengthOfTime;
    }

    public int getVolume() {
        return this.volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public String toString() {
        return "Frequency: " + this.getFrequency() + " LengthOfTime: " + this.getLengthOfTime() + " SleepDelay: " + this.getSleepDelay() + " Volume: " + this.getVolume();
    }
}

