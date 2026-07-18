/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Datagram
extends DataInput,
DataOutput {
    public String getAddress();

    public byte[] getData();

    public int getLength();

    public int getOffset();

    public void setAddress(String var1) throws IOException;

    public void setAddress(Datagram var1);

    public void setLength(int var1);

    public void setData(byte[] var1, int var2, int var3);

    public void reset();
}

