package org.neutron.tests.rms;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

public class RmsStorageStressMIDlet extends MIDlet {

    private Display display;
    private RmsCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new RmsCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class RmsCanvas extends Canvas implements Runnable {
        private final RmsStorageStressMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public RmsCanvas(RmsStorageStressMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] RMS Storage & Enumeration Stress");
            System.out.println("=================================================");

            runTest("RecordStore CRUD & Metadata", new TestCase() {
                public void execute() throws Exception {
                    String rsName = "TS_RMS_CRUD";
                    try {
                        RecordStore.deleteRecordStore(rsName);
                    } catch (Exception ignore) {}

                    RecordStore rs = RecordStore.openRecordStore(rsName, true);
                    assertTrue("RecordStore non-null", rs != null);
                    assertTrue("Initial num records == 0", rs.getNumRecords() == 0);
                    assertTrue("Initial next record ID == 1", rs.getNextRecordID() == 1);

                    byte[] rec1 = "Data-1".getBytes();
                    int id1 = rs.addRecord(rec1, 0, rec1.length);
                    assertTrue("Record 1 ID == 1", id1 == 1);
                    assertTrue("Num records == 1", rs.getNumRecords() == 1);
                    assertTrue("Next ID == 2", rs.getNextRecordID() == 2);

                    byte[] rec2 = "Data-2".getBytes();
                    int id2 = rs.addRecord(rec2, 0, rec2.length);
                    assertTrue("Record 2 ID == 2", id2 == 2);
                    assertTrue("Num records == 2", rs.getNumRecords() == 2);

                    byte[] updated = "Data-1-Updated".getBytes();
                    rs.setRecord(id1, updated, 0, updated.length);
                    byte[] retrieved = rs.getRecord(id1);
                    assertTrue("Updated record matches", "Data-1-Updated".equals(new String(retrieved)));

                    rs.deleteRecord(id2);
                    assertTrue("Num records after deletion == 1", rs.getNumRecords() == 1);

                    rs.closeRecordStore();
                    RecordStore.deleteRecordStore(rsName);
                }
            });

            runTest("RecordEnumeration, Filters & Comparators", new TestCase() {
                public void execute() throws Exception {
                    String rsName = "TS_RMS_ENUM";
                    try {
                        RecordStore.deleteRecordStore(rsName);
                    } catch (Exception ignore) {}

                    RecordStore rs = RecordStore.openRecordStore(rsName, true);
                    rs.addRecord("Apple".getBytes(), 0, 5);
                    rs.addRecord("Banana".getBytes(), 0, 6);
                    rs.addRecord("Cherry".getBytes(), 0, 6);
                    rs.addRecord("Avocado".getBytes(), 0, 7);

                    // Filter for records starting with 'A'
                    RecordFilter aFilter = new RecordFilter() {
                        public boolean matches(byte[] candidate) {
                            return candidate != null && candidate.length > 0 && candidate[0] == 'A';
                        }
                    };

                    RecordEnumeration aEnum = rs.enumerateRecords(aFilter, null, false);
                    assertTrue("Filter 'A' matches 2 records", aEnum.numRecords() == 2);
                    aEnum.destroy();

                    // Comparator ascending
                    RecordComparator ascComp = new RecordComparator() {
                        public int compare(byte[] rec1, byte[] rec2) {
                            String s1 = new String(rec1);
                            String s2 = new String(rec2);
                            int cmp = s1.compareTo(s2);
                            return cmp < 0 ? RecordComparator.PRECEDES : (cmp > 0 ? RecordComparator.FOLLOWS : RecordComparator.EQUIVALENT);
                        }
                    };

                    RecordEnumeration sortEnum = rs.enumerateRecords(null, ascComp, false);
                    assertTrue("Sorted enum has 4 records", sortEnum.numRecords() == 4);

                    byte[] first = sortEnum.nextRecord();
                    assertTrue("First sorted record is Apple", "Apple".equals(new String(first)));
                    byte[] second = sortEnum.nextRecord();
                    assertTrue("Second sorted record is Avocado", "Avocado".equals(new String(second)));

                    sortEnum.destroy();
                    rs.closeRecordStore();
                    RecordStore.deleteRecordStore(rsName);
                }
            });

            runTest("High-Volume Record Stress (200 records)", new TestCase() {
                public void execute() throws Exception {
                    String rsName = "TS_RMS_STRESS";
                    try {
                        RecordStore.deleteRecordStore(rsName);
                    } catch (Exception ignore) {}

                    RecordStore rs = RecordStore.openRecordStore(rsName, true);
                    int count = 200;
                    for (int i = 0; i < count; i++) {
                        byte[] data = ("Record_Payload_Data_Item_" + i).getBytes();
                        rs.addRecord(data, 0, data.length);
                    }
                    assertTrue("200 records added", rs.getNumRecords() == 200);

                    // Random spot verification
                    byte[] r50 = rs.getRecord(50);
                    assertTrue("Record 50 retrieved", ("Record_Payload_Data_Item_49").equals(new String(r50)));

                    byte[] r150 = rs.getRecord(150);
                    assertTrue("Record 150 retrieved", ("Record_Payload_Data_Item_149").equals(new String(r150)));

                    rs.closeRecordStore();
                    RecordStore.deleteRecordStore(rsName);
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
            g.setColor(0x1A1C20);
            g.fillRect(0, 0, w, h);

            g.setColor(0x00FF88);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("RMS Storage Stress", 5, 5, Graphics.TOP | Graphics.LEFT);

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

