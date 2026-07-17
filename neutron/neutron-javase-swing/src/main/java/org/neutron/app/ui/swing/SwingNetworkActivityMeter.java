package org.neutron.app.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.neutron.device.ui.NetworkActivityTracker;
import org.neutron.device.ui.NetworkActivityTracker.NetworkActivityListener;

public class SwingNetworkActivityMeter extends JPanel implements NetworkActivityListener {

	private static final long serialVersionUID = 1L;

	private final JLabel rxLabel = new JLabel("↓ 0.0 KB/s");
	private final JLabel txLabel = new JLabel("↑ 0.0 KB/s");
	private final LedComponent rxLed = new LedComponent(new Color(46, 204, 113)); // bright green
	private final LedComponent txLed = new LedComponent(new Color(52, 152, 219)); // bright blue

	private long lastTotalRead = 0;
	private long lastTotalWritten = 0;

	private final DecimalFormat speedFormat = new DecimalFormat("#,##0.0");
	private final DecimalFormat totalFormat = new DecimalFormat("#,##0.00");

	public SwingNetworkActivityMeter() {
		setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		setOpaque(false);

		add(rxLed);
		add(rxLabel);
		add(txLed);
		add(txLabel);

		// Initialize values
		NetworkActivityTracker.addListener(this);
	}

	public void onActivityUpdate(final long totalRead, final long totalWritten, final double readSpeed, final double writeSpeed, final int ping) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				rxLabel.setText("↓ " + formatSpeed(readSpeed));
				txLabel.setText("↑ " + formatSpeed(writeSpeed));

				if (totalRead > lastTotalRead) {
					rxLed.blink();
					lastTotalRead = totalRead;
				}
				if (totalWritten > lastTotalWritten) {
					txLed.blink();
					lastTotalWritten = totalWritten;
				}

				setToolTipText("<html><b>Network Activity</b><br>" +
						"Received: " + formatTotal(totalRead) + "<br>" +
						"Sent: " + formatTotal(totalWritten) + "</html>");
			}
		});
	}

	private String formatSpeed(double speedKbps) {
		if (speedKbps < 1024.0) {
			return speedFormat.format(speedKbps) + " KB/s";
		} else {
			return speedFormat.format(speedKbps / 1024.0) + " MB/s";
		}
	}

	private String formatTotal(long bytes) {
		if (bytes < 1024) {
			return bytes + " B";
		} else if (bytes < 1024 * 1024) {
			return totalFormat.format(bytes / 1024.0) + " KB";
		} else if (bytes < 1024 * 1024 * 1024) {
			return totalFormat.format(bytes / (1024.0 * 1024.0)) + " MB";
		} else {
			return totalFormat.format(bytes / (1024.0 * 1024.0 * 1024.0)) + " GB";
		}
	}

	public void destroy() {
		NetworkActivityTracker.removeListener(this);
	}

	private static class LedComponent extends JPanel {
		private static final long serialVersionUID = 1L;
		private final Color activeColor;
		private final Color inactiveColor;
		private boolean active = false;
		private final Timer offTimer;

		public LedComponent(Color activeColor) {
			this.activeColor = activeColor;
			// Darken the active color for the inactive state
			this.inactiveColor = new Color(
					Math.max(activeColor.getRed() / 4, 20),
					Math.max(activeColor.getGreen() / 4, 20),
					Math.max(activeColor.getBlue() / 4, 20)
			);
			setPreferredSize(new Dimension(8, 8));
			setOpaque(false);

			offTimer = new Timer(250, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					active = false;
					repaint();
				}
			});
			offTimer.setRepeats(false);
		}

		public void blink() {
			active = true;
			repaint();
			offTimer.restart();
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(active ? activeColor : inactiveColor);
			g2.fillOval(0, 0, getWidth(), getHeight());
			
			// subtle white highlights/border
			g2.setColor(new Color(255, 255, 255, 40));
			g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
			g2.dispose();
		}
	}
}
