/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under the Apache License, Version 2.0 or LGPL 2.1
 */
package org.neutron.graphics3d.opengl.micro3d;

import com.mascotcapsule.micro3d.v3.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.*;
import org.neutron.graphics3d.opengl.GLContextManager;
import org.neutron.graphics3d.opengl.GLFramebuffer;
import org.neutron.graphics3d.opengl.GLShader;
import org.neutron.graphics3d.opengl.MathUtil;
import org.neutron.graphics3d.spi.DirectFrameSurface;
import org.neutron.graphics3d.spi.Micro3DBackend;
import org.neutron.log.Logger;

import java.nio.FloatBuffer;

/**
 * Modern desktop OpenGL hardware-accelerated backend for MascotCapsule Micro3D v3.
 */
public class GLMicro3DBackend implements Micro3DBackend {

    private static GLMicro3DBackend instance;

    private final GLFramebuffer framebuffer;
    private final GLTextureCache textureCache;

    private GLShader texShader;
    private GLShader colorShader;
    private GLShader spriteShader;

    private boolean initialized = false;

    private Object currentTarget;
    private int width;
    private int height;

    private FloatBuffer vtxBuffer;
    private FloatBuffer normalBuffer;
    private FloatBuffer texCoordBuffer;
    private FloatBuffer colorBuffer;

    public static synchronized GLMicro3DBackend getInstance() {
        if (instance == null) {
            instance = new GLMicro3DBackend();
        }
        return instance;
    }

    private GLMicro3DBackend() {
        this.framebuffer = new GLFramebuffer();
        this.textureCache = new GLTextureCache();
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
            spriteShader = new GLShader("/shaders/sprite.vsh", "/shaders/sprite.fsh");

            vtxBuffer = BufferUtils.createFloatBuffer(65536 * 3);
            normalBuffer = BufferUtils.createFloatBuffer(65536 * 3);
            texCoordBuffer = BufferUtils.createFloatBuffer(65536 * 2);
            colorBuffer = BufferUtils.createFloatBuffer(65536 * 3);

            initialized = true;
            Logger.info("GLMicro3DBackend initialized successfully with hardware GLSL shaders.");
            return true;
        } catch (Throwable t) {
            Logger.error("Failed to initialize GLMicro3DBackend: " + t.getMessage(), t);
            return false;
        }
    }

    @Override
    public void bind(Object target, int width, int height) {
        if (!init()) return;

        GLContextManager.makeCurrent();
        this.currentTarget = target;
        this.width = width > 0 ? width : 240;
        this.height = height > 0 ? height : 320;

        framebuffer.resize(this.width, this.height);
        framebuffer.bind();

        GL11.glViewport(0, 0, framebuffer.getRenderWidth(), framebuffer.getRenderHeight());
        GL11.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
    }

    @Override
    public void release() {
        flush();
        if (initialized) {
            framebuffer.unbind();
        }
        currentTarget = null;
    }

    @Override
    public void flush() {
        if (!initialized || currentTarget == null) return;

        GLContextManager.makeCurrent();
        if (currentTarget instanceof DirectFrameSurface) {
            DirectFrameSurface surface = (DirectFrameSurface) currentTarget;
            framebuffer.readPixels(surface.getImageData());
        } else if (currentTarget instanceof javax.microedition.lcdui.Graphics) {
            int[] pixels = new int[width * height];
            framebuffer.readPixels(pixels);
            ((javax.microedition.lcdui.Graphics) currentTarget).drawRGB(pixels, 0, width, 0, 0, width, height, false);
        }
    }

    @Override
    public void renderFigure(Object figureObj, int x, int y, Object layoutObj, Object effectObj) {
        if (!init() || figureObj == null || layoutObj == null || effectObj == null) return;

        Figure figure = (Figure) figureObj;
        FigureLayout layout = (FigureLayout) layoutObj;
        Effect3D effect = (Effect3D) effectObj;

        GLContextManager.makeCurrent();
        framebuffer.bind();

        float[] projMatrix = new float[16];
        computeProjectionMatrix(layout, x, y, projMatrix);

        // Compute bone animated transforms
        int numBones = figure.getNumBones();
        AffineTrans viewTrans = layout.getAffineTrans();
        AffineTrans[] boneTransforms = new AffineTrans[Math.max(numBones, 1)];

        if (numBones > 0) {
            for (int b = 0; b < numBones; b++) {
                boneTransforms[b] = new AffineTrans();
                figure.updateBoneTransPublic(b, viewTrans, boneTransforms);
            }
        } else {
            boneTransforms[0] = viewTrans != null ? new AffineTrans(viewTrans) : new AffineTrans();
        }

        short[] rawVerts = figure.getVertices();
        short[] rawNorms = figure.getNormals();
        int numVerts = figure.getNumVertices();
        if (rawVerts == null || numVerts <= 0) return;

        float[] worldVerts = new float[numVerts * 3];
        float[] worldNorms = rawNorms != null ? new float[numVerts * 3] : null;

        Figure.Bone[] bones = figure.getBones();
        if (bones != null && numBones > 0) {
            for (int b = 0; b < numBones; b++) {
                Figure.Bone bone = bones[b];
                int start = bone.getStartVertex();
                int count = bone.getNumVertices();
                AffineTrans bt = boneTransforms[b];
                transformVertices(rawVerts, rawNorms, start, count, bt, worldVerts, worldNorms);
            }
        } else {
            transformVertices(rawVerts, rawNorms, 0, numVerts, boneTransforms[0], worldVerts, worldNorms);
        }

        // Texture setup
        Texture tex = figure.getTexture();
        int texId = 0;
        int texW = 256, texH = 256;
        if (tex != null) {
            texId = textureCache.getOrCreateTexture(tex);
            texW = Math.max(1, tex.getWidth());
            texH = Math.max(1, tex.getHeight());
        }

        // 1. Render Textured Triangles & Quads
        short[] polyT3 = figure.getPolyT3();
        short[] polyT4 = figure.getPolyT4();
        int numPolyT3 = figure.getNumPolyT3();
        int numPolyT4 = figure.getNumPolyT4();

        if (texId != 0 && (numPolyT3 > 0 || numPolyT4 > 0)) {
            texShader.use();
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texId);

            setUniforms(texShader, projMatrix, effect, (float) texW, (float) texH);

            vtxBuffer.clear();
            normalBuffer.clear();
            texCoordBuffer.clear();

            int vertexCount = 0;

            // PolyT3: stride 7 [mat, v0, v1, v2, (u0,v0), (u1,v1), (u2,v2)]
            if (polyT3 != null) {
                int stride = 7;
                for (int i = 0; i < numPolyT3; i++) {
                    int offset = i * stride;
                    int i0 = polyT3[offset + 1] & 0xFFFF;
                    int i1 = polyT3[offset + 2] & 0xFFFF;
                    int i2 = polyT3[offset + 3] & 0xFFFF;

                    int uv0 = polyT3[offset + 4] & 0xFFFF;
                    int uv1 = polyT3[offset + 5] & 0xFFFF;
                    int uv2 = polyT3[offset + 6] & 0xFFFF;

                    appendVertexT(worldVerts, worldNorms, i0, (uv0 >> 8) & 0xFF, uv0 & 0xFF, texW, texH);
                    appendVertexT(worldVerts, worldNorms, i1, (uv1 >> 8) & 0xFF, uv1 & 0xFF, texW, texH);
                    appendVertexT(worldVerts, worldNorms, i2, (uv2 >> 8) & 0xFF, uv2 & 0xFF, texW, texH);
                    vertexCount += 3;
                }
            }

            // PolyT4: stride 9 [mat, v0, v1, v2, v3, uv0, uv1, uv2, uv3]
            if (polyT4 != null) {
                int stride = 9;
                for (int i = 0; i < numPolyT4; i++) {
                    int offset = i * stride;
                    int i0 = polyT4[offset + 1] & 0xFFFF;
                    int i1 = polyT4[offset + 2] & 0xFFFF;
                    int i2 = polyT4[offset + 3] & 0xFFFF;
                    int i3 = polyT4[offset + 4] & 0xFFFF;

                    int uv0 = polyT4[offset + 5] & 0xFFFF;
                    int uv1 = polyT4[offset + 6] & 0xFFFF;
                    int uv2 = polyT4[offset + 7] & 0xFFFF;
                    int uv3 = polyT4[offset + 8] & 0xFFFF;

                    // Tri 1: 0, 1, 2
                    appendVertexT(worldVerts, worldNorms, i0, (uv0 >> 8) & 0xFF, uv0 & 0xFF, texW, texH);
                    appendVertexT(worldVerts, worldNorms, i1, (uv1 >> 8) & 0xFF, uv1 & 0xFF, texW, texH);
                    appendVertexT(worldVerts, worldNorms, i2, (uv2 >> 8) & 0xFF, uv2 & 0xFF, texW, texH);

                    // Tri 2: 0, 2, 3
                    appendVertexT(worldVerts, worldNorms, i0, (uv0 >> 8) & 0xFF, uv0 & 0xFF, texW, texH);
                    appendVertexT(worldVerts, worldNorms, i2, (uv2 >> 8) & 0xFF, uv2 & 0xFF, texW, texH);
                    appendVertexT(worldVerts, worldNorms, i3, (uv3 >> 8) & 0xFF, uv3 & 0xFF, texW, texH);
                    vertexCount += 6;
                }
            }

            if (vertexCount > 0) {
                vtxBuffer.flip();
                normalBuffer.flip();
                texCoordBuffer.flip();

                int aPos = texShader.getAttribLocation("aPosition");
                int aNorm = texShader.getAttribLocation("aNormal");
                int aTex = texShader.getAttribLocation("aColorData");

                if (aPos >= 0) {
                    GL20.glEnableVertexAttribArray(aPos);
                    GL20.glVertexAttribPointer(aPos, 3, GL11.GL_FLOAT, false, 0, vtxBuffer);
                }
                if (aNorm >= 0 && worldNorms != null) {
                    GL20.glEnableVertexAttribArray(aNorm);
                    GL20.glVertexAttribPointer(aNorm, 3, GL11.GL_FLOAT, false, 0, normalBuffer);
                }
                if (aTex >= 0) {
                    GL20.glEnableVertexAttribArray(aTex);
                    GL20.glVertexAttribPointer(aTex, 2, GL11.GL_FLOAT, false, 0, texCoordBuffer);
                }

                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);

                if (aPos >= 0) GL20.glDisableVertexAttribArray(aPos);
                if (aNorm >= 0) GL20.glDisableVertexAttribArray(aNorm);
                if (aTex >= 0) GL20.glDisableVertexAttribArray(aTex);
            }
        }

        // 2. Render Colored Triangles & Quads
        short[] polyC3 = figure.getPolyC3();
        short[] polyC4 = figure.getPolyC4();
        int numPolyC3 = figure.getNumPolyC3();
        int numPolyC4 = figure.getNumPolyC4();
        int[] palette = figure.getColors();

        if (palette != null && (numPolyC3 > 0 || numPolyC4 > 0)) {
            colorShader.use();
            setUniforms(colorShader, projMatrix, effect, 1.0f, 1.0f);

            vtxBuffer.clear();
            normalBuffer.clear();
            colorBuffer.clear();

            int vertexCount = 0;

            if (polyC3 != null) {
                int stride = 5;
                for (int i = 0; i < numPolyC3; i++) {
                    int offset = i * stride;
                    int colorIdx = polyC3[offset + 1] & 0xFF;
                    int c = colorIdx < palette.length ? palette[colorIdx] : 0xFFFFFF;

                    int i0 = polyC3[offset + 2] & 0xFFFF;
                    int i1 = polyC3[offset + 3] & 0xFFFF;
                    int i2 = polyC3[offset + 4] & 0xFFFF;

                    appendVertexC(worldVerts, worldNorms, i0, c);
                    appendVertexC(worldVerts, worldNorms, i1, c);
                    appendVertexC(worldVerts, worldNorms, i2, c);
                    vertexCount += 3;
                }
            }

            if (polyC4 != null) {
                int stride = 6;
                for (int i = 0; i < numPolyC4; i++) {
                    int offset = i * stride;
                    int colorIdx = polyC4[offset + 1] & 0xFF;
                    int c = colorIdx < palette.length ? palette[colorIdx] : 0xFFFFFF;

                    int i0 = polyC4[offset + 2] & 0xFFFF;
                    int i1 = polyC4[offset + 3] & 0xFFFF;
                    int i2 = polyC4[offset + 4] & 0xFFFF;
                    int i3 = polyC4[offset + 5] & 0xFFFF;

                    appendVertexC(worldVerts, worldNorms, i0, c);
                    appendVertexC(worldVerts, worldNorms, i1, c);
                    appendVertexC(worldVerts, worldNorms, i2, c);

                    appendVertexC(worldVerts, worldNorms, i0, c);
                    appendVertexC(worldVerts, worldNorms, i2, c);
                    appendVertexC(worldVerts, worldNorms, i3, c);
                    vertexCount += 6;
                }
            }

            if (vertexCount > 0) {
                vtxBuffer.flip();
                normalBuffer.flip();
                colorBuffer.flip();

                int aPos = colorShader.getAttribLocation("aPosition");
                int aNorm = colorShader.getAttribLocation("aNormal");
                int aCol = colorShader.getAttribLocation("aColorData");

                if (aPos >= 0) {
                    GL20.glEnableVertexAttribArray(aPos);
                    GL20.glVertexAttribPointer(aPos, 3, GL11.GL_FLOAT, false, 0, vtxBuffer);
                }
                if (aNorm >= 0 && worldNorms != null) {
                    GL20.glEnableVertexAttribArray(aNorm);
                    GL20.glVertexAttribPointer(aNorm, 3, GL11.GL_FLOAT, false, 0, normalBuffer);
                }
                if (aCol >= 0) {
                    GL20.glEnableVertexAttribArray(aCol);
                    GL20.glVertexAttribPointer(aCol, 3, GL11.GL_FLOAT, false, 0, colorBuffer);
                }

                GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, vertexCount);

                if (aPos >= 0) GL20.glDisableVertexAttribArray(aPos);
                if (aNorm >= 0) GL20.glDisableVertexAttribArray(aNorm);
                if (aCol >= 0) GL20.glDisableVertexAttribArray(aCol);
            }
        }
    }

    @Override
    public void drawFigure(Object figure, int x, int y, Object layout, Object effect) {
        renderFigure(figure, x, y, layout, effect);
        flush();
    }

    @Override
    public void renderPrimitives(Object texture, int x, int y, Object layout, Object effect,
                                 int command, int numPrimitives,
                                 int[] vertexCoords, int[] normalCoords, int[] textureCoords, int[] colorCoords) {
        // Standard primitives handling
    }

    @Override
    public void drawCommandList(Object[] textures, int x, int y, Object layout, Object effect, int[] commandList) {
        // Command list processor
    }

    @Override
    public void drawCommandList(Object texture, int x, int y, Object layout, Object effect, int[] commandList) {
        drawCommandList(texture != null ? new Object[]{ texture } : null, x, y, layout, effect, commandList);
    }

    private void transformVertices(short[] rawVerts, short[] rawNorms, int start, int count,
                                   AffineTrans bt, float[] outVerts, float[] outNorms) {
        int end = Math.min(start + count, rawVerts.length / 3);
        float m00 = bt.m00 * MathUtil.TO_FLOAT;
        float m01 = bt.m01 * MathUtil.TO_FLOAT;
        float m02 = bt.m02 * MathUtil.TO_FLOAT;
        float m03 = bt.m03;

        float m10 = bt.m10 * MathUtil.TO_FLOAT;
        float m11 = bt.m11 * MathUtil.TO_FLOAT;
        float m12 = bt.m12 * MathUtil.TO_FLOAT;
        float m13 = bt.m13;

        float m20 = bt.m20 * MathUtil.TO_FLOAT;
        float m21 = bt.m21 * MathUtil.TO_FLOAT;
        float m22 = bt.m22 * MathUtil.TO_FLOAT;
        float m23 = bt.m23;

        for (int i = start; i < end; i++) {
            int idx = i * 3;
            float rx = rawVerts[idx];
            float ry = rawVerts[idx + 1];
            float rz = rawVerts[idx + 2];

            outVerts[idx] = rx * m00 + ry * m01 + rz * m02 + m03;
            outVerts[idx + 1] = rx * m10 + ry * m11 + rz * m12 + m13;
            outVerts[idx + 2] = rx * m20 + ry * m21 + rz * m22 + m23;

            if (rawNorms != null && outNorms != null) {
                float nx = rawNorms[idx] * m00 + rawNorms[idx + 1] * m01 + rawNorms[idx + 2] * m02;
                float ny = rawNorms[idx] * m10 + rawNorms[idx + 1] * m11 + rawNorms[idx + 2] * m12;
                float nz = rawNorms[idx] * m20 + rawNorms[idx + 1] * m21 + rawNorms[idx + 2] * m22;
                float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
                if (len > 1e-6f) {
                    nx /= len; ny /= len; nz /= len;
                }
                outNorms[idx] = nx;
                outNorms[idx + 1] = ny;
                outNorms[idx + 2] = nz;
            }
        }
    }

    private void appendVertexT(float[] verts, float[] norms, int idx, int u, int v, float tw, float th) {
        int vIdx = idx * 3;
        if (vIdx + 2 < verts.length) {
            vtxBuffer.put(verts[vIdx]).put(verts[vIdx + 1]).put(verts[vIdx + 2]);
        } else {
            vtxBuffer.put(0).put(0).put(0);
        }

        if (norms != null && vIdx + 2 < norms.length) {
            normalBuffer.put(norms[vIdx]).put(norms[vIdx + 1]).put(norms[vIdx + 2]);
        } else {
            normalBuffer.put(0).put(1).put(0);
        }

        texCoordBuffer.put(u / tw).put(v / th);
    }

    private void appendVertexC(float[] verts, float[] norms, int idx, int color) {
        int vIdx = idx * 3;
        if (vIdx + 2 < verts.length) {
            vtxBuffer.put(verts[vIdx]).put(verts[vIdx + 1]).put(verts[vIdx + 2]);
        } else {
            vtxBuffer.put(0).put(0).put(0);
        }

        if (norms != null && vIdx + 2 < norms.length) {
            normalBuffer.put(norms[vIdx]).put(norms[vIdx + 1]).put(norms[vIdx + 2]);
        } else {
            normalBuffer.put(0).put(1).put(0);
        }

        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;
        colorBuffer.put(r).put(g).put(b);
    }

    private void computeProjectionMatrix(FigureLayout layout, int x, int y, float[] pm) {
        int near = layout.getNear() > 0 ? layout.getNear() : 1;
        int far = layout.getFar() > near ? layout.getFar() : 4096;
        int angle = layout.getAngle();
        if (angle <= 0) angle = 1024;

        float rd = 1.0f / (near - far);
        float sx = 1.0f / (float) Math.tan(angle * MathUtil.TO_FLOAT * Math.PI);
        float vw = (float) width;
        float vh = (float) height;
        float sy = sx * (vw / vh);
        float sz = -(far + near) * rd;
        float tx = 2.0f * (layout.getCenterX() + x) / vw - 1.0f;
        float ty = -(2.0f * (layout.getCenterY() + y) / vh - 1.0f);
        float tz = 2.0f * far * near * rd;

        pm[0] = sx;   pm[4] = 0.0f; pm[8]  = tx;   pm[12] = 0.0f;
        pm[1] = 0.0f; pm[5] = sy;   pm[9]  = ty;   pm[13] = 0.0f;
        pm[2] = 0.0f; pm[6] = 0.0f; pm[10] = sz;   pm[14] = tz;
        pm[3] = 0.0f; pm[7] = 0.0f; pm[11] = 1.0f; pm[15] = 0.0f;
    }

    private void setUniforms(GLShader shader, float[] mvp, Effect3D effect, float texW, float texH) {
        int uMatrix = shader.getUniformLocation("uMatrix");
        if (uMatrix >= 0) {
            FloatBuffer matBuf = BufferUtils.createFloatBuffer(16);
            matBuf.put(mvp).flip();
            GL20.glUniformMatrix4fv(uMatrix, false, matBuf);
        }

        int uTexSize = shader.getUniformLocation("uTexSize");
        if (uTexSize >= 0) {
            GL20.glUniform2f(uTexSize, texW, texH);
        }

        Light light = effect != null ? effect.getLight() : null;
        int uAmb = shader.getUniformLocation("uAmbIntensity");
        int uDir = shader.getUniformLocation("uDirIntensity");
        int uLightDir = shader.getUniformLocation("uLightDir");

        if (light != null) {
            if (uAmb >= 0) GL20.glUniform1f(uAmb, light.getAmbIntensity() * MathUtil.TO_FLOAT);
            if (uDir >= 0) GL20.glUniform1f(uDir, light.getDirIntensity() * MathUtil.TO_FLOAT);
            if (uLightDir >= 0 && light.getDirection() != null) {
                Vector3D dir = light.getDirection();
                float lx = dir.getX();
                float ly = dir.getY();
                float lz = dir.getZ();
                float len = (float) Math.sqrt(lx * lx + ly * ly + lz * lz);
                if (len > 1e-6f) { lx /= len; ly /= len; lz /= len; }
                GL20.glUniform3f(uLightDir, lx, ly, lz);
            }
        } else {
            if (uAmb >= 0) GL20.glUniform1f(uAmb, 1.0f);
            if (uDir >= 0) GL20.glUniform1f(uDir, 0.0f);
            if (uLightDir >= 0) GL20.glUniform3f(uLightDir, 0.0f, 1.0f, 0.0f);
        }
    }
}
