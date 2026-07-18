/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.log;

import java.util.LinkedList;
import java.util.List;
import org.microemu.log.LoggerAppender;
import org.microemu.log.LoggingEvent;

public class QueueAppender
implements LoggerAppender {
    private int buferSize;
    private List queue = new LinkedList();

    public QueueAppender(int buferSize) {
        this.buferSize = buferSize;
    }

    public void append(LoggingEvent event) {
        this.queue.add(event);
        if (this.queue.size() > this.buferSize) {
            this.queue.remove(0);
        }
    }

    public LoggingEvent poll() {
        if (this.queue.size() == 0) {
            return null;
        }
        return (LoggingEvent)this.queue.remove(0);
    }
}

