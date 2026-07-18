/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;
import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.app.Config;
import org.microemu.log.Logger;
import org.microemu.util.ExtendedRecordListener;
import org.microemu.util.RecordStoreImpl;

public class FileRecordStoreManager
implements RecordStoreManager {
    private static final String RECORD_STORE_SUFFIX = ".rs";
    private static final List replaceChars = new Vector();
    private MicroEmulator emulator;
    private Hashtable testOpenRecordStores = new Hashtable();
    private ExtendedRecordListener recordListener = null;
    private AccessControlContext acc;
    private FilenameFilter filter = new FilenameFilter(){

        public boolean accept(File dir, String name) {
            return name.endsWith(FileRecordStoreManager.RECORD_STORE_SUFFIX);
        }
    };

    public void init(MicroEmulator emulator) {
        this.emulator = emulator;
        this.acc = AccessController.getContext();
    }

    public String getName() {
        return "File record store";
    }

    protected File getSuiteFolder() {
        return new File(Config.getConfigPath(), "suite-" + this.emulator.getLauncher().getSuiteName());
    }

    private static String escapeCharacter(String charcter) {
        return "_%%" + charcter.charAt(0) + "%%_";
    }

    static String recordStoreName2FileName(String recordStoreName) {
        Iterator iterator = replaceChars.iterator();
        while (iterator.hasNext()) {
            String c = (String)iterator.next();
            String newValue = FileRecordStoreManager.escapeCharacter(c);
            if (c.equals("\\")) {
                c = "\\\\";
            }
            c = "[" + c + "]";
            recordStoreName = recordStoreName.replaceAll(c, newValue);
        }
        return recordStoreName + RECORD_STORE_SUFFIX;
    }

    static String fileName2RecordStoreName(String fileName) {
        Iterator iterator = replaceChars.iterator();
        while (iterator.hasNext()) {
            String c = (String)iterator.next();
            String newValue = FileRecordStoreManager.escapeCharacter(c);
            if (c.equals("\\")) {
                c = "\\\\";
            }
            fileName = fileName.replaceAll(newValue, c);
        }
        return fileName.substring(0, fileName.length() - RECORD_STORE_SUFFIX.length());
    }

    public void deleteRecordStore(final String recordStoreName) throws RecordStoreNotFoundException, RecordStoreException {
        final File storeFile = new File(this.getSuiteFolder(), FileRecordStoreManager.recordStoreName2FileName(recordStoreName));
        RecordStoreImpl recordStoreImpl = (RecordStoreImpl)this.testOpenRecordStores.get(storeFile.getName());
        if (recordStoreImpl != null && recordStoreImpl.isOpen()) {
            throw new RecordStoreException();
        }
        try {
            recordStoreImpl = this.loadFromDisk(storeFile);
        }
        catch (FileNotFoundException ex) {
            throw new RecordStoreNotFoundException(recordStoreName);
        }
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws FileNotFoundException {
                    storeFile.delete();
                    FileRecordStoreManager.this.fireRecordStoreListener(10, recordStoreName);
                    return null;
                }
            }, this.acc);
        }
        catch (PrivilegedActionException e) {
            Logger.error("Unable remove file " + storeFile, e);
            throw new RecordStoreException();
        }
    }

    public RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) throws RecordStoreException {
        RecordStoreImpl recordStoreImpl;
        File storeFile = new File(this.getSuiteFolder(), FileRecordStoreManager.recordStoreName2FileName(recordStoreName));
        try {
            recordStoreImpl = this.loadFromDisk(storeFile);
        }
        catch (FileNotFoundException e) {
            if (!createIfNecessary) {
                throw new RecordStoreNotFoundException(recordStoreName);
            }
            recordStoreImpl = new RecordStoreImpl((RecordStoreManager)this, recordStoreName);
            this.saveToDisk(storeFile, recordStoreImpl);
        }
        recordStoreImpl.setOpen(true);
        if (this.recordListener != null) {
            recordStoreImpl.addRecordListener(this.recordListener);
        }
        this.testOpenRecordStores.put(storeFile.getName(), recordStoreImpl);
        this.fireRecordStoreListener(8, recordStoreName);
        return recordStoreImpl;
    }

    public String[] listRecordStores() {
        String[] result;
        try {
            result = (String[])AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() {
                    return FileRecordStoreManager.this.getSuiteFolder().list(FileRecordStoreManager.this.filter);
                }
            }, this.acc);
        }
        catch (PrivilegedActionException e) {
            Logger.error("Unable to access storeFiles", e);
            return null;
        }
        if (result != null) {
            if (result.length == 0) {
                result = null;
            } else {
                for (int i = 0; i < result.length; ++i) {
                    result[i] = FileRecordStoreManager.fileName2RecordStoreName(result[i]);
                }
            }
        }
        return result;
    }

    public void saveChanges(RecordStoreImpl recordStoreImpl) throws RecordStoreNotOpenException, RecordStoreException {
        File storeFile = new File(this.getSuiteFolder(), FileRecordStoreManager.recordStoreName2FileName(recordStoreImpl.getName()));
        this.saveToDisk(storeFile, recordStoreImpl);
    }

    public void init() {
    }

    public void deleteStores() {
        String[] stores = this.listRecordStores();
        for (int i = 0; i < stores.length; ++i) {
            String store = stores[i];
            try {
                this.deleteRecordStore(store);
                continue;
            }
            catch (RecordStoreException e) {
                Logger.debug("deleteRecordStore", e);
            }
        }
    }

    private RecordStoreImpl loadFromDisk(final File recordStoreFile) throws FileNotFoundException {
        try {
            return (RecordStoreImpl)AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws FileNotFoundException {
                    return FileRecordStoreManager.this.loadFromDiskSecure(recordStoreFile);
                }
            }, this.acc);
        }
        catch (PrivilegedActionException e) {
            if (e.getCause() instanceof FileNotFoundException) {
                throw (FileNotFoundException)e.getCause();
            }
            Logger.error("Unable access file " + recordStoreFile, e);
            throw new FileNotFoundException();
        }
    }

    private RecordStoreImpl loadFromDiskSecure(File recordStoreFile) throws FileNotFoundException {
        RecordStoreImpl store = null;
        try {
            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(recordStoreFile)));
            store = new RecordStoreImpl((RecordStoreManager)this, dis);
            dis.close();
        }
        catch (FileNotFoundException e) {
            throw e;
        }
        catch (IOException e) {
            Logger.error("RecordStore.loadFromDisk: ERROR reading " + recordStoreFile.getName(), e);
        }
        return store;
    }

    private void saveToDisk(final File recordStoreFile, final RecordStoreImpl recordStore) throws RecordStoreException {
        try {
            AccessController.doPrivileged(new PrivilegedExceptionAction(){

                public Object run() throws RecordStoreException {
                    FileRecordStoreManager.this.saveToDiskSecure(recordStoreFile, recordStore);
                    return null;
                }
            }, this.acc);
        }
        catch (PrivilegedActionException e) {
            if (e.getCause() instanceof RecordStoreException) {
                throw (RecordStoreException)e.getCause();
            }
            Logger.error("Unable access file " + recordStoreFile, e);
            throw new RecordStoreException();
        }
    }

    private void saveToDiskSecure(File recordStoreFile, RecordStoreImpl recordStore) throws RecordStoreException {
        if (!recordStoreFile.getParentFile().exists() && !recordStoreFile.getParentFile().mkdirs()) {
            throw new RecordStoreException("Unable to create recordStore directory");
        }
        try {
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(recordStoreFile)));
            recordStore.write(dos);
            dos.close();
        }
        catch (IOException e) {
            Logger.error("RecordStore.saveToDisk: ERROR writting object to " + recordStoreFile.getName(), e);
            throw new RecordStoreException(e.getMessage());
        }
    }

    public int getSizeAvailable(RecordStoreImpl recordStoreImpl) {
        return 0x100000;
    }

    public void setRecordListener(ExtendedRecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public void fireRecordStoreListener(int type, String recordStoreName) {
        if (this.recordListener != null) {
            this.recordListener.recordStoreEvent(type, System.currentTimeMillis(), recordStoreName);
        }
    }

    static {
        replaceChars.add(":");
        replaceChars.add("*");
        replaceChars.add("?");
        replaceChars.add("=");
        replaceChars.add("|");
        replaceChars.add("/");
        replaceChars.add("\\");
        replaceChars.add("\"");
    }
}

