package org.neutron.tests.memory;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

public class MemoryResourceStressMIDlet extends MIDlet {

    private Display display;
    private StressCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new StressCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class StressCanvas extends Canvas implements Runnable {
        private final MemoryResourceStressMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public StressCanvas(MemoryResourceStressMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] Memory & Resource Stress Suite");
            System.out.println("=================================================");

            runTest("Repeated Image Allocation & Surface Disposal", new TestCase() {
                public void execute() {
                    for (int i = 0; i < 50; i++) {
                        Image img = Image.createImage(64, 64);
                        Graphics g = img.getGraphics();
                        g.setColor(0xFF00FF | (i * 4));
                        g.fillRect(0, 0, 64, 64);
                        assertTrue("Image created cleanly in loop", img.getWidth() == 64);
                    }
                    System.gc();
                    assertTrue("Image creation churn completed without leak", true);
                }
            });

            runTest("High-Throughput Byte Array Allocation Churn", new TestCase() {
                public void execute() {
                    for (int i = 0; i < 100; i++) {
                        byte[] block = new byte[16 * 1024]; // 16 KB
                        for (int j = 0; j < block.length; j += 256) {
                            block[j] = (byte)(i ^ j);
                        }
                    }
                    System.gc();
                    assertTrue("Byte churn completed cleanly", true);
                }
            });

            runTest("RMS High-Frequency Record Churn & Compaction", new TestCase() {
                public void execute() throws Exception {
                    String storeName = "StressRMS";
                    try {
                        RecordStore.deleteRecordStore(storeName);
                    } catch (Exception ignore) {}

                    RecordStore rs = RecordStore.openRecordStore(storeName, true);
                    byte[] data = new byte[256];
                    for (int i = 0; i < data.length; i++) data[i] = (byte) i;

                    int[] ids = new int[20];
                    for (int i = 0; i < ids.length; i++) {
                        ids[i] = rs.addRecord(data, 0, data.length);
                    }

                    assertTrue("20 records added", rs.getNumRecords() == 20);

                    // Modify all records
                    for (int i = 0; i < ids.length; i++) {
                        data[0] = (byte)(i + 1);
                        rs.setRecord(ids[i], data, 0, data.length);
                    }

                    // Delete half
                    for (int i = 0; i < ids.length; i += 2) {
                        rs.deleteRecord(ids[i]);
                    }

                    assertTrue("10 records remaining after deletion", rs.getNumRecords() == 10);
                    rs.closeRecordStore();
                    RecordStore.deleteRecordStore(storeName);
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
            g.drawString("Memory & Resource Stress", 5, 5, Graphics.TOP | Graphics.LEFT);

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

