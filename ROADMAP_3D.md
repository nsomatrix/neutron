# Neutron 3D Rendering Pipeline Modernization Roadmap & Specification

## Executive Summary
This document establishes the authoritative, phased engineering specification to modernize the 3D graphics rendering pipeline in **Neutron**. The goal is to surpass the performance, visual stability, and responsiveness of modern emulators (**J2ME-Loader** and **KEmulator**) by leveraging a modern desktop OpenGL Core Profile pipeline (via **LWJGL 3**) while maintaining clean architectural decoupling, zero-cost surface access, and pure-Java software fallback for headless CI.

---

## Architectural Specification: Decoupled Dual-Tier HAL

```text
                  +───────────────────────────────────────────────+
                  |         Standard Java ME 3D Client APIs       |
                  |     (M3G JSR-184 / MascotCapsule Micro3D v3)  |
                  +───────────────────────────────────────────────+
                                          │
                                          ▼
                  +───────────────────────────────────────────────+
                  |       Graphics3D Hardware Abstraction Layer   |
                  |       (org.neutron.graphics3d.spi Interfaces) |
                  +───────────────────────────────────────────────+
                                          │
                Runtime Dispatch: Graphics3DConfig.getBackendType()
                                          │
                    ┌─────────────────────┴─────────────────────┐
                    ▼                                           ▼
+───────────────────────────────────────+   +───────────────────────────────────────+
│       Modern Desktop OpenGL Core      │   │      Pure-Java Software Fallback      │
│        (:neutron-opengl / LWJGL 3)    │   │         (Headless / Safe CI)          │
+───────────────────────────────────────+   +───────────────────────────────────────+
│ • GLSL 1.20 / 3.30+ Core Shaders      │   │ • 100% portable pure Java             │
│ • VBO/VAO Geometry & Attribute Caches │   │ • DDA scanline triangle rasterizer    │
│ • Hardware Matrix / MVP Projections   │   │ • Subsampled perspective correction   │
│ • 24/32-bit Depth Buffer (Z-Buffer)   │   │ • Fixed-point subpixel rounding       │
│ • Hardware Texture Filtering & Mipmaps│   │ • Safe deterministic CI test runner   │
│ • Asynchronous Dual PBO Pixel DMA     │   │                                       │
+───────────────────────────────────────+   +───────────────────────────────────────+
```

---

## Detailed Phases & Milestones

### Phase 1: Architectural Foundation & Zero-Cost Surface Access [COMPLETED]
**Objective:** Eliminate reflection in hot loops and establish type-safe SPI contracts between API modules and rendering backends.

1. **Type-Safe SPI Contracts (`org.neutron.graphics3d.spi`):**
   - Defined `Micro3DBackend` with explicit methods for figure rendering, primitives, command lists, and lifecycle management (`beginFrame`, `endFrame`, `flush`).
   - Defined `M3GBackend` with explicit methods for submesh rendering, world traversal, camera/light binding, and target binding.
   - Refactored `Graphics3DProvider` in `:neutron:neutron-javase` to provide type-safe registration and discovery without runtime reflection.

2. **Direct Frame Surface Access (`DirectFrameSurface`):**
   - Implemented `DirectFrameSurface` in `neutron-javase` and adopted it across `J2SEDisplayGraphics` and `J2SEGraphicsSurface`.
   - Replaced dynamic reflection (`getMethod("getGraphicsSurface")`, `getMethod("getImageData")`) in `Graphics3D.bindTarget()` with compile-time interface casts, eliminating reflection and allocation overhead per frame.

---

### Phase 2: Hardware-Accelerated MascotCapsule Micro3D Engine [COMPLETED]
**Objective:** Deliver high-performance MascotCapsule Micro3D execution (*Resident Evil: Degeneration*, *Ridge Racer*, *Armored Core*, *Silent Hill Mobile*) with seamless dual-tier hardware acceleration and pure-Java software fallback.

1. **Dual-Tier MascotCapsule Micro3D Architecture:**
   - Restored Roman Lahin's battle-tested pure-Java software rasterizer (`Rasterizer.java`, `DummyCanvas.java`, `MascotME.java`) providing 100% specification compatibility across all platforms (Linux, macOS, Windows, Headless CI).
   - Fully supports all MascotCapsule primitives (Points, Lines, Triangles, Quads, Point Sprites), ActionTable bone tables, spherical environment mapping, toon shading, and command lists.
   - Wired `Graphics3DProvider` automatic backend discovery for desktop LWJGL 3 OpenGL acceleration (`GLMicro3DBackend`).
   - Fixed framebuffer clearing and alpha compositing in `GLMicro3DBackend` to eliminate solid black overlays during 2D/3D interleaving.

---

### Phase 3: High-Performance M3G (JSR-184) Pipeline [COMPLETED]
**Objective:** Deliver robust, specification-compliant 3D rendering for all JSR-184 titles (*Galaxy on Fire*, *Asphalt 3/4*, *Rally Master Pro*, *Need for Speed*, *Fishlabs* titles).

1. **Native Reference C99 Engine Integration (`libjavam3g`):**
   - Built on Nokia's authoritative C99 JSR-184 engine with OpenGL ES 1.1 / desktop GL rasterization.
   - Fixed backbuffer validation (`contentsValid` based on `M3G_OVERWRITE_BIT`) to eliminate stale frames and ghosting artifacts.
   - Restored true transparent clears (`0x00000000`) and preserved user ARGB alpha channels in `m3g_background.c` and `m3g_rendercontext.c`.
   - Added standard `CMakeLists.txt` for cross-platform builds across Linux, macOS, and Windows.

2. **Zero-Allocation JNI Surface Bridge:**
   - Replaced dynamic reflection in `Graphics3D.bindTarget()` with compile-time `DirectFrameSurface` access.
   - Implemented persistent, cached off-heap `DirectBuffer` and Java `int[]` memory pools in `Graphics3D.java`, eliminating per-frame GC pauses.
   - Added automatic 2D backdrop uploading when `OVERWRITE` is not set, enabling seamless 2D HUD and 3D scene compositing.

---

### Phase 4: Zero-Stall Frame Presentation & Dual Asynchronous PBO DMA [COMPLETED]
**Objective:** Eliminate GPU pipeline sync stalls and memory bandwidth bottlenecks during frame presentation.

1. **Dual Asynchronous Pixel Buffer Objects (PBOs):**
   - Replaced blocking `glReadPixels()` in `GLFramebuffer.java` with ping-pong dual PBO DMA transfers (`GL21.GL_PIXEL_PACK_BUFFER`):
     - Frame $N$: Dispatch non-blocking `glReadPixels(..., 0L)` into active PBO.
     - Frame $N-1$: Map the alternate completed PBO (`glMapBuffer(..., GL_READ_ONLY)`) directly to memory.
   - Implemented row-by-row inverted mapping to eliminate secondary pixel array copy passes.

---

### Phase 5: Thread Decoupling & Event Loop Responsiveness [COMPLETED]
**Objective:** Prevent 3D rendering from freezing the MIDP event dispatch loop or stalling keyboard/touch inputs.

1. **Lock Contention Elimination:**
   - In `SwingDisplayComponent.java`, narrowed the monitor lock on `this` so it only protects surface reference swapping.
   - Removed monitor locking during the execution of `deviceDisplay.paintDisplayable()`, preventing MIDP render loops from starving the AWT Event Dispatch Thread (EDT).
   - Eliminated input latency and UI freeze during high-framerate 3D rendering.

---

### Phase 6: Visual Modernization & High-DPI Upscaling [COMPLETED]
**Objective:** Deliver visual fidelity and rendering features beyond both KEmulator and J2ME-Loader.

1. **Internal Super-Resolution Scaling:**
   - Added `internalScale` (1x, 2x, 4x) in `Graphics3DConfig` and `GLFramebuffer`.
   - Supported rendering 3D geometries at high resolutions (e.g., 960x1280 Retina) while cleanly downsampling or presenting without geometric aliasing.
2. **True 24/32-bit Depth Buffer:**
   - Upgraded depth renderbuffer attachment to `GL_DEPTH_COMPONENT24` to eliminate Z-fighting and polygon flickering.
3. **Hardware Filtering & PBO Controls:**
   - Exposed unified configurations for bilinear texture filtering, asynchronous PBO DMA, subpixel precision, and perspective division.

---

## Verification & Compatibility Matrix
- **Automated Regression Testing:** 100% of existing tests must pass across all Gradle modules.
- **Headless Safety:** The test runner and headless CLI must operate seamlessly without native GLFW display errors.
- **Target Profiles:**
  - JSR-184 (M3G) Racing, Flight, and Action Titles
  - MascotCapsule Micro3D v3 3D Action & Adventure Titles
  - JSR-239 OpenGL ES 1.x Symbian/Android Ports
