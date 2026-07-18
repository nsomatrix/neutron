/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.WeakHashMap;
import org.microemu.MIDletBridge;
import org.microemu.MIDletContext;
import org.microemu.app.util.MIDletThread;
import org.microemu.app.util.MIDletTimerTask;
import org.microemu.log.Logger;

public class MIDletTimer
extends Timer
implements Runnable {
    private static Map midlets = new WeakHashMap();
    private String name;
    private MIDletContext midletContext;
    List tasks;
    private boolean cancelled;
    private MIDletThread thread;

    public MIDletTimer() {
        StackTraceElement[] ste = new Throwable().getStackTrace();
        this.name = ste[1].getClassName() + "." + ste[1].getMethodName();
        this.tasks = new ArrayList();
        this.cancelled = false;
        this.thread = new MIDletThread(this);
        this.thread.start();
    }

    public void schedule(TimerTask task, Date time) {
        MIDletTimer.register(this);
        this.schedule(task, time.getTime(), -1L, false);
    }

    public void schedule(TimerTask task, Date firstTime, long period) {
        MIDletTimer.register(this);
        this.schedule(task, firstTime.getTime(), period, false);
    }

    public void schedule(TimerTask task, long delay) {
        MIDletTimer.register(this);
        this.schedule(task, System.currentTimeMillis() + delay, -1L, false);
    }

    public void schedule(TimerTask task, long delay, long period) {
        MIDletTimer.register(this);
        this.schedule(task, System.currentTimeMillis() + delay, period, false);
    }

    public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
        MIDletTimer.register(this);
        this.schedule(task, firstTime.getTime(), period, true);
    }

    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        MIDletTimer.register(this);
        this.schedule(task, System.currentTimeMillis() + delay, period, true);
    }

    public void cancel() {
        MIDletTimer.unregister(this);
        this.terminate();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void run() {
        while (!this.cancelled) {
            TimerTask task = null;
            long nextTimeTask = Long.MAX_VALUE;
            List list = this.tasks;
            synchronized (list) {
                Iterator it = this.tasks.iterator();
                while (it.hasNext()) {
                    MIDletTimerTask candidate = (MIDletTimerTask)it.next();
                    if (candidate.time > System.currentTimeMillis()) {
                        if (candidate.time >= nextTimeTask) continue;
                        nextTimeTask = candidate.time;
                        continue;
                    }
                    if (task == null) {
                        task = candidate;
                        continue;
                    }
                    if (candidate.time < ((MIDletTimerTask)task).time) {
                        if (((MIDletTimerTask)task).time < nextTimeTask) {
                            nextTimeTask = ((MIDletTimerTask)task).time;
                        }
                        task = candidate;
                        continue;
                    }
                    if (candidate.time >= nextTimeTask) continue;
                    nextTimeTask = candidate.time;
                }
                if (task != null) {
                    if (((MIDletTimerTask)task).period > 0L) {
                        ((MIDletTimerTask)task).oneTimeTaskExcecuted = true;
                    }
                    this.tasks.remove(task);
                }
            }
            if (task != null) {
                try {
                    task.run();
                    list = this.tasks;
                    synchronized (list) {
                        if (((MIDletTimerTask)task).period > 0L) {
                            ((MIDletTimerTask)task).time = System.currentTimeMillis() + ((MIDletTimerTask)task).period;
                            this.tasks.add(task);
                            if (((MIDletTimerTask)task).time < nextTimeTask) {
                                nextTimeTask = ((MIDletTimerTask)task).time;
                            }
                        }
                    }
                }
                catch (Throwable t) {
                    Logger.debug("MIDletTimerTask throws", t);
                }
            }
            list = this.tasks;
            synchronized (list) {
                try {
                    if (nextTimeTask == Long.MAX_VALUE) {
                        this.tasks.wait();
                    } else {
                        long timeout = nextTimeTask - System.currentTimeMillis();
                        if (timeout > 0L) {
                            this.tasks.wait(timeout);
                        }
                    }
                }
                catch (InterruptedException e) {
                    // empty catch block
                }
            }
        }
    }

    private void terminate() {
        this.cancelled = true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void schedule(TimerTask task, long time, long period, boolean fixedRate) {
        List list = this.tasks;
        synchronized (list) {
            ((MIDletTimerTask)task).timer = this;
            ((MIDletTimerTask)task).time = time;
            ((MIDletTimerTask)task).period = period;
            this.tasks.add(task);
            this.tasks.notify();
        }
    }

    private static void register(MIDletTimer timer) {
        if (timer.midletContext == null) {
            timer.midletContext = MIDletBridge.getMIDletContext();
        }
        if (timer.midletContext == null) {
            Logger.error("Creating Timer with no MIDlet context", new Throwable());
            return;
        }
        HashMap<MIDletTimer, MIDletContext> timers = (HashMap<MIDletTimer, MIDletContext>)midlets.get(timer.midletContext);
        if (timers == null) {
            timers = new HashMap<MIDletTimer, MIDletContext>();
            midlets.put(timer.midletContext, timers);
        }
        timers.put(timer, timer.midletContext);
    }

    private static void unregister(MIDletTimer timer) {
        if (timer.midletContext == null) {
            return;
        }
        Map timers = (Map)midlets.get(timer.midletContext);
        if (timers == null) {
            return;
        }
        timers.remove(timer);
    }

    public static void contextDestroyed(MIDletContext midletContext) {
        if (midletContext == null) {
            return;
        }
        Map timers = (Map)midlets.get(midletContext);
        if (timers != null) {
            MIDletTimer.terminateTimers(timers);
            midlets.remove(midletContext);
        }
    }

    private static void terminateTimers(Map timers) {
        Iterator iter = timers.keySet().iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o == null) continue;
            if (o instanceof MIDletTimer) {
                MIDletTimer tm = (MIDletTimer)o;
                Logger.warn("MIDlet timer created from [" + tm.name + "] still running");
                tm.terminate();
                continue;
            }
            Logger.debug("unrecognized Object [" + o.getClass().getName() + "]");
        }
    }
}

