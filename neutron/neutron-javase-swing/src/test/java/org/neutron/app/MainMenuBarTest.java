package org.neutron.app;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.neutron.app.ui.swing.EmulatorTheme;
import org.neutron.app.ui.swing.SwingDevicePlatformPanel;
import org.neutron.app.ui.swing.SwingAboutDialog;
import org.neutron.app.ui.swing.SwingSystemInfoDialog;
import org.neutron.app.ui.swing.SwingDocsDialog;

import static org.junit.Assert.*;

public class MainMenuBarTest {

    private Main main;
    private JMenuBar menuBar;

    @Before
    public void setUp() {
        main = new Main();
        menuBar = main.getJMenuBar();
    }

    @After
    public void tearDown() {
        if (main != null) {
            main.dispose();
        }
    }

    private JMenu findMenu(String name) {
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu m = menuBar.getMenu(i);
            if (m != null && name.equalsIgnoreCase(m.getText())) {
                return m;
            }
        }
        return null;
    }

    private JMenuItem findMenuItem(JMenu parent, String name) {
        if (parent == null) return null;
        for (int i = 0; i < parent.getItemCount(); i++) {
            JMenuItem item = parent.getItem(i);
            if (item != null && name.equalsIgnoreCase(item.getText())) {
                return item;
            }
        }
        return null;
    }

    private JMenu findSubMenu(JMenu parent, String name) {
        if (parent == null) return null;
        for (int i = 0; i < parent.getItemCount(); i++) {
            JMenuItem item = parent.getItem(i);
            if (item instanceof JMenu && name.equalsIgnoreCase(item.getText())) {
                return (JMenu) item;
            }
        }
        return null;
    }

    private void drainEdt() throws Exception {
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            return;
        }
        javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {}
        });
    }

    private void clickMenuItem(final JMenuItem item) throws Exception {
        assertNotNull("MenuItem to click must not be null", item);
        Runnable r = new Runnable() {
            public void run() {
                item.doClick();
            }
        };
        if (javax.swing.SwingUtilities.isEventDispatchThread()) {
            r.run();
        } else {
            javax.swing.SwingUtilities.invokeAndWait(r);
        }
        drainEdt();
    }

    @Test
    public void testMenuBarHierarchyAndStructure() {
        assertNotNull("Main window must have a JMenuBar", menuBar);
        assertEquals("Menu bar must contain 4 top-level menus", 4, menuBar.getMenuCount());

        assertNotNull("Run menu must be present", findMenu("Run"));
        assertNotNull("Config menu must be present", findMenu("Config"));
        assertNotNull("Controls menu must be present", findMenu("Controls"));
        assertNotNull("Misc menu must be present", findMenu("Misc"));
    }

    @Test
    public void testRunMenuFeatures() {
        JMenu runMenu = findMenu("Run");
        assertNotNull(runMenu);

        // Run JAR File
        JMenuItem runJar = findMenuItem(runMenu, "Run JAR File");
        assertNotNull("Run JAR File item must exist", runJar);
        assertTrue("Run JAR File should be enabled", runJar.isEnabled());

        // Run from URL
        JMenuItem runUrl = findMenuItem(runMenu, "Run from URL");
        assertNotNull("Run from URL item must exist", runUrl);
        assertTrue("Run from URL should be enabled", runUrl.isEnabled());

        // Connected Directories
        JMenu connectedDirs = findSubMenu(runMenu, "Connected Directories");
        assertNotNull("Connected Directories submenu must exist", connectedDirs);

        // Open Root Directory
        JMenuItem openRoot = findMenuItem(runMenu, "Open Root Directory");
        assertNotNull("Open Root Directory item must exist", openRoot);

        // Snapshot Manager
        JMenu snapshotMgr = findSubMenu(runMenu, "Snapshot Manager");
        assertNotNull("Snapshot Manager submenu must exist", snapshotMgr);
        assertNotNull("Backup snapshot item must exist", findMenuItem(snapshotMgr, "Backup"));
        assertNotNull("Restore snapshot item must exist", findMenuItem(snapshotMgr, "Restore"));

        // Screenshot
        JMenuItem screenshot = findMenuItem(runMenu, "Take Screenshot");
        assertNotNull("Take Screenshot item must exist", screenshot);
        assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK), screenshot.getAccelerator());

        // Recording Capture Start/Stop
        JMenuItem startCap = findMenuItem(runMenu, "Start Recording");
        JMenuItem stopCap = findMenuItem(runMenu, "Stop Recording");
        assertNotNull("Start Recording item must exist", startCap);
        assertNotNull("Stop Recording item must exist", stopCap);
        assertTrue("Start Recording should be initially enabled", startCap.isEnabled());
        assertFalse("Stop Recording should be initially disabled", stopCap.isEnabled());

        // Exit
        JMenuItem exit = findMenuItem(runMenu, "Exit");
        assertNotNull("Exit item must exist", exit);
        assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK), exit.getAccelerator());
    }

    @Test
    public void testConfigMenuScaledDisplay() throws Exception {
        JMenu configMenu = findMenu("Config");
        assertNotNull(configMenu);

        JMenu scaledDisplay = findSubMenu(configMenu, "Scaled Display");
        assertNotNull("Scaled Display submenu must exist", scaledDisplay);
        assertEquals("Scaled Display should offer 3 zoom levels (x 2, x 3, x 4)", 3, scaledDisplay.getItemCount());

        JMenuItem z2 = findMenuItem(scaledDisplay, "x 2");
        JMenuItem z3 = findMenuItem(scaledDisplay, "x 3");
        JMenuItem z4 = findMenuItem(scaledDisplay, "x 4");
        assertNotNull(z2);
        assertNotNull(z3);
        assertNotNull(z4);

        int origZoom = Config.getScaledDisplayZoom();
        try {
            clickMenuItem(z2);
            assertEquals("Config zoom should be 2", 2, Config.getScaledDisplayZoom());

            clickMenuItem(z2);
            assertEquals("Config zoom should be -1 after untoggling", -1, Config.getScaledDisplayZoom());
        } finally {
            Config.setScaledDisplayZoom(origZoom);
        }
    }

    @Test
    public void testConfigMenuSleepModeToggle() throws Exception {
        JMenu configMenu = findMenu("Config");
        JCheckBoxMenuItem sleepMode = (JCheckBoxMenuItem) findMenuItem(configMenu, "Sleep Mode");
        assertNotNull("Sleep Mode toggle must exist", sleepMode);
        assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK), sleepMode.getAccelerator());

        boolean orig = Config.isSleepModeEnabled();
        try {
            clickMenuItem(sleepMode);
            assertEquals("Config sleep mode should match toggled state", !orig, Config.isSleepModeEnabled());

            clickMenuItem(sleepMode);
            assertEquals("Config sleep mode should revert cleanly", orig, Config.isSleepModeEnabled());
        } finally {
            Config.setSleepModeEnabled(orig);
        }
    }

    @Test
    public void testConfigMenuGraphicsFilters() throws Exception {
        JMenu configMenu = findMenu("Config");
        JMenu filterMenu = findSubMenu(configMenu, "Graphics Filter");
        assertNotNull("Graphics Filter submenu must exist", filterMenu);

        String[] expectedFilters = {
            "Nearest Neighbor", "Bilinear", "Bicubic", "xBRZ 2x", "xBRZ 3x", "xBRZ 4x",
            "xBRZ 5x", "xBRZ 6x", "Scale2x", "CRT Scanlines", "LCD Grid"
        };

        for (String filterName : expectedFilters) {
            JMenuItem filterItem = findMenuItem(filterMenu, filterName);
            assertNotNull("Filter item '" + filterName + "' must exist", filterItem);
        }

        String orig = Config.getGraphicsFilter();
        try {
            JMenuItem xbrz4x = findMenuItem(filterMenu, "xBRZ 4x");
            clickMenuItem(xbrz4x);
            assertEquals("Config graphics filter should update to xBRZ 4x", "xBRZ 4x", Config.getGraphicsFilter());

            JMenuItem crt = findMenuItem(filterMenu, "CRT Scanlines");
            clickMenuItem(crt);
            assertEquals("Config graphics filter should update to CRT Scanlines", "CRT Scanlines", Config.getGraphicsFilter());
        } finally {
            Config.setGraphicsFilter(orig);
        }
    }

    @Test
    public void testConfigMenuMemoryLimit() throws Exception {
        JMenu configMenu = findMenu("Config");
        JMenu memoryMenu = findSubMenu(configMenu, "Memory Limit");
        assertNotNull("Memory Limit submenu must exist", memoryMenu);

        String[] expectedLabels = {"Unlimited", "32 MB", "64 MB", "128 MB", "256 MB", "512 MB"};
        int[] expectedValues = {0, 32, 64, 128, 256, 512};

        int origLimit = Config.getMemoryLimit();
        try {
            for (int i = 0; i < expectedLabels.length; i++) {
                JMenuItem limitItem = findMenuItem(memoryMenu, expectedLabels[i]);
                assertNotNull("Memory limit item '" + expectedLabels[i] + "' must exist", limitItem);
                clickMenuItem(limitItem);
                assertEquals("Config memory limit should match selected preset", expectedValues[i], Config.getMemoryLimit());
            }
        } finally {
            Config.setMemoryLimit(origLimit);
        }
    }

    @Test
    public void testConfigMenuVirtualButtons() throws Exception {
        JMenu configMenu = findMenu("Config");
        JCheckBoxMenuItem virtBtn = (JCheckBoxMenuItem) findMenuItem(configMenu, "Virtual Buttons");
        assertNotNull("Virtual Buttons item must exist", virtBtn);
        assertEquals(KeyStroke.getKeyStroke(KeyEvent.VK_B, ActionEvent.CTRL_MASK), virtBtn.getAccelerator());

        boolean orig = Config.isVirtualButtonsEnabled();
        try {
            clickMenuItem(virtBtn);
            assertEquals("Virtual buttons toggled", !orig, Config.isVirtualButtonsEnabled());

            clickMenuItem(virtBtn);
            assertEquals("Virtual buttons reverted", orig, Config.isVirtualButtonsEnabled());
        } finally {
            Config.setVirtualButtonsEnabled(orig);
            main.setVirtualButtonsVisible(orig);
        }
    }

    @Test
    public void testConfigMenuMouseCoordinates() throws Exception {
        JMenu configMenu = findMenu("Config");
        JCheckBoxMenuItem mouseCoords = (JCheckBoxMenuItem) findMenuItem(configMenu, "Mouse Coordinates");
        assertNotNull("Mouse Coordinates item must exist", mouseCoords);

        boolean orig = Config.isShowMouseCoordinates();
        try {
            clickMenuItem(mouseCoords);
            assertEquals("Mouse coordinates toggled", !orig, Config.isShowMouseCoordinates());

            clickMenuItem(mouseCoords);
            assertEquals("Mouse coordinates reverted", orig, Config.isShowMouseCoordinates());
        } finally {
            Config.setShowMouseCoordinates(orig);
        }
    }

    @Test
    public void testControlsMenuFullscreenToggle() throws Exception {
        JMenu controlsMenu = findMenu("Controls");
        assertNotNull(controlsMenu);
        JCheckBoxMenuItem fs = (JCheckBoxMenuItem) findMenuItem(controlsMenu, "Fullscreen");
        assertNotNull("Fullscreen toggle item must exist", fs);

        try {
            clickMenuItem(fs);
            assertTrue("Config fullscreen should be true", Config.isFullscreen());
            assertFalse("MenuBar should be hidden in fullscreen", menuBar.isVisible());

            clickMenuItem(fs);
            assertFalse("Config fullscreen should be false", Config.isFullscreen());
            assertTrue("MenuBar should be restored", menuBar.isVisible());
        } finally {
            main.setFullscreenMode(false);
        }
    }

    @Test
    public void testControlsMenuAirplaneMode() throws Exception {
        JMenu controlsMenu = findMenu("Controls");
        JCheckBoxMenuItem airplane = (JCheckBoxMenuItem) findMenuItem(controlsMenu, "Airplane Mode");
        assertNotNull("Airplane Mode item must exist", airplane);

        boolean orig = Config.isAirplaneModeEnabled();
        try {
            clickMenuItem(airplane);
            assertEquals("Airplane mode should be toggled in Config", !orig, Config.isAirplaneModeEnabled());
            assertEquals("Network allowed state should match inverse of Airplane Mode",
                    orig, org.neutron.cldc.http.Connection.isAllowNetworkConnection());

            clickMenuItem(airplane);
            assertEquals("Airplane mode should revert cleanly", orig, Config.isAirplaneModeEnabled());
            assertEquals("Network allowed state should revert cleanly",
                    !orig, org.neutron.cldc.http.Connection.isAllowNetworkConnection());
        } finally {
            Config.setAirplaneModeEnabled(orig);
            org.neutron.cldc.http.Connection.setAllowNetworkConnection(!orig);
        }
    }

    @Test
    public void testControlsMenuFrameRateLimits() throws Exception {
        JMenu controlsMenu = findMenu("Controls");
        JMenu fpsMenu = findSubMenu(controlsMenu, "Frame Rate");
        assertNotNull("Frame Rate submenu must exist", fpsMenu);

        String[] labels = { "Unlimited", "60 FPS", "30 FPS", "15 FPS" };
        int[] values = { -1, 60, 30, 15 };

        int origFps = Config.getMaxFps();
        try {
            for (int i = 0; i < labels.length; i++) {
                JMenuItem item = findMenuItem(fpsMenu, labels[i]);
                assertNotNull("FPS item '" + labels[i] + "' must exist", item);
                clickMenuItem(item);
                assertEquals("Config max FPS should update", values[i], Config.getMaxFps());
                assertEquals("EventDispatcher max FPS should update", values[i], org.neutron.device.ui.EventDispatcher.maxFps);
            }
        } finally {
            Config.setMaxFps(origFps);
            org.neutron.device.ui.EventDispatcher.maxFps = origFps;
        }
    }

    @Test
    public void testControlsMenuEmulationSpeed() throws Exception {
        JMenu controlsMenu = findMenu("Controls");
        JMenu speedMenu = findSubMenu(controlsMenu, "Emulation Speed");
        assertNotNull("Emulation Speed submenu must exist", speedMenu);

        String[] labels = { "0.50x", "1.00x", "2.00x", "5.00x", "10.00x" };
        double[] values = { 0.50, 1.00, 2.00, 5.00, 10.00 };

        double origSpeed = Config.getSpeedMultiplier();
        try {
            for (int i = 0; i < labels.length; i++) {
                JMenuItem item = findMenuItem(speedMenu, labels[i]);
                assertNotNull("Speed item '" + labels[i] + "' must exist", item);
                clickMenuItem(item);
                assertEquals("Config speed multiplier should update", values[i], Config.getSpeedMultiplier(), 0.001);
            }
        } finally {
            Config.setSpeedMultiplier(origSpeed);
        }
    }

    @Test
    public void testControlsMenuOverlays() throws Exception {
        JMenu controlsMenu = findMenu("Controls");

        JCheckBoxMenuItem hud = (JCheckBoxMenuItem) findMenuItem(controlsMenu, "HUD Overlay");
        assertNotNull("HUD Overlay item must exist", hud);
        boolean origHud = Config.isPerfHudEnabled();
        try {
            clickMenuItem(hud);
            assertEquals("HUD overlay toggled", !origHud, Config.isPerfHudEnabled());
            clickMenuItem(hud);
            assertEquals("HUD overlay reverted", origHud, Config.isPerfHudEnabled());
        } finally {
            Config.setPerfHudEnabled(origHud);
        }

        JCheckBoxMenuItem net = (JCheckBoxMenuItem) findMenuItem(controlsMenu, "Network Overlay");
        assertNotNull("Network Overlay item must exist", net);
        boolean origNet = Config.isNetworkOverlayEnabled();
        try {
            clickMenuItem(net);
            assertEquals("Network overlay toggled", !origNet, Config.isNetworkOverlayEnabled());
            clickMenuItem(net);
            assertEquals("Network overlay reverted", origNet, Config.isNetworkOverlayEnabled());
        } finally {
            Config.setNetworkOverlayEnabled(origNet);
        }
    }

    @Test
    public void testMiscMenuDialogPanels() {
        JMenu miscMenu = findMenu("Misc");
        assertNotNull(miscMenu);

        assertNotNull("Device Platform item must exist", findMenuItem(miscMenu, "Device Platform"));
        assertNotNull("About item must exist", findMenuItem(miscMenu, "About"));
        assertNotNull("System item must exist", findMenuItem(miscMenu, "System"));
        assertNotNull("Docs item must exist", findMenuItem(miscMenu, "Docs"));

        // Verify Dialog Panels construct and validate cleanly
        SwingDevicePlatformPanel platformPanel = new SwingDevicePlatformPanel();
        assertNotNull(platformPanel);
        assertTrue(platformPanel.check(false));

        SwingAboutDialog aboutPanel = new SwingAboutDialog();
        assertNotNull(aboutPanel);

        SwingSystemInfoDialog sysPanel = new SwingSystemInfoDialog();
        assertNotNull(sysPanel);

        SwingDocsDialog docsPanel = new SwingDocsDialog();
        assertNotNull(docsPanel);
        assertNotNull(docsPanel.btOk);
    }

    @Test
    public void testThemeMenuIfPresent() throws Exception {
        if (!Main.isFlatLafAvailable()) {
            return;
        }
        JMenu configMenu = findMenu("Config");
        JMenu themeMenu = findSubMenu(configMenu, "Theme");
        assertNotNull("Theme submenu should exist when FlatLaf is available", themeMenu);

        String origTheme = Config.getTheme();
        try {
            for (EmulatorTheme theme : EmulatorTheme.values()) {
                JMenuItem item = findMenuItem(themeMenu, theme.getDisplayName());
                assertNotNull("Theme item '" + theme.getDisplayName() + "' must exist", item);
                clickMenuItem(item);
                assertEquals("Config theme should update", theme.getDisplayName(), Config.getTheme());
            }
        } finally {
            Config.setTheme(origTheme);
            Main.applyTheme(origTheme);
        }
    }
}
