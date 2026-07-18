/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.io;

import javax.microedition.io.StreamConnection;

public interface CommConnection
extends StreamConnection {
    public int getBaudRate();

    public int setBaudRate(int var1);
}

