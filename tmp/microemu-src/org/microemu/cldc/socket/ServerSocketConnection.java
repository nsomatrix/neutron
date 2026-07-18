/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.cldc.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import javax.microedition.io.StreamConnection;
import org.microemu.cldc.socket.SocketConnection;

public class ServerSocketConnection
implements javax.microedition.io.ServerSocketConnection {
    private ServerSocket serverSocket;

    public ServerSocketConnection(int port) throws IOException {
        this.serverSocket = new ServerSocket(port);
    }

    public String getLocalAddress() throws IOException {
        InetAddress localHost = InetAddress.getLocalHost();
        return localHost.getHostAddress();
    }

    public int getLocalPort() throws IOException {
        return this.serverSocket.getLocalPort();
    }

    public StreamConnection acceptAndOpen() throws IOException {
        return new SocketConnection(this.serverSocket.accept());
    }

    public void close() throws IOException {
        this.serverSocket.close();
    }
}

