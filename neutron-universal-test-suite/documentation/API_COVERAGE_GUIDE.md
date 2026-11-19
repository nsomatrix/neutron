# Neutron API Coverage & Specification Traceability Guide

## 1. Overview

Neutron implements a comprehensive subset of the Java ME specification ecosystem, spanning core configurations (CLDC 1.1), primary profiles (MIDP 2.0), optional JSR packages, and historical vendor OEM extensions.

This guide provides full traceability between the documented Java ME specifications, Neutron's 2,530 public API elements, and the test suites in the Universal Testing Framework.

---

## 2. Specification Coverage Breakdown

### 2.1 CLDC 1.1 (JSR-139) & Language Runtime
- **Specification**: JSR-139 Connected Limited Device Configuration 1.1.
- **Neutron Modules**: `neutron-cldc`, `neutron-javase`.
- **Target Packages**:
  - `java.lang`: `Object`, `Class`, `String`, `StringBuffer`, `System`, `Runtime`, `Thread`, `Runnable`, `Throwable`, `Exception`, `Error`, boxed primitives (`Integer`, `Long`, `Boolean`, `Byte`, `Short`, `Character`, `Float`, `Double`), `Math`.
  - `java.io`: `InputStream`, `OutputStream`, `ByteArrayInputStream`, `ByteArrayOutputStream`, `DataInput`, `DataOutput`, `DataInputStream`, `DataOutputStream`, `Reader`, `Writer`, `InputStreamReader`, `OutputStreamWriter`, `IOException`, `EOFException`.
  - `java.util`: `Vector`, `Hashtable`, `Enumeration`, `Stack`, `Random`, `Calendar`, `Date`, `TimeZone`, `Timer`, `TimerTask`.
- **Test Suite**: `test-midlets/cldc-runtime-conformance/` (`cldc-runtime-conformance.jar`).
- **Covered Capabilities**:
  - IEEE 754 32-bit float and 64-bit double arithmetic, NaN/Infinity propagation.
  - Multi-threaded synchronization, monitor locks, volatile memory semantics.
  - Exception hierarchies and stack unwinding.
  - Class initialization order and static initializer constraints.

### 2.2 MIDP 2.0 (JSR-118) Standard APIs
- **Specification**: JSR-118 Mobile Information Device Profile 2.0.
- **Neutron Modules**: `neutron-midp`.
- **Target Packages**:
  - `javax.microedition.midlet`: `MIDlet`, `MIDletStateChangeException`.
  - `javax.microedition.rms`: `RecordStore`, `RecordComparator`, `RecordFilter`, `RecordListener`, `RecordEnumeration`.
  - `javax.microedition.io`: `Connector`, `Connection`, `InputConnection`, `OutputConnection`, `StreamConnection`, `ContentConnection`, `HttpConnection`.
- **Test Suites**:
  - `test-midlets/midp-standard-apis/` (`midp-standard-apis.jar`)
  - `test-midlets/rms-storage-stress/` (`rms-storage-stress.jar`)
  - `test-midlets/gcf-networking/` (`gcf-networking.jar`)
- **Covered Capabilities**:
  - MIDlet lifecycle state transitions (`startApp`, `pauseApp`, `destroyApp`).
  - System property retrieval (`microedition.profiles`, `microedition.configuration`, `microedition.platform`).
  - Transactional record store creation, record iteration, record filtering, and in-place mutation.
  - HTTP connection header inspection, response parsing, and POST payload transmission.

### 2.3 LCDUI & 2D Graphics Engine
- **Specification**: JSR-118 Section 5 (LCDUI High-Level and Low-Level UI).
- **Neutron Modules**: `neutron-midp`, `neutron-javase-swing`.
- **Target Packages**:
  - `javax.microedition.lcdui`: `Display`, `Displayable`, `Canvas`, `Graphics`, `Image`, `Font`, `Form`, `List`, `TextBox`, `Alert`, `Choice`, `ChoiceGroup`, `TextField`, `StringItem`, `ImageItem`, `Gauge`, `Spacer`, `Command`, `CommandListener`, `Ticker`.
  - `javax.microedition.lcdui.game`: `GameCanvas`, `Sprite`, `TiledLayer`, `LayerManager`.
- **Test Suites**:
  - `test-midlets/lcdui-ui-components/` (`lcdui-ui-components.jar`)
  - `test-midlets/lcdui-graphics-canvas/` (`lcdui-graphics-canvas.jar`)
  - `test-midlets/game2d-engine-stress/` (`game2d-engine-stress.jar`)
- **Covered Capabilities**:
  - High-level form widget construction, item appending, layout positioning, and state change dispatch.
  - Low-level Canvas drawing primitives (lines, rectangles, rounded rects, arcs, polygons, RGB arrays).
  - Mutable vs immutable image instantiation, clipping boundaries, and coordinate translation.
  - Sprite animation frame sequencing, transformations (mirror, rotate 90/180/270), and collision detection.
  - Multi-layer composite scene management and scrolling tile grids.

### 2.4 Mobile 3D Graphics (M3G / JSR-184)
- **Specification**: JSR-184 Mobile 3D Graphics API for Java ME.
- **Neutron Modules**: `neutron-jsr-184`.
- **Target Packages**:
  - `javax.microedition.m3g`: `Graphics3D`, `World`, `Camera`, `Light`, `Mesh`, `Appearance`, `Material`, `CompositingMode`, `PolygonMode`, `VertexBuffer`, `VertexArray`, `TriangleStripArray`, `Transform`, `Texture2D`, `Image2D`, `Group`, `Node`, `AnimationController`, `KeyframeSequence`, `Loader`.
- **Test Suite**: `test-midlets/m3g-3d-graphics/` (`m3g-3d-graphics.jar`).
- **Covered Capabilities**:
  - 4x4 matrix mathematics (`postTranslate`, `postScale`, `postRotate`, `invert`, `transpose`).
  - Retained-mode scene graphs with hierarchical transforms, cameras, and directional lights.
  - Immediate-mode vertex buffer rendering with triangle strip primitives.
  - Material shading, diffuse coloration, alpha blending, and depth buffer testing.

### 2.5 MascotCapsule Micro3D v3 Engine
- **Specification**: Hi Corporation MascotCapsule Micro3D Version 3.
- **Neutron Modules**: `neutron-micro3d`.
- **Target Packages**:
  - `com.mascotcapsule.micro3d.v3`: `Graphics3D`, `Figure`, `FigureLayout`, `ActionTable`, `Effect3D`, `Texture`, `Vector3D`, `AffineTrans`, `Light`.
- **Test Suite**: `test-midlets/micro3d-graphics/` (`micro3d-graphics.jar`).
- **Covered Capabilities**:
  - 4096-unit fixed-point arithmetic (`AffineTrans.setIdentity`, `setRotationX`, `setRotationY`, `setRotationZ`).
  - 3D vector transformations (`AffineTrans.transPoint`, `transVector`, dot product, cross product).
  - Figure layout camera setup (`setPerspective`, `lookAt`, `setCenter`).
  - Directional and ambient lighting pipelines.

### 2.6 Mobile Media API (MMAPI / JSR-135)
- **Specification**: JSR-135 Mobile Media API 1.1.
- **Neutron Modules**: `neutron-mmapi`.
- **Target Packages**:
  - `javax.microedition.media`: `Manager`, `Player`, `PlayerListener`, `Control`, `Controllable`, `MediaException`.
  - `javax.microedition.media.control`: `VolumeControl`, `ToneControl`, `MIDIControl`, `StopTimeControl`.
- **Test Suite**: `test-midlets/mmapi-audio-media/` (`mmapi-audio-media.jar`).
- **Covered Capabilities**:
  - Synchronous hardware tone generation via `Manager.playTone`.
  - Tone sequence parsing and playback via `ToneControl` on `device://tone` locators.
  - Standard MIDI (Type 0/1) synthetic stream playback.
  - WAV header and PCM audio stream realization and player state transitions.

### 2.7 OEM Handset Extensions
- **Specifications**: Historical Nokia UI, Siemens MP, Samsung, Sony Ericsson APIs.
- **Neutron Modules**: `neutron-nokia-ui`, `neutron-siemens`, `neutron-samsung`.
- **Target Packages**:
  - `com.nokia.mid.ui`: `DirectGraphics`, `DirectUtils`, `FullCanvas`.
  - `com.nokia.mid.sound`: `Sound`.
  - `com.siemens.mp.game`: `Melody`, `MelodyComposer`, `Vibrator`, `ExtendedImage`.
  - `com.samsung.util`: `AudioClip`, `Vibration`.
- **Test Suite**: `test-midlets/oem-extensions/` (`oem-extensions.jar`).
- **Covered Capabilities**:
  - Nokia FullCanvas full-screen rendering without softkey margins.
  - Nokia DirectGraphics image flipping, rotation, and ARGB pixel access.
  - Siemens MP vibration control and extended image manipulation.
  - Samsung audio clip instantiation and haptic playback.

### 2.8 Optional JSR Packages
- **JSR-75**: FileConnection & PIM (`javax.microedition.io.file.FileSystemRegistry`, `FileConnection`).
- **JSR-82**: Bluetooth & OBEX (`javax.bluetooth.UUID`, `DiscoveryAgent`, `LocalDevice`).
- **JSR-172**: Web Services XML (`javax.xml.namespace.QName`, `javax.xml.parsers.SAXParserFactory`, `SAXParser`).
- **JSR-256**: Mobile Sensor API (`javax.microedition.sensor.SensorManager`).
- **Test Suite**: `test-midlets/optional-jsrs/` (`optional-jsrs.jar`).

---

## 3. Master Classification & Traceability Matrix

| Subsystem | Primary Specification | Package Root | Neutron Module | Test MIDlet Suite | Status |
| :--- | :--- | :--- | :--- | :--- | :--- |
| Core VM / Runtime | CLDC 1.1 (JSR-139) | `java.lang`, `java.io`, `java.util` | `neutron-cldc` | `cldc-runtime-conformance` | Verified |
| Application Lifecycle | MIDP 2.0 (JSR-118) | `javax.microedition.midlet` | `neutron-midp` | `midp-standard-apis` | Verified |
| High-Level LCDUI | MIDP 2.0 (JSR-118) | `javax.microedition.lcdui` | `neutron-midp` | `lcdui-ui-components` | Verified |
| Low-Level Canvas 2D | MIDP 2.0 (JSR-118) | `javax.microedition.lcdui` | `neutron-midp` | `lcdui-graphics-canvas` | Verified |
| 2D Game Engine | MIDP 2.0 (JSR-118) | `javax.microedition.lcdui.game` | `neutron-midp` | `game2d-engine-stress` | Verified |
| 3D Graphics (M3G) | JSR-184 | `javax.microedition.m3g` | `neutron-jsr-184` | `m3g-3d-graphics` | Verified |
| 3D Graphics (Micro3D) | MascotCapsule v3 | `com.mascotcapsule.micro3d.v3`| `neutron-micro3d`| `micro3d-graphics` | Verified |
| Audio & Media | MMAPI (JSR-135) | `javax.microedition.media` | `neutron-mmapi` | `mmapi-audio-media` | Verified |
| Networking (GCF) | CLDC / MIDP 2.0 | `javax.microedition.io` | `neutron-cldc` | `gcf-networking` | Verified |
| Record Storage (RMS) | MIDP 2.0 (JSR-118) | `javax.microedition.rms` | `neutron-midp` | `rms-storage-stress` | Verified |
| OEM Extensions | Nokia UI, Siemens | `com.nokia.mid.*`, `com.siemens.*` | `neutron-nokia-ui` | `oem-extensions` | Verified |
| Optional Packages | JSR-75, 82, 172, 256 | `javax.microedition.io.file`, etc. | `neutron-jsr-*` | `optional-jsrs` | Verified |
| Security Sandbox | MIDP 2.0 (JSR-118) | `javax.microedition.midlet` | `neutron-midp` | `security-sandbox` | Verified |
| Cross-API Integration | All Integrated | Integration Pipeline | All Modules | `cross-api-integration` | Verified |

