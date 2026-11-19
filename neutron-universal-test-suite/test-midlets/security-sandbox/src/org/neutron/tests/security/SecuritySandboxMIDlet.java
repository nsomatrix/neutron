package org.neutron.tests.security;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class SecuritySandboxMIDlet extends MIDlet {

    private Display display;
    private SecCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new SecCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class SecCanvas extends Canvas implements Runnable {
        private final SecuritySandboxMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public SecCanvas(SecuritySandboxMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] Security, Permissions & Sandbox");
            System.out.println("=================================================");

            runTest("MIDlet Permission Check Behavior", new TestCase() {
                public void execute() {
                    int perm = midlet.checkPermission("javax.microedition.io.Connector.http");
                    // In MIDP 2.0 (JSR-118): 1 = allowed, 0 = denied, -1 = unknown
                    assertTrue("checkPermission returns valid status (-1, 0, or 1)", perm == 0 || perm == 1 || perm == -1);
                }
            });

            runTest("Malformed Image Binary Safe Rejection", new TestCase() {
                public void execute() {
                    byte[] garbage = new byte[] { 0x00, 0x01, 0x02, 0x03, (byte)0xFF };
                    boolean rejected = false;
                    try {
                        Image.createImage(garbage, 0, garbage.length);
                    } catch (IllegalArgumentException ex) {
                        rejected = true;
                    } catch (Throwable t) {
                        rejected = true;
                    }
                    assertTrue("Malformed image byte buffer rejected safely", rejected);
                }
            });

            runTest("Controlled Memory Exhaustion & Recovery", new TestCase() {
                public void execute() {
                    Vector holder = new Vector();
                    boolean oomCaught = false;
                    try {
                        while (true) {
                            holder.addElement(new byte[1024 * 1024]); // 1MB chunks
                        }
                    } catch (OutOfMemoryError oom) {
                        oomCaught = true;
                    } finally {
                        holder.removeAllElements();
                        holder = null;
                        System.gc();
                    }

                    assertTrue("OutOfMemoryError trapped without engine crash", oomCaught);

                    // Verify allocation functions again after GC recovery
                    byte[] postRecovery = new byte[64 * 1024];
                    assertTrue("Post-recovery allocation succeeded", postRecovery != null && postRecovery.length == 65536);
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
            g.setColor(0x1F1418);
            g.fillRect(0, 0, w, h);

            g.setColor(0xFF5555);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("Security & Sandbox", 5, 5, Graphics.TOP | Graphics.LEFT);

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
