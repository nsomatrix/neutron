/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.log;

public class LoggerDataWrapper {
    private String text;

    public LoggerDataWrapper(boolean v1) {
        this.text = String.valueOf(v1);
    }

    public LoggerDataWrapper(long v1) {
        this.text = String.valueOf(v1);
    }

    public LoggerDataWrapper(Object v1) {
        this.text = String.valueOf(v1);
    }

    public LoggerDataWrapper(long v1, long v2) {
        this.text = String.valueOf(v1) + " " + String.valueOf(v2);
    }

    public LoggerDataWrapper(String v1, String v2) {
        this.text = v1 + " " + v2;
    }

    public String toString() {
        return this.text;
    }
}

