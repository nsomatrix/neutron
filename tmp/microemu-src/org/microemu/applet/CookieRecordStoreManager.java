/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  netscape.javascript.JSObject
 */
package org.microemu.applet;

import java.applet.Applet;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;
import netscape.javascript.JSObject;
import org.microemu.MicroEmulator;
import org.microemu.RecordStoreManager;
import org.microemu.log.Logger;
import org.microemu.util.Base64Coder;
import org.microemu.util.ExtendedRecordListener;
import org.microemu.util.RecordStoreImpl;

public class CookieRecordStoreManager
implements RecordStoreManager {
    private static final int MAX_SPLIT_COOKIES = 5;
    private static final int MAX_COOKIE_SIZE = 3072;
    private ExtendedRecordListener recordListener = null;
    private Applet applet;
    private JSObject document;
    private HashMap cookies;
    private String expires;

    public CookieRecordStoreManager(Applet applet) {
        this.applet = applet;
        Calendar c = Calendar.getInstance();
        c.add(1, 1);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd-MM-yyyy hh:mm:ss z");
        this.expires = "; Max-Age=31536000";
        System.out.println("CookieRecordStoreManager: " + this.expires);
    }

    public void init(MicroEmulator emulator) {
    }

    public String getName() {
        return this.getClass().toString();
    }

    public void deleteRecordStore(String recordStoreName) throws RecordStoreNotFoundException, RecordStoreException {
        CookieContent cookieContent = (CookieContent)this.cookies.get(recordStoreName);
        if (cookieContent == null) {
            throw new RecordStoreNotFoundException(recordStoreName);
        }
        this.removeCookie(recordStoreName, cookieContent);
        this.cookies.remove(recordStoreName);
        this.fireRecordStoreListener(10, recordStoreName);
        System.out.println("deleteRecordStore: " + recordStoreName);
    }

    public void deleteStores() {
        Iterator it = this.cookies.keySet().iterator();
        while (it.hasNext()) {
            try {
                this.deleteRecordStore((String)it.next());
            }
            catch (RecordStoreException ex) {
                Logger.error(ex);
            }
        }
        System.out.println("deleteStores:");
    }

    public void init() {
        JSObject window = JSObject.getWindow((Applet)this.applet);
        this.document = (JSObject)window.getMember("document");
        this.cookies = new HashMap();
        String load = (String)this.document.getMember("cookie");
        if (load != null) {
            StringTokenizer st = new StringTokenizer(load, ";");
            while (st.hasMoreTokens()) {
                String token = st.nextToken().trim();
                int index = token.indexOf("=");
                if (index == -1 || token.charAt(index + 1) != 'a') continue;
                String first = token.substring(0, 1);
                String name = token.substring(1, index).trim();
                CookieContent content = (CookieContent)this.cookies.get(name);
                if (content == null) {
                    content = new CookieContent();
                    this.cookies.put(name, content);
                }
                if (first.equals("x")) {
                    content.setPart(0, token.substring(index + 2));
                } else {
                    try {
                        content.setPart(Integer.parseInt(first), token.substring(index + 2));
                    }
                    catch (NumberFormatException ex) {
                        // empty catch block
                    }
                }
                System.out.println("init: " + token.substring(0, index) + "(" + token.substring(index + 2) + ")");
            }
        }
        System.out.println("init: " + this.cookies.size());
    }

    public String[] listRecordStores() {
        System.out.println("listRecordStores:");
        String[] result = (String[])this.cookies.keySet().toArray();
        if (result.length == 0) {
            result = null;
        }
        return result;
    }

    public RecordStore openRecordStore(String recordStoreName, boolean createIfNecessary) throws RecordStoreNotFoundException {
        RecordStoreImpl result;
        CookieContent load = (CookieContent)this.cookies.get(recordStoreName);
        if (load != null) {
            try {
                byte[] data = Base64Coder.decode(load.toCharArray());
                result = new RecordStoreImpl((RecordStoreManager)this, new DataInputStream(new ByteArrayInputStream(data)));
            }
            catch (IOException ex) {
                Logger.error(ex);
                throw new RecordStoreNotFoundException(ex.getMessage());
            }
            System.out.println("openRecordStore: " + recordStoreName + " (" + load.getParts().length + ")");
        } else {
            if (!createIfNecessary) {
                throw new RecordStoreNotFoundException(recordStoreName);
            }
            result = new RecordStoreImpl((RecordStoreManager)this, recordStoreName);
            System.out.println("openRecordStore: " + recordStoreName + " (" + load + ")");
        }
        result.setOpen(true);
        if (this.recordListener != null) {
            result.addRecordListener(this.recordListener);
        }
        this.fireRecordStoreListener(8, recordStoreName);
        return result;
    }

    public void saveChanges(RecordStoreImpl recordStoreImpl) throws RecordStoreNotOpenException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);
        try {
            recordStoreImpl.write(dos);
            CookieContent cookieContent = new CookieContent(Base64Coder.encode(baos.toByteArray()));
            CookieContent previousCookie = (CookieContent)this.cookies.get(recordStoreImpl.getName());
            if (previousCookie != null) {
                this.removeCookie(recordStoreImpl.getName(), previousCookie);
            }
            this.cookies.put(recordStoreImpl.getName(), cookieContent);
            String[] parts = cookieContent.getParts();
            if (parts.length == 1) {
                this.document.setMember("cookie", (Object)("x" + recordStoreImpl.getName() + "=a" + parts[0] + this.expires));
            } else {
                for (int i = 0; i < parts.length; ++i) {
                    this.document.setMember("cookie", (Object)(i + recordStoreImpl.getName() + "=a" + parts[i] + this.expires));
                }
            }
            System.out.println("saveChanges: " + recordStoreImpl.getName() + " (" + cookieContent.getParts().length + ")");
        }
        catch (IOException ex) {
            Logger.error(ex);
        }
    }

    public int getSizeAvailable(RecordStoreImpl recordStoreImpl) {
        int size = 15360;
        size -= recordStoreImpl.getHeaderSize();
        try {
            RecordEnumeration en = recordStoreImpl.enumerateRecords(null, null, false);
            while (en.hasNextElement()) {
                size -= en.nextRecord().length + recordStoreImpl.getRecordHeaderSize();
            }
        }
        catch (RecordStoreException ex) {
            Logger.error(ex);
        }
        System.out.println("getSizeAvailable: " + size);
        return size;
    }

    private void removeCookie(String recordStoreName, CookieContent cookieContent) {
        String[] parts = cookieContent.getParts();
        if (parts.length == 1) {
            this.document.setMember("cookie", (Object)("x" + recordStoreName + "=r"));
        } else {
            for (int i = 0; i < parts.length; ++i) {
                this.document.setMember("cookie", (Object)(i + recordStoreName + "=r"));
            }
        }
        System.out.println("removeCookie: " + recordStoreName);
    }

    public void setRecordListener(ExtendedRecordListener recordListener) {
        this.recordListener = recordListener;
    }

    public void fireRecordStoreListener(int type, String recordStoreName) {
        if (this.recordListener != null) {
            this.recordListener.recordStoreEvent(type, System.currentTimeMillis(), recordStoreName);
        }
    }

    private class CookieContent {
        private String[] parts;

        public CookieContent() {
        }

        public CookieContent(char[] buffer) {
            this.parts = new String[buffer.length / 3072 + 1];
            System.out.println("CookieContent(before): " + this.parts.length);
            int index = 0;
            for (int i = 0; i < this.parts.length; ++i) {
                int size = 3072;
                if (index + size > buffer.length) {
                    size = buffer.length - index;
                }
                System.out.println("CookieContent: " + i + "," + index + "," + size);
                this.parts[i] = new String(buffer, index, size);
                index += size;
            }
        }

        public void setPart(int index, String content) {
            if (this.parts == null) {
                this.parts = new String[index + 1];
            } else if (this.parts.length <= index) {
                String[] newParts = new String[index + 1];
                System.arraycopy(this.parts, 0, newParts, 0, this.parts.length);
                this.parts = newParts;
            }
            System.out.println("setPart: " + index + "," + this.parts.length);
            this.parts[index] = content;
        }

        public String[] getParts() {
            System.out.println("getParts: " + this.parts);
            return this.parts;
        }

        public char[] toCharArray() {
            int size = 0;
            for (int i = 0; i < this.parts.length; ++i) {
                size += this.parts[i].length();
            }
            char[] result = new char[size];
            int index = 0;
            for (int i = 0; i < this.parts.length; ++i) {
                System.out.println("toCharArray: " + i + "," + index + "," + size + "," + this.parts[i].length());
                System.arraycopy(this.parts[i].toCharArray(), 0, result, index, this.parts[i].length());
                index += this.parts[i].length();
            }
            return result;
        }
    }
}

