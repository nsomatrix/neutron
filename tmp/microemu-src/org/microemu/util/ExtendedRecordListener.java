/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.util;

import javax.microedition.rms.RecordListener;
import javax.microedition.rms.RecordStore;

public interface ExtendedRecordListener
extends RecordListener {
    public static final int RECORD_ADD = 1;
    public static final int RECORD_READ = 2;
    public static final int RECORD_CHANGE = 3;
    public static final int RECORD_DELETE = 4;
    public static final int RECORDSTORE_OPEN = 8;
    public static final int RECORDSTORE_CLOSE = 9;
    public static final int RECORDSTORE_DELETE = 10;

    public void recordEvent(int var1, long var2, RecordStore var4, int var5);

    public void recordStoreEvent(int var1, long var2, String var4);
}

