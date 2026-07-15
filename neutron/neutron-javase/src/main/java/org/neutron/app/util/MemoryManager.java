/**
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */
package org.neutron.app.util;

import java.util.Timer;
import java.util.TimerTask;
import org.neutron.app.Config;
import org.neutron.log.Logger;

/**
 * MemoryManager monitors emulator memory usage and triggers garbage collection
 * when the configured limit is exceeded to optimize resource usage in continuous runs.
 */
public class MemoryManager {
    private static Timer memoryTimer;

    public static synchronized void init() {
        if (memoryTimer == null) {
            memoryTimer = new Timer("NeutronMemoryManagerTimer", true);
            memoryTimer.scheduleAtFixedRate(new TimerTask() {
                private int gcCount = 0;

                public void run() {
                    try {
                        int limitMb = Config.getMemoryLimit();
                        if (limitMb <= 0) {
                            return;
                        }

                        long limitBytes = (long) limitMb * 1024 * 1024;
                        Runtime runtime = Runtime.getRuntime();
                        long usedMemory = runtime.totalMemory() - runtime.freeMemory();

                        if (usedMemory > limitBytes) {
                            gcCount++;
                            // Log memory warning every 60 seconds (6 ticks of 10s) to avoid spamming
                            if (gcCount % 6 == 0 || gcCount == 1) {
                                Logger.info("Memory usage (" + (usedMemory / 1024 / 1024)
                                    + " MB) exceeds J2ME limit (" + limitMb
                                    + " MB). Triggering Garbage Collection...");
                            }
                            System.gc();
                        }
                    } catch (Throwable t) {
                        Logger.error("Error in MemoryManager loop", t);
                    }
                }
            }, 10000, 10000); // Check every 10 seconds
        }
    }
}
