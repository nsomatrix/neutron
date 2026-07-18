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
import javax.microedition.io.InputConnection;
import javax.microedition.io.OutputConnection;
import org.microemu.microedition.io.ConnectorDelegate;

public abstract class ConnectorAdapter
implements ConnectorDelegate {
    public abstract Connection open(String var1, int var2, boolean var3) throws IOException;

    public Connection open(String name) throws IOException {
        return this.open(name, 3, false);
    }

    public Connection open(String name, int mode) throws IOException {
        return this.open(name, mode, false);
    }

    public DataInputStream openDataInputStream(String name) throws IOException {
        return ((InputConnection)this.open(name)).openDataInputStream();
    }

    public DataOutputStream openDataOutputStream(String name) throws IOException {
        return ((OutputConnection)this.open(name)).openDataOutputStream();
    }

    public InputStream openInputStream(String name) throws IOException {
        return ((InputConnection)this.open(name)).openInputStream();
    }

    public OutputStream openOutputStream(String name) throws IOException {
        return ((OutputConnection)this.open(name)).openOutputStream();
    }
}

