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
			// Extract bundled shared library from classpath based on OS and architecture
			String os = System.getProperty("os.name", "").toLowerCase();
			String arch = System.getProperty("os.arch", "").toLowerCase();
			String platformDir;
			String ext;
			String prefix = "lib";

			if (os.contains("win")) {
				if (arch.contains("aarch64") || arch.contains("arm")) {
					platformDir = "windows-arm64";
				} else if (arch.contains("64")) {
					platformDir = "windows-x86_64";
				} else {
					platformDir = "windows-x86";
				}
				prefix = "";
				ext = ".dll";
			} else if (os.contains("mac") || os.contains("darwin")) {
				platformDir = (arch.contains("aarch64") || arch.contains("arm")) ? "macos-arm64" : "macos-x86_64";
				ext = ".dylib";
			} else {
				platformDir = (arch.contains("aarch64") || arch.contains("arm")) ? "linux-aarch64" : "linux-x86_64";
				ext = ".so";
			}

			String libFilename = prefix + "javam3g" + ext;
			String resPath = "/natives/" + platformDir + "/" + libFilename;
			InputStream is = Platform.class.getResourceAsStream(resPath);
			if (is == null && os.contains("linux") && !platformDir.equals("linux-x86_64")) {
				// Fallback to linux-x86_64 on Linux only (e.g. running under x86 emulation)
				resPath = "/natives/linux-x86_64/libjavam3g.so";
				is = Platform.class.getResourceAsStream(resPath);
			}

			if (is != null) {
				File targetLib = null;
				try {
					targetLib = File.createTempFile("libjavam3g_", ext);
					targetLib.deleteOnExit();
				} catch (Throwable ignored) {}

				if (targetLib == null) {
					File userDir = new File(System.getProperty("user.home", "."), ".neutron/natives");
					userDir.mkdirs();
					targetLib = new File(userDir, libFilename);
				}

				try (InputStream in = is; FileOutputStream fos = new FileOutputStream(targetLib)) {
					byte[] buf = new byte[8192];
					int read;
					while ((read = in.read(buf)) != -1) {
						fos.write(buf, 0, read);
					}
				}

				try {
					System.load(targetLib.getAbsolutePath());
					libraryLoaded = true;
					return;
				} catch (UnsatisfiedLinkError ule) {
					// If loading from %TEMP% was blocked (e.g. Windows Attack Surface Reduction / noexec),
					// retry by extracting to the user home .neutron/natives directory.
					File userDir = new File(System.getProperty("user.home", "."), ".neutron/natives");
					File fallbackLib = new File(userDir, libFilename);
					if (!fallbackLib.getAbsolutePath().equals(targetLib.getAbsolutePath())) {
						userDir.mkdirs();
						InputStream isFallback = Platform.class.getResourceAsStream(resPath);
						if (isFallback != null) {
							try (InputStream in = isFallback; FileOutputStream fos = new FileOutputStream(fallbackLib)) {
								byte[] buf = new byte[8192];
								int read;
								while ((read = in.read(buf)) != -1) {
									fos.write(buf, 0, read);
								}
							}
							System.load(fallbackLib.getAbsolutePath());
							libraryLoaded = true;
							return;
						}
					}
					throw ule;
				}
			} else {
				System.err.println("[Neutron M3G] Native engine (" + libFilename + ") not bundled for " + os + " (" + arch + "). JSR-184 M3G requires native binaries.");
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

	static final synchronized void finalizeObject(long handle) {
		if (handle == 0 || !libraryLoaded) return;
		try {
			_finalizeObject(handle);
		} catch (Throwable ignored) {}
	}

	static final synchronized void finalizeObject(long handle, Interface aInterface) {
		if (handle == 0 || !libraryLoaded) return;
		if (aInterface != null && (!aInterface.isFullyInitialized() || aInterface.handle == 0)) {
			return;
		}
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

