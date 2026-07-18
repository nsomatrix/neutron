/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.log;

import java.io.PrintStream;
import org.microemu.log.LoggerAppender;
import org.microemu.log.LoggingEvent;

public class StdOutAppender
implements LoggerAppender {
    public static boolean enabled = true;

    public static String formatLocation(StackTraceElement ste) {
        if (ste == null) {
            return "";
        }
        return ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")";
    }

    public void append(LoggingEvent event) {
        String location;
        if (!enabled) {
            return;
        }
        PrintStream out = System.out;
        if (event.getLevel() == 4) {
            out = System.err;
        }
        String data = "";
        if (event.hasData()) {
            data = " [" + event.getFormatedData() + "]";
        }
        if ((location = StdOutAppender.formatLocation(event.getLocation())).length() > 0) {
            location = "\n\t  " + location;
        }
        out.println(event.getMessage() + data + location);
        if (event.getThrowable() != null) {
            event.getThrowable().printStackTrace(out);
        }
    }
}

