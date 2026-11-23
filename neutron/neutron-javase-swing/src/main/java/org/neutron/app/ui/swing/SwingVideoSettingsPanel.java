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
import org.neutron.graphics3d.Graphics3DConfig;

public class SwingVideoSettingsPanel extends SwingDialogPanel {

	private static final long serialVersionUID = 1L;

	private JSlider brightnessSlider;
	private JSlider contrastSlider;
	private JSlider gammaSlider;
	private JSlider saturationSlider;
	private JSlider sharpnessSlider;
	private JSlider ghostingSlider;
	private JCheckBox invertCheckBox;
	private JCheckBox forcePerspectiveCheckBox;
	private JCheckBox subpixelCheckBox;
	private JCheckBox bilinear3dCheckBox;

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
	private boolean originalForcePerspective;
	private boolean originalSubpixel;
	private boolean originalBilinear3d;

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
		originalForcePerspective = Graphics3DConfig.isForcePerspective();
		originalSubpixel = Graphics3DConfig.isSubpixelPrecision();
		originalBilinear3d = Graphics3DConfig.isBilinearFiltering();

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
		brightnessValueLabel = new JLabel("", JLabel.RIGHT);
		brightnessValueLabel.setPreferredSize(new java.awt.Dimension(55, 20));
		addSettingRow("Brightness:", brightnessSlider, brightnessValueLabel, 1);

		// Contrast Slider
		contrastSlider = new JSlider(50, 150, originalContrast);
		contrastValueLabel = new JLabel("", JLabel.RIGHT);
		contrastValueLabel.setPreferredSize(new java.awt.Dimension(55, 20));
		addSettingRow("Contrast:", contrastSlider, contrastValueLabel, 2);

		// Gamma Slider (scaled by 10)
		gammaSlider = new JSlider(5, 25, (int) (originalGamma * 10));
		gammaValueLabel = new JLabel("", JLabel.RIGHT);
		gammaValueLabel.setPreferredSize(new java.awt.Dimension(55, 20));
		addSettingRow("Gamma:", gammaSlider, gammaValueLabel, 3);

		// Saturation Slider
		saturationSlider = new JSlider(0, 200, originalSaturation);
		saturationValueLabel = new JLabel("", JLabel.RIGHT);
		saturationValueLabel.setPreferredSize(new java.awt.Dimension(55, 20));
		addSettingRow("Saturation:", saturationSlider, saturationValueLabel, 4);

		// Sharpness Slider
		sharpnessSlider = new JSlider(-100, 100, originalSharpness);
		sharpnessValueLabel = new JLabel("", JLabel.RIGHT);
		sharpnessValueLabel.setPreferredSize(new java.awt.Dimension(55, 20));
		addSettingRow("Sharpness:", sharpnessSlider, sharpnessValueLabel, 5);

		// LCD Ghosting Slider
		ghostingSlider = new JSlider(0, 90, originalGhosting);
		ghostingValueLabel = new JLabel("", JLabel.RIGHT);
		ghostingValueLabel.setPreferredSize(new java.awt.Dimension(55, 20));
		addSettingRow("LCD Ghosting:", ghostingSlider, ghostingValueLabel, 6);

		// Invert Checkbox
		invertCheckBox = new JCheckBox("Invert Colors", originalInvert);
		c.gridy = 7;
		c.gridx = 0;
		c.gridwidth = 3;
		c.weightx = 1.0;
		add(invertCheckBox, c);

		// 3D Options
		forcePerspectiveCheckBox = new JCheckBox("3D Force Perspective Correction", originalForcePerspective);
		c.gridy = 8;
		c.gridx = 0;
		c.gridwidth = 3;
		c.weightx = 1.0;
		add(forcePerspectiveCheckBox, c);

		subpixelCheckBox = new JCheckBox("3D Subpixel Precision (Reduce vertex jitter)", originalSubpixel);
		c.gridy = 9;
		c.gridx = 0;
		c.gridwidth = 3;
		c.weightx = 1.0;
		add(subpixelCheckBox, c);

		bilinear3dCheckBox = new JCheckBox("3D Bilinear Texture Filtering", originalBilinear3d);
		c.gridy = 10;
		c.gridx = 0;
		c.gridwidth = 3;
		c.weightx = 1.0;
		add(bilinear3dCheckBox, c);

		// Reset Button
		JButton resetButton = new JButton("Reset to Defaults");
		c.gridy = 11;
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

		forcePerspectiveCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graphics3DConfig.setForcePerspective(forcePerspectiveCheckBox.isSelected());
				repaintEmulator();
			}
		});

		subpixelCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graphics3DConfig.setSubpixelPrecision(subpixelCheckBox.isSelected());
				repaintEmulator();
			}
		});

		bilinear3dCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Graphics3DConfig.setBilinearFiltering(bilinear3dCheckBox.isSelected());
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
				forcePerspectiveCheckBox.setSelected(true);
				subpixelCheckBox.setSelected(true);
				bilinear3dCheckBox.setSelected(true);
				
				Config.setBrightness(0);
				Config.setContrast(100);
				Config.setGamma(1.0f);
				Config.setSaturation(100);
				Config.setSharpness(0);
				Config.setGhosting(0);
				Config.setInvert(false);
				Graphics3DConfig.setForcePerspective(true);
				Graphics3DConfig.setSubpixelPrecision(true);
				Graphics3DConfig.setBilinearFiltering(false);
				
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
			sharpnessValueLabel.setText("0%");
		} else if (sharp > 0) {
			sharpnessValueLabel.setText("+" + sharp + "%");
		} else {
			sharpnessValueLabel.setText(sharp + "%");
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
		Graphics3DConfig.setForcePerspective(originalForcePerspective);
		Graphics3DConfig.setSubpixelPrecision(originalSubpixel);
		Graphics3DConfig.setBilinearFiltering(originalBilinear3d);
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
