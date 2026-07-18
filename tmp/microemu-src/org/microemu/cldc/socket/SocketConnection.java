/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.cldc.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class SocketConnection
implements javax.microedition.io.SocketConnection {
    protected Socket socket;

    public SocketConnection() {
    }

    public SocketConnection(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
    }

    public SocketConnection(Socket socket) {
        this.socket = socket;
    }

    public String getAddress() throws IOException {
        if (this.socket == null || this.socket.isClosed()) {
            throw new IOException();
        }
        return this.socket.getInetAddress().toString();
    }

    public String getLocalAddress() throws IOException {
        if (this.socket == null || this.socket.isClosed()) {
            throw new IOException();
        }
        return this.socket.getLocalAddress().toString();
    }

    public int getLocalPort() throws IOException {
        if (this.socket == null || this.socket.isClosed()) {
            throw new IOException();
        }
        return this.socket.getLocalPort();
    }

    public int getPort() throws IOException {
        if (this.socket == null || this.socket.isClosed()) {
            throw new IOException();
        }
        return this.socket.getPort();
    }

    public int getSocketOption(byte option) throws IllegalArgumentException, IOException {
        if (this.socket != null && this.socket.isClosed()) {
            throw new IOException();
        }
        switch (option) {
            case 0: {
                if (this.socket.getTcpNoDelay()) {
                    return 1;
                }
                return 0;
            }
            case 1: {
                int value = this.socket.getSoLinger();
                if (value == -1) {
                    return 0;
                }
                return value;
            }
            case 2: {
                if (this.socket.getKeepAlive()) {
                    return 1;
                }
                return 0;
            }
            case 3: {
                return this.socket.getReceiveBufferSize();
            }
            case 4: {
                return this.socket.getSendBufferSize();
            }
        }
        throw new IllegalArgumentException();
    }

    public void setSocketOption(byte option, int value) throws IllegalArgumentException, IOException {
        if (this.socket.isClosed()) {
            throw new IOException();
        }
        switch (option) {
            case 0: {
                boolean delay = value != 0;
                this.socket.setTcpNoDelay(delay);
                break;
            }
            case 1: {
                if (value < 0) {
                    throw new IllegalArgumentException();
                }
                this.socket.setSoLinger(value != 0, value);
                break;
            }
            case 2: {
                boolean keepalive = value != 0;
                this.socket.setKeepAlive(keepalive);
                break;
            }
            case 3: {
                if (value <= 0) {
                    throw new IllegalArgumentException();
                }
                this.socket.setReceiveBufferSize(value);
                break;
            }
            case 4: {
                if (value <= 0) {
                    throw new IllegalArgumentException();
                }
                this.socket.setSendBufferSize(value);
                break;
            }
            default: {
                throw new IllegalArgumentException();
            }
        }
    }

    public void close() throws IOException {
        this.socket.close();
    }

    public InputStream openInputStream() throws IOException {
        return this.socket.getInputStream();
    }

    public DataInputStream openDataInputStream() throws IOException {
        return new DataInputStream(this.openInputStream());
    }

    public OutputStream openOutputStream() throws IOException {
        return this.socket.getOutputStream();
    }

    public DataOutputStream openDataOutputStream() throws IOException {
        return new DataOutputStream(this.openOutputStream());
    }
}

