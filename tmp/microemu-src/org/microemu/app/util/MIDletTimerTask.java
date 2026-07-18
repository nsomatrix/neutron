/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import java.util.List;
import java.util.TimerTask;
import org.microemu.app.util.MIDletTimer;

public abstract class MIDletTimerTask
extends TimerTask {
    MIDletTimer timer;
    long time = -1L;
    long period;
    boolean oneTimeTaskExcecuted = false;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean cancel() {
        if (this.timer == null) {
            return false;
        }
        List list = this.timer.tasks;
        synchronized (list) {
            if (this.time == -1L) {
                return false;
            }
            if (this.oneTimeTaskExcecuted) {
                return false;
            }
            this.timer.tasks.remove(this);
        }
        return true;
    }

    public long scheduledExecutionTime() {
        return this.time;
    }
}

