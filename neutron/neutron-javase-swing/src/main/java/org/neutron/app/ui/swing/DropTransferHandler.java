/**
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
 *
 *  @version $Id$
 */
package org.neutron.app.ui.swing;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import org.neutron.app.Common;
import org.neutron.app.ui.Message;
import org.neutron.app.util.IOUtils;
import org.neutron.log.Logger;

/**
 * Universal, cross-platform Drag-and-Drop handler for J2ME JAR and JAD files.
 * Supports Windows Explorer, macOS Finder, KDE Dolphin, GNOME Nautilus, and Web Browsers.
 */
public class DropTransferHandler extends TransferHandler {

	private static final long serialVersionUID = 1L;

	private static final DataFlavor uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String", null);

	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.COPY;
	}

	@Override
	public boolean canImport(TransferSupport support) {
		if (!support.isDrop()) {
			return false;
		}
		return canImport(null, support.getDataFlavors());
	}

	@Override
	public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {
		if (transferFlavors == null) {
			return false;
		}
		for (int i = 0; i < transferFlavors.length; i++) {
			DataFlavor flavor = transferFlavors[i];
			if (flavor == null) {
				continue;
			}
			// Windows Explorer, macOS Finder, Linux File Managers
			if (DataFlavor.javaFileListFlavor.equals(flavor)) {
				return true;
			}
			// Linux KDE Dolphin / GNOME Nautilus URI list
			if (uriListFlavor.equals(flavor) || "text/uri-list".equalsIgnoreCase(flavor.getMimeType())) {
				return true;
			}
			// Dragged URL from Web Browser / Network
			Class<?> repClass = flavor.getRepresentationClass();
			if (repClass != null && URL.class.isAssignableFrom(repClass)) {
				return true;
			}
			// Plain text / string path fallback
			if (DataFlavor.stringFlavor.equals(flavor)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean importData(TransferSupport support) {
		if (!canImport(support)) {
			return false;
		}
		return importData((JComponent) support.getComponent(), support.getTransferable());
	}

	@Override
	public boolean importData(JComponent comp, Transferable t) {
		if (t == null) {
			return false;
		}

		// Priority 1: Native OS File List (Windows Explorer, macOS Finder, Dolphin, Nautilus)
		if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
			try {
				Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
				if (data instanceof List) {
					List<?> list = (List<?>) data;
					if (handleFileList(list)) {
						return true;
					}
				}
			} catch (UnsupportedFlavorException | IOException e) {
				Logger.debug("javaFileListFlavor import failed, trying next flavor", e);
			}
		}

		// Priority 2: Linux text/uri-list (RFC 2483 format with CRLF, comments, percent-encoding)
		if (t.isDataFlavorSupported(uriListFlavor)) {
			try {
				Object data = t.getTransferData(uriListFlavor);
				if (handleUriListString(data)) {
					return true;
				}
			} catch (UnsupportedFlavorException | IOException e) {
				Logger.debug("uriListFlavor import failed, trying next flavor", e);
			}
		}

		// Priority 3: Dragged URL object (Browser or KDE)
		DataFlavor[] flavors = t.getTransferDataFlavors();
		for (int i = 0; i < flavors.length; i++) {
			DataFlavor flavor = flavors[i];
			Class<?> repClass = flavor != null ? flavor.getRepresentationClass() : null;
			if (repClass != null && URL.class.isAssignableFrom(repClass)) {
				try {
					Object urlObj = t.getTransferData(flavor);
					if (urlObj instanceof URL) {
						String urlString = ((URL) urlObj).toExternalForm();
						if (Common.isMIDletUrlExtension(urlString)) {
							Common.openMIDletUrlSafe(urlString);
							return true;
						}
					}
				} catch (UnsupportedFlavorException | IOException e) {
					Logger.debug("URL flavor import failed", e);
				}
			}
		}

		// Priority 4: String path / URL fallback (GNOME, Firefox, Terminal drag)
		if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			try {
				Object data = t.getTransferData(DataFlavor.stringFlavor);
				if (data instanceof String) {
					if (handleStringData((String) data)) {
						return true;
					}
				}
			} catch (UnsupportedFlavorException | IOException e) {
				Logger.debug("stringFlavor import failed", e);
			}
		}

		return false;
	}

	private boolean handleFileList(List<?> fileList) {
		if (fileList == null || fileList.isEmpty()) {
			return false;
		}

		File selectedJad = null;
		File selectedJar = null;
		File firstFile = null;

		for (int i = 0; i < fileList.size(); i++) {
			Object item = fileList.get(i);
			if (item instanceof File) {
				File f = (File) item;
				if (firstFile == null) {
					firstFile = f;
				}
				String name = f.getName().toLowerCase();
				if (name.endsWith(".jad")) {
					selectedJad = f;
					break; // JAD takes absolute priority when paired with JAR
				} else if (name.endsWith(".jar") && selectedJar == null) {
					selectedJar = f;
				}
			}
		}

		File fileToLaunch = selectedJad != null ? selectedJad : selectedJar;
		if (fileToLaunch != null) {
			Common.openMIDletUrlSafe(IOUtils.getCanonicalFileURL(fileToLaunch));
			return true;
		}

		if (firstFile != null) {
			Message.warn("Unable to open " + firstFile.getName() + ". Only .jar and .jad files are supported.");
			return true;
		}

		return false;
	}

	private boolean handleUriListString(Object data) {
		String text = null;
		if (data instanceof String) {
			text = (String) data;
		} else if (data instanceof Reader) {
			try (Reader r = (Reader) data) {
				StringBuilder sb = new StringBuilder();
				char[] buf = new char[1024];
				int n;
				while ((n = r.read(buf)) != -1) {
					sb.append(buf, 0, n);
				}
				text = sb.toString();
			} catch (IOException ignore) {
			}
		}

		if (text == null || text.trim().isEmpty()) {
			return false;
		}

		List<String> validEntries = extractUrisFromText(text);
		if (validEntries.isEmpty()) {
			return false;
		}

		// Prefer .jad over .jar if multiple entries dropped
		String chosen = null;
		for (String uri : validEntries) {
			if (uri.toLowerCase().endsWith(".jad")) {
				chosen = uri;
				break;
			}
			if (chosen == null && uri.toLowerCase().endsWith(".jar")) {
				chosen = uri;
			}
		}

		if (chosen != null) {
			Common.openMIDletUrlSafe(chosen);
			return true;
		}

		Message.warn("Unable to open " + validEntries.get(0) + ". Only .jar and .jad files are supported.");
		return true;
	}

	private boolean handleStringData(String data) {
		if (data == null || data.trim().isEmpty()) {
			return false;
		}

		List<String> validEntries = extractUrisFromText(data);
		if (!validEntries.isEmpty()) {
			String chosen = null;
			for (String uri : validEntries) {
				if (uri.toLowerCase().endsWith(".jad")) {
					chosen = uri;
					break;
				}
				if (chosen == null && uri.toLowerCase().endsWith(".jar")) {
					chosen = uri;
				}
			}
			if (chosen != null) {
				Common.openMIDletUrlSafe(chosen);
				return true;
			}
		}

		// Fallback: check first token as a filesystem path
		StringTokenizer st = new StringTokenizer(data.trim(), "\n\r");
		while (st.hasMoreTokens()) {
			String line = st.nextToken().trim();
			if (line.isEmpty() || line.startsWith("#")) {
				continue;
			}
			if (Common.isMIDletUrlExtension(line)) {
				File f = new File(line);
				if (f.exists()) {
					Common.openMIDletUrlSafe(IOUtils.getCanonicalFileURL(f));
				} else {
					Common.openMIDletUrlSafe(line);
				}
				return true;
			}
			Message.warn("Unable to open " + line + ". Only .jar and .jad files are supported.");
			return true;
		}

		return false;
	}

	private List<String> extractUrisFromText(String text) {
		List<String> result = new ArrayList<>();
		StringTokenizer st = new StringTokenizer(text, "\r\n");
		while (st.hasMoreTokens()) {
			String line = st.nextToken().trim();
			if (line.isEmpty() || line.startsWith("#")) {
				continue; // RFC 2483 comment or blank
			}

			String resolvedUrl = line;
			if (line.startsWith("file://")) {
				try {
					URI uri = URI.create(line.replace(" ", "%20"));
					File f = new File(uri);
					resolvedUrl = IOUtils.getCanonicalFileURL(f);
				} catch (Throwable t) {
					// Fallback for non-standard file:// formats (like file://localhost/...)
					String rawPath = line.substring("file://".length());
					if (rawPath.startsWith("localhost/")) {
						rawPath = rawPath.substring("localhost".length());
					}
					try {
						rawPath = URLDecoder.decode(rawPath, StandardCharsets.UTF_8.name());
					} catch (Exception ignore) {
					}
					File f = new File(rawPath);
					if (f.exists()) {
						resolvedUrl = IOUtils.getCanonicalFileURL(f);
					}
				}
			}

			if (Common.isMIDletUrlExtension(resolvedUrl)) {
				result.add(resolvedUrl);
			}
		}
		return result;
	}
}
