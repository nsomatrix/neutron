# JSR-184 Mobile 3D Graphics (M3G 1.1) Specification Summary

## 1. Scope
JSR-184 defines a high-performance 3D graphics API for mobile devices with hardware acceleration or efficient software rendering.

## 2. Core Architecture
- **Retained Mode Scene Graph**:
  - `Object3D`: Base class for all 3D scene elements; user ID, user object attachment, duplication.
  - `World`: Root of scene graph, active camera, ambient background.
  - `Node`: Base node with parent, child, bounding box, rendering flags, alignment.
  - `Group`: Internal branch node holding child nodes.
  - `Camera`: Perspective and parallel projection, field of view, aspect ratio, near/far clipping planes.
  - `Light`: Ambient, directional, omnidirectional (point), and spotlight sources.
  - `Mesh` / `SkinnedMesh` / `MorphingMesh`: Polygonal geometry linked to `VertexBuffer` and `IndexBuffer` arrays, bone skeleton weighting, morph targets.
  - `Sprite3D`: 2D billboards positioned in 3D coordinate space.
  - `Background`: Solid color or 2D image backdrop, clipping mode.
- **Immediate Mode Pipeline**:
  - `Graphics3D.getInstance()`: Singleton rendering context.
  - `bindTarget(Object target)`: Bind rendering surface (`Graphics`, `Image2D`).
  - `releaseTarget()`: Finalize frame.
  - `render(World world)`: Render retained scene graph.
  - `render(VertexBuffer vb, IndexBuffer ib, Appearance app, Transform transform)`: Render immediate geometry.
- **Material, Shading & Texturing**:
  - `Appearance`: Holds `Material`, `PolygonMode`, `CompositingMode`, `Fog`, `Texture2D`.
  - `Material`: Ambient, diffuse, specular, emissive colors, shininess.
  - `Texture2D`: 2D texture with wrap modes (`WRAP_CLAMP`, `WRAP_REPEAT`), filtering (`FILTER_BASE_LEVEL`, `FILTER_LINEAR`, `FILTER_NEAREST`), blending (`FUNC_MODULATE`, `FUNC_DECAL`, `FUNC_REPLACE`, `FUNC_BLEND`, `FUNC_ADD`).
  - `CompositingMode`: Alpha blending (`ALPHA`, `ALPHA_ADD`), depth testing, depth writing, color writing.
  - `PolygonMode`: Culling (`CULL_BACK`, `CULL_FRONT`, `CULL_NONE`), shading (`SHADE_FLAT`, `SHADE_SMOOTH`), winding (`WINDING_CCW`, `WINDING_CW`), two-sided lighting.
  - `Fog`: Linear or exponential distance fog with density and color.
- **Animation System**:
  - `AnimationController`: Playback speed, position, weight, active time intervals.
  - `AnimationTrack`: Connects property target (translation, rotation, scale, color, morph weights) to `KeyframeSequence`.
  - `KeyframeSequence`: Keyframe time points and vector values with linear or spline interpolation.
- **Binary File Format (`.m3g`)**:
  - Zlib deflate stream with Adler-32 header verification.
  - Chunk ID definitions: Section (0), ObjectTable (1), VertexArray (2), KeyframeSequence (3), AnimationTrack (4), AnimationController (5), MorphingMesh (6), SkinnedMesh (7), Texture2D (8), Sprite3D (9), Mesh (10), Group (11), Camera (12), Light (13), Material (14), VertexBuffer (15), Appearance (16), CompositingMode (17), PolygonMode (18), TriangleStripArray (19), Background (20), World (21).
  - Loaded via `Loader.load(String name)` or `Loader.load(byte[] data, int offset)`.

