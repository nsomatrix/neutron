/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.rms;

import javax.microedition.rms.RecordStore;

public interface RecordListener {
    public void recordAdded(RecordStore var1, int var2);

    public void recordChanged(RecordStore var1, int var2);

    public void recordDeleted(RecordStore var1, int var2);
}

