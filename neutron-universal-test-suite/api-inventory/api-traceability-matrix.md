# API-to-Test Traceability Matrix

This matrix maps every supported specification, subsystem, and package in Neutron directly to its dedicated testing suite, test MIDlet, host-side white-box test class, and verification method.

| Subsystem | Specification / JSR | Package / Classes | Test Suite ID | Test MIDlet Name | Host-Side Test Class | Verification Type |
|---|---|---|---|---|---|---|
| **JVM Runtime & Bytecode** | CLDC 1.1 (JSR-139) | Bytecode opcodes, types, float math, arrays, exceptions | `TS-CLDC-01` | `CldcRuntimeConformanceMIDlet` | `BytecodeExecutionHostTest` | Functional, edge cases, numeric overflow |
| **CLDC Core Libraries** | CLDC 1.1 (JSR-139) | `java.lang`, `java.io`, `java.util` | `TS-MIDP-01` | `MidpStandardApisMIDlet` | `MIDletClassLoaderTest` | Unit, functional, collections, streams |
| **Concurrency & Timers** | CLDC 1.1 (JSR-139) | `Thread`, `Runnable`, `Timer`, `TimerTask`, monitors | `TS-CLDC-01` | `CldcRuntimeConformanceMIDlet` | `MIDletTimerTest` | Deadlock detection, synchronization |
| **Application Lifecycle** | MIDP 2.0 (JSR-118) | `javax.microedition.midlet.MIDlet` | `TS-MIDP-01` | `MidpStandardApisMIDlet` | `MIDletContextLifecycleHostTest`| Pause, resume, destroy, properties |
| **High-Level LCDUI** | MIDP 2.0 (JSR-118) | `Form`, `List`, `TextBox`, `Alert`, `ChoiceGroup`, `Item` hierarchy | `TS-LCDUI-01` | `LcduiUiComponentsMIDlet` | `ItemsOnFormTest`, `AlertTest` | Layout, traversal, commands, validation |
| **Low-Level LCDUI Canvas** | MIDP 2.0 (JSR-118) | `Canvas`, `Graphics`, `Image`, `Font` | `TS-CANVAS-01`| `LcduiGraphicsCanvasMIDlet` | `J2SEDisplayGraphicsTest`, `FontTest` | Primitives, clipping, font metrics, keys |
| **Game API 2D Engine** | MIDP 2.0 (JSR-118) | `GameCanvas`, `Sprite`, `TiledLayer`, `LayerManager` | `TS-GAME2D-01`| `Game2dEngineStressMIDlet` | `SpriteTest`, `TiledLayerTest` | Pixel collision, scrolling, frame pacing |
| **3D Graphics (M3G)** | JSR-184 (M3G 1.1) | `javax.microedition.m3g.*` | `TS-M3G-01` | `M3g3dGraphicsMIDlet` | `M3GTest`, `GLM3GBackendTest` | Chunk parser, scene graph, lighting |
| **3D Graphics (Micro3D)** | MascotCapsule v3 | `com.mascotcapsule.micro3d.v3.*` | `TS-M3D-01` | `Micro3dGraphicsMIDlet` | `Micro3DTest`, `GLMicro3DBackendTest`| Pure-Java rasterizer, .mbac/.mtac |
| **MMAPI Audio Engine** | JSR-135 (MMAPI 1.1)| `javax.microedition.media.*` | `TS-MMAPI-01` | `MmapiAudioMediaMIDlet` | `AudioPlayerListenerTest`, `ToneGenerationTest`| WAV, MIDI, AMR, tone synthesis |
| **3D Positional Audio** | JSR-234 (AMMS) | `javax.microedition.amms.*` | `TS-MMAPI-01` | `MmapiAudioMediaMIDlet` | `GlobalManagerTest` | Spectator 3D positioning, equalizers |
| **GCF Networking** | CLDC 1.1 / MIDP 2.0| `HttpConnection`, `SocketConnection`, `DatagramConnection` | `TS-GCF-01` | `GcfNetworkingMIDlet` | `HttpConnectionTest`, `ServerSocketConnectionTest` | Loopback HTTP, TCP, UDP, timeouts |
| **Persistent Storage (RMS)**| MIDP 2.0 (JSR-118) | `javax.microedition.rms.*` | `TS-RMS-01` | `RmsStorageStressMIDlet` | `FileRecordStoreManagerTest`, `MemoryRecordStoreManagerTest` | Enumeration, comparator, persistence |
| **Nokia UI Extensions** | Proprietary | `com.nokia.mid.ui.*`, `FullCanvas`, `DirectGraphics` | `TS-OEM-01` | `OemExtensionsMIDlet` | `DirectGraphicsTest` | 32-bit ARGB drawPixels, rotation |
| **Siemens Extensions** | Proprietary | `com.siemens.mp.io.*`, `com.siemens.mp.game.*` | `TS-OEM-01` | `OemExtensionsMIDlet` | `SiemensFileTest` | Filesystem, vibrator, lights |
| **OEM Handset APIs** | Moto, SE, Samsung | Backlight, Vibration, Sound | `TS-OEM-01` | `OemExtensionsMIDlet` | `OemApisTest` | Hardware stubs and lifecycle |
| **JSR-75 FileConnection** | JSR-75 | `javax.microedition.io.file.*` | `TS-OPT-01` | `OptionalJsrsMIDlet` | `FileConnectionTest` | Virtual roots, stream read/write |
| **JSR-82 Bluetooth/OBEX** | JSR-82 | `javax.bluetooth.*`, `javax.obex.*` | `TS-OPT-01` | `OptionalJsrsMIDlet` | `JSR82APIDeclarationsTest` | Device discovery, OBEX headers |
| **JSR-120 Messaging** | JSR-120 (WMA 1.1) | `javax.wireless.messaging.*` | `TS-OPT-01` | `OptionalJsrsMIDlet` | `JSR120MessagingHostTest` | Text/binary SMS, connection URLs |
| **JSR-172 Web Services** | JSR-172 | `javax.xml.parsers.*`, `javax.xml.rpc.*` | `TS-OPT-01` | `OptionalJsrsMIDlet` | `JSR172Test` | SAX XML parsing, schema validation |
| **JSR-179 Location API** | JSR-179 | `javax.microedition.location.*` | `TS-OPT-01` | `OptionalJsrsMIDlet` | `JSR179Test` | Coordinate feeds, landmark queries |
| **JSR-211 CHAPI** | JSR-211 | `javax.microedition.content.*` | `TS-OPT-01` | `OptionalJsrsMIDlet` | `JSR211Test` | Invocation handlers, URL handlers |
| **JSR-226 SVG Mobile** | JSR-226 | `javax.microedition.m2g.*` | `TS-OPT-01` | `OptionalJsrsMIDlet` | `M2GTest` | W3C SVG Tiny 1.1 vector paths |
| **JSR-239 OpenGL ES** | JSR-239 | `javax.microedition.khronos.egl.*`, `opengles.*`| `TS-OPT-01`| `OptionalJsrsMIDlet` | `JSR239Test` | EGL context initialization |
| **JSR-256 Sensor API** | JSR-256 | `javax.microedition.sensor.*` | `TS-OPT-01` | `OptionalJsrsMIDlet` | `JSR256Test` | SensorManager, channel conditions |
| **Multi-API Integration**| Cross-Cutting | 2D + 3D + MMAPI + RMS + Threads | `TS-INT-01` | `CrossApiIntegrationMIDlet` | `CrossApiHostIntegrationTest` | Concurrency, memory ownership, stability |
| **Performance Benchmark**| Performance | CPU math, 2D sprites, 3D triangles, allocation rate | `TS-BENCH-01`| `PerformanceBenchmarkMIDlet` | `EngineThroughputHostBenchmark` | FPS, latency percentiles, throughput |
| **Endurance & Soak** | Stability | Continuous rendering, audio looping, memory leaks | `TS-SOAK-01` | `EnduranceSoakMIDlet` | `LongDurationSoakHostTest` | Leak detection, degradation tracking |
| **Security & Sandbox** | Security | Class isolation, buffer overruns, exceptions | `TS-SEC-01` | `SecuritySandboxMIDlet` | `SecurityIsolationHostTest` | Exception safety, bounds checks |

