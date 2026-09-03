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
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.neutron.app.Common;
import org.neutron.app.Config;

public class SwingEmulationSpeedDialog extends JDialog {

	private JSlider speedSlider;
	private JSpinner speedSpinner;
	private boolean updating = false;

	public SwingEmulationSpeedDialog(JFrame owner) {
		super(owner, "Emulation Speed", true);
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

		double currentSpeed = Config.getSpeedMultiplier();
		int sliderVal = (int) Math.round(currentSpeed * 100);

		speedSlider = new JSlider(10, 1000, Math.min(1000, Math.max(10, sliderVal)));
		speedSlider.setPreferredSize(new Dimension(240, 40));

		SpinnerNumberModel spinnerModel = new SpinnerNumberModel(currentSpeed, 0.1, 100.0, 0.25);
		speedSpinner = new JSpinner(spinnerModel);
		speedSpinner.setPreferredSize(new Dimension(75, 24));

		gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
		contentPanel.add(speedSlider, gbc);

		gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.0;
		contentPanel.add(speedSpinner, gbc);

		// Presets Row
		JPanel presetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 2));
		double[] presets = { 0.25, 0.50, 1.00, 1.50, 2.00, 5.00, 10.00 };
		for (final double p : presets) {
			JButton btnPreset = new JButton(String.format(Locale.US, "%.2fx", p));
			btnPreset.setMargin(new Insets(2, 6, 2, 6));
			btnPreset.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					applySpeed(p);
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
				applySpeed(1.0);
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

		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (updating) return;
				double val = speedSlider.getValue() / 100.0;
				updating = true;
				speedSpinner.setValue(val);
				Config.setSpeedMultiplier(val);
				Common.setStatusBar("Emulation Speed: " + String.format(Locale.US, "%.2fx", val));
				updating = false;
			}
		});

		speedSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (updating) return;
				double val = ((Number) speedSpinner.getValue()).doubleValue();
				updating = true;
				int sVal = (int) Math.round(val * 100);
				if (sVal >= speedSlider.getMinimum() && sVal <= speedSlider.getMaximum()) {
					speedSlider.setValue(sVal);
				}
				Config.setSpeedMultiplier(val);
				Common.setStatusBar("Emulation Speed: " + String.format(Locale.US, "%.2fx", val));
				updating = false;
			}
		});

		setContentPane(mainPanel);
	}

	private void applySpeed(double speedVal) {
		updating = true;
		speedSpinner.setValue(speedVal);
		int sVal = (int) Math.round(speedVal * 100);
		if (sVal >= speedSlider.getMinimum() && sVal <= speedSlider.getMaximum()) {
			speedSlider.setValue(sVal);
		}
		Config.setSpeedMultiplier(speedVal);
		Common.setStatusBar("Emulation Speed: " + String.format(Locale.US, "%.2fx", speedVal));
		updating = false;
	}
}
