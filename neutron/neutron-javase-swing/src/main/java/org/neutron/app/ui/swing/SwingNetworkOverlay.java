package org.neutron.app.ui.swing;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.DecimalFormat;

import org.neutron.app.Config;
import org.neutron.device.ui.NetworkActivityTracker;

public class SwingNetworkOverlay {

	private static boolean enabled = true;
	private static final DecimalFormat speedFormat = new DecimalFormat("#,##0.0");

	public static boolean isEnabled() {
		return enabled;
	}

	public static void setEnabled(boolean enabled) {
		SwingNetworkOverlay.enabled = enabled;
	}

	private static String formatSpeed(double speedKbps) {
		if (speedKbps < 1024.0) {
			return speedFormat.format(speedKbps) + " KB/s";
		} else {
			return speedFormat.format(speedKbps / 1024.0) + " MB/s";
		}
	}

	public static void paint(Graphics2D g2d, int screenWidth, int screenHeight) {
		if (!enabled) {
			return;
		}

		double rxSpeed = NetworkActivityTracker.getCurrentReadSpeed();
		double txSpeed = NetworkActivityTracker.getCurrentWriteSpeed();
		int ping = NetworkActivityTracker.getCurrentPing();
		boolean pingEnabled = NetworkActivityTracker.isPingEnabled();

		// Save graphics state
		java.awt.Composite origComposite = g2d.getComposite();
		java.awt.RenderingHints origHints = g2d.getRenderingHints();
		java.awt.Font origFont = g2d.getFont();
		java.awt.Color origColor = g2d.getColor();

		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Build strings
		String rxStr = "↓ " + formatSpeed(rxSpeed);
		String txStr = "↑ " + formatSpeed(txSpeed);
		String pingStr = "";
		if (pingEnabled) {
			pingStr = (ping >= 0) ? ping + " ms" : "-- ms";
		}

		// Text styling
		Font font = new Font("SansSerif", Font.BOLD, 10);
		g2d.setFont(font);
		FontMetrics fm = g2d.getFontMetrics(font);

		// Calculate sizes
		int rxWidth = fm.stringWidth(rxStr);
		int txWidth = fm.stringWidth(txStr);
		int pingWidth = pingEnabled ? fm.stringWidth(pingStr) + 14 : 0; // extra space for icon/bullet
		int separatorWidth = 16;

		int totalContentWidth = rxWidth + separatorWidth + txWidth;
		if (pingEnabled) {
			totalContentWidth += separatorWidth + pingWidth;
		}

		int paddingX = 14;
		int capsuleWidth = totalContentWidth + (paddingX * 2);
		int capsuleHeight = 24;

		int x = (screenWidth - capsuleWidth) / 2;
		int y = 8; // small margin from top

		// Draw capsule background (sleek dark semi-transparent glass)
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
		g2d.setColor(new Color(20, 20, 20));
		g2d.fillRoundRect(x, y, capsuleWidth, capsuleHeight, capsuleHeight, capsuleHeight);

		// Draw border
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
		g2d.setColor(Color.WHITE);
		g2d.drawRoundRect(x, y, capsuleWidth, capsuleHeight, capsuleHeight, capsuleHeight);

		// Draw contents
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
		int curX = x + paddingX;
		int textY = y + (capsuleHeight + fm.getAscent() - fm.getDescent()) / 2;

		// Rx (Green)
		g2d.setColor(new Color(46, 204, 113));
		g2d.drawString(rxStr, curX, textY);
		curX += rxWidth;

		// Separator
		g2d.setColor(new Color(255, 255, 255, 40));
		g2d.drawString("|", curX + 7, textY - 1);
		curX += separatorWidth;

		// Tx (Blue)
		g2d.setColor(new Color(52, 152, 219));
		g2d.drawString(txStr, curX, textY);
		curX += txWidth;

		if (pingEnabled) {
			// Separator
			g2d.setColor(new Color(255, 255, 255, 40));
			g2d.drawString("|", curX + 7, textY - 1);
			curX += separatorWidth;

			// Ping latency indicator dot
			Color pingColor;
			if (ping < 0) {
				pingColor = new Color(149, 165, 166); // gray
			} else if (ping < 100) {
				pingColor = new Color(46, 204, 113); // green
			} else if (ping < 250) {
				pingColor = new Color(241, 196, 15); // yellow
			} else {
				pingColor = new Color(231, 76, 60); // red
			}

			g2d.setColor(pingColor);
			g2d.fillOval(curX, y + (capsuleHeight - 6) / 2, 6, 6);
			curX += 10;

			// Ping text
			g2d.setColor(new Color(220, 220, 220));
			g2d.drawString(pingStr, curX, textY);
		}

		// Restore graphics state
		g2d.setComposite(origComposite);
		g2d.setRenderingHints(origHints);
		g2d.setFont(origFont);
		g2d.setColor(origColor);
	}
}
