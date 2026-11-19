# Java ME / J2ME Master Ecosystem Research & Technology Inventory

## 1. Executive Summary

The Java 2 Platform, Micro Edition (J2ME) — later rebranded as Java Platform, Micro Edition (Java ME) — was Sun Microsystems' standardized application execution environment for resource-constrained devices, dominating mobile phones, personal digital assistants (PDAs), set-top boxes, and embedded controllers from 1999 through the early 2010s.

The platform architecture is fundamentally modular, constructed in three stratified layers:
1. **Configurations**: Minimum complement of the Java virtual machine (JVM) features and core class libraries targeted at a broad category of hardware.
2. **Profiles**: A collection of domain-specific APIs tailored to a specific vertical device family (e.g., cellular phones, interactive pagers), layered directly atop a Configuration.
3. **Optional Packages (JSRs)**: Standardized modular extensions addressing specialized capabilities (multimedia, 3D graphics, Bluetooth, location, messaging, web services) that can be implemented alongside a Profile.
4. **OEM / Proprietary Extensions**: Vendor-specific APIs introduced by handset manufacturers (Nokia, Sony Ericsson, Siemens, Motorola, Samsung, LG) providing hardware-accelerated graphics, sound, vibration, direct framebuffers, and platform integration.

This document establishes the exhaustive reference inventory of the entire historical Java ME ecosystem, providing authoritative JCP (Java Community Process) specifications, version numbers, dependency graphs, historical usage contexts, and their relationship to the **Neutron** emulator.

---

## 2. Complete Java ME Technology Matrix

| Category | Specification / Standard | JSR | Lead / Vendor | Target Hardware / Profile | Baseline Dependency |
|---|---|---|---|---|---|
| **Configuration** | CLDC 1.0 | JSR-30 | Sun Microsystems | 16/32-bit RISC, 160KB-512KB RAM, battery | KVM / Host JVM |
| **Configuration** | CLDC 1.1 | JSR-139 | Sun Microsystems | Adds FP, weak refs, 32-bit ints, 512KB+ RAM | KVM / HotSpot / JVM |
| **Configuration** | CDC 1.0 / 1.0a / 1.1 | JSR-36 / JSR-218 | Sun Microsystems | 32-bit RISC, 2MB+ RAM, full JVM spec | CVM / JVM |
| **Profile** | MIDP 1.0 | JSR-37 | Motorola / Sun | 1-way/2-way wireless, 96x54 min display | CLDC 1.0 |
| **Profile** | MIDP 2.0 | JSR-118 | Sun / Motorola | Audio, Game API, RGB, push, OTA, TLS | CLDC 1.0 / 1.1 |
| **Profile** | MIDP 2.1 | JSR-271 | Sun Microsystems | Inter-MIDlet comms, fullscreen, forms | CLDC 1.1 |
| **Profile** | Foundation Profile 1.0 / 1.1 | JSR-42 / JSR-219 | Sun Microsystems | CDC without GUI (headless/server) | CDC 1.0 / 1.1 |
| **Profile** | Personal Basis Profile 1.0 / 1.1 | JSR-129 / JSR-217 | Sun Microsystems | CDC with lightweight AWT (no peers) | Foundation Profile |
| **Profile** | Personal Profile 1.0 / 1.1 | JSR-62 / JSR-216 | Sun Microsystems | Full AWT, applets, migration from PersonalJava | Personal Basis Profile |
| **Profile** | IMP (Information Module Profile) | JSR-195 | Siemens / Sun | Headless smart meters, telematics, industrial | CLDC 1.1 |
| **Profile** | IMP-NG (IMP Next Generation) | JSR-228 | Siemens / Sun | Enhanced industrial IoT / headless modules | CLDC 1.1 |
| **Profile** | Java ME Embedded 3.x / 8.x | JSR-360 / JSR-361 | Oracle Corporation | Modern IoT microcontrollers (Cortex-M/A) | CLDC 8 / CDC |
| **Umbrella** | JTWI (Java Technology for the Wireless Industry)| JSR-185 | Sun Microsystems | Roadmap unification (MIDP 2.0 + CLDC 1.0/1.1 + WMA + MMAPI) | MIDP 2.0 + CLDC |
| **Umbrella** | MSA (Mobile Service Architecture) 1.0 | JSR-248 | Sun / Vodafone | 16 standardized JSRs for feature phones | MIDP 2.0 + CLDC 1.1 |
| **Umbrella** | MSA (Mobile Service Architecture) 2.0 | JSR-249 | Sun Microsystems | Next-gen smartphones (MIDP 2.1 + advanced media) | MIDP 2.1 + CLDC 1.1 |
| **Optional JSR** | FileConnection & PIM | JSR-75 | Motorola / Sun | Virtual drives, filesystem, contacts, calendar | MIDP 2.0 / CDC |
| **Optional JSR** | Bluetooth & OBEX | JSR-82 | Motorola / Sun | RFCOMM, L2CAP, SDP, Object Exchange | CLDC 1.0 / 1.1 |
| **Optional JSR** | Wireless Messaging API (WMA 1.1) | JSR-120 | Sun Microsystems | SMS (text/binary), CBS | CLDC 1.0 / MIDP 1.0 |
| **Optional JSR** | Mobile Media API (MMAPI 1.1 / 1.2)| JSR-135 | Sun Microsystems | Sampled audio, MIDI, tone, video, camera | CLDC 1.0 / 1.1 |
| **Optional JSR** | Web Services (XML-RPC & SAX) | JSR-172 | Sun Microsystems | Lightweight XML parser (kXML/SAX), SOAP | CLDC 1.0 / 1.1 |
| **Optional JSR** | Security & Trust Services (SATSA) | JSR-177 | Sun Microsystems | Smart cards, cryptographic PKI, crypto ops | CLDC 1.0 / 1.1 |
| **Optional JSR** | Location API | JSR-179 | Nokia | GPS, cell-tower positioning, landmarks | CLDC 1.1 |
| **Optional JSR** | SIP API for Java ME | JSR-180 | Nokia | VoIP, IP multimedia session initiation | CLDC 1.1 / MIDP 2.0 |
| **Optional JSR** | Mobile 3D Graphics (M3G 1.0 / 1.1) | JSR-184 | Nokia | Retained scene graph & immediate 3D pipeline | CLDC 1.1 / MIDP 1.0+ |
| **Optional JSR** | Wireless Messaging API 2.0 (WMA 2.0) | JSR-205 | Sun Microsystems | MMS (multimedia messaging), multipart | CLDC 1.1 / MIDP 2.0 |
| **Optional JSR** | Content Handler API (CHAPI) | JSR-211 | Nokia | MIME-type handling, URL invocation | CLDC 1.0 / MIDP 2.0 |
| **Optional JSR** | Scalable 2D Vector Graphics (SVG) | JSR-226 | Sun Microsystems | W3C SVG Tiny 1.1, animated vector graphics | CLDC 1.1 / MIDP 2.0 |
| **Optional JSR** | Payment API | JSR-229 | Sun Microsystems | Premium SMS, micro-billing, payment providers | CLDC 1.1 / MIDP 2.0 |
| **Optional JSR** | Advanced Multimedia Supplements (AMMS)| JSR-234 | Nokia | 3D audio, reverb, equalizers, camera controls | MMAPI 1.1 / CLDC 1.1 |
| **Optional JSR** | Internationalization API | JSR-238 | Sun Microsystems | Resource bundles, number/date formatting | CLDC 1.1 / MIDP 2.0 |
| **Optional JSR** | Java Binding for OpenGL ES 1.0/1.1 | JSR-239 | Sun Microsystems | Native hardware EGL and OpenGL ES rasterization | CLDC 1.1 / MIDP 2.0 |
| **Optional JSR** | Mobile Sensor API | JSR-256 | Nokia | Accelerometers, compass, light, proximity | CLDC 1.1 / MIDP 2.0 |
| **Optional JSR** | Scalable 2D Vector Graphics 2.0 | JSR-287 | Sun Microsystems | SVG Tiny 1.2, rich vector UI controls | CLDC 1.1 / MIDP 2.1 |
| **OEM API** | Nokia UI API (FullCanvas, DirectGraphics)| Proprietary | Nokia Corporation | Direct frame buffer, 8-way rotation, transparent blits | Nokia Series 40 / 60 |
| **OEM API** | MascotCapsule Micro3D v3 | Proprietary | HI Corporation | Pure 3D figure animation, ActionTable, affine maps | Sony Ericsson, Motorola, Sharp |
| **OEM API** | Siemens Mobility Extension API | Proprietary | Siemens AG | Native file I/O, vibrator, LED, SMS, sound | Siemens 55/65/75 series |
| **OEM API** | Motorola Extension API | Proprietary | Motorola Mobility | Backlight, vibrator, camera, polyphonic sound | Motorola V-Series, ROKR, RAZR |
| **OEM API** | Sony Ericsson Extension API | Proprietary | Sony Ericsson | Audio controls, camera flash, lights, sensors | Sony Ericsson K, W, T-series |
| **OEM API** | Samsung Java Extensions | Proprietary | Samsung Electronics | Vibrate, sound, backlight, LCD layout | Samsung D, E, S-series |
| **OEM API** | LG Mobile Java Extensions | Proprietary | LG Electronics | Vibrator, sound, backlight, UI controls | LG Chocolate, Shine, Cookie |

---

## 3. Configuration Profiles Architecture

```text
+-----------------------------------------------------------------------------------+
|                            APPLICATIONS (MIDlets / Xlets)                         |
+-----------------------------------------------------------------------------------+
|                     OPTIONAL PACKAGES (JSRs) & OEM EXTENSIONS                     |
|  JSR-75 (File/PIM) | JSR-135 (MMAPI) | JSR-184 (M3G) | Micro3D v3 | Nokia UI API  |
|  JSR-82 (BT)       | JSR-234 (AMMS)  | JSR-226 (SVG) | JSR-239    | Siemens/SE/Moto|
+----------------------------------------------------+------------------------------+
|                     PROFILES                       |         CDC PROFILES         |
|  MIDP 1.0 / 2.0 / 2.1                              |  Foundation Profile 1.1      |
|  (LCDUI, Game API, RMS, PushRegistry, OTA)         |  Personal Basis / Personal   |
+----------------------------------------------------+------------------------------+
|                   CONFIGURATIONS                   |                              |
|  CLDC 1.0 (JSR-30) / CLDC 1.1 (JSR-139)            |  CDC 1.0 / 1.1 (JSR-36/218)  |
|  (java.lang, java.io, java.util, javax.micro.io)   |  (Full JVM Spec, JNI, JMX)   |
+----------------------------------------------------+------------------------------+
|                     VIRTUAL MACHINE & EXECUTION ENGINE                            |
|  KVM / CLDC HotSpot (Historic)  <---->  Neutron JIT MIDletClassLoader Engine      |
+-----------------------------------------------------------------------------------+
|                            HOST OPERATING SYSTEM                                  |
|  Linux / POSIX / macOS / Windows (JVM 8+ / 17+ / 21+)                             |
+-----------------------------------------------------------------------------------+
```

---

## 4. Architectural Relationship to the Neutron Emulator

Neutron takes a specialized, modern architectural stance:
1. **Host-JVM Direct Execution**: Rather than compiling or emulating a slow interpreted 16-bit register VM (like early KVM), Neutron utilizes host JVM (HotSpot / GraalVM) dynamic classloader transformation (`MIDletClassLoader`, ASM bytecode manipulation `ChangeCallsClassVisitor`).
2. **Bytecode Instrumentation**: Neutron intercepts forbidden bytecode calls (such as direct `java.lang.System.exit` or calls into unsupported desktop packages) and remaps microedition package invocations directly to Neutron's native platform implementations.
3. **Hardware Acceleration & Software Fallback**:
   - MascotCapsule Micro3D v3 is fully implemented as an authentic pure-Java software rasterizer (`com.mascotcapsule.micro3d.v3.*`) with an optional OpenGL hardware backend.
   - JSR-184 M3G is implemented with an in-depth chunk stream parser, software rasterizer, and OpenGL backend.
   - Nokia UI DirectGraphics supports 32-bit ARGB manipulation, 8-way rotations, alpha blending, and direct pixel buffers.
   - MMAPI JSR-135 is backed by Java Sound MIDI polyphonic synthesizers, PCM sampled audio (WAV, Sun AU), native AMR decoding, and frequency tone generators.
   - RMS is backed by both real-time disk persistence (`~/.neutron/suite-.../*.rms`) and non-destructive in-memory backing (`--rms memory`).
   - GCF is backed by direct Java SE network sockets, HTTP/HTTPS URLConnection handlers, and loopback emulation.

