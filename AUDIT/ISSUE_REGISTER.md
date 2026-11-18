# NEUTRON — ISSUE REGISTER

This document tracks all identified defects, architectural flaws, regressions, and stability bottlenecks discovered during the audit of the Neutron Java ME emulator.

---

### ISSUE-001: Modal AWT Dialog Blocks Thread Execution During Headless and Automated Unit Testing
- **Severity**: **P0 (Critical)**
- **Status**: **Remediated & Verified**
- **Affected Components**: `neutron-javase-swing`
- **Affected Files**:
  - `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/DropTransferHandler.java`
  - `neutron/neutron-javase-swing/src/main/java/org/neutron/app/ui/swing/SwingErrorMessageDialogPanel.java`
- **Impact**: Completely halts automated Gradle test runners (`./gradlew test`) and CI test workers when unsupported file formats are tested.
- **Root Cause**: `DropTransferHandler.handleFileList()` displays a user error modal dialog via `Message.warn()` -> `SwingErrorMessageDialogPanel.show()` -> `SwingDialogWindow.show(..., modal=true)`. When running in headless environments or automated test runners, the modal dialog blocks the thread on `Dialog.show()`, causing test suite execution to hang indefinitely.
- **Reproduction**: Run `./gradlew :neutron:neutron-javase-swing:test --tests org.neutron.app.ui.swing.DropTransferHandlerTest.testImportUnsupportedFileGraceful`. The test never completes and hangs until terminated.
- **Remediation Plan**:
  1. Add detection for headless mode via `GraphicsEnvironment.isHeadless()` or automated test property `Boolean.getBoolean("neutron.test.mode")`.
  2. In `SwingErrorMessageDialogPanel`, bypass modal dialog display if headless or testing, logging the warning instead.
  3. In `DropTransferHandler`, suppress UI popups if test context is detected.
- **Verification Criteria**: `./gradlew :neutron:neutron-javase-swing:test` must complete 100% of its tests cleanly without hanging.

---

### ISSUE-002: Native JSR-184 M3G JVM Crash / SIGSEGV on Asynchronous Object Finalization
- **Severity**: **P0 (Critical)**
- **Status**: **Remediated & Verified**
- **Affected Components**: `neutron-jsr-184`, native C99 M3G engine
- **Affected Files**:
  - `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Object3D.java`
  - `neutron/neutron-extensions/neutron-jsr-184/src/main/java/javax/microedition/m3g/Platform.java`
  - `neutron/neutron-extensions/neutron-jsr-184/src/main/cpp/m3g/src/m3g_interface.c`
- **Impact**: Hard JVM crash (SIGSEGV in `m3gDeleteObject`) during garbage collection when running 3D titles or tearing down MIDlet instances (observed in `hs_err_pid31958.log`).
- **Root Cause**: `Object3D.finalize()` delegates to `Platform._finalizeObject(handle)`. When the finalizer thread runs asynchronously during or after MIDlet teardown, the native `Interface` handle is either already invalidated, or concurrent access to the C reference-counting table without mutual exclusion causes use-after-free or double free.
- **Reproduction**: Repeatedly instantiate and abandon large M3G scene graphs and invoke `System.gc(); System.runFinalization();` under high allocation load.
- **Remediation Plan**:
  1. Add reference validity check and nullification guards in `Object3D` before dispatching native deallocation.
  2. Implement safe shutdown lifecycle in `Platform` that unregisters live native handles when the M3G interface is disposed, preventing post-mortem finalizer crashes.
  3. Ensure thread-safe synchronization across JNI boundaries for object deletion.
- **Verification Criteria**: Heavy M3G allocation and GC stress test runs without triggering native memory faults or SIGSEGV.

---

### ISSUE-003: Software Graphics Alpha Blending Pixel Degradation and Static Color Concurrency Risk
- **Severity**: **P1 (High)**
- **Status**: **Remediated & Verified**
- **Affected Components**: `neutron-javase-swing`
- **Affected Files**:
  - `neutron/neutron-javase-swing/src/main/java/org/neutron/device/j2se/J2SEDisplayGraphics.java`
- **Impact**: Visual rendering inaccuracies; translucent sprites progressively darken destination buffers due to integer arithmetic truncation. Potential thread contention or stale color objects in static cache.
- **Root Cause**:
  1. In `blendPixel()`, arithmetic division by 256 (`>> 8`) rather than 255 truncates color channels towards black when compositing (`(src * a) >> 8` maps 255 to 253 when `a=255`).
  2. Missing fast paths for `srcA == 0` (no-op) and `srcA == 255` (direct copy).
  3. `TRANS_MIRROR_ROT270` coordinate offset calculation causes boundary clipping defects.
- **Reproduction**: Render layered transparent sprites on top of each other and inspect destination pixel values against reference RGBA compositing math.
- **Remediation Plan**:
  1. Fix `blendPixel()` to use mathematically correct integer rounding: `((val * a + 128) >> 8)`.
  2. Add fast-path branch for `srcA == 0` (immediate return `destPixel`) and `srcA == 255` (immediate return `srcPixel`).
  3. Correct coordinate mapping for `TRANS_MIRROR_ROT270`.
  4. Ensure thread-safe color cache lookup.
- **Verification Criteria**: Sprite blending unit test confirms bit-accurate color reproduction across all alpha levels.

---

### ISSUE-004: Missing Nokia DirectGraphics Transformation Mappings and Incomplete Triangle Stubs
- **Severity**: **P1 (High)**
- **Status**: **Remediated & Verified**
- **Affected Components**: `neutron-nokiaui`
- **Affected Files**:
  - `neutron/neutron-extensions/neutron-nokiaui/src/main/java/com/nokia/mid/ui/DirectGraphicsImp.java`
- **Impact**: `ArrayIndexOutOfBoundsException` or graphical corruption when Nokia UI MIDlets invoke `drawPixels` / `getPixels` with `FLIP_VERTICAL | ROTATE_90` or `FLIP_VERTICAL | ROTATE_270`. `drawTriangle` and `fillTriangle` calls produce no visual output.
- **Root Cause**:
  1. `DirectGraphicsImp.java` swaps `dW` and `dH` for vertical flip + 90/270 rotations, but the `switch(manipulation)` block omits these combinations, falling through to default `dstX = col; dstY = row;` which violates buffer bounds.
  2. `drawTriangle` and `fillTriangle` are no-op empty stubs despite underlying Graphics polygon capabilities.
- **Reproduction**: Call `directGraphics.drawPixels(..., FLIP_VERTICAL | ROTATE_90)`.
- **Remediation Plan**:
  1. Add explicit cases for `FLIP_VERTICAL | ROTATE_90` and `FLIP_VERTICAL | ROTATE_270` in the transformation switch.
  2. Implement `drawTriangle` and `fillTriangle` using Java standard `drawPolygon` / `fillPolygon` with 3 vertices.
- **Verification Criteria**: Unit tests exercising all 16 Nokia DirectGraphics manipulation combinations and triangle rendering succeed without exceptions.

---

### ISSUE-005: MMAPI MidiAudioPlayer Silent Failure and Uncoupled VolumeControl
- **Severity**: **P1 (High)**
- **Status**: **Remediated & Verified**
- **Affected Components**: `neutron-jsr-135`
- **Affected Files**:
  - `neutron/neutron-extensions/neutron-jsr-135/src/main/java/javax/microedition/media/MidiAudioPlayer.java`
  - `neutron/neutron-extensions/neutron-jsr-135/src/main/java/org/neutron/media/control/J2SEVolumeControl.java`
- **Impact**: J2ME games using MIDI audio fail to play music; volume controls do not affect hardware playback levels; unclosed synthesizers leak audio resources.
- **Root Cause**:
  1. `MidiAudioPlayer.open()` returns `false` unconditionally.
  2. `realize()` and `prefetch()` are empty stubs that fail to advance the player state, causing subsequent state transitions to fail.
  3. `close()` fails to set `state = CLOSED` and does not properly close or release the `Synthesizer`.
  4. `J2SEVolumeControl` only sets a local integer field without modifying `FloatControl.Type.MASTER_GAIN` on `Clip` or MIDI channel volumes.
- **Reproduction**: Instantiate a MIDI player via `Manager.createPlayer(stream, "audio/midi")`, prefetch, set volume to 50, and call `start()`. No audio plays and volume is ineffective.
- **Remediation Plan**:
  1. Implement complete state machine transitions in `MidiAudioPlayer` (`UNREALIZED` -> `REALIZED` -> `PREFETCHED` -> `STARTED` -> `CLOSED`).
  2. Return `true` upon successful initialization in `open()`.
  3. Ensure `close()` terminates sequencer/synthesizer instances and marks state as `CLOSED`.
  4. Hook `J2SEVolumeControl` to adjust actual hardware master gain on `Clip` and MIDI channel volumes.
- **Verification Criteria**: MMAPI unit test verifies state transitions, volume control gain calculation, and proper synthesizer teardown.

---

### ISSUE-006: Non-Atomic RMS File Persistence Causing Save Data Loss
- **Severity**: **P1 (High)**
- **Status**: **Remediated & Verified**
- **Affected Components**: `neutron-javase`
- **Affected Files**:
  - `neutron/neutron-javase/src/main/java/org/neutron/app/util/FileRecordStoreManager.java`
- **Impact**: Permanent corruption and loss of user save games and settings if a crash, exception, or power cut occurs during RMS write operations.
- **Root Cause**: `FileRecordStoreManager.saveRecordStore()` instantiates `FileOutputStream(recordStoreFile)` directly on the destination file, truncating the file to 0 bytes before writing. If writing fails midway, the entire record store is lost. Streams are also not closed safely via try-with-resources.
- **Reproduction**: Simulate an IO error or thread interrupt during `RecordStore.closeRecordStore()`. The existing `.rms` file is truncated to 0 bytes.
- **Remediation Plan**:
  1. Write to a temporary file (`recordStoreFile.getPath() + ".tmp"`).
  2. Use try-with-resources to guarantee all buffers and streams are flushed and closed before moving.
  3. Atomically replace the target file using `Files.move(..., ATOMIC_MOVE, REPLACE_EXISTING)`.
  4. Sanitize suite and store names against directory traversal vulnerabilities.
- **Verification Criteria**: Unit test verifies atomic save semantics and resilience against interrupted writes.

---

### ISSUE-007: JSR-75 FileSystemFileConnection RandomAccessFile Handle Leak
- **Severity**: **P2 (Medium)**
- **Status**: **Remediated & Verified**
- **Affected Components**: `neutron-jsr-75`
- **Affected Files**:
  - `neutron/neutron-extensions/neutron-jsr-75/src/main/java/org/neutron/cldc/file/FileSystemFileConnection.java`
- **Impact**: Open file handles are leaked on the host operating system, causing file lock failures when applications attempt to delete, move, or re-open files (especially on Windows).
- **Root Cause**: `FileSystemFileConnection.openOutputStream()` creates a `RandomAccessFile` to obtain a `FileDescriptor`, wraps it in a `FileOutputStream`, but never closes the `RandomAccessFile` itself when the stream is closed.
- **Reproduction**: Open an output stream on a file connection, write bytes, close the stream, and attempt to delete the file. The file remains locked on Windows.
- **Remediation Plan**:
  1. Wrap the output stream in a delegating stream that closes both the `FileOutputStream` and the parent `RandomAccessFile` upon closure.
- **Verification Criteria**: FileConnection test demonstrates that closing the stream immediately unlocks the underlying file for deletion.

---

### ISSUE-008: Java 21+ Incompatibility in Thread.stop() Invocation
- **Severity**: **P2 (Medium)**
- **Status**: **Remediated & Verified**
- **Affected Components**: `neutron-javase`
- **Affected Files**:
  - `neutron/neutron-javase/src/main/java/org/neutron/app/util/MIDletThread.java`
- **Impact**: In modern JVMs (Java 20 and later), `Thread.stop()` throws `UnsupportedOperationException`, breaking the emulator's ability to cleanly terminate stuck MIDlet threads on shutdown.
- **Root Cause**: OpenJDK has permanently removed the deprecated `Thread.stop()` method.
- **Reproduction**: Call `MIDletThread.stop()` under Java 21. Throws `java.lang.UnsupportedOperationException`.
- **Remediation Plan**:
  1. Replace direct `Thread.stop()` with cooperative interruption (`Thread.interrupt()`).
  2. Implement timed join wait with thread group detachment and resource reclamation.
- **Verification Criteria**: Running under Java 21 shuts down threads without throwing `UnsupportedOperationException`.

---

### ISSUE-009: Unclosed Stream Descriptors and Non-Atomic Cache Writing in Launchers
- **Severity**: **P2 (Medium)**
- **Status**: **Remediated & Verified**
- **Affected Components**: `neutron-core`, `neutron-javase-swing`
- **Affected Files**:
  - `neutron/neutron-core/src/main/java/org/neutron/app/CoreLauncher.java`
  - `neutron/neutron-javase-swing/src/main/java/org/neutron/app/SwingLauncher.java`
- **Impact**: Leaked file descriptors when parsing JAD/JAR manifests; corrupted cache files when network downloads of JAD/JAR URLs fail midway.
- **Root Cause**: `FileInputStream` opened directly without try-with-resources; network URL downloads stream directly into the final cached file.
- **Reproduction**: Provide a malformed JAD or interrupted URL to the launcher. The file descriptor remains unclosed or a partial file is left in the cache.
- **Remediation Plan**:
  1. Use try-with-resources for all file and stream operations.
  2. Download network assets to temporary `.part` files and rename atomically upon successful completion.
- **Verification Criteria**: Interrupted downloads leave cache uncorrupted; all streams close cleanly.

---

### ISSUE-010: Dormant Test Suite in `neutron-test-midlet`
- **Severity**: **P2 (Medium)**
- **Status**: **Remediated & Verified**
- **Affected Components**: Build system, `neutron-tests`
- **Affected Files**:
  - `settings.gradle`
  - `neutron/neutron-tests/neutron-test-midlet/build.gradle`
- **Impact**: Critical MIDP UI unit tests (`ItemsOnFormTest.java`, `UnitTestsMIDLet.java`) are not being built or executed during Gradle test runs.
- **Root Cause**: `neutron-test-midlet` is present in the repository but omitted from root `settings.gradle`.
- **Reproduction**: Check `settings.gradle`. Module `:neutron:neutron-tests:neutron-test-midlet` is missing.
- **Remediation Plan**:
  1. Add `:neutron:neutron-tests:neutron-test-midlet` to `settings.gradle`.
  2. Ensure dependencies and tasks are properly configured to run in automated test suites.
- **Verification Criteria**: `./gradlew test` discovers and executes the test MIDlet module.

