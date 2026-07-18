/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.util;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.util.ExtendedRecordListener;
import org.microemu.util.RecordStoreImpl;

public class MemoryRecordStoreManager
implements RecordStoreManager {
    private Hashtable recordStores = new Hashtable();
    private ExtendedRecordListener recordListener = null;

    public void init(MicroEmulator emulator) {
    }

    public String getName() {
        return "Memory record store";
    }

    public void deleteRecordStore(String recordStoreName) throws RecordStoreNotFoundException, RecordStoreException {
        RecordStoreImpl recordStoreImpl = (RecordStoreImpl)this.recordStores.get(recordStoreName);
        if (recordStoreImpl == null) {
            throw new RecordStoreNotFoundException(recordStoreName);
        }
        if (recordStoreImpl.isOpen()) {
            throw new RecordStoreException();
        }
        this.recordStores.remove(recordStoreName);
        this.fireRecordStoreListener(10, recordStoreName);
    }

    public RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) throws RecordStoreNotFoundException {
        RecordStoreImpl recordStoreImpl = (RecordStoreImpl)this.recordStores.get(recordStoreName);
        if (recordStoreImpl == null) {
            if (!createIfNecessary) {
                throw new RecordStoreNotFoundException(recordStoreName);
            }
            recordStoreImpl = new RecordStoreImpl((RecordStoreManager)this, recordStoreName);
            this.recordStores.put(recordStoreName, recordStoreImpl);
        }
        recordStoreImpl.setOpen(true);
        if (this.recordListener != null) {
            recordStoreImpl.addRecordListener(this.recordListener);
        }
        this.fireRecordStoreListener(8, recordStoreName);
        return recordStoreImpl;
    }

    public String[] listRecordStores() {
        String[] result = null;
        int i = 0;
        Enumeration e = this.recordStores.keys();
        while (e.hasMoreElements()) {
            if (result == null) {
                result = new String[this.recordStores.size()];
            }
            result[i] = (String)e.nextElement();
            ++i;
        }
        return result;
    }

    public void saveChanges(RecordStoreImpl recordStoreImpl) {
    }

    public void init() {
        this.deleteStores();
    }

    public void deleteStores() {
        if (this.recordStores != null) {
            this.recordStores.clear();
        }
    }

    public int getSizeAvailable(RecordStoreImpl recordStoreImpl) {
        return (int)Runtime.getRuntime().freeMemory();
    }

    public void setRecordListener(ExtendedRecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public void fireRecordStoreListener(int type, String recordStoreName) {
        if (this.recordListener != null) {
            this.recordListener.recordStoreEvent(type, System.currentTimeMillis(), recordStoreName);
        }
    }
}

