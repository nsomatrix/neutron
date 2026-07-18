/*
 * Decompiled with CFR 0.152.
 */
package javax.microedition.rms;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotOpenException;

public interface RecordEnumeration {
    public int numRecords();

    public byte[] nextRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException;

    public int nextRecordId() throws InvalidRecordIDException;

    public byte[] previousRecord() throws InvalidRecordIDException, RecordStoreNotOpenException, RecordStoreException;

    public int previousRecordId() throws InvalidRecordIDException;

    public boolean hasNextElement();

    public boolean hasPreviousElement();

    public void reset();

    public void rebuild();

    public void keepUpdated(boolean var1);

    public boolean isKeptUpdated();

    public void destroy();
}

