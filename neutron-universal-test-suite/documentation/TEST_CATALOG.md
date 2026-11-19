# Neutron Universal Test Suite Complete Catalog

## 1. Executive Summary

The Universal Test Suite consists of:
- **16 Self-Verifying Test MIDlet Suites** (`test-midlets/`)
- **69 Empirical Test Assertions** executed inside headless guest emulations
- **4 White-Box Host-Side Test Modules** (`host-tests/`)
- **5 Performance Micro-Benchmark Workloads** (`benchmarks/`)
- **1 Continuous Soak & Endurance Test Controller** (`endurance/`)
- **16 Visual Regression Frame Assertions** (`reference-images/`)

---

## 2. Test MIDlet Suites & Assertion Catalog

### Suite 01: `cldc-runtime-conformance` (`cldc-runtime-conformance.jar`)
- **Objective**: Evaluates core JVM bytecode execution, primitive math, memory allocation, and concurrency.
- **Assertions**:
  1. `CLDC-01`: Primitive Integer Arithmetic & Boundary Conditions (32-bit wrap, division, bitwise shifts).
  2. `CLDC-02`: IEEE 754 Floating-Point Precision (Float/Double math, sqrt, sin, cos).
  3. `CLDC-03`: Array Cloning, Slicing & Boundary Exceptions (`ArrayIndexOutOfBoundsException`).
  4. `CLDC-04`: Object Lifecycle, GC Allocation & Large Buffer Handling.
  5. `CLDC-05`: Class Hierarchy, Interface Polymorphism & Dynamic Dispatch.
  6. `CLDC-06`: Exception Propagation, Finally Blocks & Nested Try/Catch.
  7. `CLDC-07`: String Immutability, Concatenation & StringBuffer Manipulation.
  8. `CLDC-08`: CLDC Collections (`Vector`, `Hashtable`, `Stack`, `Enumeration`).
  9. `CLDC-09`: Multi-threaded Synchronization, Monitor Mutex & Volatile Memory Flags.

### Suite 02: `midp-standard-apis` (`midp-standard-apis.jar`)
- **Objective**: Validates MIDP 2.0 lifecycle management, system properties, and timers.
- **Assertions**:
  1. `MIDP-01`: MIDlet Lifecycle State Verification (`getAppProperty`, state queries).
  2. `MIDP-02`: Platform Properties Verification (`microedition.profiles`, `microedition.configuration`).
  3. `MIDP-03`: System Time Resolution & Monotonic Clock Progression (`System.currentTimeMillis`).
  4. `MIDP-04`: java.util.Timer & TimerTask Asynchronous Scheduling.
  5. `MIDP-05`: java.util.Random Deterministic Number Generation.

### Suite 03: `lcdui-ui-components` (`lcdui-ui-components.jar`)
- **Objective**: Verifies high-level LCDUI form controls, item appending, layout positioning, and user input widgets.
- **Assertions**:
  1. `LCDUI-01`: Display and Displayable Management (`Display.getDisplay`, `getCurrent`).
  2. `LCDUI-02`: Form Construction & StringItem Layout.
  3. `LCDUI-03`: TextField Input Modes (ANY, NUMERIC, PASSWORD) & Text Buffering.
  4. `LCDUI-04`: ChoiceGroup & List Components (EXCLUSIVE, MULTIPLE, POPUP).
  5. `LCDUI-05`: Gauge Control (CONTINUOUS_RUNNING, INCREMENTAL).
  6. `LCDUI-06`: Alert and AlertType Integration (INFO, WARNING, ERROR).

### Suite 04: `lcdui-graphics-canvas` (`lcdui-graphics-canvas.jar`)
- **Objective**: Low-level 2D graphics rendering pipeline, color spaces, drawing primitives, and coordinate clipping.
- **Assertions**:
  1. `GRAPH-01`: Color Manipulation & RGB Pixel Packing.
  2. `GRAPH-02`: Coordinate Translation & Bounding Box Clipping.
  3. `GRAPH-03`: Geometric Primitives (Lines, Rectangles, Rounded Rects, Arcs, Polygons).
  4. `GRAPH-04`: Font Metrics & Text Boundary Calculations (`Font.stringWidth`, `getHeight`).
  5. `GRAPH-05`: Mutable Image Creation & Graphics Surface Binding.
  6. `GRAPH-06`: ARGB Raw Integer Array Image Synthesis (`Image.createRGBImage`).

### Suite 05: `game2d-engine-stress` (`game2d-engine-stress.jar`)
- **Objective**: Validates `javax.microedition.lcdui.game` sprite animation, transformations, and tile grid layers.
- **Assertions**:
  1. `GAME-01`: Sprite Construction & Animation Frame Sequencing.
  2. `GAME-02`: Sprite Geometric Transformations (TRANS_MIRROR, TRANS_ROT90, TRANS_ROT180, TRANS_ROT270).
  3. `GAME-03`: Bounding Box & Pixel-Exact Sprite Collision Detection.
  4. `GAME-04`: TiledLayer Grid Construction & Cell Manipulation.
  5. `GAME-05`: LayerManager Multi-Layer Composition & Viewport Scrolling.

### Suite 06: `m3g-3d-graphics` (`m3g-3d-graphics.jar`)
- **Objective**: Evaluates Mobile 3D Graphics (JSR-184) scene graphs, matrix transforms, and immediate-mode rendering.
- **Assertions**:
  1. `M3G-01`: Graphics3D Singleton Instantiation & Capabilities Query.
  2. `M3G-02`: 4x4 Transform Matrix Computations (`postTranslate`, `postScale`).
  3. `M3G-03`: VertexBuffer & TriangleStripArray Geometry Construction.
  4. `M3G-04`: Appearance, Material Shading & CompositingMode Depth Testing.
  5. `M3G-05`: Retained-Mode Scene Graph (World, Active Camera, Directional Light, Mesh).
  6. `M3G-06`: Immediate-Mode Graphics3D Render Loop Execution.

### Suite 07: `micro3d-graphics` (`micro3d-graphics.jar`)
- **Objective**: Validates MascotCapsule Micro3D Version 3 fixed-point trigonometry, vector transforms, and figure layout.
- **Assertions**:
  1. `U3D-01`: AffineTrans Fixed-Point Matrix Computations (4096-unit coordinate scale).
  2. `U3D-02`: Vector3D Vector Math (Dot Product, Cross Product, Normalization).
  3. `U3D-03`: FigureLayout Camera Positioning & Perspective Projection.
  4. `U3D-04`: Effect3D Directional & Ambient Lighting Pipelines.
  5. `U3D-05`: Micro3D Graphics3D Frame Buffer Binding & Primitive Render Loop.

### Suite 08: `mmapi-audio-media` (`mmapi-audio-media.jar`)
- **Objective**: Evaluates Mobile Media API (JSR-135) audio playback, tone generation, and synthesizers.
- **Assertions**:
  1. `MMA-01`: Manager Supported Content Types & Protocol Query.
  2. `MMA-02`: Synchronous Hardware Tone Synthesis (`Manager.playTone`).
  3. `MMA-03`: Tone Sequence Player & ToneControl Realization (`device://tone`).
  4. `MMA-04`: Synthetic PCM WAV Header & Stream Realization.
  5. `MMA-05`: Synthetic Standard MIDI File (SMF Type 0) Stream Player.

### Suite 09: `gcf-networking` (`gcf-networking.jar`)
- **Objective**: Evaluates Generic Connection Framework (GCF) HTTP, TCP stream, and UDP datagram connections.
- **Assertions**:
  1. `GCF-01`: HttpConnection Header Parsing & Response Status Code Validation.
  2. `GCF-02`: Local TCP Loopback Stream Socket (`ServerSocketConnection` / `SocketConnection`).
  3. `GCF-03`: Local UDP Datagram Connection (`UDPDatagramConnection` / `Datagram`).

### Suite 10: `rms-storage-stress` (`rms-storage-stress.jar`)
- **Objective**: Validates Record Management System (RMS) persistence, record indexing, and iteration.
- **Assertions**:
  1. `RMS-01`: RecordStore Lifecycle (Open, Add, Read, Delete).
  2. `RMS-02`: RecordStore Enumeration & Multi-Record Traversal.
  3. `RMS-03`: Record Mutation & Dynamic Byte Reallocation.

### Suite 11: `oem-extensions` (`oem-extensions.jar`)
- **Objective**: Evaluates historical OEM vendor extensions (Nokia UI, Siemens MP, Samsung).
- **Assertions**:
  1. `OEM-01`: Nokia FullCanvas Initialization & Key Handler Dispatch.
  2. `OEM-02`: Nokia DirectGraphics Image Manipulation & ARGB Drawing.
  3. `OEM-03`: Nokia Sound Hardware Tone Generator.
  4. `OEM-04`: Siemens MP Vibrator Haptic Pulse.
  5. `OEM-05`: Samsung AudioClip Audio Interface.

### Suite 12: `optional-jsrs` (`optional-jsrs.jar`)
- **Objective**: Validates optional JSR specifications (JSR-75, 82, 172, 256).
- **Assertions**:
  1. `OPT-01`: JSR-75 FileConnection Root Enumeration (`FileSystemRegistry.listRoots`).
  2. `OPT-02`: JSR-82 Bluetooth UUID Operations (16-bit and 128-bit UUID parsing).
  3. `OPT-03`: JSR-172 SAX XML Parser Factory & QName Operations.
  4. `OPT-04`: JSR-256 SensorManager Hardware Query.

### Suite 13: `performance-benchmark` (`performance-benchmark.jar`)
- **Objective**: Benchmarks host-JIT execution performance, memory allocation, and rendering throughput.
- **Metrics Evaluated**:
  - Integer Arithmetic Throughput (`MOps/sec`)
  - Floating-Point Precision Math (`MFlops/sec`)
  - Heap Memory Allocation Rate (`MB/sec`)
  - 2D Canvas Drawing Operations (`KOps/sec`)
  - RMS Transactional Write Speed (`Records/sec`)

### Suite 14: `endurance-soak` (`endurance-soak.jar`)
- **Objective**: Tests extended operation stability, frame pacing at 60 FPS, and memory leak detection.
- **Monitored Telemetry**:
  - Continuous frame rate tracking (smoothing window)
  - Real-time heap memory usage
  - Watchdog heartbeat pulses

### Suite 15: `security-sandbox` (`security-sandbox.jar`)
- **Objective**: Evaluates security sandbox boundaries, permission query behavior, and crash safety.
- **Assertions**:
  1. `SEC-01`: MIDlet Permission Check Conformance (`checkPermission`).
  2. `SEC-02`: Malformed Image Binary Safe Rejection without Process Crash.
  3. `SEC-03`: Controlled Memory Exhaustion & Heap Recovery (`OutOfMemoryError`).

### Suite 16: `cross-api-integration` (`cross-api-integration.jar`)
- **Objective**: Evaluates interaction between multiple distinct Java ME APIs in a single pipeline.
- **Assertions**:
  1. `INT-01`: Audio Generation to RMS Record Serialization.
  2. `INT-02`: RMS Record Deserialization to 2D Graphic Surface Rendering.
  3. `INT-03`: 2D Image Surface Projection to 3D M3G Texture2D Pipeline.
  4. `INT-04`: High-Frequency Timer Repaint & GC Pressure Pipeline.

---

## 3. Host-Side White-Box Tests (`MasterHostTestSuite.java`)
- `HOST-01`: MIDletClassLoader Configuration & Delegation Rules.
- `HOST-02`: MascotCapsule Micro3D Fixed-Point Math Accuracy.
- `HOST-03`: RMS File and In-Memory Storage Isolation.
- `HOST-04`: Host JVM Memory Metrics & Allocation Headroom.

