package org.neutron.jsr239;

import javax.microedition.khronos.egl.*;
import javax.microedition.khronos.opengles.GL;
import org.neutron.graphics3d.opengl.GLContextManager;
import org.neutron.graphics3d.opengl.GLFramebuffer;

public class EGL10Impl implements EGL11 {
    private static final GL11Impl GL_INSTANCE = new GL11Impl();
    private static final EGLDisplay DEFAULT_DISPLAY = new EGLDisplay() {};
    private static final EGLContext DEFAULT_CONTEXT = new EGLContext() {
        @Override
        public GL getGL() {
            return GL_INSTANCE;
        }
    };
    private static final EGLSurface DEFAULT_SURFACE = new EGLSurface() {};
    private static final EGLConfig DEFAULT_CONFIG = new EGLConfig() {};

    public static class SurfaceImpl extends EGLSurface {
        public Object target;
        public GLFramebuffer fbo;
        public int width = 240;
        public int height = 320;
        public int[] pixelBuffer;

        public SurfaceImpl(Object target) {
            this.target = target;
            if (target instanceof javax.microedition.lcdui.Graphics) {
                javax.microedition.lcdui.Graphics g = (javax.microedition.lcdui.Graphics) target;
                int cw = g.getClipWidth();
                int ch = g.getClipHeight();
                if (cw > 0 && ch > 0) {
                    this.width = cw;
                    this.height = ch;
                }
            } else if (target instanceof javax.microedition.lcdui.Canvas) {
                javax.microedition.lcdui.Canvas c = (javax.microedition.lcdui.Canvas) target;
                int cw = c.getWidth();
                int ch = c.getHeight();
                if (cw > 0 && ch > 0) {
                    this.width = cw;
                    this.height = ch;
                }
            }
            if (GLContextManager.isAvailable()) {
                this.fbo = new GLFramebuffer();
                this.fbo.resize(this.width, this.height);
                this.pixelBuffer = new int[this.width * this.height];
            }
        }

        public void dispose() {
            if (fbo != null) {
                fbo.destroy();
                fbo = null;
            }
        }
    }

    @Override
    public boolean eglChooseConfig(EGLDisplay display, int[] attrib_list, EGLConfig[] configs, int config_size, int[] num_config) {
        if (num_config != null && num_config.length > 0) {
            num_config[0] = 1;
        }
        if (configs != null && configs.length > 0) {
            configs[0] = DEFAULT_CONFIG;
        }
        return true;
    }

    @Override
    public boolean eglCopyBuffers(EGLDisplay display, EGLSurface surface, Object native_pixmap) {
        return true;
    }

    @Override
    public EGLContext eglCreateContext(EGLDisplay display, EGLConfig config, EGLContext share_context, int[] attrib_list) {
        GLContextManager.makeCurrent();
        return DEFAULT_CONTEXT;
    }

    @Override
    public EGLSurface eglCreatePbufferSurface(EGLDisplay display, EGLConfig config, int[] attrib_list) {
        return DEFAULT_SURFACE;
    }

    @Override
    public EGLSurface eglCreatePixmapSurface(EGLDisplay display, EGLConfig config, Object native_pixmap, int[] attrib_list) {
        return DEFAULT_SURFACE;
    }

    @Override
    public EGLSurface eglCreateWindowSurface(EGLDisplay display, EGLConfig config, Object native_window, int[] attrib_list) {
        if (native_window != null) {
            return new SurfaceImpl(native_window);
        }
        return DEFAULT_SURFACE;
    }

    @Override
    public boolean eglDestroyContext(EGLDisplay display, EGLContext context) {
        return true;
    }

    @Override
    public boolean eglDestroySurface(EGLDisplay display, EGLSurface surface) {
        if (surface instanceof SurfaceImpl) {
            ((SurfaceImpl) surface).dispose();
        }
        return true;
    }

    @Override
    public boolean eglGetConfigAttrib(EGLDisplay display, EGLConfig config, int attribute, int[] value) {
        if (value != null && value.length > 0) {
            if (attribute == EGL_DEPTH_SIZE) value[0] = 24;
            else if (attribute == EGL_RED_SIZE || attribute == EGL_GREEN_SIZE || attribute == EGL_BLUE_SIZE || attribute == EGL_ALPHA_SIZE) value[0] = 8;
            else value[0] = 0;
        }
        return true;
    }

    @Override
    public boolean eglGetConfigs(EGLDisplay display, EGLConfig[] configs, int config_size, int[] num_config) {
        return eglChooseConfig(display, null, configs, config_size, num_config);
    }

    @Override
    public EGLContext eglGetCurrentContext() {
        return DEFAULT_CONTEXT;
    }

    @Override
    public EGLDisplay eglGetCurrentDisplay() {
        return DEFAULT_DISPLAY;
    }

    @Override
    public EGLSurface eglGetCurrentSurface(int readdraw) {
        return DEFAULT_SURFACE;
    }

    @Override
    public EGLDisplay eglGetDisplay(Object native_display) {
        return DEFAULT_DISPLAY;
    }

    @Override
    public int eglGetError() {
        return EGL_SUCCESS;
    }

    @Override
    public boolean eglInitialize(EGLDisplay display, int[] major_minor) {
        GLContextManager.isAvailable();
        if (major_minor != null && major_minor.length >= 2) {
            major_minor[0] = 1;
            major_minor[1] = 4;
        }
        return true;
    }

    @Override
    public boolean eglMakeCurrent(EGLDisplay display, EGLSurface draw, EGLSurface read, EGLContext context) {
        GLContextManager.makeCurrent();
        if (draw instanceof SurfaceImpl) {
            SurfaceImpl surf = (SurfaceImpl) draw;
            if (surf.fbo != null) {
                surf.fbo.bind();
            }
        }
        return true;
    }

    @Override
    public boolean eglQueryContext(EGLDisplay display, EGLContext context, int attribute, int[] value) {
        return true;
    }

    @Override
    public String eglQueryString(EGLDisplay display, int name) {
        return "Neutron-EGL 1.4";
    }

    @Override
    public boolean eglQuerySurface(EGLDisplay display, EGLSurface surface, int attribute, int[] value) {
        return true;
    }

    @Override
    public boolean eglSwapBuffers(EGLDisplay display, EGLSurface surface) {
        GLContextManager.makeCurrent();
        if (surface instanceof SurfaceImpl) {
            SurfaceImpl surf = (SurfaceImpl) surface;
            if (surf.fbo != null && surf.pixelBuffer != null) {
                surf.fbo.readPixels(surf.pixelBuffer);
                if (surf.target instanceof javax.microedition.lcdui.Graphics) {
                    javax.microedition.lcdui.Graphics g = (javax.microedition.lcdui.Graphics) surf.target;
                    g.drawRGB(surf.pixelBuffer, 0, surf.width, 0, 0, surf.width, surf.height, true);
                }
            }
        }
        return true;
    }

    @Override
    public boolean eglTerminate(EGLDisplay display) {
        return true;
    }

    @Override
    public boolean eglWaitGL() {
        return true;
    }

    @Override
    public boolean eglWaitNative(int engine, Object bindTarget) {
        return true;
    }
}
