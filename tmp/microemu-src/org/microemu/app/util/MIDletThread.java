/*
 * Decompiled with CFR 0.152.
 */
package org.microemu.app.util;

import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;
import org.microemu.MIDletBridge;
import org.microemu.MIDletContext;
import org.microemu.app.util.MIDletTimer;
import org.microemu.log.Logger;
import org.microemu.util.ThreadUtils;

public class MIDletThread
extends Thread {
    public static int graceTerminationPeriod = 5000;
    private static final String THREAD_NAME_PREFIX = "MIDletThread-";
    private static boolean terminator = false;
    private static Map midlets = new WeakHashMap();
    private static int threadInitNumber;
    private String callLocation;

    private static synchronized int nextThreadNum() {
        return threadInitNumber++;
    }

    public MIDletThread() {
        super(THREAD_NAME_PREFIX + MIDletThread.nextThreadNum());
        MIDletThread.register(this);
    }

    public MIDletThread(Runnable target) {
        super(target, THREAD_NAME_PREFIX + MIDletThread.nextThreadNum());
        MIDletThread.register(this);
    }

    public MIDletThread(Runnable target, String name) {
        super(target, THREAD_NAME_PREFIX + name);
        MIDletThread.register(this);
    }

    public MIDletThread(String name) {
        super(THREAD_NAME_PREFIX + name);
        MIDletThread.register(this);
    }

    private static void register(MIDletThread thread) {
        MIDletContext midletContext = MIDletBridge.getMIDletContext();
        if (midletContext == null) {
            Logger.error("Creating thread with no MIDlet context", new Throwable());
            return;
        }
        thread.callLocation = ThreadUtils.getCallLocation(MIDletThread.class.getName());
        WeakHashMap<MIDletThread, MIDletContext> threads = (WeakHashMap<MIDletThread, MIDletContext>)midlets.get(midletContext);
        if (threads == null) {
            threads = new WeakHashMap<MIDletThread, MIDletContext>();
            midlets.put(midletContext, threads);
        }
        threads.put(thread, midletContext);
    }

    public void run() {
        try {
            super.run();
        }
        catch (Throwable e) {
            Logger.debug("MIDletThread throws", e);
        }
    }

    public static void contextDestroyed(MIDletContext midletContext) {
        if (midletContext == null) {
            return;
        }
        final Map threads = (Map)midlets.remove(midletContext);
        if (threads != null && threads.size() != 0) {
            terminator = true;
            Thread terminator = new Thread("MIDletThreadsTerminator"){

                public void run() {
                    MIDletThread.terminateThreads(threads);
                }
            };
            terminator.start();
        }
        MIDletTimer.contextDestroyed(midletContext);
    }

    public static boolean hasRunningThreads(MIDletContext midletContext) {
        return terminator;
    }

    private static void terminateThreads(Map threads) {
        long endTime = System.currentTimeMillis() + (long)graceTerminationPeriod;
        Iterator iter = threads.keySet().iterator();
        while (iter.hasNext()) {
            Object o = iter.next();
            if (o == null) continue;
            if (o instanceof MIDletThread) {
                MIDletThread t = (MIDletThread)o;
                if (!t.isAlive()) continue;
                Logger.info("wait thread [" + t.getName() + "] end");
                while (endTime > System.currentTimeMillis() && t.isAlive()) {
                    try {
                        t.join(700L);
                    }
                    catch (InterruptedException e) {
                        // empty catch block
                        break;
                    }
                }
                if (!t.isAlive()) continue;
                Logger.warn("MIDlet thread [" + t.getName() + "] still running" + ThreadUtils.getTreadStackTrace(t));
                if (t.callLocation != null) {
                    Logger.info("this thread [" + t.getName() + "] was created from " + t.callLocation);
                }
                t.interrupt();
                continue;
            }
            Logger.debug("unrecognized Object [" + o.getClass().getName() + "]");
        }
        Logger.debug("all " + threads.size() + " thread(s) finished");
        terminator = false;
    }
}

