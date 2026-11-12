/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under the Apache License, Version 2.0 or LGPL 2.1
 */
package org.neutron.graphics3d;

/**
 * Supported 3D hardware abstraction layer render backends.
 */
public enum RenderBackendType {
    /**
     * Pure-Java CPU software rasterizer.
     * Zero external dependencies, runs in headless/CI and across all platforms.
     */
    SOFTWARE,

    /**
     * Hardware-accelerated OpenGL / GLSL backend via modern desktop GPU.
     * Provides subpixel floating-point geometry, hardware depth buffers,
     * and full perspective-correct texture mapping without jitter.
     */
    OPENGL
}

