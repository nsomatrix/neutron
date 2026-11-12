package javax.microedition.khronos.opengles;

import java.nio.Buffer;
import java.nio.IntBuffer;

public interface GL11 extends GL10 {
    int GL_ARRAY_BUFFER = 0x8892;
    int GL_ELEMENT_ARRAY_BUFFER = 0x8893;
    int GL_STATIC_DRAW = 0x88E4;
    int GL_DYNAMIC_DRAW = 0x88E8;

    void glBindBuffer(int target, int buffer);
    void glBufferData(int target, int size, Buffer data, int usage);
    void glBufferSubData(int target, int offset, int size, Buffer data);
    void glDeleteBuffers(int n, int[] buffers, int offset);
    void glGenBuffers(int n, int[] buffers, int offset);
}

