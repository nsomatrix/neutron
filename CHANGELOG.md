# Changelog - Neutron J2ME Platform Emulator

All notable changes, defect remediations, architectural hardening, and test enhancements delivered for the **1.5.0 Production Release**.

---

## [1.5.0] - 2026-11-19

### Added
- **Universal Test Suite**: Added 26 custom-built MIDlet test applications covering all 22 Phase 11 functional requirements:
  - `cldc-runtime-conformance`: CLDC 1.1 floating point, math, strings, JVM object model.
  - `midp-standard-apis`: MIDlet lifecycle, system properties, platform requests.
  - `lcdui-graphics-canvas`: 2D rasterization, RGB arrays, transforms, clipping, fonts.
  - `lcdui-ui-components`: Forms, StringItem, TextField, Gauge, ChoiceGroup, Commands.
  - `game2d-engine-stress`: Sprite collision, animated frames, TiledLayer, LayerManager.
  - `micro3d-graphics`: MascotCapsule Micro3D v3 fixed-point math and 3D projection.
  - `m3g-3d-graphics`: JSR-184 M3G scene graphs, materials, lights, cameras, animations.
  - `mmapi-audio-media`: JSR-135 tone generation, WAV audio, player controls.
  - `nokia-oem-apis`: Nokia DirectGraphics, DirectUtils, Sound, DeviceControl.
  - `siemens-oem-apis`: Siemens ExtendedImage, Melody, Melodizer, Vibration, Light.
  - `jsr82-bluetooth`: Bluetooth discovery, UUID normalization, service records.
  - `jsr120-messaging`: Wireless Messaging API SMS and binary datagrams.
  - `jsr256-sensors`: Mobile Sensor API channels, conditions, and listeners.
  - `jsr211-content-handler`: CHAPI application invocation and registration.
  - `gcf-networking`: Generic Connection Framework HTTP, TCP sockets, datagrams.
  - `rms-storage-stress`: RecordStore transactions, filtering, comparators.
  - `threading-concurrency`: Monitor synchronization, wait/notify, deadlocks.
  - `memory-resource-stress`: High-frequency allocations, GC pressure, heap boundaries.
  - `cross-api-integration`: Combined LCDUI + RMS + MMAPI + GCF workflows.
  - `endurance-soak`: Paced 60 FPS long-duration rendering and stability soak.
  - `api-boundary-exceptions`: Strict null checks, boundary parameters, exception types.
  - `game-regression-suite`: Real-world game emulation patterns and loops.
  - `oem-extensions`: Sony Ericsson, Motorola, Samsung compatibility shims.
  - `optional-jsrs`: JSR-172, JSR-179, JSR-226, JSR-234, JSR-239.
  - `security-sandbox`: Classloader isolation and path traversal prevention.
  - `performance-benchmark`: MOps/sec, MFlops/sec, allocation rate, draw ops/sec.
- **Headless Test Infrastructure**: Implemented `org.neutron.app.Headless` backbuffer frame dumping to PNG for automated CI test execution without an X11/Wayland display server.
- **Automated Harnesses**:
  - `run_universal_test_suite.py`: Master test runner with automated result matrix generation.
  - `run_visual_regression.py`: Automated PNG frame buffer visual integrity analyzer.
  - `run_benchmarks.py`: Compute, memory, canvas, and RMS throughput benchmarks.
  - `run_host_tests.py`: Host-side white-box tests.
  - `endurance-harness.py`: Soak and memory degradation watchdog.

### Fixed & Remediated
- **JSR-82 Bluetooth (`javax.bluetooth.UUID`)**:
  - Remediated dummy stub where `toString()` returned `null` and `equals()` returned `false`.
  - Implemented complete, specification-compliant UUID parser supporting 16-bit, 32-bit, and 128-bit hexadecimal strings, base UUID promotion (`00000000-0000-1000-8000-00805F9B34FB`), equals, and hashCode.
- **Nokia UI DirectGraphics (`DirectGraphicsImp`)**:
  - Corrected `getNativePixelFormat()` return value from obsolete `TYPE_BYTE_1_GRAY` to `TYPE_INT_8888_ARGB` to accurately reflect the 32-bit color rendering pipeline.
- **Nokia UI Sound (`com.nokia.mid.sound.Sound`)**:
  - Implemented lifecycle states (`SOUND_PLAYING`, `SOUND_STOPPED`, `SOUND_UNINITIALIZED`).
  - Added gain persistence and clamping (0..255) in `setGain(int)` and `getGain()`.
- **MMAPI Media Manager (`javax.microedition.media.Manager`)**:
  - Implemented `getSupportedProtocols(String contentType)` to return supported protocols (`device`, `capture`, `http`, `https`, `file`, `jar`) instead of an empty array.
- **MIDlet Lifecycle (`javax.microedition.midlet.MIDlet`)**:
  - Implemented `resumeRequest()` to notify the runtime bridge to invoke `startApp()`.
- **RMS RecordStore (`javax.microedition.rms.RecordStore`)**:
  - Enforced strict authorization mode validation in `openRecordStore()` and `setMode()`, throwing `IllegalArgumentException` on invalid values.
  - Added record store name length and null checks for multi-suite access.
- **LCDUI Command (`javax.microedition.lcdui.Command`)**:
  - Stored `longLabel` constructor argument and returned it via `getLongLabel()`.
- **LCDUI StringItem (`javax.microedition.lcdui.StringItem`)**:
  - Implemented `setFont(Font)` storage and return from `getFont()`.
  - Forwarded `setPreferredSize()` to parent item.
- **LCDUI TextBox (`javax.microedition.lcdui.TextBox`)**:
  - Forwarded `setTicker(Ticker)` to `super.setTicker()`.

### Security
- Verified strict sandboxing in `MIDletClassLoader`.
- Enforced canonical path validation in `FileConnection` and `RecordStore` preventing `../` directory traversal attacks.
