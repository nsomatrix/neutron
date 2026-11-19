package org.neutron.tests.canvas;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class LcduiGraphicsCanvasMIDlet extends MIDlet {

    private Display display;
    private RenderCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new RenderCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class RenderCanvas extends Canvas implements Runnable {
        private final LcduiGraphicsCanvasMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public RenderCanvas(LcduiGraphicsCanvasMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] LCDUI Low-Level Graphics & Canvas");
            System.out.println("=================================================");

            runTest("Canvas Display Dimensions & Fullscreen", new TestCase() {
                public void execute() {
                    int w = getWidth();
                    int h = getHeight();
                    assertTrue("Canvas width > 0", w > 0);
                    assertTrue("Canvas height > 0", h > 0);
                    setFullScreenMode(true);
                    int fsw = getWidth();
                    int fsh = getHeight();
                    assertTrue("Fullscreen width >= initial width", fsw >= w);
                    assertTrue("Fullscreen height >= initial height", fsh >= h);
                }
            });

            runTest("Font Metrics & String Calculations", new TestCase() {
                public void execute() {
                    Font font = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
                    assertTrue("Font non-null", font != null);
                    assertTrue("Font height > 0", font.getHeight() > 0);
                    assertTrue("Font baseline > 0", font.getBaselinePosition() > 0);

                    String testText = "Neutron Canvas Test";
                    int strWidth = font.stringWidth(testText);
                    assertTrue("Font stringWidth > 0", strWidth > 0);

                    int subWidth = font.substringWidth(testText, 0, 7);
                    assertTrue("Font substringWidth < full width", subWidth < strWidth && subWidth > 0);
                }
            });

            runTest("Mutable Image & Off-screen Graphics", new TestCase() {
                public void execute() {
                    Image img = Image.createImage(64, 64);
                    assertTrue("Image isMutable", img.isMutable());
                    assertTrue("Image width == 64", img.getWidth() == 64);
                    assertTrue("Image height == 64", img.getHeight() == 64);

                    Graphics ig = img.getGraphics();
                    assertTrue("Off-screen graphics non-null", ig != null);

                    ig.setColor(0x00FF00); // Green
                    ig.fillRect(0, 0, 64, 64);

                    ig.setColor(0xFF0000); // Red line
                    ig.drawLine(0, 0, 63, 63);

                    ig.setColor(0x0000FF); // Blue rect
                    ig.drawRect(10, 10, 40, 40);

                    ig.setColor(0xFFFF00); // Yellow arc
                    ig.fillArc(15, 15, 30, 30, 0, 180);
                }
            });

            runTest("Direct RGB Image Creation & Pixel Extraction", new TestCase() {
                public void execute() {
                    int[] rgbData = new int[16 * 16];
                    for (int i = 0; i < rgbData.length; i++) {
                        rgbData[i] = (i % 2 == 0) ? 0xFFFF0000 : 0xFF0000FF; // Checkerboard
                    }

                    Image rgbImg = Image.createRGBImage(rgbData, 16, 16, true);
                    assertTrue("RGB Image non-null", rgbImg != null);
                    assertTrue("RGB Image immutable", !rgbImg.isMutable());
                    assertTrue("RGB Image width == 16", rgbImg.getWidth() == 16);

                    int[] extracted = new int[16 * 16];
                    rgbImg.getRGB(extracted, 0, 16, 0, 0, 16, 16);

                    assertTrue("Extracted pixel 0 match", (extracted[0] & 0x00FFFFFF) == 0x00FF0000);
                    assertTrue("Extracted pixel 1 match", (extracted[1] & 0x00FFFFFF) == 0x000000FF);
                }
            });

            runTest("Clipping & Coordinate Translation", new TestCase() {
                public void execute() {
                    Image testImg = Image.createImage(100, 100);
                    Graphics g = testImg.getGraphics();

                    g.setClip(10, 15, 50, 40);
                    assertTrue("ClipX == 10", g.getClipX() == 10);
                    assertTrue("ClipY == 15", g.getClipY() == 15);
                    assertTrue("ClipWidth == 50", g.getClipWidth() == 50);
                    assertTrue("ClipHeight == 40", g.getClipHeight() == 40);

                    g.clipRect(20, 25, 20, 20);
                    assertTrue("Sub-clipX == 20", g.getClipX() == 20);
                    assertTrue("Sub-clipY == 25", g.getClipY() == 25);
                    assertTrue("Sub-clipWidth == 20", g.getClipWidth() == 20);
                    assertTrue("Sub-clipHeight == 20", g.getClipHeight() == 20);

                    g.translate(15, 20);
                    assertTrue("Translated ClipX == 5", g.getClipX() == 5);
                    assertTrue("Translated ClipY == 5", g.getClipY() == 5);
                }
            });

            runTest("Key Codes & Game Actions", new TestCase() {
                public void execute() {
                    int upCode = getKeyCode(Canvas.UP);
                    int downCode = getKeyCode(Canvas.DOWN);
                    int fireCode = getKeyCode(Canvas.FIRE);

                    assertTrue("Keycode for UP resolves", upCode != 0);
                    assertTrue("Keycode for DOWN resolves", downCode != 0);
                    assertTrue("Keycode for FIRE resolves", fireCode != 0);

                    int gameAct = getGameAction(upCode);
                    assertTrue("Reverse getGameAction matches UP", gameAct == Canvas.UP);
                }
            });

            completed = true;
            currentTest = "Complete";
            System.out.println("=================================================");
            System.out.println("[TEST-SUITE-SUMMARY] Passed: " + passCount + ", Failed: " + failCount + ", Total: " + (passCount + failCount));
            System.out.println("[TEST-SUITE-STATUS: " + (failCount == 0 ? "PASSED" : "FAILED") + "]");
            System.out.println("=================================================");
            repaint();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {}
            midlet.notifyDestroyed();
        }

        private void runTest(String name, TestCase test) {
            currentTest = name;
            repaint();
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
            repaint();
        }

        private void assertTrue(String message, boolean condition) {
            if (!condition) {
                System.out.println("  [ASSERT-FAIL] " + message);
                throw new RuntimeException("Assertion Failed: " + message);
            }
            System.out.println("  [ASSERT-OK] " + message);
        }

        protected void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            g.setColor(0x0A101D);
            g.fillRect(0, 0, w, h);

            g.setColor(0x00E5FF);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("LCDUI Graphics & Canvas", 5, 5, Graphics.TOP | Graphics.LEFT);

            // Draw visual test pattern in top-right
            g.setColor(0xFF0055);
            g.drawRect(w - 45, 5, 40, 40);
            g.setColor(0x00FF88);
            g.fillRect(w - 40, 10, 30, 30);
            g.setColor(0xFFFF00);
            g.drawLine(w - 40, 10, w - 10, 40);

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
        }
    }

    public interface TestCase {
        void execute() throws Exception;
    }
}

