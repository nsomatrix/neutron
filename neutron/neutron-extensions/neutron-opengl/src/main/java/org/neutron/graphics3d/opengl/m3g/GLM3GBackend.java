/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under the Apache License, Version 2.0 or LGPL 2.1
 */
package org.neutron.graphics3d.opengl.m3g;

import javax.microedition.m3g.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.neutron.graphics3d.Graphics3DConfig;
import org.neutron.graphics3d.opengl.GLContextManager;
import org.neutron.graphics3d.opengl.GLFramebuffer;
import org.neutron.graphics3d.opengl.GLShader;
import org.neutron.graphics3d.spi.DirectFrameSurface;
import org.neutron.graphics3d.spi.M3GBackend;
import org.neutron.log.Logger;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Modern desktop OpenGL hardware-accelerated backend for JSR-184 (M3G).
 */
public class GLM3GBackend implements M3GBackend {

    private static GLM3GBackend instance;

    private final GLFramebuffer framebuffer;
    private final Map<Image2D, Integer> textureCache = new WeakHashMap<>();

    private GLShader texShader;
    private GLShader colorShader;

    private boolean initialized = false;

    private Object currentTarget;
    private int width;
    private int height;

    private FloatBuffer vtxBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer texCoordBuffer;
    private FloatBuffer colorBuffer;
    private IntBuffer indexBuffer;

    public static synchronized GLM3GBackend getInstance() {
        if (instance == null) {
            instance = new GLM3GBackend();
        }
        return instance;
    }

    private GLM3GBackend() {
        this.framebuffer = new GLFramebuffer();
    }

    @Override
    public boolean isAvailable() {
        return GLContextManager.isAvailable();
    }

    @Override
    public synchronized boolean init() {
        if (initialized) return true;
        if (!isAvailable()) return false;

        try {
            GLContextManager.makeCurrent();
            texShader = new GLShader("/shaders/tex.vsh", "/shaders/tex.fsh");
            colorShader = new GLShader("/shaders/color.vsh", "/shaders/color.fsh");

            vtxBuffer = BufferUtils.createFloatBuffer(65536 * 3);
            normalBuffer = BufferUtils.createFloatBuffer(65536 * 3);
            texCoordBuffer = BufferUtils.createFloatBuffer(65536 * 2);
            colorBuffer = BufferUtils.createFloatBuffer(65536 * 3);
            indexBuffer = BufferUtils.createIntBuffer(65536);

            initialized = true;
            Logger.info("GLM3GBackend initialized successfully with hardware GLSL shaders.");
            return true;
        } catch (Throwable t) {
            Logger.error("Failed to initialize GLM3GBackend: " + t.getMessage(), t);
            return false;
        }
    }

    @Override
    public void bindTarget(Object target, int width, int height, boolean depthBuffer, int hints) {
        if (!init()) return;

        GLContextManager.makeCurrent();
        this.currentTarget = target;
        this.width = width > 0 ? width : 240;
        this.height = height > 0 ? height : 320;

        framebuffer.resize(this.width, this.height);
        framebuffer.bind();

        GL11.glViewport(0, 0, framebuffer.getRenderWidth(), framebuffer.getRenderHeight());
        if (depthBuffer) {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glDepthFunc(GL11.GL_LEQUAL);
        } else {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        }
    }

    @Override
    public void releaseTarget() {
        if (!initialized || currentTarget == null) return;

        GLContextManager.makeCurrent();
        if (currentTarget instanceof DirectFrameSurface) {
            DirectFrameSurface surface = (DirectFrameSurface) currentTarget;
            framebuffer.readPixels(surface.getImageData());
        } else if (currentTarget instanceof Image2D) {
            Image2D img2d = (Image2D) currentTarget;
            int[] data = img2d.getImageData();
            if (data != null) {
                framebuffer.readPixels(data);
            }
        } else if (currentTarget instanceof javax.microedition.lcdui.Image) {
            javax.microedition.lcdui.Graphics g = ((javax.microedition.lcdui.Image) currentTarget).getGraphics();
            if (g instanceof DirectFrameSurface) {
                framebuffer.readPixels(((DirectFrameSurface) g).getImageData());
            } else {
                int[] pixels = new int[width * height];
                framebuffer.readPixels(pixels);
                g.drawRGB(pixels, 0, width, 0, 0, width, height, false);
            }
        } else if (currentTarget instanceof javax.microedition.lcdui.Graphics) {
            int[] pixels = new int[width * height];
            framebuffer.readPixels(pixels);
            ((javax.microedition.lcdui.Graphics) currentTarget).drawRGB(pixels, 0, width, 0, 0, width, height, false);
        }

        framebuffer.unbind();
        currentTarget = null;
    }

    @Override
    public void clear(int clearColor, float clearDepth) {
        if (!initialized) return;

        GLContextManager.makeCurrent();
        framebuffer.bind();

        float r = ((clearColor >> 16) & 0xFF) / 255.0f;
        float g = ((clearColor >> 8) & 0xFF) / 255.0f;
        float b = (clearColor & 0xFF) / 255.0f;
        float a = ((clearColor >> 24) & 0xFF) / 255.0f;

        GL11.glClearColor(r, g, b, a);
        GL11.glClearDepth(clearDepth);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void setViewport(int x, int y, int width, int height) {
        if (!initialized) return;
        int s = Math.max(1, Graphics3DConfig.getInternalScale());
        GL11.glViewport(x * s, y * s, width * s, height * s);
    }

    @Override
    public void setDepthRange(float near, float far) {
        if (!initialized) return;
        GL11.glDepthRange(near, far);
    }

    @Override
    public void renderSubmesh(Object verticesObj, Object trianglesObj, Object appearanceObj,
                              Object transformObj, int scope, int alphaFactor) {
        if (!init() || verticesObj == null || trianglesObj == null || appearanceObj == null) return;

        VertexBuffer vertices = (VertexBuffer) verticesObj;
        IndexBuffer triangles = (IndexBuffer) trianglesObj;
        Appearance appearance = (Appearance) appearanceObj;
        Transform transform = (Transform) transformObj;

        GLContextManager.makeCurrent();
        framebuffer.bind();

        // PolygonMode
        PolygonMode pmode = appearance.getPolygonMode();
        if (pmode != null) {
            int culling = pmode.getCulling();
            if (culling == PolygonMode.CULL_NONE) {
                GL11.glDisable(GL11.GL_CULL_FACE);
            } else {
                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glCullFace(culling == PolygonMode.CULL_FRONT ? GL11.GL_FRONT : GL11.GL_BACK);
            }
        } else {
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glCullFace(GL11.GL_BACK);
        }

        // CompositingMode
        CompositingMode cmode = appearance.getCompositingMode();
        if (cmode != null && cmode.getBlending() != CompositingMode.REPLACE) {
            GL11.glEnable(GL11.GL_BLEND);
            if (cmode.getBlending() == CompositingMode.ALPHA_ADD) {
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
            } else {
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            }
        } else {
            GL11.glDisable(GL11.GL_BLEND);
        }

        // Vertex Positions
        float[] scaleBias = new float[4];
        VertexArray posArray = vertices.getPositions(scaleBias);
        if (posArray == null) return;

        int numVerts = posArray.getVertexCount();
        float[] rawPos = new float[numVerts * 4];
        Transform tr = new Transform();
        if (transform != null) {
            tr.set(transform);
        } else {
            tr.setIdentity();
        }
        tr.postTranslate(scaleBias[1], scaleBias[2], scaleBias[3]);
        tr.postScale(scaleBias[0], scaleBias[0], scaleBias[0]);
        tr.transform(posArray, rawPos, true);

        vtxBuffer.clear();
        for (int i = 0; i < numVerts; i++) {
            vtxBuffer.put(rawPos[i * 4]).put(rawPos[i * 4 + 1]).put(rawPos[i * 4 + 2]);
        }
        vtxBuffer.flip();

        // Texture
        Texture2D tex = appearance.getTexture(0);
        int texId = 0;
        float texW = 256.0f, texH = 256.0f;
        if (tex != null && tex.getImage() != null) {
            Image2D img = tex.getImage();
            texId = getOrCreateTexture(img);
            texW = img.getWidth();
            texH = img.getHeight();

            float[] texScaleBias = new float[4];
            VertexArray texCoords = vertices.getTexCoords(0, texScaleBias);
            if (texCoords != null) {
                float[] rawTex = new float[numVerts * 4];
                Transform texTr = new Transform();
                tex.getCompositeTransform(texTr);
                texTr.postTranslate(texScaleBias[1], texScaleBias[2], texScaleBias[3]);
                texTr.postScale(texScaleBias[0], texScaleBias[0], texScaleBias[0]);
                texTr.transform(texCoords, rawTex, true);

                texCoordBuffer.clear();
                for (int i = 0; i < numVerts; i++) {
                    texCoordBuffer.put(rawTex[i * 4]).put(rawTex[i * 4 + 1]);
                }
                texCoordBuffer.flip();
            }
        }

        // Indices
        int indexCount = triangles.getIndexCount();
        int[] indices = new int[indexCount];
        triangles.getIndices(indices);

        indexBuffer.clear();
        indexBuffer.put(indices).flip();

        GLShader shader = (texId != 0) ? texShader : colorShader;
        shader.use();

        if (texId != 0) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
            int uTexSize = shader.getUniformLocation("uTexSize");
            if (uTexSize >= 0) GL20.glUniform2f(uTexSize, texW, texH);
        }

        int aPos = shader.getAttribLocation("aPosition");
        int aTex = shader.getAttribLocation("aColorData");

        if (aPos >= 0) {
            GL20.glEnableVertexAttribArray(aPos);
            GL20.glVertexAttribPointer(aPos, 3, GL11.GL_FLOAT, false, 0, vtxBuffer);
        }
        if (aTex >= 0 && texId != 0) {
            GL20.glEnableVertexAttribArray(aTex);
            GL20.glVertexAttribPointer(aTex, 2, GL11.GL_FLOAT, false, 0, texCoordBuffer);
        }

        GL11.glDrawElements(GL11.GL_TRIANGLES, indexBuffer);

        if (aPos >= 0) GL20.glDisableVertexAttribArray(aPos);
        if (aTex >= 0 && texId != 0) GL20.glDisableVertexAttribArray(aTex);
    }

    @Override
    public void renderWorld(Object world) {
        // World rendering delegator
    }

    private int getOrCreateTexture(Image2D image) {
        if (image == null) return 0;

        Integer cached = textureCache.get(image);
        if (cached != null && cached != 0 && GL11.glIsTexture(cached)) {
            return cached;
        }

        int w = image.getWidth();
        int h = image.getHeight();
        int[] data = image.getImageData();
        if (data == null || w <= 0 || h <= 0) return 0;

        IntBuffer pixelBuffer = BufferUtils.createIntBuffer(w * h);
        pixelBuffer.put(data).flip();

        int texId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

        boolean bilinear = Graphics3DConfig.isBilinearFiltering();
        int filter = bilinear ? GL11.GL_LINEAR : GL11.GL_NEAREST;

        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, filter);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0,
                org.lwjgl.opengl.GL12.GL_BGRA, org.lwjgl.opengl.GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);

        textureCache.put(image, texId);
        return texId;
    }
}
