/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.midp.media;

import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.media.MediaException;
import javax.microedition.media.Player;
import javax.microedition.media.PlayerListener;
import org.microemu.midp.media.TimeBase;

public abstract class BasicPlayer
implements Player {
    public static String CONTROL_TYPE = "ToneControl";
    private int state;
    private int loopCount;
    private TimeBase timeBase;
    private Vector listenersVector;

    public BasicPlayer() {
        this.setListenersVector(new Vector());
        this.setLoopCount(0);
        this.setState(100);
    }

    public synchronized void addPlayerListener(PlayerListener playerListener) {
        this.getListenersVector().add(playerListener);
    }

    public void removePlayerListener(PlayerListener playerListener) {
        Enumeration enumeration = this.getListenersVector().elements();
        while (enumeration.hasMoreElements()) {
            PlayerListener listener = (PlayerListener)enumeration.nextElement();
            if (listener != playerListener) continue;
            this.getListenersVector().remove(listener);
            break;
        }
    }

    public int getState() {
        return this.state;
    }

    public synchronized void setState(int state) {
        this.state = state;
    }

    public long getDuration() {
        return 0L;
    }

    public long getMediaTime() {
        return 0L;
    }

    public TimeBase getTimeBase() {
        return this.timeBase;
    }

    public synchronized void setTimeBase(TimeBase timeBase) {
        this.timeBase = timeBase;
    }

    public void deallocate() {
    }

    public void prefetch() throws MediaException {
    }

    public void realize() throws MediaException {
    }

    public synchronized void setLoopCount(int count) {
        this.loopCount = count;
    }

    protected int getLoopCount() {
        return this.loopCount;
    }

    public synchronized long setMediaTime(long now) throws MediaException {
        return 0L;
    }

    protected Vector getListenersVector() {
        return this.listenersVector;
    }

    protected synchronized void setListenersVector(Vector listenersVector) {
        this.listenersVector = listenersVector;
    }

    public synchronized void start() throws MediaException {
        this.setState(400);
    }

    public synchronized void stop() throws MediaException {
        this.setState(300);
    }
}

