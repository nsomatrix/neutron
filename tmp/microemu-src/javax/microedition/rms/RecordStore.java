/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.rms;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordListener;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.microemu.MIDletBridge;

public class RecordStore {
    public static final int AUTHMODE_PRIVATE = 0;
    public static final int AUTHMODE_ANY = 1;

    public static void deleteRecordStore(String recordStoreName) throws RecordStoreException, RecordStoreNotFoundException {
        MIDletBridge.getRecordStoreManager().deleteRecordStore(recordStoreName);
    }

    public static String[] listRecordStores() {
        return MIDletBridge.getRecordStoreManager().listRecordStores();
    }

    public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
        return MIDletBridge.getRecordStoreManager().openRecordStore(recordStoreName, createIfNecessary);
    }

    public static RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary, int authmode, boolean writable) throws RecordStoreException, RecordStoreFullException, RecordStoreNotFoundException {
        return RecordStore.openRecordStore(recordStoreName, createIfNecessary);
    }

    public static RecordStore openRecordStore(String recordStoreName, String vendorName, String suiteName) throws RecordStoreException, RecordStoreNotFoundException {
        return RecordStore.openRecordStore(recordStoreName, false);
    }

    public void closeRecordStore() throws RecordStoreNotOpenException, RecordStoreException {
    }

    public String getName() throws RecordStoreNotOpenException {
        return null;
    }

    public int getVersion() throws RecordStoreNotOpenException {
        return -1;
    }

    public int getNumRecords() throws RecordStoreNotOpenException {
        return -1;
    }

    public int getSize() throws RecordStoreNotOpenException {
        return -1;
    }

    public int getSizeAvailable() throws RecordStoreNotOpenException {
        return -1;
    }

    public long getLastModified() throws RecordStoreNotOpenException {
        return -1L;
    }

    public void addRecordListener(RecordListener listener) {
    }

    public void removeRecordListener(RecordListener listener) {
    }

    public int getNextRecordID() throws RecordStoreNotOpenException, RecordStoreException {
        return -1;
    }

    public int addRecord(byte[] data, int offset, int numBytes) throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {
        return -1;
    }

    public void deleteRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
    }

    public int getRecordSize(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        return -1;
    }

    public int getRecord(int recordId, byte[] buffer, int offset) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        return -1;
    }

    public byte[] getRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        return null;
    }

    public void setMode(int authmode, boolean writable) throws RecordStoreException {
    }

    public void setRecord(int recordId, byte[] newData, int offset, int numBytes) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException, RecordStoreFullException {
    }

    public RecordEnumeration enumerateRecords(RecordFilter filter, RecordComparator comparator, boolean keepUpdated) throws RecordStoreNotOpenException {
        return null;
    }
}

