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

	@Test
	public void testSPIDelegation() {
		final boolean[] flags = new boolean[4]; // [bound, flushed, released, rendered]

		org.neutron.graphics3d.spi.Micro3DBackend mockBackend = new org.neutron.graphics3d.spi.Micro3DBackend() {
			public boolean isAvailable() { return true; }
			public boolean init() { return true; }
			public void bind(Object target, int width, int height) { flags[0] = true; }
			public void release() { flags[2] = true; }
			public void flush() { flags[1] = true; }
			public void renderFigure(Object figure, int x, int y, Object layout, Object effect) { flags[3] = true; }
			public void drawFigure(Object figure, int x, int y, Object layout, Object effect) {}
			public void renderPrimitives(Object texture, int x, int y, Object layout, Object effect, int command, int numPrimitives, int[] vertexCoords, int[] normalCoords, int[] textureCoords, int[] colorCoords) {}
			public void drawCommandList(Object[] textures, int x, int y, Object layout, Object effect, int[] commandList) {}
			public void drawCommandList(Object texture, int x, int y, Object layout, Object effect, int[] commandList) {}
		};

		org.neutron.graphics3d.Graphics3DProvider.registerMicro3DBackend(mockBackend);
		org.neutron.graphics3d.Graphics3DConfig.setBackendType(org.neutron.graphics3d.RenderBackendType.OPENGL);

		Graphics g = new Graphics() {
			public int getClipX() { return 0; }
			public int getClipY() { return 0; }
			public int getClipWidth() { return 240; }
			public int getClipHeight() { return 320; }
			public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {}
		};

		Graphics3D g3d = new Graphics3D();
		g3d.bind(g);
		Assert.assertTrue("Backend must be bound via SPI", flags[0]);

		g3d.flush();
		Assert.assertTrue("Backend must be flushed via SPI", flags[1]);

		g3d.release(g);
		Assert.assertTrue("Backend must be released via SPI", flags[2]);

		// Reset provider
		org.neutron.graphics3d.Graphics3DProvider.registerMicro3DBackend(null);
	}

	@Test
	public void testGalaxyOnFireAssetsLoadAndRender() throws Exception {
		java.io.File mbacFile = new java.io.File("/tmp/test_alien.mbac");
		java.io.File bmpFile = new java.io.File("/tmp/test_ships.bmp");
		if (!mbacFile.exists() || !bmpFile.exists()) return;

		byte[] mbacData = java.nio.file.Files.readAllBytes(mbacFile.toPath());
		byte[] bmpData = java.nio.file.Files.readAllBytes(bmpFile.toPath());

		Figure figure = new Figure(mbacData);
		Texture texture = new Texture(bmpData, true);
		figure.setTexture(texture);

		Assert.assertTrue("Figure must have vertices", figure.getNumVertices() > 0);
		Assert.assertTrue("Figure must have polyT3 or polyT4", figure.getNumPolyT3() > 0 || figure.getNumPolyT4() > 0);

		FigureLayout layout = new FigureLayout();
		layout.setScale(512, 512);
		layout.setCenter(120, 160);

		Effect3D effect = new Effect3D();
		Light light = new Light(new Vector3D(0, 4096, 0), 1024, 2048);
		effect.setLight(light);

		final int[] drawnPixels = new int[240 * 320];
		final boolean[] drawCalled = new boolean[1];

		Graphics g = new Graphics() {
			public int getClipX() { return 0; }
			public int getClipY() { return 0; }
			public int getClipWidth() { return 240; }
			public int getClipHeight() { return 320; }
			public void drawRGB(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha) {
				drawCalled[0] = true;
				System.arraycopy(rgbData, offset, drawnPixels, 0, Math.min(rgbData.length, drawnPixels.length));
			}
		};

		// 1. Test Software backend
		org.neutron.graphics3d.Graphics3DConfig.setBackendType(org.neutron.graphics3d.RenderBackendType.SOFTWARE);
		Graphics3D g3d = new Graphics3D();
		g3d.bind(g);
		g3d.renderFigure(figure, 0, 0, layout, effect);
		g3d.flush();
		g3d.release(g);

		Assert.assertTrue("drawRGB should have been called in software mode", drawCalled[0]);
		int nonZeroCount = 0;
		for (int p : drawnPixels) {
			if (p != 0) nonZeroCount++;
		}
		Assert.assertTrue("Software rasterizer must render pixels", nonZeroCount > 0);
	}
}
