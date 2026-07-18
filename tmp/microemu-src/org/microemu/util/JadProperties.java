/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.util;

import java.util.Iterator;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import org.microemu.util.JadMidletEntry;

public class JadProperties
extends Manifest {
    private static final long serialVersionUID = 1L;
    static String MIDLET_PREFIX = "MIDlet-";
    Vector midletEntries = null;
    String correctedJarURL = null;

    public void clear() {
        super.clear();
        this.midletEntries = null;
        this.correctedJarURL = null;
    }

    public String getSuiteName() {
        return this.getProperty("MIDlet-Name");
    }

    public String getVersion() {
        return this.getProperty("MIDlet-Version");
    }

    public String getVendor() {
        return this.getProperty("MIDlet-Vendor");
    }

    public String getProfile() {
        return this.getProperty("MicroEdition-Profile");
    }

    public String getConfiguration() {
        return this.getProperty("MicroEdition-Configuration");
    }

    public String getJarURL() {
        if (this.correctedJarURL != null) {
            return this.correctedJarURL;
        }
        return this.getProperty("MIDlet-Jar-URL");
    }

    public void setCorrectedJarURL(String correctedJarURL) {
        this.correctedJarURL = correctedJarURL;
    }

    public int getJarSize() {
        return Integer.parseInt(this.getProperty("MIDlet-Jar-Size"));
    }

    public Vector getMidletEntries() {
        if (this.midletEntries == null) {
            this.midletEntries = new Vector();
            Attributes attributes = super.getMainAttributes();
            Iterator<Object> it = attributes.keySet().iterator();
            while (it.hasNext()) {
                Attributes.Name key = (Attributes.Name)it.next();
                if (!key.toString().startsWith(MIDLET_PREFIX)) continue;
                try {
                    Integer.parseInt(key.toString().substring(MIDLET_PREFIX.length()));
                    String test = this.getProperty(key.toString());
                    int pos = test.indexOf(44);
                    String name = test.substring(0, pos).trim();
                    String icon = test.substring(pos + 1, test.indexOf(44, pos + 1)).trim();
                    String className = test.substring(test.indexOf(44, pos + 1) + 1).trim();
                    this.midletEntries.addElement(new JadMidletEntry(name, icon, className));
                }
                catch (NumberFormatException ex) {}
            }
        }
        return this.midletEntries;
    }

    public String getProperty(String key, String defaultValue) {
        Attributes attributes = super.getMainAttributes();
        String result = null;
        try {
            result = attributes.getValue(key);
        }
        catch (IllegalArgumentException e) {
            // empty catch block
        }
        if (result != null) {
            return result.trim();
        }
        return defaultValue;
    }

    public String getProperty(String key) {
        return this.getProperty(key, null);
    }
}

