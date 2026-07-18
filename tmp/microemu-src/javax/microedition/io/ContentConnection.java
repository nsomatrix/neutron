/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.io;

import javax.microedition.io.StreamConnection;

public interface ContentConnection
extends StreamConnection {
    public String getType();

    public String getEncoding();

    public long getLength();
}

