package org.neutron.jsr239;

import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.*;
import org.junit.Assert;
import org.junit.Test;

public class JSR239Test {
    @Test
    public void testEGLInitialization() {
        EGL egl = EGLContext.getEGL();
        Assert.assertNotNull(egl);
        Assert.assertTrue(egl instanceof EGL10);

        EGL10 egl10 = (EGL10) egl;
        EGLDisplay display = egl10.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        Assert.assertNotNull(display);

        int[] version = new int[2];
        Assert.assertTrue(egl10.eglInitialize(display, version));

        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfig = new int[1];
        Assert.assertTrue(egl10.eglChooseConfig(display, new int[] { EGL10.EGL_NONE }, configs, 1, numConfig));

        EGLContext context = egl10.eglCreateContext(display, configs[0], EGL10.EGL_NO_CONTEXT, null);
        Assert.assertNotNull(context);

        EGLSurface surface = egl10.eglCreatePbufferSurface(display, configs[0], null);
        egl10.eglMakeCurrent(display, surface, surface, context);

        GL gl = context.getGL();
        Assert.assertNotNull(gl);
        Assert.assertTrue(gl instanceof GL10);
        Assert.assertTrue(gl instanceof GL11);

        GL10 gl10 = (GL10) gl;
        if (org.neutron.graphics3d.opengl.GLContextManager.isAvailable()) {
            gl10.glMatrixMode(GL10.GL_MODELVIEW);
            gl10.glLoadIdentity();
            gl10.glTranslatex(1 << 16, 2 << 16, 3 << 16);
            gl10.glRotatex(90 << 16, 0, 0, 1 << 16);
            gl10.glScalex(2 << 16, 2 << 16, 2 << 16);
            gl10.glColor4x(65536, 0, 0, 65536);
            gl10.glClearColorx(0, 0, 0, 65536);
        }
    }
}

