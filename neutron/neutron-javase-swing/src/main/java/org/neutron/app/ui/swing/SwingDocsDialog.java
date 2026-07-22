/*
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
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

public class SwingDocsDialog extends SwingDialogPanel {

	private static final long serialVersionUID = 1L;

	public SwingDocsDialog() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
		setPreferredSize(new Dimension(540, 360)); // Compact and neat size

		JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Run", createScrollPane(getRunMenuHtml()));
		tabbedPane.addTab("Config", createScrollPane(getConfigMenuHtml()));
		tabbedPane.addTab("Controls", createScrollPane(getControlsMenuHtml()));

		add(tabbedPane, BorderLayout.CENTER);
	}

	private JScrollPane createScrollPane(String htmlBody) {
		JEditorPane editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		editorPane.setEditable(false);

		// Respect Look & Feel display properties
		editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);

		// Dynamic theme colors
		Color bg = UIManager.getColor("Panel.background");
		Color fg = UIManager.getColor("Label.foreground");
		Font font = UIManager.getFont("Label.font");

		if (bg != null) editorPane.setBackground(bg);
		if (fg != null) editorPane.setForeground(fg);
		if (font != null) editorPane.setFont(font);

		// Wrap body in CSS stylesheet derived from active Look and Feel
		String styledHtml = getThemeCssHeader() + htmlBody + "</body></html>";
		editorPane.setText(styledHtml);
		editorPane.setCaretPosition(0);

		JScrollPane scrollPane = new JScrollPane(editorPane);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
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
			accent = new Color(0, 102, 204); // Fallback blue
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
				+ "body { font-family: " + fontFamily + ", sans-serif; font-size: " + (fontSize - 1) + "px; margin: 8px; line-height: 1.35; }"
				+ "h3 { color: " + toHtmlColor(accent) + "; margin-top: 4px; margin-bottom: 4px; font-size: " + (fontSize) + "px; border-bottom: 1px solid " + toHtmlColor(borderCol) + "; padding-bottom: 2px; }"
				+ "ul { margin-top: 0px; margin-bottom: 8px; padding-left: 14px; }"
				+ "li { margin-bottom: 4px; }"
				+ "b { color: " + toHtmlColor(accent) + "; }"
				+ "code { font-family: monospace; font-size: " + (fontSize - 1) + "px; }"
				+ "</style></head><body>";
	}

	private String getRunMenuHtml() {
		return "<h3>File & Execution Options</h3>"
				+ "<ul>"
				+ "  <li><b>Run JAR File</b>: Load local <code>.jar</code>/<code>.jad</code> files. Remembers last directory.</li>"
				+ "  <li><b>Run from URL</b>: Direct execution of remote MIDlets via HTTP link.</li>"
				+ "  <li><b>Connected Directories</b>:"
				+ "    <ul>"
				+ "      <li><i>Library Explorer</i>: Graphical launcher listing scanned files with titles & icons.</li>"
				+ "      <li><i>Connect New Directory</i>: Scan and register a folder to list its games in the menu.</li>"
				+ "    </ul>"
				+ "  </li>"
				+ "  <li><b>Terminate Process</b> (<code>Ctrl+W</code>): Instantly halts MIDlet execution & frees resources.</li>"
				+ "  <li><b>Installed Games</b>: MRU shortcut list of recently loaded games.</li>"
				+ "  <li><b>Open Root Directory</b>: Accesses the <code>~/.neutron</code> config & save folder in OS explorer.</li>"
				+ "  <li><b>Snapshot Manager</b>:"
				+ "    <ul>"
				+ "      <li><i>Backup</i>: Package configs, options, and RMS saves into a single <code>.zip</code> file.</li>"
				+ "      <li><i>Restore</i>: Restore all settings, directories, and saves from a backup zip file.</li>"
				+ "    </ul>"
				+ "  </li>"
				+ "  <li><b>Take Screenshot</b> (<code>Ctrl+S</code>): Captures the game screen and saves it as a <code>.png</code>.</li>"
				+ "  <li><b>Start/Stop Recording</b>: Encodes active gameplay frames into an animated <code>.gif</code>.</li>"
				+ "  <li><b>Exit</b> (<code>Ctrl+Q</code>): Saves layout settings and closes the application.</li>"
				+ "</ul>";
	}

	private String getConfigMenuHtml() {
		return "<h3>Display & Emulator Settings</h3>"
				+ "<ul>"
				+ "  <li><b>Scaled Display</b>: Launches a separate frame scaled to 2x, 3x, or 4x (great for High-DPI screens).</li>"
				+ "  <li><b>Record Store Manager</b>: View and delete keys stored in the RMS J2ME database (saves, highscores).</li>"
				+ "  <li><b>Log Console</b>: Open developer logging window to inspect output, warnings, and error traces.</li>"
				+ "  <li><b>Sleep Mode</b> (<code>Ctrl+L</code>): Pause threads on idle to drastically lower CPU usage.</li>"
				+ (org.neutron.app.Main.isFlatLafAvailable() ? "  <li><b>Theme</b>: Changes the Look-and-Feel skin style (e.g., FlatLaf macOS Dark, Dracula, System).</li>" : "")
				+ "  <li><b>Graphics Filter</b>: Renders drawing with Bilinear, Bicubic, Scale2x, CRT Scanlines, or LCD Grid.</li>"
				+ "  <li><b>Video Settings</b>: Sliders for Brightness, Contrast, Gamma, Saturation, Sharpness, Ghosting, & Invert.</li>"
				+ "  <li><b>Memory Limit</b>: Limit heap memory (32MB - 512MB). Periodically triggers GC if exceeded.</li>"
				+ "  <li><b>Mouse Coordinates</b>: Display live coordinates (X, Y) of cursor overlaying J2ME display.</li>"
				+ "</ul>";
	}

	private String getControlsMenuHtml() {
		return "<h3>Controls, Proxies & Diagnostic Tools</h3>"
				+ "<ul>"
				+ "  <li><b>Fullscreen</b>: Borderless fullscreen mode with auto-hiding top/bottom bars.</li>"
				+ "  <li><b>X-Proxy</b>: Configure SOCKS5/HTTP proxies with optional username and password auth.</li>"
				+ "  <li><b>Network Access</b>: Global safety switch to instantly cut off or allow J2ME internet requests.</li>"
				+ "  <li><b>Frame Rate</b>: Set game refresh rate limit (Unlimited, 60, 30, 20, 15 FPS) to save power.</li>"
				+ "  <li><b>Emulation Speed</b>: Adjust virtual CPU clock ticks (0.5x Slow Motion up to 8.0x Fast-Forward).</li>"
				+ "  <li><b>Network Capture</b>: Captures and inspects HTTP, socket, and datagram request logs in real-time.</li>"
				+ "  <li><b>HUD Overlay</b>: Displays real-time game FPS and heap memory usage card over the J2ME canvas.</li>"
				+ "  <li><b>Network Overlay</b>: Displays active connections and response ping status in corner.</li>"
				+ "  <li><b>Tap Automator</b>: Auto-clicker configuration for keycodes and click interval coordinates.</li>"
				+ "</ul>";
	}
}
