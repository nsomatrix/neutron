package org.neutron.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.Timer;

public class SwingSystemInfoDialog extends SwingDialogPanel {

	private static final long serialVersionUID = 1L;

	private Timer refreshTimer;
	private JLabel threadsValueLabel;
	private JLabel uptimeValueLabel;
	private JLabel cpuValueLabel;
	private JLabel gcValueLabel;
	private JProgressBar memoryBar;

	public SwingSystemInfoDialog() {
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		setPreferredSize(new Dimension(400, 280));

		JPanel contentPanel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(6, 6, 6, 6);
		c.fill = GridBagConstraints.HORIZONTAL;

		String osDetails = System.getProperty("os.name") + " " + System.getProperty("os.version") + " (" + System.getProperty("os.arch") + ")";
		String jvmDetails = System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.version") + ")";
		String javaVendor = System.getProperty("java.vendor");

		threadsValueLabel = new JLabel("N/A");
		uptimeValueLabel = new JLabel("N/A");
		cpuValueLabel = new JLabel("N/A");
		gcValueLabel = new JLabel("N/A");
		memoryBar = new JProgressBar();
		memoryBar.setStringPainted(true);
		memoryBar.setPreferredSize(new Dimension(180, 18));

		addPropRow(contentPanel, c, "Operating System:", osDetails, 0);
		addPropRow(contentPanel, c, "JVM Environment:", jvmDetails, 1);
		addPropRow(contentPanel, c, "Java Vendor:", javaVendor, 2);
		addComponentRow(contentPanel, c, "Process CPU:", cpuValueLabel, 3);
		addComponentRow(contentPanel, c, "Active Threads:", threadsValueLabel, 4);
		addComponentRow(contentPanel, c, "GC Activity:", gcValueLabel, 5);
		addComponentRow(contentPanel, c, "JVM Uptime:", uptimeValueLabel, 6);
		addComponentRow(contentPanel, c, "JVM Memory:", memoryBar, 7);

		// Fill the bottom space
		c.gridy = 8;
		c.gridx = 0;
		c.gridwidth = 2;
		c.weighty = 1.0;
		contentPanel.add(new JLabel(""), c);

		add(contentPanel, BorderLayout.CENTER);

		// Setup Timer to refresh dynamic fields every 1 second
		refreshTimer = new Timer(1000, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				refreshDynamicInfo();
			}
		});
		refreshDynamicInfo();
	}

	private void addPropRow(JPanel panel, GridBagConstraints gbc, String labelText, String valueText, int row) {
		gbc.gridy = row;
		gbc.gridx = 0;
		gbc.weightx = 0.0;
		gbc.anchor = GridBagConstraints.EAST;
		JLabel lbl = new JLabel(labelText);
		lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
		panel.add(lbl, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(new JLabel(valueText), gbc);
	}

	private void addComponentRow(JPanel panel, GridBagConstraints gbc, String labelText, JComponent comp, int row) {
		gbc.gridy = row;
		gbc.gridx = 0;
		gbc.weightx = 0.0;
		gbc.anchor = GridBagConstraints.EAST;
		JLabel lbl = new JLabel(labelText);
		lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));
		panel.add(lbl, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.anchor = GridBagConstraints.WEST;
		panel.add(comp, gbc);
	}

	private void refreshDynamicInfo() {
		// Memory usage
		long maxMemory = Runtime.getRuntime().maxMemory();
		long totalMemory = Runtime.getRuntime().totalMemory();
		long freeMemory = Runtime.getRuntime().freeMemory();
		long usedMemory = totalMemory - freeMemory;

		int maxMb = (int) (maxMemory / 1024 / 1024);
		int usedMb = (int) (usedMemory / 1024 / 1024);

		if (maxMemory == Long.MAX_VALUE) {
			int totalMb = (int) (totalMemory / 1024 / 1024);
			memoryBar.setMaximum(totalMb);
			memoryBar.setValue(usedMb);
			memoryBar.setString(usedMb + " MB / " + totalMb + " MB (Allocated)");
		} else {
			memoryBar.setMaximum(maxMb);
			memoryBar.setValue(usedMb);
			memoryBar.setString(usedMb + " MB / " + maxMb + " MB");
		}

		// Active threads
		threadsValueLabel.setText(String.valueOf(Thread.activeCount()));

		// Process CPU Load
		double processCpu = -1;
		try {
			java.lang.management.OperatingSystemMXBean osBean = java.lang.management.ManagementFactory.getOperatingSystemMXBean();
			if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
				processCpu = ((com.sun.management.OperatingSystemMXBean) osBean).getProcessCpuLoad();
			}
		} catch (Throwable t) {
			// ignore
		}
		if (processCpu >= 0) {
			cpuValueLabel.setText(String.format("%.1f%%", processCpu * 100));
		} else {
			cpuValueLabel.setText("N/A");
		}

		// Garbage Collection activity
		long gcCount = 0;
		long gcTime = 0;
		try {
			for (java.lang.management.GarbageCollectorMXBean gc : java.lang.management.ManagementFactory.getGarbageCollectorMXBeans()) {
				long count = gc.getCollectionCount();
				long time = gc.getCollectionTime();
				if (count != -1) gcCount += count;
				if (time != -1) gcTime += time;
			}
		} catch (Throwable t) {
			// ignore
		}
		gcValueLabel.setText(gcCount + " collections / " + String.format("%.2fs total pause", gcTime / 1000.0));

		// JVM Uptime
		try {
			long uptimeMs = java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime();
			long seconds = uptimeMs / 1000;
			long minutes = seconds / 60;
			long hours = minutes / 60;
			long days = hours / 24;
			uptimeValueLabel.setText(String.format("%d days, %02d:%02d:%02d", days, hours % 24, minutes % 60, seconds % 60));
		} catch (Throwable t) {
			uptimeValueLabel.setText("N/A");
		}
	}

	protected void showNotify() {
		if (refreshTimer != null) {
			refreshTimer.start();
		}
	}

	protected void hideNotify() {
		if (refreshTimer != null) {
			refreshTimer.stop();
		}
	}
}
