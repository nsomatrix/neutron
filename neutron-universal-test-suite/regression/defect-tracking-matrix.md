# Known Defects and Regression Tracking Matrix

## 1. Overview
This tracking matrix documents known edge cases, verified regressions, reproduction steps, and architectural notes for historical Java ME behavior within Neutron.

---

## 2. Regression Tracking Table

| Defect ID | Severity | Target Subsystem | Description & Edge Case | Expected Specification Behavior | Current Observed Behavior | Mitigation / Test Coverage |
|---|---|---|---|---|---|---|
| `DEF-001` | Low | LCDUI `Display` | Softkey negative code discrepancy across handset brands | Softkey 1 is `-6` on Nokia, `-1` on Sony Ericsson, `-21` on Motorola | Handled via `Config.getDevicePlatform()` and key mapping translation | Tested in `OemExtensionsMIDlet` & device profiles |
| `DEF-002` | Medium | LCDUI `Canvas` | Fullscreen canvas resizing event sequencing | `sizeChanged(w, h)` must precede subsequent `paint(Graphics g)` call | Event order is synchronous; verify no stale dimensions in cache | Tested in `LcduiGraphicsCanvasMIDlet` |
| `DEF-003` | Medium | JSR-135 `MMAPI` | Rapid audio player recreation under memory pressure | Continuous opening and closing of WAV players must not exhaust audio line buffers | Java Sound clip lines are properly closed in `Player.close()` | Tested in `MmapiAudioMediaMIDlet` & `EnduranceSoakMIDlet` |
| `DEF-004` | High | JSR-75 `File` | Path traversal protection on relative URI paths | Paths containing `/../` must be trapped before disk access | Virtual filesystem normalizes paths and forbids escape from roots | Tested in `OptionalJsrsMIDlet` & `SecuritySandboxMIDlet` |
| `DEF-005` | Medium | MascotCapsule | Fixed-point multiplication truncation error accumulation | Successive matrix operations in Micro3D fixed-point (1.0 = 4096) can drift without normalization | Identity and unit checks reset affine errors | Tested in `Micro3dGraphicsMIDlet` & `MasterHostTestSuite` |
| `DEF-006` | Low | RMS Storage | Record ID reuse after store compaction | Record IDs are strictly monotonically increasing (`getNextRecordID()`) and not reused upon record deletion | IDs increment monotonically until store recreation | Tested in `RmsStorageStressMIDlet` |
| `DEF-007` | Medium | JSR-184 `M3G` | Zlib chunk deflation on malformed `.m3g` binary stream | Corrupted chunk streams must throw `IOException` cleanly without JVM segfault | Adler-32 header verification and safe stream wrapper | Tested in `M3g3dGraphicsMIDlet` & `SecuritySandboxMIDlet` |

