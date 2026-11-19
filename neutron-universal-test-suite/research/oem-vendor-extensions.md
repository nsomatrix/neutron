# OEM and Proprietary Java ME Handset Extensions Reference

## 1. Nokia UI API

### 1.1 Overview & History
The Nokia UI API was created by Nokia to overcome the conservative limitations of MIDP 1.0/2.0. Because early MIDP canvases had mandatory softkey command bars and lacked direct pixel access, Nokia provided proprietary classes that became the de facto standard across billions of Series 40 and Series 60 devices worldwide.

### 1.2 Packages and Classes
- **Package**: `com.nokia.mid.ui`
  - `FullCanvas`: A full-screen canvas that eliminates all system command bars and status banners, allowing MIDlets to draw directly on every pixel of the physical screen. (Predecessor to MIDP 2.0 `setFullScreenMode`).
  - `DirectGraphics`: Direct frame-buffer manipulation interface obtained via `DirectUtils.getDirectGraphics(Graphics g)`.
    - Features:
      - `drawImage(Image image, int x, int y, int anchor, int manipulation)`: 8-way rotation/reflection (Rotate 90, 180, 270, Mirror, Mirror+Rotate).
      - `drawPixels(int[] pixels, boolean transparency, int offset, int scanlength, int x, int y, int width, int height, int manipulation, int format)`: High-performance 32-bit ARGB / 16-bit RGB 565 raw pixel blitting.
      - `drawPixels(short[] pixels, ...)` / `drawPixels(byte[] pixels, ...)`
      - `drawPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor)`
      - `fillPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor)`
      - `drawTriangle(...)`, `fillTriangle(...)`
      - `setARGBColor(int argbColor)`
  - `DirectUtils`: Factory class to create mutable images with alpha transparency (`createImage(width, height, argbColor)`) and get `DirectGraphics`.
  - `DeviceControl`: Hardware control interface for:
    - `setLights(int num, int level)`: Backlight control.
    - `startVibra(int freq, long duration)` / `stopVibra()`: Haptic feedback.
- **Package**: `com.nokia.mid.sound`
  - `Sound`: Monophonic / polyphonic sound playback with pitch, duration, and volume.
  - `SoundListener`: State change callback listener.

### 1.3 Neutron Implementation
Implemented in `neutron-nokiaui`. Neutron provides full 32-bit ARGB `drawPixels` support, rotation/mirroring algorithms, polygon rendering, mutable transparent image creation, and vibration/light handlers.

---

## 2. MascotCapsule Micro3D v3 (HI Corporation)

### 2.1 Overview & History
MascotCapsule Micro3D v3 (developed by HI Corporation, Japan) was the dominant commercial 3D graphics engine deployed on Japanese (DoCoMo i-mode), European, and North American handsets (Sony Ericsson K700/K750/K800/W810, Motorola E398/V3, LG, Sharp). Countless iconic 3D mobile games (Ridge Racer, Biohazard 3D, Sonic 3D) relied on Micro3D v3 rather than (or prior to) JSR-184.

### 2.2 Packages and Classes
- **Package**: `com.mascotcapsule.micro3d.v3`
  - `Graphics3D`: Core immediate-mode rendering context (`renderFigure`, `renderPrimitives`, `flush`).
  - `Figure`: 3D skeletal mesh loaded from proprietary binary `.mbac` files. Contains bone hierarchies, vertex weights, texture coordinates, and polygon lists.
  - `ActionTable`: Skeletal bone animation table loaded from proprietary binary `.mtac` files. Provides keyframed skeletal animation frames for `Figure.setPosture()`.
  - `Texture`: High-performance 2D texture map loaded from `.bmp`, `.png`, or raw `.tra` binary texture assets.
  - `FigureLayout`: Controls projection matrix, viewpoint, scale, center, and coordinate space for figure rendering.
  - `AffineTrans`: 3x3 orientation matrix plus translation vector for 3D spatial transformations (`setIdentity`, `setRotationX`, `setRotationY`, `setRotationZ`, `multiply`, `lookAt`).
  - `Vector3D`: 3D vector entity supporting cross-product, dot-product, normalization, and magnitude operations.
  - `Effect3D`: Environmental rendering effects (directional light, ambient light, spherical environment mapping, shading).
  - `Light`: Configurable directional and ambient light source.
  - `Util3D`: Coordinate projection math and trigonometric lookup utilities.

### 2.3 Neutron Implementation
Neutron features a pure-Java software rasterizer (`neutron-micro3d`) that directly decodes `.mbac` figure streams and `.mtac` action tables, evaluates skeletal bone posture matrices, and rasterizes perspective-correct textured polygons without requiring external native libraries.

---

## 3. Siemens Mobility Extension API

### 3.1 Packages and Classes
- **Package**: `com.siemens.mp.io`
  - `File`: Native filesystem access on Siemens 55/65/75 series phones prior to JSR-75 standardization. Operations: `open`, `read`, `write`, `close`, `delete`, `spaceAvailable`.
  - `Connection`: Siemens-specific serial and infrared connection endpoints.
- **Package**: `com.siemens.mp.game`
  - `GraphicObject`: Hardware-accelerated 2D graphic objects with collision detection.
  - `Light`: Direct screen backlight manipulation.
  - `Vibrator`: Device haptic motor activation.
  - `Sound`: Siemens ringtone melody player.

### 3.2 Neutron Implementation
Implemented in `neutron-siemensapi`. Maps `com.siemens.mp.io.File` onto Neutron's virtual file storage infrastructure and exposes hardware stubs for vibration and backlight.

---

## 4. Sony Ericsson Extension APIs

### 4.1 Packages and Features
- **Package**: `com.sonyericsson.media` & `com.sonyericsson.util`
  - Camera flash control, stereo widening, speakerphone toggles, and dedicated game keys (Gaming Keys A and B on W550, W900, W995).
- **Neutron Implementation**: Implemented in `neutron-oem-apis`.

---

## 5. Motorola Extension APIs

### 5.1 Packages and Features
- **Package**: `com.motorola.multimedia` & `com.motorola.funlight`
  - FunLight API: Controls rhythmic LED keyboard illumination and exterior casing lights.
  - Dual-screen / external flip-display control.
- **Neutron Implementation**: Implemented in `neutron-oem-apis`.

---

## 6. Samsung Java Extensions

### 6.1 Packages and Features
- **Package**: `com.samsung.util`
  - `AudioClip`: Proprietary Samsung audio melody player.
  - `Vibration`: Haptic motor activation.
  - `LCDLight`: Screen backlight duration controller.
- **Neutron Implementation**: Implemented in `neutron-oem-apis`.

