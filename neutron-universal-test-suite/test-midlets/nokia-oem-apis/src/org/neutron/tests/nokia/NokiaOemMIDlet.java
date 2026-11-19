package org.neutron.tests.nokia;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import com.nokia.mid.ui.*;
import com.nokia.mid.sound.Sound;

public class NokiaOemMIDlet extends MIDlet {

    private Display display;
    private NokiaCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new NokiaCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class NokiaCanvas extends Canvas implements Runnable {
        private final NokiaOemMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public NokiaCanvas(NokiaOemMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] Nokia OEM API Conformance Suite");
            System.out.println("=================================================");

            runTest("Nokia UI DirectGraphics Native Format", new TestCase() {
                public void execute() {
                    Image img = Image.createImage(16, 16);
                    Graphics g = img.getGraphics();
                    DirectGraphics dg = DirectUtils.getDirectGraphics(g);
                    assertTrue("DirectGraphics available", dg != null);
                    int format = dg.getNativePixelFormat();
                    assertTrue("Native pixel format is 32-bit ARGB", format == DirectGraphics.TYPE_INT_8888_ARGB);
                }
            });

            runTest("Nokia UI DirectGraphics 32-bit ARGB drawPixels", new TestCase() {
                public void execute() {
                    Image targetImg = Image.createImage(32, 32);
                    Graphics g = targetImg.getGraphics();
                    DirectGraphics dg = DirectUtils.getDirectGraphics(g);

                    int[] pixels = new int[16 * 16];
                    for (int i = 0; i < pixels.length; i++) {
                        pixels[i] = 0x80FF0000; // Semi-transparent red
                    }

                    dg.drawPixels(pixels, true, 0, 16, 0, 0, 16, 16, 0, DirectGraphics.TYPE_INT_8888_ARGB);
                    dg.setARGBColor(0xFF00FF00);
                    dg.drawTriangle(0, 0, 15, 0, 8, 15, 0xFF00FF00);
                    dg.fillTriangle(16, 0, 31, 0, 24, 15, 0xFF0000FF);
                    assertTrue("drawPixels and triangle operations succeeded", true);
                }
            });

            runTest("Nokia UI DirectUtils Mutable Transparent Image", new TestCase() {
                public void execute() {
                    Image img = DirectUtils.createImage(32, 32, 0x00000000);
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

            runTest("Nokia Sound Component Lifecycle", new TestCase() {
                public void execute() {
                    try {
                        Sound s = new Sound(440, 100); // 440 Hz, 100ms
                        assertTrue("Sound instantiated", s != null);
                        s.setGain(120);
                        assertTrue("Sound gain set", s.getGain() == 120);
                        s.release();
                        assertTrue("Sound released cleanly", true);
                    } catch (Throwable t) {
                        fail("Nokia Sound failed: " + t.getMessage());
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
        }

        protected void paint(Graphics g) {
            g.setColor(0x101820);
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(0x00FF66);
            g.drawString("Nokia OEM API Suite", 5, 5, Graphics.TOP | Graphics.LEFT);

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
