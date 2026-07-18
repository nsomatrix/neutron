/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.io;

import java.io.IOException;
import javax.microedition.io.StreamConnectionNotifier;

public interface ServerSocketConnection
extends StreamConnectionNotifier {
    public String getLocalAddress() throws IOException;

    public int getLocalPort() throws IOException;
}

