package org.neutron.tests.midp;

import java.io.*;
import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import javax.microedition.rms.*;

public class MidpStandardApisMIDlet extends MIDlet {

    private Display display;
    private TestCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new TestCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class TestCanvas extends Canvas implements Runnable {
        private final MidpStandardApisMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public TestCanvas(MidpStandardApisMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] MIDP Standard APIs & CLDC Conformance");
            System.out.println("=================================================");

            runTest("MIDlet App Properties & System Properties", new TestCase() {
                public void execute() {
                    String suiteName = midlet.getAppProperty("MIDlet-Name");
                    assertTrue("MIDlet-Name property exists", suiteName != null && suiteName.length() > 0);

                    String profiles = System.getProperty("microedition.profiles");
                    assertTrue("microedition.profiles contains MIDP", profiles != null && profiles.indexOf("MIDP") != -1);

                    String config = System.getProperty("microedition.configuration");
                    assertTrue("microedition.configuration contains CLDC", config != null && config.indexOf("CLDC") != -1);

                    String locale = System.getProperty("microedition.locale");
                    assertTrue("microedition.locale non-null", locale != null);

                    String encoding = System.getProperty("microedition.encoding");
                    assertTrue("microedition.encoding non-null", encoding != null);
                }
            });

            runTest("Data Streams (DataInput / DataOutput)", new TestCase() {
                public void execute() throws Exception {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    DataOutputStream dos = new DataOutputStream(baos);

                    dos.writeInt(0x12345678);
                    dos.writeLong(0x1122334455667788L);
                    dos.writeUTF("Neutron Emulator");
                    dos.writeBoolean(true);
                    dos.writeDouble(3.1415926535);
                    dos.flush();

                    byte[] data = baos.toByteArray();
                    assertTrue("Serialized byte array non-empty", data.length > 20);

                    ByteArrayInputStream bais = new ByteArrayInputStream(data);
                    DataInputStream dis = new DataInputStream(bais);

                    int iVal = dis.readInt();
                    assertTrue("DataInput int value", iVal == 0x12345678);

                    long lVal = dis.readLong();
                    assertTrue("DataInput long value", lVal == 0x1122334455667788L);

                    String sVal = dis.readUTF();
                    assertTrue("DataInput UTF string", "Neutron Emulator".equals(sVal));

                    boolean bVal = dis.readBoolean();
                    assertTrue("DataInput boolean", bVal == true);

                    double dVal = dis.readDouble();
                    assertTrue("DataInput double", Math.abs(dVal - 3.1415926535) < 0.00001);
                }
            });

            runTest("Calendar, Date & TimeZone Calculations", new TestCase() {
                public void execute() {
                    Date now = new Date();
                    assertTrue("Date epoch timestamp > 0", now.getTime() > 0);

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(now);
                    int year = cal.get(Calendar.YEAR);
                    assertTrue("Calendar year valid", year >= 2000);

                    TimeZone tz = TimeZone.getDefault();
                    assertTrue("Default timezone non-null", tz != null && tz.getID() != null);
                }
            });

            runTest("Generic Connection Framework Connector", new TestCase() {
                public void execute() {
                    boolean exceptionThrown = false;
                    try {
                        // Open completely invalid protocol
                        Connector.open("invalidprotocol://test");
                    } catch (ConnectionNotFoundException ex) {
                        exceptionThrown = true;
                    } catch (IOException ex) {
                        exceptionThrown = true;
                    }
                    assertTrue("Connector throws ConnectionNotFoundException on invalid protocol", exceptionThrown);
                }
            });

            runTest("Record Management System Baseline", new TestCase() {
                public void execute() throws Exception {
                    String testStore = "TS_MIDP_RMS";
                    try {
                        RecordStore.deleteRecordStore(testStore);
                    } catch (Exception ignore) {}

                    RecordStore rs = RecordStore.openRecordStore(testStore, true);
                    assertTrue("RecordStore open success", rs != null);
                    assertTrue("Initial record count is 0", rs.getNumRecords() == 0);

                    byte[] recData = "Record Number 1".getBytes();
                    int recId = rs.addRecord(recData, 0, recData.length);
                    assertTrue("Added record ID == 1", recId == 1);
                    assertTrue("Num records == 1", rs.getNumRecords() == 1);

                    byte[] retrieved = rs.getRecord(recId);
                    assertTrue("Retrieved record match", new String(retrieved).equals("Record Number 1"));

                    rs.closeRecordStore();
                    RecordStore.deleteRecordStore(testStore);
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
            g.setColor(0x181828);
            g.fillRect(0, 0, w, h);

            g.setColor(0x00D0FF);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("MIDP Standard APIs", 5, 5, Graphics.TOP | Graphics.LEFT);

            g.setColor(0xFFFFFF);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.drawString("Running: " + currentTest, 5, 22, Graphics.TOP | Graphics.LEFT);

            int y = 40;
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

