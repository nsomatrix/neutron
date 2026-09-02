/**
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */
package org.neutron.app.ui.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import javax.swing.UIManager;

/**
 * SwingSleepUI provides a smooth, professional pause experience.
 * It renders a light, crisp blurred game backdrop with smooth alpha fade-in & fade-out transitions.
 */
public class SwingSleepUI {

    public enum State {
        INACTIVE,
        ENTERING,
        ACTIVE,
        EXITING
    }

    private static State currentState = State.INACTIVE;
    private static long transitionStartTime = 0;
    private static final long FADE_IN_DURATION = 250;  // 250ms smooth fade-in
    private static final long FADE_OUT_DURATION = 200; // 200ms smooth fade-out
    private static float currentAlpha = 0.0f;

    private static BufferedImage cachedSnapshot = null;
    private static BufferedImage cachedBlurredSnapshot = null;

    public static boolean isWakeUpClicked(int clickX, int clickY, int width, int height) {
        return true;
    }

    public static synchronized void captureSnapshot(BufferedImage surfaceImage) {
        if (surfaceImage == null) return;
        
        int w = surfaceImage.getWidth();
        int h = surfaceImage.getHeight();
        if (w <= 0 || h <= 0) return;

        if (cachedSnapshot == null || cachedSnapshot.getWidth() != w || cachedSnapshot.getHeight() != h) {
            cachedSnapshot = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        }

        Graphics2D g2 = cachedSnapshot.createGraphics();
        g2.drawImage(surfaceImage, 0, 0, null);
        g2.dispose();

        cachedBlurredSnapshot = generateFastBlur(cachedSnapshot);
    }

    public static boolean hasSnapshot() {
        return cachedSnapshot != null;
    }

    public static synchronized void startSleep() {
        if (currentState != State.ACTIVE && currentState != State.ENTERING) {
            currentState = State.ENTERING;
            transitionStartTime = System.currentTimeMillis();
            currentAlpha = 0.0f;
        }
    }

    public static synchronized void requestWakeUp() {
        if (currentState == State.ACTIVE || currentState == State.ENTERING) {
            currentState = State.EXITING;
            transitionStartTime = System.currentTimeMillis();
        } else if (currentState == State.INACTIVE) {
            org.neutron.app.util.SleepManager.setSleepModeActive(false);
            resetSleepState();
        }
    }

    public static synchronized void resetSleepState() {
        currentState = State.INACTIVE;
        transitionStartTime = 0;
        currentAlpha = 0.0f;
        cachedSnapshot = null;
        cachedBlurredSnapshot = null;
    }

    public static boolean isTransitioning() {
        return currentState != State.INACTIVE;
    }

    public static float getCurrentAlpha() {
        return currentAlpha;
    }

    private static BufferedImage generateFastBlur(BufferedImage src) {
        if (src == null) return null;
        int w = src.getWidth();
        int h = src.getHeight();
        if (w <= 0 || h <= 0) return null;

        // Proportional 2x downscale preserving exact aspect ratio (no hardcoded dimension bounds)
        int smallW = Math.max(1, w / 2);
        int smallH = Math.max(1, h / 2);

        BufferedImage smallImg = new BufferedImage(smallW, smallH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gSmall = smallImg.createGraphics();
        gSmall.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        gSmall.drawImage(src, 0, 0, smallW, smallH, null);
        gSmall.dispose();

        int[] inPixels = ((java.awt.image.DataBufferInt) smallImg.getRaster().getDataBuffer()).getData();
        int[] outPixels = new int[smallW * smallH];

        // Separable 2-Pass Box Blur with Edge Clamping (Industry Standard: Edge-to-Edge with 0 border gap)
        int radius = 3;
        boxBlurHorizontal(inPixels, outPixels, smallW, smallH, radius);
        boxBlurVertical(outPixels, inPixels, smallW, smallH, radius);

        return smallImg;
    }

    private static void boxBlurHorizontal(int[] in, int[] out, int w, int h, int r) {
        int windowSize = 2 * r + 1;
        for (int y = 0; y < h; y++) {
            int rowOffset = y * w;
            int aSum = 0, rSum = 0, gSum = 0, bSum = 0;

            for (int i = -r; i <= r; i++) {
                int clampedX = Math.min(Math.max(i, 0), w - 1);
                int pixel = in[rowOffset + clampedX];
                aSum += (pixel >>> 24);
                rSum += (pixel >> 16) & 0xff;
                gSum += (pixel >> 8) & 0xff;
                bSum += pixel & 0xff;
            }

            for (int x = 0; x < w; x++) {
                out[rowOffset + x] = ((aSum / windowSize) << 24)
                                   | ((rSum / windowSize) << 16)
                                   | ((gSum / windowSize) << 8)
                                   | (bSum / windowSize);

                int leftClampedX = Math.min(Math.max(x - r, 0), w - 1);
                int rightClampedX = Math.min(Math.max(x + r + 1, 0), w - 1);

                int leftPixel = in[rowOffset + leftClampedX];
                int rightPixel = in[rowOffset + rightClampedX];

                aSum += (rightPixel >>> 24) - (leftPixel >>> 24);
                rSum += ((rightPixel >> 16) & 0xff) - ((leftPixel >> 16) & 0xff);
                gSum += ((rightPixel >> 8) & 0xff) - ((leftPixel >> 8) & 0xff);
                bSum += (rightPixel & 0xff) - (leftPixel & 0xff);
            }
        }
    }

    private static void boxBlurVertical(int[] in, int[] out, int w, int h, int r) {
        int windowSize = 2 * r + 1;
        for (int x = 0; x < w; x++) {
            int aSum = 0, rSum = 0, gSum = 0, bSum = 0;

            for (int i = -r; i <= r; i++) {
                int clampedY = Math.min(Math.max(i, 0), h - 1);
                int pixel = in[clampedY * w + x];
                aSum += (pixel >>> 24);
                rSum += (pixel >> 16) & 0xff;
                gSum += (pixel >> 8) & 0xff;
                bSum += pixel & 0xff;
            }

            for (int y = 0; y < h; y++) {
                out[y * w + x] = ((aSum / windowSize) << 24)
                                 | ((rSum / windowSize) << 16)
                                 | ((gSum / windowSize) << 8)
                                 | (bSum / windowSize);

                int topClampedY = Math.min(Math.max(y - r, 0), h - 1);
                int bottomClampedY = Math.min(Math.max(y + r + 1, 0), h - 1);

                int topPixel = in[topClampedY * w + x];
                int bottomPixel = in[bottomClampedY * w + x];

                aSum += (bottomPixel >>> 24) - (topPixel >>> 24);
                rSum += ((bottomPixel >> 16) & 0xff) - ((topPixel >> 16) & 0xff);
                gSum += ((bottomPixel >> 8) & 0xff) - ((topPixel >> 8) & 0xff);
                bSum += (bottomPixel & 0xff) - (topPixel & 0xff);
            }
        }
    }

    private static float easeOutCubic(float t) {
        float f = 1.0f - t;
        return 1.0f - f * f * f;
    }

    private static void updateTransition() {
        if (currentState == State.INACTIVE) {
            currentAlpha = 0.0f;
            return;
        }

        long now = System.currentTimeMillis();
        long elapsed = now - transitionStartTime;

        if (currentState == State.ENTERING) {
            float progress = Math.min(1.0f, (float) elapsed / FADE_IN_DURATION);
            currentAlpha = easeOutCubic(progress);
            if (progress >= 1.0f) {
                currentState = State.ACTIVE;
                currentAlpha = 1.0f;
            }
        } else if (currentState == State.EXITING) {
            float progress = Math.min(1.0f, (float) elapsed / FADE_OUT_DURATION);
            currentAlpha = easeOutCubic(1.0f - progress);
            if (progress >= 1.0f || currentAlpha <= 0.01f) {
                currentAlpha = 0.0f;
                currentState = State.INACTIVE;
                org.neutron.app.util.SleepManager.setSleepModeActive(false);
                resetSleepState();
            }
        }
    }

    public static void paintScreensaver(Graphics g, int width, int height) {
        updateTransition();

        if (currentAlpha <= 0.001f) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Composite oldComp = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentAlpha));

        // 1. Draw Crisp Light Blurred Game Snapshot
        if (cachedBlurredSnapshot != null) {
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2.drawImage(cachedBlurredSnapshot, 0, 0, width, height, null);
        } else {
            Color panelBg = UIManager.getColor("Panel.background");
            if (panelBg == null) panelBg = new Color(20, 24, 32);
            g2.setColor(panelBg);
            g2.fillRect(0, 0, width, height);
        }

        // 2. Ultra-Light Dark Tint Layer (keeps game frame vibrant & clear)
        g2.setColor(new Color(0, 0, 0, 45));
        g2.fillRect(0, 0, width, height);

        // 3. Soft Radial Vignette Overlay
        Point2D center = new Point2D.Float(width / 2.0f, height / 2.0f);
        float radius = Math.max(width, height) * 0.75f;
        float[] dist = {0.0f, 0.65f, 1.0f};
        Color[] colors = {
            new Color(5, 8, 15, 20),   // Center clear
            new Color(4, 6, 12, 80),   // Mid soft shade
            new Color(2, 3, 6, 150)    // Edge gentle vignette
        };
        RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
        g2.setPaint(p);
        g2.fillRect(0, 0, width, height);

        g2.setComposite(oldComp);
    }
}
