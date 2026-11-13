/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under the Apache License, Version 2.0 or LGPL 2.1
 */
package org.neutron.graphics3d.spi;

/**
 * Service Provider Interface (SPI) for JSR-184 (M3G) rendering backends.
 */
public interface M3GBackend {

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
     * @param target      target surface (e.g. Graphics or Image2D)
     * @param width       target width
     * @param height      target height
     * @param depthBuffer whether depth buffer is enabled
     * @param hints       rendering hint bitmask
     */
    void bindTarget(Object target, int width, int height, boolean depthBuffer, int hints);

    /**
     * Releases the active rendering target.
     */
    void releaseTarget();

    /**
     * Clears color and/or depth buffers.
     *
     * @param clearColor ARGB background clear color
     * @param clearDepth depth buffer clear value (typically 1.0f)
     */
    void clear(int clearColor, float clearDepth);

    /**
     * Updates viewport dimensions.
     */
    void setViewport(int x, int y, int width, int height);

    /**
     * Sets the depth range mappings.
     */
    void setDepthRange(float near, float far);

    /**
     * Sets the active Camera and camera-to-world transform for projection.
     *
     * @param camera    active Camera object
     * @param transform camera transform (view transform)
     */
    void setCamera(Object camera, Object transform);

    /**
     * Renders a submesh (VertexBuffer + IndexBuffer + Appearance).
     */
    void renderSubmesh(Object vertices, Object triangles, Object appearance, Object transform, int scope, int alphaFactor);

    /**
     * Renders an entire M3G World scene graph.
     */
    void renderWorld(Object world);
}

