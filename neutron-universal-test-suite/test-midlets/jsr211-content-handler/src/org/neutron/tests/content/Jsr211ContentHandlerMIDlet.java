package org.neutron.tests.content;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.content.*;

public class Jsr211ContentHandlerMIDlet extends MIDlet {

    private Display display;
    private ChCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new ChCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class ChCanvas extends Canvas implements Runnable {
        private final Jsr211ContentHandlerMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public ChCanvas(Jsr211ContentHandlerMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] JSR-211 Content Handler Suite");
            System.out.println("=================================================");

            runTest("Invocation Object Construction", new TestCase() {
                public void execute() {
                    Invocation inv = new Invocation("http://example.org/test.png", "image/png");
                    assertTrue("Invocation non-null", inv != null);
                    assertTrue("Invocation URL matches", "http://example.org/test.png".equals(inv.getURL()));
                    assertTrue("Invocation type matches", "image/png".equals(inv.getType()));
                }
            });

            runTest("ActionNameMap Creation & Query", new TestCase() {
                public void execute() {
                    String[] actions = new String[] { "open", "edit" };
                    String[] actionNames = new String[] { "Open Item", "Edit Item" };
                    ActionNameMap map = new ActionNameMap(actions, actionNames, "en");
                    assertTrue("ActionNameMap non-null", map != null);
                    assertTrue("Action name resolved", "Open Item".equals(map.getActionName("open")));
                    assertTrue("Locale matches", "en".equals(map.getLocale()));
                }
            });

            runTest("ContentHandlerException Instantiation", new TestCase() {
                public void execute() {
                    ContentHandlerException che = new ContentHandlerException("Handler not found", ContentHandlerException.NO_REGISTERED_HANDLER);
                    assertTrue("Exception message matches", "Handler not found".equals(che.getMessage()));
                    assertTrue("Error code matches", che.getErrorCode() == ContentHandlerException.NO_REGISTERED_HANDLER);
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
            g.drawString("JSR-211 Content Handler Suite", 5, 5, Graphics.TOP | Graphics.LEFT);

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
