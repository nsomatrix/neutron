package com.mascotcapsule.micro3d.v3;

import javax.microedition.lcdui.Graphics;
import org.junit.Assert;
import org.junit.Test;

public class Micro3DTest {

	@Test
	public void testVector3D() {
		Vector3D v = new Vector3D(100, 200, 300);
		Assert.assertEquals(100, v.getX());
		Assert.assertEquals(200, v.getY());
		Assert.assertEquals(300, v.getZ());

		v.set(10, 20, 30);
		Assert.assertEquals(10, v.getX());
		Assert.assertEquals(20, v.getY());
		Assert.assertEquals(30, v.getZ());

		Vector3D v2 = new Vector3D(1, 2, 3);
		int dot = v.innerProduct(v2);
		Assert.assertEquals(10 * 1 + 20 * 2 + 30 * 3, dot);

		Vector3D cross = new Vector3D();
		cross.outerProduct(v, v2);
		Assert.assertNotNull(cross);
	}

	@Test
	public void testAffineTrans() {
		AffineTrans trans = new AffineTrans();
		trans.setIdentity();
		Assert.assertEquals(4096, trans.m00);
		Assert.assertEquals(4096, trans.m11);
		Assert.assertEquals(4096, trans.m22);

		AffineTrans rot = new AffineTrans();
		rot.setRotationY(1024);
		Assert.assertNotNull(rot);

		AffineTrans mul = new AffineTrans();
		mul.multiply(trans, rot);
		Assert.assertNotNull(mul);
	}

	@Test
	public void testLight() {
		Vector3D dir = new Vector3D(0, 4096, 0);
		Light light = new Light(dir, 1024, 2048);
		Assert.assertNotNull(light);
		Assert.assertEquals(1024, light.getParallelLightIntensity());
		Assert.assertEquals(2048, light.getAmbientIntensity());
	}

	@Test
	public void testEffect3D() {
		Effect3D effect = new Effect3D();
		effect.setShadingType(Effect3D.NORMAL_SHADING);
		Assert.assertEquals(Effect3D.NORMAL_SHADING, effect.getShadingType());
		effect.setSemiTransparentEnabled(true);
		Assert.assertTrue(effect.isSemiTransparentEnabled());
	}

	@Test
	public void testFigureLayout() {
		FigureLayout layout = new FigureLayout();
		layout.setScale(512, 512);
		layout.setCenter(120, 160);
		Assert.assertNotNull(layout);
	}

	@Test
	public void testGraphics3DBindAndRelease() {
		Graphics g = new Graphics() {
			public int getClipX() { return 0; }
			public int getClipY() { return 0; }
			public int getClipWidth() { return 240; }
			public int getClipHeight() { return 320; }
			public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {}
		};

		Graphics3D g3d = new Graphics3D();
		g3d.bind(g);
		g3d.flush();
		g3d.release(g);
		g3d.dispose();
	}

	@Test
	public void testUtil3DResourceResolution() {
		// Non-existent resource returns null safely without NPE
		Assert.assertNull(Util3D.getResourceAsStream(null));
		Assert.assertNull(Util3D.getResourceAsStream("nonexistent_model.mbac"));
		Assert.assertNull(Util3D.getResourceAsStream("/nonexistent_model.mbac"));
	}

	@Test(expected = java.io.IOException.class)
	public void testFigureNotFoundThrowsIOException() throws Exception {
		new Figure("missing_figure.mbac");
	}

	@Test(expected = java.io.IOException.class)
	public void testTextureNotFoundThrowsIOException() throws Exception {
		new Texture("missing_texture.bmp", false);
	}

	@Test(expected = java.io.IOException.class)
	public void testActionTableNotFoundThrowsIOException() throws Exception {
		new ActionTable("missing_action.tra");
	}
}
