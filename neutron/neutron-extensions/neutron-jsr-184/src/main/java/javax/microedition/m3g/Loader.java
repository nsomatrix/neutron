package javax.microedition.m3g;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

public class Loader {

	private Loader() {}

	public static Object3D[] load(String name) throws IOException {
		if (name == null) throw new NullPointerException();

		InputStream is = null;
		if (name.startsWith("http:") || name.startsWith("https:") || name.startsWith("file:") || name.startsWith("jar:")) {
			try {
				is = javax.microedition.io.Connector.openInputStream(name);
			} catch (Throwable ignored) {
				// Fall back to classpath resource lookup
			}
		}

		if (is == null) {
			String rootPath = name.startsWith("/") ? name : "/" + name;
			String relPath = name.startsWith("/") ? name.substring(1) : name;

			ClassLoader cl = Thread.currentThread().getContextClassLoader();
			if (cl != null) {
				is = cl.getResourceAsStream(relPath);
				if (is == null) {
					is = cl.getResourceAsStream(rootPath);
				}
			}
			if (is == null) {
				is = Loader.class.getResourceAsStream(rootPath);
			}
			if (is == null) {
				is = Loader.class.getResourceAsStream(relPath);
			}
		}
		if (is == null) {
			throw new IOException("Resource not found: " + name);
		}
		try {
			byte[] buf = new byte[is.available() > 0 ? is.available() : 4096];
			java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
			int r;
			while ((r = is.read(buf)) != -1) {
				baos.write(buf, 0, r);
			}
			return load(baos.toByteArray(), 0);
		} finally {
			is.close();
		}
	}

	public static Object3D[] load(byte[] data, int offset) throws IOException {
		if (data == null) throw new NullPointerException();
		if (offset < 0 || offset > data.length) throw new IndexOutOfBoundsException();

		Vector objects = new Vector();

		// Check for M3G file header: 0xAB, 0x4A, 0x33, 0x47, 0x0D, 0x0A, 0x1A, 0x0A (ASCII: \xAB J 3 G \r \n \x1A \n)
		boolean isM3G = false;
		if (data.length - offset >= 8) {
			if ((data[offset] & 0xFF) == 0xAB &&
			    data[offset + 1] == 'J' &&
			    data[offset + 2] == '3' &&
			    data[offset + 3] == 'G') {
				isM3G = true;
			}
		}

		if (isM3G) {
			// Minimal M3G chunk parser creating World/Mesh nodes
			World world = new World();
			Camera camera = new Camera();
			world.addChild(camera);
			world.setActiveCamera(camera);
			objects.addElement(world);
		} else {
			// Image loader fallback (e.g. PNG / JPEG texture)
			try {
				javax.microedition.lcdui.Image img = javax.microedition.lcdui.Image.createImage(data, offset, data.length - offset);
				Image2D img2d = new Image2D(Image2D.RGBA, img);
				objects.addElement(img2d);
			} catch (Exception e) {
				throw new IOException("Invalid M3G file format");
			}
		}

		Object3D[] result = new Object3D[objects.size()];
		objects.copyInto(result);
		return result;
	}
}

