/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.microedition.io.Connection;

public interface OutputConnection
extends Connection {
    public OutputStream openOutputStream() throws IOException;

    public DataOutputStream openDataOutputStream() throws IOException;
}

