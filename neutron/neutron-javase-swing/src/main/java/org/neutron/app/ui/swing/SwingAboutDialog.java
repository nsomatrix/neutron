/**
 *  Neutron
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 *
 *  @version $Id$
 */
package org.neutron.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.neutron.app.Main;
import org.neutron.app.util.BuildVersion;

/**
 * @author vlads
 * 
 */
public class SwingAboutDialog extends SwingDialogPanel {

	private static final long serialVersionUID = 1L;

	public SwingAboutDialog() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setPreferredSize(new Dimension(500, 320));

		// 1. Header with icon and title info
		JPanel headerPanel = new JPanel(new GridBagLayout());
		headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridheight = 2;
		c.insets = new Insets(0, 0, 0, 15);
		c.anchor = GridBagConstraints.CENTER;
		JLabel iconLabel = new JLabel();
		iconLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				Main.class.getResource("/org/neutron/icon.png"))));
		headerPanel.add(iconLabel, c);

		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 1;
		c.weightx = 1.0;
		c.insets = new Insets(0, 0, 2, 0);
		c.anchor = GridBagConstraints.WEST;
		JLabel titleLabel = new JLabel("Neutron");
		titleLabel.setFont(new Font("Default", Font.BOLD, 22));
		headerPanel.add(titleLabel, c);

		c.gridy = 1;
		c.weightx = 1.0;
		c.insets = new Insets(0, 0, 0, 0);
		JLabel subtitleLabel = new JLabel("Modern J2ME Emulation Platform  |  v" + BuildVersion.getVersion());
		subtitleLabel.setFont(new Font("Default", Font.PLAIN, 12));
		Color fg = UIManager.getColor("Label.foreground");
		if (fg != null) {
			subtitleLabel.setForeground(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 180));
		}
		headerPanel.add(subtitleLabel, c);

		add(headerPanel, BorderLayout.NORTH);

		// 2. Tabbed pane for Overview and Credits
		JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Overview", createScrollPane(getOverviewHtml()));
		tabbedPane.addTab("Credits", createScrollPane(getCreditsHtml()));

		add(tabbedPane, BorderLayout.CENTER);
	}

	private JScrollPane createScrollPane(String htmlBody) {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);
		editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

		// Apply theme colors
		Color bg = UIManager.getColor("Panel.background");
		Color fg = UIManager.getColor("Label.foreground");
		Font font = UIManager.getFont("Label.font");

		if (bg != null) editorPane.setBackground(bg);
		if (fg != null) editorPane.setForeground(fg);
		if (font != null) editorPane.setFont(font);

		// Combine styling header and body
		String styledHtml = getThemeCssHeader() + htmlBody + "</body></html>";
		editorPane.setText(styledHtml);
		editorPane.setCaretPosition(0);

		// Enable clickable links asynchronously to prevent blocking the Event Dispatch Thread (EDT)
		editorPane.addHyperlinkListener(new HyperlinkListener() {
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					final String desc = e.getDescription();
					if (desc != null && (desc.startsWith("http://") || desc.startsWith("https://"))) {
						new Thread(new Runnable() {
							public void run() {
								try {
									if (Desktop.isDesktopSupported()) {
										Desktop.getDesktop().browse(new java.net.URI(desc));
									}
								} catch (Exception ex) {
									ex.printStackTrace();
								}
							}
						}).start();
					}
				}
			}
		});

		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		return scrollPane;
	}

	private static String toHtmlColor(Color color) {
		if (color == null) {
			return "#000000";
		}
		return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
	}

	private String getThemeCssHeader() {
		Color fg = UIManager.getColor("Label.foreground");
		Color accent = UIManager.getColor("textHighlight");
		if (accent == null || accent.equals(fg)) {
			accent = UIManager.getColor("Link.foreground");
		}
		if (accent == null) {
			accent = new Color(0, 102, 204);
		}

		Color borderCol = UIManager.getColor("Separator.foreground");
		if (borderCol == null) {
			borderCol = UIManager.getColor("Separator.background");
		}
		if (borderCol == null) {
			borderCol = new Color(200, 200, 200);
		}

		Font font = UIManager.getFont("Label.font");
		String fontFamily = font != null ? font.getFamily() : "sans-serif";
		int fontSize = font != null ? font.getSize() : 12;

		return "<html><head><style>"
				+ "body { font-family: " + fontFamily + ", sans-serif; font-size: " + (fontSize - 1) + "px; margin: 4px; line-height: 1.4; }"
				+ "h3 { color: " + toHtmlColor(accent) + "; margin-top: 2px; margin-bottom: 6px; font-size: " + fontSize + "px; border-bottom: 1px solid " + toHtmlColor(borderCol) + "; padding-bottom: 2px; }"
				+ "p { margin-top: 0px; margin-bottom: 8px; }"
				+ "ul { margin-top: 0px; margin-bottom: 8px; padding-left: 16px; }"
				+ "li { margin-bottom: 4px; }"
				+ "b { color: " + toHtmlColor(accent) + "; }"
				+ "a { color: " + toHtmlColor(accent) + "; text-decoration: none; }"
				+ "a:hover { text-decoration: underline; }"
				+ "table { width: 100%; border-collapse: collapse; margin-top: 2px; }"
				+ "td { padding: 4px 6px; font-size: " + (fontSize - 1) + "px; }"
				+ "td.label { font-weight: bold; width: 35%; color: " + toHtmlColor(fg) + "; opacity: 0.8; }"
				+ "td.value { font-family: monospace; }"
				+ "</style></head><body>";
	}

	private String getOverviewHtml() {
		return "<h3>Application Overview</h3>"
				+ "<p><b>Neutron</b> is a high-performance, cross-platform J2ME (Java 2 Micro Edition) emulator designed to execute classic mobile applications and games on modern desktop environments.</p>"
				+ "<p>Equipped with graphics scaling, advanced network options, proxy controls, dynamic look-and-feel support, and screen recording capabilities.</p>"
				+ "<p><b>Project Links:</b></p>"
				+ "<ul>"
				+ "  <li>Website & Repo: <a href=\"https://github.com/nsomatrix/neutron\">https://github.com/nsomatrix/neutron</a></li>"
				+ "  <li>Report an Issue: <a href=\"https://github.com/nsomatrix/neutron/issues\">https://github.com/nsomatrix/neutron/issues</a></li>"
				+ "</ul>";
	}

	private String getCreditsHtml() {
		return "<h3>Credits & Attributions</h3>"
				+ "<p>Neutron is built on the foundations of the open-source <b>MicroEmulator</b> project and extends it with modern features, user experience improvements, and platform isolation.</p>"
				+ "<p>Special thanks to the open-source libraries that make this possible:</p>"
				+ "<ul>"
				+ "  <li><b>FlatLaf</b> - Flat Look and Feel theme engine.</li>"
				+ "  <li><b>ProGuard</b> - Shrinker and code optimizer.</li>"
				+ "</ul>"
				+ "<p>Licensed dual-alternatively under the <b>GNU Lesser General Public License (LGPL) v2.1</b> (or newer) and the <b>Apache License v2.0</b>.</p>";
	}
}

