/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under the Apache License, Version 2.0 or LGPL 2.1
 */
package org.neutron.graphics3d.spi;

/**
 * Service Provider Interface (SPI) for MascotCapsule Micro3D v3 rendering backends.
 */
public interface Micro3DBackend {

    /**
     * Checks if this backend is available on the current host system.
     *
     * @return true if hardware context and drivers are available
     */
    boolean isAvailable();

    /**
     * Initializes the rendering backend and compiles any required shaders.
     *
     * @return true if initialization succeeded
     */
    boolean init();

    /**
     * Binds a target rendering surface.
     *
     * @param target target surface (e.g. Graphics or DirectFrameSurface)
     * @param width  viewport width
     * @param height viewport height
     */
    void bind(Object target, int width, int height);

    /**
     * Releases the active rendering target.
     */
    void release();

    /**
     * Flushes submitted geometry and presents the current frame.
     */
    void flush();

    /**
     * Renders a 3D figure with layout and lighting effects.
     */
    void renderFigure(Object figure, int x, int y, Object layout, Object effect);

    /**
     * Draws a 3D figure (render and flush).
     */
    void drawFigure(Object figure, int x, int y, Object layout, Object effect);

    /**
     * Renders primitive geometry (triangles, quads, lines, points, point sprites).
     */
    void renderPrimitives(Object texture, int x, int y, Object layout, Object effect,
                          int command, int numPrimitives,
                          int[] vertexCoords, int[] normalCoords, int[] textureCoords, int[] colorCoords);

    /**
     * Executes a precompiled MascotCapsule command list with multiple textures.
     */
    void drawCommandList(Object[] textures, int x, int y, Object layout, Object effect, int[] commandList);

    /**
     * Executes a precompiled MascotCapsule command list with a single texture.
     */
    void drawCommandList(Object texture, int x, int y, Object layout, Object effect, int[] commandList);
}

