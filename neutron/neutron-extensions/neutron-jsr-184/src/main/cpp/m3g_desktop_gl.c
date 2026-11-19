/*
 * Neutron Java ME Runtime Engine - Desktop OpenGL & EGL Abstraction Layer
 * 
 * Provides an in-process EGL 1.4 and OpenGL ES 1.1 fixed-point implementation
 * backed by host desktop OpenGL (WGL on Windows, CGL on macOS).
 * Enables running Nokia M3G (JSR-184) natively on Windows and macOS with
 * zero external dependencies (no ANGLE or Mesa required).
 */

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <stdint.h>

#define KHRONOS_STATIC 1
#include "KHR/khrplatform.h"
#include "EGL/egl.h"
#include "GLES/gl.h"

#ifndef APIENTRY
#ifdef _WIN32
#define APIENTRY __stdcall
#else
#define APIENTRY
#endif
#endif

#ifndef GL_RGBA8
#define GL_RGBA8 0x8058
#endif

#ifdef _WIN32
#define WIN32_LEAN_AND_MEAN 1
#include <windows.h>
#include <GL/gl.h>
#elif defined(__APPLE__)
#include <dlfcn.h>
typedef void* CGLContextObj;
typedef void* CGLPixelFormatObj;
typedef uint32_t CGLPixelFormatAttribute;
typedef int CGLError;
#define kCGLNoError 0
#define kCGLPFAAccelerated 73
#define kCGLPFAColorSize 8
#define kCGLPFADepthSize 12
extern CGLError CGLChoosePixelFormat(const CGLPixelFormatAttribute *attribs, CGLPixelFormatObj *pix, int *npix);
extern CGLError CGLCreateContext(CGLPixelFormatObj pix, CGLContextObj share, CGLContextObj *ctx);
extern CGLError CGLDestroyContext(CGLContextObj ctx);
extern CGLError CGLSetCurrentContext(CGLContextObj ctx);
extern CGLContextObj CGLGetCurrentContext(void);
extern void glClearDepth(double depth);
extern void glFogi(GLenum pname, GLint param);
extern void glOrtho(double left, double right, double bottom, double top, double zNear, double zFar);
extern void glDepthRange(double zNear, double zFar);
#endif

/* OpenGL Extension Function Signatures */
typedef void (APIENTRY *PFNGLGENFRAMEBUFFERSPROC)(GLsizei n, GLuint *framebuffers);
typedef void (APIENTRY *PFNGLDELETEFRAMEBUFFERSPROC)(GLsizei n, const GLuint *framebuffers);
typedef void (APIENTRY *PFNGLBINDFRAMEBUFFERPROC)(GLenum target, GLuint framebuffer);
typedef void (APIENTRY *PFNGLFRAMEBUFFERTEXTURE2DPROC)(GLenum target, GLenum attachment, GLenum textarget, GLuint texture, GLint level);
typedef void (APIENTRY *PFNGLGENRENDERBUFFERSPROC)(GLsizei n, GLuint *renderbuffers);
typedef void (APIENTRY *PFNGLDELETERENDERBUFFERSPROC)(GLsizei n, const GLuint *renderbuffers);
typedef void (APIENTRY *PFNGLBINDRENDERBUFFERPROC)(GLenum target, GLuint renderbuffer);
typedef void (APIENTRY *PFNGLRENDERBUFFERSTORAGEPROC)(GLenum target, GLenum internalformat, GLsizei width, GLsizei height);
typedef void (APIENTRY *PFNGLFRAMEBUFFERRENDERBUFFERPROC)(GLenum target, GLenum attachment, GLenum renderbuffertarget, GLuint renderbuffer);

typedef void (APIENTRY *PFNGLACTIVETEXTUREPROC)(GLenum texture);
typedef void (APIENTRY *PFNGLCLIENTACTIVETEXTUREPROC)(GLenum texture);
typedef void (APIENTRY *PFNGLCOMPRESSEDTEXIMAGE2DPROC)(GLenum target, GLint level, GLenum internalformat, GLsizei width, GLsizei height, GLint border, GLsizei imageSize, const void *data);

static PFNGLGENFRAMEBUFFERSPROC pfn_glGenFramebuffers = NULL;
static PFNGLDELETEFRAMEBUFFERSPROC pfn_glDeleteFramebuffers = NULL;
static PFNGLBINDFRAMEBUFFERPROC pfn_glBindFramebuffer = NULL;
static PFNGLFRAMEBUFFERTEXTURE2DPROC pfn_glFramebufferTexture2D = NULL;
static PFNGLGENRENDERBUFFERSPROC pfn_glGenRenderbuffers = NULL;
static PFNGLDELETERENDERBUFFERSPROC pfn_glDeleteRenderbuffers = NULL;
static PFNGLBINDRENDERBUFFERPROC pfn_glBindRenderbuffer = NULL;
static PFNGLRENDERBUFFERSTORAGEPROC pfn_glRenderbufferStorage = NULL;
static PFNGLFRAMEBUFFERRENDERBUFFERPROC pfn_glFramebufferRenderbuffer = NULL;

static PFNGLACTIVETEXTUREPROC pfn_glActiveTexture = NULL;
static PFNGLCLIENTACTIVETEXTUREPROC pfn_glClientActiveTexture = NULL;
static PFNGLCOMPRESSEDTEXIMAGE2DPROC pfn_glCompressedTexImage2D = NULL;

static void* get_proc(const char *name) {
#ifdef _WIN32
    void *p = (void*)wglGetProcAddress(name);
    if (!p || p == (void*)1 || p == (void*)2 || p == (void*)3 || p == (void*)-1) {
        static HMODULE s_gl = NULL;
        if (!s_gl) s_gl = GetModuleHandleA("opengl32.dll");
        if (s_gl) p = (void*)GetProcAddress(s_gl, name);
    }
    return p;
#elif defined(__APPLE__)
    return dlsym(RTLD_DEFAULT, name);
#else
    return NULL;
#endif
}

static void init_gl_procs(void) {
#ifdef _WIN32
    if (!wglGetCurrentContext()) {
        return;
    }
#elif defined(__APPLE__)
    if (!CGLGetCurrentContext()) {
        return;
    }
#endif
    static int inited = 0;
    if (inited) return;

    pfn_glGenFramebuffers = (PFNGLGENFRAMEBUFFERSPROC)get_proc("glGenFramebuffers");
    pfn_glDeleteFramebuffers = (PFNGLDELETEFRAMEBUFFERSPROC)get_proc("glDeleteFramebuffers");
    pfn_glBindFramebuffer = (PFNGLBINDFRAMEBUFFERPROC)get_proc("glBindFramebuffer");
    pfn_glFramebufferTexture2D = (PFNGLFRAMEBUFFERTEXTURE2DPROC)get_proc("glFramebufferTexture2D");
    pfn_glGenRenderbuffers = (PFNGLGENRENDERBUFFERSPROC)get_proc("glGenRenderbuffers");
    pfn_glDeleteRenderbuffers = (PFNGLDELETERENDERBUFFERSPROC)get_proc("glDeleteRenderbuffers");
    pfn_glBindRenderbuffer = (PFNGLBINDRENDERBUFFERPROC)get_proc("glBindRenderbuffer");
    pfn_glRenderbufferStorage = (PFNGLRENDERBUFFERSTORAGEPROC)get_proc("glRenderbufferStorage");
    pfn_glFramebufferRenderbuffer = (PFNGLFRAMEBUFFERRENDERBUFFERPROC)get_proc("glFramebufferRenderbuffer");
    pfn_glActiveTexture = (PFNGLACTIVETEXTUREPROC)get_proc("glActiveTexture");
    pfn_glClientActiveTexture = (PFNGLCLIENTACTIVETEXTUREPROC)get_proc("glClientActiveTexture");
    pfn_glCompressedTexImage2D = (PFNGLCOMPRESSEDTEXIMAGE2DPROC)get_proc("glCompressedTexImage2D");

    // Fallback to EXT/ARB extension variants if core 2.0+ names are not present
    if (!pfn_glGenFramebuffers) pfn_glGenFramebuffers = (PFNGLGENFRAMEBUFFERSPROC)get_proc("glGenFramebuffersEXT");
    if (!pfn_glDeleteFramebuffers) pfn_glDeleteFramebuffers = (PFNGLDELETEFRAMEBUFFERSPROC)get_proc("glDeleteFramebuffersEXT");
    if (!pfn_glBindFramebuffer) pfn_glBindFramebuffer = (PFNGLBINDFRAMEBUFFERPROC)get_proc("glBindFramebufferEXT");
    if (!pfn_glFramebufferTexture2D) pfn_glFramebufferTexture2D = (PFNGLFRAMEBUFFERTEXTURE2DPROC)get_proc("glFramebufferTexture2DEXT");
    if (!pfn_glGenRenderbuffers) pfn_glGenRenderbuffers = (PFNGLGENRENDERBUFFERSPROC)get_proc("glGenRenderbuffersEXT");
    if (!pfn_glDeleteRenderbuffers) pfn_glDeleteRenderbuffers = (PFNGLDELETERENDERBUFFERSPROC)get_proc("glDeleteRenderbuffersEXT");
    if (!pfn_glBindRenderbuffer) pfn_glBindRenderbuffer = (PFNGLBINDRENDERBUFFERPROC)get_proc("glBindRenderbufferEXT");
    if (!pfn_glRenderbufferStorage) pfn_glRenderbufferStorage = (PFNGLRENDERBUFFERSTORAGEPROC)get_proc("glRenderbufferStorageEXT");
    if (!pfn_glFramebufferRenderbuffer) pfn_glFramebufferRenderbuffer = (PFNGLFRAMEBUFFERRENDERBUFFERPROC)get_proc("glFramebufferRenderbufferEXT");
    if (!pfn_glActiveTexture) pfn_glActiveTexture = (PFNGLACTIVETEXTUREPROC)get_proc("glActiveTextureARB");
    if (!pfn_glClientActiveTexture) pfn_glClientActiveTexture = (PFNGLCLIENTACTIVETEXTUREPROC)get_proc("glClientActiveTextureARB");

    if (pfn_glGenFramebuffers) {
        inited = 1;
    }
}

#ifdef _WIN32
static HWND s_hiddenHwnd = NULL;
static HDC s_hiddenHdc = NULL;

static void ensure_hidden_window(void) {
    if (s_hiddenHwnd) return;
    WNDCLASSA wc;
    memset(&wc, 0, sizeof(wc));
    wc.lpfnWndProc = DefWindowProcA;
    wc.hInstance = GetModuleHandleA(NULL);
    wc.lpszClassName = "NeutronM3GHiddenWindow";
    RegisterClassA(&wc);
    s_hiddenHwnd = CreateWindowA("NeutronM3GHiddenWindow", "M3G", WS_OVERLAPPEDWINDOW, 0, 0, 16, 16, NULL, NULL, wc.hInstance, NULL);
    s_hiddenHdc = GetDC(s_hiddenHwnd);
    PIXELFORMATDESCRIPTOR pfd;
    memset(&pfd, 0, sizeof(pfd));
    pfd.nSize = sizeof(pfd);
    pfd.nVersion = 1;
    pfd.dwFlags = PFD_DRAW_TO_WINDOW | PFD_SUPPORT_OPENGL | PFD_DOUBLEBUFFER;
    pfd.iPixelType = PFD_TYPE_RGBA;
    pfd.cColorBits = 32;
    pfd.cDepthBits = 24;
    pfd.cStencilBits = 8;
    int pf = ChoosePixelFormat(s_hiddenHdc, &pfd);
    if (!pf) {
        pfd.cDepthBits = 16;
        pf = ChoosePixelFormat(s_hiddenHdc, &pfd);
    }
    if (pf) {
        SetPixelFormat(s_hiddenHdc, pf, &pfd);
    }
}
#elif defined(__APPLE__)
static CGLContextObj s_cglContext = NULL;
static CGLPixelFormatObj s_cglPixelFormat = NULL;

static void ensure_cgl_context(void) {
    if (s_cglContext) return;
    CGLPixelFormatAttribute attribs[] = {
        (CGLPixelFormatAttribute)kCGLPFAAccelerated,
        (CGLPixelFormatAttribute)kCGLPFAColorSize, (CGLPixelFormatAttribute)24,
        (CGLPixelFormatAttribute)kCGLPFADepthSize, (CGLPixelFormatAttribute)24,
        (CGLPixelFormatAttribute)0
    };
    int numPixelFormats = 0;
    CGLError err = CGLChoosePixelFormat(attribs, &s_cglPixelFormat, &numPixelFormats);
    if (err != kCGLNoError || !s_cglPixelFormat) {
        CGLPixelFormatAttribute fallbackAttribs[] = {
            (CGLPixelFormatAttribute)kCGLPFAColorSize, (CGLPixelFormatAttribute)24,
            (CGLPixelFormatAttribute)0
        };
        CGLChoosePixelFormat(fallbackAttribs, &s_cglPixelFormat, &numPixelFormats);
    }
    if (s_cglPixelFormat) {
        CGLCreateContext(s_cglPixelFormat, NULL, &s_cglContext);
    }
}
#endif

typedef struct DesktopContext {
#ifdef _WIN32
    HDC hdc;
    HGLRC hglrc;
#elif defined(__APPLE__)
    CGLContextObj cgl;
#else
    void *ctx;
#endif
    int id;
} DesktopContext;

typedef struct DesktopSurface {
    int width;
    int height;
    GLuint fbo;
    GLuint colorTex;
    GLuint depthRbo;
} DesktopSurface;

static DesktopSurface *s_currentSurface = NULL;

/* EGL API implementation */
EGLAPI EGLDisplay EGLAPIENTRY eglGetDisplay(EGLNativeDisplayType display_id) {
    return (EGLDisplay)(intptr_t)1;
}

EGLAPI EGLDisplay EGLAPIENTRY eglGetCurrentDisplay(void) {
    return (EGLDisplay)(intptr_t)1;
}

EGLAPI EGLBoolean EGLAPIENTRY eglInitialize(EGLDisplay dpy, EGLint *major, EGLint *minor) {
#ifdef _WIN32
    ensure_hidden_window();
#elif defined(__APPLE__)
    ensure_cgl_context();
#endif
    if (major) *major = 1;
    if (minor) *minor = 4;
    return EGL_TRUE;
}

EGLAPI EGLBoolean EGLAPIENTRY eglTerminate(EGLDisplay dpy) {
    return EGL_TRUE;
}

EGLAPI EGLBoolean EGLAPIENTRY eglBindAPI(EGLenum api) {
    return EGL_TRUE;
}

EGLAPI const char * EGLAPIENTRY eglQueryString(EGLDisplay dpy, EGLint name) {
    if (name == EGL_VERSION) return "1.4";
    if (name == EGL_VENDOR) return "Neutron";
    if (name == EGL_EXTENSIONS) return "";
    return "";
}

EGLAPI EGLBoolean EGLAPIENTRY eglGetConfigs(EGLDisplay dpy, EGLConfig *configs, EGLint config_size, EGLint *num_config) {
    if (num_config) *num_config = 1;
    if (configs && config_size > 0) configs[0] = (EGLConfig)(intptr_t)1;
    return EGL_TRUE;
}

EGLAPI EGLBoolean EGLAPIENTRY eglChooseConfig(EGLDisplay dpy, const EGLint *attrib_list, EGLConfig *configs, EGLint config_size, EGLint *num_config) {
    if (num_config) *num_config = 1;
    if (configs && config_size > 0) configs[0] = (EGLConfig)(intptr_t)1;
    return EGL_TRUE;
}

EGLAPI EGLBoolean EGLAPIENTRY eglGetConfigAttrib(EGLDisplay dpy, EGLConfig config, EGLint attribute, EGLint *value) {
    if (!value) return EGL_FALSE;
    if (attribute == EGL_CONFIG_ID) *value = 1;
    else if (attribute == EGL_CONFIG_CAVEAT) *value = EGL_NONE;
    else if (attribute == EGL_SURFACE_TYPE) *value = EGL_PBUFFER_BIT | EGL_WINDOW_BIT | EGL_PIXMAP_BIT;
    else *value = 0;
    return EGL_TRUE;
}

EGLAPI EGLContext EGLAPIENTRY eglCreateContext(EGLDisplay dpy, EGLConfig config, EGLContext share_context, const EGLint *attrib_list) {
    DesktopContext *c = (DesktopContext*)calloc(1, sizeof(DesktopContext));
#ifdef _WIN32
    ensure_hidden_window();
    c->hdc = s_hiddenHdc;
    c->hglrc = wglCreateContext(s_hiddenHdc);
    if (share_context) {
        DesktopContext *sc = (DesktopContext*)share_context;
        wglShareLists(sc->hglrc, c->hglrc);
    }
#elif defined(__APPLE__)
    ensure_cgl_context();
    c->cgl = s_cglContext;
#endif
    return (EGLContext)c;
}

EGLAPI EGLBoolean EGLAPIENTRY eglDestroyContext(EGLDisplay dpy, EGLContext ctx) {
    if (ctx) {
        DesktopContext *c = (DesktopContext*)ctx;
#ifdef _WIN32
        if (c->hglrc) wglDeleteContext(c->hglrc);
#endif
        free(c);
    }
    return EGL_TRUE;
}

static void ensure_surface_fbo(DesktopSurface *s) {
    if (!s || s->fbo != 0 || s->width <= 0 || s->height <= 0) return;
    init_gl_procs();
    if (!pfn_glGenFramebuffers) return;

    pfn_glGenFramebuffers(1, &s->fbo);
    pfn_glBindFramebuffer(0x8D40 /* GL_FRAMEBUFFER */, s->fbo);

    glGenTextures(1, &s->colorTex);
    glBindTexture(GL_TEXTURE_2D, s->colorTex);
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, s->width, s->height, 0, GL_RGBA, GL_UNSIGNED_BYTE, NULL);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, 0x812F /* GL_CLAMP_TO_EDGE */);
    glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, 0x812F /* GL_CLAMP_TO_EDGE */);
    pfn_glFramebufferTexture2D(0x8D40, 0x8CE0 /* GL_COLOR_ATTACHMENT0 */, GL_TEXTURE_2D, s->colorTex, 0);

    if (pfn_glGenRenderbuffers && pfn_glBindRenderbuffer && pfn_glRenderbufferStorage && pfn_glFramebufferRenderbuffer) {
        pfn_glGenRenderbuffers(1, &s->depthRbo);
        pfn_glBindRenderbuffer(0x8D41 /* GL_RENDERBUFFER */, s->depthRbo);
        pfn_glRenderbufferStorage(0x8D41, 0x81A6 /* GL_DEPTH_COMPONENT16 */, s->width, s->height);
        pfn_glFramebufferRenderbuffer(0x8D40, 0x8D00 /* GL_DEPTH_ATTACHMENT */, 0x8D41, s->depthRbo);
    }

    pfn_glBindFramebuffer(0x8D40, 0);
}

EGLAPI EGLSurface EGLAPIENTRY eglCreatePbufferSurface(EGLDisplay dpy, EGLConfig config, const EGLint *attrib_list) {
    DesktopSurface *s = (DesktopSurface*)calloc(1, sizeof(DesktopSurface));
    s->width = 240;
    s->height = 320;
    if (attrib_list) {
        for (int i = 0; attrib_list[i] != EGL_NONE; i += 2) {
            if (attrib_list[i] == EGL_WIDTH) s->width = attrib_list[i+1];
            else if (attrib_list[i] == EGL_HEIGHT) s->height = attrib_list[i+1];
        }
    }
#ifdef _WIN32
    if (wglGetCurrentContext()) {
        ensure_surface_fbo(s);
    }
#elif defined(__APPLE__)
    if (CGLGetCurrentContext()) {
        ensure_surface_fbo(s);
    }
#endif
    return (EGLSurface)s;
}

EGLAPI EGLSurface EGLAPIENTRY eglCreateWindowSurface(EGLDisplay dpy, EGLConfig config, EGLNativeWindowType win, const EGLint *attrib_list) {
    return eglCreatePbufferSurface(dpy, config, attrib_list);
}

EGLAPI EGLSurface EGLAPIENTRY eglCreatePixmapSurface(EGLDisplay dpy, EGLConfig config, EGLNativePixmapType pixmap, const EGLint *attrib_list) {
    return eglCreatePbufferSurface(dpy, config, attrib_list);
}

EGLAPI EGLBoolean EGLAPIENTRY eglDestroySurface(EGLDisplay dpy, EGLSurface surface) {
    if (surface) {
        DesktopSurface *s = (DesktopSurface*)surface;
        if (s->fbo && pfn_glDeleteFramebuffers) {
            pfn_glDeleteFramebuffers(1, &s->fbo);
        }
        if (s->colorTex) {
            glDeleteTextures(1, &s->colorTex);
        }
        if (s->depthRbo && pfn_glDeleteRenderbuffers) {
            pfn_glDeleteRenderbuffers(1, &s->depthRbo);
        }
        if (s_currentSurface == s) {
            s_currentSurface = NULL;
        }
        free(s);
    }
    return EGL_TRUE;
}

EGLAPI EGLBoolean EGLAPIENTRY eglMakeCurrent(EGLDisplay dpy, EGLSurface draw, EGLSurface read, EGLContext ctx) {
#ifdef _WIN32
    if (!ctx) {
        wglMakeCurrent(NULL, NULL);
        s_currentSurface = NULL;
        return EGL_TRUE;
    }
    DesktopContext *c = (DesktopContext*)ctx;
    if (!wglMakeCurrent(c->hdc, c->hglrc)) {
        return EGL_FALSE;
    }
#elif defined(__APPLE__)
    if (!ctx) {
        CGLSetCurrentContext(NULL);
        s_currentSurface = NULL;
        return EGL_TRUE;
    }
    DesktopContext *c = (DesktopContext*)ctx;
    if (CGLSetCurrentContext(c->cgl) != kCGLNoError) {
        return EGL_FALSE;
    }
#endif

    init_gl_procs();

    if (draw) {
        DesktopSurface *s = (DesktopSurface*)draw;
        s_currentSurface = s;
        ensure_surface_fbo(s);
        if (s->fbo && pfn_glBindFramebuffer) {
            pfn_glBindFramebuffer(0x8D40 /* GL_FRAMEBUFFER */, s->fbo);
        }
        glViewport(0, 0, s->width, s->height);
    }
    return EGL_TRUE;
}

EGLAPI EGLBoolean EGLAPIENTRY eglQuerySurface(EGLDisplay dpy, EGLSurface surface, EGLint attribute, EGLint *value) {
    if (!surface || !value) return EGL_FALSE;
    DesktopSurface *s = (DesktopSurface*)surface;
    if (attribute == EGL_WIDTH) *value = s->width;
    else if (attribute == EGL_HEIGHT) *value = s->height;
    else if (attribute == EGL_CONFIG_ID) *value = 1;
    else *value = 0;
    return EGL_TRUE;
}

EGLAPI EGLBoolean EGLAPIENTRY eglQueryContext(EGLDisplay dpy, EGLContext ctx, EGLint attribute, EGLint *value) {
    if (value) *value = 1;
    return EGL_TRUE;
}

EGLAPI EGLint EGLAPIENTRY eglGetError(void) {
    return EGL_SUCCESS;
}

EGLAPI EGLBoolean EGLAPIENTRY eglSwapBuffers(EGLDisplay dpy, EGLSurface surface) {
    return EGL_TRUE;
}

EGLAPI EGLBoolean EGLAPIENTRY eglCopyBuffers(EGLDisplay dpy, EGLSurface surface, EGLNativePixmapType target) {
    return EGL_FALSE;
}

/* OpenGL ES 1.1 Fixed-point and Extension implementations */
GL_API void GL_APIENTRY glClearColorx(GLclampx red, GLclampx green, GLclampx blue, GLclampx alpha) {
    glClearColor(red / 65536.0f, green / 65536.0f, blue / 65536.0f, alpha / 65536.0f);
}

GL_API void GL_APIENTRY glClearDepthx(GLclampx depth) {
    glClearDepth(depth / 65536.0);
}

GL_API void GL_APIENTRY glColor4x(GLfixed red, GLfixed green, GLfixed blue, GLfixed alpha) {
    glColor4f(red / 65536.0f, green / 65536.0f, blue / 65536.0f, alpha / 65536.0f);
}

GL_API void GL_APIENTRY glFogxv(GLenum pname, const GLfixed *params) {
    if (pname == GL_FOG_MODE) {
        glFogi(GL_FOG_MODE, params[0]);
    } else if (pname == GL_FOG_COLOR) {
        GLfloat c[4];
        c[0] = params[0] / 65536.0f;
        c[1] = params[1] / 65536.0f;
        c[2] = params[2] / 65536.0f;
        c[3] = params[3] / 65536.0f;
        glFogfv(GL_FOG_COLOR, c);
    } else {
        glFogf(pname, params[0] / 65536.0f);
    }
}

GL_API void GL_APIENTRY glOrthox(GLfixed left, GLfixed right, GLfixed bottom, GLfixed top, GLfixed zNear, GLfixed zFar) {
    glOrtho(left / 65536.0, right / 65536.0, bottom / 65536.0, top / 65536.0, zNear / 65536.0, zFar / 65536.0);
}

GL_API void GL_APIENTRY glTexEnvx(GLenum target, GLenum pname, GLfixed param) {
    glTexEnvi(target, pname, param);
}

GL_API void GL_APIENTRY glTexParameterx(GLenum target, GLenum pname, GLfixed param) {
    glTexParameteri(target, pname, param);
}

GL_API void GL_APIENTRY glActiveTexture(GLenum texture) {
    if (pfn_glActiveTexture) pfn_glActiveTexture(texture);
}

GL_API void GL_APIENTRY glClientActiveTexture(GLenum texture) {
    if (pfn_glClientActiveTexture) pfn_glClientActiveTexture(texture);
}

GL_API void GL_APIENTRY glCompressedTexImage2D(GLenum target, GLint level, GLenum internalformat, GLsizei width, GLsizei height, GLint border, GLsizei imageSize, const void *data) {
    if (pfn_glCompressedTexImage2D) pfn_glCompressedTexImage2D(target, level, internalformat, width, height, border, imageSize, data);
}

GL_API void GL_APIENTRY glDepthRangef(GLclampf zNear, GLclampf zFar) {
    glDepthRange(zNear, zFar);
}
