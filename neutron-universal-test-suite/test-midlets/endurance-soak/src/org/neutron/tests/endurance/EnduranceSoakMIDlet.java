package org.neutron.tests.endurance;

import java.util.*;
import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

public class EnduranceSoakMIDlet extends MIDlet {

    private Display display;
    private SoakCanvas canvas;

    protected void startApp() {
        display = Display.getDisplay(this);
        canvas = new SoakCanvas(this);
        display.setCurrent(canvas);
        new Thread(canvas).start();
    }

    protected void pauseApp() {}

    protected void destroyApp(boolean unconditional) {}

    public static class SoakCanvas extends Canvas implements Runnable {
        private final EnduranceSoakMIDlet midlet;
        private boolean running = true;
        private long startTime;
        private long frameCount = 0;
        private double currentFps = 0.0;
        private long targetDurationMs = 5000; // 5-second default soak, overridable
        private int errorCount = 0;

        public SoakCanvas(EnduranceSoakMIDlet midlet) {
            this.midlet = midlet;
            String durStr = midlet.getAppProperty("Soak-Duration-Ms");
            if (durStr != null) {
                try {
                    targetDurationMs = Long.parseLong(durStr.trim());
                } catch (Exception ignore) {}
            }
        }

        public void run() {
            startTime = System.currentTimeMillis();
            long lastFpsTime = startTime;
            long lastFpsFrames = 0;

            System.out.println("=================================================");
            System.out.println("[SOAK-START] Long-Duration Endurance & Soak Test");
            System.out.println("  Target Duration: " + (targetDurationMs / 1000) + " seconds");
            System.out.println("=================================================");

            while (running) {
                long now = System.currentTimeMillis();
                long elapsed = now - startTime;

                if (elapsed >= targetDurationMs) {
                    running = false;
                    break;
                }

                // Render cycle & allocation churn
                try {
                    byte[] tempAlloc = new byte[1024]; // Churn memory
                    tempAlloc[0] = (byte)(frameCount & 0xFF);
                } catch (Throwable t) {
                    errorCount++;
                }

                frameCount++;

                if (now - lastFpsTime >= 1000) {
                    long df = frameCount - lastFpsFrames;
                    long dt = now - lastFpsTime;
                    currentFps = (df * 1000.0) / dt;
                    lastFpsTime = now;
                    lastFpsFrames = frameCount;

                    Runtime rt = Runtime.getRuntime();
                    long freeMem = rt.freeMemory();
                    long totalMem = rt.totalMemory();
                    System.out.println("[SOAK-PULSE] Elapsed: " + (elapsed / 1000) + "s, FPS: " + ((int)(currentFps * 10) / 10.0) + ", FreeMem: " + (freeMem / 1024) + " KB / " + (totalMem / 1024) + " KB");
                }

                repaint();
                serviceRepaints();

                try {
                    Thread.sleep(15); // ~60 FPS pacing
                } catch (InterruptedException ignore) {}
            }

            System.out.println("=================================================");
            System.out.println("[SOAK-SUMMARY] Completed " + frameCount + " frames with " + errorCount + " errors.");
            System.out.println("[TEST-SUITE-STATUS: " + (errorCount == 0 ? "PASSED" : "FAILED") + "]");
            System.out.println("=================================================");

            try {
                Thread.sleep(500);
            } catch (InterruptedException ignore) {}
            midlet.notifyDestroyed();
        }

        protected void paint(Graphics g) {
            int w = getWidth();
            int h = getHeight();

            g.setColor(0x0C1920);
            g.fillRect(0, 0, w, h);

            g.setColor(0x00FF88);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL));
            g.drawString("Endurance & Soak Test", 5, 5, Graphics.TOP | Graphics.LEFT);

            long elapsed = System.currentTimeMillis() - startTime;
            g.setColor(0xFFFFFF);
            g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
            g.drawString("Elapsed: " + (elapsed / 1000) + "s / " + (targetDurationMs / 1000) + "s", 5, 25, Graphics.TOP | Graphics.LEFT);
            g.drawString("FPS: " + ((int)(currentFps * 10) / 10.0), 5, 42, Graphics.TOP | Graphics.LEFT);
            g.drawString("Frames: " + frameCount, 5, 59, Graphics.TOP | Graphics.LEFT);

            Runtime rt = Runtime.getRuntime();
            g.drawString("RAM: " + (rt.freeMemory() / 1024) + "K free / " + (rt.totalMemory() / 1024) + "K", 5, 76, Graphics.TOP | Graphics.LEFT);

            // Animated rotating indicator
            int cx = w / 2;
            int cy = h / 2 + 30;
            int radius = 30;
            double angle = (frameCount * 0.1) % (2.0 * Math.PI);
            int px = cx + (int)(Math.cos(angle) * radius);
            int py = cy + (int)(Math.sin(angle) * radius);

            g.setColor(0x00D0FF);
            g.drawArc(cx - radius, cy - radius, radius * 2, radius * 2, 0, 360);
            g.setColor(0xFF0055);
            g.fillArc(px - 6, py - 6, 12, 12, 0, 360);

            g.setColor(0x223344);
            g.fillRect(0, h - 22, w, 22);
            g.setColor(running ? 0x00FF88 : 0xFFFF00);
            g.drawString(running ? "SOAK RUNNING..." : "SOAK COMPLETE", 5, h - 18, Graphics.TOP | Graphics.LEFT);
        }
    }
}

