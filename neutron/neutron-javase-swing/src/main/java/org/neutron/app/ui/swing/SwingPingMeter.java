package org.neutron.app.ui.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.neutron.device.ui.NetworkActivityTracker;
import org.neutron.device.ui.NetworkActivityTracker.NetworkActivityListener;

public class SwingPingMeter extends JPanel implements NetworkActivityListener {

	private static final long serialVersionUID = 1L;

	private final JLabel pingLabel = new JLabel("-- ms");
	private final LedComponent pingLed = new LedComponent();

	public SwingPingMeter() {
		setLayout(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		setOpaque(false);

		add(pingLed);
		add(pingLabel);

		// Initialize values
		NetworkActivityTracker.addListener(this);
	}

	public void onActivityUpdate(long totalRead, long totalWritten, double readSpeed, double writeSpeed, final int ping) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				if (ping < 0) {
					pingLabel.setText("-- ms");
					pingLed.setColor(new Color(127, 140, 141)); // Gray
					setToolTipText("Ping: Unreachable or no active server connection");
				} else {
					pingLabel.setText(ping + " ms");
					if (ping <= 100) {
						pingLed.setColor(new Color(46, 204, 113)); // Green
					} else if (ping <= 250) {
						pingLed.setColor(new Color(230, 126, 34)); // Orange
					} else {
						pingLed.setColor(new Color(231, 76, 60)); // Red
					}
					setToolTipText("<html><b>Network Latency</b><br>Latency: " + ping + " ms</html>");
				}
			}
		});
	}

	public void destroy() {
		NetworkActivityTracker.removeListener(this);
	}

	private static class LedComponent extends JPanel {
		private static final long serialVersionUID = 1L;
		private Color color = new Color(127, 140, 141);

		public LedComponent() {
			setPreferredSize(new Dimension(8, 8));
			setOpaque(false);
		}

		public void setColor(Color color) {
			this.color = color;
			repaint();
		}

		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(color);
			g2.fillOval(0, 0, getWidth(), getHeight());
			
			g2.setColor(new Color(255, 255, 255, 40));
			g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);
			g2.dispose();
		}
	}
}
