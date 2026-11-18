# NEUTRON — INITIAL AUDIT FINDINGS

## 1. BASELINE TESTING AND VERIFICATION SUMMARY

As part of Stage B baseline verification, all modules, build targets, native compilation pipelines, and test suites across the repository were executed in a clean environment.

### 1.1 Gradle Automated Test Matrix

| Module | Test Target | Status | Notes / Failure Signature |
| :--- | :--- | :--- | :--- |
| `:neutron:api:cldcapi10` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:api:cldcapi11` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:api:midpapi20` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-cldc` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-midp` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-javase` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-javase-swing` | Unit Tests | **FAILED / HUNG** | **Hangs indefinitely in `DropTransferHandlerTest.testImportUnsupportedFileGraceful()`** |
| `:neutron:neutron-core` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-jsr-75` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-jsr-82` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-jsr-135` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-jsr-172` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-jsr-179` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-jsr-184` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-jsr-211` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-jsr-226` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-jsr-234` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-jsr-239` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-jsr-256` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-micro3d` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-opengl` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-nokiaui` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-siemensapi` | Unit Tests | **PASSED** | 0 failures, 0 errors |
| `:neutron:neutron-oem-apis` | Unit Tests | **PASSED** | 0 failures, 0 errors |

### 1.2 Automated Headless Battle Test Execution

All 11 sample and benchmark MIDlets executed through `tools/battle_test_runner.py` for 5.0s run times:
- `JBenchmark2.jar` (UI & System Benchmark): **PASS**
- `JBenchmark1.1.1.jar` (2D Canvas Benchmark): **PASS**
- `outland3d_qbw6u2v9.jar` (M3G 3D Engine): **PASS**
- `galaxy_on_fire_3d_240x320-40670.jar` (Abyss 3D Engine): **PASS**
- `NEEDFORSPEEDCARBON_E72.jar` (Ideaworks 3D Engine): **PASS**
- `Avatar_LAPRO.jar` (Gameloft 2D Engine): **PASS**
- `juiced_2_hot_import_143742.jar` (THQ 3D Engine): **PASS**
- `rollrush_en_fr_it_de_es_pt_nokian8.jar` (Digital Chocolate 2D Engine): **PASS**
- `Bounce-Tales_J2ME_EN_v204.jar` (Nokia 2D Physics Engine): **PASS**
- `KnightOnline_v403.jar` (RPG Engine): **PASS**
- `contrterro_Ab4yGCVS.jar` (Arcade Shooter Engine): **PASS**

### 1.3 Native C99 Engine Compilation
- Successfully built `libjavam3g.so` via `build.sh` using GCC 14.2.0 on Linux x86_64.
- Output binary size: 339 KB.

### 1.4 Web Portal Build
- Successfully ran Next.js 16 build (`npm run build`) in `neutron-emu/`. All 26 static routes generated with zero TypeScript or bundling errors.

---

## 2. PRELIMINARY DEFECT CATALOG

The following defects were discovered during code inspection and baseline execution:

### 2.1 [CRITICAL - P0] AWT Modal Dialog Freeze in Automated / Headless Test Runs
- **Component**: `neutron-javase-swing` (`DropTransferHandler.java`, `SwingErrorMessageDialogPanel.java`)
- **Impact**: Test runner hangs indefinitely when dropping unsupported files due to synchronous modal dialog blocking the thread on `Dialog.show()`.
- **Root Cause**: `Message.warn()` calls `SwingDialogWindow.show(..., modal=true)` without checking whether the runtime is running in headless mode or within an automated test harness.

### 2.2 [CRITICAL - P0] Native M3G Segfault on Asynchronous Finalization
- **Component**: `neutron-jsr-184` (`Platform.java`, `Object3D.java`, native `libjavam3g.so`)
- **Impact**: JVM SIGSEGV in `m3gDeleteObject` during garbage collection (documented in `hs_err_pid31958.log`).
- **Root Cause**: Finalizer threads call native deallocator on stale pointers or after the native M3G interface instance has been destroyed.

### 2.3 [HIGH - P1] Alpha Blending Math Inaccuracy & Color Corruption
- **Component**: `neutron-javase-swing` (`J2SEDisplayGraphics.java`)
- **Impact**: Transparent layers and sprites gradually darken rendering targets; inverted or corrupt background compositing.
- **Root Cause**: In integer blending, `((val * a) >> 8)` truncates towards zero (dividing by 256 rather than 255), compounding darkening errors. Fast path for `srcA == 0` and `srcA == 255` missing.

### 2.4 [HIGH - P1] Missing Nokia DirectGraphics Manipulations & Empty Triangle Stubs
- **Component**: `neutron-nokiaui` (`DirectGraphicsImp.java`)
- **Impact**: `ArrayIndexOutOfBoundsException` or corrupted rendering when games use `FLIP_VERTICAL | ROTATE_90` or `FLIP_VERTICAL | ROTATE_270`. `drawTriangle` and `fillTriangle` are no-op stubs.
- **Root Cause**: Incomplete switch statement cases in `getPixels` / `drawPixels`.

### 2.5 [HIGH - P1] MMAPI VolumeControl Decoupled & MidiAudioPlayer Silent Failure
- **Component**: `neutron-jsr-135` (`MidiAudioPlayer.java`, `J2SEVolumeControl.java`)
- **Impact**: Games playing MIDI music fail silently without audio. Volume controls do not affect audio output. Leaks `Synthesizer` resources.
- **Root Cause**: `MidiAudioPlayer.open()` returns `false` unconditionally; lifecycle methods are unhandled; volume control only changes a local integer variable without applying gain controls to `Clip` or MIDI channels.

### 2.6 [HIGH - P1] Non-Atomic RMS Writes Causing File Truncation Data Loss
- **Component**: `neutron-javase` (`FileRecordStoreManager.java`)
- **Impact**: User game saves destroyed if JVM terminates, encounters an exception, or is killed during save operations.
- **Root Cause**: Directly opening `FileOutputStream(recordStoreFile)` truncates the save file before writing data.

### 2.7 [MEDIUM - P2] JSR-75 FileConnection Handle Leak
- **Component**: `neutron-jsr-75` (`FileSystemFileConnection.java`)
- **Impact**: File locks remain active on host filesystem on Windows, preventing file deletion or renaming.
- **Root Cause**: `RandomAccessFile` is created to obtain a `FileDescriptor` for `FileOutputStream`, but the `RandomAccessFile` itself is never closed.

### 2.8 [MEDIUM - P2] Java 21 `Thread.stop()` UnsupportedOperationException
- **Component**: `neutron-javase` (`MIDletThread.java`)
- **Impact**: `UnsupportedOperationException` thrown when terminating hung or stopped MIDlet threads on modern JVMs.
- **Root Cause**: Modern Java versions (Java 20+) completely remove `Thread.stop()`.

### 2.9 [LOW - P3] Launcher Resource Handling & Non-Atomic Cache File Writing
- **Component**: `neutron-core` (`CoreLauncher.java`), `neutron-javase-swing` (`SwingLauncher.java`)
- **Impact**: File descriptor leak during manifest parsing and corrupted JAR cache if network downloads are interrupted.
- **Root Cause**: Plain `FileInputStream` usage without try-with-resources; direct write to cache target rather than atomic swap.

