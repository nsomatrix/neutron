/*
 * Decompiled with CFR 0.152.
 */
package org.microemu;

import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import org.microemu.DisplayAccess;

public abstract class MIDletAccess {
    public MIDlet midlet;
    private DisplayAccess displayAccess;

    public MIDletAccess(MIDlet amidlet) {
        this.midlet = amidlet;
    }

    public DisplayAccess getDisplayAccess() {
        return this.displayAccess;
    }

    public void setDisplayAccess(DisplayAccess adisplayAccess) {
        this.displayAccess = adisplayAccess;
    }

    public abstract void startApp() throws MIDletStateChangeException;

    public abstract void pauseApp();

    public abstract void destroyApp(boolean var1) throws MIDletStateChangeException;
}

