package org.neutron.app.ui.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.neutron.app.Config;

public class SwingPerfHUD {

	private static int frameCount = 0;
	private static int currentFps = 0;
	private static long lastFpsCalcTime = 0;

	private static boolean enabled = false;

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		SwingPerfHUD.enabled = enabled;
	}

	public static void tickFrame() {
		frameCount++;
		long now = System.currentTimeMillis();
		if (lastFpsCalcTime == 0) {
			lastFpsCalcTime = now;
		} else if (now - lastFpsCalcTime >= 1000) {
			currentFps = (int) (frameCount * 1000.0 / (now - lastFpsCalcTime));
			frameCount = 0;
			lastFpsCalcTime = now;
		}
	}

	public static void paint(Graphics2D g2d, int width, int height) {
		if (!enabled) {
			return;
		}

		tickFrame();

		// Get stats
		double speed = Config.getSpeedMultiplier();
		int maxFps = Config.getMaxFps();
		long usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
		long maxMem = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
		int activeThreads = Thread.activeCount();

		// Save graphics state
		java.awt.Composite origComposite = g2d.getComposite();
		java.awt.RenderingHints origHints = g2d.getRenderingHints();
		java.awt.Font origFont = g2d.getFont();
		java.awt.Color origColor = g2d.getColor();

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// HUD design
		int cardWidth = 180;
		int cardHeight = 95;
		int margin = 10;
		int x = margin;
		int y = margin;

		// Draw semi-transparent card background
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f));
		g2d.setColor(new Color(25, 25, 25));
		g2d.fillRoundRect(x, y, cardWidth, cardHeight, 10, 10);

		// Draw thin border
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
		g2d.setColor(new Color(150, 150, 150));
		g2d.drawRoundRect(x, y, cardWidth, cardHeight, 10, 10);

		// Draw text
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		g2d.setFont(new Font("Monospaced", Font.BOLD, 11));

		String fpsStr = maxFps > 0 ? maxFps + " capped" : "Unlimited";
		String speedStr = String.format("%.1fx", speed);

		g2d.setColor(new Color(80, 220, 100)); // Neon green
		g2d.drawString("FPS: " + currentFps + " (" + fpsStr + ")", x + 12, y + 20);

		g2d.setColor(new Color(100, 200, 255)); // Neon blue
		g2d.drawString("Speed: " + speedStr, x + 12, y + 38);

		g2d.setColor(new Color(255, 180, 50)); // Orange
		g2d.drawString("JVM-RAM: " + usedMem + "MB / " + maxMem + "MB", x + 12, y + 56);

		g2d.setColor(new Color(220, 100, 220)); // Purple
		g2d.drawString("Threads: " + activeThreads, x + 12, y + 74);

		// Restore graphics state
		g2d.setComposite(origComposite);
		g2d.setRenderingHints(origHints);
		g2d.setFont(origFont);
		g2d.setColor(origColor);
	}
}
