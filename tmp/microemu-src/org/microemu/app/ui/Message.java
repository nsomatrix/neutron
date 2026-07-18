/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.ui;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import org.microemu.app.ui.MessageListener;
import org.microemu.log.Logger;

public class Message {
    public static final int ERROR = 0;
    public static final int INFO = 1;
    public static final int WARN = 2;
    private static List listeners = new Vector();

    public static void error(String title, String text) {
        Logger.error("Message: " + title + ": " + text);
        Message.callListeners(0, title, text, null);
    }

    public static void error(String text) {
        Logger.error("Message: Error: " + text);
        Message.callListeners(0, "Error", text, null);
    }

    public static void error(String title, String text, Throwable throwable) {
        Logger.error("Message: " + title + ": " + text, throwable);
        Message.callListeners(0, title, text, throwable);
    }

    public static void error(String text, Throwable throwable) {
        Logger.error("Message: Error : " + text, throwable);
        Message.callListeners(0, "Error", text, throwable);
    }

    public static void info(String text) {
        Logger.info("Message: info: " + text);
        Message.callListeners(1, "Info", text, null);
    }

    public static void warn(String text) {
        Logger.warn("Message: warn: " + text);
        Message.callListeners(1, "Warning", text, null);
    }

    public static String getCauseMessage(Throwable throwable) {
        if (throwable.getCause() == null) {
            return throwable.toString();
        }
        return Message.getCauseMessage(throwable.getCause());
    }

    private static void callListeners(int level, String title, String text, Throwable throwable) {
        Iterator iter = listeners.iterator();
        while (iter.hasNext()) {
            MessageListener a = (MessageListener)iter.next();
            a.showMessage(level, title, text, throwable);
        }
    }

    public static void addListener(MessageListener newListener) {
        listeners.add(newListener);
    }

    static {
        Logger.addLogOrigin(Message.class);
    }
}

