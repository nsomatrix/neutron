package org.neutron.graphics3d.opengl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL30;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class GLFramebuffer {
    private int fboId = 0;
    private int colorTexId = 0;
    private int depthRboId = 0;
    private int width = 0;
    private int height = 0;
    private IntBuffer pixelBuffer;

    public void resize(int w, int h) {
        if (w <= 0 || h <= 0) return;
        if (width == w && height == h && fboId != 0) return;

        destroy();
        this.width = w;
        this.height = h;

        fboId = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);

        // Color attachment texture
        colorTexId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexId);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexId, 0);

        // Depth attachment renderbuffer (24-bit depth for jitter-free Z-buffering)
        depthRboId = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthRboId);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT24, w, h);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthRboId);

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        pixelBuffer = BufferUtils.createIntBuffer(w * h);
    }

    public void bind() {
        if (fboId != 0) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
            GL11.glViewport(0, 0, width, height);
        }
    }

    public void unbind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void readPixels(int[] dest) {
        if (fboId == 0 || dest == null || dest.length < width * height) return;

        bind();
        pixelBuffer.clear();
        // Read directly as BGRA which maps 1:1 to Java ARGB ints
        GL11.glReadPixels(0, 0, width, height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        pixelBuffer.get(dest, 0, width * height);

        // Flip vertically because OpenGL bottom-left vs AWT top-left
        int halfH = height / 2;
        int rowSize = width;
        int[] tempRow = new int[rowSize];
        for (int y = 0; y < halfH; y++) {
            int topIdx = y * rowSize;
            int botIdx = (height - 1 - y) * rowSize;
            System.arraycopy(dest, topIdx, tempRow, 0, rowSize);
            System.arraycopy(dest, botIdx, dest, topIdx, rowSize);
            System.arraycopy(tempRow, 0, dest, botIdx, rowSize);
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void destroy() {
        if (fboId != 0) {
            GL30.glDeleteFramebuffers(fboId);
            fboId = 0;
        }
        if (colorTexId != 0) {
            GL11.glDeleteTextures(colorTexId);
            colorTexId = 0;
        }
        if (depthRboId != 0) {
            GL30.glDeleteRenderbuffers(depthRboId);
            depthRboId = 0;
        }
    }
}

