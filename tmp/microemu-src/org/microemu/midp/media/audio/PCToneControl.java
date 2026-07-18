/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.midp.media.audio;

import javax.microedition.media.control.ToneControl;

public class PCToneControl
implements ToneControl {
    public byte[] sequence;

    public void setSequence(byte[] sequence) {
        this.sequence = sequence;
    }

    public byte[] getSequence() {
        return this.sequence;
    }
}

