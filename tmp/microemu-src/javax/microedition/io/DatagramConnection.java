/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.io;

import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Datagram;

public interface DatagramConnection
extends Connection {
    public int getMaximumLength() throws IOException;

    public int getNominalLength() throws IOException;

    public void send(Datagram var1) throws IOException;

    public void receive(Datagram var1) throws IOException;

    public Datagram newDatagram(int var1) throws IOException;

    public Datagram newDatagram(int var1, String var2) throws IOException;

    public Datagram newDatagram(byte[] var1, int var2) throws IOException;

    public Datagram newDatagram(byte[] var1, int var2, String var3) throws IOException;
}

