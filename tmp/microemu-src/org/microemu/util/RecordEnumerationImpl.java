/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordComparator;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordFilter;
import javax.microedition.rms.RecordListener;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.microemu.util.RecordStoreImpl;

public class RecordEnumerationImpl
implements RecordEnumeration {
    private RecordStoreImpl recordStoreImpl;
    private RecordFilter filter;
    private RecordComparator comparator;
    private boolean keepUpdated;
    private Vector enumerationRecords = new Vector();
    private int currentRecord;
    private RecordListener recordListener = new RecordListener(){

        public void recordAdded(RecordStore recordStore, int recordId) {
            RecordEnumerationImpl.this.rebuild();
        }

        public void recordChanged(RecordStore recordStore, int recordId) {
            RecordEnumerationImpl.this.rebuild();
        }

        public void recordDeleted(RecordStore recordStore, int recordId) {
            RecordEnumerationImpl.this.rebuild();
        }
    };

    public RecordEnumerationImpl(RecordStoreImpl recordStoreImpl, RecordFilter filter, RecordComparator comparator, boolean keepUpdated) {
        this.recordStoreImpl = recordStoreImpl;
        this.filter = filter;
        this.comparator = comparator;
        this.keepUpdated = keepUpdated;
        this.rebuild();
        if (keepUpdated) {
            recordStoreImpl.addRecordListener(this.recordListener);
        }
    }

    public int numRecords() {
        return this.enumerationRecords.size();
    }

    public byte[] nextRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException {
        if (!this.recordStoreImpl.isOpen()) {
            throw new RecordStoreNotOpenException();
        }
        if (this.currentRecord >= this.numRecords()) {
            throw new InvalidRecordIDException();
        }
        byte[] result = ((EnumerationRecord)this.enumerationRecords.elementAt((int)this.currentRecord)).value;
        ++this.currentRecord;
        return result;
    }

    public int nextRecordId() throws InvalidRecordIDException {
        if (this.currentRecord >= this.numRecords()) {
            throw new InvalidRecordIDException();
        }
        int result = ((EnumerationRecord)this.enumerationRecords.elementAt((int)this.currentRecord)).recordId;
        ++this.currentRecord;
        return result;
    }

    public byte[] previousRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException {
        if (!this.recordStoreImpl.isOpen()) {
            throw new RecordStoreNotOpenException();
        }
        if (this.currentRecord < 0) {
            throw new InvalidRecordIDException();
        }
        byte[] result = ((EnumerationRecord)this.enumerationRecords.elementAt((int)this.currentRecord)).value;
        --this.currentRecord;
        return result;
    }

    public int previousRecordId() throws InvalidRecordIDException {
        if (this.currentRecord < 0) {
            throw new InvalidRecordIDException();
        }
        int result = ((EnumerationRecord)this.enumerationRecords.elementAt((int)this.currentRecord)).recordId;
        --this.currentRecord;
        return result;
    }

    public boolean hasNextElement() {
        return this.currentRecord != this.numRecords();
    }

    public boolean hasPreviousElement() {
        return this.currentRecord != 0;
    }

    public void reset() {
        this.currentRecord = 0;
    }

    public void rebuild() {
        this.enumerationRecords.removeAllElements();
        Enumeration e = this.recordStoreImpl.records.keys();
        while (e.hasMoreElements()) {
            Object key = e.nextElement();
            byte[] data = (byte[])this.recordStoreImpl.records.get(key);
            if (this.filter != null && !this.filter.matches(data)) continue;
            this.enumerationRecords.add(new EnumerationRecord((Integer)key, data));
        }
        if (this.comparator != null) {
            Collections.sort(this.enumerationRecords, new Comparator(){

                public int compare(Object lhs, Object rhs) {
                    int compare = RecordEnumerationImpl.this.comparator.compare(((EnumerationRecord)lhs).value, ((EnumerationRecord)rhs).value);
                    if (compare == 0) {
                        return 0;
                    }
                    if (compare == 1) {
                        return 1;
                    }
                    return -1;
                }
            });
        }
    }

    public void keepUpdated(boolean keepUpdated) {
        if (keepUpdated) {
            if (!this.keepUpdated) {
                this.rebuild();
                this.recordStoreImpl.addRecordListener(this.recordListener);
            }
        } else {
            this.recordStoreImpl.removeRecordListener(this.recordListener);
        }
        this.keepUpdated = keepUpdated;
    }

    public boolean isKeptUpdated() {
        return this.keepUpdated;
    }

    public void destroy() {
    }

    class EnumerationRecord {
        int recordId;
        byte[] value;

        EnumerationRecord(int recordId, byte[] value) {
            this.recordId = recordId;
            this.value = value;
        }
    }
}

