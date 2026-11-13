/*
 * MascotCapsule Micro3D v3 Implementation for Neutron
 *
 * Aligned with J2ME-Loader hardware OpenGL architecture.
 */
package com.mascotcapsule.micro3d.v3;

import javax.microedition.lcdui.Graphics;
import org.neutron.graphics3d.Graphics3DConfig;
import org.neutron.graphics3d.Graphics3DProvider;
import org.neutron.graphics3d.RenderBackendType;
import org.neutron.graphics3d.spi.Micro3DBackend;

@SuppressWarnings("unused, WeakerAccess")
public class Graphics3D {
	public static final int COMMAND_LIST_VERSION_1_0 = 0xFE000001;
	public static final int COMMAND_END = 0x80000000;
	public static final int COMMAND_NOP = 0x81000000;
	public static final int COMMAND_FLUSH = 0x82000000;
	public static final int COMMAND_ATTRIBUTE = 0x83000000;
	public static final int COMMAND_CLIP = 0x84000000;
	public static final int COMMAND_CENTER = 0x85000000;
	public static final int COMMAND_TEXTURE_INDEX = 0x86000000;
	public static final int COMMAND_AFFINE_INDEX = 0x87000000;
	public static final int COMMAND_PARALLEL_SCALE = 0x90000000;
	public static final int COMMAND_PARALLEL_SIZE = 0x91000000;
	public static final int COMMAND_PERSPECTIVE_FOV = 0x92000000;
	public static final int COMMAND_PERSPECTIVE_WH = 0x93000000;
	public static final int COMMAND_AMBIENT_LIGHT = 0xa0000000;
	public static final int COMMAND_DIRECTION_LIGHT = 0xa1000000;
	public static final int COMMAND_THRESHOLD = 0xaf000000;

	public static final int ENV_ATTR_LIGHTING = 1;
	public static final int ENV_ATTR_SPHERE_MAP = 2;
	public static final int ENV_ATTR_TOON_SHADING = 4;
	public static final int ENV_ATTR_SEMI_TRANSPARENT = 8;

	public static final int PATTR_LIGHTING = 0x01;
	public static final int PATTR_SPHERE_MAP = 0x02;
	public static final int PATTR_COLORKEY = 0x10;
	public static final int PATTR_BLEND_NORMAL = 0x00;
	public static final int PATTR_BLEND_HALF = 0x20;
	public static final int PATTR_BLEND_ADD = 0x40;
	public static final int PATTR_BLEND_SUB = 0x60;

	public static final int PDATA_NORMAL_NONE = 0x0000;
	public static final int PDATA_NORMAL_PER_FACE = 0x0200;
	public static final int PDATA_NORMAL_PER_VERTEX = 0x0300;
	public static final int PDATA_COLOR_NONE = 0x0000;
	public static final int PDATA_COLOR_PER_COMMAND = 0x0400;
	public static final int PDATA_COLOR_PER_FACE = 0x0800;
	public static final int PDATA_TEXURE_COORD_NONE = 0x0000;
	public static final int PDATA_TEXURE_COORD = 0x3000;
	public static final int PDATA_POINT_SPRITE_PARAMS_PER_CMD = 0x1000;
	public static final int PDATA_POINT_SPRITE_PARAMS_PER_FACE = 0x2000;
	public static final int PDATA_POINT_SPRITE_PARAMS_PER_VERTEX = 0x3000;

	public static final int POINT_SPRITE_LOCAL_SIZE = 0;
	public static final int POINT_SPRITE_PIXEL_SIZE = 1;
	public static final int POINT_SPRITE_PERSPECTIVE = 0;
	public static final int POINT_SPRITE_NO_PERS = 2;

	public static final int PRIMITVE_POINTS = 0x01000000;
	public static final int PRIMITVE_LINES = 0x02000000;
	public static final int PRIMITVE_TRIANGLES = 0x03000000;
	public static final int PRIMITVE_QUADS = 0x04000000;
	public static final int PRIMITVE_POINT_SPRITES = 0x05000000;

	private Graphics boundGraphics;
	private Micro3DBackend activeGlBackend;
	private boolean disposed = false;

	public Graphics3D() {}

	public final synchronized void bind(Graphics graphics) {
		if (disposed) return;
		if (graphics == null) {
			throw new NullPointerException("Argument 'Graphics' is NULL");
		}
		if (boundGraphics != null) {
			throw new IllegalStateException("Target already bound");
		}

		boundGraphics = graphics;
		activeGlBackend = Graphics3DProvider.getActiveMicro3DBackend();
		if (activeGlBackend != null && activeGlBackend.isAvailable()) {
			int w = graphics.getClipWidth() > 0 ? graphics.getClipWidth() : 240;
			int h = graphics.getClipHeight() > 0 ? graphics.getClipHeight() : 320;
			activeGlBackend.bind(graphics, w, h);
		} else {
			activeGlBackend = null;
		}
	}

	public final synchronized void release(Graphics graphics) {
		if (disposed) return;
		if (graphics == null) {
			throw new NullPointerException("Argument 'Graphics' is NULL");
		}
		if (graphics != boundGraphics) {
			boundGraphics = null;
			activeGlBackend = null;
			return;
		}
		if (activeGlBackend != null) {
			activeGlBackend.release();
			activeGlBackend = null;
		}
		boundGraphics = null;
	}

	public final synchronized void flush() {
		if (disposed) return;
		if (boundGraphics == null) {
			throw new IllegalStateException("No target is bound");
		}
		if (activeGlBackend != null) {
			activeGlBackend.flush();
		}
	}

	public final synchronized void dispose() {
		disposed = true;
		if (activeGlBackend != null) {
			activeGlBackend.release();
			activeGlBackend = null;
		}
		boundGraphics = null;
	}

	public final void renderFigure(Figure figure, int x, int y, FigureLayout layout, Effect3D effect) {
		checkTargetIsValid();
		if (figure == null || layout == null || effect == null) {
			throw new NullPointerException();
		}
		if (activeGlBackend != null) {
			activeGlBackend.renderFigure(figure, x, y, layout, effect);
		}
	}

	public final void drawFigure(Figure figure, int x, int y, FigureLayout layout, Effect3D effect) {
		checkTargetIsValid();
		if (figure == null || layout == null || effect == null) {
			throw new NullPointerException();
		}
		if (activeGlBackend != null) {
			activeGlBackend.drawFigure(figure, x, y, layout, effect);
		}
	}

	public final void renderPrimitives(Texture texture, int x, int y,
									   FigureLayout layout, Effect3D effect,
									   int command, int numPrimitives,
									   int[] vertexCoords, int[] normals,
									   int[] textureCoords, int[] colors) {
		checkTargetIsValid();
		if (layout == null || effect == null || vertexCoords == null || normals == null
				|| textureCoords == null || colors == null) {
			throw new NullPointerException();
		}
		if (numPrimitives <= 0 || numPrimitives > 255) {
			throw new IllegalArgumentException("numPrimitives must be in range 1..255");
		}
		if (activeGlBackend != null) {
			activeGlBackend.renderPrimitives(texture, x, y, layout, effect,
					command, numPrimitives, vertexCoords, normals, textureCoords, colors);
		}
	}

	public final void drawCommandList(Texture[] textures, int x, int y,
									  FigureLayout layout, Effect3D effect,
									  int[] commandList) {
		checkTargetIsValid();
		if (layout == null || effect == null || commandList == null) {
			throw new NullPointerException();
		}
		if (activeGlBackend != null) {
			activeGlBackend.drawCommandList(textures, x, y, layout, effect, commandList);
		}
	}

	public final void drawCommandList(Texture texture, int x, int y,
									  FigureLayout layout, Effect3D effect,
									  int[] commandList) {
		checkTargetIsValid();
		if (layout == null || effect == null || commandList == null) {
			throw new NullPointerException();
		}
		if (activeGlBackend != null) {
			activeGlBackend.drawCommandList(texture, x, y, layout, effect, commandList);
		}
	}

	private void checkTargetIsValid() {
		if (boundGraphics == null) {
			throw new IllegalStateException("No target is bound");
		}
	}
}
