/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.util;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordListener;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.microemu.RecordStoreManager;
import org.microemu.util.ExtendedRecordListener;
import org.microemu.util.RecordEnumerationImpl;

public class RecordStoreImpl
extends RecordStore {
    protected Hashtable records = new Hashtable();
    private String recordStoreName;
    private int version = 0;
    private long lastModified = 0L;
    private int nextRecordID = 1;
    private transient boolean open;
    private transient RecordStoreManager recordStoreManager;
    private transient Vector recordListeners = new Vector();

    public RecordStoreImpl(RecordStoreManager recordStoreManager, String recordStoreName) {
        this.recordStoreManager = recordStoreManager;
        this.recordStoreName = recordStoreName.length() <= 32 ? recordStoreName : recordStoreName.substring(0, 32);
        this.open = false;
    }

    public RecordStoreImpl(RecordStoreManager recordStoreManager, DataInputStream dis) throws IOException {
        this.recordStoreManager = recordStoreManager;
        this.recordStoreName = dis.readUTF();
        this.version = dis.readInt();
        this.lastModified = dis.readLong();
        this.nextRecordID = dis.readInt();
        try {
            while (true) {
                int recordId = dis.readInt();
                byte[] data = new byte[dis.readInt()];
                dis.read(data, 0, data.length);
                this.records.put(new Integer(recordId), data);
            }
        }
        catch (EOFException eOFException) {
            return;
        }
    }

    public void write(DataOutputStream dos) throws IOException {
        dos.writeUTF(this.recordStoreName);
        dos.writeInt(this.version);
        dos.writeLong(this.lastModified);
        dos.writeInt(this.nextRecordID);
        Enumeration en = this.records.keys();
        while (en.hasMoreElements()) {
            Integer key = (Integer)en.nextElement();
            dos.writeInt(key);
            byte[] data = (byte[])this.records.get(key);
            dos.writeInt(data.length);
            dos.write(data);
        }
    }

    public boolean isOpen() {
        return this.open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void closeRecordStore() throws RecordStoreNotOpenException, RecordStoreException {
        if (!this.open) {
            throw new RecordStoreNotOpenException();
        }
        if (this.recordListeners != null) {
            this.recordListeners.removeAllElements();
        }
        this.recordStoreManager.fireRecordStoreListener(9, this.getName());
        this.open = false;
    }

    public String getName() throws RecordStoreNotOpenException {
        if (!this.open) {
            throw new RecordStoreNotOpenException();
        }
        return this.recordStoreName;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getVersion() throws RecordStoreNotOpenException {
        if (!this.open) {
            throw new RecordStoreNotOpenException();
        }
        RecordStoreImpl recordStoreImpl = this;
        synchronized (recordStoreImpl) {
            return this.version;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNumRecords() throws RecordStoreNotOpenException {
        if (!this.open) {
            throw new RecordStoreNotOpenException();
        }
        RecordStoreImpl recordStoreImpl = this;
        synchronized (recordStoreImpl) {
            return this.records.size();
        }
    }

    public int getSize() throws RecordStoreNotOpenException {
        if (!this.open) {
            throw new RecordStoreNotOpenException();
        }
        int size = 0;
        Enumeration keys = this.records.keys();
        while (keys.hasMoreElements()) {
            size += ((byte[])this.records.get(keys.nextElement())).length;
        }
        return size;
    }

    public int getSizeAvailable() throws RecordStoreNotOpenException {
        if (!this.open) {
            throw new RecordStoreNotOpenException();
        }
        return this.recordStoreManager.getSizeAvailable(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getLastModified() throws RecordStoreNotOpenException {
        if (!this.open) {
            throw new RecordStoreNotOpenException();
        }
        RecordStoreImpl recordStoreImpl = this;
        synchronized (recordStoreImpl) {
            return this.lastModified;
        }
    }

    public void addRecordListener(RecordListener listener) {
        if (!this.recordListeners.contains(listener)) {
            this.recordListeners.addElement(listener);
        }
    }

    public void removeRecordListener(RecordListener listener) {
        this.recordListeners.removeElement(listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getNextRecordID() throws RecordStoreNotOpenException, RecordStoreException {
        if (!this.open) {
            throw new RecordStoreNotOpenException();
        }
        RecordStoreImpl recordStoreImpl = this;
        synchronized (recordStoreImpl) {
            return this.nextRecordID;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int addRecord(byte[] data, int offset, int numBytes) throws RecordStoreNotOpenException, RecordStoreException, RecordStoreFullException {
        int curRecordID;
        if (!this.open) {
            throw new RecordStoreNotOpenException();
        }
        if (data == null && numBytes > 0) {
            throw new NullPointerException();
        }
        if (numBytes > this.recordStoreManager.getSizeAvailable(this)) {
            throw new RecordStoreFullException();
        }
        byte[] recordData = new byte[numBytes];
        if (data != null) {
            System.arraycopy(data, offset, recordData, 0, numBytes);
        }
        RecordStoreImpl recordStoreImpl = this;
        synchronized (recordStoreImpl) {
            this.records.put(new Integer(this.nextRecordID), recordData);
            ++this.version;
            curRecordID = this.nextRecordID++;
            this.lastModified = System.currentTimeMillis();
        }
        this.recordStoreManager.saveChanges(this);
        this.fireRecordListener(1, curRecordID);
        return curRecordID;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void deleteRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        if (!this.open) {
            throw new RecordStoreNotOpenException();
        }
        RecordStoreImpl recordStoreImpl = this;
        synchronized (recordStoreImpl) {
            if (this.records.remove(new Integer(recordId)) == null) {
                throw new InvalidRecordIDException();
            }
            ++this.version;
            this.lastModified = System.currentTimeMillis();
        }
        this.recordStoreManager.saveChanges(this);
        this.fireRecordListener(4, recordId);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getRecordSize(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        if (!this.open) {
            throw new RecordStoreNotOpenException();
        }
        RecordStoreImpl recordStoreImpl = this;
        synchronized (recordStoreImpl) {
            byte[] data = (byte[])this.records.get(new Integer(recordId));
            if (data == null) {
                throw new InvalidRecordIDException();
            }
            return data.length;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int getRecord(int recordId, byte[] buffer, int offset) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        int recordSize;
        RecordStoreImpl recordStoreImpl = this;
        synchronized (recordStoreImpl) {
            recordSize = this.getRecordSize(recordId);
            System.arraycopy(this.records.get(new Integer(recordId)), 0, buffer, offset, recordSize);
        }
        this.fireRecordListener(2, recordId);
        return recordSize;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public byte[] getRecord(int recordId) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException {
        byte[] data;
        RecordStoreImpl recordStoreImpl = this;
        synchronized (recordStoreImpl) {
            data = new byte[this.getRecordSize(recordId)];
            this.getRecord(recordId, data, 0);
        }
        return data.length < 1 ? null : data;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setRecord(int recordId, byte[] newData, int offset, int numBytes) throws RecordStoreNotOpenException, InvalidRecordIDException, RecordStoreException, RecordStoreFullException {
        if (!this.open) {
            throw new RecordStoreNotOpenException();
        }
        if (numBytes > this.recordStoreManager.getSizeAvailable(this)) {
            throw new RecordStoreFullException();
        }
        byte[] recordData = new byte[numBytes];
        System.arraycopy(newData, offset, recordData, 0, numBytes);
        RecordStoreImpl recordStoreImpl = this;
        synchronized (recordStoreImpl) {
            Integer id = new Integer(recordId);
            if (this.records.remove(id) == null) {
                throw new InvalidRecordIDException();
            }
            this.records.put(id, recordData);
            ++this.version;
            this.lastModified = System.currentTimeMillis();
        }
        this.recordStoreManager.saveChanges(this);
        this.fireRecordListener(3, recordId);
    }

    public RecordEnumeration enumerateRecords(RecordFilter filter, RecordComparator comparator, boolean keepUpdated) throws RecordStoreNotOpenException {
        if (!this.open) {
            throw new RecordStoreNotOpenException();
        }
        return new RecordEnumerationImpl(this, filter, comparator, keepUpdated);
    }

    public int getHeaderSize() {
        return this.recordStoreName.length() + 4 + 8 + 4;
    }

    public int getRecordHeaderSize() {
        return 8;
    }

    private void fireRecordListener(int type, int recordId) {
        long timestamp = System.currentTimeMillis();
        if (this.recordListeners != null) {
            Enumeration e = this.recordListeners.elements();
            while (e.hasMoreElements()) {
                RecordListener l = (RecordListener)e.nextElement();
                if (l instanceof ExtendedRecordListener) {
                    ((ExtendedRecordListener)l).recordEvent(type, timestamp, this, recordId);
                    continue;
                }
                switch (type) {
                    case 1: {
                        l.recordAdded(this, recordId);
                        break;
                    }
                    case 3: {
                        l.recordChanged(this, recordId);
                        break;
                    }
                    case 4: {
                        l.recordDeleted(this, recordId);
                    }
                }
            }
        }
    }
}

