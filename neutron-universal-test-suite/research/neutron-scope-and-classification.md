# Neutron Java ME Classification & Scope Matrix

As required by the Universal Testing Framework specification, this document establishes the formal 6-tier classification of all Java ME technologies, configurations, profiles, and JSRs with respect to the **Neutron** emulator.

---

## Tier 1: Required by Neutron's Declared Target Platform

These specifications form the non-negotiable core contract of Neutron:
- **CLDC 1.1 (JSR-139)**:
  - Bytecode execution engine with full HotSpot/GraalVM JIT acceleration.
  - 32-bit integer, 64-bit long arithmetic, IEEE 754 floating-point operations (`float`, `double`).
  - Standard language libraries: `java.lang`, `java.io`, `java.util`.
  - Generic Connection Framework core: `javax.microedition.io.Connector`.
- **MIDP 2.0 (JSR-118)**:
  - MIDlet lifecycle (`startApp`, `pauseApp`, `destroyApp`, `notifyDestroyed`).
  - LCDUI High-Level: `Display`, `Displayable`, `Form`, `List`, `TextBox`, `Alert`, `Item` hierarchy (`TextField`, `DateField`, `Gauge`, `StringItem`, `ImageItem`, `ChoiceGroup`, `Spacer`, `CustomItem`).
  - LCDUI Low-Level: `Canvas`, `Graphics`, `Image`, `Font`, key input dispatching, pointer/touch input.
  - Game API: `GameCanvas`, `Sprite`, `TiledLayer`, `LayerManager`.
  - Record Management System (RMS): `RecordStore`, `RecordEnumeration`, `RecordFilter`, `RecordComparator`, `RecordListener`.
  - Standard GCF Connection types: `HttpConnection`, `HttpsConnection`, `SocketConnection`, `ServerSocketConnection`, `DatagramConnection`.

---

## Tier 2: Implemented by Neutron and Requiring Verification

Technologies fully implemented in Neutron's codebase requiring rigorous functional and stress test verification:
- **MascotCapsule Micro3D v3 (`com.mascotcapsule.micro3d.v3.*`)**:
  - Pure-Java software 3D rasterizer.
  - Binary `.mbac` figure parsing, `.mtac` action table animation.
  - Lighting, directional, ambient, spherical environment mapping, perspective projection.
- **JSR-184 (Mobile 3D Graphics - M3G 1.1)**:
  - Binary `.m3g` chunk stream parser (Zlib deflate, Adler-32 integrity).
  - Retained scene graph (`World`, `Node`, `Camera`, `Light`, `Mesh`, `SkinnedMesh`, `VertexBuffer`, `IndexBuffer`, `Appearance`, `Material`, `Texture2D`).
  - Keyframe animation tracks and controllers.
  - Immediate mode rendering (`Graphics3D.render`).
- **Nokia UI DirectGraphics (`com.nokia.mid.ui.*`)**:
  - `DirectGraphics` interface, `DirectUtils`.
  - 32-bit ARGB `drawPixels` blitting, 8-way rotations, transparent masks, polygon/triangle rendering.
  - `FullCanvas` fullscreen canvas.
  - `DeviceControl` (backlight and vibration).
- **JSR-135 (Mobile Media API - MMAPI 1.1)**:
  - Java Sound MIDI polyphonic looping synthesizer.
  - Sampled audio playback (PCM WAV, Sun AU).
  - AMR-NB native mobile voice decoding.
  - Frequency tone synthesis (`Manager.playTone`).
  - Controls: `VolumeControl`, `ToneControl`, `MIDIControl`, `StopTimeControl`.
- **JSR-75 (FileConnection & PIM)**:
  - Virtual filesystem directory structure (`root/`, `memory_card/`).
  - Path traversal protection (`../`).
  - Stream I/O and root enumeration.
- **JSR-234 (Advanced Multimedia Supplements - AMMS)**:
  - 3D audio spectator positioning, reverberation presets, parametric equalizers.
- **JSR-226 (Scalable 2D Vector Graphics - SVG M2G)**:
  - W3C SVG Tiny 1.1 document parsing, path drawing, and affine viewBox transformations.
- **OEM APIs**:
  - Siemens Extension API (`com.siemens.mp.io.File`, `com.siemens.mp.game.*`).
  - Sony Ericsson (`com.sonyericsson.media.*`).
  - Motorola (`com.motorola.multimedia.*`).
  - Samsung (`com.samsung.util.*`).

---

## Tier 3: Partially Implemented by Neutron

Technologies present in the codebase but requiring boundary testing to identify implementation limits:
- **JSR-82 (Bluetooth & OBEX)**: API signatures and basic local/remote device contexts implemented; physical Bluetooth radio hardware pass-through is simulated.
- **JSR-120 (Wireless Messaging API - WMA 1.1)**: Message structures, SMS connection URIs implemented; real cellular carrier transmission simulated via local queue.
- **JSR-172 (Web Services / XML SAX)**: SAX XML parsing fully operational; SOAP RPC stub serialization partially implemented.
- **JSR-179 (Location API)**: Coordinates, Criteria, LocationProvider implemented with mock coordinate feeds; hardware NMEA GPS pass-through not implemented.
- **JSR-211 (Content Handler API - CHAPI)**: Registry and invocation data structures present; external OS MIME routing partially stubbed.
- **JSR-239 (OpenGL ES 1.0/1.1 Java Binding)**: Desktop OpenGL (JOGL/LWJGL) wrapper bridging implemented; software fallback handles basic calls.
- **JSR-256 (Mobile Sensor API)**: SensorManager and Channel condition data structures implemented with mock synthetic feeds.

---

## Tier 4: Unsupported but Potentially Relevant

Technologies not implemented in Neutron but relevant for broader Java ME compatibility:
- **MIDP 2.1 Inter-MIDlet Communication**: Complete shared memory domain between distinct MIDlet suites.
- **JSR-205 (Wireless Messaging API 2.0)**: Advanced MMS multipart multimedia messaging over cellular network.
- **JSR-238 (Internationalization API)**: Handset locale resource bundle compiler and localized text formatting.
- **JSR-287 (Scalable 2D Vector Graphics 2.0)**: SVG Tiny 1.2 vector text and interactive path animation.

---

## Tier 5: Outside Neutron's Intended Scope

Technologies intentionally excluded from Neutron due to architectural divergence:
- **Connected Device Configuration (CDC)**: CDC 1.0/1.1, Foundation Profile, Personal Profile (these run full desktop Java SE classlibraries rather than CLDC/MIDP).
- **JSR-177 (SATSA Smart Cards)**: Hardware SIM / smart card ISO-7816 APDU secure element communication.
- **JSR-180 (SIP API)**: Session Initiation Protocol VoIP telecommunication stacks.
- **JSR-229 (Payment API)**: Cellular carrier billing, premium SMS transaction gateways.
- **IMP / IMP-NG**: Dedicated headless industrial meter profiles without display or user interaction.

---

## Tier 6: Undocumented or Vendor-Specific Behavior Requiring Investigation

Historical handset edge cases requiring empirical verification:
- Handset-specific negative softkey codes (Nokia `-6`/`-7`, SE `-1`/`-4`, Moto `-21`/`-22`).
- Timing quirks in `Display.callSerially()` and repaint thread coalescing.
- Audio player resource recycling under rapid track switching.
- Memory leak hazards during rapid mutable `Image.createRGBImage()` allocation cycles.

