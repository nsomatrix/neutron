# MascotCapsule Micro3D v3 Specification Summary

## 1. Scope
MascotCapsule Micro3D v3 (HI Corporation) is a lightweight 3D graphics rendering engine designed specifically for Japanese i-mode and worldwide feature phones (Sony Ericsson, Motorola, LG).

## 2. Core Architecture
- **Immediate-Mode Context (`com.mascotcapsule.micro3d.v3.Graphics3D`)**:
  - `bind(Graphics g)`: Bind 2D graphics canvas for 3D output.
  - `release(Graphics g)`: Unbind rendering surface.
  - `renderFigure(Figure figure, int x, int y, FigureLayout layout, Effect3D effect)`: Render 3D skeletal figure with bone posture, lighting, and camera projection.
  - `renderPrimitives(Texture texture, int x, int y, FigureLayout layout, Effect3D effect, int command, int numPrimitives, int[] vertexData, int[] normalData, int[] textureData, int[] colorData)`: Low-level polygon and strip rendering.
  - `flush()`: Flush rendered primitives to the bound display buffer.
- **Skeletal Models (`Figure`)**:
  - Loaded from proprietary binary `.mbac` stream via `new Figure(String name)` or `new Figure(byte[] b)`.
  - Maintains bone hierarchy, skin weights, texture coordinates.
  - `setTexture(Texture texture)`: Apply active texture map.
  - `setPosture(ActionTable action, int actionIdx, int frame)`: Deform bones to match specific frame of an action.
- **Bone Animation (`ActionTable`)**:
  - Loaded from proprietary binary `.mtac` stream via `new ActionTable(String name)` or `new ActionTable(byte[] b)`.
  - Contains keyframed rotations, translations, and scale matrices for figure bones.
  - `getNumActions()`: Returns count of actions in table.
  - `getNumFrames(int action)`: Returns frame count for a specific action.
- **Spatial Transformation & Math**:
  - `AffineTrans`: 3x3 transformation matrix + 3D translation vector.
  - `Vector3D`: Unit and 3D position vector math (`unit`, `dot`, `cross`).
  - `FigureLayout`: Controls parallel/perspective projection, viewpoint, scaling, orientation, and screen center offset.
- **Lighting & Shading (`Effect3D`)**:
  - Directional light vector, directional intensity, ambient light intensity.
  - Spherical environment mapping for chrome / reflective surface effects.
  - Shading flags: Flat shading, Gouraud shading, Toon shading.

