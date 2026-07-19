package org.neutron.app.ui.swing;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
				transitionToText(processedText, false);
			}
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
		if (text.startsWith("Loading") || text.startsWith("Downloading") || (text.contains("...") && !text.contains("complete"))) {
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

	private boolean isDarkTheme() {
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
		private double angle = 0;
		private final Timer timer;

		public SpinnerComponent() {
			setPreferredSize(new Dimension(16, 16));
			setVisible(false);

			timer = new Timer(30, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					angle += Math.PI / 8;
					if (angle >= 2 * Math.PI) {
						angle -= 2 * Math.PI;
					}
					repaint();
				}
			});
		}

		public void start() {
			timer.start();
			setVisible(true);
			revalidate();
		}

		public void stop() {
			timer.stop();
			setVisible(false);
			revalidate();
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

			int w = getWidth();
			int h = getHeight();
			int size = Math.min(w, h) - 4;
			int x = (w - size) / 2;
			int y = (h - size) / 2;

			g2.rotate(angle, w / 2.0, h / 2.0);
			g2.setStroke(new java.awt.BasicStroke(2.0f, java.awt.BasicStroke.CAP_ROUND, java.awt.BasicStroke.JOIN_ROUND));

			Color accent = UIManager.getColor("ProgressBar.foreground");
			if (accent == null) {
				accent = new Color(52, 152, 219); // Fallback: sleek blue
			}

			// Draw full subtle track
			g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
			g2.drawArc(x, y, size, size, 0, 360);

			// Draw active rotating part
			g2.setColor(accent);
			g2.drawArc(x, y, size, size, 0, 120);

			g2.dispose();
		}
	}

	private static class StatusDotComponent extends JComponent {
		private static final long serialVersionUID = 1L;
		
		public enum Mode {
			IDLE,
			RUNNING
		}
		
		private Mode mode = Mode.IDLE;
		private float pulseAlpha = 0.0f;
		private float pulseTime = 0.0f;
		private final Timer pulseTimer;

		public StatusDotComponent() {
			setPreferredSize(new Dimension(16, 16));
			
			pulseTimer = new Timer(30, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					pulseTime += 0.05f;
					if (pulseTime >= (float) (Math.PI * 2)) {
						pulseTime = 0.0f;
					}
					pulseAlpha = (float) ((Math.sin(pulseTime) + 1.0) / 2.0);
					repaint();
				}
			});
		}

		public void setMode(Mode mode) {
			if (this.mode != mode) {
				this.mode = mode;
				if (mode == Mode.RUNNING) {
					pulseTime = 0.0f;
					pulseAlpha = 0.0f;
					pulseTimer.start();
				} else {
					pulseTimer.stop();
					pulseAlpha = 0.0f;
				}
				repaint();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();
			int size = 6;
			int x = (w - size) / 2;
			int y = (h - size) / 2;

			if (mode == Mode.RUNNING) {
				Color baseGreen = new Color(46, 204, 113); // Emerald Green
				
				float currentGlowRadius = size + (pulseAlpha * 6.0f);
				int glowSize = Math.round(currentGlowRadius);
				int gx = (w - glowSize) / 2;
				int gy = (h - glowSize) / 2;
				
				int alpha = Math.round((1.0f - pulseAlpha) * 120);
				g2.setColor(new Color(baseGreen.getRed(), baseGreen.getGreen(), baseGreen.getBlue(), alpha));
				g2.fillOval(gx, gy, glowSize, glowSize);
				
				g2.setColor(baseGreen);
				g2.fillOval(x, y, size, size);
			} else {
				Color accent = UIManager.getColor("ProgressBar.foreground");
				if (accent == null) {
					accent = new Color(52, 152, 219); // Fallback: sleek blue
				}
				g2.setColor(accent);
				g2.fillOval(x, y, size, size);
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
