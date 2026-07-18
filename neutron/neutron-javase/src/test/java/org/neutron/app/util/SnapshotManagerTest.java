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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import junit.framework.TestCase;

public class SnapshotManagerTest extends TestCase {

	private File tempDir;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		tempDir = Files.createTempDirectory("snapshot_test").toFile();
	}

	@Override
	protected void tearDown() throws Exception {
		deleteRecursive(tempDir);
		super.tearDown();
	}

	private void deleteRecursive(File file) {
		if (file.isDirectory()) {
			File[] children = file.listFiles();
			if (children != null) {
				for (File child : children) {
					deleteRecursive(child);
				}
			}
		}
		file.delete();
	}

	public void testIsAncestorOf() throws IOException {
		File parent = new File(tempDir, "parent");
		File child = new File(parent, "child");
		File unrelated = new File(tempDir, "unrelated");

		parent.mkdirs();
		child.createNewFile();
		unrelated.createNewFile();

		assertTrue(SnapshotManager.isAncestorOf(parent, child));
		assertFalse(SnapshotManager.isAncestorOf(parent, unrelated));
		assertFalse(SnapshotManager.isAncestorOf(child, parent));
	}

	public void testNewFileZipSlip() throws IOException {
		File destDir = new File(tempDir, "dest");
		destDir.mkdirs();

		// Safe entry
		ZipEntry safeEntry = new ZipEntry("sub/file.txt");
		File safeFile = SnapshotManager.newFile(destDir, safeEntry);
		assertEquals(new File(destDir, "sub/file.txt").getCanonicalPath(), safeFile.getCanonicalPath());

		// Malicious entry (Zip Slip)
		ZipEntry maliciousEntry = new ZipEntry("../malicious.txt");
		try {
			SnapshotManager.newFile(destDir, maliciousEntry);
			fail("Should throw IOException for Zip Slip entry");
		} catch (IOException e) {
			assertTrue(e.getMessage().contains("outside of the target dir"));
		}
	}

	public void testZipAndUnzip() throws Exception {
		File sourceDir = new File(tempDir, "source");
		sourceDir.mkdirs();

		File file1 = new File(sourceDir, "file1.txt");
		try (FileWriter fw = new FileWriter(file1)) {
			fw.write("hello world");
		}

		File subDir = new File(sourceDir, "subdir");
		subDir.mkdirs();
		File file2 = new File(subDir, "file2.txt");
		try (FileWriter fw = new FileWriter(file2)) {
			fw.write("nested content");
		}

		File zipFile = new File(tempDir, "backup.zip");

		// Test zipDirectory
		try (FileOutputStream fos = new FileOutputStream(zipFile);
			 ZipOutputStream zos = new ZipOutputStream(fos)) {
			SnapshotManager.zipDirectory(sourceDir, sourceDir, zos, zipFile);
		}

		assertTrue(zipFile.exists());
		assertTrue(zipFile.length() > 0);

		// Test unzip
		File destDir = new File(tempDir, "destination");
		SnapshotManager.unzip(zipFile, destDir);

		File unzippedFile1 = new File(destDir, "file1.txt");
		File unzippedSubDir = new File(destDir, "subdir");
		File unzippedFile2 = new File(unzippedSubDir, "file2.txt");

		assertTrue(unzippedFile1.exists());
		assertTrue(unzippedSubDir.exists());
		assertTrue(unzippedFile2.exists());

		String content1 = new String(Files.readAllBytes(unzippedFile1.toPath()));
		String content2 = new String(Files.readAllBytes(unzippedFile2.toPath()));

		assertEquals("hello world", content1);
		assertEquals("nested content", content2);
	}

	public void testDeleteDirectoryExcept() throws Exception {
		File root = new File(tempDir, "root");
		root.mkdirs();

		File file1 = new File(root, "file1.txt");
		file1.createNewFile();

		File keep = new File(root, "keep.zip");
		keep.createNewFile();

		File sub = new File(root, "sub");
		sub.mkdirs();
		File file2 = new File(sub, "file2.txt");
		file2.createNewFile();

		SnapshotManager.deleteDirectoryExcept(root, keep);

		assertFalse(file1.exists());
		assertFalse(sub.exists());
		assertFalse(file2.exists());
		assertTrue(keep.exists());
		assertTrue(root.exists());
	}
}
