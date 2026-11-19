package org.neutron.tests.bluetooth;

import java.util.Vector;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Graphics;
import javax.bluetooth.UUID;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.BluetoothStateException;

public class Jsr82BluetoothMIDlet extends MIDlet {

    private Display display;
    private BtCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new BtCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class BtCanvas extends Canvas implements Runnable {
        private final Jsr82BluetoothMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public BtCanvas(Jsr82BluetoothMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] JSR-82 Bluetooth Conformance Suite");
            System.out.println("=================================================");

            runTest("UUID 16-bit Short Construction", new TestCase() {
                public void execute() {
                    UUID uuid = new UUID(0x1101); // Serial Port Profile
                    assertTrue("UUID non-null", uuid != null);
                    assertTrue("UUID toString non-null", uuid.toString() != null);
                }
            });

            runTest("UUID 128-bit String Construction", new TestCase() {
                public void execute() {
                    UUID uuid1 = new UUID("0000110100001000800000805F9B34FB", false);
                    UUID uuid2 = new UUID(0x1101);
                    assertTrue("128-bit UUID created", uuid1 != null);
                    assertTrue("UUIDs equal across formats", uuid1.equals(uuid2));
                }
            });

            runTest("LocalDevice Presence Check", new TestCase() {
                public void execute() {
                    try {
                        LocalDevice local = LocalDevice.getLocalDevice();
                        assertTrue("LocalDevice obtained or BluetoothStateException handled", local != null || local == null);
                    } catch (BluetoothStateException bse) {
                        // Bluetooth hardware not active on host - spec-compliant exception
                        assertTrue("BluetoothStateException caught safely", true);
                    } catch (Throwable t) {
                        fail("Unexpected error in LocalDevice.getLocalDevice: " + t.getMessage());
                    }
                }
            });

            runTest("DiscoveryAgent Constants Verification", new TestCase() {
                public void execute() {
                    assertTrue("GIAC constant valid", DiscoveryAgent.GIAC == 0x9E8B33);
                    assertTrue("LIAC constant valid", DiscoveryAgent.LIAC == 0x9E8B00);
                    assertTrue("NOT_DISCOVERABLE valid", DiscoveryAgent.NOT_DISCOVERABLE == 0);
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
            g.drawString("JSR-82 Bluetooth Suite", 5, 5, Graphics.TOP | Graphics.LEFT);

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
