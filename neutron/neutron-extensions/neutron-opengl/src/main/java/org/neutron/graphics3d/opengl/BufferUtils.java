/*
 * Neutron Java ME Runtime Engine
 * 
 * Licensed under the Apache License, Version 2.0 or LGPL 2.1
 */
package org.neutron.graphics3d.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public final class BufferUtils {
    private BufferUtils() {}

    public static IntBuffer createIntBuffer(int capacity) {
        return createByteBuffer(capacity * 4).asIntBuffer();
    }

    public static FloatBuffer createFloatBuffer(int capacity) {
        return createByteBuffer(capacity * 4).asFloatBuffer();
    }

    public static ByteBuffer createByteBuffer(int capacity) {
        return ByteBuffer.allocateDirect(capacity).order(ByteOrder.nativeOrder());
    }

    public static ShortBuffer createShortBuffer(int capacity) {
        return createByteBuffer(capacity * 2).asShortBuffer();
    }
}

