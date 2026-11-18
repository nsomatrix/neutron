package javax.microedition.khronos.opengles;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public interface GL10 extends GL {
    int GL_POINTS = 0x0000;
    int GL_LINES = 0x0001;
    int GL_LINE_LOOP = 0x0002;
    int GL_LINE_STRIP = 0x0003;
    int GL_TRIANGLES = 0x0004;
    int GL_TRIANGLE_STRIP = 0x0005;
    int GL_TRIANGLE_FAN = 0x0006;

    int GL_NEVER = 0x0200;
    int GL_LESS = 0x0201;
    int GL_EQUAL = 0x0202;
    int GL_LEQUAL = 0x0203;
    int GL_GREATER = 0x0204;
    int GL_NOTEQUAL = 0x0205;
    int GL_GEQUAL = 0x0206;
    int GL_ALWAYS = 0x0207;

    int GL_DEPTH_BUFFER_BIT = 0x00000100;
    int GL_STENCIL_BUFFER_BIT = 0x00000400;
    int GL_COLOR_BUFFER_BIT = 0x00004000;

    int GL_SRC_COLOR = 0x0300;
    int GL_ONE_MINUS_SRC_COLOR = 0x0301;
    int GL_SRC_ALPHA = 0x0302;
    int GL_ONE_MINUS_SRC_ALPHA = 0x0303;
    int GL_DST_ALPHA = 0x0304;
    int GL_ONE_MINUS_DST_ALPHA = 0x0305;
    int GL_DST_COLOR = 0x0306;
    int GL_ONE_MINUS_DST_COLOR = 0x0307;
    int GL_SRC_ALPHA_SATURATE = 0x0308;

    int GL_CULL_FACE = 0x0B44;
    int GL_LIGHTING = 0x0B50;
    int GL_COLOR_MATERIAL = 0x0B57;
    int GL_FOG = 0x0B60;
    int GL_DEPTH_TEST = 0x0B71;
    int GL_STENCIL_TEST = 0x0B90;
    int GL_NORMALIZE = 0x0BA1;
    int GL_ALPHA_TEST = 0x0BC0;
    int GL_DITHER = 0x0BD0;
    int GL_BLEND = 0x0BE2;
    int GL_SCISSOR_TEST = 0x0C11;
    int GL_TEXTURE_2D = 0x0DE1;

    int GL_BYTE = 0x1400;
    int GL_UNSIGNED_BYTE = 0x1401;
    int GL_SHORT = 0x1402;
    int GL_UNSIGNED_SHORT = 0x1403;
    int GL_FLOAT = 0x1406;
    int GL_FIXED = 0x140C;

    int GL_MODELVIEW = 0x1700;
    int GL_PROJECTION = 0x1701;
    int GL_TEXTURE = 0x1702;

    int GL_CW = 0x0900;
    int GL_CCW = 0x0901;
    int GL_FRONT = 0x0404;
    int GL_BACK = 0x0405;
    int GL_FRONT_AND_BACK = 0x0408;

    int GL_VERTEX_ARRAY = 0x8074;
    int GL_NORMAL_ARRAY = 0x8075;
    int GL_COLOR_ARRAY = 0x8076;
    int GL_TEXTURE_COORD_ARRAY = 0x8078;

    int GL_TEXTURE_ENV = 0x2300;
    int GL_TEXTURE_ENV_MODE = 0x2200;
    int GL_MODULATE = 0x2100;
    int GL_DECAL = 0x2101;
    int GL_REPLACE = 0x1E01;
    int GL_ADD = 0x0104;

    int GL_NEAREST = 0x2600;
    int GL_LINEAR = 0x2601;
    int GL_NEAREST_MIPMAP_NEAREST = 0x2700;
    int GL_LINEAR_MIPMAP_NEAREST = 0x2701;
    int GL_NEAREST_MIPMAP_LINEAR = 0x2702;
    int GL_LINEAR_MIPMAP_LINEAR = 0x2703;

    int GL_TEXTURE_MAG_FILTER = 0x2800;
    int GL_TEXTURE_MIN_FILTER = 0x2801;
    int GL_TEXTURE_WRAP_S = 0x2802;
    int GL_TEXTURE_WRAP_T = 0x2803;
    int GL_REPEAT = 0x2901;
    int GL_CLAMP_TO_EDGE = 0x812F;

    int GL_RGBA = 0x1908;
    int GL_RGB = 0x1907;

    void glBindTexture(int target, int texture);
    void glBlendFunc(int sfactor, int dfactor);
    void glClear(int mask);
    void glClearColor(float red, float green, float blue, float alpha);
    void glClearDepthf(float depth);
    void glColor4f(float red, float green, float blue, float alpha);
    void glColorPointer(int size, int type, int stride, Buffer pointer);
    void glCullFace(int mode);
    void glDeleteTextures(int n, int[] textures, int offset);
    void glDisable(int cap);
    void glDisableClientState(int array);
    void glDrawArrays(int mode, int first, int count);
    void glDrawElements(int mode, int count, int type, Buffer indices);
    void glEnable(int cap);
    void glEnableClientState(int array);
    void glFinish();
    void glFlush();
    void glFrustumf(float left, float right, float bottom, float top, float zNear, float zFar);
    void glGenTextures(int n, int[] textures, int offset);
    int glGetError();
    void glLightModelfv(int pname, float[] params, int offset);
    void glLightfv(int light, int pname, float[] params, int offset);
    void glLoadIdentity();
    void glLoadMatrixf(float[] m, int offset);
    void glMaterialfv(int face, int pname, float[] params, int offset);
    void glMatrixMode(int mode);
    void glMultMatrixf(float[] m, int offset);
    void glNormalPointer(int type, int stride, Buffer pointer);
    void glOrthof(float left, float right, float bottom, float top, float zNear, float zFar);
    void glPopMatrix();
    void glPushMatrix();
    void glRotatef(float angle, float x, float y, float z);
    void glScalef(float x, float y, float z);
    void glScissor(int x, int y, int width, int height);
    void glTexCoordPointer(int size, int type, int stride, Buffer pointer);
    void glTexEnvf(int target, int pname, float param);
    void glTexImage2D(int target, int level, int internalformat, int width, int height, int border, int format, int type, Buffer pixels);
    void glTexParameteri(int target, int pname, int param);
    void glTranslatef(float x, float y, float z);
    void glVertexPointer(int size, int type, int stride, Buffer pointer);
    void glViewport(int x, int y, int width, int height);

    // Common-Lite Fixed-Point API
    void glClearColorx(int red, int green, int blue, int alpha);
    void glClearDepthx(int depth);
    void glColor4x(int red, int green, int blue, int alpha);
    void glFrustumx(int left, int right, int bottom, int top, int zNear, int zFar);
    void glLightModelx(int pname, int param);
    void glLightModelxv(int pname, int[] params, int offset);
    void glLightModelxv(int pname, IntBuffer params);
    void glLightx(int light, int pname, int param);
    void glLightxv(int light, int pname, int[] params, int offset);
    void glLightxv(int light, int pname, IntBuffer params);
    void glLineWidthx(int width);
    void glLoadMatrixx(int[] m, int offset);
    void glLoadMatrixx(IntBuffer m);
    void glMaterialx(int face, int pname, int param);
    void glMaterialxv(int face, int pname, int[] params, int offset);
    void glMaterialxv(int face, int pname, IntBuffer params);
    void glMultMatrixx(int[] m, int offset);
    void glMultMatrixx(IntBuffer m);
    void glMultiTexCoord4x(int target, int s, int t, int r, int q);
    void glNormal3x(int nx, int ny, int nz);
    void glOrthox(int left, int right, int bottom, int top, int zNear, int zFar);
    void glPointSizex(int size);
    void glPolygonOffsetx(int factor, int units);
    void glRotatex(int angle, int x, int y, int z);
    void glSampleCoveragex(int value, boolean invert);
    void glScalex(int x, int y, int z);
    void glTexEnvx(int target, int pname, int param);
    void glTexEnvxv(int target, int pname, int[] params, int offset);
    void glTexEnvxv(int target, int pname, IntBuffer params);
    void glTexParameterx(int target, int pname, int param);
    void glTexParameterxv(int target, int pname, int[] params, int offset);
    void glTexParameterxv(int target, int pname, IntBuffer params);
    void glTranslatex(int x, int y, int z);

    // Buffer Overloads
    void glLightModelfv(int pname, FloatBuffer params);
    void glLightfv(int light, int pname, FloatBuffer params);
    void glLoadMatrixf(FloatBuffer m);
    void glMaterialfv(int face, int pname, FloatBuffer params);
    void glMultMatrixf(FloatBuffer m);
    void glTexEnvfv(int target, int pname, float[] params, int offset);
    void glTexEnvfv(int target, int pname, FloatBuffer params);
    void glTexParameterfv(int target, int pname, float[] params, int offset);
    void glTexParameterfv(int target, int pname, FloatBuffer params);
}

