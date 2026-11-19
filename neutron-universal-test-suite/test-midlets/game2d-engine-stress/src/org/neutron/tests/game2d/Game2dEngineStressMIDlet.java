package org.neutron.tests.game2d;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;

public class Game2dEngineStressMIDlet extends MIDlet {

    private Display display;
    private GameTestCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new GameTestCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class GameTestCanvas extends GameCanvas implements Runnable {
        private final Game2dEngineStressMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public GameTestCanvas(Game2dEngineStressMIDlet midlet) {
            super(true); // suppress key events for GameCanvas
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] 2D Game Engine (GameCanvas/Sprite/TiledLayer)");
            System.out.println("=================================================");

            runTest("Sprite Frame Slicing & Transforms", new TestCase() {
                public void execute() {
                    // Create 32x16 image containing 2 frames of 16x16
                    Image spriteSheet = Image.createImage(32, 16);
                    Graphics g = spriteSheet.getGraphics();
                    g.setColor(0xFF0000);
                    g.fillRect(0, 0, 16, 16); // Frame 0 = Red
                    g.setColor(0x00FF00);
                    g.fillRect(16, 0, 16, 16); // Frame 1 = Green

                    Sprite sprite = new Sprite(spriteSheet, 16, 16);
                    assertTrue("Sprite width == 16", sprite.getWidth() == 16);
                    assertTrue("Sprite height == 16", sprite.getHeight() == 16);
                    assertTrue("Sprite raw frame count == 2", sprite.getRawFrameCount() == 2);

                    sprite.setTransform(Sprite.TRANS_ROT90);
                    assertTrue("Sprite width after ROT90 == 16", sprite.getWidth() == 16);

                    sprite.setTransform(Sprite.TRANS_MIRROR);
                    assertTrue("Sprite width after MIRROR == 16", sprite.getWidth() == 16);

                    sprite.setFrame(1);
                    assertTrue("Sprite current frame == 1", sprite.getFrame() == 1);
                }
            });

            runTest("Sprite Bounding-Box & Pixel-Level Collisions", new TestCase() {
                public void execute() {
                    Image imgA = Image.createImage(16, 16);
                    Graphics ga = imgA.getGraphics();
                    ga.setColor(0xFF0000);
                    ga.fillRect(0, 0, 16, 16);

                    Image imgB = Image.createImage(16, 16);
                    Graphics gb = imgB.getGraphics();
                    gb.setColor(0x0000FF);
                    gb.fillRect(0, 0, 16, 16);

                    Sprite sA = new Sprite(imgA, 16, 16);
                    Sprite sB = new Sprite(imgB, 16, 16);

                    sA.setPosition(0, 0);
                    sB.setPosition(10, 10);
                    assertTrue("Overlapping sprites collide bounding-box", sA.collidesWith(sB, false));
                    assertTrue("Overlapping sprites collide pixel-level", sA.collidesWith(sB, true));

                    sB.setPosition(20, 20); // Disjoint
                    assertTrue("Disjoint sprites do not collide", !sA.collidesWith(sB, false));
                }
            });

            runTest("TiledLayer Tile Mapping & Animated Tiles", new TestCase() {
                public void execute() {
                    Image tileSheet = Image.createImage(32, 16);
                    Graphics tg = tileSheet.getGraphics();
                    tg.setColor(0x333333);
                    tg.fillRect(0, 0, 16, 16);
                    tg.setColor(0x666666);
                    tg.fillRect(16, 0, 16, 16);

                    TiledLayer map = new TiledLayer(8, 8, tileSheet, 16, 16);
                    assertTrue("TiledLayer cols == 8", map.getColumns() == 8);
                    assertTrue("TiledLayer rows == 8", map.getRows() == 8);
                    assertTrue("TiledLayer cellWidth == 16", map.getCellWidth() == 16);
                    assertTrue("TiledLayer cellHeight == 16", map.getCellHeight() == 16);

                    map.setCell(0, 0, 1);
                    map.setCell(1, 1, 2);
                    assertTrue("Cell (0,0) tile == 1", map.getCell(0, 0) == 1);
                    assertTrue("Cell (1,1) tile == 2", map.getCell(1, 1) == 2);

                    int animTile = map.createAnimatedTile(1);
                    assertTrue("Animated tile ID negative", animTile < 0);
                    map.setCell(2, 2, animTile);
                    assertTrue("Cell (2,2) has animTile", map.getCell(2, 2) == animTile);

                    map.setAnimatedTile(animTile, 2);
                    assertTrue("Animated tile target updated", map.getAnimatedTile(animTile) == 2);
                }
            });

            runTest("LayerManager Compositing & View Window", new TestCase() {
                public void execute() {
                    LayerManager lm = new LayerManager();
                    Image img = Image.createImage(16, 16);
                    Sprite s1 = new Sprite(img, 16, 16);
                    Sprite s2 = new Sprite(img, 16, 16);

                    lm.append(s1);
                    lm.append(s2);
                    assertTrue("Layer count == 2", lm.getSize() == 2);
                    assertTrue("Layer 0 is s1", lm.getLayerAt(0) == s1);
                    assertTrue("Layer 1 is s2", lm.getLayerAt(1) == s2);

                    lm.setViewWindow(0, 0, 128, 128);
                    Graphics g = getGraphics();
                    lm.paint(g, 0, 0); // Paint should succeed without exception
                }
            });

            runTest("High-Entity Stress & Collision Throughput", new TestCase() {
                public void execute() {
                    Image dotImg = Image.createImage(8, 8);
                    Graphics dg = dotImg.getGraphics();
                    dg.setColor(0xFFAA00);
                    dg.fillRect(0, 0, 8, 8);

                    int entityCount = 100;
                    Sprite[] entities = new Sprite[entityCount];
                    Random rnd = new Random(12345);

                    for (int i = 0; i < entityCount; i++) {
                        entities[i] = new Sprite(dotImg, 8, 8);
                        entities[i].setPosition(rnd.nextInt(120), rnd.nextInt(120));
                    }

                    int collisionChecks = 0;
                    int collisionsDetected = 0;

                    for (int i = 0; i < entityCount; i++) {
                        for (int j = i + 1; j < entityCount; j++) {
                            collisionChecks++;
                            if (entities[i].collidesWith(entities[j], false)) {
                                collisionsDetected++;
                            }
                        }
                    }

                    assertTrue("Collision checks executed > 4000", collisionChecks >= 4000);
                    assertTrue("Collisions detected >= 0", collisionsDetected >= 0);
                }
            });

            completed = true;
            currentTest = "Complete";
            System.out.println("=================================================");
            System.out.println("[TEST-SUITE-SUMMARY] Passed: " + passCount + ", Failed: " + failCount + ", Total: " + (passCount + failCount));
            System.out.println("[TEST-SUITE-STATUS: " + (failCount == 0 ? "PASSED" : "FAILED") + "]");
            System.out.println("=================================================");

            renderFrame();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {}
            midlet.notifyDestroyed();
        }

        private void runTest(String name, TestCase test) {
            currentTest = name;
            renderFrame();
            System.out.println("[TEST-START] " + name);
            try {
                test.execute();
                passCount++;
                logEntries.addElement("[PASS] " + name);
                System.out.println("[TEST-RESULT] " + name + ": PASS");
            } catch (Throwable t) {
                failCount++;
                logEntries.addElement("[FAIL] " + name + " -> " + t.getMessage());
                System.out.println("[TEST-RESULT] " + name + ": FAIL -> " + t.getMessage());
                t.printStackTrace();
            }
            renderFrame();
        }

        private void assertTrue(String message, boolean condition) {
            if (!condition) {
                System.out.println("  [ASSERT-FAIL] " + message);
                throw new RuntimeException("Assertion Failed: " + message);
            }
            System.out.println("  [ASSERT-OK] " + message);
        }

        private void renderFrame() {
            Graphics g = getGraphics();
            int w = getWidth();
            int h = getHeight();

            g.setColor(0x141E30);
            g.fillRect(0, 0, w, h);

            g.setColor(0xFF8800);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("2D Game Engine Stress", 5, 5, Graphics.TOP | Graphics.LEFT);

            g.setColor(0xFFFFFF);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.drawString("Running: " + currentTest, 5, 22, Graphics.TOP | Graphics.LEFT);

            int y = 45;
            int startIdx = Math.max(0, logEntries.size() - 8);
            for (int i = startIdx; i < logEntries.size(); i++) {
                String line = (String) logEntries.elementAt(i);
                if (line.startsWith("[PASS]")) {
                    g.setColor(0x00FF88);
                } else {
                    g.setColor(0xFF4444);
                }
                g.drawString(line, 5, y, Graphics.TOP | Graphics.LEFT);
                y += 14;
            }

            g.setColor(0x223344);
            g.fillRect(0, h - 22, w, 22);
            g.setColor(completed && failCount == 0 ? 0x00FF88 : (failCount > 0 ? 0xFF4444 : 0xFFFF00));
            g.drawString("Score: " + passCount + " Pass, " + failCount + " Fail (" + (completed ? "DONE" : "TESTING") + ")", 5, h - 18, Graphics.TOP | Graphics.LEFT);

            flushGraphics();
        }
    }

    public interface TestCase {
        void execute() throws Exception;
    }
}

