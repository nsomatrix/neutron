/*
 * MIT License
 * Copyright (c) 2026 Yury Kharchenko
 * Copyright (c) 2026 Roman Lahin
 */

package com.mascotcapsule.micro3d.v3;

public class Util3D {
	
	private static final short[] sinTable = new short[4096];
	
	static {
		//Mascot Capsule docs state that result should be multiplied by 1024
		//But reference implementation uses 4096 scale factor
		
		for(int i = 0; i < 4096; i++) {
			sinTable[i] = (short) Math.floor(Math.sin(i * Math.PI / 2048.0) * 4096.0 + 0.5);
		}
	}
	
	private Util3D() {}

	public static final int sin(int p) {
		return sinTable[p & 4095];
	}

	public static final int cos(int p) {
		return sinTable[(p + 1024) & 4095];
	}
	
	public static final int sqrt(int p) {
		if (p == 0) return 0;
		
		double a;
		if (p < 0) {
			//Fix for numbers with sign bit used
			if (p > 0xfffd0002) return 0xffff;
			a = p & 0xffffffffL;
		} else {
			a = p;
		}
		
		return (int) (Math.sqrt(a) + 0.5);
	}

	public static java.io.InputStream getResourceAsStream(String name) {
		if (name == null || name.isEmpty()) {
			return null;
		}

		// Handle file: scheme
		if (name.startsWith("file:")) {
			try {
				String filePath = name.substring(5);
				while (filePath.startsWith("//")) {
					filePath = filePath.substring(1);
				}
				if (filePath.length() >= 3 && filePath.charAt(0) == '/' && ((filePath.charAt(1) >= 'a' && filePath.charAt(1) <= 'z') || (filePath.charAt(1) >= 'A' && filePath.charAt(1) <= 'Z')) && filePath.charAt(2) == ':') {
					filePath = filePath.substring(1);
				}
				java.io.File f = new java.io.File(filePath);
				if (f.exists() && f.isFile()) {
					return new java.io.FileInputStream(f);
				}
			} catch (Throwable ignored) {}
		}

		java.io.InputStream is = null;
		try {
			java.io.File f = new java.io.File(name);
			if (f.exists() && f.isFile()) {
				return new java.io.FileInputStream(f);
			}
		} catch (Throwable ignored) {}

		try {
			is = org.neutron.MIDletBridge.getResourceAsStream(Util3D.class, name.startsWith("/") ? name : "/" + name);
			if (is == null) {
				is = org.neutron.MIDletBridge.getResourceAsStream(Util3D.class, name.startsWith("/") ? name.substring(1) : name);
			}
		} catch (Throwable ignored) {}

		String rootPath = name.startsWith("/") ? name : "/" + name;
		String relPath = name.startsWith("/") ? name.substring(1) : name;

		if (is == null) {
			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl != null) {
				is = cl.getResourceAsStream(relPath);
				if (is == null) {
					is = cl.getResourceAsStream(rootPath);
				}
			}
		}
		if (is == null) {
			is = Util3D.class.getResourceAsStream(rootPath);
		}
		if (is == null) {
			is = Util3D.class.getResourceAsStream(relPath);
		}
		if (is == null && Util3D.class.getClassLoader() != null) {
			is = Util3D.class.getClassLoader().getResourceAsStream(relPath);
		}
		return is;
	}
}
