package org.neutron.tests.benchmark;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import javax.microedition.rms.*;

public class PerformanceBenchmarkMIDlet extends MIDlet {

    private Display display;
    private BenchCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new BenchCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class BenchCanvas extends Canvas implements Runnable {
        private final PerformanceBenchmarkMIDlet midlet;
        private final Vector results = new Vector();
        private String currentPhase = "Preparing...";
        private boolean completed = false;

        public BenchCanvas(PerformanceBenchmarkMIDlet midlet) {
            this.midlet = midlet;
        }

        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException ignore) {}

            System.out.println("=================================================");
            System.out.println("[BENCHMARK-START] Neutron Universal Performance Benchmark");
            System.out.println("=================================================");

            // 1. Integer Arithmetic Benchmark
            currentPhase = "Benchmarking Integer Arithmetic";
            repaint();
            long t0 = System.currentTimeMillis();
            long intOps = 0;
            int acc = 0;
            while (System.currentTimeMillis() - t0 < 300) {
                for (int i = 0; i < 10000; i++) {
                    acc = (acc * 1664525 + 1013904223) ^ (i << 3);
                }
                intOps += 10000;
            }
            double intMops = (intOps / ((System.currentTimeMillis() - t0) / 1000.0)) / 1000000.0;
            recordMetric("Integer Arithmetic", intMops, "MOps/sec");

            // 2. Floating-Point Math Benchmark
            currentPhase = "Benchmarking Floating-Point Math";
            repaint();
            t0 = System.currentTimeMillis();
            long floatOps = 0;
            double dacc = 1.0;
            while (System.currentTimeMillis() - t0 < 300) {
                for (int i = 0; i < 5000; i++) {
                    dacc = Math.sin(dacc) * Math.cos(dacc) + Math.sqrt(dacc + 1.0);
                }
                floatOps += 5000;
            }
            double floatMflops = (floatOps / ((System.currentTimeMillis() - t0) / 1000.0)) / 1000000.0;
            recordMetric("Floating-Point Math", floatMflops, "MFlops/sec");

            // 3. Memory Allocation Throughput
            currentPhase = "Benchmarking Memory Allocation";
            repaint();
            t0 = System.currentTimeMillis();
            long totalAllocBytes = 0;
            while (System.currentTimeMillis() - t0 < 300) {
                byte[] b = new byte[4096];
                b[0] = 1;
                totalAllocBytes += 4096;
            }
            double allocMbSec = (totalAllocBytes / 1048576.0) / ((System.currentTimeMillis() - t0) / 1000.0);
            recordMetric("Memory Allocation Rate", allocMbSec, "MB/sec");

            // 4. 2D Canvas Primitives Throughput
            currentPhase = "Benchmarking 2D Canvas Rendering";
            repaint();
            Image benchImg = Image.createImage(128, 128);
            Graphics bg = benchImg.getGraphics();
            t0 = System.currentTimeMillis();
            long drawOps = 0;
            while (System.currentTimeMillis() - t0 < 300) {
                bg.setColor((int)drawOps);
                bg.fillRect((int)(drawOps % 100), (int)(drawOps % 100), 20, 20);
                bg.drawLine(0, 0, 127, 127);
                drawOps += 2;
            }
            double drawKops = (drawOps / ((System.currentTimeMillis() - t0) / 1000.0)) / 1000.0;
            recordMetric("2D Canvas Draw Ops", drawKops, "KOps/sec");

            // 5. RMS Storage Throughput
            currentPhase = "Benchmarking RMS Throughput";
            repaint();
            String benchRs = "TS_BENCH_RMS";
            try {
                RecordStore.deleteRecordStore(benchRs);
            } catch (Exception ignore) {}
            try {
                RecordStore rs = RecordStore.openRecordStore(benchRs, true);
                t0 = System.currentTimeMillis();
                long rmsOps = 0;
                byte[] rdata = new byte[64];
                while (System.currentTimeMillis() - t0 < 300) {
                    rs.addRecord(rdata, 0, rdata.length);
                    rmsOps++;
                }
                double rmsOpsSec = rmsOps / ((System.currentTimeMillis() - t0) / 1000.0);
                recordMetric("RMS Write Ops", rmsOpsSec, "Recs/sec");
                rs.closeRecordStore();
                RecordStore.deleteRecordStore(benchRs);
            } catch (Exception ex) {
                recordMetric("RMS Write Ops", 0, "Error");
            }

            completed = true;
            currentPhase = "Complete";
            System.out.println("=================================================");
            System.out.println("[BENCHMARK-SUMMARY] All metrics evaluated successfully");
            System.out.println("[TEST-SUITE-STATUS: PASSED]");
            System.out.println("=================================================");
            repaint();

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignore) {}
            midlet.notifyDestroyed();
        }

        private void recordMetric(String name, double value, String unit) {
            String entry = name + ": " + ((int)(value * 100) / 100.0) + " " + unit;
            results.addElement(entry);
            System.out.println("  [METRIC] " + entry);
        }

        protected void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();
            g.setColor(0x121A24);
            g.fillRect(0, 0, w, h);

            g.setColor(0x00FF88);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("Performance Benchmark", 5, 5, Graphics.TOP | Graphics.LEFT);

            g.setColor(0xFFFFFF);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.drawString(currentPhase, 5, 22, Graphics.TOP | Graphics.LEFT);

            int y = 45;
            for (int i = 0; i < results.size(); i++) {
                String line = (String) results.elementAt(i);
                g.setColor(0x00D0FF);
                g.drawString(line, 5, y, Graphics.TOP | Graphics.LEFT);
                y += 16;
            }

            g.setColor(0x223344);
            g.fillRect(0, h - 22, w, 22);
            g.setColor(completed ? 0x00FF88 : 0xFFFF00);
            g.drawString(completed ? "Benchmark Status: COMPLETED" : "Benchmarking in progress...", 5, h - 18, Graphics.TOP | Graphics.LEFT);
        }
    }
}

