# Historical Handset Generations and Device Variations

## 1. Handset Generations Timeline

```text
Era 1: Monochrome & Early Color (2000 - 2002)
   Nokia 3310 (WAP/early), Nokia 3410, Siemens SL45i, Motorola Timeport 260
   CLDC 1.0, MIDP 1.0, 96x65 to 101x80 display, 32KB - 64KB RAM heap

Era 2: S40 / S60 & Early 2.5G Expansion (2002 - 2004)
   Nokia 7210, 6610 (Series 40 1st/2nd Ed, 128x128, 4096 colors)
   Nokia 7650, 3650, 6600, N-Gage (Series 60 1st/2nd Ed, 176x208, 65k colors)
   Siemens M55, S55, SX1; Sony Ericsson T610, K700i (128x160 to 176x220)
   CLDC 1.0/1.1, MIDP 2.0, Nokia UI API, MascotCapsule Micro3D v3, MMAPI, WMA 1.1

Era 3: Golden Age of 3D & Multimedia Feature Phones (2005 - 2008)
   Sony Ericsson K750i, W800i, K800i, W995 (176x220 to 240x320, Micro3D v3 + M3G)
   Nokia 6230i, 6300, 5310 XpressMusic (Series 40 3rd/5th Ed, 240x320)
   Nokia N70, N73, N95, E71, E72 (Series 60 3rd Ed, 240x320 portrait / 320x240 landscape)
   Motorola RAZR V3, ROKR E1, E398 (176x220)
   Samsung SGH-D500, E250, U600 (176x220, 240x320)
   CLDC 1.1, MIDP 2.0/2.1, JSR-75, JSR-82, JSR-135, JSR-184, JSR-226, JSR-234, MSA 1.0

Era 4: Touchscreen Feature Phones & Late Symbian (2008 - 2012)
   Nokia 5800 XpressMusic, N97, C6 (Series 60 5th Ed / Symbian^1, 360x640 resistive touch)
   Samsung Star (S5230), Corby (S3650) (240x400 touch)
   LG Cookie (KP500), Renoir (240x400 touch)
   CLDC 1.1, MIDP 2.1, JSR-256 (Sensors), Pointer drag/tap events, virtual keypads
```

---

## 2. Handset Architectural Profiles Comparison

| Device Model | Manufacturer | OS / Platform | Screen Resolution | Color Depth | Keypad Layout | Graphics APIs | Audio APIs | Heap RAM |
|---|---|---|---|---|---|---|---|---|
| **Nokia 3410** | Nokia | Series 30 | 96 x 65 | 1-bit B&W | Numeric 12-key + Softkeys | MIDP 1.0 Canvas | Basic Tone | ~180 KB |
| **Nokia 7210 / 6610** | Nokia | Series 40 1st Ed | 128 x 128 | 12-bit (4096) | Numeric + 2 Softkeys | Nokia UI FullCanvas, DirectGraphics | Sound, OTT Ringtone | ~512 KB |
| **Nokia 6600** | Nokia | Series 60 2nd Ed | 176 x 208 | 16-bit (65K) | Joystick + Numeric + Options | MIDP 2.0, Nokia UI | MMAPI (WAV, MIDI, AMR) | ~2.5 MB |
| **Nokia N-Gage** | Nokia | Series 60 1st Ed | 176 x 208 | 12-bit (4096) | 8-way D-Pad + 5/7 Gaming keys | DirectGraphics | MMAPI, MIDI, WAV | ~3.0 MB |
| **Nokia N95** | Nokia | Series 60 3rd Ed | 240 x 320 | 24-bit (16M) | D-Pad + Softkeys + Multimedia | M3G JSR-184, Nokia UI | MMAPI, AMMS, 3D Audio | ~30 MB |
| **Nokia E71 / E72** | Nokia | Series 60 3rd Ed | 320 x 240 (Land) | 24-bit (16M) | QWERTY Keyboard + Optical Nav | M3G JSR-184, Nokia UI | MMAPI, AMMS | ~45 MB |
| **Nokia 5800** | Nokia | Series 60 5th Ed | 360 x 640 | 24-bit (16M) | Resistive Stylus / Touchscreen | M3G, Nokia UI | MMAPI, AMMS | ~60 MB |
| **Sony Ericsson K750i**| Sony Ericsson | A100 | 176 x 220 | 18-bit (262K) | Joystick + Softkeys | Micro3D v3, M3G | MMAPI, AMR, MP3, MIDI | ~4.0 MB |
| **Sony Ericsson K800i**| Sony Ericsson | A200 | 240 x 320 | 18-bit (262K) | Joystick + Softkeys | Micro3D v3, M3G | MMAPI, AMMS, MP3 | ~16 MB |
| **Motorola RAZR V3** | Motorola | Synergy OS | 176 x 220 | 18-bit (262K) | D-Pad + 2 Softkeys | MIDP 2.0, Moto FunLight | MMAPI, MIDI, WAV | ~2.0 MB |
| **Samsung SGH-D500** | Samsung | Proprietary | 176 x 220 | 18-bit (262K) | D-Pad + 2 Softkeys | MIDP 2.0, Samsung Util | MMAPI, Samsung Audio | ~4.0 MB |
| **Siemens SX1** | Siemens | Series 60 1st Ed | 176 x 220 | 16-bit (65K) | Side-rail numeric keys | Siemens API, MIDP 2.0 | Siemens Sound, MMAPI | ~3.5 MB |

---

## 3. Key Differences and Handset Quirks

1. **Key Codes & Game Action Mapping**:
   - Standard MIDP defines standard game actions (`UP`, `DOWN`, `LEFT`, `RIGHT`, `FIRE`, `GAME_A`..`GAME_D`).
   - Handsets mapped numeric keys differently: Nokia used `KEY_NUM2` (up), `KEY_NUM8` (down), `KEY_NUM4` (left), `KEY_NUM6` (right), `KEY_NUM5` (fire); Motorola and Sony Ericsson frequently used different negative keycodes for softkeys (e.g., `-6` / `-7` on Nokia, `-1` / `-4` on Sony Ericsson, `-21` / `-22` on Motorola).
2. **Double Buffering Behavior**:
   - `Graphics.isDoubleBuffered()` was true on Series 60 and Sony Ericsson A200, but false on early Series 40.
   - Applications implementing manual off-screen image buffers suffered severe memory waste and frame-rate drops when the emulator already maintained internal double-buffering.
3. **Clip Rectangles**:
   - Historical handsets had off-by-one errors in `getClipWidth()` and `getClipHeight()` when translations were applied.
4. **RGB Image Alpha Handling**:
   - MIDP 2.0 `Image.createRGBImage` specifies that `processAlpha=false` must render every pixel as fully opaque (ignoring top 8 bits of ARGB). Several phone implementations incorrectly blended alpha even with `processAlpha=false`.

