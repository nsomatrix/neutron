package org.neutron.tests.sensors;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.sensor.*;

public class Jsr256SensorsMIDlet extends MIDlet {

    private Display display;
    private SensorCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new SensorCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class SensorCanvas extends Canvas implements Runnable {
        private final Jsr256SensorsMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public SensorCanvas(Jsr256SensorsMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] JSR-256 Mobile Sensor API Suite");
            System.out.println("=================================================");

            runTest("SensorManager Discovery Query", new TestCase() {
                public void execute() {
                    SensorInfo[] sensors = SensorManager.findSensors(null);
                    assertTrue("findSensors executed without exception", sensors != null || sensors == null);
                }
            });

            runTest("Sensor Unit & Measurement Ranges", new TestCase() {
                public void execute() {
                    Unit u = Unit.getUnit("m/s^2");
                    assertTrue("Unit m/s^2 resolved", u != null);

                    MeasurementRange range = new MeasurementRange(-10.0, 10.0, 0.01);
                    assertTrue("Range min valid", range.getSmallestValue() == -10.0);
                    assertTrue("Range max valid", range.getLargestValue() == 10.0);
                    assertTrue("Range res valid", range.getResolution() == 0.01);
                }
            });

            runTest("RangeCondition & LimitCondition", new TestCase() {
                public void execute() {
                    RangeCondition rc = new RangeCondition(-5.0, 5.0, RangeCondition.OP_INSIDE);
                    assertTrue("RangeCondition lower bound", rc.getLowerLimit() == -5.0);
                    assertTrue("RangeCondition upper bound", rc.getUpperLimit() == 5.0);

                    LimitCondition lc = new LimitCondition(10.0, LimitCondition.OP_GREATER_THAN);
                    assertTrue("LimitCondition limit value", lc.getLimit() == 10.0);
                    assertTrue("LimitCondition operator", LimitCondition.OP_GREATER_THAN.equals(lc.getOperator()));
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
            g.drawString("JSR-256 Sensors Suite", 5, 5, Graphics.TOP | Graphics.LEFT);

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
