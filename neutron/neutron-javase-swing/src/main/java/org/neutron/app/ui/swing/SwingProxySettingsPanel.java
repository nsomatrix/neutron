/*
 *  Neutron
 *  Copyright (C) 2026 Bartek Teodorczyk <barteo@barteo.net>
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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.neutron.app.Config;

public class SwingProxySettingsPanel extends SwingDialogPanel {

	private static final long serialVersionUID = 1L;

	private JCheckBox proxyEnabledCheckBox;
	private JComboBox proxyTypeComboBox;
	private JTextField proxyHostField;
	private JTextField proxyPortField;
	private JCheckBox proxyAuthCheckBox;
	private JTextField proxyUsernameField;
	private JPasswordField proxyPasswordField;

	private JButton testButton;
	private JLabel statusLabel;

	// Keep track of original values to restore on cancel
	private final boolean originalEnabled;
	private final String originalType;
	private final String originalHost;
	private final int originalPort;
	private final boolean originalAuth;
	private final String originalUsername;
	private final String originalPassword;

	public SwingProxySettingsPanel() {
		// Read current configuration
		originalEnabled = Config.isProxyEnabled();
		originalType = Config.getProxyType();
		originalHost = Config.getProxyHost();
		originalPort = Config.getProxyPort();
		originalAuth = Config.isProxyAuthEnabled();
		originalUsername = Config.getProxyUsername();
		originalPassword = Config.getProxyPassword();

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(8, 12, 8, 12);
		c.fill = GridBagConstraints.HORIZONTAL;

		// 1. Enable Proxy Checkbox
		proxyEnabledCheckBox = new JCheckBox("Enable network proxy for games", originalEnabled);
		proxyEnabledCheckBox.setFont(proxyEnabledCheckBox.getFont().deriveFont(java.awt.Font.BOLD));
		c.gridy = 0;
		c.gridx = 0;
		c.gridwidth = 2;
		add(proxyEnabledCheckBox, c);

		// 2. Server Details Panel
		JPanel serverPanel = new JPanel(new GridBagLayout());
		serverPanel.setBorder(BorderFactory.createTitledBorder("Proxy Server Details"));
		GridBagConstraints sc = new GridBagConstraints();
		sc.insets = new Insets(4, 8, 4, 8);
		sc.fill = GridBagConstraints.HORIZONTAL;

		sc.gridy = 0;
		sc.gridx = 0;
		sc.weightx = 0.0;
		serverPanel.add(new JLabel("Proxy Type:"), sc);

		proxyTypeComboBox = new JComboBox(new String[] { "HTTP", "SOCKS" });
		proxyTypeComboBox.setSelectedItem(originalType);
		sc.gridx = 1;
		sc.weightx = 1.0;
		serverPanel.add(proxyTypeComboBox, sc);

		sc.gridy = 1;
		sc.gridx = 0;
		sc.weightx = 0.0;
		serverPanel.add(new JLabel("Proxy Host:"), sc);

		proxyHostField = new JTextField(originalHost, 18);
		sc.gridx = 1;
		sc.weightx = 1.0;
		serverPanel.add(proxyHostField, sc);

		sc.gridy = 2;
		sc.gridx = 0;
		sc.weightx = 0.0;
		serverPanel.add(new JLabel("Proxy Port:"), sc);

		proxyPortField = new JTextField(String.valueOf(originalPort), 6);
		sc.gridx = 1;
		sc.weightx = 1.0;
		serverPanel.add(proxyPortField, sc);

		c.gridy = 1;
		c.gridx = 0;
		c.gridwidth = 2;
		add(serverPanel, c);

		// 3. Authentication Panel
		JPanel authPanel = new JPanel(new GridBagLayout());
		authPanel.setBorder(BorderFactory.createTitledBorder("Proxy Authentication"));
		GridBagConstraints ac = new GridBagConstraints();
		ac.insets = new Insets(4, 8, 4, 8);
		ac.fill = GridBagConstraints.HORIZONTAL;

		proxyAuthCheckBox = new JCheckBox("Proxy requires authentication", originalAuth);
		ac.gridy = 0;
		ac.gridx = 0;
		ac.gridwidth = 2;
		authPanel.add(proxyAuthCheckBox, ac);

		ac.gridy = 1;
		ac.gridx = 0;
		ac.gridwidth = 1;
		ac.weightx = 0.0;
		authPanel.add(new JLabel("Username:"), ac);

		proxyUsernameField = new JTextField(originalUsername, 18);
		ac.gridx = 1;
		ac.weightx = 1.0;
		authPanel.add(proxyUsernameField, ac);

		ac.gridy = 2;
		ac.gridx = 0;
		ac.weightx = 0.0;
		authPanel.add(new JLabel("Password:"), ac);

		proxyPasswordField = new JPasswordField(originalPassword, 18);
		ac.gridx = 1;
		ac.weightx = 1.0;
		authPanel.add(proxyPasswordField, ac);

		c.gridy = 2;
		c.gridx = 0;
		c.gridwidth = 2;
		add(authPanel, c);

		// 4. Test connection row
		JPanel testPanel = new JPanel(new BorderLayout(10, 0));
		testButton = new JButton("Test Proxy Connection");
		statusLabel = new JLabel("Click to verify proxy configuration.");
		statusLabel.setPreferredSize(new Dimension(280, 20));
		testPanel.add(testButton, BorderLayout.WEST);
		testPanel.add(statusLabel, BorderLayout.CENTER);

		c.gridy = 3;
		c.gridx = 0;
		c.gridwidth = 2;
		c.insets = new Insets(4, 12, 12, 12);
		add(testPanel, c);

		// Listeners to update field state
		ActionListener stateUpdater = new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateFieldsEnabledState();
			}
		};
		proxyEnabledCheckBox.addActionListener(stateUpdater);
		proxyAuthCheckBox.addActionListener(stateUpdater);

		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				testProxyConnection();
			}
		});

		updateFieldsEnabledState();
	}

	private void updateFieldsEnabledState() {
		boolean proxyEnabled = proxyEnabledCheckBox.isSelected();
		proxyTypeComboBox.setEnabled(proxyEnabled);
		proxyHostField.setEnabled(proxyEnabled);
		proxyPortField.setEnabled(proxyEnabled);
		proxyAuthCheckBox.setEnabled(proxyEnabled);

		boolean authEnabled = proxyEnabled && proxyAuthCheckBox.isSelected();
		proxyUsernameField.setEnabled(authEnabled);
		proxyPasswordField.setEnabled(authEnabled);

		testButton.setEnabled(proxyEnabled);
		if (!proxyEnabled) {
			statusLabel.setText("Proxy is disabled.");
			statusLabel.setForeground(Color.GRAY);
		} else {
			statusLabel.setText("Click to verify proxy configuration.");
			statusLabel.setForeground(UIManager.getLookAndFeelDefaults().getColor("Label.foreground"));
		}
	}

	private void testProxyConnection() {
		final String host = proxyHostField.getText().trim();
		if (host.isEmpty()) {
			statusLabel.setText("Error: Host is empty.");
			statusLabel.setForeground(Color.RED);
			return;
		}

		int portVal = 8080;
		try {
			portVal = Integer.parseInt(proxyPortField.getText().trim());
			if (portVal < 1 || portVal > 65535) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			statusLabel.setText("Error: Port must be 1 - 65535.");
			statusLabel.setForeground(Color.RED);
			return;
		}

		final int port = portVal;
		final String typeStr = (String) proxyTypeComboBox.getSelectedItem();
		final boolean authEnabled = proxyAuthCheckBox.isSelected();
		final String username = proxyUsernameField.getText().trim();
		final String password = new String(proxyPasswordField.getPassword());

		testButton.setEnabled(false);
		statusLabel.setText("Testing connection...");
		statusLabel.setForeground(Color.BLUE);

		// Store dialog values temporarily in Config for Authenticator
		Config.setProxyEnabled(true);
		Config.setProxyType(typeStr);
		Config.setProxyHost(host);
		Config.setProxyPort(port);
		Config.setProxyAuthEnabled(authEnabled);
		Config.setProxyUsername(username);
		Config.setProxyPassword(password);

		new Thread(new Runnable() {
			public void run() {
				String errorMsg = null;
				boolean success = false;
				java.net.HttpURLConnection conn = null;
				try {
					java.net.Proxy proxy = Config.getProxyInstance();
					// Use clients3.google.com/generate_204 as the standard check URL
					java.net.URL url = new java.net.URL("http://clients3.google.com/generate_204");
					conn = (java.net.HttpURLConnection) url.openConnection(proxy);
					conn.setConnectTimeout(8000);
					conn.setReadTimeout(8000);
					conn.connect();
					int code = conn.getResponseCode();
					if (code >= 200 && code < 400) {
						success = true;
					} else {
						errorMsg = "HTTP Response Code " + code;
					}
				} catch (Exception ex) {
					errorMsg = ex.getMessage();
					if (errorMsg == null || errorMsg.trim().isEmpty()) {
						errorMsg = ex.toString();
					}
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
					// Restore original config values temporarily
					restoreConfig(originalEnabled, originalType, originalHost, originalPort, originalAuth, originalUsername, originalPassword);
				}

				final boolean testSuccess = success;
				final String testError = errorMsg;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						testButton.setEnabled(true);
						if (testSuccess) {
							statusLabel.setText("Connection Successful!");
							statusLabel.setForeground(new Color(0, 128, 0));
						} else {
							statusLabel.setText("Connection Failed: " + testError);
							statusLabel.setForeground(Color.RED);
						}
					}
				});
			}
		}).start();
	}

	private void restoreConfig(boolean enabled, String type, String host, int port, boolean auth, String username, String password) {
		Config.setProxyEnabled(enabled);
		Config.setProxyType(type);
		Config.setProxyHost(host);
		Config.setProxyPort(port);
		Config.setProxyAuthEnabled(auth);
		Config.setProxyUsername(username);
		Config.setProxyPassword(password);
	}

	public boolean check(boolean showErrors) {
		if (proxyEnabledCheckBox.isSelected()) {
			String host = proxyHostField.getText().trim();
			if (host.isEmpty()) {
				if (showErrors) {
					JOptionPane.showMessageDialog(this, "Proxy Host cannot be empty.", "Validation Error", JOptionPane.ERROR_MESSAGE);
				}
				return false;
			}
			try {
				int port = Integer.parseInt(proxyPortField.getText().trim());
				if (port < 1 || port > 65535) {
					throw new NumberFormatException();
				}
			} catch (NumberFormatException e) {
				if (showErrors) {
					JOptionPane.showMessageDialog(this, "Proxy Port must be a valid number between 1 and 65535.", "Validation Error", JOptionPane.ERROR_MESSAGE);
				}
				return false;
			}
			if (proxyAuthCheckBox.isSelected()) {
				String user = proxyUsernameField.getText().trim();
				if (user.isEmpty()) {
					if (showErrors) {
						JOptionPane.showMessageDialog(this, "Username cannot be empty when authentication is enabled.", "Validation Error", JOptionPane.ERROR_MESSAGE);
					}
					return false;
				}
			}
		}

		// Save the final settings to configuration
		boolean enabled = proxyEnabledCheckBox.isSelected();
		String type = (String) proxyTypeComboBox.getSelectedItem();
		String host = proxyHostField.getText().trim();
		int port = Integer.parseInt(proxyPortField.getText().trim());
		boolean auth = proxyAuthCheckBox.isSelected();
		String username = proxyUsernameField.getText().trim();
		String password = new String(proxyPasswordField.getPassword());

		Config.setProxyEnabled(enabled);
		Config.setProxyType(type);
		Config.setProxyHost(host);
		Config.setProxyPort(port);
		Config.setProxyAuthEnabled(auth);
		Config.setProxyUsername(username);
		Config.setProxyPassword(password);
		Config.saveConfig();

		return true;
	}

	public void hideNotify() {
		// If dialog is closed and settings weren't saved (state = false), restore original settings
		if (!state) {
			restoreConfig(originalEnabled, originalType, originalHost, originalPort, originalAuth, originalUsername, originalPassword);
		}
	}
}
