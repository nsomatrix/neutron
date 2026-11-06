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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.neutron.app.Config;

public class SwingDevicePlatformPanel extends SwingDialogPanel {

	private static final long serialVersionUID = 1L;

	public static class PlatformPreset {
		private final String displayName;
		private final String platformValue;

		public PlatformPreset(String displayName, String platformValue) {
			this.displayName = displayName;
			this.platformValue = platformValue;
		}

		public String getDisplayName() {
			return displayName;
		}

		public String getPlatformValue() {
			return platformValue;
		}

		public String toString() {
			return displayName;
		}
	}

	private static final PlatformPreset[] PRESETS = new PlatformPreset[] {
		new PlatformPreset("Neutron (Default)", "Neutron"),
		new PlatformPreset("Nokia 6300 (Series 40)", "Nokia6300/2.0"),
		new PlatformPreset("Nokia N95 (S60v3)", "NokiaN95/35.0.002"),
		new PlatformPreset("Sony Ericsson K800i", "SonyEricssonK800i/R1CB"),
		new PlatformPreset("Sony Ericsson W995", "SonyEricssonW995/R1HA"),
		new PlatformPreset("Motorola RAZR V3", "Motorola-V3"),
		new PlatformPreset("Samsung Star (GT-S5230)", "SAMSUNG-GT-S5230"),
		new PlatformPreset("Custom", "")
	};

	private JComboBox presetComboBox;
	private JTextField platformField;
	private JLabel infoLabel;
	private boolean updatingProgrammatically = false;

	public SwingDevicePlatformPanel() {
		setLayout(new BorderLayout(0, 10));
		setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

		// Description / Header
		JPanel topPanel = new JPanel();
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
		JLabel titleLabel = new JLabel("Device Platform");
		titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 14f));
		topPanel.add(titleLabel);
		topPanel.add(Box.createVerticalStrut(4));

		JLabel descLabel = new JLabel("<html>Modify the <code>microedition.platform</code> string resolved to J2ME games.<br>Certain games check this property to enable vendor features or optimize rendering.</html>");
		descLabel.setForeground(UIManager.getColor("Label.disabledForeground") != null ? UIManager.getColor("Label.disabledForeground") : Color.GRAY);
		topPanel.add(descLabel);
		add(topPanel, BorderLayout.NORTH);

		// Form panel
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder("Platform Configuration"),
			BorderFactory.createEmptyBorder(8, 8, 8, 8)
		));

		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(6, 6, 6, 6);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;

		// Row 0: Preset
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.0;
		formPanel.add(new JLabel("Device Preset:"), c);

		presetComboBox = new JComboBox(PRESETS);
		presetComboBox.setPreferredSize(new Dimension(280, 28));
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		formPanel.add(presetComboBox, c);

		// Row 1: Platform String
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 0.0;
		formPanel.add(new JLabel("Platform String:"), c);

		platformField = new JTextField(Config.getDevicePlatform(), 24);
		platformField.setPreferredSize(new Dimension(280, 28));
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 1.0;
		formPanel.add(platformField, c);

		// Row 2: Reset button
		c.gridx = 1;
		c.gridy = 2;
		c.weightx = 0.0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.EAST;
		JButton resetButton = new JButton("Reset to Default");
		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectPresetByValue("Neutron");
			}
		});
		formPanel.add(resetButton, c);

		add(formPanel, BorderLayout.CENTER);

		// Bottom info label
		infoLabel = new JLabel("Currently active: " + Config.getDevicePlatform());
		infoLabel.setFont(infoLabel.getFont().deriveFont(Font.ITALIC, 11f));
		add(infoLabel, BorderLayout.SOUTH);

		// Sync initial preset with current config value
		selectPresetByValue(Config.getDevicePlatform());

		// Event handlers
		presetComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (updatingProgrammatically) {
					return;
				}
				PlatformPreset selected = (PlatformPreset) presetComboBox.getSelectedItem();
				if (selected != null && !selected.getPlatformValue().isEmpty()) {
					updatingProgrammatically = true;
					platformField.setText(selected.getPlatformValue());
					updatingProgrammatically = false;
				}
			}
		});

		platformField.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if (updatingProgrammatically) {
					return;
				}
				String text = platformField.getText().trim();
				boolean matched = false;
				for (int i = 0; i < PRESETS.length - 1; i++) {
					if (PRESETS[i].getPlatformValue().equalsIgnoreCase(text)) {
						updatingProgrammatically = true;
						presetComboBox.setSelectedIndex(i);
						updatingProgrammatically = false;
						matched = true;
						break;
					}
				}
				if (!matched) {
					updatingProgrammatically = true;
					presetComboBox.setSelectedIndex(PRESETS.length - 1); // Custom
					updatingProgrammatically = false;
				}
			}
		});
	}

	private void selectPresetByValue(String val) {
		if (val == null || val.trim().isEmpty()) {
			val = "Neutron";
		}
		val = val.trim();
		updatingProgrammatically = true;
		platformField.setText(val);
		boolean found = false;
		for (int i = 0; i < PRESETS.length - 1; i++) {
			if (PRESETS[i].getPlatformValue().equalsIgnoreCase(val)) {
				presetComboBox.setSelectedIndex(i);
				found = true;
				break;
			}
		}
		if (!found) {
			presetComboBox.setSelectedIndex(PRESETS.length - 1); // Custom
		}
		updatingProgrammatically = false;
	}

	public boolean check(boolean showErrors) {
		String platform = platformField.getText().trim();
		if (platform.isEmpty()) {
			platform = "Neutron";
		}
		Config.setDevicePlatform(platform);
		infoLabel.setText("Currently active: " + platform);
		return true;
	}
}

