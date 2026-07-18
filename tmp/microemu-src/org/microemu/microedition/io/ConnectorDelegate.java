/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import org.microemu.microedition.Implementation;

public interface ConnectorDelegate
extends Implementation {
    public Connection open(String var1) throws IOException;

    public Connection open(String var1, int var2) throws IOException;

    public Connection open(String var1, int var2, boolean var3) throws IOException;

    public DataInputStream openDataInputStream(String var1) throws IOException;

    public DataOutputStream openDataOutputStream(String var1) throws IOException;

    public InputStream openInputStream(String var1) throws IOException;

    public OutputStream openOutputStream(String var1) throws IOException;
}

