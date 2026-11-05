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
 * for all J2ME games (D-Pad, Softkeys, and full 12-key numeric keypad).
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

	private void initComponents() {
		// Top Bar: Softkeys and mode switcher
		JPanel topBar = new JPanel(new BorderLayout(12, 0));
		topBar.setOpaque(false);

		VirtualKeyButton lskButton = new VirtualKeyButton(ButtonName.SOFT1, "LSK", "Menu", 74, 28, false);
		VirtualKeyButton rskButton = new VirtualKeyButton(ButtonName.SOFT2, "RSK", "Back", 74, 28, false);

		modeToggleButton = new JButton(isFullKeypadMode ? "Mode: Full Keypad" : "Mode: Game Pad") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				boolean dark = isDarkTheme();
				g2.setColor(dark ? new Color(50, 54, 64) : new Color(225, 228, 235));
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
				g2.setColor(dark ? new Color(75, 80, 95) : new Color(195, 200, 210));
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
				g2.setColor(dark ? new Color(190, 195, 205) : new Color(60, 65, 75));
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

		// Center body: Left is D-Pad, Right is Action Cluster / Numeric Keypad
		JPanel bodyPanel = new JPanel();
		bodyPanel.setLayout(new BoxLayout(bodyPanel, BoxLayout.X_AXIS));
		bodyPanel.setOpaque(false);

		// 1. D-Pad Section
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
	 * Creates a classic 3x3 D-Pad with directional arrows and center FIRE/SELECT.
	 */
	private JPanel createDPadPanel() {
		JPanel panel = new JPanel(new GridLayout(3, 3, 3, 3));
		panel.setOpaque(false);
		panel.setPreferredSize(new Dimension(135, 115));
		panel.setMaximumSize(new Dimension(145, 125));

		VirtualKeyButton up = new VirtualKeyButton(ButtonName.UP, "▲", null, 42, 36, false);
		VirtualKeyButton down = new VirtualKeyButton(ButtonName.DOWN, "▼", null, 42, 36, false);
		VirtualKeyButton left = new VirtualKeyButton(ButtonName.LEFT, "◀", null, 42, 36, false);
		VirtualKeyButton right = new VirtualKeyButton(ButtonName.RIGHT, "▶", null, 42, 36, false);
		VirtualKeyButton fire = new VirtualKeyButton(ButtonName.SELECT, "FIRE", "OK", 44, 36, true);

		panel.add(new EmptyCell());
		panel.add(up);
		panel.add(new EmptyCell());

		panel.add(left);
		panel.add(fire);
		panel.add(right);

		panel.add(new EmptyCell());
		panel.add(down);
		panel.add(new EmptyCell());

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

		VirtualKeyButton key5 = new VirtualKeyButton(ButtonName.KEY_NUM5, "5", "Action", 56, 44, true);
		VirtualKeyButton clear = new VirtualKeyButton(ButtonName.DELETE, "C", "Del", 56, 44, false);
		VirtualKeyButton star = new VirtualKeyButton(ButtonName.KEY_STAR, "*", null, 56, 44, false);
		VirtualKeyButton pound = new VirtualKeyButton(ButtonName.KEY_POUND, "#", null, 56, 44, false);

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

		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM1, "1", null, 38, 26, false));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM2, "2", "ABC", 38, 26, false));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM3, "3", "DEF", 38, 26, false));

		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM4, "4", "GHI", 38, 26, false));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM5, "5", "JKL", 38, 26, true));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM6, "6", "MNO", 38, 26, false));

		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM7, "7", "PQRS", 38, 26, false));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM8, "8", "TUV", 38, 26, false));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM9, "9", "WXYZ", 38, 26, false));

		grid.add(new VirtualKeyButton(ButtonName.KEY_STAR, "*", null, 38, 26, false));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM0, "0", "+", 38, 26, false));
		grid.add(new VirtualKeyButton(ButtonName.KEY_POUND, "#", null, 38, 26, false));

		wrapper.add(grid, BorderLayout.CENTER);

		JPanel sideBar = new JPanel(new GridLayout(2, 1, 0, 3));
		sideBar.setOpaque(false);
		sideBar.add(new VirtualKeyButton(ButtonName.DELETE, "C", "Del", 34, 46, false));
		wrapper.add(sideBar, BorderLayout.EAST);

		return wrapper;
	}

	private static boolean isDarkTheme() {
		Color bg = UIManager.getColor("Panel.background");
		if (bg != null) {
			double luminance = (0.299 * bg.getRed() + 0.587 * bg.getGreen() + 0.114 * bg.getBlue()) / 255.0;
			return luminance < 0.5;
		}
		return true;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		boolean dark = isDarkTheme();
		int w = getWidth();
		int h = getHeight();

		// Board background
		Color bg = dark ? new Color(24, 26, 31, 245) : new Color(242, 244, 248, 245);
		g2.setColor(bg);
		g2.fillRect(0, 0, w, h);

		// Top border separator line
		Color border = dark ? new Color(48, 52, 62) : new Color(215, 218, 226);
		g2.setColor(border);
		g2.drawLine(0, 0, w, 0);

		g2.dispose();
	}

	/**
	 * An individual virtual key button with tactile click response and repeat support.
	 */
	public static class VirtualKeyButton extends JComponent {

		private static final long serialVersionUID = 1L;

		private final ButtonName buttonName;
		private final String primaryLabel;
		private final String subLabel;
		private final boolean isAccent;
		private boolean isPressed = false;
		private boolean isHovered = false;
		private Timer repeatTimer;

		public VirtualKeyButton(final ButtonName buttonName, String primaryLabel, String subLabel,
				int prefWidth, int prefHeight, boolean isAccent) {
			this.buttonName = buttonName;
			this.primaryLabel = primaryLabel;
			this.subLabel = subLabel;
			this.isAccent = isAccent;

			setPreferredSize(new Dimension(prefWidth, prefHeight));
			setMinimumSize(new Dimension(prefWidth - 6, prefHeight - 4));
			setFocusable(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			initMouseListener();
		}

		private void initMouseListener() {
			repeatTimer = new Timer(75, new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (isPressed) {
						VirtualKeypadController.repeatButton(buttonName);
					}
				}
			});
			repeatTimer.setInitialDelay(350);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					isPressed = true;
					repaint();
					VirtualKeypadController.pressButton(buttonName);
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
				VirtualKeypadController.releaseButton(buttonName);
				repaint();
			}
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			boolean dark = isDarkTheme();
			int w = getWidth();
			int h = getHeight();
			int arc = 10;

			// Determine button background color
			Color fillColor;
			Color borderColor;
			Color textColor;

			if (isAccent) {
				if (isPressed) {
					fillColor = new Color(29, 78, 216); // Deep blue
					borderColor = new Color(59, 130, 246);
				} else if (isHovered) {
					fillColor = new Color(37, 99, 235);
					borderColor = new Color(96, 165, 250);
				} else {
					fillColor = new Color(30, 64, 175);
					borderColor = new Color(59, 130, 246);
				}
				textColor = Color.WHITE;
			} else {
				if (dark) {
					if (isPressed) {
						fillColor = new Color(70, 76, 92);
						borderColor = new Color(110, 118, 140);
						textColor = Color.WHITE;
					} else if (isHovered) {
						fillColor = new Color(48, 52, 64);
						borderColor = new Color(80, 86, 102);
						textColor = new Color(230, 235, 245);
					} else {
						fillColor = new Color(36, 39, 48);
						borderColor = new Color(60, 65, 78);
						textColor = new Color(210, 215, 225);
					}
				} else {
					if (isPressed) {
						fillColor = new Color(205, 212, 226);
						borderColor = new Color(150, 160, 180);
						textColor = new Color(15, 20, 30);
					} else if (isHovered) {
						fillColor = new Color(250, 252, 255);
						borderColor = new Color(180, 188, 205);
						textColor = new Color(30, 35, 45);
					} else {
						fillColor = new Color(255, 255, 255);
						borderColor = new Color(205, 210, 222);
						textColor = new Color(40, 45, 55);
					}
				}
			}

			// Draw button background
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

				// Subtitle label (smaller text)
				g2.setFont(getFont().deriveFont(Font.PLAIN, 8.5f));
				g2.setColor(isAccent ? new Color(210, 230, 255) : (dark ? new Color(145, 150, 165) : new Color(120, 125, 140)));
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

	private static class EmptyCell extends JPanel {
		public EmptyCell() {
			setOpaque(false);
		}
	}
}

