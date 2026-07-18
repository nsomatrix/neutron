/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.io;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.Connection;
import org.microemu.microedition.ImplFactory;

public class Connector {
    public static final int READ = 1;
    public static final int WRITE = 2;
    public static final int READ_WRITE = 3;

    private Connector() {
    }

    public static Connection open(String name) throws IOException {
        return ImplFactory.getCGFImplementation(name).open(name);
    }

    public static Connection open(String name, int mode) throws IOException {
        return ImplFactory.getCGFImplementation(name).open(name, mode);
    }

    public static Connection open(String name, int mode, boolean timeouts) throws IOException {
        return ImplFactory.getCGFImplementation(name).open(name, mode, timeouts);
    }

    public static DataInputStream openDataInputStream(String name) throws IOException {
        return ImplFactory.getCGFImplementation(name).openDataInputStream(name);
    }

    public static DataOutputStream openDataOutputStream(String name) throws IOException {
        return ImplFactory.getCGFImplementation(name).openDataOutputStream(name);
    }

    public static InputStream openInputStream(String name) throws IOException {
        return ImplFactory.getCGFImplementation(name).openInputStream(name);
    }

    public static OutputStream openOutputStream(String name) throws IOException {
        return ImplFactory.getCGFImplementation(name).openOutputStream(name);
    }
}

