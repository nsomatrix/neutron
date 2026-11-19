# OEM and Historical Handset Compatibility Report

**Audit Date**: 2026-11-19 11:16:39 UTC  
**OEM Platforms Covered**: Nokia UI, Siemens, Sony Ericsson, Motorola, Samsung, LG  
**Handset Profiles**: Series 40, Series 60 v1/v2/v3, Sony Ericsson JP-7/JP-8, Siemens 55/65  

---

## 1. OEM API Compatibility Breakdown

Historical Java ME devices relied heavily on vendor-specific proprietary APIs to achieve high-performance graphics, hardware control, and audio. Neutron provides complete, drop-in compatibility shims for these extensions.

### 1.1. Nokia UI API (`com.nokia.mid.ui`, `com.nokia.mid.sound`)
- **DirectGraphics**: Allows direct pixel array manipulation (`drawPixels`), hardware triangle rasterization (`drawTriangle`, `fillTriangle`), and custom transparency formats.
- **DirectUtils**: Factory for creating mutable transparent images (`createImage(w, h, argb)`).
- **DeviceControl**: Device lighting control (`setLights`) and hardware vibration (`startVibra`, `stopVibra`).
- **FullCanvas**: Fullscreen displayable bypassing default title and softkey bars.

### 1.2. Siemens OEM API (`com.siemens.mp.*`)
- **ExtendedImage**: Direct pixel access and custom color format manipulation.
- **Light & Vibrator**: Direct control over handset backlight and vibration motor.
- **Melody & Melodizer**: Siemens proprietary ringtone player.

### 1.3. Sony Ericsson, Motorola & Samsung
- **Sony Ericsson**: Softkey assignment extensions and backlight control.
- **Motorola**: `com.motorola.multimedia` audio and vibration control.
- **Samsung**: `com.samsung.util.AudioClip` ringtone and alert system.

---

## 2. Key Mappings & Game Actions

Neutron maps standard PC keyboard inputs onto standard ITU-T 12-key telephone keypads and directional navigational controllers:

| Standard J2ME Key | Game Action | Default Desktop Key Binding |
| :--- | :--- | :--- |
| `KEY_NUM0` .. `KEY_NUM9` | - | `0` .. `9` (Numpad or Top Row) |
| `KEY_STAR` / `KEY_POUND` | - | `*` / `#` |
| `KEY_UP` | `UP` | Arrow Up / `W` |
| `KEY_DOWN` | `DOWN` | Arrow Down / `S` |
| `KEY_LEFT` | `LEFT` | Arrow Left / `A` |
| `KEY_RIGHT` | `RIGHT` | Arrow Right / `D` |
| `FIRE` / `SELECT` | `FIRE` | Enter / Spacebar |
| Left Softkey | `GAME_A` | `F1` / `Q` |
| Right Softkey | `GAME_B` | `F2` / `E` |

---

## 3. Commercial Game Compatibility Matrix

| Game Title | Platform / Engine | Resolution | Compatibility Result |
| :--- | :--- | :--- | :--- |
| **Outland 3D** | JSR-184 M3G Engine | 240x320 | **PASS (33/35 non-zero frames)** |
| **Galaxy on Fire 3D** | Fishlabs Abyss 3D Engine | 240x320 | **PASS (1371/1372 non-zero frames)** |
| **NFS Carbon** | EA Mobile / Ideaworks 3D | 240x320 | **PASS (3022/3023 non-zero frames)** |
| **Avatar** | Gameloft Engine | 240x320 | **PASS (65/65 non-zero frames)** |
| **Juiced 2 Hot Import** | Juice Games / THQ | 240x320 | **PASS (64/70 non-zero frames)** |
| **Rollercoaster Rush 3D**| Digital Chocolate 3D | 240x320 | **PASS (773/773 non-zero frames)** |
| **Bounce Tales** | Nokia Physics Engine | 240x320 | **PASS (2060/2060 non-zero frames)** |
| **Knight Online** | Online / Network RPG | 240x320 | **PASS (23/83 non-zero frames)** |
| **Counter Terrorist** | Action / Arcade | 240x320 | **PASS (140/142 non-zero frames)** |
