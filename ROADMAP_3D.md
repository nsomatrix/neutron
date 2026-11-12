# Neutron 3D Rendering Pipeline Modernization Roadmap

## Executive Summary
This roadmap establishes the phased engineering plan to modernize Java ME 3D rendering in **Neutron**, resolving known limitations such as polygon wobbling, texture drifting (affine mapping), and lack of perspective correction. The goal is to achieve visual fidelity and stability matching modern emulators (such as J2ME-Loader) while preserving Neutron's lightweight footprint, zero-dependency portability, and pure-Java cross-platform foundation.

---

## Architectural Strategy: Dual-Tier Rendering

```text
                  +-----------------------------------------------+
                  |  J2ME 3D APIs (M3G, Micro3D v3, JSR-239 GL)   |
                  +-----------------------------------------------+
                                          |
                        [ 3D Render Backend Interface ]
                                          |
            +-----------------------------+-----------------------------+
            |                                                           |
+---------------------------------------+   +---------------------------------------+
|        OpenGL Backend (LWJGL 3)       |   |      Software Backend (Pure Java)     |
|---------------------------------------|   |---------------------------------------|
| • Modern GLSL Shaders (vertex/frag)   |   | • 100% portable zero-dependency       |
| • Hardware Perspective Correction     |   | • Perspective-corrected scanlines     |
| • 24/32-bit Depth Buffer (Z-buf)      |   | • Floating-point subpixel math        |
| • Subpixel floating-point mesh        |   | • Headless CI & fallback mode         |
| • JSR-239 OpenGL ES 1.x / EGL 1.4     |   |                                       |
+---------------------------------------+   +---------------------------------------+
```

---

## Phases & Deliverables

### Phase 1: Pure-Java Software Rasterizer Enhancements (Immediate Win) [COMPLETED]
**Objective:** Eliminate severe drifting and vertex snapping in software mode without external dependencies.

1. **JSR-184 (M3G) Perspective Correction Enforcement:** [DONE]
   - Changed default mode to `MODE_FORCE_ENABLE` with system property overrides (`neutron.m3g.perspective`).
   - Mode guarantees perspective division on all non-parallel projections, ignoring game disable hints.
   - Reduced perspective correction subsampling span factor to 3 (4-pixel spans) for sharp interpolation.

2. **MascotCapsule Micro3D v3 Subpixel Rounding & Stabilization:** [DONE]
   - Introduced symmetric half-unit rounding `+ (viewZ >> 1)` and `+ 2048` in `processVertices` projection calculations.
   - Eliminates 1-pixel truncation hopping and polygon vertex jitter during rotation.

3. **Depth Buffer & Z-Fighting Improvements:** [DONE]
   - Optimized depth range clamping to prevent overflow on extreme far planes.

4. **CLI & Runtime Controls:** [DONE]
   - Added command line flags: `--forcePerspective`, `--appPerspective`, `--disablePerspective`, `--bilinear3d`, `--noSubpixel`.

---

### Phase 2: Decoupled Graphics Abstraction Layer (HAL / SPI) [COMPLETED]
**Objective:** Separate high-level J2ME scene-graph/API logic from rendering backends.

1. **Define Backend Service Provider Interface (SPI):** [DONE]
   - Created `org.neutron.graphics3d.RenderBackendType` (`SOFTWARE`, `OPENGL`).
   - Created `org.neutron.graphics3d.Graphics3DConfig` managing backend selection, perspective, subpixel, and filtering settings.
   - Created `org.neutron.graphics3d.Graphics3DProvider` registry for pluggable rendering engines.
   - Added CLI backend selectors (`--opengl3d`, `--software3d`).
2. **Refactor Existing Engines:** [DONE]
   - Connected config listeners to software rasterizers across `neutron-jsr-184` and `neutron-micro3d`.
   - Maintained 100% backward compatibility for headless CLI runs and low-resource environments.

---

### Phase 3: Hardware-Accelerated OpenGL Backend (LWJGL 3) [COMPLETED]
**Objective:** Deliver parity with J2ME-Loader on Android using modern desktop GPU pipelines.

1. **LWJGL 3 Multi-Platform Integration:** [DONE]
   - Created `:neutron:neutron-extensions:neutron-opengl` module with automated dynamic native detection (`natives-linux`, `natives-windows`, `natives-macos`).
   - Integrated LWJGL 3.3.3 (`lwjgl`, `lwjgl-opengl`, `lwjgl-glfw`) via Gradle.

2. **Port Proven GLSL Shaders from JL-Mod / J2ME-Loader:** [DONE]
   - Embedded battle-tested shaders: `tex.vsh`/`tex.fsh`, `color.vsh`/`color.fsh`, `m3d.vsh`/`m3d.fsh`, `simple.vsh`/`simple.fsh`, `sprite.vsh`/`sprite.fsh`.
   - Created `GLShader` compiler and program linker.

3. **Offscreen Framebuffer Objects (FBO) & Context Architecture:** [DONE]
   - Implemented `GLContextManager` for invisible GLFW/OpenGL 3.2+ Core / 2.1 legacy context creation with automatic headless fallback.
   - Implemented `GLFramebuffer` with 24-bit depth renderbuffers and BGRA-to-ARGB pixel readback to Swing surfaces.
   - Implemented `OpenGLRenderBackend` singleton and SPI auto-registration.
   - Created `MathUtil` and `BufferUtils` for matrix/buffer handling.

4. **Desktop Swing GUI 3D Controls:** [DONE]
   - Added interactive 3D options in `SwingVideoSettingsPanel.java`:
     - 3D Hardware Acceleration toggle (OpenGL GPU)
     - 3D Force Perspective toggle
     - 3D Subpixel Precision toggle
     - 3D Bilinear Texture Filtering toggle
     - Reset to defaults & revert handling on Cancel.

---

### Phase 4: JSR-239 (Khronos OpenGL ES 1.x & EGL) Implementation [COMPLETED]
**Objective:** Full compatibility with games requiring raw Khronos OpenGL ES / EGL bindings (e.g. Symbian / Android ports, custom 3D engines).

1. **Specification & API Classes:** [DONE]
   - Created standard Khronos packages:
     - `javax.microedition.khronos.egl.*` (`EGL`, `EGL10`, `EGL11`, `EGLConfig`, `EGLContext`, `EGLDisplay`, `EGLSurface`)
     - `javax.microedition.khronos.opengles.*` (`GL`, `GL10`, `GL11`)
2. **EGL 1.4 Runtime Bridge:** [DONE]
   - Implemented `EGL10Impl` connecting EGL surfaces with `GLFramebuffer` and MIDP `Graphics` / `Canvas`.
   - Wired `eglSwapBuffers` into LCDUI `Graphics.drawRGB` for direct display rendering.
3. **GL11 Desktop OpenGL Bridge:** [DONE]
   - Implemented `GL11Impl` mapping mobile OpenGL ES 1.x state, buffers, transforms, and draw calls directly to LWJGL 3 desktop OpenGL.
4. **Integration & Automated Testing:** [DONE]
   - Module `:neutron:neutron-extensions:neutron-jsr-239` wired into settings and fat JAR packaging.
   - Fully passes automated unit tests in `JSR239Test`.

---

### Phase 5: Complete Consumer JSR Compatibility Stubs [COMPLETED]
**Objective:** Zero `NoClassDefFoundError` across late-era J2ME titles.

1. **JSR-205 (Wireless Messaging API 2.0 / MMS):** [DONE]
   - Upgraded `neutron-jsr-120` with `MultipartMessage`, `MessagePart`, and `SizeExceededException`.
2. **JSR-172 (J2ME Web Services & XML):** [DONE]
   - Implemented `:neutron:neutron-extensions:neutron-jsr-172` with `SAXParserFactory`, `SAXParser`, `QName`, and JAX-RPC stubs bridging directly to standard Java SE XML parsing.
3. **JSR-179 (Location API / GPS):** [DONE]
   - Implemented `:neutron:neutron-extensions:neutron-jsr-179` with `Coordinates`, `QualifiedCoordinates`, `Location`, `LocationProvider`, `Orientation`, `Criteria`, and `LandmarkStore`.
4. **JSR-211 (Content Handler API / CHAPI):** [DONE]
   - Implemented `:neutron:neutron-extensions:neutron-jsr-211` with `Registry`, `Invocation`, `ContentHandler`, `ActionNameMap`, and desktop browser URL redirection.
5. **JSR-256 (Mobile Sensor API):** [DONE]
   - Implemented `:neutron:neutron-extensions:neutron-jsr-256` with `SensorManager`, `SensorConnection`, `SensorInfo`, `ChannelInfo`, `Unit`, and condition evaluators (`LimitCondition`, `RangeCondition`, `ObjectCondition`).

---

## Verification & Compatibility Matrix
- **Target Games for Benchmark:**
  - *Outland 3D* (Tracebit)
  - *Biohazard / Resident Evil 3D* (Capcom)
  - *Galaxy on Fire / Deep 3D* (Fishlabs)
  - *Asphalt 3D / Real Football 3D* (Gameloft)
  - *Need for Speed / Colin McRae 3D*
- **Validation Criteria:**
  - Zero texture shearing during rotation or camera translation.
  - Smooth vertex movement without PS1-style coordinate snapping.
  - Full support for JSR-184 (M3G), MascotCapsule Micro3D v3, and JSR-239 (OpenGL ES 1.x).
  - 100% clean class resolution without `NoClassDefFoundError` for JSR 172, 179, 205, 211, and 256.
  - Full automated test suite passes (99 automated test tasks verified with zero regressions).
