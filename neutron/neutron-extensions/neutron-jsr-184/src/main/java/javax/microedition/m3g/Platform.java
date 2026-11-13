/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under Apache 2.0 / EPL 1.0
 */
package javax.microedition.m3g;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Desktop platform loader for native libjavam3g.
 */
class Platform {
	private static boolean libraryLoaded = false;

	static void executeInUIThread(M3gRunnable obj) {
		if (obj != null) {
			obj.run();
			obj.checkAndThrow();
		}
	}

	static boolean uiThreadAvailable() {
		loadNativeLibrary();
		return true;
	}

	static synchronized void loadNativeLibrary() {
		if (libraryLoaded) return;
		try {
			// Try standard library load
			System.loadLibrary("javam3g");
			libraryLoaded = true;
			return;
		} catch (Throwable ignored) {}

		try {
			// Extract bundled shared library from classpath
			String resPath = "/natives/linux-x86_64/libjavam3g.so";
			InputStream is = Platform.class.getResourceAsStream(resPath);
			if (is != null) {
				File tempLib = File.createTempFile("libjavam3g_", ".so");
				tempLib.deleteOnExit();
				try (FileOutputStream fos = new FileOutputStream(tempLib)) {
					byte[] buf = new byte[8192];
					int read;
					while ((read = is.read(buf)) != -1) {
						fos.write(buf, 0, read);
					}
				}
				System.load(tempLib.getAbsolutePath());
				libraryLoaded = true;
				return;
			}
		} catch (Throwable t) {
			System.err.println("[Neutron M3G] Failed to load native library: " + t.getMessage());
		}
	}

	static final void registerFinalizer(Object3D obj) {}
	static final void registerFinalizer(Graphics3D g3d) {}
	static final void registerFinalizer(Interface m3g) {}
	static final void registerFinalizer(Loader loader) {}

	static final void sync(Graphics g) {}
	static final void sync(Image img) {}

	static final native void finalizeInterface(long handle);

	static final void finalizeObject(long handle) {
		try {
			_finalizeObject(handle);
		} catch (Throwable ignored) {}
	}

	static final void finalizeObject(long handle, Interface aInterface) {
		try {
			_finalizeObject(handle);
		} catch (Throwable ignored) {}
	}

	static final void heuristicGC() {}

	private static final native void _finalizeObject(long handle);

	static {
		loadNativeLibrary();
	}
}

