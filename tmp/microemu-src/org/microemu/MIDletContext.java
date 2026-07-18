/*
 * Decompiled with CFR 0.152.
 */
package org.microemu;

import javax.microedition.midlet.MIDlet;
import org.microemu.MIDletAccess;
import org.microemu.app.launcher.Launcher;

public class MIDletContext {
    private MIDletAccess midletAccess;

    public MIDletAccess getMIDletAccess() {
        return this.midletAccess;
    }

    protected void setMIDletAccess(MIDletAccess midletAccess) {
        this.midletAccess = midletAccess;
    }

    public MIDlet getMIDlet() {
        if (this.midletAccess == null) {
            return null;
        }
        return this.midletAccess.midlet;
    }

    public boolean isLauncher() {
        return this.getMIDlet() instanceof Launcher;
    }
}

