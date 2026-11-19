package org.neutron.tests.messaging;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.wireless.messaging.*;

public class Jsr120MessagingMIDlet extends MIDlet {

    private Display display;
    private MsgCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new MsgCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class MsgCanvas extends Canvas implements Runnable {
        private final Jsr120MessagingMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public MsgCanvas(Jsr120MessagingMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] JSR-120 Wireless Messaging Suite");
            System.out.println("=================================================");

            runTest("Message Connection Constants", new TestCase() {
                public void execute() {
                    assertTrue("TEXT_MESSAGE constant valid", "text".equals(MessageConnection.TEXT_MESSAGE));
                    assertTrue("BINARY_MESSAGE constant valid", "binary".equals(MessageConnection.BINARY_MESSAGE));
                }
            });

            runTest("SizeExceededException Instantiation", new TestCase() {
                public void execute() {
                    SizeExceededException ex = new SizeExceededException("Payload too large");
                    assertTrue("SizeExceededException message matches", "Payload too large".equals(ex.getMessage()));
                }
            });

            runTest("Message Interface Contract Verification", new TestCase() {
                public void execute() {
                    Message msg = new Message() {
                        private String addr;
                        private Date date;
                        public String getAddress() { return addr; }
                        public void setAddress(String addr) { this.addr = addr; }
                        public Date getTimestamp() { return date; }
                    };
                    msg.setAddress("sms://+1234567890:5000");
                    assertTrue("Message address stored", "sms://+1234567890:5000".equals(msg.getAddress()));
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
            g.drawString("JSR-120 Messaging Suite", 5, 5, Graphics.TOP | Graphics.LEFT);

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
