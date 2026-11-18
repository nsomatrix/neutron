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
import org.neutron.graphics3d.opengl.MathUtil;
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

    private Camera activeCamera;
    private final Transform activeCameraTrans = new Transform();
    private final Transform activeCameraTransInv = new Transform();
    private final float[] projMatrix = new float[16];
    private final float[] viewMatrix = new float[16];
    private final float[] vpMatrix = new float[16];
    private final float[] mvpMatrix = new float[16];
    private final FloatBuffer mvpBuffer = BufferUtils.createFloatBuffer(16);
    private final FloatBuffer texMatBuffer = BufferUtils.createFloatBuffer(16);

    private FloatBuffer vtxBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer texCoordBuffer;
    private FloatBuffer colorBuffer;
    private IntBuffer indexBuffer;

    private int[] cachedPixelBuffer;

    private int[] getPixelBuffer(int size) {
        if (cachedPixelBuffer == null || cachedPixelBuffer.length < size) {
            cachedPixelBuffer = new int[Math.max(size, 1024 * 1024)];
        }
        return cachedPixelBuffer;
    }

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
            texShader = new GLShader("/shaders/m3g/m3g_tex.vsh", "/shaders/m3g/m3g_tex.fsh");
            colorShader = new GLShader("/shaders/m3g/m3g_color.vsh", "/shaders/m3g/m3g_color.fsh");

            vtxBuffer = BufferUtils.createFloatBuffer(65536 * 3);
            normalBuffer = BufferUtils.createFloatBuffer(65536 * 3);
            texCoordBuffer = BufferUtils.createFloatBuffer(65536 * 2);
            colorBuffer = BufferUtils.createFloatBuffer(65536 * 4);
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
        int[] srcPixels = getPixelBuffer(width * height);
        framebuffer.readPixels(srcPixels);

        if (currentTarget instanceof DirectFrameSurface) {
            DirectFrameSurface surface = (DirectFrameSurface) currentTarget;
            int[] dstPixels = surface.getImageData();
            if (dstPixels != null) {
                int total = Math.min(width * height, dstPixels.length);
                for (int i = 0; i < total; i++) {
                    int src = srcPixels[i];
                    int a = (src >>> 24);
                    if (a == 255) {
                        dstPixels[i] = src;
                    } else if (a > 0) {
                        int dst = dstPixels[i];
                        int sr = (src >> 16) & 0xff;
                        int sg = (src >> 8) & 0xff;
                        int sb = src & 0xff;
                        int dr = (dst >> 16) & 0xff;
                        int dg = (dst >> 8) & 0xff;
                        int db = dst & 0xff;
                        int invA = 255 - a;
                        int r = ((sr * a) + (dr * invA)) / 255;
                        int g = ((sg * a) + (dg * invA)) / 255;
                        int b = ((sb * a) + (db * invA)) / 255;
                        dstPixels[i] = 0xff000000 | (r << 16) | (g << 8) | b;
                    }
                }
            }
        } else if (currentTarget instanceof Image2D) {
            Image2D img2d = (Image2D) currentTarget;
            int[] data = img2d.getImageData();
            if (data != null) {
                System.arraycopy(srcPixels, 0, data, 0, Math.min(srcPixels.length, data.length));
            }
        } else if (currentTarget instanceof javax.microedition.lcdui.Graphics) {
            ((javax.microedition.lcdui.Graphics) currentTarget).drawRGB(srcPixels, 0, width, 0, 0, width, height, false);
        }
    }

    @Override
    public void clear(int clearColor, float clearDepth) {
        if (!initialized) return;

        GLContextManager.makeCurrent();
        framebuffer.bind();

        float a = ((clearColor >> 24) & 0xff) / 255.0f;
        float r = ((clearColor >> 16) & 0xff) / 255.0f;
        float g = ((clearColor >> 8) & 0xff) / 255.0f;
        float b = (clearColor & 0xff) / 255.0f;

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
    public void setCamera(Object cameraObj, Object transformObj) {
        if (cameraObj instanceof Camera) {
            this.activeCamera = (Camera) cameraObj;
            Transform camProj = new Transform();
            this.activeCamera.getProjection(camProj);
            camProj.get(this.projMatrix);

            if (transformObj instanceof Transform) {
                this.activeCameraTrans.set((Transform) transformObj);
                this.activeCameraTransInv.set((Transform) transformObj);
            } else {
                this.activeCameraTrans.setIdentity();
                this.activeCameraTransInv.setIdentity();
            }
            this.activeCameraTransInv.invert();
            this.activeCameraTransInv.get(this.viewMatrix);

            // Compute VP = Projection * View
            MathUtil.multiplyMM(this.vpMatrix, this.projMatrix, this.viewMatrix);
        }
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
        float alphaThreshold = 0.0f;
        if (cmode != null) {
            alphaThreshold = cmode.getAlphaThreshold();
            int blending = cmode.getBlending();
            if (blending != CompositingMode.REPLACE) {
                GL11.glEnable(GL11.GL_BLEND);
                if (blending == CompositingMode.ALPHA_ADD) {
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
                } else {
                    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                }
            } else {
                GL11.glDisable(GL11.GL_BLEND);
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
        Transform modelTr = new Transform();
        modelTr.postTranslate(scaleBias[1], scaleBias[2], scaleBias[3]);
        modelTr.postScale(scaleBias[0], scaleBias[0], scaleBias[0]);
        modelTr.transform(posArray, rawPos, true);

        vtxBuffer.clear();
        for (int i = 0; i < numVerts; i++) {
            vtxBuffer.put(rawPos[i * 4]).put(rawPos[i * 4 + 1]).put(rawPos[i * 4 + 2]);
        }
        vtxBuffer.flip();

        // Model Transform Matrix
        float[] modelMat = new float[16];
        if (transform != null) {
            transform.get(modelMat);
        } else {
            Transform id = new Transform();
            id.setIdentity();
            id.get(modelMat);
        }

        // MVP = VP * Model
        MathUtil.multiplyMM(this.mvpMatrix, this.vpMatrix, modelMat);
        mvpBuffer.clear();
        mvpBuffer.put(this.mvpMatrix).flip();

        // Texture setup
        Texture2D tex = appearance.getTexture(0);
        int texId = 0;
        float texW = 1.0f, texH = 1.0f;
        float[] texMat = new float[16];
        Transform idTex = new Transform();
        idTex.setIdentity();
        idTex.get(texMat);

        if (tex != null && tex.getImage() != null) {
            Image2D img = tex.getImage();
            texId = getOrCreateTexture(img, tex.getWrappingS(), tex.getWrappingT());
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

        // Diffuse / Ambient Colors
        float diffR = 1.0f, diffG = 1.0f, diffB = 1.0f, diffA = (alphaFactor / 256.0f);
        Material mat = appearance.getMaterial();
        if (mat != null) {
            int diffCol = mat.getColor(Material.DIFFUSE);
            diffA *= ((diffCol >> 24) & 0xff) / 255.0f;
            diffR = ((diffCol >> 16) & 0xff) / 255.0f;
            diffG = ((diffCol >> 8) & 0xff) / 255.0f;
            diffB = (diffCol & 0xff) / 255.0f;
        }

        // Colors buffer (default white)
        colorBuffer.clear();
        for (int i = 0; i < numVerts; i++) {
            colorBuffer.put(1.0f).put(1.0f).put(1.0f).put(1.0f);
        }
        colorBuffer.flip();

        // Indices
        int indexCount = triangles.getIndexCount();
        int[] indices = new int[indexCount];
        triangles.getIndices(indices);

        indexBuffer.clear();
        indexBuffer.put(indices).flip();

        GLShader shader = (texId != 0) ? texShader : colorShader;
        shader.use();

        int uMatrix = shader.getUniformLocation("uMatrix");
        if (uMatrix >= 0) {
            GL20.glUniformMatrix4fv(uMatrix, false, mvpBuffer);
        }

        int uDiffuseColor = shader.getUniformLocation("uDiffuseColor");
        if (uDiffuseColor >= 0) {
            GL20.glUniform4f(uDiffuseColor, diffR, diffG, diffB, diffA);
        }

        int uAlphaThresh = shader.getUniformLocation("uAlphaThreshold");
        if (uAlphaThresh >= 0) {
            GL20.glUniform1f(uAlphaThresh, alphaThreshold);
        }

        if (texId != 0) {
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);
            int uTex = shader.getUniformLocation("uTexture");
            if (uTex >= 0) GL20.glUniform1i(uTex, 0);

            int uTexSize = shader.getUniformLocation("uTexSize");
            if (uTexSize >= 0) GL20.glUniform2f(uTexSize, texW, texH);

            int uTexMatrix = shader.getUniformLocation("uTexMatrix");
            if (uTexMatrix >= 0) {
                texMatBuffer.clear();
                texMatBuffer.put(texMat).flip();
                GL20.glUniformMatrix4fv(uTexMatrix, false, texMatBuffer);
            }
        }

        int aPos = shader.getAttribLocation("aPosition");
        int aTex = shader.getAttribLocation("aTexCoord");
        int aColor = shader.getAttribLocation("aColor");

        if (aPos >= 0) {
            GL20.glEnableVertexAttribArray(aPos);
            GL20.glVertexAttribPointer(aPos, 3, GL11.GL_FLOAT, false, 0, vtxBuffer);
        }
        if (aTex >= 0 && texId != 0) {
            GL20.glEnableVertexAttribArray(aTex);
            GL20.glVertexAttribPointer(aTex, 2, GL11.GL_FLOAT, false, 0, texCoordBuffer);
        }
        if (aColor >= 0) {
            GL20.glEnableVertexAttribArray(aColor);
            GL20.glVertexAttribPointer(aColor, 4, GL11.GL_FLOAT, false, 0, colorBuffer);
        }

        GL11.glDrawElements(GL11.GL_TRIANGLES, indexBuffer);

        if (aPos >= 0) GL20.glDisableVertexAttribArray(aPos);
        if (aTex >= 0 && texId != 0) GL20.glDisableVertexAttribArray(aTex);
        if (aColor >= 0) GL20.glDisableVertexAttribArray(aColor);
    }

    @Override
    public void renderWorld(Object worldObj) {
        if (!init() || worldObj == null) return;
        // World scene graph render
    }

    private int getOrCreateTexture(Image2D image, int wrapS, int wrapT) {
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

        int glWrapS = (wrapS == Texture2D.WRAP_CLAMP) ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT;
        int glWrapT = (wrapT == Texture2D.WRAP_CLAMP) ? GL12.GL_CLAMP_TO_EDGE : GL11.GL_REPEAT;
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, glWrapS);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, glWrapT);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0,
                GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);

        textureCache.put(image, texId);
        return texId;
    }
}
