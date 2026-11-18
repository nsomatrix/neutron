package org.neutron.app.util;

import java.io.File;
import java.io.InputStream;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neutron.MIDletContext;
import org.neutron.Neutron;
import org.neutron.RecordStoreManager;
import org.neutron.app.launcher.Launcher;

import static org.junit.Assert.*;

public class FileRecordStoreManagerTest {

    private FileRecordStoreManager manager;

    @Before
    public void setUp() {
        manager = new FileRecordStoreManager();
        Neutron emulator = new Neutron() {
            public RecordStoreManager getRecordStoreManager() { return manager; }
            public Launcher getLauncher() { return null; }
            public String getAppProperty(String key) { return null; }
            public InputStream getResourceAsStream(Class origClass, String name) { return null; }
            public void notifyDestroyed(MIDletContext midletContext) {}
            public void destroyMIDletContext(MIDletContext midletContext) {}
            public int checkPermission(String permission) { return 1; }
            public boolean platformRequest(String URL) { return false; }
            public String getSuiteName() { return "TestGameSuite"; }
        };
        manager.init(emulator);
    }

    @After
    public void tearDown() {
        try {
            manager.deleteRecordStore("Scores");
        } catch (Throwable ignored) {}
        try {
            manager.deleteRecordStore("ToDelete");
        } catch (Throwable ignored) {}
    }

    @Test
    public void testSuiteFolderSanitization() {
        File folder = manager.getSuiteFolder();
        assertNotNull(folder);
        assertTrue("Suite folder name should contain sanitized suite name",
                folder.getName().contains("TestGameSuite"));
        assertFalse("Suite folder should not contain directory traversal characters",
                folder.getName().contains(".."));
    }

    @Test
    public void testRecordStoreNameConversion() {
        String safeName = FileRecordStoreManager.recordStoreName2FileName("Game:Save*Data?");
        assertFalse("Must not contain colon", safeName.contains(":"));
        assertFalse("Must not contain asterisk", safeName.contains("*"));
        assertFalse("Must not contain question mark", safeName.contains("?"));
    }

    @Test
    public void testAtomicSaveAndLoadRecordStore() throws Exception {
        RecordStore store = manager.openRecordStore("Scores", true);

        byte[] scoreData1 = new byte[] { 10, 20, 30, 40 };
        byte[] scoreData2 = new byte[] { 50, 60, 70, 80 };

        int id1 = store.addRecord(scoreData1, 0, scoreData1.length);
        int id2 = store.addRecord(scoreData2, 0, scoreData2.length);

        store.closeRecordStore();

        File suiteFolder = manager.getSuiteFolder();
        File savedFile = new File(suiteFolder, FileRecordStoreManager.recordStoreName2FileName("Scores"));
        assertTrue("RMS save file should exist on disk", savedFile.exists());
        assertTrue("RMS file size must be non-zero", savedFile.length() > 0);

        // Verify that temporary file was atomically cleaned up
        File tmpFile = new File(suiteFolder, savedFile.getName() + ".tmp");
        assertFalse("Temporary write file should not remain after successful atomic swap", tmpFile.exists());

        // Re-read from disk
        RecordStore reloaded = manager.openRecordStore("Scores", false);

        assertEquals("Number of records should match", 2, reloaded.getNumRecords());
        assertArrayEquals("Record 1 contents must match", scoreData1, reloaded.getRecord(id1));
        assertArrayEquals("Record 2 contents must match", scoreData2, reloaded.getRecord(id2));

        reloaded.closeRecordStore();
    }

    @Test
    public void testDeleteRecordStore() throws Exception {
        RecordStore store = manager.openRecordStore("ToDelete", true);
        store.addRecord(new byte[] { 1, 2, 3 }, 0, 3);
        store.closeRecordStore();

        String[] stores = manager.listRecordStores();
        assertNotNull(stores);
        boolean found = false;
        for (String s : stores) {
            if ("ToDelete".equals(s)) found = true;
        }
        assertTrue("Store must be listed", found);

        manager.deleteRecordStore("ToDelete");

        String[] remaining = manager.listRecordStores();
        if (remaining != null) {
            for (String s : remaining) {
                assertNotEquals("Deleted store must not appear in listing", "ToDelete", s);
            }
        }
    }
}
