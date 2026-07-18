/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.log;

public class LoggingEvent {
    public static final int DEBUG = 1;
    public static final int INFO = 2;
    public static final int WARN = 3;
    public static final int ERROR = 4;
    protected int level;
    protected String message;
    protected StackTraceElement location;
    protected boolean hasData = false;
    protected Object data;
    protected Throwable throwable;
    protected long eventTime = System.currentTimeMillis();

    public LoggingEvent() {
    }

    public LoggingEvent(int level, String message, StackTraceElement location, Throwable throwable) {
        this();
        this.level = level;
        this.message = message;
        this.location = location;
        this.throwable = throwable;
    }

    public LoggingEvent(int level, String message, StackTraceElement location, Throwable throwable, Object data) {
        this(level, message, location, throwable);
        this.setData(data);
    }

    public Object getData() {
        return this.data;
    }

    public void setData(Object data) {
        this.data = data;
        this.hasData = true;
    }

    public boolean hasData() {
        return this.hasData;
    }

    public String getFormatedData() {
        if (this.hasData()) {
            if (this.getData() == null) {
                return "{null}";
            }
            return this.getData().toString();
        }
        return "";
    }

    public long getEventTime() {
        return this.eventTime;
    }

    public int getLevel() {
        return this.level;
    }

    public StackTraceElement getLocation() {
        return this.location;
    }

    public String getMessage() {
        return this.message;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }
}

