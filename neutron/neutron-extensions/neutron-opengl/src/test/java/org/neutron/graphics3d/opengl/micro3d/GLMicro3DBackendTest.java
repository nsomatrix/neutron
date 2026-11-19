/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under the Apache License, Version 2.0 or LGPL 2.1
 */
package org.neutron.graphics3d.opengl.micro3d;

import org.junit.Assert;
import org.junit.Test;
import org.neutron.graphics3d.Graphics3DConfig;
import org.neutron.graphics3d.Graphics3DProvider;
import org.neutron.graphics3d.RenderBackendType;
import org.neutron.graphics3d.opengl.OpenGLRenderBackend;
import org.neutron.graphics3d.spi.DirectFrameSurface;
import org.neutron.graphics3d.spi.Micro3DBackend;

public class GLMicro3DBackendTest {

    @Test
    public void testBackendInstance() {
        GLMicro3DBackend backend = GLMicro3DBackend.getInstance();
        Assert.assertNotNull("GLMicro3DBackend instance must not be null", backend);
    }

    @Test
    public void testSPIRegistration() {
        System.setProperty("neutron.3d.experimental_gl_micro3d", "true");
        try {
            Graphics3DConfig.setBackendType(RenderBackendType.OPENGL);
            OpenGLRenderBackend.registerIfAvailable();

            if (GLMicro3DBackend.getInstance().isAvailable()) {
                Micro3DBackend active = Graphics3DProvider.getActiveMicro3DBackend();
                Assert.assertNotNull("Active Micro3D backend must be registered when OpenGL is available", active);
            }
        } finally {
            System.clearProperty("neutron.3d.experimental_gl_micro3d");
            Graphics3DProvider.registerMicro3DBackend(null);
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

