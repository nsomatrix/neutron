/**
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */
package org.neutron.app.ui.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.swing.ImageIcon;

/**
 * SwingLoadingUI provides an industry-standard, authentic retro-modern bootloader
 * for J2ME games in Neutron.
 * 
 * Features:
 *  - Retro LCD matrix backdrop with subtle scanline lattice & radial backlight glow.
 *  - Dynamic game identity: extracted icon, bold title, vendor/version, and profile tags.
 *  - Authentic Nokia / Sony Ericsson style segmented progress bar.
 *  - Cycling application status messages.
 *  - Cinematic cross-fade handoff into the active game canvas.
 *  - Immediate skip on user interaction (key or mouse press).
 */
public class SwingLoadingUI {

    public enum State {
        INACTIVE,
        LOADING,
        FADING_OUT
    }

    private static volatile State currentState = State.INACTIVE;
    private static volatile long loadingStartTime = 0;
    private static volatile long fadeOutStartTime = 0;
    private static volatile boolean gameReady = false;

    private static final long MIN_DISPLAY_DURATION = 900;  // 900ms smooth retro boot feel
    private static final long MAX_SAFETY_TIMEOUT   = 6000; // 6s safety timeout
    private static final long FADE_OUT_DURATION    = 300;  // 300ms smooth cross-fade

    private static volatile float currentAlpha = 0.0f;
    private static volatile Runnable repaintCallback = null;

    public static void setRepaintCallback(Runnable callback) {
        repaintCallback = callback;
    }

    // Game Metadata
    private static volatile String gameTitle = "Loading...";
    private static volatile String gameVendor = "Java™ Application";
    private static volatile String gameVersion = "";
    private static volatile String gameProfile = "MIDP-2.0";
    private static volatile Image gameIcon = null;

    // Palette: Retro LCD Dark Theme with Nokia Cyan / Emerald Accent
    private static final Color BG_DARK          = new Color(11, 15, 20);
    private static final Color BG_LIGHTER       = new Color(18, 24, 32);
    private static final Color ACCENT_CYAN      = new Color(0, 210, 211);
    private static final Color ACCENT_EMERALD   = new Color(46, 204, 113);
    private static final Color TEXT_PRIMARY     = new Color(240, 246, 252);
    private static final Color TEXT_SECONDARY   = new Color(139, 148, 158);
    private static final Color BORDER_COLOR     = new Color(48, 54, 61);
    private static final Color TRACK_BG         = new Color(22, 27, 34);

    /**
     * Start the loading sequence for a given JAR/JAD target.
     */
    public static synchronized void startLoading(final String urlString, String fallbackName) {
        currentState = State.LOADING;
        loadingStartTime = System.currentTimeMillis();
        fadeOutStartTime = 0;
        gameReady = false;
        currentAlpha = 1.0f;

        if (repaintCallback != null) {
            try {
                repaintCallback.run();
            } catch (Throwable ignore) {
            }
        }

        // Clean initial defaults
        if (fallbackName != null && !fallbackName.trim().isEmpty()) {
            gameTitle = fallbackName.trim();
        } else if (urlString != null) {
            String name = new File(urlString).getName();
            if (name.toLowerCase().endsWith(".jar") || name.toLowerCase().endsWith(".jad")) {
                name = name.substring(0, name.length() - 4);
            }
            gameTitle = name;
        } else {
            gameTitle = "Starting Application";
        }
        gameVendor = "Java™ ME Platform";
        gameVersion = "";
        gameProfile = "MIDP-2.0";
        gameIcon = null;

        // Asynchronously extract precise metadata & icon from archive
        if (urlString != null) {
            new Thread("Neutron-LoaderMetadataScanner") {
                public void run() {
                    scanArchiveMetadata(urlString);
                }
            }.start();
        }
    }

    private static void scanArchiveMetadata(String urlString) {
        try {
            File file = null;
            if (urlString.startsWith("file:")) {
                file = new File(new URL(urlString).toURI());
            } else {
                file = new File(urlString);
            }

            if (!file.exists() || !file.isFile() || !file.getName().toLowerCase().endsWith(".jar")) {
                return;
            }

            JarFile jar = new JarFile(file);
            Manifest manifest = jar.getManifest();
            if (manifest != null) {
                Attributes attr = manifest.getMainAttributes();
                String name = attr.getValue("MIDlet-Name");
                String vendor = attr.getValue("MIDlet-Vendor");
                String version = attr.getValue("MIDlet-Version");
                String profile = attr.getValue("MicroEdition-Profile");
                String config = attr.getValue("MicroEdition-Configuration");
                String iconPath = attr.getValue("MIDlet-Icon");

                // If not in root attributes, check MIDlet-1
                if (name == null || iconPath == null) {
                    String midlet1 = attr.getValue("MIDlet-1");
                    if (midlet1 != null) {
                        String[] parts = midlet1.split(",");
                        if (parts.length >= 1 && name == null) {
                            name = parts[0].trim();
                        }
                        if (parts.length >= 2 && iconPath == null) {
                            iconPath = parts[1].trim();
                        }
                    }
                }

                if (name != null && !name.trim().isEmpty()) {
                    gameTitle = name.trim();
                }
                if (vendor != null && !vendor.trim().isEmpty()) {
                    gameVendor = vendor.trim();
                }
                if (version != null && !version.trim().isEmpty()) {
                    gameVersion = "v" + version.trim();
                }

                StringBuilder sb = new StringBuilder();
                if (config != null) sb.append(config.trim());
                if (profile != null) {
                    if (sb.length() > 0) sb.append(" • ");
                    sb.append(profile.trim());
                }
                if (sb.length() > 0) {
                    gameProfile = sb.toString();
                }

                // Load Icon
                if (iconPath != null && !iconPath.trim().isEmpty()) {
                    String cleanPath = iconPath.trim();
                    if (cleanPath.startsWith("/")) cleanPath = cleanPath.substring(1);
                    JarEntry iconEntry = jar.getJarEntry(cleanPath);
                    if (iconEntry != null) {
                        InputStream is = jar.getInputStream(iconEntry);
                        byte[] bytes = is.readAllBytes();
                        is.close();
                        ImageIcon imgIcon = new ImageIcon(bytes);
                        if (imgIcon.getIconWidth() > 0) {
                            gameIcon = imgIcon.getImage();
                        }
                    }
                }
            }
            jar.close();
        } catch (Throwable ignore) {
            // Non-critical, fallback defaults will remain
        }
    }

    /**
     * Called when the game engine finishes startApp() and is ready to paint.
     */
    public static synchronized void onGameStarted() {
        gameReady = true;
    }

    /**
     * Instantly skip loading on user interaction.
     */
    public static synchronized void requestSkip() {
        if (currentState == State.LOADING) {
            fadeOutStartTime = System.currentTimeMillis();
            currentState = State.FADING_OUT;
        } else if (currentState == State.FADING_OUT) {
            currentState = State.INACTIVE;
            currentAlpha = 0.0f;
        }
    }

    /**
     * Reset/dismiss the loader immediately.
     */
    public static synchronized void dismiss() {
        currentState = State.INACTIVE;
        currentAlpha = 0.0f;
        gameReady = false;
        gameIcon = null;
    }

    public static boolean isDisplaying() {
        return currentState != State.INACTIVE;
    }

    public static float getCurrentAlpha() {
        return currentAlpha;
    }

    private static float easeOutCubic(float t) {
        float f = 1.0f - t;
        return 1.0f - f * f * f;
    }

    /**
     * Update state transition and alpha over time.
     */
    private static void updateTiming() {
        if (currentState == State.INACTIVE) {
            currentAlpha = 0.0f;
            return;
        }

        long now = System.currentTimeMillis();
        long elapsed = now - loadingStartTime;

        if (currentState == State.LOADING) {
            currentAlpha = 1.0f;

        if (repaintCallback != null) {
            try {
                repaintCallback.run();
            } catch (Throwable ignore) {
            }
        }
            // Transition out once game is ready and minimum display time elapsed (or safety timeout reached)
            if ((gameReady && elapsed >= MIN_DISPLAY_DURATION) || elapsed >= MAX_SAFETY_TIMEOUT) {
                currentState = State.FADING_OUT;
                fadeOutStartTime = now;
            }
        } else if (currentState == State.FADING_OUT) {
            long fadeElapsed = now - fadeOutStartTime;
            float progress = Math.min(1.0f, (float) fadeElapsed / FADE_OUT_DURATION);
            currentAlpha = 1.0f - easeOutCubic(progress);
            if (progress >= 1.0f) {
                currentState = State.INACTIVE;
                currentAlpha = 0.0f;
            }
        }
    }

    /**
     * Render the retro-modern bootloader over the device canvas.
     */
    public static void paintLoader(Graphics g, int width, int height) {
        if (currentState == State.INACTIVE || width <= 0 || height <= 0) {
            return;
        }

        updateTiming();

        if (currentAlpha <= 0.001f) {
            return;
        }

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        Composite oldComp = g2.getComposite();
        if (currentAlpha < 0.999f) {
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, currentAlpha));
        }

        // 1. Draw Deep LCD Vignette Background
        Point2D center = new Point2D.Float(width / 2.0f, height * 0.45f);
        float radius = Math.max(width, height) * 0.75f;
        float[] dist = { 0.0f, 0.65f, 1.0f };
        Color[] colors = { BG_LIGHTER, BG_DARK, new Color(6, 8, 12) };
        RadialGradientPaint p = new RadialGradientPaint(center, radius, dist, colors);
        g2.setPaint(p);
        g2.fillRect(0, 0, width, height);

        // 2. Authentic LCD Scanline / Subpixel Lattice
        g2.setColor(new Color(255, 255, 255, 7));
        for (int y = 0; y < height; y += 3) {
            g2.drawLine(0, y, width, y);
        }

        int scaleRef = Math.min(width, height);
        int iconSize = scaleRef >= 320 ? 64 : (scaleRef >= 200 ? 48 : 36);
        int centerX = width / 2;
        int centerY = (int) (height * 0.38f);

        // 3. Render Game Icon with Beveled Glow Box
        int iconBoxX = centerX - iconSize / 2;
        int iconBoxY = centerY - iconSize / 2;

        // Icon Bezel & Drop Shadow
        g2.setColor(new Color(0, 0, 0, 80));
        g2.fill(new RoundRectangle2D.Float(iconBoxX + 2, iconBoxY + 3, iconSize, iconSize, 14, 14));

        g2.setColor(TRACK_BG);
        g2.fill(new RoundRectangle2D.Float(iconBoxX, iconBoxY, iconSize, iconSize, 14, 14));

        g2.setColor(BORDER_COLOR);
        g2.draw(new RoundRectangle2D.Float(iconBoxX, iconBoxY, iconSize, iconSize, 14, 14));

        if (gameIcon != null) {
            g2.drawImage(gameIcon, iconBoxX + 4, iconBoxY + 4, iconSize - 8, iconSize - 8, null);
        } else {
            // Retro Java Coffee Cup Placeholder
            g2.setColor(ACCENT_CYAN);
            int cupW = iconSize / 2;
            int cupH = iconSize / 3;
            int cupX = centerX - cupW / 2;
            int cupY = centerY - cupH / 4;
            g2.drawRoundRect(cupX, cupY, cupW, cupH, 6, 6);
            g2.drawArc(cupX + cupW - 2, cupY + 2, cupW / 3, cupH - 4, 270, 180);
            g2.drawLine(cupX + 2, cupY + cupH + 3, cupX + cupW - 2, cupY + cupH + 3);
        }

        // 4. Render Game Title & Subtitles
        int titleFontSize = Math.max(12, Math.min(18, scaleRef / 16));
        Font titleFont = new Font(Font.SANS_SERIF, Font.BOLD, titleFontSize);
        g2.setFont(titleFont);
        FontMetrics fmTitle = g2.getFontMetrics();

        int textY = iconBoxY + iconSize + fmTitle.getAscent() + (scaleRef >= 240 ? 16 : 8);

        // Clamped display title
        String displayTitle = gameTitle;
        if (fmTitle.stringWidth(displayTitle) > width - 24) {
            while (displayTitle.length() > 4 && fmTitle.stringWidth(displayTitle + "...") > width - 24) {
                displayTitle = displayTitle.substring(0, displayTitle.length() - 1);
            }
            displayTitle = displayTitle + "...";
        }

        g2.setColor(TEXT_PRIMARY);
        g2.drawString(displayTitle, centerX - fmTitle.stringWidth(displayTitle) / 2, textY);

        // Vendor & Version Subtitle
        int subFontSize = Math.max(10, Math.min(12, titleFontSize - 3));
        Font subFont = new Font(Font.SANS_SERIF, Font.PLAIN, subFontSize);
        g2.setFont(subFont);
        FontMetrics fmSub = g2.getFontMetrics();

        textY += fmSub.getHeight() + 2;
        String subText = gameVendor + (gameVersion.isEmpty() ? "" : " • " + gameVersion);
        g2.setColor(TEXT_SECONDARY);
        g2.drawString(subText, centerX - fmSub.stringWidth(subText) / 2, textY);

        // 5. Authentic Retro Segmented Progress Bar
        long now = System.currentTimeMillis();
        long elapsed = now - loadingStartTime;
        float progress = Math.min(1.0f, (float) elapsed / MIN_DISPLAY_DURATION);

        int barWidth = Math.min(220, (int) (width * 0.72f));
        int barHeight = Math.max(8, scaleRef >= 240 ? 12 : 8);
        int barX = centerX - barWidth / 2;
        int barY = (int) (height * 0.72f);

        // Bezel Track
        g2.setColor(TRACK_BG);
        g2.fillRoundRect(barX, barY, barWidth, barHeight, 4, 4);
        g2.setColor(BORDER_COLOR);
        g2.drawRoundRect(barX, barY, barWidth, barHeight, 4, 4);

        // 10 Segments (Iconic Nokia / Sony Ericsson look)
        int totalSegments = 10;
        int activeSegments = (int) Math.floor(progress * totalSegments);
        int segSpacing = 2;
        int segWidth = (barWidth - 4 - (totalSegments - 1) * segSpacing) / totalSegments;

        for (int i = 0; i < totalSegments; i++) {
            int segX = barX + 2 + i * (segWidth + segSpacing);
            int segY = barY + 2;
            int segH = barHeight - 4;

            if (i < activeSegments) {
                // Gradient accent block
                float segRatio = (float) i / (float) totalSegments;
                int r = (int) (ACCENT_CYAN.getRed() * (1.0f - segRatio) + ACCENT_EMERALD.getRed() * segRatio);
                int gr = (int) (ACCENT_CYAN.getGreen() * (1.0f - segRatio) + ACCENT_EMERALD.getGreen() * segRatio);
                int b = (int) (ACCENT_CYAN.getBlue() * (1.0f - segRatio) + ACCENT_EMERALD.getBlue() * segRatio);
                g2.setColor(new Color(r, gr, b));
                g2.fillRect(segX, segY, segWidth, segH);
            } else {
                // Dim unlit segment
                g2.setColor(new Color(255, 255, 255, 12));
                g2.fillRect(segX, segY, segWidth, segH);
            }
        }

        // 6. Cycling Retro Status Label
        int statusFontSize = Math.max(9, Math.min(11, subFontSize - 1));
        Font statusFont = new Font(Font.MONOSPACED, Font.PLAIN, statusFontSize);
        g2.setFont(statusFont);
        FontMetrics fmStatus = g2.getFontMetrics();

        String statusMsg;
        if (progress < 0.35f) {
            statusMsg = "Reading Archive & Manifest...";
        } else if (progress < 0.65f) {
            statusMsg = "Verifying CLDC / MIDP Bytecode...";
        } else if (progress < 0.90f) {
            statusMsg = "Initializing Graphics Engine...";
        } else {
            statusMsg = "Starting Java™ Application...";
        }

        int statusY = barY + barHeight + fmStatus.getAscent() + 8;
        g2.setColor(new Color(88, 166, 255));
        g2.drawString(statusMsg, centerX - fmStatus.stringWidth(statusMsg) / 2, statusY);

        // 7. Subtle Feature Pill / Tag at Bottom
        if (height >= 260) {
            Font pillFont = new Font(Font.SANS_SERIF, Font.PLAIN, Math.max(8, statusFontSize - 1));
            g2.setFont(pillFont);
            FontMetrics fmPill = g2.getFontMetrics();
            int pillY = height - 12;
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawString(gameProfile, centerX - fmPill.stringWidth(gameProfile) / 2, pillY);
        }

        g2.setComposite(oldComp);
        g2.dispose();
    }
}
