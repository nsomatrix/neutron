# Nokia UI API Specification Summary

## 1. Scope
The Nokia UI API provides proprietary extensions for direct screen rendering, pixel-level manipulation, hardware backlight/vibration control, and audio effects across Nokia Series 40 and Series 60 devices.

## 2. Package `com.nokia.mid.ui`
- **`FullCanvas`** (extends `javax.microedition.lcdui.Canvas`):
  - Automatically occupies the full physical pixel dimensions of the display.
  - Eliminates title bars, command banners, and system softkey labels.
- **`DirectGraphics`**:
  - Obtained via `DirectUtils.getDirectGraphics(Graphics g)`.
  - Manipulations:
    - `TYPE_BYTE_1_GRAY`: 1 bit per pixel.
    - `TYPE_BYTE_2_GRAY`: 2 bits per pixel.
    - `TYPE_BYTE_4_GRAY`: 4 bits per pixel.
    - `TYPE_BYTE_8_GRAY`: 8 bits per pixel (grayscale).
    - `TYPE_USHORT_4444_ARGB`: 16-bit 4:4:4:4 ARGB.
    - `TYPE_USHORT_565_RGB`: 16-bit 5:6:5 RGB.
    - `TYPE_INT_8888_ARGB`: 32-bit 8:8:8:8 ARGB.
  - Rotations and Reflections:
    - `ROTATE_90`, `ROTATE_180`, `ROTATE_270`, `FLIP_HORIZONTAL`, `FLIP_VERTICAL`.
  - Method Operations:
    - `drawImage(Image img, int x, int y, int anchor, int manipulation)`.
    - `drawPixels(int[] pixels, boolean transparency, int offset, int scanlength, int x, int y, int width, int height, int manipulation, int format)`.
    - `drawPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor)`.
    - `fillPolygon(int[] xPoints, int xOffset, int[] yPoints, int yOffset, int nPoints, int argbColor)`.
    - `drawTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor)`.
    - `fillTriangle(int x1, int y1, int x2, int y2, int x3, int y3, int argbColor)`.
    - `setARGBColor(int argbColor)`.
- **`DirectUtils`**:
  - `getDirectGraphics(Graphics g)`: Bind DirectGraphics to graphics surface.
  - `createImage(int width, int height, int argbColor)`: Create mutable transparent image initialized with ARGB color.
- **`DeviceControl`**:
  - `setLights(int num, int level)`: Backlight control (num 0 = primary LCD, level 0-100).
  - `startVibra(int freq, long duration)`: Vibrate haptic motor for specified duration in ms.
  - `stopVibra()`: Immediately cease vibration.

