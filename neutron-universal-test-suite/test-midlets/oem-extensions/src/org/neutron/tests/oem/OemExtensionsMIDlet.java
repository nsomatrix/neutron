package org.neutron.tests.oem;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import com.nokia.mid.ui.*;
import com.siemens.mp.game.Light;
import com.siemens.mp.game.Vibrator;
import com.samsung.util.Vibration;
import com.samsung.util.LCDLight;

public class OemExtensionsMIDlet extends MIDlet {

    private Display display;
    private OemCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new OemCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class OemCanvas extends Canvas implements Runnable {
        private final OemExtensionsMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public OemCanvas(OemExtensionsMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] OEM & Historical Vendor Extensions");
            System.out.println("=================================================");

            runTest("Nokia UI DirectGraphics 32-bit ARGB drawPixels", new TestCase() {
                public void execute() {
                    Image targetImg = Image.createImage(32, 32);
                    Graphics g = targetImg.getGraphics();
                    DirectGraphics dg = DirectUtils.getDirectGraphics(g);
                    assertTrue("DirectGraphics non-null", dg != null);

                    int[] pixels = new int[16 * 16];
                    for (int i = 0; i < pixels.length; i++) {
                        pixels[i] = 0x80FF0000; // Semi-transparent red
                    }

                    dg.drawPixels(pixels, true, 0, 16, 0, 0, 16, 16, 0, DirectGraphics.TYPE_INT_8888_ARGB);
                    assertTrue("drawPixels 32-bit executed", true);

                    dg.setARGBColor(0xFF00FF00);
                    dg.drawTriangle(0, 0, 15, 0, 8, 15, 0xFF00FF00);
                    dg.fillTriangle(16, 0, 31, 0, 24, 15, 0xFF0000FF);
                    assertTrue("drawTriangle and fillTriangle executed", true);
                }
            });

            runTest("Nokia UI DirectUtils Mutable Transparent Image", new TestCase() {
                public void execute() {
                    Image img = DirectUtils.createImage(32, 32, 0x00000000); // Fully transparent
                    assertTrue("DirectUtils created transparent image", img != null);
                    assertTrue("Image width == 32", img.getWidth() == 32);
                    assertTrue("Image height == 32", img.getHeight() == 32);
                }
            });

            runTest("Nokia UI DeviceControl (Lights & Vibra)", new TestCase() {
                public void execute() {
                    try {
                        DeviceControl.setLights(0, 100);
                        DeviceControl.startVibra(50, 50);
                        DeviceControl.stopVibra();
                        assertTrue("DeviceControl calls executed cleanly", true);
                    } catch (Throwable t) {
                        fail("DeviceControl failed: " + t.getMessage());
                    }
                }
            });

            runTest("Siemens Extension API (Light & Vibrator)", new TestCase() {
                public void execute() {
                    try {
                        Light.setLightOn();
                        Light.setLightOff();
                        Vibrator.triggerVibrator(50);
                        assertTrue("Siemens Light and Vibrator executed", true);
                    } catch (Throwable t) {
                        fail("Siemens API failed: " + t.getMessage());
                    }
                }
            });

            runTest("Samsung Extension API (Vibration & LCDLight)", new TestCase() {
                public void execute() {
                    try {
                        boolean vibSupported = Vibration.isSupported();
                        Vibration.start(50, 10);
                        Vibration.stop();

                        boolean lightSupported = LCDLight.isSupported();
                        LCDLight.on(1);
                        LCDLight.off();
                        assertTrue("Samsung Vibration & LCDLight executed", true);
                    } catch (Throwable t) {
                        fail("Samsung API failed: " + t.getMessage());
                    }
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

        private void fail(String message) {
            System.out.println("  [ASSERT-FAIL] " + message);
            throw new RuntimeException("Test Failed: " + message);
        }

        protected void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            g.setColor(0x111E25);
            g.fillRect(0, 0, w, h);

            g.setColor(0x00FF88);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("OEM Handset Extensions", 5, 5, Graphics.TOP | Graphics.LEFT);

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

