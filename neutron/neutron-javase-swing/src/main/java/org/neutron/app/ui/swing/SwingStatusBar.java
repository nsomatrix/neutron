package org.neutron.app.ui.swing;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;

public class SwingStatusBar extends JPanel {

	private static final long serialVersionUID = 1L;

	private final JLabel messageLabel = new JLabel("");
	private final BadgeLabel coordinateLabel = new BadgeLabel();
	private final SpinnerComponent spinnerComponent = new SpinnerComponent();
	private final StatusDotComponent statusDotComponent = new StatusDotComponent();

	private final JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

	private String persistentMessage = "";
	private boolean activeTransient = false;

	private Timer revertTimer;
	private Timer fadeTimer;
	private Timer coordinateTimeoutTimer;
	private Timer persistentHideTimer;

	private double currentOpacity = 1.0;

	private enum MessageType {
		PERSISTENT,
		TRANSIENT,
		LOADING
	}

	public SwingStatusBar() {
		setLayout(new BorderLayout());
		setBorder(new javax.swing.border.EmptyBorder(3, 10, 3, 10));

		// Left compartment: status dot, spinner, status message, coordinate badge
		JPanel leftPanel = new JPanel(new GridBagLayout());
		leftPanel.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weighty = 1.0;

		gbc.gridx = 0;
		gbc.insets = new Insets(0, 0, 0, 8);
		leftPanel.add(statusDotComponent, gbc);

		gbc.gridx = 1;
		gbc.insets = new Insets(0, 0, 0, 8);
		leftPanel.add(spinnerComponent, gbc);

		gbc.gridx = 2;
		gbc.insets = new Insets(0, 0, 0, 8);
		leftPanel.add(messageLabel, gbc);

		gbc.gridx = 3;
		gbc.insets = new Insets(0, 0, 0, 0);
		leftPanel.add(coordinateLabel, gbc);

		// Style badges
		styleBadge(coordinateLabel);

		coordinateLabel.setVisible(false);

		add(leftPanel, BorderLayout.WEST);

		// Right compartment (other components like ping/meters will be added to this panel on the East by Main)
		rightPanel.setOpaque(false);
		add(rightPanel, BorderLayout.EAST);

		// Configure coordinate auto-hide timer
		coordinateTimeoutTimer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				coordinateLabel.setVisible(false);
				revalidate();
				repaint();
			}
		});
		coordinateTimeoutTimer.setRepeats(false);

		// Set default fonts
		Font baseFont = messageLabel.getFont();
		if (baseFont != null) {
			Font smallFont = baseFont.deriveFont(11.0f);
			messageLabel.setFont(smallFont);
		}

		// Register configuration change listener to dynamically trigger activity spinner on settings update
		org.neutron.app.Config.addConfigChangeListener(new org.neutron.app.ConfigChangeListener() {
			public void onConfigChanged(final String key, final Object value) {
				if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
					javax.swing.SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							onConfigChanged(key, value);
						}
					});
					return;
				}
				String valStr = (value instanceof Boolean) ? (((Boolean) value) ? "Enabled" : "Disabled") : (value != null ? value.toString() : "");
				setText("Setting: " + key + (valStr.isEmpty() ? "" : " → " + valStr));
			}
		});
	}

	public void setText(final String text) {
		if (!javax.swing.SwingUtilities.isEventDispatchThread()) {
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					setText(text);
				}
			});
			return;
		}

		if (persistentHideTimer != null) {
			persistentHideTimer.stop();
		}

		String processedText = text == null ? "" : text.trim();

		// 1. Coordinate check
		if (processedText.matches("^-?\\d+,-?\\d+( -?\\d+x-?\\d+)?$")) {
			coordinateLabel.setText("[" + processedText + "]");
			coordinateLabel.setVisible(true);
			coordinateTimeoutTimer.restart();
			revalidate();
			repaint();
			return;
		}

		// 2. Normal status message check
		MessageType type = classifyMessage(processedText);

		if (type == MessageType.LOADING) {
			if (revertTimer != null) {
				revertTimer.stop();
			}
			activeTransient = false;
			statusDotComponent.setVisible(false);
			spinnerComponent.start();
			if (processedText.equalsIgnoreCase("Loading...")) {
				transitionToText("", false);
			} else {
				transitionToText(processedText, true);
			}
			startRevertTimer();
		} else if (type == MessageType.TRANSIENT) {
			spinnerComponent.stop();
			statusDotComponent.setVisible(true);
			boolean isRunning = persistentMessage.startsWith("Running");
			statusDotComponent.setMode(isRunning ? StatusDotComponent.Mode.RUNNING : StatusDotComponent.Mode.IDLE);
			activeTransient = true;
			transitionToText(processedText, true);
		} else { // PERSISTENT
			persistentMessage = processedText;
			boolean isRunning = persistentMessage.startsWith("Running");
			statusDotComponent.setMode(isRunning ? StatusDotComponent.Mode.RUNNING : StatusDotComponent.Mode.IDLE);
			if (!activeTransient) {
				spinnerComponent.stop();
				statusDotComponent.setVisible(true);
				transitionToText(persistentMessage, false);
			}
		}
	}

	public String getText() {
		return messageLabel.getText();
	}

	public JPanel getRightPanel() {
		return rightPanel;
	}


	public void setOnCoordinateBadgeClick(Runnable r) {
		coordinateLabel.setClickAction(r);
	}

	private MessageType classifyMessage(String text) {
		if (text.isEmpty() || text.equalsIgnoreCase("Ready") || text.startsWith("Ready to launch") || text.startsWith("Running")) {
			return MessageType.PERSISTENT;
		}
		if (text.startsWith("Loading") || text.startsWith("Downloading") || text.startsWith("Setting:") || text.startsWith("Updating") || (text.contains("...") && !text.contains("complete"))) {
			return MessageType.LOADING;
		}
		return MessageType.TRANSIENT;
	}

	private void transitionToText(final String newText, final boolean isTransient) {
		if (fadeTimer != null) {
			fadeTimer.stop();
		}
		if (persistentHideTimer != null) {
			persistentHideTimer.stop();
		}

		if (newText.equals(messageLabel.getText()) && Math.abs(currentOpacity - 1.0) < 0.05) {
			if (isTransient) {
				startRevertTimer();
			} else {
				startPersistentHideTimer();
			}
			return;
		}

		fadeTimer = new Timer(15, new ActionListener() {
			private boolean textChanged = false;

			public void actionPerformed(ActionEvent e) {
				if (!textChanged) {
					currentOpacity -= 0.15; // fast fade out
					if (currentOpacity <= 0.0) {
						currentOpacity = 0.0;
						messageLabel.setText(newText);
						textChanged = true;
					}
				} else {
					currentOpacity += 0.15; // fast fade in
					if (currentOpacity >= 1.0) {
						currentOpacity = 1.0;
						fadeTimer.stop();
						if (isTransient) {
							startRevertTimer();
						} else {
							startPersistentHideTimer();
						}
					}
				}
				updateLabelColors();
			}
		});
		fadeTimer.start();
	}

	private void startRevertTimer() {
		if (org.neutron.app.Config.isFullscreen()) {
			return;
		}
		if (revertTimer == null) {
			revertTimer = new Timer(3500, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					activeTransient = false;
					transitionToText(persistentMessage, false);
				}
			});
			revertTimer.setRepeats(false);
		} else {
			revertTimer.restart();
		}
	}

	private void startPersistentHideTimer() {
		if (persistentHideTimer == null) {
			persistentHideTimer = new Timer(5000, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					fadeTextToEmpty();
				}
			});
			persistentHideTimer.setRepeats(false);
			persistentHideTimer.start();
		} else {
			persistentHideTimer.restart();
		}
	}

	private void fadeTextToEmpty() {
		if (fadeTimer != null) {
			fadeTimer.stop();
		}
		fadeTimer = new Timer(15, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				currentOpacity -= 0.05; // smooth slow fade out
				if (currentOpacity <= 0.0) {
					currentOpacity = 0.0;
					messageLabel.setText("");
					fadeTimer.stop();
				}
				updateLabelColors();
			}
		});
		fadeTimer.start();
	}

	public void clearTransientMessage() {
		if (revertTimer != null) {
			revertTimer.stop();
		}
		if (persistentHideTimer != null) {
			persistentHideTimer.stop();
		}
		activeTransient = false;
		messageLabel.setText(persistentMessage);
		currentOpacity = 1.0;
		updateLabelColors();
		
		boolean isRunning = persistentMessage.startsWith("Running");
		statusDotComponent.setMode(isRunning ? StatusDotComponent.Mode.RUNNING : StatusDotComponent.Mode.IDLE);

		startPersistentHideTimer();
	}

	private void updateLabelColors() {
		Color baseColor = UIManager.getColor("Label.foreground");
		if (baseColor == null) {
			baseColor = Color.DARK_GRAY;
		}
		int r = baseColor.getRed();
		int g = baseColor.getGreen();
		int b = baseColor.getBlue();
		int a = (int) (currentOpacity * 255.0);
		if (a < 0) a = 0;
		if (a > 255) a = 255;
		
		if (messageLabel != null) {
			messageLabel.setForeground(new Color(r, g, b, a));
		}

		if (coordinateLabel != null) {
			updateBadgeColors(coordinateLabel);
		}
	}

	private void styleBadge(BadgeLabel label) {
		Font font = label.getFont();
		if (font != null) {
			label.setFont(font.deriveFont(Font.BOLD, 10.0f));
		}
		updateBadgeColors(label);
	}

	private void updateBadgeColors(BadgeLabel label) {
		if (isDarkTheme()) {
			label.setBackground(new Color(255, 255, 255, 18));
			label.setForeground(new Color(200, 200, 200));
		} else {
			label.setBackground(new Color(0, 0, 0, 15));
			label.setForeground(new Color(70, 70, 70));
		}
	}

	private static boolean isDarkTheme() {
		try {
			Class<?> flatLafClass = Class.forName("com.formdev.flatlaf.FlatLaf");
			java.lang.reflect.Method isDarkMethod = flatLafClass.getMethod("isLafDark");
			return (Boolean) isDarkMethod.invoke(null);
		} catch (Exception e) {
			Color bg = UIManager.getColor("Panel.background");
			if (bg != null) {
				double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255.0;
				return luminance < 0.5;
			}
			return false;
		}
	}

	@Override
	public void updateUI() {
		super.updateUI();
		updateLabelColors();
	}

	@Override
	public Dimension getPreferredSize() {
		Dimension d = super.getPreferredSize();
		return new Dimension(d.width, 28);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		// Sleek top separator line separating emulator panel from status bar
		Color borderColor = UIManager.getColor("Component.borderColor");
		if (borderColor == null) {
			borderColor = isDarkTheme() ? new Color(255, 255, 255, 20) : new Color(0, 0, 0, 30);
		}
		g2.setColor(borderColor);
		g2.drawLine(0, 0, getWidth(), 0);
		g2.dispose();
	}


	private static class SpinnerComponent extends JComponent {
		private static final long serialVersionUID = 1L;

		private static final Stroke TRACK_STROKE = new BasicStroke(1.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		private static final Stroke ARC_STROKE = new BasicStroke(1.8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

		private int angle = 0;
		private final Timer timer;

		public SpinnerComponent() {
			setPreferredSize(new Dimension(16, 16));
			setVisible(false);

			// Silky smooth ~30 FPS circular animation rotation
			timer = new Timer(33, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					angle = (angle + 12) % 360;
					repaint();
				}
			});
		}

		public void start() {
			angle = 0;
			timer.start();
			setVisible(true);
			revalidate();
			repaint();
		}

		public void stop() {
			timer.stop();
			setVisible(false);
			revalidate();
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			if (!isVisible()) {
				return;
			}
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

			int size = 11;
			double x = (getWidth() - size) / 2.0;
			double y = (getHeight() - size) / 2.0;

			// Modern monochrome palette: refined charcoal/black in light mode, elegant silver gray in dark mode
			Color spinnerColor = UIManager.getColor("Label.foreground");
			if (spinnerColor == null) {
				spinnerColor = isDarkTheme() ? new Color(185, 185, 185) : new Color(45, 45, 45);
			}

			// 1. Subtle circular track ring
			Color trackColor = isDarkTheme() ? new Color(255, 255, 255, 30) : new Color(0, 0, 0, 25);
			g2.setColor(trackColor);
			g2.setStroke(TRACK_STROKE);
			g2.draw(new java.awt.geom.Ellipse2D.Double(x, y, size, size));

			// 2. Active sweeping rotating arc (90-degree arc)
			g2.setColor(spinnerColor);
			g2.setStroke(ARC_STROKE);
			g2.draw(new java.awt.geom.Arc2D.Double(x, y, size, size, -angle, 90, java.awt.geom.Arc2D.OPEN));

			g2.dispose();
		}
	}

	private static class StatusDotComponent extends JComponent {
		private static final long serialVersionUID = 1L;

		private static final Stroke RING_STROKE = new BasicStroke(1.2f);
		private static final Stroke HALO_STROKE = new BasicStroke(1.0f);

		public enum Mode {
			IDLE,
			RUNNING
		}

		private Mode mode = Mode.IDLE;

		public StatusDotComponent() {
			setPreferredSize(new Dimension(14, 14));
		}

		public void setMode(Mode mode) {
			if (this.mode != mode) {
				this.mode = mode;
				repaint();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

			double dotSize = 6.0;
			double x = (getWidth() - dotSize) / 2.0;
			double y = (getHeight() - dotSize) / 2.0;

			boolean dark = isDarkTheme();

			if (mode == Mode.RUNNING) {
				// Clean modern sage green (GitHub / VS Code design standard)
				Color activeGreen = dark ? new Color(63, 185, 80) : new Color(45, 164, 78);
				Color subtleHalo = new Color(activeGreen.getRed(), activeGreen.getGreen(), activeGreen.getBlue(), dark ? 45 : 35);

				// Subtle crisp static ambient halo
				double haloSize = 9.0;
				double hx = (getWidth() - haloSize) / 2.0;
				double hy = (getHeight() - haloSize) / 2.0;
				g2.setColor(subtleHalo);
				g2.setStroke(HALO_STROKE);
				g2.draw(new java.awt.geom.Ellipse2D.Double(hx, hy, haloSize, haloSize));

				// Solid clean center pip
				g2.setColor(activeGreen);
				g2.fill(new java.awt.geom.Ellipse2D.Double(x, y, dotSize, dotSize));
			} else {
				// Muted neutral standby indicator (clean hollow ring)
				Color idleColor = UIManager.getColor("Label.disabledForeground");
				if (idleColor == null) {
					idleColor = dark ? new Color(130, 130, 130) : new Color(160, 160, 160);
				}
				g2.setColor(idleColor);
				g2.setStroke(RING_STROKE);
				g2.draw(new java.awt.geom.Ellipse2D.Double(x, y, dotSize, dotSize));
			}

			g2.dispose();
		}
	}

	private static class BadgeLabel extends JLabel {
		private static final long serialVersionUID = 1L;
		private boolean hovered = false;
		private Runnable clickAction;

		public BadgeLabel() {
			setOpaque(false);
			setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
			setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
			
			addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseEntered(java.awt.event.MouseEvent e) {
					hovered = true;
					repaint();
				}
				@Override
				public void mouseExited(java.awt.event.MouseEvent e) {
					hovered = false;
					repaint();
				}
				@Override
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (clickAction != null) {
						clickAction.run();
					}
				}
			});
		}

		public void setClickAction(Runnable clickAction) {
			this.clickAction = clickAction;
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
			Color bg = getBackground();
			if (hovered) {
				int alpha = Math.min(255, bg.getAlpha() * 2);
				bg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), alpha);
			}
			g2.setColor(bg);
			g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
			g2.dispose();
			
			super.paintComponent(g);
		}
	}
}
