package org.neutron.util;

import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MemoryRecordStoreManagerTest {

    private MemoryRecordStoreManager manager;

    @Before
    public void setUp() {
        manager = new MemoryRecordStoreManager();
    }

    @Test
    public void testRecordPersistenceAcrossCloseAndOpen() throws Exception {
        RecordStore store = manager.openRecordStore("gameScores", true);
        assertNotNull(store);

        byte[] score1 = "Player1:1000".getBytes("UTF-8");
        byte[] score2 = "Player2:2500".getBytes("UTF-8");

        int id1 = store.addRecord(score1, 0, score1.length);
        int id2 = store.addRecord(score2, 0, score2.length);

        assertEquals(2, store.getNumRecords());
        assertArrayEquals(score1, store.getRecord(id1));
        assertArrayEquals(score2, store.getRecord(id2));

        // Close store
        store.closeRecordStore();

        try {
            store.getRecord(id1);
            fail("Calling getRecord on closed store must throw RecordStoreNotOpenException");
        } catch (RecordStoreNotOpenException e) {
            // Expected
        }

        // Reopen store
        RecordStore reopenedStore = manager.openRecordStore("gameScores", false);
        assertNotNull(reopenedStore);
        assertEquals("Record count must be preserved across close/reopen", 2, reopenedStore.getNumRecords());

        assertArrayEquals("Record 1 content must persist", score1, reopenedStore.getRecord(id1));
        assertArrayEquals("Record 2 content must persist", score2, reopenedStore.getRecord(id2));

        RecordEnumeration enumRecords = reopenedStore.enumerateRecords(null, null, false);
        assertEquals(2, enumRecords.numRecords());
        assertTrue(enumRecords.hasNextElement());
        assertEquals(id1, enumRecords.nextRecordId());
        assertTrue(enumRecords.hasNextElement());
        assertEquals(id2, enumRecords.nextRecordId());

        reopenedStore.closeRecordStore();
    }

    @Test
    public void testDeleteRecordStoreWipesData() throws Exception {
        RecordStore store = manager.openRecordStore("tempStore", true);
        byte[] data = "Temporary".getBytes("UTF-8");
        store.addRecord(data, 0, data.length);
        store.closeRecordStore();

        manager.deleteRecordStore("tempStore");

        try {
            manager.openRecordStore("tempStore", false);
            fail("Expected RecordStoreNotFoundException after deletion");
        } catch (RecordStoreNotFoundException e) {
            // Expected
        }

        RecordStore freshStore = manager.openRecordStore("tempStore", true);
        assertEquals(0, freshStore.getNumRecords());
        freshStore.closeRecordStore();
    }
}

