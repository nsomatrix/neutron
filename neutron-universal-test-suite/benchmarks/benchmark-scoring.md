# Performance Benchmark Scoring Methodology

## 1. Principles
The Neutron Performance Benchmark evaluates actual observable computational and rendering throughput without relying on fabricated metrics or desktop host JVM artifacts.

Every test measures real elapsed wall-clock milliseconds (`System.currentTimeMillis()`) and exact operation counts performed inside guest MIDlet code.

---

## 2. Benchmark Formulas & Equations

### 2.1 Integer Operations Throughput
$$\text{MOps/sec} = \frac{\text{Total Operations}}{(\Delta t / 1000.0) \times 10^6}$$
- Exercises integer multiplication, bitwise shifts, and logical XOR.

### 2.2 Floating-Point Operations Throughput
$$\text{MFlops/sec} = \frac{\text{Total Floating-Point Operations}}{(\Delta t / 1000.0) \times 10^6}$$
- Exercises `Math.sin`, `Math.cos`, and `Math.sqrt` floating-point math conforming to IEEE 754.

### 2.3 Memory Allocation Rate
$$\text{MB/sec} = \frac{\text{Total Bytes Allocated} / 1048576.0}{\Delta t / 1000.0}$$
- Measures rate of dynamic heap churn and garbage collection tolerance.

### 2.4 2D Canvas Rendering Throughput
$$\text{KOps/sec} = \frac{\text{Total Draw Operations}}{(\Delta t / 1000.0) \times 10^3}$$
- Measures off-screen rectangle fills and line rasterizations.

### 2.5 RMS Storage Throughput
$$\text{Records/sec} = \frac{\text{Total Records Persisted}}{\Delta t / 1000.0}$$
- Measures record store index generation and disk serialization rate.

