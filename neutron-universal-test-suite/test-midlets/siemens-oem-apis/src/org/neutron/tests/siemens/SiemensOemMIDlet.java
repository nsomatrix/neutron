package org.neutron.tests.siemens;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import com.siemens.mp.game.Light;
import com.siemens.mp.game.Vibrator;
import com.siemens.mp.game.Sound;
import com.siemens.mp.game.Melody;
import com.siemens.mp.game.ExtendedImage;

public class SiemensOemMIDlet extends MIDlet {

    private Display display;
    private SiemensCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new SiemensCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class SiemensCanvas extends Canvas implements Runnable {
        private final SiemensOemMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public SiemensCanvas(SiemensOemMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] Siemens OEM API Conformance Suite");
            System.out.println("=================================================");

            runTest("Siemens Light Control", new TestCase() {
                public void execute() {
                    Light.setLightOn();
                    Light.setLightOff();
                    assertTrue("Siemens Light toggle executed cleanly", true);
                }
            });

            runTest("Siemens Vibrator Control", new TestCase() {
                public void execute() {
                    Vibrator.triggerVibrator(50);
                    Vibrator.stopVibrator();
                    assertTrue("Siemens Vibrator executed cleanly", true);
                }
            });

            runTest("Siemens Sound Tone Synthesis", new TestCase() {
                public void execute() {
                    try {
                        Sound.playTone(440, 50);
                        assertTrue("Siemens Sound.playTone executed cleanly", true);
                    } catch (Throwable t) {
                        fail("Siemens Sound failed: " + t.getMessage());
                    }
                }
            });

            runTest("Siemens Melody Construction", new TestCase() {
                public void execute() {
                    try {
                        Melody m = new Melody();
                        assertTrue("Siemens Melody created", m != null);
                    } catch (Throwable t) {
                        fail("Siemens Melody failed: " + t.getMessage());
                    }
                }
            });

            runTest("Siemens ExtendedImage", new TestCase() {
                public void execute() {
                    try {
                        Image base = Image.createImage(16, 16);
                        ExtendedImage ext = new ExtendedImage(base);
                        assertTrue("ExtendedImage created", ext != null);
                        assertTrue("ExtendedImage width == 16", ext.getImage().getWidth() == 16);
                        assertTrue("ExtendedImage height == 16", ext.getImage().getHeight() == 16);
                    } catch (Throwable t) {
                        fail("Siemens ExtendedImage failed: " + t.getMessage());
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
            g.drawString("Siemens OEM API Suite", 5, 5, Graphics.TOP | Graphics.LEFT);

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
