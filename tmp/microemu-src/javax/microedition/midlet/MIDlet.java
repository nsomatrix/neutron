/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.midlet;

import javax.microedition.io.ConnectionNotFoundException;
import javax.microedition.midlet.MIDletStateChangeException;
import org.microemu.DisplayAccess;
import org.microemu.MIDletAccess;
import org.microemu.MIDletBridge;

public abstract class MIDlet {
    private boolean destroyed;

    protected MIDlet() {
        MIDletBridge.registerMIDletAccess(new MIDletAccessor());
    }

    protected abstract void startApp() throws MIDletStateChangeException;

    protected abstract void pauseApp();

    protected abstract void destroyApp(boolean var1) throws MIDletStateChangeException;

    public final int checkPermission(String permission) {
        return MIDletBridge.checkPermission(permission);
    }

    public final String getAppProperty(String key) {
        return MIDletBridge.getAppProperty(key);
    }

    public final void notifyDestroyed() {
        this.destroyed = true;
        MIDletBridge.notifyDestroyed();
    }

    public final void notifyPaused() {
    }

    public final boolean platformRequest(String URL2) throws ConnectionNotFoundException {
        return MIDletBridge.platformRequest(URL2);
    }

    public final void resumeRequest() {
    }

    class MIDletAccessor
    extends MIDletAccess {
        public MIDletAccessor() {
            super(MIDlet.this);
            MIDlet.this.destroyed = false;
        }

        public void startApp() throws MIDletStateChangeException {
            MIDletBridge.setCurrentMIDlet(this.midlet);
            this.midlet.startApp();
        }

        public void pauseApp() {
            this.midlet.pauseApp();
        }

        public void destroyApp(boolean unconditional) throws MIDletStateChangeException {
            DisplayAccess da;
            if (!this.midlet.destroyed) {
                this.midlet.destroyApp(unconditional);
            }
            if ((da = this.getDisplayAccess()) != null) {
                da.clean();
                this.setDisplayAccess(null);
            }
            MIDletBridge.destroyMIDletContext(MIDletBridge.getMIDletContext(this.midlet));
        }
    }
}

