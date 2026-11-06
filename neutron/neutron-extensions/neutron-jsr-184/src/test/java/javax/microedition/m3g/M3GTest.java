package javax.microedition.m3g;

import org.junit.Test;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.zip.Adler32;
import java.util.zip.Deflater;
import static org.junit.Assert.*;

public class M3GTest {

	@Test
	public void testTransformMatrixOperations() {
		Transform t = new Transform();
		float[] m = new float[16];
		t.get(m);
		assertEquals(1.0f, m[0], 0.0001f);
		assertEquals(1.0f, m[5], 0.0001f);
		assertEquals(1.0f, m[10], 0.0001f);
		assertEquals(1.0f, m[15], 0.0001f);

		t.postTranslate(10.0f, 20.0f, 30.0f);
		t.get(m);
		assertEquals(10.0f, m[3], 0.0001f);
		assertEquals(20.0f, m[7], 0.0001f);
		assertEquals(30.0f, m[11], 0.0001f);
	}

	@Test
	public void testGraphics3DBindRenderRelease() {
		Graphics3D g3d = Graphics3D.getInstance();
		assertNotNull(g3d);

		Graphics g = new Graphics() {
			public int getClipX() { return 0; }
			public int getClipY() { return 0; }
			public int getClipWidth() { return 100; }
			public int getClipHeight() { return 100; }
			public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {}
		};
		g3d.bindTarget(g);
		assertEquals(g, g3d.getTarget());

		World world = new World();
		Camera cam = new Camera();
		cam.setPerspective(45.0f, 1.0f, 0.1f, 100.0f);
		world.addChild(cam);
		world.setActiveCamera(cam);

		Background bg = new Background();
		bg.setColor(0xFF112233);
		world.setBackground(bg);

		// Render world
		g3d.render(world);
		g3d.releaseTarget();
		assertNull(g3d.getTarget());
	}

	@Test
	public void testTriangleRasterization() {
		Graphics3D g3d = Graphics3D.getInstance();
		Graphics g = new Graphics() {
			public int getClipX() { return 0; }
			public int getClipY() { return 0; }
			public int getClipWidth() { return 64; }
			public int getClipHeight() { return 64; }
			public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {}
		};
		g3d.bindTarget(g);

		VertexBuffer vb = new VertexBuffer();
		VertexArray va = new VertexArray(3, 3, 2);
		short[] pos = new short[] {
			0, 10, -20,
			-10, -10, -20,
			10, -10, -20
		};
		va.set(0, 3, pos);
		vb.setPositions(va, 1.0f, null);
		vb.setDefaultColor(0xFFFF0000);

		int[] strip = new int[] { 3 };
		TriangleStripArray tsa = new TriangleStripArray(0, strip);
		Appearance app = new Appearance();

		Camera cam = new Camera();
		cam.setPerspective(60.0f, 1.0f, 0.1f, 100.0f);
		g3d.setCamera(cam, null);

		g3d.render(vb, tsa, app, null);
		g3d.releaseTarget();
	}

	@Test
	public void testViewportOffsetBlittingAndClear() {
		Graphics3D g3d = Graphics3D.getInstance();
		final int[] capturedOffset = new int[1];
		final int[] capturedX = new int[1];
		final int[] capturedY = new int[1];
		final int[] capturedW = new int[1];
		final int[] capturedH = new int[1];

		Graphics g = new Graphics() {
			public int getClipX() { return 0; }
			public int getClipY() { return 0; }
			public int getClipWidth() { return 100; }
			public int getClipHeight() { return 100; }
			public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
				capturedOffset[0] = offset;
				capturedX[0] = x;
				capturedY[0] = y;
				capturedW[0] = width;
				capturedH[0] = height;
			}
		};

		g3d.bindTarget(g);
		g3d.setViewport(15, 25, 40, 50);
		assertEquals(15, g3d.getViewportX());
		assertEquals(25, g3d.getViewportY());
		assertEquals(40, g3d.getViewportWidth());
		assertEquals(50, g3d.getViewportHeight());

		Background bg = new Background();
		bg.setColor(0xFF00FF00);
		g3d.clear(bg);

		g3d.releaseTarget();

		// Target width is 100, so offset for (15, 25) must be 25 * 100 + 15 = 2515
		assertEquals(25 * 100 + 15, capturedOffset[0]);
		assertEquals(15, capturedX[0]);
		assertEquals(25, capturedY[0]);
		assertEquals(40, capturedW[0]);
		assertEquals(50, capturedH[0]);
	}

	@Test(expected = java.io.IOException.class)
	public void testLoaderMissingResourceThrowsIOException() throws Exception {
		Loader.load("nonexistent_model.m3g");
	}

	@Test(expected = java.io.IOException.class)
	public void testLoaderMissingRootResourceThrowsIOException() throws Exception {
		Loader.load("/nonexistent_model.m3g");
	}

	private void writeShortLE(DataOutputStream dos, int val) throws Exception {
		dos.writeByte(val & 0xFF);
		dos.writeByte((val >> 8) & 0xFF);
	}

	private void writeIntLE(DataOutputStream dos, int val) throws Exception {
		dos.writeByte(val & 0xFF);
		dos.writeByte((val >> 8) & 0xFF);
		dos.writeByte((val >> 16) & 0xFF);
		dos.writeByte((val >> 24) & 0xFF);
	}

	private void writeFloatLE(DataOutputStream dos, float val) throws Exception {
		writeIntLE(dos, Float.floatToIntBits(val));
	}

	private void writeObject3D(DataOutputStream dos, int userID) throws Exception {
		writeIntLE(dos, userID);
		writeIntLE(dos, 0); // animationTrackCount
		writeIntLE(dos, 0); // userParameterCount
	}

	private void writeTransformable(DataOutputStream dos, int userID) throws Exception {
		writeObject3D(dos, userID);
		dos.writeBoolean(false); // hasComponentTransform
		dos.writeBoolean(false); // hasGeneralTransform
	}

	private void writeNode(DataOutputStream dos, int userID) throws Exception {
		writeTransformable(dos, userID);
		dos.writeBoolean(true); // renderingEnable
		dos.writeBoolean(true); // pickingEnable
		dos.writeByte(255); // alphaFactor (255 = 1.0f)
		writeIntLE(dos, -1); // scope
		dos.writeBoolean(false); // hasAlignment
	}

	private void writeGroup(DataOutputStream dos, int userID, int[] children) throws Exception {
		writeNode(dos, userID);
		writeIntLE(dos, children.length);
		for (int child : children) {
			writeIntLE(dos, child);
		}
	}

	private byte[] createTestM3GPayload() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);

		// 1. Camera (ID 2 in Loader)
		ByteArrayOutputStream camOut = new ByteArrayOutputStream();
		DataOutputStream camDos = new DataOutputStream(camOut);
		writeNode(camDos, 101);
		camDos.writeByte(Camera.PERSPECTIVE); // 43
		writeFloatLE(camDos, 60.0f); // fovy
		writeFloatLE(camDos, 1.33f); // aspect
		writeFloatLE(camDos, 0.1f); // near
		writeFloatLE(camDos, 100.0f); // far
		byte[] camBytes = camOut.toByteArray();
		dos.writeByte(Loader.TYPE_CAMERA);
		writeIntLE(dos, camBytes.length);
		dos.write(camBytes);

		// 2. Background (ID 3 in Loader)
		ByteArrayOutputStream bgOut = new ByteArrayOutputStream();
		DataOutputStream bgDos = new DataOutputStream(bgOut);
		writeObject3D(bgDos, 102);
		// readRGBA: r, g, b, a -> ARGB 0xFF336699
		bgDos.writeByte(0x33); bgDos.writeByte(0x66); bgDos.writeByte(0x99); bgDos.writeByte(0xFF);
		writeIntLE(bgDos, 0); // imageRef = 0
		bgDos.writeByte(Background.BORDER);
		bgDos.writeByte(Background.BORDER);
		writeIntLE(bgDos, 0); writeIntLE(bgDos, 0); writeIntLE(bgDos, 0); writeIntLE(bgDos, 0); // crop
		bgDos.writeBoolean(true); // depthClear
		bgDos.writeBoolean(true); // colorClear
		byte[] bgBytes = bgOut.toByteArray();
		dos.writeByte(Loader.TYPE_BACKGROUND);
		writeIntLE(dos, bgBytes.length);
		dos.write(bgBytes);

		// 3. VertexArray (ID 4 in Loader)
		ByteArrayOutputStream vaOut = new ByteArrayOutputStream();
		DataOutputStream vaDos = new DataOutputStream(vaOut);
		writeObject3D(vaDos, 103);
		vaDos.writeByte(2); // componentSize
		vaDos.writeByte(3); // components
		vaDos.writeByte(0); // encoding (0 = raw)
		writeShortLE(vaDos, 3); // vertices count
		writeShortLE(vaDos, 0); writeShortLE(vaDos, 10); writeShortLE(vaDos, -20);
		writeShortLE(vaDos, -10); writeShortLE(vaDos, -10); writeShortLE(vaDos, -20);
		writeShortLE(vaDos, 10); writeShortLE(vaDos, -10); writeShortLE(vaDos, -20);
		byte[] vaBytes = vaOut.toByteArray();
		dos.writeByte(Loader.TYPE_VERTEX_ARRAY);
		writeIntLE(dos, vaBytes.length);
		dos.write(vaBytes);

		// 4. VertexBuffer (ID 5 in Loader)
		ByteArrayOutputStream vbOut = new ByteArrayOutputStream();
		DataOutputStream vbDos = new DataOutputStream(vbOut);
		writeObject3D(vbDos, 104);
		// defaultColor RGBA
		vbDos.writeByte(0xFF); vbDos.writeByte(0x00); vbDos.writeByte(0x00); vbDos.writeByte(0xFF);
		writeIntLE(vbDos, 4); // positionsRef (VertexArray ID 4)
		writeFloatLE(vbDos, 0.0f); writeFloatLE(vbDos, 0.0f); writeFloatLE(vbDos, 0.0f); // bias
		writeFloatLE(vbDos, 1.0f); // scale
		writeIntLE(vbDos, 0); // normalsRef
		writeIntLE(vbDos, 0); // colorsRef
		writeIntLE(vbDos, 0); // texCoordCount
		byte[] vbBytes = vbOut.toByteArray();
		dos.writeByte(Loader.TYPE_VERTEX_BUFFER);
		writeIntLE(dos, vbBytes.length);
		dos.write(vbBytes);

		// 5. TriangleStripArray (ID 6 in Loader)
		ByteArrayOutputStream tsaOut = new ByteArrayOutputStream();
		DataOutputStream tsaDos = new DataOutputStream(tsaOut);
		writeObject3D(tsaDos, 105);
		tsaDos.writeByte(0); // encoding = 0 (firstIndex)
		writeIntLE(tsaDos, 0); // firstIndex
		writeIntLE(tsaDos, 1); // numStripLengths
		writeIntLE(tsaDos, 3); // stripLengths[0]
		byte[] tsaBytes = tsaOut.toByteArray();
		dos.writeByte(Loader.TYPE_TRIANGLE_STRIP_ARRAY);
		writeIntLE(dos, tsaBytes.length);
		dos.write(tsaBytes);

		// 6. Appearance (ID 7 in Loader)
		ByteArrayOutputStream appOut = new ByteArrayOutputStream();
		DataOutputStream appDos = new DataOutputStream(appOut);
		writeObject3D(appDos, 106);
		appDos.writeByte(0); // layer
		writeIntLE(appDos, 0); // compositingModeRef
		writeIntLE(appDos, 0); // fogRef
		writeIntLE(appDos, 0); // polygonModeRef
		writeIntLE(appDos, 0); // materialRef
		writeIntLE(appDos, 0); // numTextures
		byte[] appBytes = appOut.toByteArray();
		dos.writeByte(Loader.TYPE_APPEARANCE);
		writeIntLE(dos, appBytes.length);
		dos.write(appBytes);

		// 7. Mesh (ID 8 in Loader)
		ByteArrayOutputStream meshOut = new ByteArrayOutputStream();
		DataOutputStream meshDos = new DataOutputStream(meshOut);
		writeNode(meshDos, 107);
		writeIntLE(meshDos, 5); // vbRef
		writeIntLE(meshDos, 1); // subMeshCount
		writeIntLE(meshDos, 6); // ibRef
		writeIntLE(meshDos, 7); // appRef
		byte[] meshBytes = meshOut.toByteArray();
		dos.writeByte(Loader.TYPE_MESH);
		writeIntLE(dos, meshBytes.length);
		dos.write(meshBytes);

		// 8. World (ID 9 in Loader)
		ByteArrayOutputStream wOut = new ByteArrayOutputStream();
		DataOutputStream wDos = new DataOutputStream(wOut);
		writeGroup(wDos, 108, new int[] { 8 }); // child: Mesh ID 8
		writeIntLE(wDos, 2); // activeCamera: Camera ID 2
		writeIntLE(wDos, 3); // background: Background ID 3
		byte[] wBytes = wOut.toByteArray();
		dos.writeByte(Loader.TYPE_WORLD);
		writeIntLE(dos, wBytes.length);
		dos.write(wBytes);

		return baos.toByteArray();
	}

	private byte[] buildM3GFile(byte[] payload, boolean compress, boolean corruptChecksum) throws Exception {
		ByteArrayOutputStream fileBaos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(fileBaos);

		// 12-byte header
		byte[] identifier = new byte[] {
			(byte) 0xAB, 'J', 'S', 'R', '1', '8', '4', (byte) 0xBB, 0x0D, 0x0A, 0x1A, 0x0A
		};
		dos.write(identifier);

		// Prepare Section 1 (Objects payload)
		byte[] sec1Payload;
		int scheme1;
		if (compress) {
			scheme1 = 1;
			ByteArrayOutputStream defOut = new ByteArrayOutputStream();
			Deflater def = new Deflater();
			def.setInput(payload);
			def.finish();
			byte[] buf = new byte[1024];
			while (!def.finished()) {
				int count = def.deflate(buf);
				defOut.write(buf, 0, count);
			}
			def.end();
			sec1Payload = defOut.toByteArray();
		} else {
			scheme1 = 0;
			sec1Payload = payload;
		}

		int sec1TotalLen = 1 + 4 + 4 + sec1Payload.length + 4;

		// Section 0 (Header Object)
		ByteArrayOutputStream hOut = new ByteArrayOutputStream();
		DataOutputStream hDos = new DataOutputStream(hOut);
		hDos.writeByte(1); // versionHigh
		hDos.writeByte(0); // versionLow
		hDos.writeBoolean(false); // hasExternalReferences
		writeIntLE(hDos, 0); // placeholder for totalFileSize
		writeIntLE(hDos, payload.length); // approximateContentSize
		hDos.writeByte(0); // authoringField = "" (null terminator)
		byte[] hData = hOut.toByteArray();

		ByteArrayOutputStream hChunkOut = new ByteArrayOutputStream();
		DataOutputStream hChunkDos = new DataOutputStream(hChunkOut);
		hChunkDos.writeByte(Loader.TYPE_HEADER);
		writeIntLE(hChunkDos, hData.length);
		hChunkDos.write(hData);
		byte[] hObjBytes = hChunkOut.toByteArray();

		int sec0TotalLen = 1 + 4 + 4 + hObjBytes.length + 4;
		int totalFileSize = identifier.length + sec0TotalLen + sec1TotalLen;

		// Rewrite hData with actual totalFileSize
		hOut = new ByteArrayOutputStream();
		hDos = new DataOutputStream(hOut);
		hDos.writeByte(1);
		hDos.writeByte(0);
		hDos.writeBoolean(false);
		writeIntLE(hDos, totalFileSize);
		writeIntLE(hDos, payload.length);
		hDos.writeByte(0);
		hData = hOut.toByteArray();

		hChunkOut = new ByteArrayOutputStream();
		hChunkDos = new DataOutputStream(hChunkOut);
		hChunkDos.writeByte(Loader.TYPE_HEADER);
		writeIntLE(hChunkDos, hData.length);
		hChunkDos.write(hData);
		hObjBytes = hChunkOut.toByteArray();

		// Write Section 0
		dos.writeByte(0); // compressionScheme = 0
		writeIntLE(dos, sec0TotalLen);
		writeIntLE(dos, hObjBytes.length);
		dos.write(hObjBytes);

		Adler32 adler0 = new Adler32();
		adler0.update(0);
		adler0.update((byte) sec0TotalLen);
		adler0.update((byte) (sec0TotalLen >> 8));
		adler0.update((byte) (sec0TotalLen >> 16));
		adler0.update((byte) (sec0TotalLen >> 24));
		adler0.update((byte) hObjBytes.length);
		adler0.update((byte) (hObjBytes.length >> 8));
		adler0.update((byte) (hObjBytes.length >> 16));
		adler0.update((byte) (hObjBytes.length >> 24));
		adler0.update(hObjBytes);
		writeIntLE(dos, (int) adler0.getValue());

		// Write Section 1
		dos.writeByte(scheme1);
		writeIntLE(dos, sec1TotalLen);
		writeIntLE(dos, payload.length);
		dos.write(sec1Payload);

		Adler32 adler1 = new Adler32();
		adler1.update(scheme1);
		adler1.update((byte) sec1TotalLen);
		adler1.update((byte) (sec1TotalLen >> 8));
		adler1.update((byte) (sec1TotalLen >> 16));
		adler1.update((byte) (sec1TotalLen >> 24));
		adler1.update((byte) payload.length);
		adler1.update((byte) (payload.length >> 8));
		adler1.update((byte) (payload.length >> 16));
		adler1.update((byte) (payload.length >> 24));
		adler1.update(sec1Payload);

		int checksum = (int) adler1.getValue();
		if (corruptChecksum) {
			checksum ^= 0xFFFFFFFF;
		}
		writeIntLE(dos, checksum);

		return fileBaos.toByteArray();
	}

	@Test
	public void testLoaderM3GBinaryStreamUncompressed() throws Exception {
		byte[] payload = createTestM3GPayload();
		byte[] m3gFile = buildM3GFile(payload, false, false);

		Object3D[] roots = Loader.load(m3gFile, 0);
		assertNotNull(roots);
		assertTrue(roots.length >= 1);
		assertTrue("Root 0 must be World", roots[0] instanceof World);

		World world = (World) roots[0];
		assertNotNull("Active camera must be set", world.getActiveCamera());
		assertNotNull("Background must be set", world.getBackground());
		assertEquals(0xFF336699, world.getBackground().getColor());

		assertEquals(1, world.getChildCount());
		assertTrue("World child must be Mesh", world.getChild(0) instanceof Mesh);
		Mesh mesh = (Mesh) world.getChild(0);
		assertNotNull("Mesh vertex buffer", mesh.getVertexBuffer());
		assertEquals(0xFFFF0000, mesh.getVertexBuffer().getDefaultColor());
		assertNotNull("Mesh index buffer", mesh.getIndexBuffer(0));
		assertNotNull("Mesh appearance", mesh.getAppearance(0));
	}

	@Test
	public void testLoaderM3GBinaryStreamDeflated() throws Exception {
		byte[] payload = createTestM3GPayload();
		byte[] m3gFile = buildM3GFile(payload, true, false);

		Object3D[] roots = Loader.load(m3gFile, 0);
		assertNotNull(roots);
		assertTrue(roots.length >= 1);
		assertTrue("Root 0 must be World", roots[0] instanceof World);

		World world = (World) roots[0];
		assertNotNull(world.getActiveCamera());
		assertEquals(0xFF336699, world.getBackground().getColor());
		assertEquals(1, world.getChildCount());
	}

	@Test(expected = java.io.IOException.class)
	public void testLoaderCorruptedChecksumThrowsIOException() throws Exception {
		byte[] payload = createTestM3GPayload();
		byte[] m3gFile = buildM3GFile(payload, false, true); // Corrupted checksum

		Loader.load(m3gFile, 0);
	}

	@Test(expected = java.io.IOException.class)
	public void testLoaderShortHeaderVariant() throws Exception {
		// Truncated invalid header: must throw IOException
		byte[] shortHeaderFile = new byte[] {
			(byte) 0xAB, 'J', '3', 'G', 0x0D, 0x0A, 0x1A, 0x0A
		};
		Loader.load(shortHeaderFile, 0);
	}

	@Test
	public void testVertexScaleAndBiasRendering() {
		Graphics3D g3d = Graphics3D.getInstance();
		final int[] pixels = new int[64 * 64];
		Graphics g = new Graphics() {
			public int getClipX() { return 0; }
			public int getClipY() { return 0; }
			public int getClipWidth() { return 64; }
			public int getClipHeight() { return 64; }
			public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
				System.arraycopy(rgbData, offset, pixels, 0, width * height);
			}
		};

		g3d.bindTarget(g);
		Background bg = new Background();
		bg.setColor(0x00000000);
		g3d.clear(bg);

		VertexBuffer vb = new VertexBuffer();
		VertexArray va = new VertexArray(3, 3, 2);
		short[] rawPos = new short[] {
			0, 20, -40,
			-20, -20, -40,
			20, -20, -40
		};
		va.set(0, 3, rawPos);
		vb.setPositions(va, 0.5f, new float[] { 0.0f, 0.0f, 0.0f });
		vb.setDefaultColor(0xFF00FF00);

		int[] strip = new int[] { 3 };
		TriangleStripArray tsa = new TriangleStripArray(0, strip);
		Appearance app = new Appearance();

		Camera cam = new Camera();
		cam.setPerspective(60.0f, 1.0f, 0.1f, 100.0f);
		g3d.setCamera(cam, null);

		g3d.render(vb, tsa, app, null);
		g3d.releaseTarget();

		int centerPixel = pixels[32 * 64 + 32];
		assertEquals(0xFF00FF00, centerPixel);
	}

	@Test
	public void testAlphaThresholdDiscard() {
		Graphics3D g3d = Graphics3D.getInstance();
		final int[] pixels = new int[64 * 64];
		Graphics g = new Graphics() {
			public int getClipX() { return 0; }
			public int getClipY() { return 0; }
			public int getClipWidth() { return 64; }
			public int getClipHeight() { return 64; }
			public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
				System.arraycopy(rgbData, offset, pixels, 0, width * height);
			}
		};

		g3d.bindTarget(g);
		Background bg = new Background();
		bg.setColor(0x00000000);
		g3d.clear(bg);

		VertexBuffer vb = new VertexBuffer();
		VertexArray va = new VertexArray(3, 3, 2);
		short[] pos = new short[] {
			0, 10, -20,
			-10, -10, -20,
			10, -10, -20
		};
		va.set(0, 3, pos);
		vb.setPositions(va, 1.0f, null);

		VertexArray uvArray = new VertexArray(3, 2, 2);
		uvArray.set(0, 3, new short[] { 0, 0, 0, 0, 0, 0 });
		vb.setTexCoords(0, uvArray, 1.0f, null);

		byte[] transparentPixel = new byte[] { (byte) 255, 0, 0, 0 };
		Image2D img2d = new Image2D(Image2D.RGBA, 1, 1, transparentPixel);
		Texture2D tex = new Texture2D(img2d);
		tex.setBlending(Texture2D.FUNC_REPLACE);

		Appearance app = new Appearance();
		app.setTexture(0, tex);
		CompositingMode cm = new CompositingMode();
		cm.setAlphaThreshold(0.5f);
		app.setCompositingMode(cm);

		TriangleStripArray tsa = new TriangleStripArray(0, new int[] { 3 });
		Camera cam = new Camera();
		cam.setPerspective(60.0f, 1.0f, 0.1f, 100.0f);
		g3d.setCamera(cam, null);

		g3d.render(vb, tsa, app, null);
		g3d.releaseTarget();

		assertEquals(0x00000000, pixels[32 * 64 + 32]);
	}

	@Test
	public void testPolygonModeCullNoneDoubleSided() {
		Graphics3D g3d = Graphics3D.getInstance();
		final int[] pixels = new int[64 * 64];
		Graphics g = new Graphics() {
			public int getClipX() { return 0; }
			public int getClipY() { return 0; }
			public int getClipWidth() { return 64; }
			public int getClipHeight() { return 64; }
			public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
				System.arraycopy(rgbData, offset, pixels, 0, width * height);
			}
		};

		g3d.bindTarget(g);
		Background bg = new Background();
		bg.setColor(0x00000000);
		g3d.clear(bg);

		VertexBuffer vb = new VertexBuffer();
		VertexArray va = new VertexArray(3, 3, 2);
		short[] pos = new short[] {
			0, 10, -20,
			10, -10, -20,
			-10, -10, -20
		};
		va.set(0, 3, pos);
		vb.setPositions(va, 1.0f, null);
		vb.setDefaultColor(0xFF00FF00);

		TriangleStripArray tsa = new TriangleStripArray(0, new int[] { 3 });
		Appearance app = new Appearance();

		Camera cam = new Camera();
		cam.setPerspective(60.0f, 1.0f, 0.1f, 100.0f);
		g3d.setCamera(cam, null);

		g3d.render(vb, tsa, app, null);
		g3d.releaseTarget();
		assertEquals("Back-facing triangle should be culled by default", 0x00000000, pixels[32 * 64 + 32]);

		g3d.bindTarget(g);
		g3d.clear(bg);

		PolygonMode pm = new PolygonMode();
		pm.setCulling(PolygonMode.CULL_NONE);
		app.setPolygonMode(pm);

		g3d.render(vb, tsa, app, null);
		g3d.releaseTarget();
		assertEquals("Back-facing triangle must render with CULL_NONE", 0xFF00FF00, pixels[32 * 64 + 32]);
	}

	@Test
	public void testCompositingModeAlphaBlending() {
		Graphics3D g3d = Graphics3D.getInstance();
		final int[] pixels = new int[64 * 64];
		Graphics g = new Graphics() {
			public int getClipX() { return 0; }
			public int getClipY() { return 0; }
			public int getClipWidth() { return 64; }
			public int getClipHeight() { return 64; }
			public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
				System.arraycopy(rgbData, offset, pixels, 0, width * height);
			}
		};

		g3d.bindTarget(g);
		Background bg = new Background();
		bg.setColor(0xFFFF0000);
		g3d.clear(bg);

		VertexBuffer vb = new VertexBuffer();
		VertexArray va = new VertexArray(3, 3, 2);
		short[] pos = new short[] {
			0, 10, -20,
			-10, -10, -20,
			10, -10, -20
		};
		va.set(0, 3, pos);
		vb.setPositions(va, 1.0f, null);

		VertexArray uvArray = new VertexArray(3, 2, 2);
		uvArray.set(0, 3, new short[] { 0, 0, 0, 0, 0, 0 });
		vb.setTexCoords(0, uvArray, 1.0f, null);

		byte[] rgba = new byte[] { (byte) 255, (byte) 255, (byte) 255, (byte) 128 };
		Image2D img2d = new Image2D(Image2D.RGBA, 1, 1, rgba);
		Texture2D tex = new Texture2D(img2d);
		tex.setBlending(Texture2D.FUNC_REPLACE);

		Appearance app = new Appearance();
		app.setTexture(0, tex);
		CompositingMode cm = new CompositingMode();
		cm.setBlending(CompositingMode.ALPHA);
		cm.setAlphaThreshold(0.0f);
		app.setCompositingMode(cm);

		TriangleStripArray tsa = new TriangleStripArray(0, new int[] { 3 });
		Camera cam = new Camera();
		cam.setPerspective(60.0f, 1.0f, 0.1f, 100.0f);
		g3d.setCamera(cam, null);

		g3d.render(vb, tsa, app, null);
		g3d.releaseTarget();

		int blendedPixel = pixels[32 * 64 + 32];
		int r = (blendedPixel >>> 16) & 0xFF;
		int gVal = (blendedPixel >>> 8) & 0xFF;
		int b = blendedPixel & 0xFF;

		assertTrue("Red should be blended to ~254-255", r >= 254 && r <= 255);
		assertTrue("Green should be blended to ~128", gVal >= 126 && gVal <= 129);
		assertTrue("Blue should be blended to ~128", b >= 126 && b <= 129);
	}

	@Test
	public void testUVTextureScaleNormalization() {
		Graphics3D g3d = Graphics3D.getInstance();
		final int[] pixels = new int[64 * 64];
		Graphics g = new Graphics() {
			public int getClipX() { return 0; }
			public int getClipY() { return 0; }
			public int getClipWidth() { return 64; }
			public int getClipHeight() { return 64; }
			public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
				System.arraycopy(rgbData, offset, pixels, 0, width * height);
			}
		};

		g3d.bindTarget(g);
		Background bg = new Background();
		bg.setColor(0x00000000);
		g3d.clear(bg);

		VertexBuffer vb = new VertexBuffer();
		VertexArray va = new VertexArray(3, 3, 2);
		short[] pos = new short[] {
			0, 10, -20,
			-10, -10, -20,
			10, -10, -20
		};
		va.set(0, 3, pos);
		vb.setPositions(va, 1.0f, null);

		VertexArray uvArray = new VertexArray(3, 2, 2);
		uvArray.set(0, 3, new short[] { 16384, 16384, 16384, 16384, 16384, 16384 });
		vb.setTexCoords(0, uvArray, 1.0f / 32768.0f, new float[] { 0.0f, 0.0f, 0.0f });

		byte[] texBytes = new byte[] {
			(byte) 255, 0, 0, (byte) 255,         0, (byte) 255, 0, (byte) 255,
			0, 0, (byte) 255, (byte) 255,         (byte) 255, (byte) 255, 0, (byte) 255
		};
		Image2D img2d = new Image2D(Image2D.RGBA, 2, 2, texBytes);
		Texture2D tex = new Texture2D(img2d);
		tex.setWrapping(Texture2D.WRAP_CLAMP, Texture2D.WRAP_CLAMP);
		tex.setBlending(Texture2D.FUNC_REPLACE);

		Appearance app = new Appearance();
		app.setTexture(0, tex);

		TriangleStripArray tsa = new TriangleStripArray(0, new int[] { 3 });
		Camera cam = new Camera();
		cam.setPerspective(60.0f, 1.0f, 0.1f, 100.0f);
		g3d.setCamera(cam, null);

		g3d.render(vb, tsa, app, null);
		g3d.releaseTarget();

		int centerPixel = pixels[32 * 64 + 32];
		assertEquals(0xFFFFFF00, centerPixel);
	}

	@Test
	public void testSkinnedMeshBoneDeformation() {
		Graphics3D g3d = Graphics3D.getInstance();
		Graphics g = new Graphics() {
			public int getClipX() { return 0; }
			public int getClipY() { return 0; }
			public int getClipWidth() { return 64; }
			public int getClipHeight() { return 64; }
			public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {}
		};

		g3d.bindTarget(g);
		Background bg = new Background();
		bg.setColor(0x00000000);
		g3d.clear(bg);

		VertexBuffer vb = new VertexBuffer();
		VertexArray va = new VertexArray(3, 3, 2);
		short[] pos = new short[] {
			0, 10, -20,
			-10, -10, -20,
			10, -10, -20
		};
		va.set(0, 3, pos);
		vb.setPositions(va, 1.0f, null);
		vb.setDefaultColor(0xFF00FF00);

		TriangleStripArray tsa = new TriangleStripArray(0, new int[] { 3 });
		Appearance app = new Appearance();

		Group skeleton = new Group();
		Node bone = new Group();
		skeleton.addChild(bone);

		SkinnedMesh mesh = new SkinnedMesh(vb, tsa, app, skeleton);
		mesh.addTransform(bone, 100, 0, 3); // 100% weight to bone

		// Move bone
		bone.translate(5.0f, 0.0f, 0.0f);

		Camera cam = new Camera();
		cam.setPerspective(60.0f, 1.0f, 0.1f, 100.0f);
		g3d.setCamera(cam, null);

		// Render SkinnedMesh
		g3d.render(mesh, null);
		g3d.releaseTarget();

		VertexBuffer skinnedVb = mesh.getPosedVertexBuffer();
		assertNotNull("Skinned vertices buffer must be generated", skinnedVb);
		assertNotNull("Skinned positions array must not be null", skinnedVb.getPositions(null));
	}

}
