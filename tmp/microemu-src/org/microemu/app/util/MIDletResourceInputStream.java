/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import java.io.IOException;
import java.io.InputStream;

public class MIDletResourceInputStream
extends InputStream {
    private InputStream is;

    public MIDletResourceInputStream(InputStream is) {
        this.is = is;
    }

    public int available() throws IOException {
        return this.is.available();
    }

    public int read() throws IOException {
        return this.is.read();
    }

    public int read(byte[] b) throws IOException {
        int result = 0;
        int count = 0;
        do {
            if (!((count = this.is.read(b, result, b.length - result)) != -1 ? (result += count) == b.length : result != 0)) continue;
            return result;
        } while (count != -1);
        return -1;
    }
}

