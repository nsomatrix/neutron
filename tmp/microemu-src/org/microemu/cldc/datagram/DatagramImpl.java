/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.cldc.datagram;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.nio.BufferOverflowException;
import javax.microedition.io.Datagram;

public class DatagramImpl
implements Datagram {
    private DatagramPacket packet;
    private BufferOutputStream os;
    private DataOutputStream dos;
    private DataInputStream dis;

    DatagramImpl(int size) {
        if (size <= 0) {
            throw new IllegalArgumentException("Invalid size: " + size);
        }
        this.packet = new DatagramPacket(new byte[size], size);
        this.initialiseInOut();
    }

    DatagramImpl(byte[] buff, int length) {
        this.packet = new DatagramPacket(buff, length);
        this.initialiseInOut();
    }

    private void initialiseInOut() {
        this.os = new BufferOutputStream();
        this.dos = new DataOutputStream(this.os);
        this.dis = new DataInputStream(new ByteArrayInputStream(this.packet.getData()));
    }

    public String getAddress() {
        return this.packet.getAddress().getCanonicalHostName() + ":" + this.packet.getPort();
    }

    public byte[] getData() {
        return this.packet.getData();
    }

    public int getLength() {
        return this.packet.getLength();
    }

    public int getOffset() {
        return this.packet.getOffset();
    }

    public void reset() {
        try {
            this.os.reset();
            this.dis.reset();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setAddress(String address) throws IOException {
        if (address == null) {
            throw new NullPointerException("address cannot be null");
        }
        int index = address.indexOf(58);
        if (index == -1) {
            throw new IllegalArgumentException("Missing port in address: " + address);
        }
        String host = address.substring(0, index);
        String port = address.substring(index + 1);
        this.packet.setAddress(InetAddress.getByName(host));
        this.packet.setPort(Integer.parseInt(port));
    }

    public void setAddress(Datagram reference) {
        this.packet.setAddress(((DatagramImpl)reference).getDatagramPacket().getAddress());
        this.packet.setPort(((DatagramImpl)reference).getDatagramPacket().getPort());
    }

    public void setData(byte[] buffer, int offset, int len) {
        this.packet.setData(buffer, offset, len);
    }

    public void setLength(int len) {
        this.packet.setLength(len);
    }

    public boolean readBoolean() throws IOException {
        return this.dis.readBoolean();
    }

    public byte readByte() throws IOException {
        return this.dis.readByte();
    }

    public char readChar() throws IOException {
        return this.dis.readChar();
    }

    public double readDouble() throws IOException {
        return this.dis.readDouble();
    }

    public float readFloat() throws IOException {
        return this.dis.readFloat();
    }

    public void readFully(byte[] b) throws IOException {
        this.dis.readFully(b);
    }

    public void readFully(byte[] b, int off, int len) throws IOException {
        this.dis.read(b, off, len);
    }

    public int readInt() throws IOException {
        return this.dis.readInt();
    }

    public String readLine() throws IOException {
        return this.dis.readLine();
    }

    public long readLong() throws IOException {
        return this.dis.readLong();
    }

    public short readShort() throws IOException {
        return this.dis.readShort();
    }

    public String readUTF() throws IOException {
        return this.dis.readUTF();
    }

    public int readUnsignedByte() throws IOException {
        return this.dis.readUnsignedByte();
    }

    public int readUnsignedShort() throws IOException {
        return this.dis.readUnsignedShort();
    }

    public int skipBytes(int n) throws IOException {
        return this.dis.skipBytes(n);
    }

    public void write(int b) throws IOException {
        this.dos.write(b);
    }

    public void write(byte[] b) throws IOException {
        this.dos.write(b);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        this.dos.write(b, off, len);
    }

    public void writeBoolean(boolean v) throws IOException {
        this.dos.writeBoolean(v);
    }

    public void writeByte(int v) throws IOException {
        this.dos.writeByte(v);
    }

    public void writeBytes(String s) throws IOException {
        this.dos.writeBytes(s);
    }

    public void writeChar(int v) throws IOException {
        this.dos.writeChar(v);
    }

    public void writeChars(String v) throws IOException {
        this.dos.writeChars(v);
    }

    public void writeDouble(double v) throws IOException {
        this.dos.writeDouble(v);
    }

    public void writeFloat(float v) throws IOException {
        this.dos.writeFloat(v);
    }

    public void writeInt(int v) throws IOException {
        this.dos.writeInt(v);
    }

    public void writeLong(long v) throws IOException {
        this.dos.writeLong(v);
    }

    public void writeShort(int v) throws IOException {
        this.dos.writeShort(v);
    }

    public void writeUTF(String str) throws IOException {
        this.dos.writeUTF(str);
    }

    DatagramPacket getDatagramPacket() {
        return this.packet;
    }

    class BufferOutputStream
    extends OutputStream {
        private int originalOffset;
        private int offset;

        public BufferOutputStream() {
            this.offset = this.originalOffset = DatagramImpl.this.packet.getOffset();
        }

        public void write(int b) throws IOException {
            byte[] buffer = DatagramImpl.this.packet.getData();
            if (this.offset == buffer.length - 1) {
                throw new BufferOverflowException();
            }
            buffer[this.offset++] = (byte)b;
        }

        public void reset() {
            this.offset = this.originalOffset;
        }
    }
}

