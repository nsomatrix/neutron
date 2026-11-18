package org.neutron.app.util;

import org.junit.Test;
import static org.junit.Assert.*;

public class PlaytimeTrackerTest {

    @Test
    public void testFormatPlaytimeZero() {
        assertEquals("Never", PlaytimeTracker.formatPlaytime(0));
        assertEquals("Never", PlaytimeTracker.formatPlaytime(-10));
    }

    @Test
    public void testFormatPlaytimeSecondsAndMinutes() {
        assertEquals("45s", PlaytimeTracker.formatPlaytime(45));
        assertEquals("2m 5s", PlaytimeTracker.formatPlaytime(125));
    }

    @Test
    public void testFormatPlaytimeHours() {
        assertEquals("1h", PlaytimeTracker.formatPlaytime(3600));
        assertEquals("1h 2m 5s", PlaytimeTracker.formatPlaytime(3725));
        assertEquals("23h 59m 59s", PlaytimeTracker.formatPlaytime(86399));
    }

    @Test
    public void testFormatPlaytimeDays() {
        // 1 day + 1 hour + 2 min + 5 sec = 86400 + 3600 + 120 + 5 = 90125
        assertEquals("1d 1h 2m", PlaytimeTracker.formatPlaytime(90125));
        // 5 days + 12 hours + 30 min + 15 sec
        long seconds = 5 * 86400 + 12 * 3600 + 30 * 60 + 15;
        assertEquals("5d 12h 30m", PlaytimeTracker.formatPlaytime(seconds));
    }

    @Test
    public void testNormalizeKey() {
        assertEquals("unknown", PlaytimeTracker.normalizeKey(null));
        assertEquals("unknown", PlaytimeTracker.normalizeKey(""));
        assertEquals("test.jar", PlaytimeTracker.normalizeKey("test.jar"));
        assertEquals("game.jar", PlaytimeTracker.normalizeKey("/games/arcade/game.jar"));
        assertEquals("suite.jar", PlaytimeTracker.normalizeKey("file:/tmp/suite.jar"));
    }

    @Test
    public void testSessionLifecycle() throws InterruptedException {
        String testGame = "test_unit_game.jar";
        PlaytimeTracker.onGameStarted(testGame);
        Thread.sleep(1100);
        long active = PlaytimeTracker.getPlaytimeSeconds(testGame);
        assertTrue("Active session should register elapsed time >= 1s", active >= 1);
        PlaytimeTracker.onGameStopped();
        long recorded = PlaytimeTracker.getPlaytimeSeconds(testGame);
        assertTrue("Stopped session should record playtime", recorded >= 1);
    }
}

