/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.rms;

public interface RecordComparator {
    public static final int EQUIVALENT = 0;
    public static final int FOLLOWS = 1;
    public static final int PRECEDES = -1;

    public int compare(byte[] var1, byte[] var2);
}

