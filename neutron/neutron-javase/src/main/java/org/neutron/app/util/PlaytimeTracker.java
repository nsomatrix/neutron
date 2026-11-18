/**
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */
package org.neutron.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import nanoxml.XMLElement;
import org.neutron.app.Config;
import org.neutron.log.Logger;

/**
 * PlaytimeTracker records and persists total gameplay time across midlets.
 * Follows the existing Neutron XML storage conventions in Config.getConfigPath().
 */
public class PlaytimeTracker {

    private static final Map<String, Long> playtimeMap = new HashMap<>();
    private static volatile String activeGameKey = null;
    private static volatile long sessionStartTime = 0;
    private static boolean loaded = false;

    static {
        try {
            Runtime.getRuntime().addShutdownHook(new Thread("Neutron-PlaytimeShutdownHook") {
                @Override
                public void run() {
                    onGameStopped();
                }
            });
        } catch (Throwable ignore) {
        }
    }

    private static File getStatsFile() {
        File configPath = Config.getConfigPath();
        if (configPath == null) {
            configPath = new File(System.getProperty("user.home", "."), ".neutron");
        }
        return new File(configPath, "playtime.xml");
    }

    public static synchronized void load() {
        if (loaded) {
            return;
        }
        loaded = true;
        File f = getStatsFile();
        if (!f.exists()) {
            return;
        }

        try (FileInputStream fis = new FileInputStream(f)) {
            XMLElement xml = new XMLElement();
            StringBuilder sb = new StringBuilder();
            byte[] buf = new byte[4096];
            int n;
            while ((n = fis.read(buf)) != -1) {
                sb.append(new String(buf, 0, n, "UTF-8"));
            }
            xml.parseString(sb.toString());
            for (Enumeration e = xml.enumerateChildren(); e.hasMoreElements();) {
                XMLElement gameXml = (XMLElement) e.nextElement();
                String key = gameXml.getStringAttribute("key");
                String secondsStr = gameXml.getStringAttribute("seconds");
                if (key != null && secondsStr != null) {
                    try {
                        playtimeMap.put(key, Long.parseLong(secondsStr));
                    } catch (NumberFormatException ignore) {
                    }
                }
            }
        } catch (Throwable e) {
            Logger.error("Failed to load playtime stats", e);
        }
    }

    public static synchronized void save() {
        try {
            File f = getStatsFile();
            File parent = f.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            XMLElement root = new XMLElement();
            root.setName("playtimes");
            for (Map.Entry<String, Long> entry : playtimeMap.entrySet()) {
                XMLElement item = new XMLElement();
                item.setName("game");
                item.setAttribute("key", entry.getKey());
                item.setAttribute("seconds", String.valueOf(entry.getValue()));
                root.addChild(item);
            }
            try (Writer w = new OutputStreamWriter(new FileOutputStream(f), "UTF-8")) {
                root.write(w);
            }
        } catch (Throwable e) {
            Logger.error("Failed to save playtime stats", e);
        }
    }

    public static synchronized void onGameStarted(String gameKey) {
        load();
        if (activeGameKey != null) {
            onGameStopped();
        }
        activeGameKey = normalizeKey(gameKey);
        sessionStartTime = System.currentTimeMillis();
    }

    public static synchronized void onGameStopped() {
        if (activeGameKey == null || sessionStartTime == 0) {
            return;
        }
        long elapsedSec = (System.currentTimeMillis() - sessionStartTime) / 1000;
        if (elapsedSec > 0) {
            Long currentVal = playtimeMap.get(activeGameKey);
            long current = currentVal != null ? currentVal : 0L;
            playtimeMap.put(activeGameKey, current + elapsedSec);
            save();
        }
        activeGameKey = null;
        sessionStartTime = 0;
    }

    public static synchronized long getPlaytimeSeconds(String gameKey) {
        load();
        String normKey = normalizeKey(gameKey);
        Long recordedVal = playtimeMap.get(normKey);
        long recorded = recordedVal != null ? recordedVal : 0L;
        if (normKey != null && normKey.equals(activeGameKey) && sessionStartTime > 0) {
            recorded += (System.currentTimeMillis() - sessionStartTime) / 1000;
        }
        return recorded;
    }

    public static String normalizeKey(String key) {
        if (key == null || key.trim().isEmpty()) {
            return "unknown";
        }
        try {
            String clean = key.trim();
            if (clean.startsWith("file:/")) {
                clean = new java.net.URL(clean).getPath();
            }
            return new File(clean).getName();
        } catch (Throwable e) {
            int lastSlash = key.lastIndexOf('/');
            if (lastSlash != -1 && lastSlash < key.length() - 1) {
                return key.substring(lastSlash + 1);
            }
            return key;
        }
    }

    /**
     * Format seconds into industry standard clean unit shorthand (Steam & RetroArch style).
     * E.g. "Never", "45s", "12m 30s", "1h 45m 20s", "2d 5h 30m", or "1y 45d 12h".
     */
    public static String formatPlaytime(long totalSeconds) {
        if (totalSeconds <= 0) {
            return "Never";
        }
        long sec = totalSeconds % 60;
        long totalMin = totalSeconds / 60;
        long min = totalMin % 60;
        long totalHours = totalMin / 60;
        long hr = totalHours % 24;
        long totalDays = totalHours / 24;
        long day = totalDays % 365;
        long yr = totalDays / 365;

        StringBuilder sb = new StringBuilder();
        if (yr > 0) {
            sb.append(yr).append("y ");
        }
        if (day > 0) {
            sb.append(day).append("d ");
        }
        if (hr > 0) {
            sb.append(hr).append("h ");
        }
        if (min > 0 || (hr > 0 && sec > 0 && yr == 0 && day == 0)) {
            sb.append(min).append("m ");
        }
        if (yr == 0 && day == 0) {
            if (sec > 0 || sb.length() == 0) {
                sb.append(sec).append("s");
            }
        }
        return sb.toString().trim();
    }
}
