/*
 * Decompiled with CFR 0.152.
 */
package org.microemu;

import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.microemu.MicroEmulator;
import org.microemu.util.ExtendedRecordListener;
import org.microemu.util.RecordStoreImpl;

public interface RecordStoreManager {
    public String getName();

    public void deleteRecordStore(String var1) throws RecordStoreNotFoundException, RecordStoreException;

    public RecordStore openRecordStore(String var1, boolean var2) throws RecordStoreException;

    public String[] listRecordStores();

    public void saveChanges(RecordStoreImpl var1) throws RecordStoreNotOpenException, RecordStoreException;

    public int getSizeAvailable(RecordStoreImpl var1);

    public void init(MicroEmulator var1);

    public void deleteStores();

    public void setRecordListener(ExtendedRecordListener var1);

    public void fireRecordStoreListener(int var1, String var2);
}

