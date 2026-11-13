/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under the Apache License, Version 2.0 or LGPL 2.1
 */
package org.neutron.graphics3d.opengl.m3g;

import org.junit.Assert;
import org.junit.Test;
import org.neutron.graphics3d.Graphics3DConfig;
import org.neutron.graphics3d.Graphics3DProvider;
import org.neutron.graphics3d.RenderBackendType;
import org.neutron.graphics3d.opengl.OpenGLRenderBackend;
import org.neutron.graphics3d.spi.DirectFrameSurface;
import org.neutron.graphics3d.spi.M3GBackend;

public class GLM3GBackendTest {

    @Test
    public void testBackendInstance() {
        GLM3GBackend backend = GLM3GBackend.getInstance();
        Assert.assertNotNull("GLM3GBackend instance must not be null", backend);
    }

    @Test
    public void testSPIRegistration() {
        Graphics3DConfig.setBackendType(RenderBackendType.OPENGL);
        OpenGLRenderBackend.registerIfAvailable();

        if (GLM3GBackend.getInstance().isAvailable()) {
            M3GBackend active = Graphics3DProvider.getActiveM3GBackend();
            Assert.assertNotNull("Active M3G backend must be registered when OpenGL is available", active);
        }
    }

    @Test
    public void testDirectFrameSurfaceContract() {
        final int[] pixels = new int[240 * 320];
        DirectFrameSurface surface = new DirectFrameSurface() {
            @Override
            public int[] getImageData() {
                return pixels;
            }

            @Override
            public int getWidth() {
                return 240;
            }

            @Override
            public int getHeight() {
                return 320;
            }
        };

        Assert.assertEquals(240, surface.getWidth());
        Assert.assertEquals(320, surface.getHeight());
        Assert.assertNotNull(surface.getImageData());
        Assert.assertEquals(240 * 320, surface.getImageData().length);
    }
}

