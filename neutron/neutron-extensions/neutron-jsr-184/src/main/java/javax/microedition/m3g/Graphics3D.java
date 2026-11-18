/*
 * Copyright (c) 2003 Nokia Corporation and/or its subsidiary(-ies).
 * All rights reserved.
 * This component and the accompanying materials are made available
 * under the terms of "Eclipse Public License v1.0"
 * which accompanies this distribution, and is available
 * at the URL "http://www.eclipse.org/legal/epl-v10.html".
 *
 * Initial Contributors:
 * Nokia Corporation - initial contribution.
 *
 * Contributors:
 *
 * Description:
 *
 */

package javax.microedition.m3g;




import java.util.Hashtable;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;

public class Graphics3D {
	//------------------------------------------------------------------
	// Static data
	//------------------------------------------------------------------

	public static final int ANTIALIAS = 2;
	public static final int DITHER = 4;
	public static final int TRUE_COLOR = 8;

	// M3G 1.1
	public static final int OVERWRITE = 16;

	// Singleton instances
	private static Graphics3D s_instance = null;

	private static boolean unlitTexturesWorkaround;

	// Shared reusable off-heap DirectBuffer and int array cache to eliminate per-frame GC churn
	private static java.nio.ByteBuffer s_cachedDirectBuffer = null;
	private static int s_cachedBufferSize = 0;
	private static int[] s_cachedIntData = null;

	private static synchronized java.nio.ByteBuffer getDirectBuffer(int bytes) {
		if (s_cachedDirectBuffer == null || s_cachedBufferSize < bytes) {
			s_cachedBufferSize = Math.max(bytes, 1024 * 1024 * 4);
			s_cachedDirectBuffer = java.nio.ByteBuffer.allocateDirect(s_cachedBufferSize);
		}
		s_cachedDirectBuffer.clear();
		return s_cachedDirectBuffer;
	}

	private static synchronized int[] getIntArray(int size) {
		if (s_cachedIntData == null || s_cachedIntData.length < size) {
			s_cachedIntData = new int[Math.max(size, 1024 * 1024)];
		}
		return s_cachedIntData;
	}

	//------------------------------------------------------------------
	// Instance data
	//------------------------------------------------------------------

	private long handle;
	private Object buffer;
	private int cur_width, cur_height;
	private int cur_clipX, cur_clipY, cur_clipW, cur_clipH;
	private boolean hasCustomViewport = false;
	private int unionVpX, unionVpY, unionVpW, unionVpH;

	private Camera camera = null;
	private Vector lights = new Vector();

	private java.lang.Object currentTarget = null;
	private int offsetX, offsetY, hints = 0;
	private boolean depthEnabled = true;
	private Destroyer destroyer;
	private Interface iInterface;

	// this flag is for identification of image target types
	// - True for mutable off-screen images
	// - False for canvas/GameCanvas framebuffer
	private boolean iIsImageTarget;

	// this flag is for identification if MBX HW accelerator is present
	// - True - MBX is NOT present
	// - False - MBX is present
	private boolean iIsProperRenderer;

	private boolean iNativeInitialized = false;

	// Shutdown listener
	private class Destroyer {
		Graphics3D target;

		Destroyer(Graphics3D g3d) {
			target = g3d;
		}

		// This method gets called when application is shuttingdown
		public void shuttingDown() {

			// Finalize native peer
			Platform.finalizeObject(target.handle, target.iInterface);

			// signal shutdown (set shutdown flag)
			// and remove references
			target.iInterface.signalShutdown();
			target.iInterface = null;
			target.camera = null;
			//target.s_instance = null;

			// All done, Call gc() and finalization to collect
			// remaining objects, thus zeroying liveObjects count
			// in interface instance

		}
	}

	//------------------------------------------------------------------
	// Constructor(s)
	//------------------------------------------------------------------
	public static final Graphics3D getInstance() {

		if (s_instance == null) {
			s_instance = new Graphics3D();
		}
		return s_instance;
	}

	public static void initGraphics3D() {
		s_instance = null;
	}

	public static void setUnlitTexturesWorkaround(boolean enabled) {
		unlitTexturesWorkaround = enabled;
	}

	static boolean isUnlitTexturesWorkaroundEnabled() {
		return unlitTexturesWorkaround;
	}

	private Graphics3D() {
		iInterface = Interface.getInstance();
		initNativePeer();

		// setup listener for singleton teardown
		destroyer = new Destroyer(this);
	}

	//------------------------------------------------------------------
	// Public methods
	//------------------------------------------------------------------

	/**
	 */
	public void bindTarget(java.lang.Object target) {
		bindTarget(target, true, 0);
	}

	/**
	 *
	 */
	public void bindTarget(java.lang.Object target, boolean depth, int flags) {
		integrityCheck();
		if (currentTarget != null) {
			throw new IllegalStateException();
		}
		if (target == null) {
			throw new NullPointerException();
		}

		final int finalFlags = flags;
		final boolean finalDepth = depth;

		if (target instanceof Graphics) {
			final Graphics finalG = (Graphics) target;
			final int clipX = finalG.getClipX() + finalG.getTranslateX();
			final int clipY = finalG.getClipY() + finalG.getTranslateY();
			final int clipW = finalG.getClipWidth();
			final int clipH = finalG.getClipHeight();
			if (clipW > Defs.MAX_VIEWPORT_WIDTH ||
					clipH > Defs.MAX_VIEWPORT_HEIGHT) {
				throw new IllegalArgumentException();
			}

			int w = clipW > 0 ? clipW : 240;
			int h = clipH > 0 ? clipH : 320;
			int[] directSurfacePixels = null;
			if (finalG instanceof org.neutron.graphics3d.spi.DirectFrameSurface) {
				org.neutron.graphics3d.spi.DirectFrameSurface dfs = (org.neutron.graphics3d.spi.DirectFrameSurface) finalG;
				w = dfs.getWidth();
				h = dfs.getHeight();
				directSurfacePixels = dfs.getImageData();
			} else {
				try {
					java.lang.reflect.Method m = finalG.getClass().getMethod("getGraphicsSurface");
					Object s = m.invoke(finalG);
					if (s instanceof org.neutron.graphics3d.spi.DirectFrameSurface) {
						org.neutron.graphics3d.spi.DirectFrameSurface dfs = (org.neutron.graphics3d.spi.DirectFrameSurface) s;
						w = dfs.getWidth();
						h = dfs.getHeight();
						directSurfacePixels = dfs.getImageData();
					} else if (s != null) {
						w = (Integer) s.getClass().getMethod("getWidth").invoke(s);
						h = (Integer) s.getClass().getMethod("getHeight").invoke(s);
					}
				} catch (Throwable ignored) {}
			}
			final int width = w;
			final int height = h;
			java.nio.ByteBuffer directBuf = getDirectBuffer(width * height * 4);
			directBuf.order(java.nio.ByteOrder.nativeOrder());
			directBuf.position(0);

			if ((finalFlags & OVERWRITE) == 0 && directSurfacePixels != null && directSurfacePixels.length >= width * height) {
				directBuf.asIntBuffer().put(directSurfacePixels, 0, width * height);
			}

			buffer = directBuf;

			Platform.executeInUIThread(
					new M3gRunnable() {
						@Override
						public void doRun() {
							iIsImageTarget = _bindGraphics(
									handle, 0, width, height,
									clipX, clipY, clipW, clipH,
									finalDepth, finalFlags, iIsProperRenderer,
									buffer);
						}
					});
			currentTarget = finalG;
			hasCustomViewport = false;
			cur_width = width;
			cur_height = height;
			cur_clipX = clipX;
			cur_clipY = clipY;
			cur_clipW = clipW > 0 ? clipW : width;
			cur_clipH = clipH > 0 ? clipH : height;
		} else if (target instanceof Image2D) {
			Image2D img = (Image2D) target;

			offsetX = offsetY = 0;
			final long imageHandle = img.handle;

			Platform.executeInUIThread(
					new M3gRunnable() {
						@Override
						public void doRun() {
							_bindImage(handle, imageHandle, finalDepth, finalFlags);
						}
					});
			currentTarget = img;
		} else {
			throw new IllegalArgumentException();
		}

		hints = flags;
		depthEnabled = depth;
	}

	/**
	 *
	 */
	public void releaseTarget() {
		integrityCheck();
		if (currentTarget == null) {
			return;
		}

		if (currentTarget instanceof Graphics) {
			final Graphics finalG = (Graphics) currentTarget;
			Platform.executeInUIThread(
					new M3gRunnable() {
						@Override
						public void doRun() {
							_releaseGraphics(handle,
									0, iIsImageTarget, iIsProperRenderer, buffer);
						}
					});
			if (buffer instanceof java.nio.ByteBuffer) {
				java.nio.ByteBuffer byteBuf = (java.nio.ByteBuffer) buffer;
				int w = cur_width;
				int h = cur_height;
				int[] intData = getIntArray(w * h);
				byteBuf.order(java.nio.ByteOrder.nativeOrder());
				byteBuf.position(0);
				byteBuf.asIntBuffer().get(intData, 0, w * h);

				int vx = hasCustomViewport ? unionVpX : getViewportX();
				int vy = hasCustomViewport ? unionVpY : getViewportY();
				int vw = hasCustomViewport ? unionVpW : getViewportWidth();
				int vh = hasCustomViewport ? unionVpH : getViewportHeight();

				int startX = Math.max(vx, cur_clipX);
				int startY = Math.max(vy, cur_clipY);
				int endX = Math.min(Math.min(w, vx + vw), cur_clipX + cur_clipW);
				int endY = Math.min(Math.min(h, vy + vh), cur_clipY + cur_clipH);
				int blitW = endX - startX;
				int blitH = endY - startY;

				if (blitW > 0 && blitH > 0) {
					int offset = startY * w + startX;
					finalG.drawRGB(intData, offset, w, startX, startY, blitW, blitH, false);
				}
				buffer = null;
				hasCustomViewport = false;
			}
		} else if (currentTarget instanceof Image2D) {
			Platform.executeInUIThread(
					new M3gRunnable() {
						@Override
						public void doRun() {
							_releaseImage(handle);
						}
					});
		} else {
			throw new Error();
		}
		currentTarget = null;
	}

	/**
	 *
	 */
	public void setViewport(int x, int y, int width, int height) {
		integrityCheck();
		if (width <= 0 || height <= 0
				|| width > Defs.MAX_VIEWPORT_DIMENSION
				|| height > Defs.MAX_VIEWPORT_DIMENSION) {
			throw new IllegalArgumentException();
		}
		if (!hasCustomViewport) {
			unionVpX = x;
			unionVpY = y;
			unionVpW = width;
			unionVpH = height;
			hasCustomViewport = true;
		} else {
			int minX = Math.min(unionVpX, x);
			int minY = Math.min(unionVpY, y);
			int maxX = Math.max(unionVpX + unionVpW, x + width);
			int maxY = Math.max(unionVpY + unionVpH, y + height);
			unionVpX = minX;
			unionVpY = minY;
			unionVpW = maxX - minX;
			unionVpH = maxY - minY;
		}
		_setViewport(handle, x + offsetX, y + offsetY, width, height);
	}

	/**
	 *
	 */
	public void clear(Background background) {
		integrityCheck();
		final Background finalBackground = background;
		Platform.executeInUIThread(
				new M3gRunnable() {
					@Override
					public void doRun() {
						_clear(handle, finalBackground != null ? finalBackground.handle : 0);
					}
				});
	}

	/**
	 *
	 */
	public void render(World world) {
		integrityCheck();
		final World finalWorld = world;
		Platform.executeInUIThread(
				new M3gRunnable() {
					@Override
					public void doRun() {
						_renderWorld(handle, finalWorld.handle);
					}
				});
	}

	/**
	 *
	 */
	public void render(VertexBuffer vertices,
					   IndexBuffer primitives,
					   Appearance appearance,
					   Transform transform) {
		// Call rendering method with default visibility
		integrityCheck();
		render(vertices, primitives, appearance, transform, -1);
	}

	/**
	 *
	 */
	public void render(VertexBuffer vertices,
					   IndexBuffer primitives,
					   Appearance appearance,
					   Transform transform,
					   int scope) {

		// null pointer exceptions thrown automatically below
		integrityCheck();

		final VertexBuffer finalVertices = vertices;
		final IndexBuffer finalPrimitives = primitives;
		final Appearance finalAppearance = appearance;
		final Transform finalTransform = transform;
		final int finalScope = scope;

		Platform.executeInUIThread(
				new M3gRunnable() {
					@Override
					public void doRun() {
						_render(handle,
								finalVertices.handle,
								finalPrimitives.handle,
								finalAppearance.handle,
								finalTransform != null ? finalTransform.matrix : null,
								finalScope);
					}
				});
	}

	/**
	 *
	 */
	public void render(Node node, Transform transform) {
		if (!(node instanceof Mesh
				|| node instanceof Sprite3D
				|| node instanceof Group)
				&& node != null) {
			throw new IllegalArgumentException();
		}
		integrityCheck();

		final Node finalNode = node;
		final Transform finalTransform = transform;

		Platform.executeInUIThread(
				new M3gRunnable() {
					@Override
					public void doRun() {
						_renderNode(handle,
								finalNode.handle,
								finalTransform != null ? finalTransform.matrix : null);
					}
				});
	}


	public void setCamera(Camera camera, Transform transform) {
		integrityCheck();
		_setCamera(handle,
				camera != null ? camera.handle : 0,
				transform != null ? transform.matrix : null);

		this.camera = camera;
	}

	/**
	 */
	public int addLight(Light light, Transform transform) {
		integrityCheck();
		int index = _addLight(handle,
				light.handle,
				transform != null ? transform.matrix : null);
		if (lights.size() < index + 1) {
			lights.setSize(index + 1);
		}
		lights.setElementAt(light, index);
		return index;
	}

	/**
	 *
	 */
	public void setLight(int index, Light light, Transform transform) {
		integrityCheck();
		_setLight(handle,
				index,
				light != null ? light.handle : 0,
				transform != null ? transform.matrix : null);
		lights.setElementAt(light, index);
	}

	/**
	 */
	public void resetLights() {
		integrityCheck();
		_resetLights(handle);
		lights.removeAllElements();
	}

	/**
	 *
	 */
	public static final Hashtable getProperties() {
		Hashtable props = new Hashtable();

		props.put("supportAntialiasing", new java.lang.Boolean(
				_isAASupported(Interface.getHandle())));
		props.put("supportTrueColor", new java.lang.Boolean(Defs.supportTrueColor));
		props.put("supportDithering", new java.lang.Boolean(Defs.supportDithering));
		props.put("supportMipmapping", new java.lang.Boolean(Defs.supportMipmapping));
		props.put("supportPerspectiveCorrection", new java.lang.Boolean(Defs.supportPerspectiveCorrection));
		props.put("supportLocalCameraLighting", new java.lang.Boolean(Defs.supportLocalCameraLighting));
		props.put("maxLights", new java.lang.Integer(Defs.MAX_LIGHTS));
		props.put("maxViewportWidth", new java.lang.Integer(Defs.MAX_VIEWPORT_WIDTH));
		props.put("maxViewportHeight", new java.lang.Integer(Defs.MAX_VIEWPORT_HEIGHT));
		props.put("maxViewportDimension", new java.lang.Integer(Defs.MAX_VIEWPORT_DIMENSION));
		props.put("maxTextureDimension", new java.lang.Integer(Defs.MAX_TEXTURE_DIMENSION));
		props.put("maxSpriteCropDimension", new java.lang.Integer(Defs.MAX_TEXTURE_DIMENSION));
		props.put("numTextureUnits", new java.lang.Integer(Defs.NUM_TEXTURE_UNITS));
		props.put("maxTransformsPerVertex", new java.lang.Integer(Defs.MAX_TRANSFORMS_PER_VERTEX));

		// Extra properties
		props.put("m3gRelease", new java.lang.String("04_wk49"));

		return props;
	}

	/**
	 *
	 */
	public void setDepthRange(float near, float far) {
		integrityCheck();
		_setDepthRange(handle, near, far);
	}

	// M3G 1.1

	public Camera getCamera(Transform transform) {
		integrityCheck();
		if (transform != null) {
			_getViewTransform(handle, transform.matrix);
		}

		return (Camera) Object3D.getInstance(_getCamera(handle));
	}

	public float getDepthRangeFar() {
		integrityCheck();
		return _getDepthRangeFar(handle);
	}

	public float getDepthRangeNear() {
		integrityCheck();
		return _getDepthRangeNear(handle);
	}

	public Light getLight(int index, Transform transform) {
		integrityCheck();
		if (index < 0 || index >= _getLightCount(handle)) {
			throw new IndexOutOfBoundsException();
		}

		return (Light) Object3D.getInstance(_getLightTransform(handle,
				index,
				transform != null ? transform.matrix : null));
	}

	public int getLightCount() {
		integrityCheck();
		return _getLightCount(handle);
	}

	public java.lang.Object getTarget() {
		return currentTarget;
	}

	public int getViewportHeight() {
		integrityCheck();
		return _getViewportHeight(handle);
	}

	public int getViewportWidth() {
		integrityCheck();
		return _getViewportWidth(handle);
	}

	public int getViewportX() {
		integrityCheck();
		return _getViewportX(handle) - offsetX;
	}

	public int getViewportY() {
		integrityCheck();
		return _getViewportY(handle) - offsetY;
	}

	public int getHints() {
		return hints;
	}

	public boolean isDepthBufferEnabled() {
		return depthEnabled;
	}

	// M3G 1.1 getters END

	//------------------------------------------------------------------
	// Private methods
	//------------------------------------------------------------------

	private void integrityCheck() {
		if (iInterface == null) {
			throw new RuntimeException("Graphics3D closed");
		}
		if (!iNativeInitialized) {
			// If native interface cannot be initialized we cannot recover from it
			if (!initNativePeer()) {
				throw new Error("UI thread not available");
			}
		}
	}

	/**
	 * Initializes native peer
	 *
	 * @return true if native interface was succesfully inialized otherwise false
	 */
	private boolean initNativePeer() {
		if (iNativeInitialized) {
			return true;
		}
		if (iInterface.isFullyInitialized() && Platform.uiThreadAvailable()) {
			handle = _ctor(Interface.getHandle());
			_addRef(handle);

			Platform.executeInUIThread(
					new M3gRunnable() {
						@Override
						public void doRun() {
							iIsProperRenderer = _isProperRenderer();
						}
					});
			iNativeInitialized = true;
			return true;
		} else {
			return false;
		}
	}

	//------------------------------------------------------------------
	// Native implementation methods
	//------------------------------------------------------------------
	private native static long _ctor(long hInterface);

	private native static void _addRef(long hObject);

	private native static int _addLight(long handle,
										long hLight,
										byte[] transform);

	private native static boolean _bindGraphics(long handle,
												long surfaceHandle,
												int width, int height,
												int clipX, int clipY,
												int clipW, int clipH,
												boolean depth,
												int hintBits,
												boolean aIsProperRenderer,
												Object pixels);

	private native static void _bindImage(long handle, long imgHandle, boolean depth, int hintBits);

	private native static void _releaseGraphics(long handle,
												long surfaceHandle,
												boolean aIsImageTarget,
												boolean aIsProperRenderer,
												Object pixels);

	private native static void _releaseImage(long handle);

	private native static void _resetLights(long handle);

	private native static void _clear(long handle, long hBackground);

	private native static void _render(long handle,
									   long hVtxBuffer,
									   long hIdxBuffer,
									   long hAppearance,
									   byte[] transform,
									   int scope);

	private native static void _renderNode(long handle, long hNode, byte[] transform);

	private native static void _renderWorld(long handle, long hWorld);

	private native static void _setCamera(long handle,
										  long hCamera,
										  byte[] transform);

	private native static void _setViewport(long handle,
											int x, int y,
											int width, int height);

	private native static void _setLight(long handle,
										 int index,
										 long hLight,
										 byte[] transform);

	private native static void _setDepthRange(long handle,
											  float near,
											  float far);

	// M3G 1.1
	// Maintenance release getters

	private native static void _getViewTransform(long handle,
												 byte[] transform);

	private native static long _getCamera(long handle);

	private native static long _getLightTransform(long handle,
												 int index,
												 byte[] transform);

	private native static int _getLightCount(long handle);

	private native static float _getDepthRangeNear(long handle);

	private native static float _getDepthRangeFar(long handle);

	private native static int _getViewportX(long handle);

	private native static int _getViewportY(long handle);

	private native static int _getViewportWidth(long handle);

	private native static int _getViewportHeight(long handle);

	/* Statistics support, MUST be disabled in official releases! */
    /*
        public native static int getStatistics(int[] statistics);
    */
	private native static boolean _isAASupported(long handle);

	private native static boolean _isProperRenderer();
}
