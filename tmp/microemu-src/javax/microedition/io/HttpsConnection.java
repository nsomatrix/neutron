/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.io;

import java.io.IOException;
import javax.microedition.io.HttpConnection;
import javax.microedition.io.SecurityInfo;

public interface HttpsConnection
extends HttpConnection {
    public SecurityInfo getSecurityInfo() throws IOException;

    public int getPort();
}

