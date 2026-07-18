/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Timer;

public class ThreadUtils {
    private static boolean java13 = false;
    private static boolean java14 = false;

    public static Timer createTimer(String name) {
        try {
            Constructor c = Timer.class.getConstructor(String.class);
            return (Timer)c.newInstance(name);
        }
        catch (Throwable e) {
            return new Timer();
        }
    }

    public static String getCallLocation(String fqn) {
        if (!java13) {
            try {
                StackTraceElement[] ste = new Throwable().getStackTrace();
                for (int i = 0; i < ste.length - 1; ++i) {
                    StackTraceElement callLocation;
                    String nextClassName;
                    if (!fqn.equals(ste[i].getClassName()) || (nextClassName = (callLocation = ste[i + 1]).getClassName()).equals(fqn)) continue;
                    return callLocation.toString();
                }
            }
            catch (Throwable e) {
                java13 = true;
            }
        }
        return null;
    }

    public static String getTreadStackTrace(Thread t) {
        if (java14) {
            return "";
        }
        try {
            Method m = t.getClass().getMethod("getStackTrace", null);
            StackTraceElement[] trace = (StackTraceElement[])m.invoke(t, null);
            StringBuffer b = new StringBuffer();
            for (int i = 0; i < trace.length; ++i) {
                b.append("\n\tat ").append(trace[i]);
            }
            return b.toString();
        }
        catch (Throwable e) {
            java14 = true;
            return "";
        }
    }
}

