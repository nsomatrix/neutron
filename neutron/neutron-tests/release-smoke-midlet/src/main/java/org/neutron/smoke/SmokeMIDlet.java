package org.neutron.smoke;

import com.mascotcapsule.micro3d.v3.Effect3D;
import com.nokia.mid.ui.DirectUtils;

import javax.microedition.io.PushRegistry;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.m3g.Graphics3D;
import javax.microedition.media.Manager;
import javax.microedition.media.MediaException;
import javax.microedition.midlet.MIDlet;
import javax.microedition.rms.RecordStore;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Self-terminating E2E smoke MIDlet executed inside the packed release JAR
 * (under MIDletClassLoader bytecode instrumentation) to verify MIDlet lifecycle,
 * graphics, RecordStore, Timer/Thread rewriting, resource loading, and extensions.
 */
public class SmokeMIDlet extends MIDlet {

    private static final String RMS_NAME = "smoke_rms";

    private static final class SmokeCanvas extends GameCanvas {
        SmokeCanvas() {
            super(false);
        }

        void renderFrame() {
            Graphics g = getGraphics();
            g.setColor(0x20242A);
            g.fillRect(0, 0, Math.max(1, getWidth()), Math.max(1, getHeight()));
            g.setColor(0x00FF88);
            g.fillRect(4, 20, 24, 24);
            g.setFont(Font.getDefaultFont());
            g.drawString("Neutron Smoke", 4, 4, Graphics.TOP | Graphics.LEFT);
            flushGraphics();
            repaint();
            serviceRepaints();
        }
    }

    protected void startApp() {
        final SmokeCanvas canvas = new SmokeCanvas();
        Display.getDisplay(this).setCurrent(canvas);

        Thread worker = new Thread(new Runnable() {
            public void run() {
                try {
                    runAllChecks(canvas);
                    System.out.println("SMOKE_MIDLET_PASS");
                } catch (Throwable t) {
                    System.out.println("SMOKE_MIDLET_FAIL: " + t);
                    t.printStackTrace();
                } finally {
                    notifyDestroyed();
                }
            }
        });
        worker.start();
    }

    private void runAllChecks(SmokeCanvas canvas) throws Exception {
        // 1. Injected System.getProperty & getResourceAsStream
        String platform = System.getProperty("microedition.platform");
        if (platform == null || platform.length() == 0) {
            throw new RuntimeException("microedition.platform missing");
        }

        InputStream is = SmokeMIDlet.class.getResourceAsStream("/smoke.txt");
        if (is == null) {
            throw new RuntimeException("Missing /smoke.txt inside MIDlet JAR");
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[64];
        int n;
        while ((n = is.read(buf)) != -1) {
            baos.write(buf, 0, n);
        }
        is.close();
        String text = new String(baos.toByteArray(), "UTF-8").trim();
        if (!"neutron-release-smoke-ok".equals(text)) {
            throw new RuntimeException("Unexpected /smoke.txt content: " + text);
        }

        // 2. Canvas / GameCanvas graphics & image rendering
        canvas.renderFrame();
        int[] argb = new int[16];
        for (int i = 0; i < argb.length; i++) {
            argb[i] = 0xFF11AA44;
        }
        Image rgb = Image.createRGBImage(argb, 4, 4, true);
        if (rgb.getWidth() != 4 || rgb.getHeight() != 4) {
            throw new RuntimeException("RGB image creation failed");
        }

        // 3. MIDletTimer / MIDletTimerTask / Thread.sleep rewriting
        final boolean[] timerRan = new boolean[1];
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                synchronized (timerRan) {
                    timerRan[0] = true;
                    timerRan.notifyAll();
                }
            }
        }, 20L);
        synchronized (timerRan) {
            long deadline = System.currentTimeMillis() + 3000L;
            while (!timerRan[0] && System.currentTimeMillis() < deadline) {
                timerRan.wait(100L);
            }
        }
        timer.cancel();
        if (!timerRan[0]) {
            throw new RuntimeException("MIDletTimer task did not execute");
        }
        Thread.sleep(50L);

        // 4. RecordStore persistence (FileRecordStoreManager / MemoryRecordStoreManager)
        try {
            RecordStore.deleteRecordStore(RMS_NAME);
        } catch (Exception ignore) {
        }
        RecordStore rs = RecordStore.openRecordStore(RMS_NAME, true);
        byte[] payload = "neutron-rms-ok".getBytes("UTF-8");
        int id = rs.addRecord(payload, 0, payload.length);
        byte[] loaded = rs.getRecord(id);
        rs.closeRecordStore();
        RecordStore.deleteRecordStore(RMS_NAME);
        if (loaded == null || !"neutron-rms-ok".equals(new String(loaded, "UTF-8"))) {
            throw new RuntimeException("RecordStore round-trip mismatch");
        }

        // 5. PushRegistry, NokiaUI, M3G, Micro3D, Media
        PushRegistry.listConnections(false);
        Image nokiaImg = DirectUtils.createImage(8, 8, 0xFF00FF00);
        if (nokiaImg == null || nokiaImg.getWidth() != 8) {
            throw new RuntimeException("NokiaUI DirectUtils failed");
        }
        if (Graphics3D.getInstance() == null) {
            throw new RuntimeException("M3G Graphics3D.getInstance() returned null");
        }
        new Effect3D();
        try {
            Manager.playTone(60, 10, 50);
        } catch (MediaException ignoreOnHeadlessAudio) {
            // Headless CI containers may not have an audio device
        }
    }

    protected void pauseApp() {
    }

    protected void destroyApp(boolean unconditional) {
    }
}
