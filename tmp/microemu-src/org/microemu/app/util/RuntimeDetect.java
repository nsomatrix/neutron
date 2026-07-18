/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

public class RuntimeDetect {
    private static boolean java13 = false;
    private static boolean java14 = false;
    private static boolean java15 = false;
    private static boolean inEclipseUnitTests = false;
    private static boolean runtimeDetected = false;

    private static synchronized void detect() {
        if (runtimeDetected) {
            return;
        }
        if (RuntimeDetect.detectJava14()) {
            RuntimeDetect.detectJava15();
            RuntimeDetect.detectEclipse();
        }
        runtimeDetected = true;
    }

    private static synchronized boolean detectJava14() {
        try {
            Class.forName("java.lang.StackTraceElement");
            java14 = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return java14;
    }

    private static boolean detectJava15() {
        try {
            RuntimeDetect.java5Function();
            java15 = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        return java15;
    }

    private static boolean java5Function() {
        return Thread.currentThread().getStackTrace() != null;
    }

    private static void detectEclipse() {
        StackTraceElement[] ste = new Throwable().getStackTrace();
        for (int i = 0; i < ste.length; ++i) {
            StackTraceElement s = ste[i];
            if (!s.getClassName().startsWith("org.eclipse.jdt")) continue;
            inEclipseUnitTests = true;
            break;
        }
    }

    public static boolean isJava13() {
        RuntimeDetect.detect();
        return java13;
    }

    public static boolean isJava14() {
        RuntimeDetect.detect();
        return java14;
    }

    public static boolean isJava15() {
        RuntimeDetect.detect();
        return java15;
    }

    public static boolean isInEclipseUnitTests() {
        RuntimeDetect.detect();
        return inEclipseUnitTests;
    }
}

