package org.neutron.tests.cldc;

import java.util.*;
import javax.microedition.midlet.MIDlet;
import javax.microedition.lcdui.*;

public class CldcRuntimeConformanceMIDlet extends MIDlet {

    private Display display;
    private TestCanvas canvas;

    public CldcRuntimeConformanceMIDlet() {
        super();
    }

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new TestCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class TestCanvas extends Canvas implements Runnable {
        private final MIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public TestCanvas(MIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] CLDC 1.1 Runtime Conformance Suite");
            System.out.println("=================================================");

            runTest("Bytecode Integer Arithmetic & Overflow", new TestCase() {
                public void execute() {
                    int max = Integer.MAX_VALUE;
                    int overflow = max + 1;
                    assertTrue("Integer overflow wraps to MIN_VALUE", overflow == Integer.MIN_VALUE);

                    long lmax = Long.MAX_VALUE;
                    long loverflow = lmax + 1L;
                    assertTrue("Long overflow wraps to MIN_VALUE", loverflow == Long.MIN_VALUE);

                    byte b = (byte) 255;
                    assertTrue("Byte sign extension to -1", b == -1);

                    int shift = 0x80000000 >>> 1;
                    assertTrue("Logical unsigned shift right", shift == 0x40000000);

                    int ashift = 0x80000000 >> 1;
                    assertTrue("Arithmetic signed shift right", ashift == 0xC0000000);
                }
            });

            runTest("Floating Point IEEE 754 Math", new TestCase() {
                public void execute() {
                    float f1 = 1.0f;
                    float f2 = 3.0f;
                    float fdiv = f1 / f2;
                    assertTrue("Float division non-zero", fdiv > 0.333f && fdiv < 0.334f);

                    double dzero = 0.0;
                    double dinf = 1.0 / dzero;
                    assertTrue("Division by zero produces positive infinity", Double.isInfinite(dinf) && dinf > 0);

                    double dnan = 0.0 / 0.0;
                    assertTrue("0.0 / 0.0 produces NaN", Double.isNaN(dnan));
                    assertTrue("NaN not equal to NaN", !(dnan == dnan));

                    double sqrtVal = Math.sqrt(16.0);
                    assertTrue("Math.sqrt(16.0) == 4.0", Math.abs(sqrtVal - 4.0) < 0.0001);

                    double sinVal = Math.sin(0.0);
                    assertTrue("Math.sin(0.0) == 0.0", Math.abs(sinVal) < 0.0001);

                    double ceilVal = Math.ceil(3.2);
                    assertTrue("Math.ceil(3.2) == 4.0", Math.abs(ceilVal - 4.0) < 0.0001);
                }
            });

            runTest("Polymorphism, Interfaces & Dispatch", new TestCase() {
                public void execute() {
                    BaseClass obj = new DerivedClass();
                    assertTrue("Virtual method dispatch", "Derived".equals(obj.identify()));
                    assertTrue("Overridden method dispatch", obj.compute(5) == 25);

                    Executable exec = (Executable) obj;
                    assertTrue("Interface invocation", exec.executeAction() == 100);

                    assertTrue("instanceof check true for base", obj instanceof BaseClass);
                    assertTrue("instanceof check true for derived", obj instanceof DerivedClass);
                    assertTrue("instanceof check true for interface", obj instanceof Executable);
                }
            });

            runTest("Multi-Dimensional Arrays & Copying", new TestCase() {
                public void execute() {
                    int[][] grid = new int[4][8];
                    for (int r = 0; r < 4; r++) {
                        for (int c = 0; c < 8; c++) {
                            grid[r][c] = r * 10 + c;
                        }
                    }
                    assertTrue("Array index 3,7 value", grid[3][7] == 37);

                    int[] src = new int[] { 10, 20, 30, 40, 50 };
                    int[] dst = new int[5];
                    System.arraycopy(src, 1, dst, 2, 3);
                    assertTrue("System.arraycopy element 2", dst[2] == 20);
                    assertTrue("System.arraycopy element 3", dst[3] == 30);
                    assertTrue("System.arraycopy element 4", dst[4] == 40);

                    boolean oobCaught = false;
                    try {
                        int x = src[10];
                    } catch (ArrayIndexOutOfBoundsException ex) {
                        oobCaught = true;
                    }
                    assertTrue("ArrayIndexOutOfBoundsException caught", oobCaught);
                }
            });

            runTest("String Manipulation & Character Encoding", new TestCase() {
                public void execute() {
                    String str = "Hello Java ME World";
                    assertTrue("String length", str.length() == 19);
                    assertTrue("String startsWith", str.startsWith("Hello"));
                    assertTrue("String indexOf", str.indexOf("Java") == 6);
                    assertTrue("String substring", str.substring(6, 10).equals("Java"));

                    StringBuffer sb = new StringBuffer();
                    for (int i = 0; i < 50; i++) {
                        sb.append(i).append(",");
                    }
                    assertTrue("StringBuffer expansion", sb.length() > 100);

                    byte[] utfBytes = new byte[] { (byte)0xE2, (byte)0x82, (byte)0xAC }; // Euro symbol €
                    try {
                        String euroStr = new String(utfBytes, "UTF-8");
                        assertTrue("UTF-8 decoded length", euroStr.length() == 1);
                    } catch (Exception ex) {
                        fail("UTF-8 decoding failed: " + ex.getMessage());
                    }
                }
            });

            runTest("Collections (Vector, Hashtable, Stack)", new TestCase() {
                public void execute() {
                    Vector v = new Vector(2, 5);
                    v.addElement("Alpha");
                    v.addElement("Beta");
                    v.addElement("Gamma");
                    assertTrue("Vector size", v.size() == 3);
                    assertTrue("Vector contains", v.contains("Beta"));
                    v.removeElement("Beta");
                    assertTrue("Vector after removal", v.size() == 2 && !v.contains("Beta"));

                    Hashtable ht = new Hashtable();
                    ht.put("K1", "V1");
                    ht.put("K2", "V2");
                    assertTrue("Hashtable get", "V1".equals(ht.get("K1")));
                    assertTrue("Hashtable containsKey", ht.containsKey("K2"));
                    ht.put("K1", "V_NEW");
                    assertTrue("Hashtable overwrite", "V_NEW".equals(ht.get("K1")));

                    Stack s = new Stack();
                    s.push("Item 1");
                    s.push("Item 2");
                    assertTrue("Stack peek", "Item 2".equals(s.peek()));
                    assertTrue("Stack pop", "Item 2".equals(s.pop()));
                    assertTrue("Stack size after pop", s.size() == 1);
                }
            });

            runTest("Exception Hierarchy & Finally Guarantees", new TestCase() {
                public void execute() {
                    boolean finallyExecuted = false;
                    boolean caughtSpecific = false;
                    try {
                        String s = null;
                        int len = s.length(); // NPE
                    } catch (NullPointerException npe) {
                        caughtSpecific = true;
                    } catch (Exception ex) {
                        fail("Wrong catch block matched");
                    } finally {
                        finallyExecuted = true;
                    }
                    assertTrue("NullPointerException caught", caughtSpecific);
                    assertTrue("finally block guaranteed execution", finallyExecuted);
                }
            });

            runTest("Thread Synchronization & Monitors", new TestCase() {
                public void execute() {
                    final Object lock = new Object();
                    final boolean[] notified = new boolean[] { false };

                    Thread waiter = new Thread(new Runnable() {
                        public void run() {
                            synchronized (lock) {
                                try {
                                    lock.wait(1000);
                                    notified[0] = true;
                                } catch (InterruptedException ignore) {}
                            }
                        }
                    });
                    waiter.start();

                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException ignore) {}

                    synchronized (lock) {
                        lock.notify();
                    }

                    try {
                        waiter.join(500);
                    } catch (InterruptedException ignore) {}

                    assertTrue("Thread monitor wait/notify succeeded", notified[0]);
                }
            });

            runTest("Timer & TimerTask Scheduling", new TestCase() {
                public void execute() {
                    Timer timer = new Timer();
                    final int[] executionCount = new int[] { 0 };
                    TimerTask task = new TimerTask() {
                        public void run() {
                            executionCount[0]++;
                        }
                    };

                    timer.schedule(task, 50, 50);
                    try {
                        Thread.sleep(220);
                    } catch (InterruptedException ignore) {}

                    task.cancel();
                    timer.cancel();

                    assertTrue("Timer periodic executions count >= 2", executionCount[0] >= 2);
                }
            });

            completed = true;
            currentTest = "Complete";
            System.out.println("=================================================");
            System.out.println("[TEST-SUITE-SUMMARY] Passed: " + passCount + ", Failed: " + failCount + ", Total: " + (passCount + failCount));
            System.out.println("[TEST-SUITE-STATUS: " + (failCount == 0 ? "PASSED" : "FAILED") + "]");
            System.out.println("=================================================");
            repaint();

            // Give frame time to render, then notifyDestroyed
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

        private void fail(String message) {
            System.out.println("  [ASSERT-FAIL] " + message);
            throw new RuntimeException("Test Failed: " + message);
        }

        protected void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            g.setColor(0x101820);
            g.fillRect(0, 0, w, h);

            g.setColor(0x00FF88);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("CLDC 1.1 Conformance", 5, 5, Graphics.TOP | Graphics.LEFT);

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

    public interface Executable {
        int executeAction();
    }

    public static class BaseClass {
        public String identify() {
            return "Base";
        }
        public int compute(int x) {
            return x * 2;
        }
    }

    public static class DerivedClass extends BaseClass implements Executable {
        public String identify() {
            return "Derived";
        }
        public int compute(int x) {
            return x * x;
        }
        public int executeAction() {
            return 100;
        }
    }
}
