/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.midp.media.audio;

import org.microemu.midp.media.TimeBase;

class PCTimeBase
implements TimeBase {
    private static long timeBase = System.currentTimeMillis();

    PCTimeBase() {
    }

    public long getTime() {
        return System.currentTimeMillis() - timeBase;
    }
}

