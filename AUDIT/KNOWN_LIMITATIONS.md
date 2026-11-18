# NEUTRON — KNOWN LIMITATIONS & HOST ENVIRONMENT CONSTRAINTS

This document provides an objective, transparent engineering assessment of known limitations, platform dependencies, and hardware constraints in the Neutron emulator as of version 1.5.0.

---

## 1. Native Platform Dependencies & Architecture Support

### JSR-184 M3G Engine (`libjavam3g.so`)
- **Supported Architecture**: Pre-compiled native binary is provided for **Linux x86_64** (`glibc >= 2.31`).
- **Other Platforms (Windows x64, macOS Apple Silicon / Intel, Linux ARM64)**:
  - Native C99 source code is provided in `neutron-jsr-184/src/main/cpp/m3g`.
  - Platforms without a pre-compiled native library automatically fall back to the built-in pure Java software rasterizer.
  - The software fallback provides full functional compatibility for M3G scene graphs, materials, and textures, but operates with lower polygon fill rates compared to native hardware-accelerated binaries.

---

## 2. 3D Graphics & Hardware Acceleration Backends

### MascotCapsule Micro3D v3
- **Dual-Backend Design**:
  - Pure-Java software rasterizer (`SoftwareMicro3D`).
  - Hardware-accelerated OpenGL backend (`OpenGLMicro3D`).
- **Limitations**:
  - In headless or virtualized container environments lacking an active X11/Wayland display server or hardware GPU, the OpenGL backend cannot initialize a native GL context and automatically routes rendering through the software engine.
  - Certain non-standard Micro3D texture blending modes (e.g., proprietary additive blending flags used in obscure Japanese DoCoMo i-mode titles) are approximated via standard alpha blending.

---

## 3. Multimedia & Audio (MMAPI JSR-135)

### Synthesizer & Timing Constraints
- **MIDI & Tone Playback**:
  - Synthesized via the standard OpenJDK Gervill Software Synthesizer.
  - Audio latency is bound by the host operating system's audio server (PulseAudio / PipeWire / ALSA / Windows WASAPI). Typical latency ranges from 15 ms to 35 ms.
  - Titles that depend on microsecond-exact MIDI event interrupts for game loop physics (rather than `System.currentTimeMillis()` or standard MIDlet timer tasks) may experience minor synchronization variations.
- **Video Capture & Playback (`video/mpeg`, `video/3gpp`)**:
  - Video stream rendering is not backed by hardware H.264/MPEG-4 decoders; queries for video controls return graceful fallback stubs or throw `MediaException("Unsupported format")` per the JSR-135 specification.

---

## 4. Networking & Wireless Connectivity

### Generic Connection Framework (GCF)
- **HTTP / HTTPS**:
  - Fully functional via standard Java network stack; supports redirects, headers, chunked transfers, and TLS encryption.
- **TCP Sockets (`socket://`)**:
  - Fully functional for outbound connections to legacy game servers.
- **JSR-82 Bluetooth & OBEX**:
  - Emulated via simulated in-memory discovery and loopback connections.
  - Physical Bluetooth radio binding (BlueZ / Windows Bluetooth API) is not implemented; multiplayer connectivity between two distinct physical host machines requires TCP bridge tunnels.
- **JSR-120 / JSR-205 Wireless Messaging (SMS / MMS)**:
  - Messages sent by MIDlets are captured by a simulated local message center and can be read via emulator debug panels.
  - Direct transmission over physical GSM AT command serial modems is unsupported.

---

## 5. Build Toolchain & JVM Evolution

### Java 8 Bytecode vs. Modern OpenJDK
- **Java 8 Source/Target Compatibility**:
  - CLDC and MIDP bytecode compatibility requires generating Java 1.8 class files.
  - Modern Java compilers (OpenJDK 21+) emit informational warnings during compilation:
    `warning: [options] source value 8 is obsolete and will be removed in a future release`.
  - This does not affect binary correctness or execution, but migrating the core build to Java 11/17 bytecode target while retaining CLDC API compatibility is planned for a future release.
- **Removed JVM Methods**:
  - Direct invocations of `Thread.stop()` and `Thread.suspend()` have been eliminated from `MIDletThread.java` and replaced with cooperative thread interruption and bounded timeouts.

