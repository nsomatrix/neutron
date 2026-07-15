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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.neutron.app.Config;

public class SwingVideoSettingsPanel extends SwingDialogPanel {

	private static final long serialVersionUID = 1L;

	private JSlider brightnessSlider;
	private JSlider contrastSlider;
	private JSlider gammaSlider;
	private JSlider saturationSlider;
	private JSlider sharpnessSlider;
	private JSlider ghostingSlider;
	private JCheckBox invertCheckBox;

	private JLabel brightnessValueLabel;
	private JLabel contrastValueLabel;
	private JLabel gammaValueLabel;
	private JLabel saturationValueLabel;
	private JLabel sharpnessValueLabel;
	private JLabel ghostingValueLabel;

	private int originalBrightness;
	private int originalContrast;
	private float originalGamma;
	private int originalSaturation;
	private int originalSharpness;
	private int originalGhosting;
	private boolean originalInvert;

	private SwingDeviceComponent devicePanel;

	public SwingVideoSettingsPanel(SwingDeviceComponent devicePanel) {
		this.devicePanel = devicePanel;

		// Save original settings
		originalBrightness = Config.getBrightness();
		originalContrast = Config.getContrast();
		originalGamma = Config.getGamma();
		originalSaturation = Config.getSaturation();
		originalSharpness = Config.getSharpness();
		originalGhosting = Config.getGhosting();
		originalInvert = Config.isInvert();

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(6, 12, 6, 12);
		c.fill = GridBagConstraints.HORIZONTAL;

		// Headers
		c.gridy = 0;
		c.gridx = 0;
		c.weightx = 0.0;
		add(new JLabel("Adjustment"), c);

		c.gridx = 1;
		c.weightx = 1.0;
		add(new JLabel("Setting"), c);

		c.gridx = 2;
		c.weightx = 0.0;
		add(new JLabel("Value"), c);

		// Brightness Slider
		brightnessSlider = new JSlider(-100, 100, originalBrightness);
		brightnessValueLabel = new JLabel();
		addSettingRow("Brightness:", brightnessSlider, brightnessValueLabel, 1);

		// Contrast Slider
		contrastSlider = new JSlider(50, 150, originalContrast);
		contrastValueLabel = new JLabel();
		addSettingRow("Contrast:", contrastSlider, contrastValueLabel, 2);

		// Gamma Slider (scaled by 10)
		gammaSlider = new JSlider(5, 25, (int) (originalGamma * 10));
		gammaValueLabel = new JLabel();
		addSettingRow("Gamma:", gammaSlider, gammaValueLabel, 3);

		// Saturation Slider
		saturationSlider = new JSlider(0, 200, originalSaturation);
		saturationValueLabel = new JLabel();
		addSettingRow("Saturation:", saturationSlider, saturationValueLabel, 4);

		// Sharpness Slider
		sharpnessSlider = new JSlider(-100, 100, originalSharpness);
		sharpnessValueLabel = new JLabel();
		addSettingRow("Sharpness:", sharpnessSlider, sharpnessValueLabel, 5);

		// LCD Ghosting Slider
		ghostingSlider = new JSlider(0, 90, originalGhosting);
		ghostingValueLabel = new JLabel();
		addSettingRow("LCD Ghosting:", ghostingSlider, ghostingValueLabel, 6);

		// Invert Checkbox
		invertCheckBox = new JCheckBox("Invert Colors", originalInvert);
		c.gridy = 7;
		c.gridx = 0;
		c.gridwidth = 3;
		c.weightx = 1.0;
		add(invertCheckBox, c);

		// Reset Button
		JButton resetButton = new JButton("Reset to Defaults");
		c.gridy = 8;
		c.gridx = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.CENTER;
		add(resetButton, c);

		updateSliderLabels();

		// Change listeners
		brightnessSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Config.setBrightness(brightnessSlider.getValue());
				updateSliderLabels();
				repaintEmulator();
			}
		});

		contrastSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Config.setContrast(contrastSlider.getValue());
				updateSliderLabels();
				repaintEmulator();
			}
		});

		gammaSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Config.setGamma(gammaSlider.getValue() / 10.0f);
				updateSliderLabels();
				repaintEmulator();
			}
		});

		saturationSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Config.setSaturation(saturationSlider.getValue());
				updateSliderLabels();
				repaintEmulator();
			}
		});

		sharpnessSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Config.setSharpness(sharpnessSlider.getValue());
				updateSliderLabels();
				repaintEmulator();
			}
		});

		ghostingSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Config.setGhosting(ghostingSlider.getValue());
				updateSliderLabels();
				repaintEmulator();
			}
		});

		invertCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Config.setInvert(invertCheckBox.isSelected());
				repaintEmulator();
			}
		});

		resetButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				brightnessSlider.setValue(0);
				contrastSlider.setValue(100);
				gammaSlider.setValue(10);
				saturationSlider.setValue(100);
				sharpnessSlider.setValue(0);
				ghostingSlider.setValue(0);
				invertCheckBox.setSelected(false);
				
				Config.setBrightness(0);
				Config.setContrast(100);
				Config.setGamma(1.0f);
				Config.setSaturation(100);
				Config.setSharpness(0);
				Config.setGhosting(0);
				Config.setInvert(false);
				
				updateSliderLabels();
				repaintEmulator();
			}
		});
	}

	private void addSettingRow(String name, JSlider slider, JLabel valueLabel, int row) {
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(6, 12, 6, 12);
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridy = row;

		c.gridx = 0;
		c.weightx = 0.0;
		add(new JLabel(name), c);

		c.gridx = 1;
		c.weightx = 1.0;
		add(slider, c);

		c.gridx = 2;
		c.weightx = 0.0;
		add(valueLabel, c);
	}

	private void updateSliderLabels() {
		brightnessValueLabel.setText(String.format("%+d", brightnessSlider.getValue()));
		contrastValueLabel.setText(contrastSlider.getValue() + "%");
		gammaValueLabel.setText(String.format("%.1f", gammaSlider.getValue() / 10.0f));
		saturationValueLabel.setText(saturationSlider.getValue() + "%");

		int sharp = sharpnessSlider.getValue();
		if (sharp == 0) {
			sharpnessValueLabel.setText("Off");
		} else if (sharp > 0) {
			sharpnessValueLabel.setText("Sharpen " + sharp + "%");
		} else {
			sharpnessValueLabel.setText("Blur " + (-sharp) + "%");
		}

		ghostingValueLabel.setText(ghostingSlider.getValue() + "%");
	}

	public void revertSettings() {
		Config.setBrightness(originalBrightness);
		Config.setContrast(originalContrast);
		Config.setGamma(originalGamma);
		Config.setSaturation(originalSaturation);
		Config.setSharpness(originalSharpness);
		Config.setGhosting(originalGhosting);
		Config.setInvert(originalInvert);
		repaintEmulator();
	}

	private void repaintEmulator() {
		if (devicePanel != null) {
			SwingDisplayComponent sdc = (SwingDisplayComponent) devicePanel.getDisplayComponent();
			if (sdc != null) {
				sdc.repaint();
			}
		}
	}
}
