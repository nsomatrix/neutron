package org.neutron.graphics3d.opengl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL21;
import org.lwjgl.opengl.GL30;
import org.neutron.graphics3d.Graphics3DConfig;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

/**
 * High-performance OpenGL Framebuffer Object (FBO) with asynchronous
 * dual PBO DMA readback and configurable internal super-sampling.
 */
public class GLFramebuffer {
    private int fboId = 0;
    private int colorTexId = 0;
    private int depthRboId = 0;
    private int width = 0;
    private int height = 0;
    private int renderWidth = 0;
    private int renderHeight = 0;
    private int scale = 1;

    private IntBuffer pixelBuffer;

    // Dual PBO asynchronous DMA transfer
    private final int[] pboIds = new int[2];
    private int pboIndex = 0;
    private boolean pboAvailable = false;
    private boolean pboFirstFrame = true;

    public void resize(int w, int h) {
        if (w <= 0 || h <= 0) return;
        int currentScale = Math.max(1, Graphics3DConfig.getInternalScale());
        if (width == w && height == h && scale == currentScale && fboId != 0) return;

        destroy();
        this.width = w;
        this.height = h;
        this.scale = currentScale;
        this.renderWidth = w * scale;
        this.renderHeight = h * scale;

        fboId = GL30.glGenFramebuffers();
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);

        // Color attachment texture
        colorTexId = GL11.glGenTextures();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, colorTexId);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, renderWidth, renderHeight, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, (ByteBuffer) null);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL30.glFramebufferTexture2D(GL30.GL_FRAMEBUFFER, GL30.GL_COLOR_ATTACHMENT0, GL11.GL_TEXTURE_2D, colorTexId, 0);

        // Depth attachment renderbuffer (24-bit depth for jitter-free Z-buffering, fallback to 16-bit)
        depthRboId = GL30.glGenRenderbuffers();
        GL30.glBindRenderbuffer(GL30.GL_RENDERBUFFER, depthRboId);
        GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT24, renderWidth, renderHeight);
        GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthRboId);

        int fboStatus = GL30.glCheckFramebufferStatus(GL30.GL_FRAMEBUFFER);
        if (fboStatus != GL30.GL_FRAMEBUFFER_COMPLETE) {
            // Fallback to 16-bit depth buffer if 24-bit attachment is rejected by GPU driver
            GL30.glRenderbufferStorage(GL30.GL_RENDERBUFFER, GL30.GL_DEPTH_COMPONENT16, renderWidth, renderHeight);
            GL30.glFramebufferRenderbuffer(GL30.GL_FRAMEBUFFER, GL30.GL_DEPTH_ATTACHMENT, GL30.GL_RENDERBUFFER, depthRboId);
        }

        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
        pixelBuffer = BufferUtils.createIntBuffer(renderWidth * renderHeight);

        // Initialize Dual PBOs for asynchronous DMA transfer if enabled
        if (Graphics3DConfig.isAsyncPbo()) {
            try {
                pboIds[0] = GL15.glGenBuffers();
                pboIds[1] = GL15.glGenBuffers();
                long bufferBytes = (long) renderWidth * renderHeight * 4L;
                for (int i = 0; i < 2; i++) {
                    GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, pboIds[i]);
                    GL15.glBufferData(GL21.GL_PIXEL_PACK_BUFFER, bufferBytes, GL15.GL_STREAM_READ);
                }
                GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);
                pboAvailable = true;
                pboIndex = 0;
                pboFirstFrame = true;
            } catch (Throwable t) {
                pboAvailable = false;
            }
        } else {
            pboAvailable = false;
        }
    }

    public void bind() {
        if (fboId != 0) {
            GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, fboId);
            GL11.glViewport(0, 0, renderWidth, renderHeight);
        }
    }

    public void unbind() {
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, 0);
    }

    public void readPixels(int[] dest) {
        if (fboId == 0 || dest == null || dest.length < width * height) return;

        if (pboAvailable && Graphics3DConfig.isAsyncPbo()) {
            readPixelsAsyncPBO(dest);
        } else {
            readPixelsSync(dest);
        }
    }

    private void readPixelsAsyncPBO(int[] dest) {
        bind();
        int currentPbo = pboIndex % 2;
        int nextPbo = (pboIndex + 1) % 2;

        // 1. Asynchronous non-blocking transfer from framebuffer into current PBO
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, pboIds[currentPbo]);
        GL11.glReadPixels(0, 0, renderWidth, renderHeight, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, 0L);

        // 2. Map previously transferred PBO (nextPbo) or current on first frame
        int readPbo = pboFirstFrame ? currentPbo : nextPbo;
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, pboIds[readPbo]);
        ByteBuffer mapped = GL15.glMapBuffer(GL21.GL_PIXEL_PACK_BUFFER, GL15.GL_READ_ONLY);
        if (mapped != null) {
            IntBuffer intBuf = mapped.asIntBuffer();
            copyAndDownsample(intBuf, dest);
            GL15.glUnmapBuffer(GL21.GL_PIXEL_PACK_BUFFER);
        } else {
            // Mapping failed or unsupported on this driver; fall back to synchronous readPixels
            readPixelsSync(dest);
        }
        GL15.glBindBuffer(GL21.GL_PIXEL_PACK_BUFFER, 0);

        pboIndex = nextPbo;
        pboFirstFrame = false;
    }

    private void readPixelsSync(int[] dest) {
        bind();
        pixelBuffer.clear();
        GL11.glReadPixels(0, 0, renderWidth, renderHeight, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, pixelBuffer);
        copyAndDownsample(pixelBuffer, dest);
    }

    private void copyAndDownsample(IntBuffer srcBuf, int[] dest) {
        if (scale == 1) {
            for (int y = 0; y < height; y++) {
                srcBuf.position((height - 1 - y) * width);
                srcBuf.get(dest, y * width, width);
            }
        } else {
            for (int y = 0; y < height; y++) {
                int srcY = (renderHeight - 1 - y * scale);
                int destRow = y * width;
                for (int x = 0; x < width; x++) {
                    int srcX = x * scale;
                    dest[destRow + x] = srcBuf.get(srcY * renderWidth + srcX);
                }
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getRenderWidth() {
        return renderWidth;
    }

    public int getRenderHeight() {
        return renderHeight;
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
        if (pboIds[0] != 0) {
            GL15.glDeleteBuffers(pboIds[0]);
            pboIds[0] = 0;
        }
        if (pboIds[1] != 0) {
            GL15.glDeleteBuffers(pboIds[1]);
            pboIds[1] = 0;
        }
        pboAvailable = false;
        pboFirstFrame = true;
    }
}
