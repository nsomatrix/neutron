package org.neutron.jsr239;


import org.lwjgl.opengl.GL15;
import javax.microedition.khronos.opengles.GL11;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class GL11Impl implements GL11 {

    @Override
    public void glBindTexture(int target, int texture) {
        org.lwjgl.opengl.GL11.glBindTexture(target, texture);
    }

    @Override
    public void glBlendFunc(int sfactor, int dfactor) {
        org.lwjgl.opengl.GL11.glBlendFunc(sfactor, dfactor);
    }

    @Override
    public void glClear(int mask) {
        org.lwjgl.opengl.GL11.glClear(mask);
    }

    @Override
    public void glClearColor(float red, float green, float blue, float alpha) {
        org.lwjgl.opengl.GL11.glClearColor(red, green, blue, alpha);
    }

    @Override
    public void glClearDepthf(float depth) {
        org.lwjgl.opengl.GL11.glClearDepth(depth);
    }

    @Override
    public void glColor4f(float red, float green, float blue, float alpha) {
        org.lwjgl.opengl.GL11.glColor4f(red, green, blue, alpha);
    }

    @Override
    public void glColorPointer(int size, int type, int stride, Buffer pointer) {
        if (pointer instanceof FloatBuffer) {
            org.lwjgl.opengl.GL11.glColorPointer(size, type, stride, (FloatBuffer) pointer);
        } else if (pointer instanceof ByteBuffer) {
            org.lwjgl.opengl.GL11.glColorPointer(size, type, stride, (ByteBuffer) pointer);
        }
    }

    @Override
    public void glCullFace(int mode) {
        org.lwjgl.opengl.GL11.glCullFace(mode);
    }

    @Override
    public void glDeleteTextures(int n, int[] textures, int offset) {
        for (int i = 0; i < n; i++) {
            org.lwjgl.opengl.GL11.glDeleteTextures(textures[offset + i]);
        }
    }

    @Override
    public void glDisable(int cap) {
        org.lwjgl.opengl.GL11.glDisable(cap);
    }

    @Override
    public void glDisableClientState(int array) {
        org.lwjgl.opengl.GL11.glDisableClientState(array);
    }

    @Override
    public void glDrawArrays(int mode, int first, int count) {
        org.lwjgl.opengl.GL11.glDrawArrays(mode, first, count);
    }

    @Override
    public void glDrawElements(int mode, int count, int type, Buffer indices) {
        if (indices instanceof ByteBuffer) {
            org.lwjgl.opengl.GL11.glDrawElements(mode, (ByteBuffer) indices);
        } else if (indices instanceof ShortBuffer) {
            org.lwjgl.opengl.GL11.glDrawElements(mode, (ShortBuffer) indices);
        } else if (indices instanceof IntBuffer) {
            org.lwjgl.opengl.GL11.glDrawElements(mode, (IntBuffer) indices);
        }
    }

    @Override
    public void glEnable(int cap) {
        org.lwjgl.opengl.GL11.glEnable(cap);
    }

    @Override
    public void glEnableClientState(int array) {
        org.lwjgl.opengl.GL11.glEnableClientState(array);
    }

    @Override
    public void glFinish() {
        org.lwjgl.opengl.GL11.glFinish();
    }

    @Override
    public void glFlush() {
        org.lwjgl.opengl.GL11.glFlush();
    }

    @Override
    public void glFrustumf(float left, float right, float bottom, float top, float zNear, float zFar) {
        org.lwjgl.opengl.GL11.glFrustum(left, right, bottom, top, zNear, zFar);
    }

    @Override
    public void glGenTextures(int n, int[] textures, int offset) {
        for (int i = 0; i < n; i++) {
            textures[offset + i] = org.lwjgl.opengl.GL11.glGenTextures();
        }
    }

    @Override
    public int glGetError() {
        return org.lwjgl.opengl.GL11.glGetError();
    }

    @Override
    public void glLightModelfv(int pname, float[] params, int offset) {
        FloatBuffer fb = FloatBuffer.wrap(params, offset, params.length - offset);
        org.lwjgl.opengl.GL11.glLightModelfv(pname, fb);
    }

    @Override
    public void glLightfv(int light, int pname, float[] params, int offset) {
        FloatBuffer fb = FloatBuffer.wrap(params, offset, params.length - offset);
        org.lwjgl.opengl.GL11.glLightfv(light, pname, fb);
    }

    @Override
    public void glLoadIdentity() {
        org.lwjgl.opengl.GL11.glLoadIdentity();
    }

    @Override
    public void glLoadMatrixf(float[] m, int offset) {
        FloatBuffer fb = FloatBuffer.wrap(m, offset, 16);
        org.lwjgl.opengl.GL11.glLoadMatrixf(fb);
    }

    @Override
    public void glMaterialfv(int face, int pname, float[] params, int offset) {
        FloatBuffer fb = FloatBuffer.wrap(params, offset, params.length - offset);
        org.lwjgl.opengl.GL11.glMaterialfv(face, pname, fb);
    }

    @Override
    public void glMatrixMode(int mode) {
        org.lwjgl.opengl.GL11.glMatrixMode(mode);
    }

    @Override
    public void glMultMatrixf(float[] m, int offset) {
        FloatBuffer fb = FloatBuffer.wrap(m, offset, 16);
        org.lwjgl.opengl.GL11.glMultMatrixf(fb);
    }

    @Override
    public void glNormalPointer(int type, int stride, Buffer pointer) {
        if (pointer instanceof FloatBuffer) {
            org.lwjgl.opengl.GL11.glNormalPointer(type, stride, (FloatBuffer) pointer);
        } else if (pointer instanceof ByteBuffer) {
            org.lwjgl.opengl.GL11.glNormalPointer(type, stride, (ByteBuffer) pointer);
        }
    }

    @Override
    public void glOrthof(float left, float right, float bottom, float top, float zNear, float zFar) {
        org.lwjgl.opengl.GL11.glOrtho(left, right, bottom, top, zNear, zFar);
    }

    @Override
    public void glPopMatrix() {
        org.lwjgl.opengl.GL11.glPopMatrix();
    }

    @Override
    public void glPushMatrix() {
        org.lwjgl.opengl.GL11.glPushMatrix();
    }

    @Override
    public void glRotatef(float angle, float x, float y, float z) {
        org.lwjgl.opengl.GL11.glRotatef(angle, x, y, z);
    }

    @Override
    public void glScalef(float x, float y, float z) {
        org.lwjgl.opengl.GL11.glScalef(x, y, z);
    }

    @Override
    public void glScissor(int x, int y, int width, int height) {
        org.lwjgl.opengl.GL11.glScissor(x, y, width, height);
    }

    @Override
    public void glTexCoordPointer(int size, int type, int stride, Buffer pointer) {
        if (pointer instanceof FloatBuffer) {
            org.lwjgl.opengl.GL11.glTexCoordPointer(size, type, stride, (FloatBuffer) pointer);
        } else if (pointer instanceof ByteBuffer) {
            org.lwjgl.opengl.GL11.glTexCoordPointer(size, type, stride, (ByteBuffer) pointer);
        }
    }

    @Override
    public void glTexEnvf(int target, int pname, float param) {
        org.lwjgl.opengl.GL11.glTexEnvf(target, pname, param);
    }

    @Override
    public void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels) {
        if (pixels instanceof ByteBuffer) {
            org.lwjgl.opengl.GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (ByteBuffer) pixels);
        } else if (pixels instanceof IntBuffer) {
            org.lwjgl.opengl.GL11.glTexImage2D(target, level, internalformat, width, height, border, format, type, (IntBuffer) pixels);
        }
    }

    @Override
    public void glTexParameteri(int target, int pname, int param) {
        org.lwjgl.opengl.GL11.glTexParameteri(target, pname, param);
    }

    @Override
    public void glTranslatef(float x, float y, float z) {
        org.lwjgl.opengl.GL11.glTranslatef(x, y, z);
    }

    @Override
    public void glVertexPointer(int size, int type, int stride, Buffer pointer) {
        if (pointer instanceof FloatBuffer) {
            org.lwjgl.opengl.GL11.glVertexPointer(size, type, stride, (FloatBuffer) pointer);
        } else if (pointer instanceof ByteBuffer) {
            org.lwjgl.opengl.GL11.glVertexPointer(size, type, stride, (ByteBuffer) pointer);
        }
    }

    @Override
    public void glViewport(int x, int y, int width, int height) {
        org.lwjgl.opengl.GL11.glViewport(x, y, width, height);
    }

    @Override
    public void glBindBuffer(int target, int buffer) {
        GL15.glBindBuffer(target, buffer);
    }

    @Override
    public void glBufferData(int target, int size, Buffer data, int usage) {
        if (data instanceof ByteBuffer) {
            GL15.glBufferData(target, (ByteBuffer) data, usage);
        } else if (data instanceof FloatBuffer) {
            GL15.glBufferData(target, (FloatBuffer) data, usage);
        } else if (data instanceof IntBuffer) {
            GL15.glBufferData(target, (IntBuffer) data, usage);
        }
    }

    @Override
    public void glBufferSubData(int target, int offset, int size, Buffer data) {
        if (data instanceof ByteBuffer) {
            GL15.glBufferSubData(target, offset, (ByteBuffer) data);
        }
    }

    @Override
    public void glDeleteBuffers(int n, int[] buffers, int offset) {
        for (int i = 0; i < n; i++) {
            GL15.glDeleteBuffers(buffers[offset + i]);
        }
    }

    @Override
    public void glGenBuffers(int n, int[] buffers, int offset) {
        for (int i = 0; i < n; i++) {
            buffers[offset + i] = GL15.glGenBuffers();
        }
    }

    private static float toFloat(int fixed) {
        return fixed / 65536.0f;
    }

    private static float[] toFloatArray(int[] fixed, int offset, int count) {
        float[] f = new float[count];
        for (int i = 0; i < count; i++) {
            f[i] = toFloat(fixed[offset + i]);
        }
        return f;
    }

    private static float[] toFloatArray(IntBuffer fixed, int count) {
        float[] f = new float[count];
        int pos = fixed.position();
        for (int i = 0; i < count; i++) {
            f[i] = toFloat(fixed.get(pos + i));
        }
        return f;
    }

    // Common-Lite Fixed-Point API
    @Override
    public void glClearColorx(int red, int green, int blue, int alpha) {
        glClearColor(toFloat(red), toFloat(green), toFloat(blue), toFloat(alpha));
    }

    @Override
    public void glClearDepthx(int depth) {
        glClearDepthf(toFloat(depth));
    }

    @Override
    public void glColor4x(int red, int green, int blue, int alpha) {
        glColor4f(toFloat(red), toFloat(green), toFloat(blue), toFloat(alpha));
    }

    @Override
    public void glFrustumx(int left, int right, int bottom, int top, int zNear, int zFar) {
        glFrustumf(toFloat(left), toFloat(right), toFloat(bottom), toFloat(top), toFloat(zNear), toFloat(zFar));
    }

    @Override
    public void glLightModelx(int pname, int param) {
        org.lwjgl.opengl.GL11.glLightModeli(pname, param);
    }

    @Override
    public void glLightModelxv(int pname, int[] params, int offset) {
        float[] f = toFloatArray(params, offset, 4);
        glLightModelfv(pname, f, 0);
    }

    @Override
    public void glLightModelxv(int pname, IntBuffer params) {
        float[] f = toFloatArray(params, Math.min(params.remaining(), 4));
        glLightModelfv(pname, f, 0);
    }

    @Override
    public void glLightx(int light, int pname, int param) {
        org.lwjgl.opengl.GL11.glLighti(light, pname, param);
    }

    @Override
    public void glLightxv(int light, int pname, int[] params, int offset) {
        float[] f = toFloatArray(params, offset, 4);
        glLightfv(light, pname, f, 0);
    }

    @Override
    public void glLightxv(int light, int pname, IntBuffer params) {
        float[] f = toFloatArray(params, Math.min(params.remaining(), 4));
        glLightfv(light, pname, f, 0);
    }

    @Override
    public void glLineWidthx(int width) {
        org.lwjgl.opengl.GL11.glLineWidth(toFloat(width));
    }

    @Override
    public void glLoadMatrixx(int[] m, int offset) {
        float[] f = toFloatArray(m, offset, 16);
        glLoadMatrixf(f, 0);
    }

    @Override
    public void glLoadMatrixx(IntBuffer m) {
        float[] f = toFloatArray(m, 16);
        glLoadMatrixf(f, 0);
    }

    @Override
    public void glMaterialx(int face, int pname, int param) {
        org.lwjgl.opengl.GL11.glMateriali(face, pname, param);
    }

    @Override
    public void glMaterialxv(int face, int pname, int[] params, int offset) {
        float[] f = toFloatArray(params, offset, 4);
        glMaterialfv(face, pname, f, 0);
    }

    @Override
    public void glMaterialxv(int face, int pname, IntBuffer params) {
        float[] f = toFloatArray(params, Math.min(params.remaining(), 4));
        glMaterialfv(face, pname, f, 0);
    }

    @Override
    public void glMultMatrixx(int[] m, int offset) {
        float[] f = toFloatArray(m, offset, 16);
        glMultMatrixf(f, 0);
    }

    @Override
    public void glMultMatrixx(IntBuffer m) {
        float[] f = toFloatArray(m, 16);
        glMultMatrixf(f, 0);
    }

    @Override
    public void glMultiTexCoord4x(int target, int s, int t, int r, int q) {
        org.lwjgl.opengl.GL13.glMultiTexCoord4f(target, toFloat(s), toFloat(t), toFloat(r), toFloat(q));
    }

    @Override
    public void glNormal3x(int nx, int ny, int nz) {
        org.lwjgl.opengl.GL11.glNormal3f(toFloat(nx), toFloat(ny), toFloat(nz));
    }

    @Override
    public void glOrthox(int left, int right, int bottom, int top, int zNear, int zFar) {
        glOrthof(toFloat(left), toFloat(right), toFloat(bottom), toFloat(top), toFloat(zNear), toFloat(zFar));
    }

    @Override
    public void glPointSizex(int size) {
        org.lwjgl.opengl.GL11.glPointSize(toFloat(size));
    }

    @Override
    public void glPolygonOffsetx(int factor, int units) {
        org.lwjgl.opengl.GL11.glPolygonOffset(toFloat(factor), toFloat(units));
    }

    @Override
    public void glRotatex(int angle, int x, int y, int z) {
        glRotatef(toFloat(angle), toFloat(x), toFloat(y), toFloat(z));
    }

    @Override
    public void glSampleCoveragex(int value, boolean invert) {
        org.lwjgl.opengl.GL13.glSampleCoverage(toFloat(value), invert);
    }

    @Override
    public void glScalex(int x, int y, int z) {
        glScalef(toFloat(x), toFloat(y), toFloat(z));
    }

    @Override
    public void glTexEnvx(int target, int pname, int param) {
        org.lwjgl.opengl.GL11.glTexEnvi(target, pname, param);
    }

    @Override
    public void glTexEnvxv(int target, int pname, int[] params, int offset) {
        float[] f = toFloatArray(params, offset, 4);
        glTexEnvfv(target, pname, f, 0);
    }

    @Override
    public void glTexEnvxv(int target, int pname, IntBuffer params) {
        float[] f = toFloatArray(params, Math.min(params.remaining(), 4));
        glTexEnvfv(target, pname, f, 0);
    }

    @Override
    public void glTexParameterx(int target, int pname, int param) {
        glTexParameteri(target, pname, param);
    }

    @Override
    public void glTexParameterxv(int target, int pname, int[] params, int offset) {
        float[] f = toFloatArray(params, offset, 4);
        glTexParameterfv(target, pname, f, 0);
    }

    @Override
    public void glTexParameterxv(int target, int pname, IntBuffer params) {
        float[] f = toFloatArray(params, Math.min(params.remaining(), 4));
        glTexParameterfv(target, pname, f, 0);
    }

    @Override
    public void glTranslatex(int x, int y, int z) {
        glTranslatef(toFloat(x), toFloat(y), toFloat(z));
    }

    // Buffer Overloads
    @Override
    public void glLightModelfv(int pname, FloatBuffer params) {
        org.lwjgl.opengl.GL11.glLightModelfv(pname, params);
    }

    @Override
    public void glLightfv(int light, int pname, FloatBuffer params) {
        org.lwjgl.opengl.GL11.glLightfv(light, pname, params);
    }

    @Override
    public void glLoadMatrixf(FloatBuffer m) {
        org.lwjgl.opengl.GL11.glLoadMatrixf(m);
    }

    @Override
    public void glMaterialfv(int face, int pname, FloatBuffer params) {
        org.lwjgl.opengl.GL11.glMaterialfv(face, pname, params);
    }

    @Override
    public void glMultMatrixf(FloatBuffer m) {
        org.lwjgl.opengl.GL11.glMultMatrixf(m);
    }

    @Override
    public void glTexEnvfv(int target, int pname, float[] params, int offset) {
        FloatBuffer buf = FloatBuffer.wrap(params, offset, params.length - offset);
        org.lwjgl.opengl.GL11.glTexEnvfv(target, pname, buf);
    }

    @Override
    public void glTexEnvfv(int target, int pname, FloatBuffer params) {
        org.lwjgl.opengl.GL11.glTexEnvfv(target, pname, params);
    }

    @Override
    public void glTexParameterfv(int target, int pname, float[] params, int offset) {
        FloatBuffer buf = FloatBuffer.wrap(params, offset, params.length - offset);
        org.lwjgl.opengl.GL11.glTexParameterfv(target, pname, buf);
    }

    @Override
    public void glTexParameterfv(int target, int pname, FloatBuffer params) {
        org.lwjgl.opengl.GL11.glTexParameterfv(target, pname, params);
    }
}
