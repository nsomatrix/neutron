/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.media.control;

import javax.microedition.media.Control;

public interface VolumeControl
extends Control {
    public void setMute(boolean var1);

    public boolean isMuted();

    public int setLevel(int var1);

    public int getLevel();
}

