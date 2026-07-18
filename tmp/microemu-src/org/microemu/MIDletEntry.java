/*
 * Decompiled with CFR 0.152.
 */
package org.microemu;

public class MIDletEntry {
    private String name;
    private Class midletClass;

    public MIDletEntry(String name, Class midletClass) {
        this.name = name;
        this.midletClass = midletClass;
    }

    public String getName() {
        return this.name;
    }

    public Class getMIDletClass() {
        return this.midletClass;
    }
}

