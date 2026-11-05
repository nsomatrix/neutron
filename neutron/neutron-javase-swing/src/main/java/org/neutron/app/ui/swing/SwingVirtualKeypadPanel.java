/**
 *  Neutron
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 */
package org.neutron.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import org.neutron.app.Config;
import org.neutron.device.impl.ButtonName;

/**
 * On-screen virtual button board providing touch and mouse-driven controls
 * for all J2ME games (8-way D-Pad, Softkeys, and full 12-key numeric keypad).
 * Fully integrated with native Look-and-Feel / FlatLaf themes via UIManager.
 */
public class SwingVirtualKeypadPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private CardLayout rightCardLayout;
	private JPanel rightCardPanel;
	private JButton modeToggleButton;
	private boolean isFullKeypadMode = false;

	public SwingVirtualKeypadPanel() {
		setLayout(new BorderLayout(8, 6));
		setBorder(BorderFactory.createEmptyBorder(6, 10, 8, 10));

		String initialMode = Config.getVirtualButtonsMode();
		isFullKeypadMode = "full".equalsIgnoreCase(initialMode);

		initComponents();
		updateModeView();
	}

	@Override
	public void updateUI() {
		super.updateUI();
		repaint();
	}

	private void initComponents() {
		// Top Bar: Softkeys and mode switcher
		JPanel topBar = new JPanel(new BorderLayout(12, 0));
		topBar.setOpaque(false);

		VirtualKeyButton lskButton = new VirtualKeyButton(ButtonName.SOFT1, "LSK", "Menu", 74, 28);
		VirtualKeyButton rskButton = new VirtualKeyButton(ButtonName.SOFT2, "RSK", "Back", 74, 28);

		modeToggleButton = new JButton(isFullKeypadMode ? "Mode: Full Keypad" : "Mode: Game Pad") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				Color fill = getButtonBackground();
				Color border = getBorderColor();
				Color text = getButtonForeground();

				g2.setColor(fill);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2.setColor(border);
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);

				g2.setColor(text);
				g2.setFont(getFont().deriveFont(Font.BOLD, 10.5f));
				FontMetrics fm = g2.getFontMetrics();
				int tx = (getWidth() - fm.stringWidth(getText())) / 2;
				int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
				g2.drawString(getText(), tx, ty);
				g2.dispose();
			}
		};
		modeToggleButton.setPreferredSize(new Dimension(130, 24));
		modeToggleButton.setFocusable(false);
		modeToggleButton.setContentAreaFilled(false);
		modeToggleButton.setBorderPainted(false);
		modeToggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		modeToggleButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				isFullKeypadMode = !isFullKeypadMode;
				Config.setVirtualButtonsMode(isFullKeypadMode ? "full" : "gamepad");
				updateModeView();
			}
		});

		topBar.add(lskButton, BorderLayout.WEST);
		topBar.add(modeToggleButton, BorderLayout.CENTER);
		topBar.add(rskButton, BorderLayout.EAST);
		add(topBar, BorderLayout.NORTH);

		// Center body: Left is 8-Way D-Pad, Right is Action Cluster / Numeric Keypad
		JPanel bodyPanel = new JPanel();
		bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.X_AXIS));
		bodyPanel.setOpaque(false);

		// 1. 8-Way D-Pad Section
		JPanel dpadPanel = createDPadPanel();
		bodyPanel.add(dpadPanel);

		bodyPanel.add(Box.createHorizontalStrut(14));

		// 2. Right Section with CardLayout (Gamepad vs Full Keypad)
		rightCardLayout = new CardLayout();
		rightCardPanel = new JPanel(rightCardLayout);
		rightCardPanel.setOpaque(false);

		JPanel gamepadActionPanel = createGamepadActionPanel();
		JPanel fullKeypadPanel = createFullKeypadPanel();

		rightCardPanel.add(gamepadActionPanel, "gamepad");
		rightCardPanel.add(fullKeypadPanel, "full");

		bodyPanel.add(rightCardPanel);
		add(bodyPanel, BorderLayout.CENTER);
	}

	private void updateModeView() {
		if (isFullKeypadMode) {
			modeToggleButton.setText("Mode: Full Keypad");
			rightCardLayout.show(rightCardPanel, "full");
		} else {
			modeToggleButton.setText("Mode: Game Pad");
			rightCardLayout.show(rightCardPanel, "gamepad");
		}
		revalidate();
		repaint();
	}

	/**
	 * Creates a complete 8-way D-Pad (Cardinals + Diagonals + Center OK/SELECT).
	 */
	private JPanel createDPadPanel() {
		JPanel panel = new JPanel(new GridLayout(3, 3, 3, 3));
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(135, 115));
		panel.setMaximumSize(new Dimension(145, 125));

		// Top row: Top-Left (↖), Up (▲), Top-Right (↗)
		VirtualKeyButton upLeft = new VirtualKeyButton(new ButtonName[] { ButtonName.UP, ButtonName.LEFT }, "↖", null, 42, 36);
		VirtualKeyButton up = new VirtualKeyButton(ButtonName.UP, "▲", null, 42, 36);
		VirtualKeyButton upRight = new VirtualKeyButton(new ButtonName[] { ButtonName.UP, ButtonName.RIGHT }, "↗", null, 42, 36);

		// Middle row: Left (◀), Center OK (OK), Right (▶)
		VirtualKeyButton left = new VirtualKeyButton(ButtonName.LEFT, "◀", null, 42, 36);
		VirtualKeyButton ok = new VirtualKeyButton(ButtonName.SELECT, "OK", null, 42, 36);
		VirtualKeyButton right = new VirtualKeyButton(ButtonName.RIGHT, "▶", null, 42, 36);

		// Bottom row: Bottom-Left (↙), Down (▼), Bottom-Right (↘)
		VirtualKeyButton downLeft = new VirtualKeyButton(new ButtonName[] { ButtonName.DOWN, ButtonName.LEFT }, "↙", null, 42, 36);
		VirtualKeyButton down = new VirtualKeyButton(ButtonName.DOWN, "▼", null, 42, 36);
		VirtualKeyButton downRight = new VirtualKeyButton(new ButtonName[] { ButtonName.DOWN, ButtonName.RIGHT }, "↘", null, 42, 36);

		panel.add(upLeft);
		panel.add(up);
		panel.add(upRight);

		panel.add(left);
		panel.add(ok);
		panel.add(right);

		panel.add(downLeft);
		panel.add(down);
		panel.add(downRight);

		return panel;
	}

	/**
	 * Creates a sleek action cluster for arcade/action games (Key 5, Clear, *, #).
	 */
	private JPanel createGamepadActionPanel() {
		JPanel panel = new JPanel(new GridLayout(2, 2, 4, 4));
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(135, 115));
		panel.setMaximumSize(new Dimension(150, 125));

		VirtualKeyButton key5 = new VirtualKeyButton(ButtonName.KEY_NUM5, "5", "Action", 56, 44);
		VirtualKeyButton clear = new VirtualKeyButton(ButtonName.DELETE, "C", "Del", 56, 44);
		VirtualKeyButton star = new VirtualKeyButton(ButtonName.KEY_STAR, "*", null, 56, 44);
		VirtualKeyButton pound = new VirtualKeyButton(ButtonName.KEY_POUND, "#", null, 56, 44);

		panel.add(key5);
		panel.add(clear);
		panel.add(star);
		panel.add(pound);

		return panel;
	}

	/**
	 * Creates the complete 3x4 mobile phone numeric keypad (1-9, *, 0, #) + Clear.
	 */
	private JPanel createFullKeypadPanel() {
		JPanel wrapper = new JPanel(new BorderLayout(4, 0));
		wrapper.setOpaque(false);

		JPanel grid = new JPanel(new GridLayout(4, 3, 3, 3));
		grid.setOpaque(false);

		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM1, "1", null, 38, 26));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM2, "2", "ABC", 38, 26));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM3, "3", "DEF", 38, 26));

		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM4, "4", "GHI", 38, 26));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM5, "5", "JKL", 38, 26));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM6, "6", "MNO", 38, 26));

		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM7, "7", "PQRS", 38, 26));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM8, "8", "TUV", 38, 26));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM9, "9", "WXYZ", 38, 26));

		grid.add(new VirtualKeyButton(ButtonName.KEY_STAR, "*", null, 38, 26));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM0, "0", "+", 38, 26));
		grid.add(new VirtualKeyButton(ButtonName.KEY_POUND, "#", null, 38, 26));

		wrapper.add(grid, BorderLayout.CENTER);

		JPanel sideBar = new JPanel(new GridLayout(2, 1, 0, 3));
		sideBar.setOpaque(false);
		sideBar.add(new VirtualKeyButton(ButtonName.DELETE, "C", "Del", 34, 46));
		wrapper.add(sideBar, BorderLayout.EAST);

		return wrapper;
	}

	static boolean isDarkTheme() {
		try {
			Class flatLafClass = Class.forName("com.formdev.flatlaf.FlatLaf");
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

	static Color getPanelBackground() {
		Color c = UIManager.getColor("Panel.background");
		return (c != null) ? c : (isDarkTheme() ? new Color(60, 63, 65) : new Color(242, 242, 242));
	}

	static Color getBorderColor() {
		Color c = UIManager.getColor("Component.borderColor");
		if (c == null) {
			c = UIManager.getColor("Button.borderColor");
		}
		if (c == null) {
			c = UIManager.getColor("Separator.foreground");
		}
		if (c == null) {
			c = isDarkTheme() ? new Color(255, 255, 255, 30) : new Color(0, 0, 0, 35);
		}
		return c;
	}

	static Color getButtonBackground() {
		Color c = UIManager.getColor("Button.background");
		if (c != null) {
			return c;
		}
		return isDarkTheme() ? new Color(76, 80, 82) : new Color(255, 255, 255);
	}

	static Color getButtonForeground() {
		Color c = UIManager.getColor("Button.foreground");
		if (c == null) {
			c = UIManager.getColor("Label.foreground");
		}
		return (c != null) ? c : (isDarkTheme() ? new Color(187, 187, 187) : new Color(30, 30, 30));
	}

	static Color getButtonHoverBackground() {
		Color c = UIManager.getColor("Button.hoverBackground");
		if (c != null) {
			return c;
		}
		Color base = getButtonBackground();
		return isDarkTheme() ? lighten(base, 0.08f) : darken(base, 0.04f);
	}

	static Color getButtonPressedBackground() {
		Color c = UIManager.getColor("Button.pressedBackground");
		if (c == null) {
			c = UIManager.getColor("Button.select");
		}
		if (c != null) {
			return c;
		}
		Color base = getButtonBackground();
		return isDarkTheme() ? lighten(base, 0.18f) : darken(base, 0.10f);
	}

	static Color getSubLabelColor() {
		Color c = UIManager.getColor("Label.disabledForeground");
		return (c != null) ? c : (isDarkTheme() ? new Color(150, 150, 150) : new Color(130, 130, 130));
	}

	private static Color lighten(Color c, float amount) {
		int r = Math.min(255, (int) (c.getRed() + (255 - c.getRed()) * amount));
		int g = Math.min(255, (int) (c.getGreen() + (255 - c.getGreen()) * amount));
		int b = Math.min(255, (int) (c.getBlue() + (255 - c.getBlue()) * amount));
		return new Color(r, g, b, c.getAlpha());
	}

	private static Color darken(Color c, float amount) {
		int r = Math.max(0, (int) (c.getRed() * (1.0f - amount)));
		int g = Math.max(0, (int) (c.getGreen() * (1.0f - amount)));
		int b = Math.max(0, (int) (c.getBlue() * (1.0f - amount)));
		return new Color(r, g, b, c.getAlpha());
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		int w = getWidth();
		int h = getHeight();

		// Board background matches native panel background exactly
		g2.setColor(getPanelBackground());
		g2.fillRect(0, 0, w, h);

		// Top separator line matching native component border
		g2.setColor(getBorderColor());
		g2.drawLine(0, 0, w, 0);

		g2.dispose();
	}

	/**
	 * An individual virtual key button with tactile click response and repeat support.
	 * Can trigger single or compound (diagonal) ButtonName actions.
	 */
	public static class VirtualKeyButton extends JComponent {

		private static final long serialVersionUID = 1L;

		private final ButtonName[] targetButtons;
		private final String primaryLabel;
		private final String subLabel;
		private boolean isPressed = false;
		private boolean isHovered = false;
		private Timer repeatTimer;

		public VirtualKeyButton(final ButtonName buttonName, String primaryLabel, String subLabel,
				int prefWidth, int prefHeight) {
			this(new ButtonName[] { buttonName }, primaryLabel, subLabel, prefWidth, prefHeight);
		}

		public VirtualKeyButton(final ButtonName[] targetButtons, String primaryLabel, String subLabel,
				int prefWidth, int prefHeight) {
			this.targetButtons = targetButtons;
			this.primaryLabel = primaryLabel;
			this.subLabel = subLabel;

			setPreferredSize(new Dimension(prefWidth, prefHeight));
			setMinimumSize(new Dimension(prefWidth - 6, prefHeight - 4));
			setFocusable(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			initMouseListener();
		}

		@Override
		public void updateUI() {
			super.updateUI();
			repaint();
		}

		private void initMouseListener() {
			repeatTimer = new Timer(75, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (isPressed && targetButtons != null) {
						for (int i = 0; i < targetButtons.length; i++) {
							VirtualKeypadController.repeatButton(targetButtons[i]);
						}
					}
				}
			});
			repeatTimer.setInitialDelay(350);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					isPressed = true;
					repaint();
					if (targetButtons != null) {
						for (int i = 0; i < targetButtons.length; i++) {
							VirtualKeypadController.pressButton(targetButtons[i]);
						}
					}
					repeatTimer.restart();
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					stopPress();
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					isHovered = true;
					repaint();
				}

				@Override
				public void mouseExited(MouseEvent e) {
					isHovered = false;
					if (isPressed) {
						stopPress();
					} else {
						repaint();
					}
				}
			});
		}

		private void stopPress() {
			if (isPressed) {
				isPressed = false;
				repeatTimer.stop();
				if (targetButtons != null) {
					for (int i = 0; i < targetButtons.length; i++) {
						VirtualKeypadController.releaseButton(targetButtons[i]);
					}
				}
				repaint();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			int w = getWidth();
			int h = getHeight();
			int arc = 10;

			// Resolve colors dynamically from native Look-and-Feel UIManager
			Color fillColor;
			Color borderColor = getBorderColor();
			Color textColor = getButtonForeground();

			if (isPressed) {
				fillColor = getButtonPressedBackground();
			} else if (isHovered) {
				fillColor = getButtonHoverBackground();
			} else {
				fillColor = getButtonBackground();
			}

			// Draw button background with tactile press depression
			int yOffset = isPressed ? 1 : 0;
			g2.setColor(fillColor);
			g2.fillRoundRect(1, 1 + yOffset, w - 2, h - 2 - yOffset, arc, arc);

			g2.setColor(borderColor);
			g2.drawRoundRect(1, 1 + yOffset, w - 3, h - 3 - yOffset, arc, arc);

			// Draw text labels
			g2.setColor(textColor);
			if (subLabel != null && !subLabel.isEmpty()) {
				// Primary label
				g2.setFont(getFont().deriveFont(Font.BOLD, 11.5f));
				FontMetrics fmPrimary = g2.getFontMetrics();
				int px = (w - fmPrimary.stringWidth(primaryLabel)) / 2;
				int py = (h / 2) + yOffset;
				g2.drawString(primaryLabel, px, py);

				// Subtitle label (smaller text matching disabled/secondary text)
				g2.setFont(getFont().deriveFont(Font.PLAIN, 8.5f));
				g2.setColor(getSubLabelColor());
				FontMetrics fmSub = g2.getFontMetrics();
				int sx = (w - fmSub.stringWidth(subLabel)) / 2;
				int sy = (h / 2) + fmSub.getAscent() + yOffset;
				g2.drawString(subLabel, sx, sy);
			} else {
				// Single centered primary label
				float fontSize = (primaryLabel.length() > 3) ? 10.5f : 13.0f;
				g2.setFont(getFont().deriveFont(Font.BOLD, fontSize));
				FontMetrics fm = g2.getFontMetrics();
				int x = (w - fm.stringWidth(primaryLabel)) / 2;
				int y = (h - fm.getHeight()) / 2 + fm.getAscent() + yOffset;
				g2.drawString(primaryLabel, x, y);
			}

			g2.dispose();
		}
	}
}
