/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.media;

import javax.microedition.media.Controllable;
import javax.microedition.media.MediaException;
import javax.microedition.media.PlayerListener;

public interface Player
extends Controllable {
    public static final int UNREALIZED = 100;
    public static final int REALIZED = 200;
    public static final int PREFETCHED = 300;
    public static final int STARTED = 400;
    public static final int CLOSED = 0;
    public static final long TIME_UNKNOWN = -1L;

    public void realize() throws MediaException;

    public void prefetch() throws MediaException;

    public void start() throws MediaException;

    public void stop() throws MediaException;

    public void deallocate();

    public void close();

    public long setMediaTime(long var1) throws MediaException;

    public long getMediaTime();

    public int getState();

    public long getDuration();

    public String getContentType();

    public void setLoopCount(int var1);

    public void addPlayerListener(PlayerListener var1);

    public void removePlayerListener(PlayerListener var1);
}

