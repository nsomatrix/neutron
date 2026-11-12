package org.neutron.graphics3d.opengl;

import org.lwjgl.opengl.GL11;
import org.neutron.graphics3d.Graphics3DConfig;
import org.neutron.graphics3d.Graphics3DProvider;
import org.neutron.graphics3d.RenderBackendType;
import org.neutron.log.Logger;

public class OpenGLRenderBackend {
    private static OpenGLRenderBackend instance;
    private final GLFramebuffer framebuffer;
    private GLShader texShader;
    private GLShader colorShader;
    private GLShader simpleShader;
    private GLShader spriteShader;
    private boolean initialized = false;

    public static synchronized OpenGLRenderBackend getInstance() {
        if (instance == null) {
            instance = new OpenGLRenderBackend();
        }
        return instance;
    }

    private OpenGLRenderBackend() {
        this.framebuffer = new GLFramebuffer();
    }

    public synchronized boolean init() {
        if (initialized) return true;
        if (!GLContextManager.isAvailable()) {
            return false;
        }

        try {
            GLContextManager.makeCurrent();
            texShader = new GLShader("/shaders/tex.vsh", "/shaders/tex.fsh");
            colorShader = new GLShader("/shaders/color.vsh", "/shaders/color.fsh");
            simpleShader = new GLShader("/shaders/simple.vsh", "/shaders/simple.fsh");
            spriteShader = new GLShader("/shaders/sprite.vsh", "/shaders/sprite.fsh");

            initialized = true;
            Logger.info("OpenGL 3D Shaders successfully compiled & linked.");
            return true;
        } catch (Throwable t) {
            Logger.error("Failed to initialize OpenGL 3D shaders, falling back to software rasterizer.", t);
            return false;
        }
    }

    public void beginFrame(int width, int height, int clearColor) {
        if (!init()) return;
        GLContextManager.makeCurrent();
        framebuffer.resize(width, height);
        framebuffer.bind();

        float r = ((clearColor >> 16) & 0xFF) / 255.0f;
        float g = ((clearColor >> 8) & 0xFF) / 255.0f;
        float b = (clearColor & 0xFF) / 255.0f;

        GL11.glClearColor(r, g, b, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }

    public void endFrame(int[] outputPixels) {
        if (!initialized) return;
        framebuffer.readPixels(outputPixels);
        framebuffer.unbind();
    }

    public GLShader getTexShader() {
        return texShader;
    }

    public GLShader getColorShader() {
        return colorShader;
    }

    public GLShader getSimpleShader() {
        return simpleShader;
    }

    public GLShader getSpriteShader() {
        return spriteShader;
    }

    public static void registerIfAvailable() {
        if (Graphics3DConfig.getBackendType() == RenderBackendType.OPENGL && GLContextManager.isAvailable()) {
            OpenGLRenderBackend backend = getInstance();
            if (backend.init()) {
                Graphics3DProvider.registerMicro3DBackend(backend);
                Graphics3DProvider.registerM3GBackend(backend);
                Logger.info("OpenGL 3D Hardware Backend registered as active provider.");
            }
        }
    }
}

