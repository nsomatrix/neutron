/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.log;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.microemu.app.util.IOUtils;
import org.microemu.log.LoggerAppender;
import org.microemu.log.LoggerDataWrapper;
import org.microemu.log.LoggingEvent;
import org.microemu.log.StdOutAppender;

public class Logger {
    private static final String FQCN = Logger.class.getName();
    private static final Set fqcnSet = new HashSet();
    private static final Set logFunctionsSet = new HashSet();
    private static boolean java13 = false;
    private static boolean locationEnabled = true;
    private static List loggerAppenders = new Vector();

    public static boolean isDebugEnabled() {
        return true;
    }

    public static boolean isErrorEnabled() {
        return true;
    }

    public static boolean isLocationEnabled() {
        return locationEnabled;
    }

    public static void setLocationEnabled(boolean state) {
        locationEnabled = state;
    }

    private static StackTraceElement getLocation() {
        if (java13 || !locationEnabled) {
            return null;
        }
        try {
            StackTraceElement[] ste = new Throwable().getStackTrace();
            boolean wrapperFound = false;
            for (int i = 0; i < ste.length - 1; ++i) {
                if (fqcnSet.contains(ste[i].getClassName())) {
                    wrapperFound = false;
                    String nextClassName = ste[i + 1].getClassName();
                    if (nextClassName.startsWith("java.") || nextClassName.startsWith("sun.") || fqcnSet.contains(nextClassName)) continue;
                    if (logFunctionsSet.contains(ste[i + 1].getMethodName())) {
                        wrapperFound = true;
                        continue;
                    }
                    if (nextClassName.startsWith("$Proxy")) {
                        return ste[i + 2];
                    }
                    return ste[i + 1];
                }
                if (!wrapperFound || logFunctionsSet.contains(ste[i].getMethodName())) continue;
                return ste[i];
            }
            return ste[ste.length - 1];
        }
        catch (Throwable e) {
            java13 = true;
            return null;
        }
    }

    private static void write(int level, String message, Throwable throwable) {
        while (message != null && message.endsWith("\n")) {
            message = message.substring(0, message.length() - 1);
        }
        Logger.callAppenders(new LoggingEvent(level, message, Logger.getLocation(), throwable));
    }

    private static void write(int level, String message, Throwable throwable, Object data) {
        Logger.callAppenders(new LoggingEvent(level, message, Logger.getLocation(), throwable, data));
    }

    public static void debug(String message) {
        if (Logger.isDebugEnabled()) {
            Logger.write(1, message, null);
        }
    }

    public static void debug(String message, Throwable t) {
        if (Logger.isDebugEnabled()) {
            Logger.write(1, message, t);
        }
    }

    public static void debug(Throwable t) {
        if (Logger.isDebugEnabled()) {
            Logger.write(1, "error", t);
        }
    }

    public static void debug(String message, String v) {
        if (Logger.isDebugEnabled()) {
            Logger.write(1, message, null, v);
        }
    }

    public static void debug(String message, Object o) {
        if (Logger.isDebugEnabled()) {
            Logger.write(1, message, null, new LoggerDataWrapper(o));
        }
    }

    public static void debug(String message, String v1, String v2) {
        if (Logger.isDebugEnabled()) {
            Logger.write(1, message, null, new LoggerDataWrapper(v1, v2));
        }
    }

    public static void debug(String message, long v) {
        if (Logger.isDebugEnabled()) {
            Logger.write(1, message, null, new LoggerDataWrapper(v));
        }
    }

    public static void debug0x(String message, long v) {
        if (Logger.isDebugEnabled()) {
            Logger.write(1, message, null, new LoggerDataWrapper("0x" + Long.toHexString(v)));
        }
    }

    public static void debug(String message, long v1, long v2) {
        if (Logger.isDebugEnabled()) {
            Logger.write(1, message, null, new LoggerDataWrapper(v1, v2));
        }
    }

    public static void debug(String message, boolean v) {
        if (Logger.isDebugEnabled()) {
            Logger.write(1, message, null, new LoggerDataWrapper(v));
        }
    }

    public static void debugClassLoader(String message, Object obj) {
        Class<?> klass;
        if (obj == null) {
            Logger.write(1, message + " no class, no object", null, null);
            return;
        }
        StringBuffer buf = new StringBuffer();
        buf.append(message).append(" ");
        if (obj instanceof Class) {
            klass = (Class<?>)obj;
            buf.append("class ");
        } else {
            klass = obj.getClass();
            buf.append("instance ");
        }
        buf.append(klass.getName() + " loaded by ");
        if (klass.getClassLoader() != null) {
            buf.append(klass.getClassLoader().hashCode());
            buf.append(" ");
            buf.append(klass.getClassLoader().getClass().getName());
        } else {
            buf.append("system");
        }
        Logger.write(1, buf.toString(), null, null);
    }

    public static void info(String message) {
        if (Logger.isErrorEnabled()) {
            Logger.write(2, message, null);
        }
    }

    public static void info(Object message) {
        if (Logger.isErrorEnabled()) {
            Logger.write(2, "" + message, null);
        }
    }

    public static void info(String message, String data) {
        if (Logger.isErrorEnabled()) {
            Logger.write(2, message, null, data);
        }
    }

    public static void warn(String message) {
        if (Logger.isErrorEnabled()) {
            Logger.write(3, message, null);
        }
    }

    public static void error(String message) {
        if (Logger.isErrorEnabled()) {
            Logger.write(4, "error " + message, null);
        }
    }

    public static void error(Object message) {
        if (Logger.isErrorEnabled()) {
            Logger.write(4, "error " + message, null);
        }
    }

    public static void error(String message, long v) {
        if (Logger.isErrorEnabled()) {
            Logger.write(4, "error " + message, null, new LoggerDataWrapper(v));
        }
    }

    public static void error(String message, String v) {
        if (Logger.isErrorEnabled()) {
            Logger.write(4, "error " + message, null, v);
        }
    }

    public static void error(String message, String v, Throwable t) {
        if (Logger.isErrorEnabled()) {
            Logger.write(4, "error " + message, t, v);
        }
    }

    public static void error(Throwable t) {
        if (Logger.isErrorEnabled()) {
            Logger.write(4, "error " + t.toString(), t);
        }
    }

    public static void error(String message, Throwable t) {
        if (Logger.isErrorEnabled()) {
            Logger.write(4, "error " + message + " " + t.toString(), t);
        }
    }

    private static void callAppenders(LoggingEvent event) {
        Iterator iter = loggerAppenders.iterator();
        while (iter.hasNext()) {
            LoggerAppender a = (LoggerAppender)iter.next();
            a.append(event);
        }
    }

    public static void addLogOrigin(Class origin) {
        fqcnSet.add(origin.getName());
    }

    public static void addAppender(LoggerAppender newAppender) {
        loggerAppenders.add(newAppender);
    }

    public static void removeAppender(LoggerAppender appender) {
        loggerAppenders.remove(appender);
    }

    public static void removeAllAppenders() {
        loggerAppenders.clear();
    }

    public static void threadDumpToConsole() {
        try {
            StringBuffer out = new StringBuffer("Full ThreadDump\n");
            Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
            Iterator<Map.Entry<Thread, StackTraceElement[]>> iterator = traces.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Thread, StackTraceElement[]> entry = iterator.next();
                Thread thread = entry.getKey();
                out.append("Thread= " + thread.getName() + " " + (thread.isDaemon() ? "daemon" : "") + " prio=" + thread.getPriority() + "id=" + thread.getId() + " " + (Object)((Object)thread.getState()));
                out.append("\n");
                StackTraceElement[] ste = entry.getValue();
                for (int i = 0; i < ste.length; ++i) {
                    out.append("\t");
                    out.append(ste[i].toString());
                    out.append("\n");
                }
                out.append("---------------------------------\n");
            }
            Logger.info(out.toString());
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    public static void threadDumpToFile() {
        SimpleDateFormat fmt = new SimpleDateFormat("MM-dd_HH-mm-ss");
        FileWriter out = null;
        try {
            File file = new File("ThreadDump-" + fmt.format(new Date()) + ".log");
            out = new FileWriter(file);
            Map<Thread, StackTraceElement[]> traces = Thread.getAllStackTraces();
            Iterator<Map.Entry<Thread, StackTraceElement[]>> iterator = traces.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Thread, StackTraceElement[]> entry = iterator.next();
                Thread thread = entry.getKey();
                out.write("Thread= " + thread.getName() + " " + (thread.isDaemon() ? "daemon" : "") + " prio=" + thread.getPriority() + "id=" + thread.getId() + " " + (Object)((Object)thread.getState()));
                out.write("\n");
                StackTraceElement[] ste = entry.getValue();
                for (int i = 0; i < ste.length; ++i) {
                    out.write("\t");
                    out.write(ste[i].toString());
                    out.write("\n");
                }
                out.write("---------------------------------\n");
            }
            out.close();
            out = null;
            Logger.info("Full ThreadDump created " + file.getAbsolutePath());
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(out);
            catch (Throwable throwable2) {
                IOUtils.closeQuietly(out);
                throw throwable2;
            }
        }
        IOUtils.closeQuietly(out);
    }

    static {
        fqcnSet.add(FQCN);
        Logger.addAppender(new StdOutAppender());
        logFunctionsSet.add("debug");
        logFunctionsSet.add("log");
        logFunctionsSet.add("error");
        logFunctionsSet.add("fatal");
        logFunctionsSet.add("info");
        logFunctionsSet.add("warn");
    }
}

