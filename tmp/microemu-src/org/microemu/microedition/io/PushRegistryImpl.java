/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.microedition.io;

import java.io.IOException;
import javax.microedition.io.ConnectionNotFoundException;
import org.microemu.microedition.Implementation;
import org.microemu.microedition.io.PushRegistryDelegate;

public class PushRegistryImpl
implements PushRegistryDelegate,
Implementation {
    public String getFilter(String connection) {
        return null;
    }

    public String getMIDlet(String connection) {
        return null;
    }

    public String[] listConnections(boolean available) {
        return new String[0];
    }

    public long registerAlarm(String midlet, long time) throws ClassNotFoundException, ConnectionNotFoundException {
        throw new ConnectionNotFoundException();
    }

    public void registerConnection(String connection, String midlet, String filter) throws ClassNotFoundException, IOException {
    }

    public boolean unregisterConnection(String connection) {
        return false;
    }
}

