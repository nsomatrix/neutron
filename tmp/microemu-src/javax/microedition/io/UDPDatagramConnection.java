/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.io;

import java.io.IOException;
import javax.microedition.io.DatagramConnection;

public interface UDPDatagramConnection
extends DatagramConnection {
    public String getLocalAddress() throws IOException;

    public int getLocalPort() throws IOException;
}

