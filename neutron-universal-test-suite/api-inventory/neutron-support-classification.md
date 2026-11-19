# Neutron Comprehensive API Support Classification

This document classifies every Java ME technology, configuration, profile, and optional package according to the six designated tiers:

## Summary Counts
- **Tier 1 (Required by Declared Platform)**: 2 core specifications (CLDC 1.1, MIDP 2.0) across 8 standard packages.
- **Tier 2 (Implemented and Requiring Verification)**: 9 full extension systems (Micro3D v3, JSR-184 M3G, Nokia UI DirectGraphics, JSR-135 MMAPI, JSR-75 FileConnection, JSR-234 AMMS, JSR-226 SVG M2G, Siemens API, OEM Handset APIs).
- **Tier 3 (Partially Implemented / Simulated)**: 7 optional packages (JSR-82 Bluetooth/OBEX, JSR-120 WMA, JSR-172 Web Services XML, JSR-179 Location, JSR-211 CHAPI, JSR-239 OpenGL ES, JSR-256 Sensor).
- **Tier 4 (Unsupported but Potentially Relevant)**: 4 specifications (MIDP 2.1 Inter-MIDlet, JSR-205 WMA 2.0, JSR-238 I18N, JSR-287 SVG 2.0).
- **Tier 5 (Outside Intended Scope)**: 5 specifications (CDC 1.0/1.1, Foundation Profile, Personal Profile, JSR-177 SATSA Smart Card, JSR-180 SIP VoIP).
- **Tier 6 (Undocumented / Vendor Edge Cases)**: 6 verified behavioral variation domains (Softkey negative codes, double buffering synchronization, clipping offsets, direct pixel formats, rapid audio player recreation, memory allocation spikes).

---

## Detailed Classification Table

| Specification / API Element | Tier | Target Status in Neutron | Reason & Scope |
|---|---|---|---|
| `java.lang.*` (CLDC 1.1 subset) | Tier 1 | Required / Fully Implemented | Core Java language runtime execution |
| `java.io.*` (CLDC 1.1 subset) | Tier 1 | Required / Fully Implemented | Core streams and data encoding |
| `java.util.*` (CLDC 1.1 subset)| Tier 1 | Required / Fully Implemented | Collections, dates, calendars, timers |
| `javax.microedition.midlet.*` | Tier 1 | Required / Fully Implemented | Application lifecycle management |
| `javax.microedition.lcdui.*` | Tier 1 | Required / Fully Implemented | Core LCDUI UI and Canvas rendering |
| `javax.microedition.lcdui.game.*`| Tier 1 | Required / Fully Implemented | 2D GameCanvas, Sprite, TiledLayer |
| `javax.microedition.rms.*` | Tier 1 | Required / Fully Implemented | Record Management System storage |
| `javax.microedition.io.*` | Tier 1 | Required / Fully Implemented | Generic Connection Framework |
| `com.mascotcapsule.micro3d.v3.*` | Tier 2 | Implemented / Verified | Software 3D rasterizer and `.mbac`/`.mtac` |
| `javax.microedition.m3g.*` (JSR-184)| Tier 2 | Implemented / Verified | Mobile 3D Graphics retained/immediate mode |
| `com.nokia.mid.ui.*` (Nokia UI) | Tier 2 | Implemented / Verified | DirectGraphics 32-bit ARGB, FullCanvas |
| `javax.microedition.media.*` (JSR-135)| Tier 2 | Implemented / Verified | MMAPI audio (WAV, MIDI, AMR, tones) |
| `javax.microedition.amms.*` (JSR-234)| Tier 2 | Implemented / Verified | 3D audio, reverb, equalizers |
| `javax.microedition.io.file.*` (JSR-75)| Tier 2 | Implemented / Verified | Virtual file storage and root enumeration |
| `javax.microedition.m2g.*` (JSR-226)| Tier 2 | Implemented / Verified | Scalable 2D SVG Tiny 1.1 vector graphics |
| `com.siemens.mp.*` | Tier 2 | Implemented / Verified | Siemens filesystem, lights, vibrator |
| `com.sonyericsson.*`, `com.samsung.*`, `com.motorola.*` | Tier 2 | Implemented / Verified | Handset OEM backlight, vibrator, audio |
| `javax.bluetooth.*`, `javax.obex.*` (JSR-82) | Tier 3 | Partially Implemented | Simulated Bluetooth discovery & OBEX |
| `javax.wireless.messaging.*` (JSR-120) | Tier 3 | Partially Implemented | Local loopback SMS message connection |
| `javax.xml.parsers.*`, `rpc.*` (JSR-172) | Tier 3 | Partially Implemented | SAX XML parsing implemented; SOAP stubbed |
| `javax.microedition.location.*` (JSR-179)| Tier 3 | Partially Implemented | Simulated location and coordinate feeds |
| `javax.microedition.content.*` (JSR-211) | Tier 3 | Partially Implemented | Content registration structures stubbed |
| `javax.microedition.khronos.*` (JSR-239) | Tier 3 | Partially Implemented | OpenGL ES / EGL desktop wrappers |
| `javax.microedition.sensor.*` (JSR-256) | Tier 3 | Partially Implemented | Synthetic sensor channels and limits |
| MIDP 2.1 Inter-MIDlet IPC (JSR-271) | Tier 4 | Unsupported | Out of scope for standalone execution |
| JSR-205 (WMA 2.0 MMS) | Tier 4 | Unsupported | Cellular carrier MMS payload handling |
| JSR-238 (Mobile I18N API) | Tier 4 | Unsupported | Seldom used in commercial gaming titles |
| JSR-287 (SVG Mobile 2.0) | Tier 4 | Unsupported | Replaced by WebGL/HTML5 in late phones |
| CDC 1.0 / 1.1 & Foundation Profile | Tier 5 | Outside Scope | Different VM architecture (desktop Java SE) |
| Personal Profile / Personal Basis | Tier 5 | Outside Scope | Desktop AWT applet migration target |
| JSR-177 (SATSA Smart Cards) | Tier 5 | Outside Scope | Requires hardware ISO-7816 SIM hardware |
| JSR-180 (SIP VoIP API) | Tier 5 | Outside Scope | Requires active SIP telephony registrar |
| JSR-229 (Payment API) | Tier 5 | Outside Scope | Requires mobile operator billing gateways |
| Handset Softkey Keycodes | Tier 6 | Vendor Variation | Negative keycode translation table |
| Canvas Double Buffering Timing | Tier 6 | Vendor Variation | Frame coalescing and repaint pacing |
| Micro3D Affine Fixed-Point Precision | Tier 6 | Vendor Variation | Matrix multiplication rounding behavior |

