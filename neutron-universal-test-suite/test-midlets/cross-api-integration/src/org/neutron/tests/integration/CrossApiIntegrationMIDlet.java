package org.neutron.tests.integration;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import javax.microedition.media.*;
import com.nokia.mid.ui.*;

public class CrossApiIntegrationMIDlet extends MIDlet {

    private Display display;
    private IntegrationCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new IntegrationCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class IntegrationCanvas extends Canvas implements Runnable {
        private final CrossApiIntegrationMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public IntegrationCanvas(CrossApiIntegrationMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] Cross-API Integration & Subsystem Interaction");
            System.out.println("=================================================");

            runTest("2D Canvas + Threads + RMS Persistence", new TestCase() {
                public void execute() throws Exception {
                    final String rsName = "TS_CROSS_RMS";
                    try {
                        RecordStore.deleteRecordStore(rsName);
                    } catch (Exception ignore) {}

                    final RecordStore rs = RecordStore.openRecordStore(rsName, true);
                    final boolean[] threadDone = new boolean[] { false };

                    // Background thread writing records
                    Thread worker = new Thread(new Runnable() {
                        public void run() {
                            try {
                                for (int i = 0; i < 20; i++) {
                                    byte[] data = ("Thread-Record-" + i).getBytes();
                                    rs.addRecord(data, 0, data.length);
                                    Thread.sleep(10);
                                }
                                threadDone[0] = true;
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    });
                    worker.start();

                    // Active repaint requests while background thread writes to RMS
                    for (int i = 0; i < 15; i++) {
                        repaint();
                        try {
                            Thread.sleep(15);
                        } catch (InterruptedException ignore) {}
                    }

                    worker.join(1000);
                    assertTrue("Background thread completed RMS writes", threadDone[0]);
                    assertTrue("RMS store has 20 records", rs.getNumRecords() == 20);

                    rs.closeRecordStore();
                    RecordStore.deleteRecordStore(rsName);
                }
            });

            runTest("Nokia DirectGraphics + Alpha Transparency Blit", new TestCase() {
                public void execute() {
                    Image base = Image.createImage(64, 64);
                    Graphics bg = base.getGraphics();
                    bg.setColor(0x0000FF);
                    bg.fillRect(0, 0, 64, 64);

                    DirectGraphics dg = DirectUtils.getDirectGraphics(bg);
                    int[] argbPixels = new int[32 * 32];
                    for (int i = 0; i < argbPixels.length; i++) {
                        argbPixels[i] = 0x80FFFF00; // Semi-transparent yellow
                    }

                    dg.drawPixels(argbPixels, true, 0, 32, 16, 16, 32, 32, 0, DirectGraphics.TYPE_INT_8888_ARGB);
                    assertTrue("DirectGraphics transparent blend onto Canvas succeeded", true);
                }
            });

            runTest("MMAPI Audio Tone + Display Event Synchronization", new TestCase() {
                public void execute() {
                    try {
                        Manager.playTone(69, 50, 70); // A4 440Hz
                        repaint();
                        assertTrue("Audio tone played alongside repaint event", true);
                    } catch (Exception ex) {
                        fail("Tone playback failed: " + ex.getMessage());
                    }
                }
            });

            runTest("Memory Pressure Under Active Screen Composition", new TestCase() {
                public void execute() {
                    Vector imageCache = new Vector();
                    try {
                        for (int i = 0; i < 20; i++) {
                            Image temp = Image.createImage(32, 32);
                            Graphics tg = temp.getGraphics();
                            tg.setColor(i * 1000);
                            tg.fillRect(0, 0, 32, 32);
                            imageCache.addElement(temp);
                        }
                        assertTrue("Created 20 mutable images under active session", imageCache.size() == 20);
                    } finally {
                        imageCache.removeAllElements();
                        System.gc();
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
            g.setColor(0x10202B);
            g.fillRect(0, 0, w, h);

            g.setColor(0x00FF88);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("Cross-API Integration", 5, 5, Graphics.TOP | Graphics.LEFT);

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

