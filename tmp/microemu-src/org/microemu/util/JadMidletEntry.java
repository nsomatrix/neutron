/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.util;

public class JadMidletEntry {
    String name;
    String icon;
    String className;

    JadMidletEntry(String name, String icon, String className) {
        this.name = name;
        this.icon = icon;
        this.className = className;
    }

    public String getClassName() {
        return this.className;
    }

    public String getName() {
        return this.name;
    }

    public String toString() {
        return this.name + "+" + this.icon + "+" + this.className;
    }
}

