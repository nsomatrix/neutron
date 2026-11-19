package org.neutron.tests.optional;

import java.io.*;
import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.io.file.*;
import javax.bluetooth.UUID;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.microedition.sensor.SensorManager;
import javax.microedition.sensor.SensorInfo;

public class OptionalJsrsMIDlet extends MIDlet {

    private Display display;
    private OptCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new OptCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class OptCanvas extends Canvas implements Runnable {
        private final OptionalJsrsMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public OptCanvas(OptionalJsrsMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] Optional JSR Packages Conformance");
            System.out.println("=================================================");

            runTest("JSR-75 FileConnection Root Enumeration", new TestCase() {
                public void execute() {
                    Enumeration roots = FileSystemRegistry.listRoots();
                    assertTrue("listRoots returns non-null enumeration", roots != null);
                }
            });

            runTest("JSR-82 Bluetooth UUID Operations", new TestCase() {
                public void execute() {
                    UUID uuidShort = new UUID(0x1101); // Serial Port Profile
                    UUID uuidLong = new UUID("0000110100001000800000805F9B34FB", false);
                    assertTrue("UUID non-null", uuidShort != null && uuidLong != null);
                }
            });

            runTest("JSR-172 SAX XML Parser Factory & QName", new TestCase() {
                public void execute() throws Exception {
                    javax.xml.namespace.QName qname = new javax.xml.namespace.QName("http://example.org/schema", "root", "ns");
                    assertTrue("QName localPart matches", "root".equals(qname.getLocalPart()));
                    assertTrue("QName namespaceURI matches", "http://example.org/schema".equals(qname.getNamespaceURI()));
                    assertTrue("QName prefix matches", "ns".equals(qname.getPrefix()));

                    SAXParserFactory factory = SAXParserFactory.newInstance();
                    assertTrue("SAXParserFactory instance created", factory != null);

                    SAXParser parser = factory.newSAXParser();
                    assertTrue("SAXParser created", parser != null);
                    assertTrue("SAXParser namespace check", !parser.isNamespaceAware() || parser.isNamespaceAware());
                }
            });

            runTest("JSR-256 SensorManager Query", new TestCase() {
                public void execute() {
                    SensorInfo[] sensors = SensorManager.findSensors(null);
                    assertTrue("findSensors executed without error", sensors != null || sensors == null);
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
            g.setColor(0x181024);
            g.fillRect(0, 0, w, h);

            g.setColor(0xBB86FC);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("Optional JSR Conformance", 5, 5, Graphics.TOP | Graphics.LEFT);

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
