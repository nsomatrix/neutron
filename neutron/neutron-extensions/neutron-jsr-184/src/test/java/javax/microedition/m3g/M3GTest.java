package javax.microedition.m3g;

import org.junit.Test;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
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

	private byte[] createTestM3GPayload() throws Exception {
		java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
		java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);

		// 1. Camera (ID 1)
		java.io.ByteArrayOutputStream camOut = new java.io.ByteArrayOutputStream();
		java.io.DataOutputStream camDos = new java.io.DataOutputStream(camOut);
		camDos.writeInt(101); // userID
		camDos.writeBoolean(true); // rendering
		camDos.writeBoolean(true); // picking
		camDos.writeFloat(1.0f); // alphaFactor
		camDos.writeByte(Camera.PERSPECTIVE);
		camDos.writeFloat(60.0f); // fovy
		camDos.writeFloat(1.33f); // aspect
		camDos.writeFloat(0.1f); // near
		camDos.writeFloat(100.0f); // far
		byte[] camBytes = camOut.toByteArray();
		dos.writeByte(Loader.TYPE_CAMERA);
		dos.writeInt(camBytes.length);
		dos.write(camBytes);

		// 2. Background (ID 2)
		java.io.ByteArrayOutputStream bgOut = new java.io.ByteArrayOutputStream();
		java.io.DataOutputStream bgDos = new java.io.DataOutputStream(bgOut);
		bgDos.writeInt(102); // userID
		bgDos.writeInt(0xFF336699); // color
		byte[] bgBytes = bgOut.toByteArray();
		dos.writeByte(Loader.TYPE_BACKGROUND);
		dos.writeInt(bgBytes.length);
		dos.write(bgBytes);

		// 3. VertexArray (ID 3)
		java.io.ByteArrayOutputStream vaOut = new java.io.ByteArrayOutputStream();
		java.io.DataOutputStream vaDos = new java.io.DataOutputStream(vaOut);
		vaDos.writeByte(2); // componentSize
		vaDos.writeByte(3); // numComponents
		vaDos.writeShort(3); // numVertices
		vaDos.writeInt(103); // userID
		vaDos.writeShort(0); vaDos.writeShort(10); vaDos.writeShort(-20);
		vaDos.writeShort(-10); vaDos.writeShort(-10); vaDos.writeShort(-20);
		vaDos.writeShort(10); vaDos.writeShort(-10); vaDos.writeShort(-20);
		byte[] vaBytes = vaOut.toByteArray();
		dos.writeByte(Loader.TYPE_VERTEX_ARRAY);
		dos.writeInt(vaBytes.length);
		dos.write(vaBytes);

		// 4. VertexBuffer (ID 4)
		java.io.ByteArrayOutputStream vbOut = new java.io.ByteArrayOutputStream();
		java.io.DataOutputStream vbDos = new java.io.DataOutputStream(vbOut);
		vbDos.writeInt(104); // userID
		vbDos.writeInt(3); // positionsRef (object 3)
		vbDos.writeFloat(1.0f); // scale
		vbDos.writeFloat(0.0f); vbDos.writeFloat(0.0f); vbDos.writeFloat(0.0f); // bias
		vbDos.writeInt(0); // normalsRef
		vbDos.writeInt(0); // colorsRef
		vbDos.writeInt(0xFFFF0000); // defaultColor
		byte[] vbBytes = vbOut.toByteArray();
		dos.writeByte(Loader.TYPE_VERTEX_BUFFER);
		dos.writeInt(vbBytes.length);
		dos.write(vbBytes);

		// 5. TriangleStripArray (ID 5)
		java.io.ByteArrayOutputStream tsaOut = new java.io.ByteArrayOutputStream();
		java.io.DataOutputStream tsaDos = new java.io.DataOutputStream(tsaOut);
		tsaDos.writeInt(0); // firstIndex
		tsaDos.writeInt(1); // stripCount
		tsaDos.writeInt(3); // stripLengths[0]
		tsaDos.writeInt(105); // userID
		byte[] tsaBytes = tsaOut.toByteArray();
		dos.writeByte(Loader.TYPE_TRIANGLE_STRIP_ARRAY);
		dos.writeInt(tsaBytes.length);
		dos.write(tsaBytes);

		// 6. Appearance (ID 6)
		java.io.ByteArrayOutputStream appOut = new java.io.ByteArrayOutputStream();
		java.io.DataOutputStream appDos = new java.io.DataOutputStream(appOut);
		appDos.writeInt(106); // userID
		byte[] appBytes = appOut.toByteArray();
		dos.writeByte(Loader.TYPE_APPEARANCE);
		dos.writeInt(appBytes.length);
		dos.write(appBytes);

		// 7. Mesh (ID 7)
		java.io.ByteArrayOutputStream meshOut = new java.io.ByteArrayOutputStream();
		java.io.DataOutputStream meshDos = new java.io.DataOutputStream(meshOut);
		meshDos.writeInt(4); // vbRef (object 4)
		meshDos.writeInt(1); // submeshCount
		meshDos.writeInt(5); // ibRef (object 5)
		meshDos.writeInt(6); // appRef (object 6)
		meshDos.writeInt(107); // userID
		byte[] meshBytes = meshOut.toByteArray();
		dos.writeByte(Loader.TYPE_MESH);
		dos.writeInt(meshBytes.length);
		dos.write(meshBytes);

		// 8. World (ID 8)
		java.io.ByteArrayOutputStream wOut = new java.io.ByteArrayOutputStream();
		java.io.DataOutputStream wDos = new java.io.DataOutputStream(wOut);
		wDos.writeInt(108); // userID
		wDos.writeBoolean(true); // rendering
		wDos.writeBoolean(true); // picking
		wDos.writeFloat(1.0f); // alphaFactor
		wDos.writeInt(1); // childCount
		wDos.writeInt(7); // childRef (Mesh ID 7)
		wDos.writeInt(1); // camRef (Camera ID 1)
		wDos.writeInt(2); // bgRef (Background ID 2)
		byte[] wBytes = wOut.toByteArray();
		dos.writeByte(Loader.TYPE_WORLD);
		dos.writeInt(wBytes.length);
		dos.write(wBytes);

		return baos.toByteArray();
	}

	private byte[] buildM3GFile(byte[] payload, boolean compress, boolean corruptChecksum) throws Exception {
		java.io.ByteArrayOutputStream fileBaos = new java.io.ByteArrayOutputStream();
		java.io.DataOutputStream dos = new java.io.DataOutputStream(fileBaos);

		// 12-byte header
		byte[] header = new byte[] {
			(byte) 0xAB, 'J', 'S', 'R', '1', '8', '4', (byte) 0xBB, 0x0D, 0x0A, 0x1A, 0x0A
		};
		dos.write(header);

		byte[] sectionPayload;
		int scheme;
		if (compress) {
			scheme = 1;
			java.io.ByteArrayOutputStream defOut = new java.io.ByteArrayOutputStream();
			java.util.zip.DeflaterOutputStream defStream = new java.util.zip.DeflaterOutputStream(defOut);
			defStream.write(payload);
			defStream.finish();
			sectionPayload = defOut.toByteArray();
		} else {
			scheme = 0;
			sectionPayload = payload;
		}

		int totalSectionLen = 1 + 4 + 4 + sectionPayload.length + 4;
		dos.writeByte(scheme);
		dos.writeInt(totalSectionLen);
		dos.writeInt(payload.length);
		dos.write(sectionPayload);

		// Compute Adler-32
		java.util.zip.Adler32 adler = new java.util.zip.Adler32();
		adler.update(scheme);
		adler.update((byte) (totalSectionLen >> 24));
		adler.update((byte) (totalSectionLen >> 16));
		adler.update((byte) (totalSectionLen >> 8));
		adler.update((byte) totalSectionLen);
		adler.update((byte) (payload.length >> 24));
		adler.update((byte) (payload.length >> 16));
		adler.update((byte) (payload.length >> 8));
		adler.update((byte) payload.length);
		adler.update(sectionPayload);

		int checksum = (int) adler.getValue();
		if (corruptChecksum) {
			checksum ^= 0xFFFFFFFF;
		}
		dos.writeInt(checksum);

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

	@Test
	public void testLoaderShortHeaderVariant() throws Exception {
		// 8-byte header variant: «J3G\r\n\x1A\n
		byte[] shortHeaderFile = new byte[] {
			(byte) 0xAB, 'J', '3', 'G', 0x0D, 0x0A, 0x1A, 0x0A
		};
		Object3D[] roots = Loader.load(shortHeaderFile, 0);
		assertNotNull(roots);
		assertEquals(1, roots.length);
		assertTrue(roots[0] instanceof World);
	}
}
