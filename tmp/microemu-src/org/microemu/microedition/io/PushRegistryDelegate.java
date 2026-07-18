/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.microedition.io;

import java.io.IOException;
import javax.microedition.io.ConnectionNotFoundException;

public interface PushRegistryDelegate {
    public void registerConnection(String var1, String var2, String var3) throws ClassNotFoundException, IOException;

    public boolean unregisterConnection(String var1);

    public String[] listConnections(boolean var1);

    public String getMIDlet(String var1);

    public String getFilter(String var1);

    public long registerAlarm(String var1, long var2) throws ClassNotFoundException, ConnectionNotFoundException;
}

