/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.io;

import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.StreamConnection;

public interface StreamConnectionNotifier
extends Connection {
    public StreamConnection acceptAndOpen() throws IOException;
}

