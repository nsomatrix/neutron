package org.neutron.tests.boundary;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;
import javax.microedition.io.*;

public class ApiBoundaryExceptionsMIDlet extends MIDlet {

    private Display display;
    private BoundCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new BoundCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class BoundCanvas extends Canvas implements Runnable {
        private final ApiBoundaryExceptionsMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public BoundCanvas(ApiBoundaryExceptionsMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] API Boundary & Exception Suite");
            System.out.println("=================================================");

            runTest("Image.createImage Null & Negative Dimension Rejection", new TestCase() {
                public void execute() {
                    boolean npeCaught = false;
                    try {
                        Image.createImage((String) null);
                    } catch (NullPointerException npe) {
                        npeCaught = true;
                    } catch (Throwable ignore) {}
                    assertTrue("Image.createImage(null) threw NPE", npeCaught);

                    boolean iaeCaught = false;
                    try {
                        Image.createImage(-10, 20);
                    } catch (IllegalArgumentException iae) {
                        iaeCaught = true;
                    } catch (Throwable ignore) {}
                    assertTrue("Negative dimension threw IAE", iaeCaught);
                }
            });

            runTest("RecordStore Invalid Authmode Exception", new TestCase() {
                public void execute() {
                    boolean iaeCaught = false;
                    try {
                        RecordStore.openRecordStore("TestAuth", true, 999, true);
                    } catch (IllegalArgumentException iae) {
                        iaeCaught = true;
                    } catch (Throwable ignore) {}
                    assertTrue("Invalid authmode threw IllegalArgumentException", iaeCaught);
                }
            });

            runTest("Connector.open Null & Invalid Scheme Rejection", new TestCase() {
                public void execute() {
                    boolean iaeCaught = false;
                    try {
                        Connector.open(null);
                    } catch (IllegalArgumentException iae) {
                        iaeCaught = true;
                    } catch (Throwable ignore) {}
                    assertTrue("Connector.open(null) threw IAE", iaeCaught);

                    boolean cnfeCaught = false;
                    try {
                        Connector.open("unknownproto://host:1234");
                    } catch (ConnectionNotFoundException cnfe) {
                        cnfeCaught = true;
                    } catch (Throwable ignore) {}
                    assertTrue("Unknown protocol threw ConnectionNotFoundException", cnfeCaught);
                }
            });

            runTest("Command Long Label Property Contract", new TestCase() {
                public void execute() {
                    Command cmdShort = new Command("OK", Command.OK, 1);
                    assertTrue("Default long label is null", cmdShort.getLongLabel() == null);

                    Command cmdLong = new Command("OK", "Confirm Action", Command.OK, 1);
                    assertTrue("Short label preserved", "OK".equals(cmdLong.getLabel()));
                    assertTrue("Long label preserved", "Confirm Action".equals(cmdLong.getLongLabel()));
                }
            });

            runTest("StringItem Font & PreferredSize Boundary", new TestCase() {
                public void execute() {
                    StringItem item = new StringItem("Title", "Body");
                    Font customFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_LARGE);
                    item.setFont(customFont);
                    assertTrue("Stored font returned", item.getFont() == customFont);

                    item.setPreferredSize(100, 50);
                    assertTrue("Preferred size set without error", true);
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
            g.drawString("API Boundary Exceptions", 5, 5, Graphics.TOP | Graphics.LEFT);

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

