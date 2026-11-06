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
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.UIManager;
import org.neutron.device.impl.ButtonName;

/**
 * On-screen virtual button board providing touch and mouse-driven controls
 * for all J2ME games (8-way D-Pad, Softkeys, and full 12-key numeric keypad).
 * 
 * Fully dynamic and responsive: scales seamlessly to any resolution (from 176x220,
 * 240x320 up to 480x800+), never constraining or overriding the emulator's display size.
 */
public class SwingVirtualKeypadPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton closeButton;
	private ActionListener closeActionListener;

	public SwingVirtualKeypadPanel() {
		setLayout(new BorderLayout(6, 4));
		setBorder(BorderFactory.createEmptyBorder(4, 6, 6, 6));

		initComponents();
	}

	public void setCloseActionListener(ActionListener listener) {
		this.closeActionListener = listener;
	}

	@Override
	public void setVisible(boolean visible) {
		if (!visible) {
			releaseAllButtons();
		}
		super.setVisible(visible);
	}

	public void releaseAllButtons() {
		stopAllPresses(this);
	}

	private void stopAllPresses(java.awt.Container container) {
		for (java.awt.Component comp : container.getComponents()) {
			if (comp instanceof VirtualKeyButton) {
				((VirtualKeyButton) comp).stopPress();
			} else if (comp instanceof java.awt.Container) {
				stopAllPresses((java.awt.Container) comp);
			}
		}
	}

	@Override
	public Dimension getPreferredSize() {
		// Return 0 width so the keypad dynamically conforms to the emulator display's width
		// without imposing a minimum width constraint that would prevent resolutions like 240x320.
		return new Dimension(0, 142);
	}

	@Override
	public Dimension getMinimumSize() {
		return new Dimension(0, 0);
	}

	@Override
	public void updateUI() {
		super.updateUI();
		repaint();
	}

	private void initComponents() {
		// Top Bar: Softkeys and Close button evenly spaced via 1x3 GridLayout
		JPanel topBar = new JPanel(new GridLayout(1, 3, 5, 0));
		topBar.setOpaque(false);
		topBar.setPreferredSize(new Dimension(0, 25));

		VirtualKeyButton lskButton = new VirtualKeyButton(ButtonName.SOFT1, "Menu", null);
		VirtualKeyButton rskButton = new VirtualKeyButton(ButtonName.SOFT2, "Back", null);

		closeButton = new JButton("Close") {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				Color fill = getButtonBackground();
				Color border = getBorderColor();
				Color text = getButtonForeground();

				g2.setColor(fill);
				g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
				g2.setColor(border);
				g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);

				g2.setColor(text);
				float fontSize = Math.max(8.5f, Math.min(10.5f, getHeight() * 0.44f));
				g2.setFont(getFont().deriveFont(Font.BOLD, fontSize));
				FontMetrics fm = g2.getFontMetrics();
				int tx = (getWidth() - fm.stringWidth(getText())) / 2;
				int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
				g2.drawString(getText(), tx, ty);
				g2.dispose();
			}
		};
		closeButton.setFocusable(false);
		closeButton.setContentAreaFilled(false);
		closeButton.setBorderPainted(false);
		closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		closeButton.setToolTipText("Close virtual buttons");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (closeActionListener != null) {
					closeActionListener.actionPerformed(e);
				} else {
					setVisible(false);
				}
			}
		});

		topBar.add(lskButton);
		topBar.add(closeButton);
		topBar.add(rskButton);
		add(topBar, BorderLayout.NORTH);

		// Center body: 50% Left (3x4 Keypad), 50% Right (8-Way D-Pad)
		JPanel bodyPanel = new JPanel(new GridLayout(1, 2, 6, 0));
		bodyPanel.setOpaque(false);

		bodyPanel.add(createKeypadPanel());
		bodyPanel.add(createDPadPanel());

		add(bodyPanel, BorderLayout.CENTER);
	}

	/**
	 * Creates a complete 8-way D-Pad (Cardinals + Diagonals + Center OK/SELECT)
	 * dynamically fitting within a 3x3 responsive grid.
	 */
	private JPanel createDPadPanel() {
		JPanel panel = new JPanel(new GridLayout(3, 3, 2, 2));
		panel.setOpaque(false);

		// Top row: Top-Left (↖), Up (▲), Top-Right (↗)
		VirtualKeyButton upLeft = new VirtualKeyButton(new ButtonName[] { ButtonName.UP, ButtonName.LEFT }, "↖", null);
		VirtualKeyButton up = new VirtualKeyButton(ButtonName.UP, "▲", null);
		VirtualKeyButton upRight = new VirtualKeyButton(new ButtonName[] { ButtonName.UP, ButtonName.RIGHT }, "↗", null);

		// Middle row: Left (◀), Center OK (OK), Right (▶)
		VirtualKeyButton left = new VirtualKeyButton(ButtonName.LEFT, "◀", null);
		VirtualKeyButton ok = new VirtualKeyButton(ButtonName.SELECT, "OK", null);
		VirtualKeyButton right = new VirtualKeyButton(ButtonName.RIGHT, "▶", null);

		// Bottom row: Bottom-Left (↙), Down (▼), Bottom-Right (↘)
		VirtualKeyButton downLeft = new VirtualKeyButton(new ButtonName[] { ButtonName.DOWN, ButtonName.LEFT }, "↙", null);
		VirtualKeyButton down = new VirtualKeyButton(ButtonName.DOWN, "▼", null);
		VirtualKeyButton downRight = new VirtualKeyButton(new ButtonName[] { ButtonName.DOWN, ButtonName.RIGHT }, "↘", null);

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
	 * Creates the complete 3x4 mobile phone numeric keypad (1-9, *, 0, #).
	 */
	private JPanel createKeypadPanel() {
		JPanel grid = new JPanel(new GridLayout(4, 3, 2, 2));
		grid.setOpaque(false);

		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM1, "1", null));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM2, "2", null));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM3, "3", null));

		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM4, "4", null));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM5, "5", null));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM6, "6", null));

		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM7, "7", null));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM8, "8", null));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM9, "9", null));

		grid.add(new VirtualKeyButton(ButtonName.KEY_STAR, "*", null));
		grid.add(new VirtualKeyButton(ButtonName.KEY_NUM0, "0", null));
		grid.add(new VirtualKeyButton(ButtonName.KEY_POUND, "#", null));

		return grid;
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
	 * Dynamically sizes and scales fonts based on current component bounds.
	 */
	public static class VirtualKeyButton extends JComponent {

		private static final long serialVersionUID = 1L;

		private final ButtonName[] targetButtons;
		private final String primaryLabel;
		private final String subLabel;
		private boolean isPressed = false;
		private boolean isHovered = false;
		private Timer repeatTimer;

		public VirtualKeyButton(final ButtonName buttonName, String primaryLabel, String subLabel) {
			this(new ButtonName[] { buttonName }, primaryLabel, subLabel);
		}

		public VirtualKeyButton(final ButtonName[] targetButtons, String primaryLabel, String subLabel) {
			this.targetButtons = targetButtons;
			this.primaryLabel = primaryLabel;
			this.subLabel = subLabel;

			setFocusable(false);
			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

			initMouseListener();
		}

		@Override
		public Dimension getMinimumSize() {
			return new Dimension(0, 0);
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

		void stopPress() {
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
			if (w <= 0 || h <= 0) {
				g2.dispose();
				return;
			}

			int arc = Math.max(6, Math.min(10, h / 3));

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

			// Draw text labels with dynamically scaled font sizes
			g2.setColor(textColor);
			if (subLabel != null && !subLabel.isEmpty()) {
				// Primary label
				float fontSize = Math.max(9.0f, Math.min(12.0f, h * 0.40f));
				g2.setFont(getFont().deriveFont(Font.BOLD, fontSize));
				FontMetrics fmPrimary = g2.getFontMetrics();
				int px = (w - fmPrimary.stringWidth(primaryLabel)) / 2;
				int py = (h * 42 / 100) + yOffset;
				g2.drawString(primaryLabel, px, py);

				// Subtitle label (smaller text matching disabled/secondary text)
				float subFontSize = Math.max(7.5f, Math.min(9.5f, h * 0.28f));
				g2.setFont(getFont().deriveFont(Font.PLAIN, subFontSize));
				g2.setColor(getSubLabelColor());
				FontMetrics fmSub = g2.getFontMetrics();
				int sx = (w - fmSub.stringWidth(subLabel)) / 2;
				int sy = py + fmSub.getAscent() + 1;
				g2.drawString(subLabel, sx, sy);
			} else {
				// Single centered primary label
				float fontSize = (primaryLabel.length() > 3) 
						? Math.max(9.0f, Math.min(13.0f, h * 0.38f))
						: Math.max(9.5f, Math.min(13.5f, h * 0.44f));
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
