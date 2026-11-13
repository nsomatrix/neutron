/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under the Apache License, Version 2.0 or LGPL 2.1
 */
package org.neutron.graphics3d.spi;

/**
 * Direct access interface for frame surface pixel buffers.
 * Eliminates runtime reflection overhead in hot rendering loops.
 */
public interface DirectFrameSurface {

    /**
     * Returns the underlying direct pixel array (ARGB format).
     *
     * @return raw integer pixel array, or null if direct memory access is not supported
     */
    int[] getImageData();

    /**
     * Width of the frame surface in pixels.
     *
     * @return surface width
     */
    int getWidth();

    /**
     * Height of the frame surface in pixels.
     *
     * @return surface height
     */
    int getHeight();
}

