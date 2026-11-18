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
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import org.neutron.app.util.PlaytimeTracker;

/**
 * Modern, theme-aware bootloader and splash transition for Neutron.
 * 
 * Design Principles:
 *  - 100% Native Theme Adaptive: dynamically derives all backgrounds, text, surfaces,
 *    borders, and accents from UIManager (FlatLaf Dark/Light, Mac, Dracula, System).
 *  - Modern, Clean Minimalist UI: replaces segmented XP-style blocks with a smooth continuous
 *    progress track, elevated card layout, and anti-aliased geometry.
 *  - Clear & Human Status: simplified labels with zero low-level jargon and no ellipsis.
 *  - Total Playtime Counter: displays cumulative time spent on this game in ss mm hh dd mm yy.
 *  - Seamless Cinematic Handoff: smooth cubic cross-fade into gameplay on completion.
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

    private static final long MIN_DISPLAY_DURATION = 800;  // 800ms clean modern transition
    private static final long MAX_SAFETY_TIMEOUT   = 6000; // 6s safety timeout
    private static final long FADE_OUT_DURATION    = 250;  // 250ms smooth cross-fade

    private static volatile float currentAlpha = 0.0f;
    private static volatile Runnable repaintCallback = null;

    public static void setRepaintCallback(Runnable callback) {
        repaintCallback = callback;
    }

    // Game Metadata
    private static volatile String currentGameKey = "";
    private static volatile String gameTitle = "Starting Game";
    private static volatile String gameVendor = "Java™ Application";
    private static volatile String gameVersion = "";
    private static volatile String gameProfile = "MIDP-2.0";
    private static volatile String gamePlaytimeStr = "Never";
    private static volatile Image gameIcon = null;

    /**
     * Start the loading sequence for a given JAR/JAD target.
     */
    public static synchronized void startLoading(final String urlString, String fallbackName) {
        currentState = State.LOADING;
        loadingStartTime = System.currentTimeMillis();
        fadeOutStartTime = 0;
        gameReady = false;
        currentAlpha = 1.0f;
        currentGameKey = urlString != null ? urlString : "";

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
            gameTitle = "Starting Game";
        }
        gameVendor = "Java™ ME Platform";
        gameVersion = "";
        gameProfile = "MIDP-2.0";
        gameIcon = null;

        // Fetch cumulative playtime
        long playtimeSec = PlaytimeTracker.getPlaytimeSeconds(currentGameKey);
        gamePlaytimeStr = PlaytimeTracker.formatPlaytime(playtimeSec);

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
                        try (InputStream is = jar.getInputStream(iconEntry)) {
                            byte[] bytes = is.readAllBytes();
                            ImageIcon imgIcon = new ImageIcon(bytes);
                            if (imgIcon.getIconWidth() > 0) {
                                gameIcon = imgIcon.getImage();
                            }
                        }
                    }
                }
            }
            jar.close();

            // Refresh playtime in case key normalized
            long playtimeSec = PlaytimeTracker.getPlaytimeSeconds(urlString);
            gamePlaytimeStr = PlaytimeTracker.formatPlaytime(playtimeSec);
        } catch (Throwable ignore) {
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
     * Render the theme-adaptive, modern bootloader over the device canvas.
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

        // --- 1. Dynamic Native Theme Resolution (Zero Hardcoded Colors) ---
        Color themeBg = UIManager.getColor("Panel.background");
        if (themeBg == null) {
            themeBg = UIManager.getColor("window");
        }
        if (themeBg == null) {
            themeBg = new Color(24, 28, 36);
        }

        // Check luminance for native dark vs light adaptation
        double luminance = (0.299 * themeBg.getRed() + 0.587 * themeBg.getGreen() + 0.114 * themeBg.getBlue());
        boolean isDark = luminance < 135;

        Color textPrimary = UIManager.getColor("Label.foreground");
        if (textPrimary == null) {
            textPrimary = isDark ? new Color(245, 247, 250) : new Color(30, 41, 59);
        }

        Color textSecondary = UIManager.getColor("Label.disabledForeground");
        if (textSecondary == null) {
            textSecondary = isDark ? new Color(156, 163, 175) : new Color(100, 116, 139);
        }

        Color accentColor = UIManager.getColor("Component.accentColor");
        if (accentColor == null) {
            accentColor = UIManager.getColor("ProgressBar.foreground");
        }
        if (accentColor == null) {
            accentColor = UIManager.getColor("textHighlight");
        }
        if (accentColor == null) {
            accentColor = isDark ? new Color(59, 130, 246) : new Color(37, 99, 235);
        }

        Color compBorder = UIManager.getColor("Component.borderColor");
        Color cardBorder;
        if (compBorder != null) {
            cardBorder = new Color(compBorder.getRed(), compBorder.getGreen(), compBorder.getBlue(), isDark ? 80 : 60);
        } else {
            cardBorder = isDark ? new Color(255, 255, 255, 28) : new Color(0, 0, 0, 24);
        }

        Color pbTrack = UIManager.getColor("ProgressBar.trackColor");
        Color trackColor;
        if (pbTrack != null) {
            trackColor = pbTrack;
        } else {
            trackColor = isDark ? new Color(255, 255, 255, 22) : new Color(0, 0, 0, 18);
        }

        Color cardSurface = isDark
                ? new Color(255, 255, 255, 14)
                : new Color(0, 0, 0, 8);

        Color badgeBg = isDark
                ? new Color(255, 255, 255, 16)
                : new Color(0, 0, 0, 10);

        Font sysFont = UIManager.getFont("Label.font");
        String fontName = sysFont != null ? sysFont.getFamily() : Font.SANS_SERIF;

        // 2. Fill Native Adaptive Background
        g2.setColor(themeBg);
        g2.fillRect(0, 0, width, height);

        // Subtle modern radial vignette
        Color glowColor = isDark
                ? new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 28)
                : new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 16);
        int glowSize = (int) (Math.max(width, height) * 0.95f);
        java.awt.RadialGradientPaint glowPaint = new java.awt.RadialGradientPaint(
                width / 2.0f, height * 0.42f, glowSize / 2.0f,
                new float[]{ 0.0f, 1.0f },
                new Color[]{ glowColor, new Color(themeBg.getRed(), themeBg.getGreen(), themeBg.getBlue(), 0) }
        );
        g2.setPaint(glowPaint);
        g2.fillRect(0, 0, width, height);

        int scaleRef = Math.min(width, height);
        int centerX = width / 2;
        int centerY = (int) (height * 0.35f);

        // 3. Elevated Modern Icon Card
        int iconSize = scaleRef >= 320 ? 64 : (scaleRef >= 220 ? 52 : 40);
        int iconBoxSize = iconSize + 16;
        int iconBoxX = centerX - iconBoxSize / 2;
        int iconBoxY = centerY - iconBoxSize / 2;

        // Soft drop shadow
        g2.setColor(isDark ? new Color(0, 0, 0, 70) : new Color(0, 0, 0, 25));
        g2.fill(new RoundRectangle2D.Float(iconBoxX, iconBoxY + 3, iconBoxSize, iconBoxSize, 20, 20));

        // Surface Card
        g2.setColor(cardSurface);
        g2.fill(new RoundRectangle2D.Float(iconBoxX, iconBoxY, iconBoxSize, iconBoxSize, 20, 20));
        g2.setColor(cardBorder);
        g2.draw(new RoundRectangle2D.Float(iconBoxX, iconBoxY, iconBoxSize, iconBoxSize, 20, 20));

        if (gameIcon != null) {
            g2.drawImage(gameIcon, iconBoxX + 8, iconBoxY + 8, iconSize, iconSize, null);
        } else {
            // Modern Minimalist Controller Glyph
            g2.setColor(accentColor);
            int gw = iconSize / 2;
            int gh = iconSize / 3;
            int gx = centerX - gw / 2;
            int gy = centerY - gh / 2;
            g2.drawRoundRect(gx, gy, gw, gh, 8, 8);
            g2.drawLine(gx + gw / 4, gy + gh / 2, gx + gw / 4 + 4, gy + gh / 2);
            g2.fillOval(gx + gw - gw / 4 - 2, gy + gh / 2 - 2, 4, 4);
        }

        // 4. Game Title & Subtitle Typography
        int titleFontSize = Math.max(13, Math.min(19, scaleRef / 15));
        Font titleFont = new Font(fontName, Font.BOLD, titleFontSize);
        g2.setFont(titleFont);
        FontMetrics fmTitle = g2.getFontMetrics();

        int textY = iconBoxY + iconBoxSize + fmTitle.getAscent() + (scaleRef >= 240 ? 14 : 10);

        String displayTitle = gameTitle;
        if (fmTitle.stringWidth(displayTitle) > width - 28) {
            while (displayTitle.length() > 3 && fmTitle.stringWidth(displayTitle + "…") > width - 28) {
                displayTitle = displayTitle.substring(0, displayTitle.length() - 1);
            }
            displayTitle = displayTitle + "…";
        }

        g2.setColor(textPrimary);
        g2.drawString(displayTitle, centerX - fmTitle.stringWidth(displayTitle) / 2, textY);

        // Subtitle (Vendor • Version)
        int subFontSize = Math.max(10, Math.min(12, titleFontSize - 3));
        Font subFont = new Font(fontName, Font.PLAIN, subFontSize);
        g2.setFont(subFont);
        FontMetrics fmSub = g2.getFontMetrics();

        textY += fmSub.getHeight() + 3;
        String subText = gameVendor + (gameVersion.isEmpty() ? "" : " • " + gameVersion);
        g2.setColor(textSecondary);
        g2.drawString(subText, centerX - fmSub.stringWidth(subText) / 2, textY);

        // 5. Total Playtime Counter (ss mm hh dd mm yy)
        int timeFontSize = Math.max(9, Math.min(11, subFontSize));
        Font timeFont = new Font(fontName, Font.PLAIN, timeFontSize);
        g2.setFont(timeFont);
        FontMetrics fmTime = g2.getFontMetrics();

        String playtimeLabel = "Played: " + gamePlaytimeStr;
        int badgePaddingH = 10;
        int badgePaddingV = 4;
        int badgeW = fmTime.stringWidth(playtimeLabel) + badgePaddingH * 2;
        int badgeH = fmTime.getHeight() + badgePaddingV * 2;
        int badgeX = centerX - badgeW / 2;
        int badgeY = textY + 8;

        g2.setColor(badgeBg);
        g2.fill(new RoundRectangle2D.Float(badgeX, badgeY, badgeW, badgeH, 12, 12));
        g2.setColor(cardBorder);
        g2.draw(new RoundRectangle2D.Float(badgeX, badgeY, badgeW, badgeH, 12, 12));

        g2.setColor(textSecondary);
        g2.drawString(playtimeLabel, badgeX + badgePaddingH, badgeY + badgePaddingV + fmTime.getAscent());

        // 6. Modern Continuous Progress Pill (No XP Segments)
        long now = System.currentTimeMillis();
        long elapsed = now - loadingStartTime;
        float progress = Math.min(1.0f, (float) elapsed / MIN_DISPLAY_DURATION);

        int barWidth = Math.min(240, (int) (width * 0.70f));
        int barHeight = Math.max(4, scaleRef >= 240 ? 6 : 4);
        int barX = centerX - barWidth / 2;
        int barY = (int) (height * 0.76f);

        // Track background
        g2.setColor(trackColor);
        g2.fill(new RoundRectangle2D.Float(barX, barY, barWidth, barHeight, barHeight, barHeight));

        // Smooth active fill with modern theme accent
        int fillWidth = Math.max(barHeight, (int) (barWidth * progress));
        g2.setColor(accentColor);
        g2.fill(new RoundRectangle2D.Float(barX, barY, fillWidth, barHeight, barHeight, barHeight));

        // 7. Simplified Human Status (No Jargon, No Ellipsis)
        int statusFontSize = Math.max(10, Math.min(12, subFontSize));
        Font statusFont = new Font(fontName, Font.PLAIN, statusFontSize);
        g2.setFont(statusFont);
        FontMetrics fmStatus = g2.getFontMetrics();

        String statusMsg;
        if (progress < 0.40f) {
            statusMsg = "Loading Game";
        } else if (progress < 0.75f) {
            statusMsg = "Preparing Assets";
        } else if (progress < 0.95f) {
            statusMsg = "Starting Game";
        } else {
            statusMsg = "Ready";
        }

        int statusY = barY + barHeight + fmStatus.getAscent() + 10;
        g2.setColor(textSecondary);
        g2.drawString(statusMsg, centerX - fmStatus.stringWidth(statusMsg) / 2, statusY);

        g2.setComposite(oldComp);
        g2.dispose();
    }
}
