/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui;

public interface MessageListener {
    public static final int ERROR = 0;
    public static final int INFO = 1;
    public static final int WARN = 2;

    public void showMessage(int var1, String var2, String var3, Throwable var4);
}

