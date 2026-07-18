/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.cldc.datagram;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.microedition.io.Datagram;
import javax.microedition.io.DatagramConnection;
import javax.microedition.io.UDPDatagramConnection;
import org.microemu.cldc.datagram.DatagramImpl;
import org.microemu.microedition.io.ConnectionImplementation;

public class Connection
implements DatagramConnection,
UDPDatagramConnection,
ConnectionImplementation {
    public static final String PROTOCOL = "datagram://";
    private DatagramSocket socket;
    private String address;

    public void close() throws IOException {
        this.socket.close();
    }

    public int getMaximumLength() throws IOException {
        return Math.min(this.socket.getReceiveBufferSize(), this.socket.getSendBufferSize());
    }

    public int getNominalLength() throws IOException {
        return this.getMaximumLength();
    }

    public void send(Datagram dgram) throws IOException {
        this.socket.send(((DatagramImpl)dgram).getDatagramPacket());
    }

    public void receive(Datagram dgram) throws IOException {
        this.socket.receive(((DatagramImpl)dgram).getDatagramPacket());
    }

    public Datagram newDatagram(int size) throws IOException {
        return this.newDatagram(size, this.address);
    }

    public Datagram newDatagram(int size, String addr) throws IOException {
        DatagramImpl datagram = new DatagramImpl(size);
        datagram.setAddress(addr);
        return datagram;
    }

    public Datagram newDatagram(byte[] buf, int size) throws IOException {
        return this.newDatagram(buf, size, this.address);
    }

    public Datagram newDatagram(byte[] buf, int size, String addr) throws IOException {
        DatagramImpl datagram = new DatagramImpl(buf, size);
        datagram.setAddress(addr);
        return datagram;
    }

    public String getLocalAddress() throws IOException {
        InetAddress address = this.socket.getInetAddress();
        address = address == null ? InetAddress.getLocalHost() : this.socket.getLocalAddress();
        return address.getHostAddress();
    }

    public int getLocalPort() throws IOException {
        return this.socket.getLocalPort();
    }

    public javax.microedition.io.Connection openConnection(String name, int mode, boolean timeouts) throws IOException {
        if (!org.microemu.cldc.http.Connection.isAllowNetworkConnection()) {
            throw new IOException("No network");
        }
        if (!name.startsWith(PROTOCOL)) {
            throw new IOException("Invalid Protocol " + name);
        }
        this.address = name.substring(PROTOCOL.length());
        int port = -1;
        int index = this.address.indexOf(58);
        if (index == -1) {
            throw new IOException("port missing");
        }
        port = Integer.parseInt(this.address.substring(index + 1));
        if (index == 0) {
            this.socket = new DatagramSocket(port);
        } else {
            String host = this.address.substring(0, index);
            this.socket = new DatagramSocket();
            this.socket.connect(InetAddress.getByName(host), port);
        }
        return this;
    }
}

