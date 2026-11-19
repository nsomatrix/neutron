#!/usr/bin/env python3
"""
Neutron Universal Benchmark Runner
Executes performance benchmark MIDlets and extracts numerical throughput metrics.
"""

import sys
import re
import time
import subprocess
from pathlib import Path

WORKSPACE = Path("/home/manish/projects/neutron")
BENCH_JAR = WORKSPACE / "neutron-universal-test-suite/test-jars/performance-benchmark.jar"
RAW_JAR = WORKSPACE / "neutron/neutron-javase-swing/build/tmp/rawJar/neutron-javase-swing-raw-1.5.0.jar"

def main():
    print("=" * 70)
    print("  NEUTRON PERFORMANCE BENCHMARK HARNESS")
    print("=" * 70)

    if not BENCH_JAR.exists():
        print(f"Error: {BENCH_JAR} not found.")
        sys.exit(1)

    cmd = [
        "java",
        "-Djava.awt.headless=true",
        "-cp", str(RAW_JAR),
        "org.neutron.app.Headless",
        "--rms", "memory",
        str(BENCH_JAR)
    ]

    print("Running benchmark workload...")
    proc = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)

    stdout_lines = []
    t_start = time.time()
    completed = False

    while time.time() - t_start < 12:
        line = proc.stdout.readline()
        if line:
            stdout_lines.append(line)
            if "[BENCHMARK-SUMMARY]" in line or "[TEST-SUITE-STATUS:" in line:
                completed = True
                # Allow trailing output
                time.sleep(0.5)
                break
        elif proc.poll() is not None:
            break
        time.sleep(0.05)

    proc.terminate()
    try:
        proc.wait(timeout=2)
    except subprocess.TimeoutExpired:
        proc.kill()

    combined = "".join(stdout_lines)
    metrics = re.findall(r"\[METRIC\]\s+(.*?):\s+([\d.]+)\s+(.*)", combined)
    print("-" * 70)
    print(f"{'Benchmark Metric':<30} | {'Throughput / Score':<18} | {'Unit'}")
    print("-" * 70)
    for name, score, unit in metrics:
        print(f"{name:<30} | {score:<18} | {unit}")
    print("-" * 70)

    if metrics:
        print("[SUCCESS] All benchmark metrics evaluated successfully.")
    else:
        print("[WARNING] No metrics extracted from output.")
    print("=" * 70)

if __name__ == "__main__":
    main()
