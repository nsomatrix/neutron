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
 */

package org.neutron.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.neutron.app.Config;
import org.neutron.app.util.BuildVersion;
import org.neutron.log.Logger;

/**
 * An industry-standard utility to check for emulator updates.
 * Performs asynchronous network request to GitHub Releases API.
 */
public class UpdateChecker {

	private static final String GITHUB_API_URL = "https://api.github.com/repos/nsomatrix/neutron/releases/latest";
	private static final String FALLBACK_RELEASE_URL = "https://github.com/nsomatrix/neutron/releases";

	public static class UpdateInfo {
		public final String tagName;
		public final String htmlUrl;
		public final String body;

		public UpdateInfo(String tagName, String htmlUrl, String body) {
			this.tagName = tagName;
			this.htmlUrl = htmlUrl;
			this.body = body;
		}
	}

	public static void checkForUpdates(final JFrame parent, final boolean silentOnNoUpdates) {
		final JDialog progressDialog = new JDialog(parent, "Check for Updates", true);
		progressDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		progressDialog.setLayout(new BorderLayout(15, 15));

		JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 15, 20));

		JLabel statusLabel = new JLabel("Checking for latest updates from GitHub...");
		contentPanel.add(statusLabel, BorderLayout.NORTH);

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		contentPanel.add(progressBar, BorderLayout.CENTER);

		progressDialog.add(contentPanel, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		progressDialog.add(buttonPanel, BorderLayout.SOUTH);

		final SwingWorker<UpdateInfo, Void> worker = new SwingWorker<UpdateInfo, Void>() {
			@Override
			protected UpdateInfo doInBackground() throws Exception {
				URL url = new URL(GITHUB_API_URL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection(Config.getProxyInstance());
				conn.setRequestMethod("GET");
				conn.setRequestProperty("User-Agent", "Neutron-Emulator/" + BuildVersion.getVersion());
				conn.setConnectTimeout(8000);
				conn.setReadTimeout(8000);

				int responseCode = conn.getResponseCode();
				if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
					throw new Exception("No releases found for this repository on GitHub.");
				}
				if (responseCode != HttpURLConnection.HTTP_OK) {
					throw new Exception("Server returned HTTP response code: " + responseCode);
				}

				BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
				}
				reader.close();

				String json = sb.toString();
				String tagName = null;
				String htmlUrl = null;
				String body = null;

				Pattern tagPattern = Pattern.compile("\"tag_name\"\\s*:\\s*\"([^\"]+)\"");
				Matcher tagMatcher = tagPattern.matcher(json);
				if (tagMatcher.find()) {
					tagName = tagMatcher.group(1);
				}

				Pattern urlPattern = Pattern.compile("\"html_url\"\\s*:\\s*\"([^\"]+)\"");
				Matcher urlMatcher = urlPattern.matcher(json);
				if (urlMatcher.find()) {
					htmlUrl = urlMatcher.group(1);
				}

				Pattern bodyPattern = Pattern.compile("\"body\"\\s*:\\s*\"([^\"]+)\"");
				Matcher bodyMatcher = bodyPattern.matcher(json);
				if (bodyMatcher.find()) {
					body = bodyMatcher.group(1);
					// Simple decode of release notes newlines
					body = body.replace("\\r\\n", "\n").replace("\\n", "\n").replace("\\t", "\t");
				}

				if (tagName == null || htmlUrl == null) {
					throw new Exception("Could not parse release information from response.");
				}

				return new UpdateInfo(tagName, htmlUrl, body);
			}

			@Override
			protected void done() {
				if (progressDialog.isVisible()) {
					progressDialog.dispose();
				}

				if (isCancelled()) {
					return;
				}

				try {
					UpdateInfo info = get();
					String currentVersion = BuildVersion.getVersion();
					
					if (isNewerVersion(currentVersion, info.tagName)) {
						String message = "A new version of Neutron is available!\n\n"
								+ "Current Version: " + currentVersion + "\n"
								+ "Latest Version: " + info.tagName + "\n\n"
								+ "Would you like to open the download page?";
						
						int option = JOptionPane.showConfirmDialog(parent, message, "Update Available",
								JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
						
						if (option == JOptionPane.YES_OPTION) {
							openBrowser(info.htmlUrl);
						}
					} else {
						if (!silentOnNoUpdates) {
							JOptionPane.showMessageDialog(parent,
									"You are running the latest version of Neutron (" + currentVersion + ").",
									"Up to Date", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				} catch (Exception e) {
					Logger.error("Failed to check for updates", e);
					if (!silentOnNoUpdates) {
						String errMsg = e.getCause() != null ? e.getCause().getMessage() : e.getMessage();
						JOptionPane.showMessageDialog(parent,
								"Failed to check for updates:\n" + errMsg,
								"Update Check Failed", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		};

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				worker.cancel(true);
				progressDialog.dispose();
			}
		});

		// Position and show progress dialog
		progressDialog.pack();
		progressDialog.setResizable(false);
		progressDialog.setSize(380, 160);
		progressDialog.setLocationRelativeTo(parent);

		// Start background task
		worker.execute();

		progressDialog.setVisible(true);
	}

	public static boolean isNewerVersion(String currentStr, String latestStr) {
		if (currentStr == null || latestStr == null) {
			return false;
		}

		String normCurrent = normalizeVersion(currentStr);
		String normLatest = normalizeVersion(latestStr);

		String[] currentParts = normCurrent.split("\\.");
		String[] latestParts = normLatest.split("\\.");

		int length = Math.max(currentParts.length, latestParts.length);
		for (int i = 0; i < length; i++) {
			int curr = i < currentParts.length ? parseOrZero(currentParts[i]) : 0;
			int lat = i < latestParts.length ? parseOrZero(latestParts[i]) : 0;
			if (lat > curr) {
				return true;
			} else if (curr > lat) {
				return false;
			}
		}
		return false;
	}

	private static String normalizeVersion(String ver) {
		if (ver.startsWith("v") || ver.startsWith("V")) {
			ver = ver.substring(1);
		}
		// Replace SNAPSHOT/letters with dots or keep only digits/dots
		ver = ver.replaceAll("[^0-9\\.]", ".");
		ver = ver.replaceAll("\\.+", ".");
		if (ver.endsWith(".")) {
			ver = ver.substring(0, ver.length() - 1);
		}
		if (ver.startsWith(".")) {
			ver = ver.substring(1);
		}
		return ver;
	}

	private static int parseOrZero(String part) {
		try {
			return Integer.parseInt(part);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private static void openBrowser(String url) {
		try {
			if (Desktop.isDesktopSupported()) {
				Desktop.getDesktop().browse(new URI(url));
			} else {
				Runtime.getRuntime().exec("xdg-open " + url);
			}
		} catch (Exception ex) {
			Logger.error("Failed to open browser", ex);
		}
	}
}
