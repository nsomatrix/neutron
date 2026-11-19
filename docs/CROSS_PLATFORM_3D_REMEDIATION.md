# Cross-Platform 3D J2ME Graphics Engine Remediation Specification

**Document Version:** 1.0.0  
**Target Architecture:** Neutron J2ME Emulation Runtime (`neutron-jsr-184`, `neutron-opengl`, `neutron-jsr-239`, `neutron-javase-swing`)  
**Supported Platforms:** Linux (`x86_64`, `aarch64`), Windows (`x86_64`, `x86`, `arm64`), macOS (`x86_64`, `arm64` Apple Silicon)  
**Status:** Approved for Implementation  

---

## 1. Executive Summary & Problem Statement

### 1.1 Context
In the Neutron J2ME emulator, 3D graphics (JSR-184 Mobile 3D Graphics / M3G and JSR-239 OpenGL ES 1.1) execute smoothly on Linux host environments (both under desktop X11/Wayland and headless Mesa software rasterization). However, on Windows and macOS host operating systems, 3D J2ME games encounter catastrophic failures, presenting either as fatal startup crashes (`UnsatisfiedLinkError`, Cocoa main thread aborts) or complete rendering failure (black screens, zero-sized buffers, or silent fallback failures).

### 1.2 Root Cause Summary
Through rigorous multi-platform binary inspection, C/C++ native source auditing, and Java ME bytecode flow tracking, sixteen distinct architectural, native, and lifecycle barriers were identified. These span:
1. **Dynamic Linker Topology:** Missing Mach-O `LC_LOAD_DYLIB` linkages to system OpenGL frameworks on macOS.
2. **Context Lifecycle & Driver ABI:** Windows WGL extension pointer caching before active context binding, rendering FBO allocation non-functional.
3. **Thread Concurrency:** macOS Cocoa/AppKit main-thread initialization constraints violated by background MIDlet execution threads.
4. **Filesystem URI Parsing:** Windows absolute file path drive letters (`C:\...`) misrouted to network URI handlers (`getHttpInputStream()`).
5. **Shader & Profile Divergence:** Driver rejection of GLSL 1.20 and OpenGL 3.2 Compatibility Profiles on Apple Silicon/macOS drivers.
6. **Toolchain & ABI Coverage:** Missing native binaries for Windows 32-bit and Windows ARM64 architectures.

---

## 2. Comprehensive Compatibility Matrix (The 16 Defect Classes)

| ID | Component | Defect Description | Impacted OS | Severity | Root Cause |
|---|---|---|---|---|---|
| **DEF-01** | `javam3g` Mach-O | Missing `LC_LOAD_DYLIB` to `OpenGL.framework` | macOS (x86_64 / arm64) | **Blocker** | Zig cross-compilation without `-framework OpenGL` produces dylib lacking runtime linkage to Apple OpenGL driver. |
| **DEF-02** | `m3g_desktop_gl.c` | Premature `init_gl_procs()` in `eglCreatePbufferSurface` | Windows (x86_64 / x86 / arm64) | **Blocker** | `wglGetProcAddress()` returns `NULL` without active WGL context. Pointer cache marked `inited = 1` permanently with null pointers. |
| **DEF-03** | `m3g_desktop_gl.c` | Non-current GL context during FBO allocation | Windows / macOS | **Major** | Surface FBO (`s->fbo == 0`) never allocated; rendering falls back to dummy 16x16 window framebuffer. |
| **DEF-04** | `Loader.java` | Drive letter colon (`C:\`) misclassified as URI | Windows | **Blocker** | Line 858 `name.indexOf(':') != -1` routes local absolute paths to `Connector.open()`, throwing `ConnectionNotFoundException`. |
| **DEF-05** | `GLContextManager.java` | Background thread GLFW initialization on Cocoa | macOS | **Blocker** | Cocoa requires main/initial thread for windowing subsystems. Throws `GLFW_PLATFORM_ERROR` or causes JVM abort. |
| **DEF-06** | `GLContextManager.java` | Requesting 3.2 Compatibility Profile | macOS | **Major** | Apple drivers reject `GLFW_OPENGL_COMPAT_PROFILE` for OpenGL 3.2+. Context creation fails completely. |
| **DEF-07** | `shaders/tex.vsh/.fsh` | GLSL 120 shader syntax under Core Profile | macOS / Windows Core | **Major** | `attribute` / `varying` invalid under modern Core Profiles. Shaders must target standard 2.1 or dynamic #version. |
| **DEF-08** | Native Bundles | Absence of Windows x86 (32-bit) and ARM64 DLLs | Windows (x86 / arm64) | **Major** | Missing architecture DLLs cause `UnsatisfiedLinkError: no javam3g in java.library.path`. |
| **DEF-09** | `Platform.java` | Incomplete Windows ARM64 arch detection & ASR tempdir | Windows | **Major** | Detects `aarch64` as `windows-x86_64`. Enterprise Windows ASR blocks DLL execution from `%TEMP%`. |
| **DEF-10** | `build-all-natives.sh`| Missing `@rpath/libjavam3g.dylib` `LC_ID_DYLIB` | macOS | **Minor** | Mach-O identity omitted, leading to dyld resolution ambiguity when relocated. |
| **DEF-11** | Win32 Calling ABI | JNIEXPORT and stdcall name decoration mismatch | Windows (x86 32-bit) | **Major** | Stack misalignment or unresolved exports if `__stdcall` vs `__cdecl` decorations differ. |
| **DEF-12** | Dummy Window DC | `ChoosePixelFormat` without software fallback flags | Windows | **Minor** | Headless or basic display adapters fail `wglCreateContext` without software fallback pixel format descriptors. |
| **DEF-13** | Framebuffer Depth | Unsized `GL_DEPTH_COMPONENT` attachment | macOS / Intel Win32 | **Minor** | Incomplete FBO status on drivers requiring sized `GL_DEPTH_COMPONENT16` / `GL_DEPTH_COMPONENT24`. |
| **DEF-14** | CI Pipeline | Testing exclusively on `ubuntu-latest` with Mesa | Windows / macOS | **Major** | Zero regression detection for Windows and macOS native loading and 3D pipelines in CI. |
| **DEF-15** | `Graphics3D.java` | Target texture and surface state invalidation | Cross-platform | **Minor** | Retained target textures not cleared on surface recreation, risking stale frame presentation. |
| **DEF-16** | Display Scaling | Virtual canvas to physical viewport scaling | macOS / Windows High-DPI | **Minor** | High-DPI backing scale factor mismatch causing viewport cropping or black borders. |

---

## 3. Detailed Root Cause Analysis & Architectural Solutions

### 3.1 macOS: Dynamic Linker & Cocoa Context Architecture

#### A. Mach-O Framework Linkage (DEF-01, DEF-10)
- **Mechanism:** Zig provides cross-compilation for Darwin (`x86_64-macos`, `aarch64-macos`), but Zig's bundled sysroot does not include Apple's closed-source proprietary framework stubs. When compiling `libjavam3g.dylib` with `-Wl,-undefined,dynamic_lookup`, symbols such as `_glEnable`, `_glViewport`, `_CGLCreateContext` are left unlinked. At runtime, macOS `dyld` does not bind them to `/System/Library/Frameworks/OpenGL.framework/Versions/A/OpenGL`.
- **Remediation:** During cross-compilation on Linux, generate a minimal Mach-O framework stub with `install_name: /System/Library/Frameworks/OpenGL.framework/Versions/A/OpenGL` and link targets using `-F<framework_dir> -framework OpenGL -Wl,-install_name,@rpath/libjavam3g.dylib`.

#### B. Cocoa Thread Affiliation & Profile Hints (DEF-05, DEF-06, DEF-07)
- **Mechanism:** GLFW on macOS interfaces with `NSApplication` and `NSWindow`. In AWT/Swing applications, Cocoa's event loop runs on the main thread. Invoking `glfwInit()` from a Java worker thread triggers an assertion failure. Furthermore, requesting OpenGL 3.2 with `GLFW_OPENGL_COMPAT_PROFILE` is rejected by Apple's OpenGL framework (macOS only supports OpenGL 2.1 legacy or 3.2+ Core Profile).
- **Remediation:** In `GLContextManager.java`:
  1. On macOS, detect the OS and request an OpenGL 2.1 legacy context (skipping the invalid 3.2 Compatibility Profile hint) which natively executes GLSL 1.20 shaders (`tex.vsh` and `tex.fsh`).
  2. Surround GLFW initialization with defensive fallback: if GLFW fails or is unavailable on macOS headless/worker threads, cleanly route to software/headless fallback without aborting the JVM.

---

### 3.2 Windows: WGL Lifecycle, FBO Allocation & Path Handling

#### A. WGL Extension Pointers & Deferred FBO Init (DEF-02, DEF-03, DEF-12, DEF-13)
- **Mechanism:** Under the Win32 WGL model:
  1. `wglGetProcAddress()` queries the driver's current ICD. If no OpenGL context is current on the calling thread (`wglGetCurrentContext() == NULL`), it returns `NULL`.
  2. In `m3g_desktop_gl.c`, `init_gl_procs()` was called inside `eglCreatePbufferSurface()` before `eglMakeCurrent()` was called. Because no context was active, all FBO function pointers (`pfn_glGenFramebuffers`, `pfn_glBindFramebuffer`, `pfn_glActiveTexture`) remained `NULL`.
  3. `inited = 1` was permanently set, preventing any future re-initialization.
  4. Surface creation failed to allocate `s->fbo` (`s->fbo == 0`).
  5. Rendering subsequently wrote to the default 16x16 dummy window DC, and `glReadPixels()` read back uninitialized memory or black rectangles.
- **Remediation:**
  1. In `init_gl_procs()`, only mark initialized if an active OpenGL context exists (`wglGetCurrentContext() != NULL` or `CGLGetCurrentContext() != NULL`) and `pfn_glGenFramebuffers != NULL`.
  2. Refactor surface FBO allocation into a deferred helper `ensure_surface_fbo(DesktopSurface *s)` invoked inside `eglMakeCurrent()` immediately after `wglMakeCurrent()` activates the context.
  3. Ensure explicit sized depth renderbuffers (`GL_DEPTH_COMPONENT16` / `GL_DEPTH_COMPONENT24`) and verify FBO completeness with `glCheckFramebufferStatus`.

#### B. Windows Filesystem Path Routing in Loader (DEF-04)
- **Mechanism:** In `javax.microedition.m3g.Loader.java`:
  ```java
  private InputStream getInputStream(String name) throws IOException {
      if (name.indexOf(':') != -1) {
          return getHttpInputStream(name);
      }
      ...
  ```
  On Windows, any local path containing a drive letter (e.g., `C:\assets\model.m3g`) contains a colon at index 1. `Loader` treats this as an absolute URI and routes it to `Connector.open("C:\\assets\\model.m3g")`, which fails with `ConnectionNotFoundException`.
- **Remediation:**
  1. Add `isWindowsDrivePath(String name)` checking `length >= 2 && Character.isLetter(charAt(0)) && charAt(1) == ':'`.
  2. If a Windows drive path or a `file:` URL is detected, bypass HTTP network loading and route to local filesystem stream opening with fallback to ClassLoader resource streams.

#### C. Windows Architecture Matrix & Enterprise ASR (DEF-08, DEF-09, DEF-11)
- **Mechanism:** Modern Windows deployments on ARM64 and legacy 32-bit Windows systems lack native binaries in `resources/natives/`. Furthermore, corporate Windows environments with Attack Surface Reduction (ASR) rules block DLL loading from `%TEMP%`.
- **Remediation:**
  1. Cross-compile `natives/windows-x86/javam3g.dll` (`x86-windows-gnu`) and `natives/windows-arm64/javam3g.dll` (`aarch64-windows-gnu`).
  2. Update `Platform.java` to distinguish `windows-arm64` from `windows-x86_64`.
  3. Provide secondary DLL extraction to `%USERPROFILE%\.neutron\natives\` if extracting to `%TEMP%` throws a `SecurityException` or `UnsatisfiedLinkError`.

---

## 4. Engineering Implementation Tasks

- [x] **Phase 1: Architecture Specification & Audit Documentation**
  - Author comprehensive markdown remediation specification (`docs/CROSS_PLATFORM_3D_REMEDIATION.md`).
  - Publish structured UI artifact for real-time progress visibility.
- [ ] **Phase 2: JSR-184 Java Path & Loader Remediation**
  - Update `javax/microedition/m3g/Loader.java` with Windows drive letter detection and local file stream resolution.
  - Update `javax/microedition/m3g/Platform.java` with `windows-arm64` architecture mapping and resilient ASR directory fallback.
- [ ] **Phase 3: C/C++ Native OpenGL & EGL Subsystem Hardening**
  - Update `neutron-jsr-184/src/main/cpp/m3g_desktop_gl.c`:
    - Context currency verification before function pointer caching in `init_gl_procs()`.
    - Deferred FBO creation via `ensure_surface_fbo()` in `eglMakeCurrent()`.
    - Proper FBO attachment cleanup in `eglDestroySurface()` with context safety.
    - Sized depth renderbuffer attachments and completeness validation.
- [ ] **Phase 4: Toolchain & Cross-Compilation Pipeline Enhancement**
  - Automate macOS Mach-O OpenGL framework stub generation in `build-all-natives.sh`.
  - Add `windows-x86` (32-bit) and `windows-arm64` compilation steps with proper flags.
  - Link macOS targets against `-F... -framework OpenGL` and `-Wl,-install_name,@rpath/libjavam3g.dylib`.
  - Recompile and refresh all bundled binaries across `linux-x86_64`, `windows-x86_64`, `windows-x86`, `windows-arm64`, `macos-x86_64`, and `macos-arm64`.
- [ ] **Phase 5: LWJGL / GLFW Context Manager & Shader Profile Alignment**
  - Update `GLContextManager.java` to handle macOS profile constraints (legacy 2.1 vs 3.2 Core) and non-main thread GLFW resilience.
- [ ] **Phase 6: Multi-Platform Verification & Continuous Integration**
  - Run local test suite (`./gradlew test`).
  - Verify Mach-O load commands and PE32/PE32+ export tables with diagnostic scripts.
  - Enhance `.github/workflows/build-all.yml` with multi-OS matrix testing (Linux, Windows, macOS).

---

## 5. Verification & Acceptance Criteria
1. **macOS Binary Acceptance:** `otool -L` / Mach-O load command parser confirms `LC_LOAD_DYLIB` pointing to `/System/Library/Frameworks/OpenGL.framework/Versions/A/OpenGL` on both x86_64 and arm64 dylibs.
2. **Windows Binary Acceptance:** PE headers confirm valid export tables with `Java_javax_microedition_m3g_*` symbols across x86_64, x86, and arm64 DLLs.
3. **Execution Parity:** 3D J2ME test suites (`GLM3GBackendTest`, `JSR184Test`, `JSR239Test`) execute cleanly without `UnsatisfiedLinkError` or `ConnectionNotFoundException`.
4. **Zero Regression:** Existing Linux software rendering and headless test performance remain 100% intact.

