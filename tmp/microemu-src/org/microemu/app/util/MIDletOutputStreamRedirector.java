/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.microemu.log.Logger;

public class MIDletOutputStreamRedirector
extends PrintStream {
    private static final boolean keepMultiLinePrint = true;
    public static final PrintStream out = MIDletOutputStreamRedirector.outPrintStream();
    public static final PrintStream err = MIDletOutputStreamRedirector.errPrintStream();
    private boolean isErrorStream;

    private MIDletOutputStreamRedirector(boolean error) {
        super(new OutputStream2Log(error));
        this.isErrorStream = error;
    }

    private static PrintStream outPrintStream() {
        return new MIDletOutputStreamRedirector(false);
    }

    private static PrintStream errPrintStream() {
        return new MIDletOutputStreamRedirector(true);
    }

    public void print(boolean b) {
        super.print(b);
    }

    public void print(char c) {
        super.print(c);
    }

    public void print(char[] s) {
        super.print(s);
    }

    public void print(double d) {
        super.print(d);
    }

    public void print(float f) {
        super.print(f);
    }

    public void print(int i) {
        super.print(i);
    }

    public void print(long l) {
        super.print(l);
    }

    public void print(Object obj) {
        super.print(obj);
    }

    public void print(String s) {
        super.print(s);
    }

    public void println() {
        super.println();
    }

    public void println(boolean x) {
        super.println(x);
    }

    public void println(char x) {
        super.println(x);
    }

    public void println(char[] x) {
        super.println(x);
    }

    public void println(double x) {
        super.println(x);
    }

    public void println(float x) {
        super.println(x);
    }

    public void println(int x) {
        super.println(x);
    }

    public void println(long x) {
        super.println(x);
    }

    public void println(Object x) {
        super.flush();
        if (this.isErrorStream) {
            Logger.error(x);
        } else {
            Logger.info(x);
        }
    }

    public void println(String x) {
        super.flush();
        if (this.isErrorStream) {
            Logger.error(x);
        } else {
            Logger.info(x);
        }
    }

    public void write(byte[] buf, int off, int len) {
        super.write(buf, off, len);
    }

    public void write(int b) {
        super.write(b);
    }

    static {
        Logger.addLogOrigin(MIDletOutputStreamRedirector.class);
        Logger.addLogOrigin(OutputStream2Log.class);
    }

    private static class OutputStream2Log
    extends OutputStream {
        boolean isErrorStream;
        StringBuffer buffer = new StringBuffer();

        OutputStream2Log(boolean error) {
            this.isErrorStream = error;
        }

        public void flush() {
            if (this.buffer.length() > 0) {
                this.write(10);
            }
        }

        public void write(int b) {
            if (b == 10 || b == 13) {
                if (this.buffer.length() > 0) {
                    if (this.isErrorStream) {
                        Logger.error(this.buffer.toString());
                    } else {
                        Logger.info(this.buffer.toString());
                    }
                    this.buffer = new StringBuffer();
                }
            } else {
                this.buffer.append((char)b);
            }
        }
    }
}

