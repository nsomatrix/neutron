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

	private static final AlphaComposite BG_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f);
	private static final AlphaComposite BORDER_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f);
	private static final AlphaComposite TEXT_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);

	private static final Color BG_COLOR = new Color(20, 20, 20);
	private static final Color RX_COLOR = new Color(46, 204, 113);
	private static final Color TX_COLOR = new Color(52, 152, 219);
	private static final Color SEPARATOR_COLOR = new Color(255, 255, 255, 40);
	private static final Color PING_GRAY = new Color(149, 165, 166);
	private static final Color PING_YELLOW = new Color(241, 196, 15);
	private static final Color PING_RED = new Color(231, 76, 60);
	private static final Color PING_TEXT_COLOR = new Color(220, 220, 220);

	private static final Font OVERLAY_FONT = new Font("SansSerif", Font.BOLD, 10);

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
		g2d.setFont(OVERLAY_FONT);
		FontMetrics fm = g2d.getFontMetrics(OVERLAY_FONT);

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
		g2d.setComposite(BG_COMPOSITE);
		g2d.setColor(BG_COLOR);
		g2d.fillRoundRect(x, y, capsuleWidth, capsuleHeight, capsuleHeight, capsuleHeight);

		// Draw border
		g2d.setComposite(BORDER_COMPOSITE);
		g2d.setColor(Color.WHITE);
		g2d.drawRoundRect(x, y, capsuleWidth, capsuleHeight, capsuleHeight, capsuleHeight);

		// Draw contents
		g2d.setComposite(TEXT_COMPOSITE);
		int curX = x + paddingX;
		int textY = y + (capsuleHeight + fm.getAscent() - fm.getDescent()) / 2;

		// Rx (Green)
		g2d.setColor(RX_COLOR);
		g2d.drawString(rxStr, curX, textY);
		curX += rxWidth;

		// Separator
		g2d.setColor(SEPARATOR_COLOR);
		g2d.drawString("|", curX + 7, textY - 1);
		curX += separatorWidth;

		// Tx (Blue)
		g2d.setColor(TX_COLOR);
		g2d.drawString(txStr, curX, textY);
		curX += txWidth;

		if (pingEnabled) {
			// Separator
			g2d.setColor(SEPARATOR_COLOR);
			g2d.drawString("|", curX + 7, textY - 1);
			curX += separatorWidth;

			// Ping latency indicator dot
			Color pingColor;
			if (ping < 0) {
				pingColor = PING_GRAY;
			} else if (ping < 100) {
				pingColor = RX_COLOR;
			} else if (ping < 250) {
				pingColor = PING_YELLOW;
			} else {
				pingColor = PING_RED;
			}

			g2d.setColor(pingColor);
			g2d.fillOval(curX, y + (capsuleHeight - 6) / 2, 6, 6);
			curX += 10;

			// Ping text
			g2d.setColor(PING_TEXT_COLOR);
			g2d.drawString(pingStr, curX, textY);
		}

		// Restore graphics state
		g2d.setComposite(origComposite);
		g2d.setRenderingHints(origHints);
		g2d.setFont(origFont);
		g2d.setColor(origColor);
	}
}
