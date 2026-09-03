/*
 *  Neutron
 *
 *  It is licensed under the following two licenses as alternatives:
 *    1. GNU Lesser General Public License (the "LGPL") version 2.1 or any newer version
 *    2. Apache License (the "AL") Version 2.0
 *
 *  You may not use this file except in compliance with at least one of
 *  the above two licenses.
 *
 *  You may obtain a copy of the LGPL at
 *      http://www.gnu.org/licenses/old-licenses/lgpl-2.1.txt
 *
 *  You may obtain a copy of the AL at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the LGPL or the AL for the specific language governing permissions and
 *  limitations.
 */

package org.neutron.app.util;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.neutron.log.Logger;

/**
 * Manages backup and restore snapshots of the emulator's root directory (.neutron folder).
 */
public class SnapshotManager {

	public static void backup(final Component parent) {
		final File rootDir = org.neutron.app.Config.getConfigPath();
		if (!rootDir.exists() || !rootDir.isDirectory()) {
			JOptionPane.showMessageDialog(parent,
					"No emulator root folder found at " + rootDir.getAbsolutePath() + " to backup.",
					"Backup Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Save Snapshot Backup...");
		chooser.setFileFilter(new FileNameExtensionFilter("Zip Backup files (*.zip)", "zip"));

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
		String defaultName = "neutron_snapshot_" + sdf.format(new Date()) + ".zip";
		chooser.setSelectedFile(new File(chooser.getCurrentDirectory(), defaultName));

		if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		File targetFile = chooser.getSelectedFile();
		String path = targetFile.getAbsolutePath();
		if (!path.toLowerCase().endsWith(".zip") && path.indexOf('.') == -1) {
			targetFile = new File(path + ".zip");
		}

		if (targetFile.exists()) {
			int confirm = JOptionPane.showConfirmDialog(parent,
					"The file '" + targetFile.getName() + "' already exists. Overwrite it?",
					"Confirm Overwrite",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (confirm != JOptionPane.YES_OPTION) {
				return;
			}
		}

		final File finalTargetFile = targetFile;

		final JDialog progressDialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Snapshot Backup", JDialog.ModalityType.APPLICATION_MODAL);
		progressDialog.setLayout(new BorderLayout(10, 10));
		progressDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		JLabel statusLabel = new JLabel("Compressing .neutron directory... Please wait.");
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setPreferredSize(new Dimension(300, 20));

		progressDialog.add(statusLabel, BorderLayout.NORTH);
		progressDialog.add(progressBar, BorderLayout.CENTER);
		progressDialog.pack();
		progressDialog.setLocationRelativeTo(parent);

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				org.neutron.app.Common.setStatusBar("Backup started...");
				org.neutron.app.Config.saveConfig();
				try (FileOutputStream fos = new FileOutputStream(finalTargetFile);
					 BufferedOutputStream bos = new BufferedOutputStream(fos);
					 ZipOutputStream zos = new ZipOutputStream(bos)) {

					zipDirectory(rootDir, rootDir, zos, finalTargetFile);
				}
				return null;
			}

			@Override
			protected void done() {
				progressDialog.dispose();
				try {
					get();
					org.neutron.app.Common.setStatusBar("Backup completed: " + finalTargetFile.getName());
					JOptionPane.showMessageDialog(parent,
							"Backup completed successfully!\nSaved to: " + finalTargetFile.getAbsolutePath(),
							"Backup Success",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					Logger.error("Backup failed", e);
					org.neutron.app.Common.setStatusBar("Backup failed");
					JOptionPane.showMessageDialog(parent,
							"Backup failed:\n" + e.getMessage(),
							"Backup Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		worker.execute();
		progressDialog.setVisible(true);
	}

	public static void restore(final Component parent) {
		final File rootDir = org.neutron.app.Config.getConfigPath();

		int confirm = JOptionPane.showConfirmDialog(parent,
				"Restoring a snapshot will COMPLETELY overwrite all current settings, RMS databases, and games.\n" +
				"It is highly recommended to do this when no game is running.\n" +
				"The emulator will automatically close after the restore is finished to load the new settings.\n\n" +
				"Do you want to proceed?",
				"Confirm Restore",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);

		if (confirm != JOptionPane.YES_OPTION) {
			return;
		}

		JFileChooser chooser = new JFileChooser();
		chooser.setDialogTitle("Select Snapshot Backup File...");
		chooser.setFileFilter(new FileNameExtensionFilter("Zip Backup files (*.zip)", "zip"));

		if (chooser.showOpenDialog(parent) != JFileChooser.APPROVE_OPTION) {
			return;
		}

		final File backupFile = chooser.getSelectedFile();
		if (!backupFile.exists()) {
			JOptionPane.showMessageDialog(parent,
					"Selected backup file does not exist.",
					"Restore Error",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		final JDialog progressDialog = new JDialog(SwingUtilities.getWindowAncestor(parent), "Snapshot Restore", JDialog.ModalityType.APPLICATION_MODAL);
		progressDialog.setLayout(new BorderLayout(10, 10));
		progressDialog.getRootPane().setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

		JLabel statusLabel = new JLabel("Restoring snapshot... Please wait.");
		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setPreferredSize(new Dimension(300, 20));

		progressDialog.add(statusLabel, BorderLayout.NORTH);
		progressDialog.add(progressBar, BorderLayout.CENTER);
		progressDialog.pack();
		progressDialog.setLocationRelativeTo(parent);

		SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				org.neutron.app.Common.setStatusBar("Restore started...");
				if (rootDir.exists()) {
					deleteDirectoryExcept(rootDir, backupFile);
				}

				if (!rootDir.exists()) {
					rootDir.mkdirs();
				}

				unzip(backupFile, rootDir);
				return null;
			}

			@Override
			protected void done() {
				progressDialog.dispose();
				try {
					get();
					org.neutron.app.Common.setStatusBar("Restore completed successfully");
					JOptionPane.showMessageDialog(parent,
							"Restore completed successfully!\nThe emulator will now exit. Please restart it manually.",
							"Restore Success",
							JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				} catch (Exception e) {
					Logger.error("Restore failed", e);
					org.neutron.app.Common.setStatusBar("Restore failed");
					JOptionPane.showMessageDialog(parent,
							"Restore failed:\n" + e.getMessage(),
							"Restore Error",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		};

		worker.execute();
		progressDialog.setVisible(true);
	}

	static void unzip(File zipFile, File destFolder) throws IOException {
		if (!destFolder.exists()) {
			destFolder.mkdirs();
		}

		try (FileInputStream fis = new FileInputStream(zipFile);
			 BufferedInputStream bis = new BufferedInputStream(fis);
			 ZipInputStream zis = new ZipInputStream(bis)) {

			ZipEntry zipEntry = zis.getNextEntry();
			byte[] buffer = new byte[8192];
			while (zipEntry != null) {
				File newFile = newFile(destFolder, zipEntry);
				if (zipEntry.isDirectory()) {
					if (!newFile.isDirectory() && !newFile.mkdirs()) {
						throw new IOException("Failed to create directory " + newFile);
					}
				} else {
					File parentDir = newFile.getParentFile();
					if (!parentDir.isDirectory() && !parentDir.mkdirs()) {
						throw new IOException("Failed to create directory " + parentDir);
					}
					try (FileOutputStream fos = new FileOutputStream(newFile);
						 BufferedOutputStream bos = new BufferedOutputStream(fos)) {
						int len;
						while ((len = zis.read(buffer)) > 0) {
							bos.write(buffer, 0, len);
						}
					}
				}
				zis.closeEntry();
				zipEntry = zis.getNextEntry();
			}
		}
	}

	static void zipDirectory(File rootFolder, File currentFile, ZipOutputStream zos, File excludeFile) throws IOException {
		if (currentFile.getCanonicalPath().equals(excludeFile.getCanonicalPath())) {
			return;
		}

		if (currentFile.isDirectory()) {
			File[] files = currentFile.listFiles();
			if (files != null) {
				for (File file : files) {
					zipDirectory(rootFolder, file, zos, excludeFile);
				}
			}
		} else {
			String relativePath = rootFolder.toURI().relativize(currentFile.toURI()).getPath();
			ZipEntry zipEntry = new ZipEntry(relativePath);
			zos.putNextEntry(zipEntry);
			try (FileInputStream fis = new FileInputStream(currentFile);
				 BufferedInputStream bis = new BufferedInputStream(fis)) {
				byte[] buffer = new byte[8192];
				int length;
				while ((length = bis.read(buffer)) > 0) {
					zos.write(buffer, 0, length);
				}
			}
			zos.closeEntry();
		}
	}

	static void deleteDirectoryExcept(File current, File keep) throws IOException {
		if (current.getCanonicalPath().equals(keep.getCanonicalPath())) {
			return;
		}
		if (current.isDirectory()) {
			File[] files = current.listFiles();
			if (files != null) {
				for (File f : files) {
					deleteDirectoryExcept(f, keep);
				}
			}
		}
		if (!current.getCanonicalPath().equals(keep.getCanonicalPath()) && !isAncestorOf(current, keep)) {
			boolean deleted = current.delete();
			if (!deleted && current.exists()) {
				Logger.warn("Could not delete file/folder: " + current.getAbsolutePath());
			}
		}
	}

	static boolean isAncestorOf(File ancestor, File descendent) throws IOException {
		String ancestorPath = ancestor.getCanonicalPath();
		String descendentPath = descendent.getCanonicalPath();
		return descendentPath.startsWith(ancestorPath + File.separator);
	}

	static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
		File destFile = new File(destinationDir, zipEntry.getName());
		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();
		if (!destFilePath.startsWith(destDirPath + File.separator)) {
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}
		return destFile;
	}
}
