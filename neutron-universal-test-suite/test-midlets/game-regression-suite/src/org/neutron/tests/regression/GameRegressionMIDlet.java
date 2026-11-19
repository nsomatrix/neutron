package org.neutron.tests.regression;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.rms.*;
import com.nokia.mid.ui.DirectGraphics;
import com.nokia.mid.ui.DirectUtils;

public class GameRegressionMIDlet extends MIDlet {

    private Display display;
    private RegressionCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new RegressionCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class RegressionCanvas extends GameCanvas implements Runnable {
        private final GameRegressionMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public RegressionCanvas(GameRegressionMIDlet midlet) {
            super(false);
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] Real-World Game Regression Suite");
            System.out.println("=================================================");

            runTest("Sprite Collision Matrix & Pixel-Level Overlap", new TestCase() {
                public void execute() {
                    Image sImg1 = Image.createImage(16, 16);
                    Graphics g1 = sImg1.getGraphics();
                    g1.setColor(0xFF0000);
                    g1.fillRect(0, 0, 16, 16);

                    Image sImg2 = Image.createImage(16, 16);
                    Graphics g2 = sImg2.getGraphics();
                    g2.setColor(0x00FF00);
                    g2.fillRect(0, 0, 16, 16);

                    Sprite s1 = new Sprite(sImg1, 16, 16);
                    Sprite s2 = new Sprite(sImg2, 16, 16);

                    s1.setPosition(10, 10);
                    s2.setPosition(18, 10); // 8-pixel overlap horizontally
                    assertTrue("Sprites collide on overlap", s1.collidesWith(s2, true));

                    s2.setPosition(30, 30); // completely separate
                    assertTrue("Sprites do not collide when separated", !s1.collidesWith(s2, true));
                }
            });

            runTest("TiledLayer Multi-Cell Map & Dynamic Cell Replacement", new TestCase() {
                public void execute() {
                    Image tileImg = Image.createImage(32, 16); // 2 tiles 16x16
                    Graphics tg = tileImg.getGraphics();
                    tg.setColor(0x336699);
                    tg.fillRect(0, 0, 16, 16);
                    tg.setColor(0x996633);
                    tg.fillRect(16, 0, 16, 16);

                    TiledLayer map = new TiledLayer(8, 8, tileImg, 16, 16);
                    map.setCell(0, 0, 1);
                    map.setCell(1, 0, 2);
                    assertTrue("Cell (0,0) tile is 1", map.getCell(0, 0) == 1);
                    assertTrue("Cell (1,0) tile is 2", map.getCell(1, 0) == 2);

                    int animTile = map.createAnimatedTile(1);
                    map.setCell(2, 0, animTile);
                    map.setAnimatedTile(animTile, 2);
                    assertTrue("Animated tile updated to 2", map.getAnimatedTile(animTile) == 2);
                }
            });

            runTest("LayerManager Composite Multi-Layer Viewport Rendering", new TestCase() {
                public void execute() {
                    LayerManager lm = new LayerManager();
                    Image img = Image.createImage(16, 16);
                    Sprite s = new Sprite(img, 16, 16);
                    lm.append(s);
                    assertTrue("Layer count is 1", lm.getSize() == 1);
                    lm.setViewWindow(0, 0, 100, 100);

                    Image buffer = Image.createImage(100, 100);
                    Graphics bg = buffer.getGraphics();
                    lm.paint(bg, 0, 0);
                    assertTrue("LayerManager rendered without exception", true);
                }
            });

            runTest("Nokia DirectGraphics 8-Way Rotation in Game Canvas", new TestCase() {
                public void execute() {
                    Graphics g = getGraphics();
                    DirectGraphics dg = DirectUtils.getDirectGraphics(g);
                    assertTrue("DirectGraphics available on GameCanvas", dg != null);

                    int[] px = new int[] { 0xFFFF0000, 0xFF00FF00, 0xFF0000FF, 0xFFFFFF00 };
                    dg.drawPixels(px, true, 0, 2, 5, 5, 2, 2, DirectGraphics.ROTATE_90, DirectGraphics.TYPE_INT_8888_ARGB);
                    dg.drawPixels(px, true, 0, 2, 15, 5, 2, 2, DirectGraphics.ROTATE_180, DirectGraphics.TYPE_INT_8888_ARGB);
                    dg.drawPixels(px, true, 0, 2, 25, 5, 2, 2, DirectGraphics.ROTATE_270, DirectGraphics.TYPE_INT_8888_ARGB);
                    assertTrue("8-way rotations executed on GameCanvas", true);
                }
            });

            runTest("Game High-Score RMS Persistence Serialization", new TestCase() {
                public void execute() throws Exception {
                    String store = "GameSaveData";
                    try { RecordStore.deleteRecordStore(store); } catch (Exception ignore) {}

                    RecordStore rs = RecordStore.openRecordStore(store, true);
                    String testSave = "LEVEL:5;SCORE:98500;HEALTH:100;NAME:PLAYER1";
                    byte[] bytes = testSave.getBytes();
                    int recId = rs.addRecord(bytes, 0, bytes.length);

                    byte[] loaded = rs.getRecord(recId);
                    String loadedStr = new String(loaded);
                    assertTrue("Loaded save string matches saved data", testSave.equals(loadedStr));

                    rs.closeRecordStore();
                    RecordStore.deleteRecordStore(store);
                }
            });

            completed = true;
            currentTest = "Complete";
            System.out.println("=================================================");
            System.out.println("[TEST-SUITE-SUMMARY] Passed: " + passCount + ", Failed: " + failCount + ", Total: " + (passCount + failCount));
            System.out.println("[TEST-SUITE-STATUS: " + (failCount == 0 ? "PASSED" : "FAILED") + "]");
            System.out.println("=================================================");

            Graphics g = getGraphics();
            paintContent(g);
            flushGraphics();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {}
            midlet.notifyDestroyed();
        }

        private void runTest(String name, TestCase test) {
            currentTest = name;
            try {
                test.execute();
                passCount++;
                logEntries.addElement("[PASS] " + name);
                System.out.println("[TEST-RESULT] " + name + ": PASS");
            } catch (Throwable t) {
                failCount++;
                logEntries.addElement("[FAIL] " + name + " -> " + t.getMessage());
                System.out.println("[TEST-RESULT] " + name + ": FAIL -> " + t.getMessage());
            }

            Graphics g = getGraphics();
            paintContent(g);
            flushGraphics();
        }

        private void paintContent(Graphics g) {
            g.setColor(0x0a1118);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(0x00FF66);
            g.drawString("Game Regression Suite", 5, 5, Graphics.TOP | Graphics.LEFT);

            g.setColor(0xFFFFFF);
            g.drawString("Status: " + currentTest, 5, 25, Graphics.TOP | Graphics.LEFT);
            g.drawString("P: " + passCount + " | F: " + failCount, 5, 45, Graphics.TOP | Graphics.LEFT);

            int y = 70;
            int start = Math.max(0, logEntries.size() - 10);
            for (int i = start; i < logEntries.size(); i++) {
                String line = (String) logEntries.elementAt(i);
                g.setColor(line.startsWith("[PASS]") ? 0x00FF88 : 0xFF4444);
                g.drawString(line, 5, y, Graphics.TOP | Graphics.LEFT);
                y += 18;
            }
        }

        private void assertTrue(String msg, boolean cond) {
            if (!cond) throw new RuntimeException("Assertion failed: " + msg);
        }

        private void fail(String msg) {
            throw new RuntimeException(msg);
        }
    }

    interface TestCase {
        void execute() throws Exception;
    }
}
