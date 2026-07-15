/**
 *  Neutron
 *
 *  Licensed under LGPL 2.1 or Apache 2.0.
 */
package org.neutron.app.ui.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.UIManager;

/**
 * SwingSleepUI provides a clean, theme-aware app-based lock screen matching
 * the active FlatLaf / Look and Feel theme colors. It renders a beautiful, 
 * dynamic orbital Neutron icon, displays floating ambient light blobs in the background,
 * and wakes on a simple tap anywhere on the screen.
 */
public class SwingSleepUI {
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM dd");

    private static final int containerWidth = 290;
    private static final int containerHeight = 240;

    public static boolean isWakeUpClicked(int clickX, int clickY, int width, int height) {
        // Any click on the screen wakes up the emulator
        return true;
    }

    public static void paintScreensaver(Graphics g, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Fetch theme colors dynamically from UIManager
        Color panelBg = UIManager.getColor("Panel.background");
        if (panelBg == null) panelBg = new Color(240, 240, 240);
        
        Color textColor = UIManager.getColor("Label.foreground");
        if (textColor == null) textColor = Color.BLACK;

        Color subTextColor = UIManager.getColor("Label.disabledForeground");
        if (subTextColor == null) subTextColor = Color.GRAY;

        Color borderCol = UIManager.getColor("Component.borderColor");
        if (borderCol == null) borderCol = new Color(200, 200, 200);

        Color accentColor = UIManager.getColor("Component.focusColor");
        if (accentColor == null) accentColor = UIManager.getColor("Button.default.background");
        if (accentColor == null) accentColor = new Color(0, 120, 215); // Clean modern blue

        // 1. Draw Solid Theme Background
        g2.setColor(panelBg);
        g2.fillRect(0, 0, width, height);

        // 2. Draw Floating Ambient Glowing Particles
        long now = System.currentTimeMillis();
        int[] sizes = { 100, 140, 110, 160, 95, 120 };
        double[] speeds = { 0.04, 0.02, 0.035, 0.015, 0.05, 0.025 };
        double[] phases = { 0.0, 1.5, 3.1, 4.5, 2.1, 0.8 };
        
        for (int i = 0; i < 6; i++) {
            double t = (now / 1000.0) * speeds[i];
            int px = (int) (width * (0.5 + 0.45 * Math.sin(t + phases[i])));
            int py = (int) (height * (0.5 + 0.48 * Math.cos(t * 0.8 + phases[i])));
            
            // Soft outer glow matching the active theme's accent color
            g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 12));
            g2.fillOval(px - sizes[i] / 2, py - sizes[i] / 2, sizes[i], sizes[i]);
            
            // Subtle core highlight
            g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 6));
            g2.fillOval(px - sizes[i] / 4, py - sizes[i] / 4, sizes[i] / 2, sizes[i] / 2);
        }

        // 3. Central Lock Card
        int cx = (width - containerWidth) / 2;
        int cy = (height - containerHeight) / 2;

        // Card Background (slightly different from panel bg for contrast)
        Color cardBg = UIManager.getColor("TextArea.background");
        if (cardBg == null) cardBg = UIManager.getColor("EditorPane.background");
        if (cardBg == null) cardBg = Color.WHITE;
        
        g2.setColor(cardBg);
        g2.fillRoundRect(cx, cy, containerWidth, containerHeight, 12, 12);

        // Card Border
        g2.setColor(borderCol);
        g2.setStroke(new BasicStroke(1.0f));
        g2.drawRoundRect(cx, cy, containerWidth, containerHeight, 12, 12);

        // 4. Digital Clock at the top
        g2.setFont(new Font("SansSerif", Font.BOLD, 26));
        g2.setColor(textColor);
        String timeStr = timeFormat.format(new Date());
        FontMetrics fmTime = g2.getFontMetrics();
        g2.drawString(timeStr, cx + (containerWidth - fmTime.stringWidth(timeStr)) / 2, cy + 35);

        // 5. Date Subtitle
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        g2.setColor(subTextColor);
        String dateStr = dateFormat.format(new Date());
        FontMetrics fmDate = g2.getFontMetrics();
        g2.drawString(dateStr, cx + (containerWidth - fmDate.stringWidth(dateStr)) / 2, cy + 54);

        // 6. Animated Neutron Icon in the center
        int lockX = cx + containerWidth / 2;
        int lockY = cy + 115;
        double animFrame = (System.currentTimeMillis() % 100000) * 0.003;

        // Outer orbit ring
        g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 40));
        g2.drawOval(lockX - 45, lockY - 45, 90, 90);

        // Two intersecting elliptical orbits
        g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 100));
        g2.drawOval(lockX - 50, lockY - 22, 100, 44);
        g2.drawOval(lockX - 22, lockY - 50, 44, 100);

        // Pulsating nucleus core
        double pulse = Math.sin(animFrame * 2.0);
        int pulseOffset = (int) (pulse * 3.0);
        g2.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 60));
        g2.fillOval(lockX - 18 - pulseOffset / 2, lockY - 18 - pulseOffset / 2, 36 + pulseOffset, 36 + pulseOffset);

        g2.setColor(accentColor);
        g2.fillOval(lockX - 11, lockY - 11, 22, 22);

        g2.setColor(textColor);
        g2.fillOval(lockX - 4, lockY - 4, 8, 8);

        // Orbiting electrons
        g2.setColor(textColor);
        // Horizontal orbit
        int hx1 = lockX + (int) (50 * Math.cos(animFrame));
        int hy1 = lockY + (int) (22 * Math.sin(animFrame));
        int hx2 = lockX + (int) (50 * Math.cos(animFrame + Math.PI));
        int hy2 = lockY + (int) (22 * Math.sin(animFrame + Math.PI));
        g2.fillOval(hx1 - 3, hy1 - 3, 6, 6);
        g2.fillOval(hx2 - 3, hy2 - 3, 6, 6);

        // Vertical orbit (reverse direction)
        int vx1 = lockX + (int) (22 * Math.cos(-animFrame));
        int vy1 = lockY + (int) (50 * Math.sin(-animFrame));
        int vx2 = lockX + (int) (22 * Math.cos(-animFrame + Math.PI));
        int vy2 = lockY + (int) (50 * Math.sin(-animFrame + Math.PI));
        g2.fillOval(vx1 - 3, vy1 - 3, 6, 6);
        g2.fillOval(vx2 - 3, vy2 - 3, 6, 6);

        // 7. Dynamic Stats Section
        g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
        g2.setColor(subTextColor);
        long freeMem = Runtime.getRuntime().freeMemory() / (1024 * 1024);
        long totalMem = Runtime.getRuntime().totalMemory() / (1024 * 1024);
        long usedMem = totalMem - freeMem;
        String statsStr = "MEM: " + usedMem + "MB / " + totalMem + "MB";
        FontMetrics fmStats = g2.getFontMetrics();
        g2.drawString(statsStr, cx + (containerWidth - fmStats.stringWidth(statsStr)) / 2, cy + 185);

        // 8. Pulsing Unlock Prompt at the bottom
        double textPulse = Math.sin(System.currentTimeMillis() * 0.003);
        int alpha = (int) (120 + textPulse * 60); // pulse alpha between 60 and 180
        alpha = Math.max(0, Math.min(255, alpha));
        
        g2.setFont(new Font("SansSerif", Font.BOLD, 11));
        g2.setColor(new Color(textColor.getRed(), textColor.getGreen(), textColor.getBlue(), alpha));
        String promptStr = "TAP ANYWHERE TO WAKE UP";
        FontMetrics fmPrompt = g2.getFontMetrics();
        g2.drawString(promptStr, cx + (containerWidth - fmPrompt.stringWidth(promptStr)) / 2, cy + 212);
    }
}
