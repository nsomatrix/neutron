/**
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */
package org.neutron.app.util;

import org.neutron.log.Logger;

/**
 * SleepManager controls the sleep and hibernate state of the emulator.
 * When sleep mode is active, J2ME thread execution is suspended to optimize CPU and memory resources.
 * It supports auto-sleep after 2 minutes of inactivity.
 */
public class SleepManager {
    private static volatile boolean sleepEnabled = false;
    private static volatile boolean sleepModeActive = false;
    private static final Object sleepLock = new Object();
    private static Runnable uiCallback;

    private static volatile long lastActivityTime = System.currentTimeMillis();
    private static final long INACTIVITY_DELAY = 120000; // 2 minutes in milliseconds
    private static java.util.Timer inactivityTimer;

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
        lastActivityTime = System.currentTimeMillis();
        if (sleepModeActive) {
            setSleepModeActive(false);
        }
    }

    public static void setSleepModeActive(boolean active) {
        synchronized (sleepLock) {
            if (sleepModeActive != active) {
                sleepModeActive = active;
                if (active) {
                    Logger.info("Entering Sleep/Hibernate Mode due to inactivity. Suspending J2ME threads...");
                    System.gc();
                } else {
                    Logger.info("Waking up from Sleep/Hibernate Mode. Resuming J2ME threads...");
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
        if (inactivityTimer == null) {
            inactivityTimer = new java.util.Timer("NeutronSleepInactivityTimer", true);
            inactivityTimer.scheduleAtFixedRate(new java.util.TimerTask() {
                public void run() {
                    checkInactivity();
                }
            }, 1000, 1000);
        }
    }

    private static synchronized void stopInactivityTimer() {
        if (inactivityTimer != null) {
            inactivityTimer.cancel();
            inactivityTimer = null;
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
        if (!sleepModeActive) {
            return;
        }
        synchronized (sleepLock) {
            while (sleepModeActive) {
                try {
                    sleepLock.wait(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }
}
