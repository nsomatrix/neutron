# Neutron Universal Test Suite Architecture

## 1. System Overview

The **Neutron Universal Java ME / J2ME Conformance, Compatibility, Stress & Endurance Testing Framework** is an enterprise-grade, specification-aligned testing ecosystem designed to evaluate the **Neutron** Java ME emulator against JCP standards, OEM device extensions, real-world workloads, and long-duration soak conditions.

```mermaid
flowchart TD
    subgraph Host Harness ["Host-Side Test Infrastructure"]
        MTR["run_universal_test_suite.py"]
        HWT["run_host_tests.py (MasterHostTestSuite)"]
        BENCH["run_benchmarks.py"]
        SOAK["endurance-harness.py"]
        VREG["run_visual_regression.py"]
        MOCK["mock_network_servers.py (HTTP/TCP/UDP)"]
    end

    subgraph Emulation Core ["Neutron Headless Emulation Engine"]
        HL["org.neutron.app.Headless"]
        MCL["MIDletClassLoader (ASM Instrumentation)"]
        NUD["NoUiDisplayComponent (Frame Capture)"]
        RMS["RMS Memory/File Storage Engine"]
        GCF["Generic Connection Framework (Loopback Sockets)"]
    end

    subgraph Guest Execution ["Guest J2ME MIDlet Suites (16 Suites)"]
        M1["CLDC Runtime Conformance"]
        M2["MIDP 2.0 Standard APIs"]
        M3["LCDUI UI Components"]
        M4["LCDUI Graphics 2D"]
        M5["Game2D Engine Stress"]
        M6["M3G 3D Graphics (JSR-184)"]
        M7["Micro3D Engine (MascotCapsule v3)"]
        M8["MMAPI Audio & Synthesis (JSR-135)"]
        M9["GCF Networking"]
        M10["RMS Storage Engine Stress"]
        M11["OEM Vendor Extensions"]
        M12["Optional JSRs (75, 82, 172, 256)"]
        M13["Performance Benchmarking"]
        M14["Endurance & 60 FPS Soak"]
        M15["Security & Sandbox Isolation"]
        M16["Cross-API Integration Pipeline"]
    end

    subgraph Verification & Reporting ["Artifacts & Diagnostics"]
        REPORTS["master-conformance-report.md / CSV / JSON"]
        FRAMES["reference-images/*.png"]
        LOGS["Execution Traces & Diagnostics"]
    end

    MTR -->|Launches| HL
    HL -->|Loads via| MCL
    MCL -->|Executes| Guest Execution
    HL -->|Captures Frame via| NUD
    NUD -->|Dumps PNG| FRAMES
    Guest Execution -->|Network Calls| MOCK
    Guest Execution -->|Record Storage| RMS
    Guest Execution -->|Emitted Logs| MTR
    MTR -->|Synthesizes| REPORTS
    HWT -->|White-box Tests| Emulation Core
    BENCH -->|Measures Throughput| HL
    SOAK -->|Monitors Watchdog & FPS| HL
    VREG -->|Verifies Frame Buffers| FRAMES
```

---

## 2. Emulation Execution Pipeline

### 2.1 Direct Bytecode Execution via Host-JIT and ASM Transformation
Unlike interpreted emulators (e.g., KVM), Neutron executes J2ME bytecode directly on the modern host Java Virtual Machine (OpenJDK 21 / HotSpot) by dynamic class transformation:
1. **Dynamic Bytecode Rewrite**: When a MIDlet class is loaded, `MIDletClassLoader` uses ObjectWeb ASM (`ChangeCallsClassVisitor`, `StackSizeMethodVisitor`, `EnhanceThreadCreationVisitor`) to transparently rewrite calls to standard Java APIs, routing them into Neutron's sandboxed microedition runtime classes (e.g. `java.lang.Thread` -> `MIDletThread`).
2. **Deterministic Guest Isolation**: System classes (`java.lang.*`, `javax.microedition.*`) are strictly segregated from host JVM internals. Class delegation rules prevent guest MIDlets from escaping into host JVM reflection or internal classes.

### 2.2 Headless Execution Subsystem
For automated, CI/CD-compatible test execution without display servers (X11/Wayland), Neutron provides `org.neutron.app.Headless`:
- Command invocation:
  ```bash
  java -Djava.awt.headless=true -cp neutron-javase-swing-raw-1.5.0.jar org.neutron.app.Headless --rms memory <test-jar>
  ```
- **Frame Buffer Capture**: When `-Dneutron.headless.dump_frame=<output.png>` is supplied, `NoUiDisplayComponent` intercepts the primary LCDUI canvas paint pipeline and flushes the active ARGB frame buffer directly to a PNG image on disk upon every repaint. This powers the visual regression harness.

---

## 3. Test Harness Subsystems

### 3.1 Master Conformance Runner (`run_universal_test_suite.py`)
- Automatically discovers all 16 packaged test JARs in `test-jars/`.
- Executes each test suite in an isolated headless subshell with customizable execution timeouts.
- Intercepts stdout log markers (`[SUITE-START]`, `[TEST-START]`, `[ASSERT-OK]`, `[ASSERT-FAIL]`, `[TEST-RESULT]`, `[TEST-SUITE-SUMMARY]`, `[TEST-SUITE-STATUS]`).
- Captures reference visual frames to `reference-images/<suite>_frame.png`.
- Produces consolidated reports in Markdown, CSV, and JSON formats.

### 3.2 Host White-Box Test Runner (`run_host_tests.py`)
- Compiles and runs `MasterHostTestSuite.java` against Neutron's host runtime.
- Verifies internal components not directly accessible from guest bytecode:
  - `MIDletClassLoaderConfig`: class delegation policies and relaxed classpath rules.
  - `com.mascotcapsule.micro3d.v3.AffineTrans`: 4096-unit fixed-point trigonometry, vector transforms, and matrix multiplications.
  - `RecordStoreImpl`: RMS storage partitioning and transaction safety.
  - `Runtime`: Host memory metrics, heap headroom, and allocation ceiling under emulation load.

### 3.3 Micro-Benchmark Suite (`run_benchmarks.py`)
- Executes `performance-benchmark.jar` and captures numeric throughput metrics:
  - **Integer Arithmetic Throughput**: Pseudorandom linear congruential bit-shift iterations (MOps/sec).
  - **Floating-Point Precision Math**: Trigonometric and polynomial iterations (MFlops/sec).
  - **Memory Allocation Rate**: Bulk allocation and garbage recovery throughput (MB/sec).
  - **2D Canvas Rendering**: High-frequency graphics primitive drawing operations (KOps/sec).
  - **RMS Record Store I/O**: Transactional record write throughput (Records/sec).

### 3.4 Endurance & Soak Harness (`endurance/endurance-harness.py`)
- Launches `endurance-soak.jar` in headless mode.
- Employs a non-blocking queue reader to stream real-time telemetry pulses:
  - Elapsed soak duration.
  - Instantaneous and smoothed frame rate (FPS).
  - Heap memory utilization (free vs total KB).
- Implements a strict watchdog timer that halts execution if frame rates degrade below threshold or memory leaks occur.

### 3.5 Visual Regression Analyzer (`scripts/run_visual_regression.py`)
- Inspects all PNG frame captures dumped by `NoUiDisplayComponent`.
- Validates image dimensions, non-zero file sizes, and color entropy.
- Ensures the graphics pipeline rendered actual visible primitives rather than blank or corrupted buffers.

### 3.6 Offline Network Mock Fixtures (`network-fixtures/mock_network_servers.py`)
- Lightweight, zero-external-dependency Python daemon providing:
  - HTTP Server (Port 18458) responding to `/status`, `/data`, `/echo`, `/404`, and POST bodies.
  - TCP Echo Server (Port 18459) for bidirectional streaming socket verification.
  - UDP Echo Server (Port 18460) for datagram packet testing.

