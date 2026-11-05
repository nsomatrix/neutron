package javax.microedition.m3g;

import java.util.Hashtable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Graphics3D {

	public static final int ANTIALIAS = 2;
	public static final int DITHER = 4;
	public static final int TRUE_COLOR = 8;
	public static final int OVERWRITE = 16;

	private static final Graphics3D INSTANCE = new Graphics3D();

	private Object target = null;
	private int targetWidth = 240;
	private int targetHeight = 320;
	private int[] targetPixels = null;
	private float[] depthBuffer = null;

	private int vpX = 0;
	private int vpY = 0;
	private int vpWidth = 240;
	private int vpHeight = 320;
	private float depthNear = 0.0f;
	private float depthFar = 1.0f;

	private Camera activeCamera = null;
	private final Transform cameraTransform = new Transform();
	private final Transform viewMatrix = new Transform();
	private final Transform projectionMatrix = new Transform();
	private final Transform viewProjectionMatrix = new Transform();

	private final java.util.Vector lights = new java.util.Vector();

	private Graphics3D() {}

	public static Graphics3D getInstance() {
		return INSTANCE;
	}

	public static Hashtable getProperties() {
		Hashtable props = new Hashtable();
		props.put("maxLights", new Integer(8));
		props.put("maxViewportWidth", new Integer(2048));
		props.put("maxViewportHeight", new Integer(2048));
		props.put("maxTextureDimension", new Integer(2048));
		props.put("numTextureUnits", new Integer(2));
		props.put("maxTransformsPerVertex", new Integer(4));
		return props;
	}

	public synchronized void bindTarget(Object target) {
		bindTarget(target, true, 0);
	}

	public synchronized void bindTarget(Object target, boolean depthBufferEnable, int flags) {
		if (target == null) throw new NullPointerException();
		if (this.target != null) throw new IllegalStateException("Target already bound");

		if (target instanceof Graphics) {
			Graphics g = (Graphics) target;
			this.target = g;
			this.targetWidth = g.getClipWidth() > 0 ? g.getClipWidth() : 240;
			this.targetHeight = g.getClipHeight() > 0 ? g.getClipHeight() : 320;
		} else if (target instanceof Image) {
			Image img = (Image) target;
			if (!img.isMutable()) throw new IllegalArgumentException("Target image must be mutable");
			this.target = img;
			this.targetWidth = img.getWidth();
			this.targetHeight = img.getHeight();
		} else {
			throw new IllegalArgumentException("Invalid target: " + target.getClass().getName());
		}

		this.vpX = 0;
		this.vpY = 0;
		this.vpWidth = targetWidth;
		this.vpHeight = targetHeight;

		int totalPixels = targetWidth * targetHeight;
		if (this.targetPixels == null || this.targetPixels.length < totalPixels) {
			this.targetPixels = new int[totalPixels];
		}

		if (depthBufferEnable) {
			if (this.depthBuffer == null || this.depthBuffer.length < totalPixels) {
				this.depthBuffer = new float[totalPixels];
			}
			java.util.Arrays.fill(this.depthBuffer, 1.0f);
		}
	}

	public synchronized void releaseTarget() {
		if (target == null) return;

		// Blit rendered pixels back to target Graphics or Image
		if (target instanceof Graphics) {
			Graphics g = (Graphics) target;
			g.drawRGB(targetPixels, 0, targetWidth, vpX, vpY, vpWidth, vpHeight, true);
		} else if (target instanceof Image) {
			Image img = (Image) target;
			Graphics g = img.getGraphics();
			g.drawRGB(targetPixels, 0, targetWidth, vpX, vpY, vpWidth, vpHeight, true);
		}

		this.target = null;
	}

	public Object getTarget() {
		return target;
	}

	public void setViewport(int x, int y, int width, int height) {
		if (width <= 0 || height <= 0 || width > 2048 || height > 2048) {
			throw new IllegalArgumentException();
		}
		this.vpX = x;
		this.vpY = y;
		this.vpWidth = width;
		this.vpHeight = height;
	}

	public int getViewportX() { return vpX; }
	public int getViewportY() { return vpY; }
	public int getViewportWidth() { return vpWidth; }
	public int getViewportHeight() { return vpHeight; }

	public void setDepthRange(float near, float far) {
		if (near < 0.0f || near > 1.0f || far < 0.0f || far > 1.0f) throw new IllegalArgumentException();
		this.depthNear = near;
		this.depthFar = far;
	}

	public float getDepthRangeNear() { return depthNear; }
	public float getDepthRangeFar() { return depthFar; }

	public void setCamera(Camera camera, Transform transform) {
		this.activeCamera = camera;
		if (transform != null) {
			cameraTransform.set(transform);
		} else if (camera != null) {
			camera.getCompositeTransform(cameraTransform);
		} else {
			cameraTransform.setIdentity();
		}

		try {
			viewMatrix.set(cameraTransform);
			viewMatrix.invert();
		} catch (Exception e) {
			viewMatrix.setIdentity();
		}

		if (camera != null) {
			camera.getProjection(projectionMatrix);
		} else {
			projectionMatrix.setIdentity();
		}

		viewProjectionMatrix.set(projectionMatrix);
		viewProjectionMatrix.postMultiply(viewMatrix);
	}

	public Camera getCamera(Transform transform) {
		if (transform != null) {
			transform.set(cameraTransform);
		}
		return activeCamera;
	}

	public void resetLights() {
		lights.removeAllElements();
	}

	public int addLight(Light light, Transform transform) {
		if (light == null) throw new NullPointerException();
		lights.addElement(light);
		return lights.size() - 1;
	}

	public void setLight(int index, Light light, Transform transform) {
		if (index < 0 || index >= lights.size()) throw new IndexOutOfBoundsException();
		lights.setElementAt(light, index);
	}

	public int getLightCount() {
		return lights.size();
	}

	public void clear(Background background) {
		if (target == null) throw new IllegalStateException();
		int clearColor = 0x00000000;
		if (background != null) {
			clearColor = background.getColor();
			if (background.isColorClearEnabled()) {
				java.util.Arrays.fill(targetPixels, clearColor);
			}
			if (background.isDepthClearEnabled() && depthBuffer != null) {
				java.util.Arrays.fill(depthBuffer, 1.0f);
			}
		} else {
			java.util.Arrays.fill(targetPixels, 0);
			if (depthBuffer != null) {
				java.util.Arrays.fill(depthBuffer, 1.0f);
			}
		}
	}

	public void render(World world) {
		if (world == null) throw new NullPointerException();
		if (target == null) throw new IllegalStateException();

		if (world.getActiveCamera() != null) {
			setCamera(world.getActiveCamera(), null);
		}
		if (world.getBackground() != null) {
			clear(world.getBackground());
		}

		renderNode(world, new Transform());
	}

	public void render(Node node, Transform transform) {
		if (node == null) throw new NullPointerException();
		if (target == null) throw new IllegalStateException();

		Transform t = new Transform();
		if (transform != null) {
			t.set(transform);
		}
		renderNode(node, t);
	}

	public void render(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance, Transform transform) {
		render(vertices, triangles, appearance, transform, -1);
	}

	public void render(VertexBuffer vertices, IndexBuffer triangles, Appearance appearance, Transform transform, int scope) {
		if (vertices == null || triangles == null || appearance == null) throw new NullPointerException();
		if (target == null) throw new IllegalStateException();

		Transform modelMatrix = new Transform();
		if (transform != null) {
			modelMatrix.set(transform);
		}

		Transform mvp = new Transform(viewProjectionMatrix);
		mvp.postMultiply(modelMatrix);

		rasterize(vertices, triangles, appearance, mvp);
	}

	private void renderNode(Node node, Transform parentTransform) {
		if (!node.isRenderingEnabled()) return;

		Transform currentTransform = new Transform(parentTransform);
		Transform localTransform = new Transform();
		node.getCompositeTransform(localTransform);
		currentTransform.postMultiply(localTransform);

		if (node instanceof Mesh) {
			Mesh mesh = (Mesh) node;
			VertexBuffer vb = mesh.getVertexBuffer();
			int count = mesh.getSubmeshCount();
			for (int i = 0; i < count; i++) {
				IndexBuffer ib = mesh.getIndexBuffer(i);
				Appearance app = mesh.getAppearance(i);
				if (vb != null && ib != null && app != null) {
					render(vb, ib, app, currentTransform, mesh.getScope());
				}
			}
		} else if (node instanceof Group) {
			Group group = (Group) node;
			int childCount = group.getChildCount();
			for (int i = 0; i < childCount; i++) {
				renderNode(group.getChild(i), currentTransform);
			}
		}
	}

	/**
	 * Pure-Java software rasterizer with depth buffering, perspective interpolation, and texturing.
	 */
	private void rasterize(VertexBuffer vb, IndexBuffer ib, Appearance app, Transform mvp) {
		VertexArray posArray = vb.getPositions(null);
		if (posArray == null) return;

		int vertexCount = vb.getVertexCount();
		float[] projX = new float[vertexCount];
		float[] projY = new float[vertexCount];
		float[] projZ = new float[vertexCount];
		float[] invW = new float[vertexCount];

		float[] v = new float[4];
		for (int i = 0; i < vertexCount; i++) {
			v[0] = posArray.getComponentFloat(i, 0);
			v[1] = posArray.getComponentFloat(i, 1);
			v[2] = posArray.getComponentFloat(i, 2);
			v[3] = 1.0f;

			mvp.transform(v);

			float w = v[3];
			if (w <= 0.0001f) w = 0.0001f;
			invW[i] = 1.0f / w;

			// Viewport mapping
			projX[i] = (v[0] * invW[i] * 0.5f + 0.5f) * vpWidth + vpX;
			projY[i] = (-v[1] * invW[i] * 0.5f + 0.5f) * vpHeight + vpY;
			projZ[i] = v[2] * invW[i] * 0.5f + 0.5f;
		}

		Texture2D tex = app.getTexture(0);
		Image2D texImg = tex != null ? tex.getImage() : null;
		int[] texPixels = texImg != null ? texImg.getPixels() : null;
		int texW = texImg != null ? texImg.getWidth() : 0;
		int texH = texImg != null ? texImg.getHeight() : 0;

		VertexArray texArray = vb.getTexCoords(0, null);

		int[][] triangles = ib.getTriangles();
		for (int t = 0; t < triangles.length; t++) {
			int i0 = triangles[t][0];
			int i1 = triangles[t][1];
			int i2 = triangles[t][2];

			if (i0 >= vertexCount || i1 >= vertexCount || i2 >= vertexCount) continue;

			// Backface culling in screen space
			float x0 = projX[i0], y0 = projY[i0], z0 = projZ[i0];
			float x1 = projX[i1], y1 = projY[i1], z1 = projZ[i1];
			float x2 = projX[i2], y2 = projY[i2], z2 = projZ[i2];

			float area = (x1 - x0) * (y2 - y0) - (y1 - y0) * (x2 - x0);
			if (area <= 0.0f) continue; // Cull back-facing triangle

			float invArea = 1.0f / area;

			// Triangle bounding box clamped to viewport
			int minX = Math.max(vpX, (int) Math.floor(Math.min(x0, Math.min(x1, x2))));
			int maxX = Math.min(vpX + vpWidth - 1, (int) Math.ceil(Math.max(x0, Math.max(x1, x2))));
			int minY = Math.max(vpY, (int) Math.floor(Math.min(y0, Math.min(y1, y2))));
			int maxY = Math.min(vpY + vpHeight - 1, (int) Math.ceil(Math.max(y0, Math.max(y1, y2))));

			float u0 = 0, v0_c = 0, u1 = 0, v1_c = 0, u2 = 0, v2_c = 0;
			if (texArray != null) {
				u0 = texArray.getComponentFloat(i0, 0) * invW[i0];
				v0_c = texArray.getComponentFloat(i0, 1) * invW[i0];
				u1 = texArray.getComponentFloat(i1, 0) * invW[i1];
				v1_c = texArray.getComponentFloat(i1, 1) * invW[i1];
				u2 = texArray.getComponentFloat(i2, 0) * invW[i2];
				v2_c = texArray.getComponentFloat(i2, 1) * invW[i2];
			}

			int baseColor = vb.getDefaultColor();

			for (int py = minY; py <= maxY; py++) {
				int rowOffset = py * targetWidth;
				for (int px = minX; px <= maxX; px++) {
					// Barycentric coordinates
					float w0 = ((x1 - px) * (y2 - py) - (y1 - py) * (x2 - px)) * invArea;
					float w1 = ((x2 - px) * (y0 - py) - (y2 - py) * (x0 - px)) * invArea;
					float w2 = 1.0f - w0 - w1;

					if (w0 >= 0.0f && w1 >= 0.0f && w2 >= 0.0f) {
						float z = w0 * z0 + w1 * z1 + w2 * z2;
						int pixelIdx = rowOffset + px;

						if (depthBuffer == null || z < depthBuffer[pixelIdx]) {
							if (depthBuffer != null) {
								depthBuffer[pixelIdx] = z;
							}

							int pixelColor = baseColor;
							if (texPixels != null && texW > 0 && texH > 0) {
								float interpInvW = w0 * invW[i0] + w1 * invW[i1] + w2 * invW[i2];
								if (interpInvW > 0.0f) {
									float w_orig = 1.0f / interpInvW;
									float u = (w0 * u0 + w1 * u1 + w2 * u2) * w_orig;
									float vCoord = (w0 * v0_c + w1 * v1_c + w2 * v2_c) * w_orig;

									int tx = ((int) Math.abs(u * texW)) % texW;
									int ty = ((int) Math.abs(vCoord * texH)) % texH;
									pixelColor = texPixels[ty * texW + tx];
								}
							}

							targetPixels[pixelIdx] = pixelColor;
						}
					}
				}
			}
		}
	}
}

