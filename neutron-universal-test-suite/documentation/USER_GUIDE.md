# Neutron Universal Test Suite User Guide

## 1. Prerequisites & Environment Setup

### Required Tools
- **Linux** (Debian, Ubuntu, Fedora, or equivalent kernel 5.x+)
- **OpenJDK 21+** (`javac`, `java`, `jar`)
- **Python 3.10+**
- **Neutron Emulator Build Artifact**: `neutron/neutron-javase-swing/build/tmp/rawJar/neutron-javase-swing-raw-1.5.0.jar`

Ensure the raw JAR is compiled by running Gradle in the root repository if not already built:
```bash
cd /home/manish/projects/neutron
./gradlew :neutron-javase-swing:rawJar
```

---

## 2. Compiling and Packaging Test MIDlets

The test suite includes 16 self-verifying MIDlet suites. To compile and package all test MIDlets into valid `.jar` and `.jad` files:

```bash
cd /home/manish/projects/neutron
python3 neutron-universal-test-suite/scripts/build_test_midlets.py
```

### Compiler Script Behavior:
1. Automatically scans `neutron/` modules and builds the guest Java ME compilation classpath.
2. Compiles guest source files targeting Java 8 bytecode compatibility (`-source 8 -target 8`).
3. Injects standard `MANIFEST.MF` files with MIDP 2.0 and CLDC 1.1 attributes (`MIDlet-1`, `MIDlet-Name`, `MIDlet-Version`, `MIDlet-Vendor`).
4. Generates corresponding `.jad` application descriptor files in `neutron-universal-test-suite/test-jads/`.

---

## 3. Running Test Suites

### 3.1 Running the Full Universal Test Suite
To run all 16 test suites with automated frame capture and reporting:

```bash
python3 neutron-universal-test-suite/scripts/run_universal_test_suite.py
```

#### Command-Line Options:
| Flag | Description | Default |
| :--- | :--- | :--- |
| `--subsystem <name>` | Execute only suites matching the specified name filter (e.g. `m3g-3d-graphics`, `cldc`) | `all` |
| `--timeout <sec>` | Timeout in seconds allocated per individual suite | `12` |

Example of executing only the 3D graphics suites:
```bash
python3 neutron-universal-test-suite/scripts/run_universal_test_suite.py --subsystem 3d
```

### 3.2 Running Host-Side White-Box Tests
To execute host-side white-box tests against classloader configuration, fixed-point math, and RMS isolation:

```bash
python3 neutron-universal-test-suite/scripts/run_host_tests.py
```

### 3.3 Running Performance Benchmarks
To measure host-JIT execution throughput, memory allocation rate, 2D drawing performance, and RMS I/O:

```bash
python3 neutron-universal-test-suite/scripts/run_benchmarks.py
```

### 3.4 Running Long-Duration Endurance & Soak Tests
To perform stability and memory leak verification over extended runtime:

```bash
python3 neutron-universal-test-suite/endurance/endurance-harness.py --duration 30
```

### 3.5 Verifying Visual Regression & Frame Integrity
To check captured frame buffers for rendering corruption or blank surfaces:

```bash
python3 neutron-universal-test-suite/scripts/run_visual_regression.py
```

---

## 4. Manual / Interactive Headless Execution

To run an individual test JAR directly in headless mode and inspect raw standard output:

```bash
java -Djava.awt.headless=true \
     -Dneutron.headless.dump_frame=/tmp/my_frame.png \
     -cp neutron/neutron-javase-swing/build/tmp/rawJar/neutron-javase-swing-raw-1.5.0.jar \
     org.neutron.app.Headless \
     --rms memory \
     neutron-universal-test-suite/test-jars/m3g-3d-graphics.jar
```

### Useful JVM & Neutron Properties:
- `-Djava.awt.headless=true`: Ensures no X11/Wayland windows are opened.
- `-Dneutron.headless.dump_frame=<path>`: Directs `NoUiDisplayComponent` to dump the current frame buffer to a PNG image upon repaint.
- `--rms memory`: Configures transient in-memory RMS to isolate test runs.
- `--rms file`: Persists RMS record stores into the user home `.neutron/` directory.

---

## 5. Mock Network Server Setup

For testing networking protocols (HTTP, TCP, UDP) offline:

```bash
# Start mock servers in the background
python3 neutron-universal-test-suite/network-fixtures/mock_network_servers.py &
MOCK_PID=$!

# Run networking test suite
python3 neutron-universal-test-suite/scripts/run_universal_test_suite.py --subsystem gcf-networking

# Stop mock servers
kill $MOCK_PID
```

---

## 6. Generated Reports & Artifacts

After running the test suite, generated reports are stored in:
- **Markdown Report**: `neutron-universal-test-suite/reports/master-conformance-report.md`
- **CSV Results Matrix**: `neutron-universal-test-suite/reports/test-results-matrix.csv`
- **JSON Structured Data**: `neutron-universal-test-suite/reports/test-results-matrix.json`
- **Captured Visual Frames**: `neutron-universal-test-suite/reference-images/*.png`

