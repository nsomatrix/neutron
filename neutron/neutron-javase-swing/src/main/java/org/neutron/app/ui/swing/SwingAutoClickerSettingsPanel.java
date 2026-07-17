package org.neutron.app.ui.swing;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;

public class SwingAutoClickerSettingsPanel extends SwingDialogPanel {

	private final JCheckBox chkEnabled;
	private final JLabel lblTargetsCount;
	private final JTextField txtInterval;
	private final JButton btnClear;

	public SwingAutoClickerSettingsPanel() {
		setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 8, 8, 8);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// 1. Enable Checkbox
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		chkEnabled = new JCheckBox("Enable Tap Automator Loop", SwingAutoClicker.isEnabled());
		add(chkEnabled, gbc);

		// 2. Target Waypoints Count Label
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		add(new JLabel("Active Waypoints:"), gbc);

		gbc.gridx = 1;
		lblTargetsCount = new JLabel(SwingAutoClicker.getTargets().size() + " target(s) loaded");
		add(lblTargetsCount, gbc);

		// 3. Clear Waypoints Button
		gbc.gridx = 0;
		gbc.gridy = 2;
		add(new JLabel("Manage Loop:"), gbc);

		gbc.gridx = 1;
		btnClear = new JButton("Clear Click Sequence");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SwingAutoClicker.clearCoordinates();
				lblTargetsCount.setText("0 target(s) loaded");
				// Trigger repaint of display component to clear overlays immediately
				java.awt.Frame[] frames = java.awt.Frame.getFrames();
				for (int i = 0; i < frames.length; i++) {
					frames[i].repaint();
				}
			}
		});
		add(btnClear, gbc);

		// 4. Click Interval Text Field
		gbc.gridx = 0;
		gbc.gridy = 3;
		add(new JLabel("Click Interval (ms):"), gbc);

		gbc.gridx = 1;
		txtInterval = new JTextField(String.valueOf(SwingAutoClicker.getIntervalMs()), 8);
		add(txtInterval, gbc);

		// 5. Instructions
		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.gridwidth = 2;
		JLabel lblInstructions = new JLabel("<html><i>Instructions:<br>"
			+ "1. Hold <b>Ctrl</b> and click on the game screen to <b>add</b> click targets.<br>"
			+ "2. Click targets will execute in sequence (1 &rarr; 2 &rarr; 3 &rarr; 1).<br>"
			+ "3. The next click target is highlighted in gold.</i></html>");
		lblInstructions.setForeground(java.awt.Color.GRAY);
		add(lblInstructions, gbc);

		// Add a timer to refresh targets count in case user Ctrl+clicks while the dialog is open!
		final javax.swing.Timer refreshTimer = new javax.swing.Timer(200, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				lblTargetsCount.setText(SwingAutoClicker.getTargets().size() + " target(s) loaded");
			}
		});
		refreshTimer.start();

		// Cleanup timer on hide
		addComponentListener(new java.awt.event.ComponentAdapter() {
			public void componentHidden(java.awt.event.ComponentEvent e) {
				refreshTimer.stop();
			}
		});
	}

	public boolean check(boolean state) {
		try {
			int interval = Integer.parseInt(txtInterval.getText().trim());
			if (interval < 50) {
				if (state) {
					javax.swing.JOptionPane.showMessageDialog(this, 
						"Click interval must be at least 50 ms.", 
						"Invalid Input", 
						javax.swing.JOptionPane.ERROR_MESSAGE);
				}
				return false;
			}
		} catch (NumberFormatException ex) {
			if (state) {
				javax.swing.JOptionPane.showMessageDialog(this, 
					"Click interval must be a valid number.", 
					"Invalid Input", 
					javax.swing.JOptionPane.ERROR_MESSAGE);
			}
			return false;
		}
		return true;
	}

	public void applySettings() {
		SwingAutoClicker.setEnabled(chkEnabled.isSelected());
		try {
			int interval = Integer.parseInt(txtInterval.getText().trim());
			SwingAutoClicker.setIntervalMs(interval);
		} catch (NumberFormatException ex) {
			// Already validated by check()
		}
	}
}
