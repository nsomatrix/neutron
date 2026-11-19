# Real-World Java ME Application & Game Testing Corpus

## 1. Overview
Evaluating an emulator exclusively against artificial synthetic unit tests is insufficient. Real-world commercial mobile games frequently employ extreme optimization techniques, undocumented handset edge cases, tight timing loops, manual memory management hacks, custom binary compression, and direct pixel manipulations.

This corpus documents the representative set of legally sourced commercial titles used in the Neutron testing matrix.

---

## 2. Real-World Compatibility Corpus Catalog

| Corpus ID | Game / Application Title | Vendor / Developer | Engine Type | Handset Target | Profile / Config | Expected Observable Behavior |
|---|---|---|---|---|---|---|
| `CORPUS-01` | **Avatar LAPRO** | TeaMobi / Gameloft | 2.5D Isometric RPG | Nokia S60 / S40 | MIDP 2.0 / CLDC 1.0 | Boots cleanly, loads resources, displays animated login/splash screen, accepts numeric/softkey input without throwing uncaught exceptions. |
| `CORPUS-02` | **Bounce Tales** | Nokia / Rovio Mobile | 2D Physics Engine | Nokia Series 40 | MIDP 2.0 / CLDC 1.1 | Boots cleanly, executes physics simulation loop, renders 2D tiled background, responds to arrow/numpad controls. |
| `CORPUS-03` | **Galaxy on Fire 3D** | Fishlabs Entertainment| Abyss Engine 3D | Nokia S60 / SE | MIDP 2.0 / CLDC 1.1 | Parses M3G binary chunk streams, initializes 3D scene graph, renders 3D starfield and ship models at >=30 FPS. |
| `CORPUS-04` | **Need for Speed Carbon**| EA Mobile / Ideaworks3D| 3D Vector Engine | Nokia E71 / E72 | MIDP 2.0 / CLDC 1.1 | Adapts to 320x240 landscape orientation, renders 3D car models and city tracks, executes polyphonic engine sounds. |
| `CORPUS-05` | **Rollercoaster Rush 3D**| Digital Chocolate | 3D Perspective Engine| Nokia S60 / SE | MIDP 2.0 / CLDC 1.1 | Renders 3D coaster tracks, calculates perspective camera movement, maintains consistent frame timing. |
| `CORPUS-06` | **Juiced 2 Hot Import** | Juice Games / THQ | 3D Drift Physics | Nokia S60 / SE | MIDP 2.0 / CLDC 1.1 | Initializes 3D figure models, loads vehicle textures, executes drift physics. |
| `CORPUS-07` | **Outland 3D** | Realore Studios | JSR-184 M3G Engine | Nokia S60 3rd Ed | MIDP 2.0 / CLDC 1.1 | Renders immediate and retained mode M3G models, executes particle explosions and HUD overlays. |
| `CORPUS-08` | **Counter Terrorist** | Inlogic Software | 2D Action Engine | Sony Ericsson / Nokia | MIDP 2.0 / CLDC 1.1 | Renders multi-layer 2D tilemaps, animates character sprites, handles rapid directional input. |

