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
}
