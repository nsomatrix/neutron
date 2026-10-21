package org.neutron.app.ui.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import org.neutron.app.Config;

public class SwingPerfHUD {

	private static int frameCount = 0;
	private static int currentFps = 0;
	private static long lastFpsCalcTime = 0;

	private static boolean enabled = false;

	private static final AlphaComposite BG_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.75f);
	private static final AlphaComposite BORDER_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f);
	private static final AlphaComposite TEXT_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

	private static final Color BG_COLOR = new Color(25, 25, 25);
	private static final Color BORDER_COLOR = new Color(150, 150, 150);
	private static final Color GREEN_COLOR = new Color(235, 235, 235);
	private static final Color BLUE_COLOR = new Color(195, 195, 195);
	private static final Color ORANGE_COLOR = new Color(155, 155, 155);
	private static final Color PURPLE_COLOR = new Color(120, 120, 120);

	private static final Font HUD_FONT = new Font("Monospaced", Font.BOLD, 11);

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
		long usedMem = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024L / 1024L;
		long maxMem = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
		int activeThreads = Thread.activeCount();

		// Save graphics state
		java.awt.Composite origComposite = g2d.getComposite();
		java.awt.RenderingHints origHints = g2d.getRenderingHints();
		java.awt.Font origFont = g2d.getFont();
		java.awt.Color origColor = g2d.getColor();

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Format text strings first to dynamically measure exact content width
		String speedStr = (speed == (long) speed) ? String.format("%d.0x", (long) speed) : String.format("%.1fx", speed);
		String line1 = "FPS: " + currentFps;
		String line2 = "Speed: " + speedStr;
		String line3 = "JVM-RAM: " + usedMem + "MB / " + maxMem + "MB";
		String line4 = "Threads: " + activeThreads;

		g2d.setFont(HUD_FONT);
		FontMetrics fm = g2d.getFontMetrics(HUD_FONT);
		int maxTextWidth = Math.max(Math.max(fm.stringWidth(line1), fm.stringWidth(line2)),
		                            Math.max(fm.stringWidth(line3), fm.stringWidth(line4)));

		// Dynamic HUD dimensions with balanced padding
		int paddingX = 12;
		int paddingY = 10;
		int lineSpacing = fm.getHeight() + 3;
		int cardWidth = maxTextWidth + (paddingX * 2);
		int cardHeight = (lineSpacing * 4) + paddingY;
		int margin = 10;
		int x = margin;
		int y = margin;

		// Draw semi-transparent card background
		g2d.setComposite(BG_COMPOSITE);
		g2d.setColor(BG_COLOR);
		g2d.fillRoundRect(x, y, cardWidth, cardHeight, 10, 10);

		// Draw thin border
		g2d.setComposite(BORDER_COMPOSITE);
		g2d.setColor(BORDER_COLOR);
		g2d.drawRoundRect(x, y, cardWidth, cardHeight, 10, 10);

		// Draw text
		g2d.setComposite(TEXT_COMPOSITE);

		int textX = x + paddingX;
		int startY = y + paddingY + fm.getAscent();

		g2d.setColor(GREEN_COLOR);
		g2d.drawString(line1, textX, startY);

		g2d.setColor(BLUE_COLOR);
		g2d.drawString(line2, textX, startY + lineSpacing);

		g2d.setColor(ORANGE_COLOR); // Orange
		g2d.drawString(line3, textX, startY + (lineSpacing * 2));

		g2d.setColor(PURPLE_COLOR); // Purple
		g2d.drawString(line4, textX, startY + (lineSpacing * 3));

		// Restore graphics state
		g2d.setComposite(origComposite);
		g2d.setRenderingHints(origHints);
		g2d.setFont(origFont);
		g2d.setColor(origColor);
	}
}
