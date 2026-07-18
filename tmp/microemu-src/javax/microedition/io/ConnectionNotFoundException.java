/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.io;

import java.io.IOException;

public class ConnectionNotFoundException
extends IOException {
    private static final long serialVersionUID = 1L;

    public ConnectionNotFoundException() {
    }

    public ConnectionNotFoundException(String s) {
        super(s);
    }
}

