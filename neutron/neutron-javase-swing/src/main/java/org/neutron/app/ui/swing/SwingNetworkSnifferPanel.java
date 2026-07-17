package org.neutron.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.neutron.device.ui.NetworkSniffer;
import org.neutron.device.ui.NetworkSniffer.NetworkEvent;
import org.neutron.device.ui.NetworkSniffer.NetworkSnifferListener;

public class SwingNetworkSnifferPanel extends SwingDialogPanel implements NetworkSnifferListener {

	private final DefaultTableModel tableModel;
	private final JTable table;
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

	public SwingNetworkSnifferPanel() {
		setLayout(new BorderLayout());

		// Columns
		String[] columnNames = { "Time", "Type", "Destination", "Status" };
		tableModel = new DefaultTableModel(columnNames, 0) {
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		table = new JTable(tableModel);
		table.getColumnModel().getColumn(0).setPreferredWidth(80);
		table.getColumnModel().getColumn(1).setPreferredWidth(60);
		table.getColumnModel().getColumn(2).setPreferredWidth(300);
		table.getColumnModel().getColumn(3).setPreferredWidth(100);

		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);

		// Top Control Bar
		JPanel controlBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		final JCheckBox chkEnable = new JCheckBox("Enable Sniffing", NetworkSniffer.isEnabled());
		chkEnable.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NetworkSniffer.setEnabled(chkEnable.isSelected());
			}
		});
		controlBar.add(chkEnable);

		JButton btnClear = new JButton("Clear Logs");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				NetworkSniffer.clear();
			}
		});
		controlBar.add(btnClear);

		add(controlBar, BorderLayout.NORTH);

		// Load existing events
		List events = NetworkSniffer.getEvents();
		for (int i = 0; i < events.size(); i++) {
			addEventToTable((NetworkEvent) events.get(i));
		}

		NetworkSniffer.addListener(this);
	}

	private void addEventToTable(NetworkEvent event) {
		String timeStr = dateFormat.format(event.timestamp);
		String typeStr = event.type == NetworkSniffer.Type.HTTP ? "HTTP" : "SOCKET";
		tableModel.addRow(new Object[] {
			timeStr,
			typeStr,
			event.urlOrHost,
			event.status
		});
	}

	public void onNetworkEvent(final NetworkEvent event) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				addEventToTable(event);
			}
		});
	}

	public void onCleared() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				tableModel.setRowCount(0);
			}
		});
	}

	public void dispose() {
		NetworkSniffer.removeListener(this);
	}
}
