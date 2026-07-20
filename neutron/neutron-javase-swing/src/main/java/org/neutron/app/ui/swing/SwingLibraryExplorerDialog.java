package org.neutron.app.ui.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import nanoxml.XMLElement;
import org.neutron.app.Common;
import org.neutron.app.Config;
import org.neutron.app.util.IOUtils;

public class SwingLibraryExplorerDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	public static class GameInfo {
		public String jarPath;
		public long lastModified;
		public long size;
		public String name;
		public String version;
		public String vendor;
		public String profile;
		public String iconCachePath;
		public ImageIcon icon;
	}

	private Frame parentFrame;
	private JList directoryList;
	private DefaultListModel directoryListModel;

	private JList gamesList;
	private DefaultListModel gamesListModel;
	private List<GameInfo> allGames = new ArrayList<>();

	private JTextField searchField;
	private JLabel statusLabel;
	private JProgressBar progressBar;
	private JButton runButton;
	private JButton refreshButton;

	private static XMLElement cacheXml = new XMLElement();
	private static File cacheFile = new File(Config.getConfigPath(), "library_cache.xml");

	public SwingLibraryExplorerDialog(Frame parent) {
		super(parent, "Library Explorer", true);
		this.parentFrame = parent;

		setSize(850, 550);
		setMinimumSize(new Dimension(650, 450));
		setLocationRelativeTo(parent);

		initComponents();
		loadDirectories();
		
		// Auto-select first directory if available
		if (directoryListModel.getSize() > 0) {
			directoryList.setSelectedIndex(0);
		}
	}

	private void initComponents() {
		getContentPane().setLayout(new BorderLayout());

		// Left panel (Directories)
		JPanel leftPanel = new JPanel(new BorderLayout());
		leftPanel.setBorder(BorderFactory.createTitledBorder("Connected Folders"));

		directoryListModel = new DefaultListModel();
		directoryList = new JList(directoryListModel);
		directoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		directoryList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					String selected = (String) directoryList.getSelectedValue();
					if (selected != null) {
						scanDirectory(selected);
					} else {
						clearGamesList();
					}
				}
			}
		});

		leftPanel.add(new JScrollPane(directoryList), BorderLayout.CENTER);

		JPanel leftButtons = new JPanel(new java.awt.GridLayout(1, 2, 5, 5));
		leftButtons.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		JButton addDirButton = new JButton("Add");
		addDirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				chooseAndAddDirectory();
			}
		});
		JButton removeDirButton = new JButton("Remove");
		removeDirButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeSelectedDirectory();
			}
		});
		leftButtons.add(addDirButton);
		leftButtons.add(removeDirButton);
		leftPanel.add(leftButtons, BorderLayout.SOUTH);

		// Right panel (Games list)
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

		// Search & Refresh Panel
		JPanel topBar = new JPanel(new GridBagLayout());
		topBar.setBorder(BorderFactory.createEmptyBorder(5, 5, 10, 5));
		GridBagConstraints c = new GridBagConstraints();
		c.insets = new Insets(0, 5, 0, 5);

		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		topBar.add(new JLabel("Search:"), c);

		c.gridx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.0;
		searchField = new JTextField();
		searchField.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { filterGames(); }
			public void removeUpdate(DocumentEvent e) { filterGames(); }
			public void changedUpdate(DocumentEvent e) { filterGames(); }
		});
		topBar.add(searchField, c);

		c.gridx = 2;
		c.fill = GridBagConstraints.NONE;
		c.weightx = 0.0;
		refreshButton = new JButton("Refresh");
		refreshButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String selected = (String) directoryList.getSelectedValue();
				if (selected != null) {
					// Force clear cache for this dir or just force rescan
					scanDirectory(selected);
				}
			}
		});
		topBar.add(refreshButton, c);
		rightPanel.add(topBar, BorderLayout.NORTH);

		// Games List
		gamesListModel = new DefaultListModel();
		gamesList = new JList(gamesListModel);
		gamesList.setCellRenderer(new GameListCellRenderer());
		gamesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		gamesList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					launchSelectedGame();
				}
			}
		});
		gamesList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				runButton.setEnabled(gamesList.getSelectedValue() != null);
			}
		});

		rightPanel.add(new JScrollPane(gamesList), BorderLayout.CENTER);

		// Bottom panel (Status & actions)
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		
		JPanel statusPanel = new JPanel(new BorderLayout(10, 0));
		statusLabel = new JLabel("Please connect a directory to start.");
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		progressBar.setPreferredSize(new Dimension(150, 16));
		statusPanel.add(statusLabel, BorderLayout.CENTER);
		statusPanel.add(progressBar, BorderLayout.EAST);
		bottomPanel.add(statusPanel, BorderLayout.CENTER);

		JPanel actionPanel = new JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 10, 0));
		runButton = new JButton("Run Game");
		runButton.setEnabled(false);
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchSelectedGame();
			}
		});
		JButton closeButton = new JButton("Close");
		closeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		actionPanel.add(runButton);
		actionPanel.add(closeButton);
		bottomPanel.add(actionPanel, BorderLayout.EAST);

		// SplitPane
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		splitPane.setDividerLocation(220);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		getContentPane().add(bottomPanel, BorderLayout.SOUTH);
	}

	private void loadDirectories() {
		directoryListModel.clear();
		List<String> dirs = Config.getConnectedDirectories();
		for (String d : dirs) {
			directoryListModel.addElement(d);
		}
	}

	private void chooseAndAddDirectory() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		chooser.setDialogTitle("Select Directory of JAR Files");
		String lastDir = Config.getRecentDirectory("recentLibraryDirectory");
		if (lastDir != null && !lastDir.isEmpty()) {
			chooser.setCurrentDirectory(new File(lastDir));
		}
		
		if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File dir = chooser.getSelectedFile();
			Config.setRecentDirectory("recentLibraryDirectory", dir.getParent());
			String path = dir.getAbsolutePath();
			Config.addConnectedDirectory(path);
			loadDirectories();
			directoryList.setSelectedValue(path, true);
		}
	}

	private void removeSelectedDirectory() {
		String selected = (String) directoryList.getSelectedValue();
		if (selected != null) {
			Config.removeConnectedDirectory(selected);
			loadDirectories();
			if (directoryListModel.getSize() > 0) {
				directoryList.setSelectedIndex(0);
			} else {
				clearGamesList();
			}
		}
	}

	private void clearGamesList() {
		allGames.clear();
		gamesListModel.clear();
		statusLabel.setText("No directory selected.");
	}

	private void filterGames() {
		String query = searchField.getText().trim().toLowerCase();
		gamesListModel.clear();
		for (GameInfo g : allGames) {
			if (query.isEmpty() || 
				(g.name != null && g.name.toLowerCase().contains(query)) || 
				(g.vendor != null && g.vendor.toLowerCase().contains(query))) {
				gamesListModel.addElement(g);
			}
		}
	}

	private void scanDirectory(final String dirPath) {
		if (dirPath == null) return;
		final File dir = new File(dirPath);
		if (!dir.exists() || !dir.isDirectory()) {
			statusLabel.setText("Directory does not exist.");
			return;
		}

		statusLabel.setText("Scanning games...");
		progressBar.setVisible(true);
		progressBar.setIndeterminate(true);
		gamesList.setEnabled(false);
		refreshButton.setEnabled(false);

		SwingWorker<List<GameInfo>, Void> worker = new SwingWorker<List<GameInfo>, Void>() {
			protected List<GameInfo> doInBackground() throws Exception {
				List<GameInfo> games = new ArrayList<>();
				File[] files = dir.listFiles();
				if (files == null) {
					return games;
				}

				for (File file : files) {
					if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
						GameInfo info = getOrScanGame(file);
						games.add(info);
					}
				}
				return games;
			}

			protected void done() {
				try {
					allGames = get();
					filterGames();
					statusLabel.setText("Found " + allGames.size() + " games.");
				} catch (Exception e) {
					statusLabel.setText("Error scanning directory.");
					org.neutron.log.Logger.error("Failed scanning directory", e);
				} finally {
					progressBar.setVisible(false);
					gamesList.setEnabled(true);
					refreshButton.setEnabled(true);
				}
			}
		};

		worker.execute();
	}

	private void launchSelectedGame() {
		GameInfo selected = (GameInfo) gamesList.getSelectedValue();
		if (selected == null) {
			return;
		}
		setVisible(false);
		dispose();

		String url = IOUtils.getCanonicalFileURL(new File(selected.jarPath));
		Common.openMIDletUrlSafe(url, new Runnable() {
			public void run() {
				// Refresh any status if needed
			}
		});
	}

	public static synchronized GameInfo getOrScanGame(File file) {
		loadCache();
		GameInfo info = getCachedGame(file);
		boolean iconMissing = false;
		if (info != null && info.iconCachePath != null) {
			if (!new File(info.iconCachePath).exists()) {
				iconMissing = true;
			}
		}

		if (info == null || iconMissing) {
			if (info == null) {
				info = new GameInfo();
				info.jarPath = file.getAbsolutePath();
				info.lastModified = file.lastModified();
				info.size = file.length();
				info.name = file.getName();
				info.version = "1.0.0";
				info.vendor = "Unknown";
				info.profile = "MIDP-2.0";
			}

			File iconsCacheDir = new File(Config.getConfigPath(), "cache/icons");
			iconsCacheDir.mkdirs();

			try (JarFile jar = new JarFile(file)) {
				Manifest mf = jar.getManifest();
				if (mf != null) {
					Attributes attr = mf.getMainAttributes();
					String mName = attr.getValue("MIDlet-Name");
					if (mName != null && !mName.trim().isEmpty()) {
						info.name = mName.trim();
					}
					String mVer = attr.getValue("MIDlet-Version");
					if (mVer != null && !mVer.trim().isEmpty()) {
						info.version = mVer.trim();
					}
					String mVendor = attr.getValue("MIDlet-Vendor");
					if (mVendor != null && !mVendor.trim().isEmpty()) {
						info.vendor = mVendor.trim();
					}
					String mProfile = attr.getValue("MicroEdition-Profile");
					if (mProfile != null && !mProfile.trim().isEmpty()) {
						info.profile = mProfile.trim();
					}

					String iconPath = attr.getValue("MIDlet-Icon");
					if (iconPath == null || iconPath.trim().isEmpty()) {
						String midlet1 = attr.getValue("MIDlet-1");
						if (midlet1 != null) {
							String[] parts = midlet1.split(",");
							if (parts.length > 1) {
								iconPath = parts[1].trim();
							}
						}
					}

					if (iconPath != null) {
						iconPath = iconPath.trim();
						if (iconPath.startsWith("/")) {
							iconPath = iconPath.substring(1);
						}
						JarEntry entry = jar.getJarEntry(iconPath);
						if (entry != null) {
							File iconFile = new File(iconsCacheDir, String.valueOf(info.jarPath.hashCode()) + ".png");
							try (InputStream is = jar.getInputStream(entry)) {
								IOUtils.copyToFile(is, iconFile);
								info.iconCachePath = iconFile.getAbsolutePath();
							}
						}
					}
				}
			} catch (Exception e) {
				// use defaults
			}
			cacheGame(info);
			saveCache();
		}

		// Load cached icon image
		if (info.iconCachePath != null) {
			File iconFile = new File(info.iconCachePath);
			if (iconFile.exists()) {
				try {
					ImageIcon icon = new ImageIcon(iconFile.getAbsolutePath());
					if (icon.getIconWidth() != 32 || icon.getIconHeight() != 32) {
						Image img = icon.getImage();
						Image scaled = img.getScaledInstance(32, 32, Image.SCALE_SMOOTH);
						icon = new ImageIcon(scaled);
					}
					info.icon = icon;
				} catch (Exception e) {
					// ignore load errors
				}
			}
		}

		return info;
	}

	// --- Caching Logic ---
	private static void loadCache() {
		if (!cacheFile.exists()) {
			cacheXml = new XMLElement();
			cacheXml.setName("libraryCache");
			return;
		}
		try {
			java.io.InputStream is = new java.io.BufferedInputStream(new java.io.FileInputStream(cacheFile));
			StringBuilder xml = new StringBuilder();
			try {
				while (is.available() > 0) {
					byte[] b = new byte[is.available()];
					int read = is.read(b);
					xml.append(new String(b, 0, read));
				}
				cacheXml = new XMLElement();
				cacheXml.parseString(xml.toString());
			} finally {
				is.close();
			}
		} catch (Exception e) {
			cacheXml = new XMLElement();
			cacheXml.setName("libraryCache");
		}
	}

	private static void saveCache() {
		Config.getConfigPath().mkdirs();
		try (java.io.FileWriter fw = new java.io.FileWriter(cacheFile)) {
			cacheXml.write(fw);
		} catch (Exception e) {
			org.neutron.log.Logger.error("Failed to save library cache", e);
		}
	}

	private static XMLElement findCachedGame(String jarPath) {
		for (Enumeration e = cacheXml.enumerateChildren(); e.hasMoreElements();) {
			XMLElement child = (XMLElement) e.nextElement();
			if ("game".equals(child.getName()) && jarPath.equals(child.getStringAttribute("jarPath"))) {
				return child;
			}
		}
		return null;
	}

	private static GameInfo getCachedGame(File jarFile) {
		String jarPath = jarFile.getAbsolutePath();
		XMLElement game = findCachedGame(jarPath);
		if (game != null) {
			try {
				long lastModified = Long.parseLong(game.getStringAttribute("lastModified"));
				long size = Long.parseLong(game.getStringAttribute("size"));
				if (lastModified == jarFile.lastModified() && size == jarFile.length()) {
					GameInfo info = new GameInfo();
					info.jarPath = jarPath;
					info.lastModified = lastModified;
					info.size = size;
					info.name = game.getStringAttribute("name");
					info.version = game.getStringAttribute("version");
					info.vendor = game.getStringAttribute("vendor");
					info.profile = game.getStringAttribute("profile");
					info.iconCachePath = game.getStringAttribute("iconCachePath");
					return info;
				}
			} catch (Exception e) {
				// ignore
			}
		}
		return null;
	}

	private static void cacheGame(GameInfo info) {
		XMLElement game = findCachedGame(info.jarPath);
		if (game == null) {
			game = cacheXml.addChild("game");
			game.setAttribute("jarPath", info.jarPath);
		}
		game.setAttribute("lastModified", String.valueOf(info.lastModified));
		game.setAttribute("size", String.valueOf(info.size));
		game.setAttribute("name", info.name);
		game.setAttribute("version", info.version);
		game.setAttribute("vendor", info.vendor);
		game.setAttribute("profile", info.profile);
		if (info.iconCachePath != null) {
			game.setAttribute("iconCachePath", info.iconCachePath);
		}
	}

	private static ImageIcon getDefaultIcon() {
		java.awt.image.BufferedImage img = new java.awt.image.BufferedImage(32, 32, java.awt.image.BufferedImage.TYPE_INT_ARGB);
		java.awt.Graphics2D g = img.createGraphics();
		g.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(new java.awt.Color(120, 120, 120));
		g.fillRoundRect(2, 6, 28, 20, 6, 6);
		g.setColor(java.awt.Color.WHITE);
		g.fillRect(6, 12, 6, 4);
		g.fillRect(8, 10, 2, 8);
		g.setColor(new java.awt.Color(200, 50, 50));
		g.fillOval(20, 12, 4, 4);
		g.fillOval(24, 12, 4, 4);
		g.dispose();
		return new ImageIcon(img);
	}

	// --- Custom Renderer ---
	private class GameListCellRenderer implements ListCellRenderer {
		private JPanel panel = new JPanel(new BorderLayout(15, 0));
		private JLabel iconLabel = new JLabel();
		private JLabel titleLabel = new JLabel();
		private JLabel subtitleLabel = new JLabel();
		private JPanel textPanel = new JPanel(new java.awt.GridLayout(2, 1, 0, 2));

		public GameListCellRenderer() {
			panel.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
			titleLabel.setFont(titleLabel.getFont().deriveFont(java.awt.Font.BOLD, 13f));
			subtitleLabel.setForeground(java.awt.Color.GRAY);
			subtitleLabel.setFont(subtitleLabel.getFont().deriveFont(11f));
			textPanel.setOpaque(false);
			textPanel.add(titleLabel);
			textPanel.add(subtitleLabel);
			panel.add(iconLabel, BorderLayout.WEST);
			panel.add(textPanel, BorderLayout.CENTER);
		}

		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if (value instanceof GameInfo) {
				GameInfo info = (GameInfo) value;
				titleLabel.setText(info.name);
				subtitleLabel.setText((info.vendor != null ? info.vendor : "Unknown") 
					+ " • v" + (info.version != null ? info.version : "1.0") 
					+ " • " + (info.profile != null ? info.profile : "MIDP-2.0"));
				if (info.icon != null) {
					iconLabel.setIcon(info.icon);
				} else {
					iconLabel.setIcon(getDefaultIcon());
				}
			}

			if (isSelected) {
				panel.setBackground(list.getSelectionBackground());
				titleLabel.setForeground(list.getSelectionForeground());
				subtitleLabel.setForeground(list.getSelectionForeground());
			} else {
				panel.setBackground(list.getBackground());
				titleLabel.setForeground(list.getForeground());
				subtitleLabel.setForeground(java.awt.Color.GRAY);
			}
			return panel;
		}
	}
}
