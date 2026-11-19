package org.neutron.tests.threading;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class ThreadingConcurrencyMIDlet extends MIDlet {

    private Display display;
    private ThreadCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new ThreadCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class ThreadCanvas extends Canvas implements Runnable {
        private final ThreadingConcurrencyMIDlet midlet;
        private final Vector logEntries = new Vector();
        private int passCount = 0;
        private int failCount = 0;
        private boolean completed = false;
        private String currentTest = "Initializing...";

        public ThreadCanvas(ThreadingConcurrencyMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[SUITE-START] Threading & Concurrency Conformance Suite");
            System.out.println("=================================================");

            runTest("Concurrent Worker Thread Coordination", new TestCase() {
                public void execute() throws Exception {
                    final int workerCount = 4;
                    final int iterationsPerWorker = 250;
                    final int[] sharedCounter = new int[] { 0 };
                    final Object lock = new Object();
                    Thread[] workers = new Thread[workerCount];

                    for (int i = 0; i < workerCount; i++) {
                        workers[i] = new Thread(new Runnable() {
                            public void run() {
                                for (int j = 0; j < iterationsPerWorker; j++) {
                                    synchronized (lock) {
                                        sharedCounter[0]++;
                                    }
                                }
                            }
                        });
                        workers[i].start();
                    }

                    for (int i = 0; i < workerCount; i++) {
                        workers[i].join(2000);
                    }

                    assertTrue("Counter equals total iterations across workers", sharedCounter[0] == workerCount * iterationsPerWorker);
                }
            });

            runTest("Producer-Consumer Queue with wait/notifyAll", new TestCase() {
                public void execute() throws Exception {
                    final Vector queue = new Vector();
                    final int capacity = 5;
                    final int totalItems = 20;
                    final Vector consumed = new Vector();

                    Thread producer = new Thread(new Runnable() {
                        public void run() {
                            for (int i = 0; i < totalItems; i++) {
                                synchronized (queue) {
                                    while (queue.size() >= capacity) {
                                        try {
                                            queue.wait(500);
                                        } catch (InterruptedException ignore) {}
                                    }
                                    queue.addElement(new Integer(i));
                                    queue.notifyAll();
                                }
                            }
                        }
                    });

                    Thread consumer = new Thread(new Runnable() {
                        public void run() {
                            while (consumed.size() < totalItems) {
                                synchronized (queue) {
                                    while (queue.isEmpty() && consumed.size() < totalItems) {
                                        try {
                                            queue.wait(500);
                                        } catch (InterruptedException ignore) {}
                                    }
                                    if (!queue.isEmpty()) {
                                        Object item = queue.firstElement();
                                        queue.removeElementAt(0);
                                        consumed.addElement(item);
                                        queue.notifyAll();
                                    }
                                }
                            }
                        }
                    });

                    producer.start();
                    consumer.start();

                    producer.join(2000);
                    consumer.join(2000);

                    assertTrue("All produced items consumed in order", consumed.size() == totalItems);
                    assertTrue("First item was 0", ((Integer) consumed.firstElement()).intValue() == 0);
                    assertTrue("Last item was 19", ((Integer) consumed.lastElement()).intValue() == 19);
                }
            });

            runTest("Thread Priority Adjustment", new TestCase() {
                public void execute() {
                    Thread t = new Thread();
                    t.setPriority(Thread.MAX_PRIORITY);
                    assertTrue("Thread max priority set", t.getPriority() == Thread.MAX_PRIORITY);
                    t.setPriority(Thread.MIN_PRIORITY);
                    assertTrue("Thread min priority set", t.getPriority() == Thread.MIN_PRIORITY);
                    t.setPriority(Thread.NORM_PRIORITY);
                    assertTrue("Thread norm priority set", t.getPriority() == Thread.NORM_PRIORITY);
                }
            });

            runTest("Interrupted Thread Wakeup", new TestCase() {
                public void execute() throws Exception {
                    final boolean[] interruptedCaught = new boolean[] { false };
                    Thread sleeper = new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ie) {
                                interruptedCaught[0] = true;
                            }
                        }
                    });

                    sleeper.start();
                    Thread.sleep(50);
                    sleeper.interrupt();
                    sleeper.join(1000);

                    assertTrue("InterruptedException caught upon thread interrupt", interruptedCaught[0]);
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
            g.drawString("Threading & Concurrency", 5, 5, Graphics.TOP | Graphics.LEFT);

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
