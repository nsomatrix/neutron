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
**Objective:** Deliver locked 60+ FPS in MascotCapsule Micro3D titles (*Resident Evil: Degeneration*, *Ridge Racer*, *Armored Core*, *Silent Hill Mobile*) with zero CPU rasterization.

1. **OpenGL Micro3D Engine Implementation:**
   - Implemented desktop LWJGL 3 OpenGL Core Profile pipeline in `:neutron:neutron-extensions:neutron-opengl` under `org.neutron.graphics3d.opengl.micro3d.GLMicro3DBackend`.
   - Wired vertex and fragment GLSL shaders (`tex.vsh`/`tex.fsh`, `color.vsh`/`color.fsh`) supporting ambient, directional, toon, and specular shading.
   - Supported MascotCapsule primitive rendering: Triangles (C3/T3), Quads (C4/T4), Lines, and Points.
   - Implemented skeletal bone transformation matrix evaluation, vertex attribute unpacking, and sphere environment mapping.
   - Implemented `GLTextureCache` for GPU texture recycling and wrapping.

2. **SPI Hookup into `neutron-micro3d`:**
   - In `com.mascotcapsule.micro3d.v3.Graphics3D`, bound `Micro3DBackend` with seamless fallback to Roman Lahin's software `Rasterizer.java` when in headless or CI environments.

---

### Phase 3: Hardware-Accelerated M3G (JSR-184) Pipeline [COMPLETED]
**Objective:** Full GPU offloading for all JSR-184 titles (*Galaxy on Fire*, *Asphalt 3/4*, *Rally Master Pro*, *Need for Speed*, *Fishlabs* titles).

1. **OpenGL M3G Backend (`GLM3GBackend`):**
   - Created `GLM3GBackend` implementing `M3GBackend` in `:neutron:neutron-extensions:neutron-opengl`.
   - Implemented GPU vertex and index buffer streaming (`vtxBuffer`, `texCoordBuffer`, `indexBuffer`).
   - Implemented hardware compositing blend modes (`REPLACE`, `ALPHA`, `ALPHA_ADD`), depth testing, and face culling.
   - Built GLSL texture and color shaders with GPU viewport projection.

2. **SPI Hookup into `neutron-jsr-184`:**
   - Routed `javax.microedition.m3g.Graphics3D.bindTarget()`, `clear()`, `setViewport()`, `setDepthRange()`, `renderSubmesh()`, and `releaseTarget()` directly to `GLM3GBackend`.
   - Handled zero-copy surface extraction from `DirectFrameSurface`, `Image2D`, `javax.microedition.lcdui.Image`, and `javax.microedition.lcdui.Graphics`.

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
