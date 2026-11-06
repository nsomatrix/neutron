package javax.microedition.m3g;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;
import java.util.zip.Adler32;
import java.util.zip.Inflater;

public class Loader {

	public static final int TYPE_HEADER = 0;
	public static final int TYPE_ANIMATION_CONTROLLER = 1;
	public static final int TYPE_ANIMATION_TRACK = 2;
	public static final int TYPE_APPEARANCE = 3;
	public static final int TYPE_BACKGROUND = 4;
	public static final int TYPE_CAMERA = 5;
	public static final int TYPE_COMPOSITING_MODE = 6;
	public static final int TYPE_FOG = 7;
	public static final int TYPE_POLYGON_MODE = 8;
	public static final int TYPE_GROUP = 9;
	public static final int TYPE_IMAGE2D = 10;
	public static final int TYPE_TRIANGLE_STRIP_ARRAY = 11;
	public static final int TYPE_LIGHT = 12;
	public static final int TYPE_MATERIAL = 13;
	public static final int TYPE_MESH = 14;
	public static final int TYPE_MORPHING_MESH = 15;
	public static final int TYPE_NODE = 16;
	public static final int TYPE_OBJECT3D = 17;
	public static final int TYPE_RAY_INTERSECTION = 18;
	public static final int TYPE_SKINNED_MESH = 19;
	public static final int TYPE_VERTEX_ARRAY = 20;
	public static final int TYPE_VERTEX_BUFFER = 21;
	public static final int TYPE_WORLD = 22;
	public static final int TYPE_TEXTURE2D = 23;

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
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
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

		int avail = data.length - offset;
		if (avail < 8) {
			throw new IOException("Stream too short for M3G or image");
		}

		// 1. Check for standard 12-byte M3G file identifier:
		// 0xAB, 'J', 'S', 'R', '1', '8', '4', 0xBB, 0x0D, 0x0A, 0x1A, 0x0A
		// Or 8-byte variant:
		// 0xAB, 'J', '3', 'G', 0x0D, 0x0A, 0x1A, 0x0A
		boolean isM3G = false;
		int headerLength = 0;
		if ((data[offset] & 0xFF) == 0xAB) {
			if (avail >= 12 &&
				data[offset + 1] == 'J' && data[offset + 2] == 'S' && data[offset + 3] == 'R' &&
				data[offset + 4] == '1' && data[offset + 5] == '8' && data[offset + 6] == '4' &&
				(data[offset + 7] & 0xFF) == 0xBB &&
				data[offset + 8] == 0x0D && data[offset + 9] == 0x0A &&
				data[offset + 10] == 0x1A && data[offset + 11] == 0x0A) {
				isM3G = true;
				headerLength = 12;
			} else if (avail >= 8 &&
				data[offset + 1] == 'J' && data[offset + 2] == '3' && data[offset + 3] == 'G' &&
				data[offset + 4] == 0x0D && data[offset + 5] == 0x0A &&
				data[offset + 6] == 0x1A && data[offset + 7] == 0x0A) {
				isM3G = true;
				headerLength = 8;
			}
		}

		if (!isM3G) {
			// Image loader fallback (e.g. PNG / JPEG texture)
			try {
				javax.microedition.lcdui.Image img = javax.microedition.lcdui.Image.createImage(data, offset, data.length - offset);
				Image2D img2d = new Image2D(Image2D.RGBA, img);
				return new Object3D[] { img2d };
			} catch (Exception e) {
				throw new IOException("Invalid M3G file format");
			}
		}

		// It is M3G! Parse binary sections
		M3GParser parser = new M3GParser(data, offset + headerLength, offset + avail);
		return parser.parse();
	}

	private static class M3GParser {
		private final byte[] data;
		private int pos;
		private final int end;
		private final Vector objects = new Vector(); // 1-based indexing: object 1 is index 0

		M3GParser(byte[] data, int start, int end) {
			this.data = data;
			this.pos = start;
			this.end = end;
		}

		Object3D[] parse() throws IOException {
			// If file only had header (empty file / minimal scene), return default World
			if (pos >= end) {
				World world = new World();
				Camera camera = new Camera();
				world.addChild(camera);
				world.setActiveCamera(camera);
				return new Object3D[] { world };
			}

			// Parse sections sequentially
			while (pos < end) {
				if (end - pos < 13) {
					// Need at least: compressionScheme (1) + totalSectionLength (4) + uncompressedLength (4) + checksum (4) = 13 bytes
					break;
				}

				int sectionStart = pos;
				int compressionScheme = data[pos++] & 0xFF;
				int totalSectionLength = readInt32();
				int uncompressedLength = readInt32();

				if (totalSectionLength < 13 || sectionStart + totalSectionLength > end) {
					throw new IOException("Corrupted section header in M3G: totalLength=" + totalSectionLength);
				}

				int payloadLength = totalSectionLength - 13;
				byte[] payload = new byte[payloadLength];
				System.arraycopy(data, pos, payload, 0, payloadLength);
				pos += payloadLength;

				int expectedChecksum = readInt32();

				// Verify Adler-32 checksum over [sectionStart .. sectionStart + totalSectionLength - 4]
				Adler32 adler = new Adler32();
				adler.update(data, sectionStart, totalSectionLength - 4);
				int actualChecksum = (int) adler.getValue();
				if (expectedChecksum != 0 && expectedChecksum != actualChecksum) {
					throw new IOException("Adler-32 checksum mismatch in M3G section: expected " +
						Integer.toHexString(expectedChecksum) + " but got " + Integer.toHexString(actualChecksum));
				}

				// Decompress payload if needed
				byte[] uncompressedData;
				if (compressionScheme == 0) {
					uncompressedData = payload;
				} else if (compressionScheme == 1) {
					uncompressedData = inflate(payload, uncompressedLength);
				} else {
					throw new IOException("Unsupported M3G compression scheme: " + compressionScheme);
				}

				// Parse objects within this section
				parseObjects(uncompressedData);
			}

			// If no objects were parsed, return default World
			if (objects.isEmpty()) {
				World world = new World();
				Camera camera = new Camera();
				world.addChild(camera);
				world.setActiveCamera(camera);
				return new Object3D[] { world };
			}

			// Collect root objects
			Vector roots = new Vector();
			World primaryWorld = null;

			for (int i = 0; i < objects.size(); i++) {
				Object3D obj = (Object3D) objects.elementAt(i);
				if (obj instanceof World) {
					if (primaryWorld == null) {
						primaryWorld = (World) obj;
					} else {
						roots.addElement(obj);
					}
				} else if (obj instanceof Node) {
					Node node = (Node) obj;
					if (node.getParent() == null) {
						roots.addElement(node);
					}
				}
			}

			// Prepend primaryWorld at index 0 if found
			if (primaryWorld != null) {
				roots.insertElementAt(primaryWorld, 0);
			}

			// If no root nodes, return all loaded objects
			if (roots.isEmpty()) {
				for (int i = 0; i < objects.size(); i++) {
					roots.addElement(objects.elementAt(i));
				}
			}

			Object3D[] result = new Object3D[roots.size()];
			roots.copyInto(result);
			return result;
		}

		private int readInt32() {
			int b1 = data[pos++] & 0xFF;
			int b2 = data[pos++] & 0xFF;
			int b3 = data[pos++] & 0xFF;
			int b4 = data[pos++] & 0xFF;
			return (b1 << 24) | (b2 << 16) | (b3 << 8) | b4;
		}

		private static byte[] inflate(byte[] compressed, int uncompressedLength) throws IOException {
			try {
				Inflater inflater = new Inflater();
				inflater.setInput(compressed);
				byte[] output = new byte[uncompressedLength];
				int count = inflater.inflate(output);
				inflater.end();
				if (count != uncompressedLength) {
					byte[] trimmed = new byte[count];
					System.arraycopy(output, 0, trimmed, 0, count);
					return trimmed;
				}
				return output;
			} catch (Exception e) {
				throw new IOException("Failed to decompress M3G zlib section: " + e.getMessage());
			}
		}

		private void parseObjects(byte[] data) throws IOException {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
			while (dis.available() > 0) {
				int objectType = dis.readByte() & 0xFF;
				int length = dis.readInt();
				if (length < 0) {
					throw new IOException("Invalid object chunk length: " + length);
				}
				byte[] objBytes = new byte[length];
				dis.readFully(objBytes);

				Object3D obj = parseSingleObject(objectType, objBytes);
				if (obj != null) {
					objects.addElement(obj);
				}
			}
		}

		private Object3D parseSingleObject(int objectType, byte[] bytes) {
			DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
			try {
				switch (objectType) {
				case TYPE_HEADER:
					return null; // Header chunk is not an Object3D in the table

				case TYPE_ANIMATION_CONTROLLER: {
					AnimationController ctrl = new AnimationController();
					readObject3DProperties(ctrl, dis);
					return ctrl;
				}

				case TYPE_ANIMATION_TRACK: {
					return null;
				}

				case TYPE_APPEARANCE: {
					Appearance app = new Appearance();
					readObject3DProperties(app, dis);
					if (dis.available() >= 4) {
						int matRef = dis.readInt();
						if (matRef > 0 && matRef <= objects.size()) {
							Object3D o = (Object3D) objects.elementAt(matRef - 1);
							if (o instanceof Material) app.setMaterial((Material) o);
						}
					}
					if (dis.available() >= 4) {
						int compRef = dis.readInt();
						if (compRef > 0 && compRef <= objects.size()) {
							Object3D o = (Object3D) objects.elementAt(compRef - 1);
							if (o instanceof CompositingMode) app.setCompositingMode((CompositingMode) o);
						}
					}
					if (dis.available() >= 4) {
						int polyRef = dis.readInt();
						if (polyRef > 0 && polyRef <= objects.size()) {
							Object3D o = (Object3D) objects.elementAt(polyRef - 1);
							if (o instanceof PolygonMode) app.setPolygonMode((PolygonMode) o);
						}
					}
					if (dis.available() >= 4) {
						int fogRef = dis.readInt();
						if (fogRef > 0 && fogRef <= objects.size()) {
							Object3D o = (Object3D) objects.elementAt(fogRef - 1);
							if (o instanceof Fog) app.setFog((Fog) o);
						}
					}
					return app;
				}

				case TYPE_BACKGROUND: {
					Background bg = new Background();
					readObject3DProperties(bg, dis);
					if (dis.available() >= 4) {
						bg.setColor(dis.readInt());
					}
					if (dis.available() >= 4) {
						int imgRef = dis.readInt();
						if (imgRef > 0 && imgRef <= objects.size()) {
							Object3D o = (Object3D) objects.elementAt(imgRef - 1);
							if (o instanceof Image2D) bg.setImage((Image2D) o);
						}
					}
					return bg;
				}

				case TYPE_CAMERA: {
					Camera cam = new Camera();
					readNodeProperties(cam, dis);
					if (dis.available() >= 1) {
						int projType = dis.readByte() & 0xFF;
						if (dis.available() >= 16) {
							float p1 = dis.readFloat();
							float p2 = dis.readFloat();
							float p3 = dis.readFloat();
							float p4 = dis.readFloat();
							if (projType == Camera.PARALLEL) {
								cam.setParallel(p1, p2, p3, p4);
							} else {
								cam.setPerspective(p1, p2, p3, p4);
							}
						}
					}
					return cam;
				}

				case TYPE_COMPOSITING_MODE: {
					CompositingMode cm = new CompositingMode();
					readObject3DProperties(cm, dis);
					if (dis.available() >= 1) {
						cm.setBlending(dis.readByte() & 0xFF);
					}
					if (dis.available() >= 4) {
						cm.setAlphaThreshold(dis.readFloat());
					}
					return cm;
				}

				case TYPE_FOG: {
					Fog fog = new Fog();
					readObject3DProperties(fog, dis);
					if (dis.available() >= 4) {
						fog.setColor(dis.readInt());
					}
					if (dis.available() >= 1) {
						fog.setMode(dis.readByte() & 0xFF);
					}
					if (dis.available() >= 8) {
						fog.setLinear(dis.readFloat(), dis.readFloat());
					}
					return fog;
				}

				case TYPE_POLYGON_MODE: {
					PolygonMode pm = new PolygonMode();
					readObject3DProperties(pm, dis);
					if (dis.available() >= 1) {
						pm.setCulling(dis.readByte() & 0xFF);
					}
					if (dis.available() >= 1) {
						pm.setShading(dis.readByte() & 0xFF);
					}
					if (dis.available() >= 1) {
						pm.setWinding(dis.readByte() & 0xFF);
					}
					return pm;
				}

				case TYPE_GROUP: {
					Group group = new Group();
					readNodeProperties(group, dis);
					if (dis.available() >= 4) {
						int childCount = dis.readInt();
						for (int i = 0; i < childCount && dis.available() >= 4; i++) {
							int childRef = dis.readInt();
							if (childRef > 0 && childRef <= objects.size()) {
								Object3D o = (Object3D) objects.elementAt(childRef - 1);
								if (o instanceof Node && ((Node) o).getParent() == null) {
									group.addChild((Node) o);
								}
							}
						}
					}
					return group;
				}

				case TYPE_IMAGE2D: {
					int format = Image2D.RGBA;
					int width = 1;
					int height = 1;
					if (dis.available() >= 9) {
						format = dis.readByte() & 0xFF;
						width = dis.readInt();
						height = dis.readInt();
					}
					if (width <= 0) width = 1;
					if (height <= 0) height = 1;
					if (format < Image2D.ALPHA || format > Image2D.RGBA) format = Image2D.RGBA;
					Image2D img = new Image2D(format, width, height);
					readObject3DProperties(img, dis);
					return img;
				}

				case TYPE_TRIANGLE_STRIP_ARRAY: {
					int firstIndex = 0;
					int[] stripLengths = new int[] { 3 };
					if (dis.available() >= 8) {
						firstIndex = dis.readInt();
						int numStrips = dis.readInt();
						if (numStrips > 0 && numStrips < 100000) {
							stripLengths = new int[numStrips];
							for (int s = 0; s < numStrips && dis.available() >= 4; s++) {
								stripLengths[s] = dis.readInt();
							}
						}
					}
					TriangleStripArray tsa = new TriangleStripArray(firstIndex, stripLengths);
					readObject3DProperties(tsa, dis);
					return tsa;
				}

				case TYPE_LIGHT: {
					Light light = new Light();
					readNodeProperties(light, dis);
					if (dis.available() >= 4) {
						light.setColor(dis.readInt());
					}
					if (dis.available() >= 1) {
						light.setMode(dis.readByte() & 0xFF);
					}
					if (dis.available() >= 4) {
						light.setIntensity(dis.readFloat());
					}
					return light;
				}

				case TYPE_MATERIAL: {
					Material mat = new Material();
					readObject3DProperties(mat, dis);
					if (dis.available() >= 4) mat.setColor(Material.AMBIENT, dis.readInt());
					if (dis.available() >= 4) mat.setColor(Material.DIFFUSE, dis.readInt());
					if (dis.available() >= 4) mat.setColor(Material.SPECULAR, dis.readInt());
					if (dis.available() >= 4) mat.setColor(Material.EMISSIVE, dis.readInt());
					if (dis.available() >= 4) mat.setShininess(dis.readFloat());
					return mat;
				}

				case TYPE_MESH: {
					VertexBuffer vb = null;
					IndexBuffer[] submeshes = null;
					Appearance[] appearances = null;

					if (dis.available() >= 8) {
						int vbRef = dis.readInt();
						if (vbRef > 0 && vbRef <= objects.size()) {
							Object3D o = (Object3D) objects.elementAt(vbRef - 1);
							if (o instanceof VertexBuffer) vb = (VertexBuffer) o;
						}
						int submeshCount = dis.readInt();
						if (submeshCount > 0 && submeshCount < 1000) {
							submeshes = new IndexBuffer[submeshCount];
							appearances = new Appearance[submeshCount];
							for (int i = 0; i < submeshCount && dis.available() >= 8; i++) {
								int ibRef = dis.readInt();
								int appRef = dis.readInt();
								if (ibRef > 0 && ibRef <= objects.size()) {
									Object3D o = (Object3D) objects.elementAt(ibRef - 1);
									if (o instanceof IndexBuffer) submeshes[i] = (IndexBuffer) o;
								}
								if (appRef > 0 && appRef <= objects.size()) {
									Object3D o = (Object3D) objects.elementAt(appRef - 1);
									if (o instanceof Appearance) appearances[i] = (Appearance) o;
								}
							}
						}
					}

					if (vb == null) {
						vb = new VertexBuffer();
					}
					if (submeshes == null || submeshes.length == 0 || submeshes[0] == null) {
						submeshes = new IndexBuffer[] { new TriangleStripArray(0, new int[] { 3 }) };
					}
					if (appearances == null || appearances.length == 0) {
						appearances = new Appearance[] { new Appearance() };
					}

					Mesh mesh = new Mesh(vb, submeshes, appearances);
					readNodeProperties(mesh, dis);
					return mesh;
				}

				case TYPE_MORPHING_MESH: {
					VertexBuffer vb = new VertexBuffer();
					TriangleStripArray tsa = new TriangleStripArray(0, new int[] { 3 });
					Appearance app = new Appearance();
					Mesh mesh = new Mesh(vb, tsa, app);
					readNodeProperties(mesh, dis);
					return mesh;
				}

				case TYPE_VERTEX_ARRAY: {
					int numVertices = 3;
					int numComponents = 3;
					int componentSize = 2;
					if (dis.available() >= 4) {
						componentSize = dis.readByte() & 0xFF;
						numComponents = dis.readByte() & 0xFF;
						numVertices = dis.readShort() & 0xFFFF;
					}
					if (componentSize != 1 && componentSize != 2) componentSize = 2;
					if (numComponents < 2 || numComponents > 4) numComponents = 3;
					if (numVertices <= 0) numVertices = 3;

					VertexArray va = new VertexArray(numVertices, numComponents, componentSize);
					readObject3DProperties(va, dis);

					int totalVals = numVertices * numComponents;
					if (componentSize == 2) {
						short[] vals = new short[totalVals];
						for (int i = 0; i < totalVals && dis.available() >= 2; i++) {
							vals[i] = dis.readShort();
						}
						va.set(0, numVertices, vals);
					} else {
						byte[] vals = new byte[totalVals];
						for (int i = 0; i < totalVals && dis.available() >= 1; i++) {
							vals[i] = dis.readByte();
						}
						va.set(0, numVertices, vals);
					}
					return va;
				}

				case TYPE_VERTEX_BUFFER: {
					VertexBuffer vb = new VertexBuffer();
					readObject3DProperties(vb, dis);
					if (dis.available() >= 4) {
						int posRef = dis.readInt();
						float scale = (dis.available() >= 4) ? dis.readFloat() : 1.0f;
						float[] bias = new float[3];
						if (dis.available() >= 12) {
							bias[0] = dis.readFloat();
							bias[1] = dis.readFloat();
							bias[2] = dis.readFloat();
						}
						if (posRef > 0 && posRef <= objects.size()) {
							Object3D o = (Object3D) objects.elementAt(posRef - 1);
							if (o instanceof VertexArray) {
								vb.setPositions((VertexArray) o, scale, bias);
							}
						}
					}
					if (dis.available() >= 4) {
						int normRef = dis.readInt();
						if (normRef > 0 && normRef <= objects.size()) {
							Object3D o = (Object3D) objects.elementAt(normRef - 1);
							if (o instanceof VertexArray) vb.setNormals((VertexArray) o);
						}
					}
					if (dis.available() >= 4) {
						int colRef = dis.readInt();
						if (colRef > 0 && colRef <= objects.size()) {
							Object3D o = (Object3D) objects.elementAt(colRef - 1);
							if (o instanceof VertexArray) vb.setColors((VertexArray) o);
						}
					}
					if (dis.available() >= 4) {
						vb.setDefaultColor(dis.readInt());
					}
					return vb;
				}

				case TYPE_WORLD: {
					World world = new World();
					readNodeProperties(world, dis);
					if (dis.available() >= 4) {
						int childCount = dis.readInt();
						for (int i = 0; i < childCount && dis.available() >= 4; i++) {
							int childRef = dis.readInt();
							if (childRef > 0 && childRef <= objects.size()) {
								Object3D o = (Object3D) objects.elementAt(childRef - 1);
								if (o instanceof Node && ((Node) o).getParent() == null) {
									world.addChild((Node) o);
								}
							}
						}
					}
					if (dis.available() >= 4) {
						int camRef = dis.readInt();
						if (camRef > 0 && camRef <= objects.size()) {
							Object3D o = (Object3D) objects.elementAt(camRef - 1);
							if (o instanceof Camera) world.setActiveCamera((Camera) o);
						}
					}
					if (dis.available() >= 4) {
						int bgRef = dis.readInt();
						if (bgRef > 0 && bgRef <= objects.size()) {
							Object3D o = (Object3D) objects.elementAt(bgRef - 1);
							if (o instanceof Background) world.setBackground((Background) o);
						}
					}
					return world;
				}

				case TYPE_TEXTURE2D: {
					if (dis.available() >= 4) {
						int imgRef = dis.readInt();
						if (imgRef > 0 && imgRef <= objects.size()) {
							Object3D o = (Object3D) objects.elementAt(imgRef - 1);
							if (o instanceof Image2D) {
								Texture2D tex = new Texture2D((Image2D) o);
								readObject3DProperties(tex, dis);
								return tex;
							}
						}
					}
					return null;
				}

				default:
					return null;
				}
			} catch (Exception e) {
				// Non-fatal property read failure for individual chunk
				return null;
			}
		}

		private void readObject3DProperties(Object3D obj, DataInputStream dis) {
			try {
				if (dis.available() >= 4) {
					obj.setUserID(dis.readInt());
				}
			} catch (Exception ignored) {}
		}

		private void readNodeProperties(Node node, DataInputStream dis) {
			readObject3DProperties(node, dis);
			try {
				if (dis.available() >= 1) {
					node.setRenderingEnable(dis.readBoolean());
				}
				if (dis.available() >= 1) {
					node.setPickingEnable(dis.readBoolean());
				}
				if (dis.available() >= 4) {
					node.setAlphaFactor(dis.readFloat());
				}
			} catch (Exception ignored) {}
		}
	}
}
