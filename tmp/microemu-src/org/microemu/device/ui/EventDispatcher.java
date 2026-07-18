/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.device.ui;

import org.microemu.device.DeviceFactory;

public class EventDispatcher
implements Runnable {
    public static final String EVENT_DISPATCHER_NAME = "event-thread";
    public static int maxFps = -1;
    private volatile boolean cancelled = false;
    private Event head = null;
    private Event tail = null;
    private PaintEvent scheduledPaintEvent = null;
    private PointerEvent scheduledPointerDraggedEvent = null;
    private Object serviceRepaintsLock = new Object();
    private long lastPaintEventTime = 0L;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        while (!this.cancelled) {
            Event event = null;
            Object object = this;
            synchronized (object) {
                if (this.head != null) {
                    long difference;
                    event = this.head;
                    if (maxFps > 0 && event instanceof PaintEvent && (difference = System.currentTimeMillis() - this.lastPaintEventTime) < (long)(1000 / maxFps)) {
                        event = null;
                        try {
                            this.wait((long)(1000 / maxFps) - difference);
                        }
                        catch (InterruptedException e) {
                            // empty catch block
                        }
                    }
                    if (event != null) {
                        this.head = event.next;
                        if (this.head == null) {
                            this.tail = null;
                        }
                        if (event instanceof PointerEvent && ((PointerEvent)event).type == 2) {
                            this.scheduledPointerDraggedEvent = null;
                        }
                    }
                } else {
                    try {
                        this.wait();
                    }
                    catch (InterruptedException interruptedException) {
                        // empty catch block
                    }
                }
            }
            if (event == null) continue;
            if (event instanceof PaintEvent) {
                object = this.serviceRepaintsLock;
                synchronized (object) {
                    this.scheduledPaintEvent = null;
                    this.lastPaintEventTime = System.currentTimeMillis();
                    this.post(event);
                    this.serviceRepaintsLock.notifyAll();
                    continue;
                }
            }
            this.post(event);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public final void cancel() {
        this.cancelled = true;
        EventDispatcher eventDispatcher = this;
        synchronized (eventDispatcher) {
            this.notify();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void put(Event event) {
        EventDispatcher eventDispatcher = this;
        synchronized (eventDispatcher) {
            if (event instanceof PaintEvent && this.scheduledPaintEvent != null) {
                this.scheduledPaintEvent.merge((PaintEvent)event);
            } else if (event instanceof PointerEvent && this.scheduledPointerDraggedEvent != null && ((PointerEvent)event).type == 2) {
                this.scheduledPointerDraggedEvent.x = ((PointerEvent)event).x;
                this.scheduledPointerDraggedEvent.y = ((PointerEvent)event).y;
            } else {
                if (event instanceof PaintEvent) {
                    this.scheduledPaintEvent = (PaintEvent)event;
                }
                if (event instanceof PointerEvent && ((PointerEvent)event).type == 2) {
                    this.scheduledPointerDraggedEvent = (PointerEvent)event;
                }
                if (this.tail != null) {
                    this.tail.next = event;
                }
                this.tail = event;
                if (this.head == null) {
                    this.head = event;
                }
                this.notify();
            }
        }
    }

    public void put(Runnable runnable) {
        this.put(new RunnableEvent(runnable));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void serviceRepaints() {
        Object object = this.serviceRepaintsLock;
        synchronized (object) {
            EventDispatcher eventDispatcher = this;
            synchronized (eventDispatcher) {
                if (this.scheduledPaintEvent == null) {
                    return;
                }
            }
            try {
                this.serviceRepaintsLock.wait();
            }
            catch (InterruptedException interruptedException) {
                // empty catch block
            }
        }
    }

    protected void post(Event event) {
        event.run();
    }

    private class RunnableEvent
    extends Event {
        private Runnable runnable;

        public RunnableEvent(Runnable runnable) {
            this.runnable = runnable;
        }

        public void run() {
            this.runnable.run();
        }
    }

    public final class HideNotifyEvent
    extends Event {
        private Runnable runnable;

        public HideNotifyEvent(Runnable runnable) {
            this.runnable = runnable;
        }

        public void run() {
            this.runnable.run();
        }
    }

    public final class ShowNotifyEvent
    extends Event {
        private Runnable runnable;

        public ShowNotifyEvent(Runnable runnable) {
            this.runnable = runnable;
        }

        public void run() {
            this.runnable.run();
        }
    }

    public final class PointerEvent
    extends Event {
        public static final short POINTER_PRESSED = 0;
        public static final short POINTER_RELEASED = 1;
        public static final short POINTER_DRAGGED = 2;
        private Runnable runnable;
        private short type;
        private int x;
        private int y;

        public PointerEvent(Runnable runnable, short type, int x, int y) {
            this.runnable = runnable;
            this.type = type;
            this.x = x;
            this.y = y;
        }

        public void run() {
            this.runnable.run();
        }
    }

    public final class PaintEvent
    extends Event {
        private int x;
        private int y;
        private int width;
        private int height;

        public PaintEvent(int x, int y, int width, int height) {
            this.x = -1;
            this.y = -1;
            this.width = -1;
            this.height = -1;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }

        public void run() {
            DeviceFactory.getDevice().getDeviceDisplay().repaint(this.x, this.y, this.width, this.height);
        }

        public final void merge(PaintEvent event) {
            int xMax = this.x + this.width;
            int yMax = this.y + this.height;
            this.x = Math.min(this.x, event.x);
            xMax = Math.max(xMax, event.x + event.width);
            this.y = Math.min(this.y, event.y);
            yMax = Math.max(yMax, event.y + event.height);
            this.width = xMax - this.x;
            this.height = yMax - this.y;
        }
    }

    public abstract class Event
    implements Runnable {
        Event next = null;
    }
}

