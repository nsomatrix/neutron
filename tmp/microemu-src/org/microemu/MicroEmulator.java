/*
 * Decompiled with CFR 0.152.
 */
package org.microemu;

import java.io.InputStream;
import org.microemu.MIDletContext;
import org.microemu.RecordStoreManager;
import org.microemu.app.launcher.Launcher;

public interface MicroEmulator {
    public RecordStoreManager getRecordStoreManager();

    public Launcher getLauncher();

    public String getAppProperty(String var1);

    public InputStream getResourceAsStream(String var1);

    public void notifyDestroyed(MIDletContext var1);

    public void destroyMIDletContext(MIDletContext var1);

    public int checkPermission(String var1);

    public boolean platformRequest(String var1);
}

