# Java ME Configurations and Profiles Deep-Dive Reference

## 1. Connected Limited Device Configuration (CLDC)

### 1.1 CLDC 1.0 (JSR-30)
- **Release Date**: May 2000
- **Target Specifications**: 16/32-bit CPU, 160 KB non-volatile memory (ROM/Flash), 32 KB volatile memory (RAM), intermittent network connectivity, battery operated.
- **Virtual Machine Requirements (KVM)**:
  - Bytecode execution of Java Virtual Machine Specification v2.
  - **No floating-point support**: Bytecodes `fadd`, `fsub`, `fmul`, `fdiv`, `dadd`, `dsub`, `dmul`, `ddiv`, `f2i`, `i2f`, etc., are strictly prohibited and cause class verification errors.
  - **No object finalization**: `Object.finalize()` does not exist; garbage collection performs instantaneous unreferenced reclamation.
  - **No JNI**: Native method invocations are strictly vendor-internal.
  - **No user-defined class loaders**: `java.lang.ClassLoader` is absent; dynamic loading is strictly controlled by system runtime.
  - **No thread groups or daemon threads**: Threads are basic preemptive or cooperative execution contexts.
  - **No reflection**: `java.lang.reflect` package is completely absent.
  - **Two-Pass Class File Verification**: Split verification using pre-verification at build time (StackMap attributes) and linear verification at runtime on-device.
- **Core Standard Library Packages**:
  - `java.lang`: `Object`, `Class`, `String`, `StringBuffer`, `Throwable`, `Exception`, `Error`, `Runtime`, `System`, `Thread`, `Runnable`, `Math` (integer-only math), primitive wrappers (`Byte`, `Short`, `Integer`, `Long`, `Boolean`, `Character`).
  - `java.io`: `InputStream`, `OutputStream`, `ByteArrayInputStream`, `ByteArrayOutputStream`, `DataInput`, `DataOutput`, `DataInputStream`, `DataOutputStream`, `Reader`, `Writer`, `InputStreamReader`, `OutputStreamWriter`.
  - `java.util`: `Vector`, `Hashtable`, `Enumeration`, `Stack`, `Date`, `Calendar`, `TimeZone`, `Random`.
  - `javax.microedition.io`: Generic Connection Framework (GCF) — `Connector`, `Connection`, `InputConnection`, `OutputConnection`, `StreamConnection`, `ContentConnection`, `StreamConnectionNotifier`.

### 1.2 CLDC 1.1 (JSR-139)
- **Release Date**: March 2003
- **Primary Enhancements**:
  - **IEEE 754 Floating-Point Arithmetic**: Full restoration of single-precision (`float`, `Float`) and double-precision (`double`, `Double`) types, arithmetic opcodes, and trigonometric/transcendental functions in `java.lang.Math` (`sin`, `cos`, `tan`, `sqrt`, `ceil`, `floor`).
  - **Weak References**: Introduction of `java.lang.ref.WeakReference` for cache management without impeding garbage collector cycles.
  - **Thread Error Handling**: Addition of `Thread.interrupt()` and exception propagation.
  - **Calendar / TimeZone Enhancements**: Full support for millisecond-precision UTC calculations.
  - **Minimum Memory Requirement**: Raised from 192 KB to 512 KB total memory footprint.

---

## 2. Mobile Information Device Profile (MIDP)

### 2.1 MIDP 1.0 (JSR-37)
- **Release Date**: September 2000
- **Hardware Profile**: 96x54 pixel screen minimum, 1-bit monochrome display minimum, 128 KB non-volatile memory for MIDP components, 32 KB dynamic runtime heap, two-way wireless networking.
- **Key Subsystems**:
  - **Application Lifecycle**: `javax.microedition.midlet.MIDlet` (`startApp`, `pauseApp`, `destroyApp`).
  - **High-Level UI**: `Display`, `Displayable`, `Form`, `List`, `TextBox`, `Alert`, `TextField`, `ChoiceGroup`, `DateField`, `Gauge`, `StringItem`, `ImageItem`.
  - **Low-Level UI**: `Canvas` with immediate-mode `paint(Graphics g)` rendering, directional key codes, game actions (`UP`, `DOWN`, `LEFT`, `RIGHT`, `FIRE`, `GAME_A`..`GAME_D`).
  - **Persistent Storage**: Record Management System (`javax.microedition.rms.RecordStore`).
  - **Networking**: HTTP 1.1 over GCF (`javax.microedition.io.HttpConnection`).

### 2.2 MIDP 2.0 (JSR-118)
- **Release Date**: November 2002
- **Major Innovations**:
  - **Game API (`javax.microedition.lcdui.game`)**:
    - `GameCanvas`: Off-screen graphics buffering, immediate synchronous buffer flushing (`flushGraphics()`), asynchronous non-blocking key polling (`getKeyStates()`).
    - `Sprite`: Hardware-like 2D animated sprites with 8 affine transforms (rotations by 90°, 180°, 270°, mirror reflections), frame sequences, and pixel-level bounding-box collision detection (`collidesWith`).
    - `TiledLayer`: Fast high-performance grid-based scrolling background tile maps with static and animated tiles.
    - `LayerManager`: Compositing viewport controller managing render order, layering, and camera scrolling.
  - **Enhanced LCDUI**:
    - Fullscreen mode for `Canvas` (`setFullScreenMode(true)`).
    - Custom UI elements via `CustomItem`.
    - Text layout: `Spacer`, improved form traversal, `ItemCommandListener`.
    - Direct RGB rendering: `Image.createRGBImage()`, `Graphics.drawRGB()`, `Image.getRGB()`.
  - **Audio & Media Integration**: Direct subset of MMAPI (`javax.microedition.media.Manager`, simple tone generation, sampled audio playback).
  - **Networking Expansion**: Secure sockets (`HttpsConnection`, `SecureConnection`), stream sockets (`SocketConnection`, `ServerSocketConnection`), UDP datagrams (`DatagramConnection`, `UDPDatagramConnection`), push registry (`PushRegistry`).
  - **Comprehensive Security Model**: Untrusted vs. Trusted domain security, X.509 PKI certificate validation, application signing, permission gating (`MIDlet-Permissions`).

### 2.3 MIDP 2.1 (JSR-271)
- **Release Date**: December 2009
- **Refinements**:
  - Inter-MIDlet communication and invocation.
  - Enhanced softkey and menu positioning.
  - Native display orientation changes (portrait / landscape dynamic notification).
  - Deeper audio and camera capture integration.

---

## 3. Connected Device Configuration (CDC) & Profiles

### 3.1 CDC 1.0 / 1.1 (JSR-36 / JSR-218)
- Targeted at 32-bit microprocessors with 2 MB to 16 MB RAM (PDAs, automotive head units, set-top boxes).
- Contains full JVM specification: JNI, standard class loaders, reflection, serialization, full floating point, object finalization.

### 3.2 Foundation Profile 1.0 / 1.1 (JSR-42 / JSR-219)
- CDC profile providing a comprehensive headless Java SE foundation (`java.lang`, `java.util`, `java.io`, `java.net`, `java.text`, `java.security`).

### 3.3 Personal Basis Profile (JSR-129 / JSR-217) & Personal Profile (JSR-62 / JSR-216)
- Adds lightweight UI (PBP) and complete AWT applet compatibility (PP), bridging legacy PersonalJava applications.

---

## 4. Headless & Industrial Profiles: IMP & IMP-NG

### 4.1 IMP (JSR-195)
- Information Module Profile: Strips all graphical LCDUI packages from MIDP 1.0/2.0 to provide a minimal footprint runtime for telemetry, POS terminals, and cellular modems.

### 4.2 IMP-NG (JSR-228)
- Information Module Profile Next Generation: Aligns with MIDP 2.0/2.1 security, networking, and OTA management without display or audio requirements.

---

## 5. Compatibility Initiatives: JTWI & MSA

### 5.1 JTWI (JSR-185 - Java Technology for the Wireless Industry)
- Established in 2003 to combat handset fragmentation across Nokia, Motorola, Sony Ericsson, and Siemens.
- Mandated baseline: CLDC 1.0 + MIDP 2.0 + WMA 1.1 (JSR-120) + MMAPI (JSR-135 tone & MIDI subset).
- Defined standard screen size minimum: 125x125 pixels.
- Mandated max JAR size minimum: 64 KB (recommended 128 KB+).

### 5.2 MSA (JSR-248 / JSR-249 - Mobile Service Architecture)
- **MSA 1.0 (JSR-248)**: Standardized 16 JSRs on all 3G feature phones (CLDC 1.1, MIDP 2.0, JSR-75 File/PIM, JSR-82 Bluetooth, JSR-135 MMAPI, JSR-184 M3G, JSR-205 WMA 2.0, JSR-211 CHAPI, JSR-226 SVG, JSR-234 AMMS, JSR-238 I18N, JSR-239 OpenGL ES).
- **MSA 2.0 (JSR-249)**: Modern smartphone standard incorporating MIDP 2.1, JSR-256 Sensors, JSR-287 SVG 2.0, SIP VoIP.

