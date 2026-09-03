/**
 *  Neutron
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 */

package org.neutron.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.neutron.app.Common;
import org.neutron.app.Config;

public class SwingFrameRateDialog extends JDialog {

	private JComboBox<String> fpsComboBox;
	private boolean updating = false;

	public SwingFrameRateDialog(JFrame owner) {
		super(owner, "Frame Rate Limit", true);
		initUI();
		pack();
		setResizable(false);
		setLocationRelativeTo(owner);
	}

	private void initUI() {
		JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

		JPanel contentPanel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		String[] fpsOptions = { "Unlimited / Uncapped", "15 FPS", "24 FPS", "30 FPS", "60 FPS", "90 FPS", "120 FPS", "144 FPS", "240 FPS" };
		fpsComboBox = new JComboBox<>(fpsOptions);
		fpsComboBox.setPreferredSize(new Dimension(180, 26));

		selectFpsCombo(Config.getMaxFps());

		fpsComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (updating) return;
				int selectedFps = parseFpsSelection((String) fpsComboBox.getSelectedItem());
				Config.setMaxFps(selectedFps);
				org.neutron.device.ui.EventDispatcher.maxFps = selectedFps;
				Common.setStatusBar("Max FPS Cap set to: " + (selectedFps <= 0 ? "Unlimited" : selectedFps + " FPS"));
			}
		});

		gbc.gridx = 0; gbc.gridy = 0;
		contentPanel.add(new JLabel("Target Frame Rate:"), gbc);
		gbc.gridx = 1; gbc.gridy = 0;
		contentPanel.add(fpsComboBox, gbc);

		// Presets Row
		JPanel presetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
		int[] quickFps = { 0, 30, 60, 120 };
		for (final int fps : quickFps) {
			String label = fps == 0 ? "Unlimited" : fps + " FPS";
			JButton btnPreset = new JButton(label);
			btnPreset.setMargin(new Insets(2, 6, 2, 6));
			btnPreset.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					selectFpsCombo(fps);
					Config.setMaxFps(fps);
					org.neutron.device.ui.EventDispatcher.maxFps = fps;
					Common.setStatusBar("Max FPS Cap set to: " + (fps <= 0 ? "Unlimited" : fps + " FPS"));
				}
			});
			presetPanel.add(btnPreset);
		}

		gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
		contentPanel.add(presetPanel, gbc);

		mainPanel.add(contentPanel, BorderLayout.CENTER);

		// Bottom Buttons
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 2));
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectFpsCombo(0);
				Config.setMaxFps(0);
				org.neutron.device.ui.EventDispatcher.maxFps = 0;
				Common.setStatusBar("FPS Limit reset to Unlimited");
			}
		});

		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		buttonPanel.add(btnReset);
		buttonPanel.add(btnClose);
		mainPanel.add(buttonPanel, BorderLayout.SOUTH);

		setContentPane(mainPanel);
	}

	private void selectFpsCombo(int fps) {
		updating = true;
		if (fps <= 0) {
			fpsComboBox.setSelectedIndex(0);
		} else {
			String match = fps + " FPS";
			for (int i = 0; i < fpsComboBox.getItemCount(); i++) {
				if (fpsComboBox.getItemAt(i).equals(match)) {
					fpsComboBox.setSelectedIndex(i);
					break;
				}
			}
		}
		updating = false;
	}

	private int parseFpsSelection(String str) {
		if (str == null || str.startsWith("Unlimited")) return 0;
		try {
			return Integer.parseInt(str.replace(" FPS", "").trim());
		} catch (Exception e) {
			return 0;
		}
	}
}
