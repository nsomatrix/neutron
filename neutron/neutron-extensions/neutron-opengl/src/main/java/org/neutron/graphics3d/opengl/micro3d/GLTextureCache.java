/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under the Apache License, Version 2.0 or LGPL 2.1
 */
package org.neutron.graphics3d.opengl.micro3d;

import com.mascotcapsule.micro3d.v3.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.neutron.graphics3d.Graphics3DConfig;

import java.nio.IntBuffer;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Manages GPU texture allocation and caching for MascotCapsule Micro3D textures.
 */
public final class GLTextureCache {

    private final Map<Texture, Integer> textureIdMap = new WeakHashMap<>();

    public int getOrCreateTexture(Texture texture) {
        if (texture == null) return 0;

        Integer cachedId = textureIdMap.get(texture);
        if (cachedId != null && cachedId != 0 && GL11.glIsTexture(cachedId)) {
            return cachedId;
        }

        int w = texture.getWidth();
        int h = texture.getHeight();
        if (w <= 0 || h <= 0) return 0;

        int[] envmap = texture.getEnvmapData();
        byte[] bitmap = texture.getBitmapData();
        int[] palette = texture.getOrigPalette();

        int[] argb = new int[w * h];
        if (envmap != null && envmap.length >= w * h) {
            System.arraycopy(envmap, 0, argb, 0, w * h);
        } else if (bitmap != null && palette != null) {
            int len = Math.min(bitmap.length, w * h);
            for (int i = 0; i < len; i++) {
                int idx = bitmap[i] & 0xFF;
                if (idx < palette.length) {
                    argb[i] = palette[idx];
                }
            }
        } else {
            return 0;
        }

        IntBuffer pixelBuffer = BufferUtils.createIntBuffer(w * h);
        pixelBuffer.put(argb).flip();

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

        textureIdMap.put(texture, texId);
        return texId;
    }

    public void clear() {
        for (Integer id : textureIdMap.values()) {
            if (id != null && id != 0) {
                GL11.glDeleteTextures(id);
            }
        }
        textureIdMap.clear();
    }
}

