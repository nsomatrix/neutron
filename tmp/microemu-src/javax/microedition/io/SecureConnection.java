/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.io;

import java.io.IOException;
import javax.microedition.io.SecurityInfo;
import javax.microedition.io.SocketConnection;

public interface SecureConnection
extends SocketConnection {
    public SecurityInfo getSecurityInfo() throws IOException;
}

