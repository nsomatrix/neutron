# Java ME Optional Packages and JSR Specifications Reference

## 1. JSR-75: FileConnection and PIM API
- **Specification Title**: PDA Optional Packages for the J2ME Platform
- **Lead**: Motorola & Sun Microsystems
- **Packages**:
  - `javax.microedition.io.file`: Access to local file systems, memory cards (`root/`, `memory_card/`, `SDCard/`), directories, file metadata, permissions, stream I/O.
  - `javax.microedition.pim`: Personal Information Management (Contacts, Events/Calendar, To-Do items).
- **Key Classes**: `FileConnection`, `FileSystemRegistry`, `FileSystemListener`, `PIM`, `ContactList`, `EventList`.
- **Neutron Implementation**: Implemented in module `neutron-jsr-75`. Features virtual filesystem mapping, root enumeration, directory creation/deletion, read/write stream wrappers, and path traversal protection against `../` exploits.

---

## 2. JSR-82: Java APIs for Bluetooth and OBEX
- **Specification Title**: Java APIs for Bluetooth Wireless Technology
- **Lead**: Motorola & IBM
- **Packages**:
  - `javax.bluetooth`: LocalDevice, RemoteDevice, DiscoveryAgent, ServiceRecord, UUID, L2CAPConnection, L2CAPConnectionNotifier.
  - `javax.obex`: ClientSession, ServerRequestHandler, HeaderSet, Operation, ResponseCodes.
- **Neutron Implementation**: Implemented in `neutron-jsr-82`. Provides standard Bluetooth API declarations, inquiry stubs, OBEX protocol structures, and simulated local/remote device contexts.

---

## 3. JSR-120 & JSR-205: Wireless Messaging APIs (WMA 1.1 & 2.0)
- **Specification Title**: Wireless Messaging API
- **Lead**: Sun Microsystems
- **Packages**: `javax.wireless.messaging`
- **Key Classes**: `MessageConnection`, `TextMessage`, `BinaryMessage`, `MultipartMessage`, `MessagePart`, `MessageListener`.
- **Neutron Implementation**: Implemented in `neutron-jsr-120`. Handles SMS URI scheme (`sms://:5000`, `sms://+123456789`), text/binary payloads, segmentation, and connection handlers.

---

## 4. JSR-135: Mobile Media API (MMAPI 1.1 / 1.2)
- **Specification Title**: Mobile Media API
- **Lead**: Sun Microsystems & Philips
- **Packages**:
  - `javax.microedition.media`: `Manager`, `Player`, `PlayerListener`, `Control`, `Controllable`, `DataSource`.
  - `javax.microedition.media.control`: `VolumeControl`, `ToneControl`, `MIDIControl`, `VideoControl`, `StopTimeControl`, `PitchControl`, `RateControl`, `RecordControl`.
- **Audio Formats Supported**:
  - PCM WAV (uncompressed mono/stereo 8-bit/16-bit)
  - Sun AU (`audio/basic`)
  - MIDI polyphonic sequences (Standard MIDI Files Type 0 & Type 1)
  - Synthetic monophonic and polyphonic frequency tone sequences (`audio/x-tone-seq`)
  - AMR-NB (Adaptive Multi-Rate narrowband speech codec, 8 kHz sampling)
- **Neutron Implementation**: Implemented in `neutron-jsr-135`. Uses Java Sound API for polyphonic MIDI playback with continuous looping, low-latency audio lines for PCM WAV/AU, built-in AMR bitstream decoder, and mathematical sine-wave synthesis for J2ME tones.

---

## 5. JSR-172: J2ME Web Services Specification
- **Specification Title**: J2ME Web Services Specification
- **Lead**: Sun Microsystems
- **Packages**:
  - `javax.xml.parsers`: `SAXParserFactory`, `SAXParser`.
  - `javax.xml.rpc`: `Stub`, `JAXRPCException`, `FaultDetailHandler`.
  - `org.xml.sax`, `org.xml.sax.helpers`: Event-based streaming XML parsing.
- **Neutron Implementation**: Implemented in `neutron-jsr-172`. Provides full XML SAX parser factory bridging to Java XML parsers and stub interfaces for SOAP RPC over HTTP.

---

## 6. JSR-177: Security and Trust Services API (SATSA)
- **Specification Title**: Security and Trust Services API for J2ME
- **Lead**: Sun Microsystems
- **Packages**: `javax.microedition.pki`, `javax.microedition.securityservice`, `javax.crypto`.
- **Scope**: Smart card access (APDU protocol), digital signature generation, cryptographic key management.

---

## 7. JSR-179: Location API for J2ME
- **Specification Title**: Location API for J2ME
- **Lead**: Nokia Corporation
- **Packages**: `javax.microedition.location`
- **Key Classes**: `LocationProvider`, `Location`, `Coordinates`, `Criteria`, `Landmark`, `LandmarkStore`, `Orientation`.
- **Neutron Implementation**: Implemented in `neutron-jsr-179`. Provides mock and configurable location feeds (latitude, longitude, altitude, speed, course).

---

## 8. JSR-184: Mobile 3D Graphics API (M3G 1.0 / 1.1)
- **Specification Title**: Mobile 3D Graphics API for J2ME
- **Lead**: Nokia Corporation
- **Packages**: `javax.microedition.m3g`
- **Key Architecture**:
  - **Retained Mode**: Complete hierarchical scene graph rooted at `World`. Nodes include `Camera`, `Light`, `Group`, `Mesh`, `SkinnedMesh`, `MorphingMesh`, `Sprite3D`, `Background`.
  - **Immediate Mode**: Direct rendering via `Graphics3D.render(VertexBuffer, IndexBuffer, Appearance, Transform)`.
  - **Binary File Format (`.m3g`)**: Compressed chunk-based binary stream format with Zlib deflate compression and Adler-32 integrity validation.
  - **Animation Engine**: `AnimationController`, `AnimationTrack`, `KeyframeSequence` with linear and spline interpolation.
  - **Lighting & Texturing**: Ambient, directional, omni, and spotlight sources; `Material` (diffuse, specular, ambient, emissive); `Texture2D` with wrap/filter modes; `CompositingMode` (alpha, replace, modulate, add).
- **Neutron Implementation**: Implemented in `neutron-jsr-184`. Full software rasterizer with subpixel precision, configurable perspective correction, bilinear filtering, and an optional high-performance OpenGL backend (`neutron-opengl`).

---

## 9. JSR-211: Content Handler API (CHAPI)
- **Specification Title**: Content Handler API
- **Lead**: Nokia Corporation
- **Packages**: `javax.microedition.content`
- **Key Classes**: `Registry`, `Invocation`, `ContentHandler`.
- **Neutron Implementation**: Implemented in `neutron-jsr-211`. Allows URL and MIME type routing between registered applications.

---

## 10. JSR-226 & JSR-287: Scalable 2D Vector Graphics (SVG Mobile)
- **Specification Title**: Scalable 2D Vector Graphics API for J2ME
- **Lead**: Sun Microsystems
- **Packages**: `javax.microedition.m2g`, `org.w3c.dom.svg`
- **Key Classes**: `SVGImage`, `SVGAnimator`, `ScalableGraphics`, `ScalableImage`.
- **Neutron Implementation**: Implemented in `neutron-jsr-226`. Renders W3C SVG Tiny 1.1 documents, handles viewBox transformations, and animates vector paths.

---

## 11. JSR-234: Advanced Multimedia Supplements (AMMS)
- **Specification Title**: Advanced Multimedia Supplements
- **Lead**: Nokia Corporation
- **Packages**:
  - `javax.microedition.amms`: `GlobalManager`, `SoundSource3D`, `SpectatorModelling`, `EffectModule`.
  - `javax.microedition.amms.control`: `PanControl`, `PriorityControl`, `EffectControl`.
  - `javax.microedition.amms.control.audioeffect`: `EqualizerControl`, `ReverbControl`.
  - `javax.microedition.amms.control.audio3d`: `LocationControl`, `OrientationControl`, `DistanceAttenuationControl`.
- **Neutron Implementation**: Implemented in `neutron-jsr-234`. Provides 3D positional spectator calculations, parametric equalizers, environmental reverb presets, and camera effects.

---

## 12. JSR-239: Java Binding for OpenGL ES 1.0 / 1.1
- **Specification Title**: Java Binding for the OpenGL ES API
- **Lead**: Sun Microsystems & Khronos Group
- **Packages**:
  - `javax.microedition.khronos.egl`: `EGL`, `EGL10`, `EGL11`, `EGLConfig`, `EGLContext`, `EGLDisplay`, `EGLSurface`.
  - `javax.microedition.khronos.opengles`: `GL`, `GL10`, `GL11`, `GL10Ext`, `GL11Ext`.
- **Neutron Implementation**: Implemented in `neutron-jsr-239`. Software and OpenGL bridging for mobile 3D pipelines.

---

## 13. JSR-256: Mobile Sensor API
- **Specification Title**: Mobile Sensor API
- **Lead**: Nokia Corporation
- **Packages**: `javax.microedition.sensor`
- **Key Classes**: `SensorManager`, `SensorInfo`, `SensorConnection`, `Channel`, `ChannelInfo`, `Data`, `Condition`, `LimitCondition`, `RangeCondition`.
- **Neutron Implementation**: Implemented in `neutron-jsr-256`. Simulates device telemetry: 3-axis accelerometer, ambient light, orientation, battery temperature.

