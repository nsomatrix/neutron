package org.neutron.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
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

	private final JLabel messageLabel = new JLabel("Ready");
	private final JLabel coordinateLabel = new JLabel();
	private final JLabel resolutionLabel = new JLabel();
	private final SpinnerComponent spinnerComponent = new SpinnerComponent();

	private final JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));

	private String persistentMessage = "Ready";
	private boolean activeTransient = false;

	private Timer revertTimer;
	private Timer fadeTimer;
	private Timer coordinateTimeoutTimer;

	private double currentOpacity = 1.0;

	private static final Pattern sizePattern = Pattern.compile(
			"^(New size|Window size):\\s*(\\d+x\\d+)(?:\\s*\\(Scaled from:\\s*(\\d+x\\d+)\\))?\\s*$"
	);

	private enum MessageType {
		PERSISTENT,
		TRANSIENT,
		LOADING
	}

	public SwingStatusBar() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

		// Left compartment: spinner, status message, coordinate badge
		JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		leftPanel.setOpaque(false);
		leftPanel.add(spinnerComponent);
		leftPanel.add(messageLabel);
		leftPanel.add(coordinateLabel);

		// Style badges
		styleBadge(coordinateLabel);
		styleBadge(resolutionLabel);

		coordinateLabel.setVisible(false);
		resolutionLabel.setVisible(false);

		add(leftPanel, BorderLayout.WEST);

		// Right compartment: resolution badge (other components like ping/meters will be added to this panel on the East by Main)
		rightPanel.setOpaque(false);
		rightPanel.add(resolutionLabel);
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

	public void setText(String text) {
		if (text == null) {
			text = "";
		}
		text = text.trim();

		// 1. Coordinate check
		if (text.matches("^-?\\d+,-?\\d+( -?\\d+x-?\\d+)?$")) {
			coordinateLabel.setText("[" + text + "]");
			coordinateLabel.setVisible(true);
			coordinateTimeoutTimer.restart();
			revalidate();
			repaint();
			return;
		}

		// 2. Resolution check
		Matcher sizeMatcher = sizePattern.matcher(text);
		if (sizeMatcher.matches()) {
			String size1 = sizeMatcher.group(2);
			String size2 = sizeMatcher.group(3);

			if (size2 != null && !size2.isEmpty()) {
				resolutionLabel.setText(size2 + " (Scaled: " + size1 + ")");
			} else {
				resolutionLabel.setText(size1);
			}
			resolutionLabel.setVisible(true);
			revalidate();
			repaint();
			return;
		}

		// 3. Normal status message check
		MessageType type = classifyMessage(text);

		if (type == MessageType.LOADING) {
			if (revertTimer != null) {
				revertTimer.stop();
			}
			activeTransient = false;
			spinnerComponent.start();
			transitionToText(text, false);
		} else if (type == MessageType.TRANSIENT) {
			spinnerComponent.stop();
			activeTransient = true;
			transitionToText(text, true);
		} else { // PERSISTENT
			persistentMessage = text.isEmpty() ? "Ready" : text;
			if (!activeTransient) {
				spinnerComponent.stop();
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

	private MessageType classifyMessage(String text) {
		if (text.isEmpty() || text.equalsIgnoreCase("Ready") || text.startsWith("Running")) {
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

		if (newText.equals(messageLabel.getText()) && Math.abs(currentOpacity - 1.0) < 0.05) {
			if (isTransient) {
				startRevertTimer();
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
						}
					}
				}
				updateLabelColors();
			}
		});
		fadeTimer.start();
	}

	private void startRevertTimer() {
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
		if (resolutionLabel != null) {
			updateBadgeColors(resolutionLabel);
		}
	}

	private void styleBadge(JLabel label) {
		Font font = label.getFont();
		if (font != null) {
			label.setFont(font.deriveFont(Font.BOLD, 10.0f));
		}
		label.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));
		label.setOpaque(true);
		updateBadgeColors(label);
	}

	private void updateBadgeColors(JLabel label) {
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
}
