/**
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */
package org.neutron.app.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.neutron.log.Logger;

/**
 * SleepManager controls the sleep and hibernate state of the emulator.
 * When sleep mode is active, J2ME rendering is suspended while background logic and connections remain active.
 * It supports auto-sleep after 2 minutes of inactivity using a ScheduledExecutorService.
 */
public class SleepManager {
    private static volatile boolean sleepEnabled = false;
    private static volatile boolean sleepModeActive = false;
    private static final Object sleepLock = new Object();
    private static Runnable uiCallback;

    private static volatile long lastActivityTime = System.currentTimeMillis();
    private static final long INACTIVITY_DELAY = 120000; // 2 minutes in milliseconds
    private static ScheduledExecutorService inactivityScheduler;

    public static boolean isSleepModeActive() {
        return sleepModeActive;
    }

    public static boolean isSleepEnabled() {
        return sleepEnabled;
    }

    public static void setUiCallback(Runnable callback) {
        uiCallback = callback;
    }

    public static synchronized void setSleepEnabled(boolean enabled) {
        sleepEnabled = enabled;
        if (enabled) {
            lastActivityTime = System.currentTimeMillis();
            startInactivityTimer();
        } else {
            stopInactivityTimer();
            setSleepModeActive(false);
        }
    }

    public static void notifyActivity() {
        if (!sleepModeActive) {
            lastActivityTime = System.currentTimeMillis();
        }
    }

    public static void setSleepModeActive(boolean active) {
        synchronized (sleepLock) {
            if (sleepModeActive != active) {
                sleepModeActive = active;
                if (active) {
                    Logger.info("Entering Sleep/Hibernate Mode due to inactivity.");
                    System.gc();
                } else {
                    Logger.info("Waking up from Sleep/Hibernate Mode.");
                    lastActivityTime = System.currentTimeMillis();
                    sleepLock.notifyAll();
                    System.gc();
                }
                if (uiCallback != null) {
                    try {
                        uiCallback.run();
                    } catch (Throwable t) {
                        Logger.error("Failed to run UI callback", t);
                    }
                }
            }
        }
    }

    private static synchronized void startInactivityTimer() {
        if (inactivityScheduler == null || inactivityScheduler.isShutdown()) {
            inactivityScheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "NeutronSleepInactivityTimer");
                t.setDaemon(true);
                return t;
            });
            inactivityScheduler.scheduleAtFixedRate(SleepManager::checkInactivity, 1, 1, TimeUnit.SECONDS);
        }
    }

    private static synchronized void stopInactivityTimer() {
        if (inactivityScheduler != null) {
            inactivityScheduler.shutdownNow();
            inactivityScheduler = null;
        }
    }

    private static void checkInactivity() {
        if (sleepEnabled && !sleepModeActive) {
            if (System.currentTimeMillis() - lastActivityTime >= INACTIVITY_DELAY) {
                setSleepModeActive(true);
            }
        }
    }

    public static void checkSleep() {
        if (sleepModeActive) {
            try {
                // Throttle background J2ME execution loops (~12 FPS) during sleep mode to drastically lower CPU usage
                // while preserving active TCP/HTTP socket connections and MIDlet state.
                Thread.sleep(80);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void onWindowIconified() {
        if (sleepEnabled && !sleepModeActive) {
            setSleepModeActive(true);
        }
    }
}


