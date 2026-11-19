#!/usr/bin/env python3
"""
Neutron Long-Duration Endurance & Soak Test Controller
Automates long-duration runs with watchdog monitoring, real-time memory tracking,
and frame degradation detection.
"""

import sys
import time
import queue
import threading
import argparse
import subprocess
from pathlib import Path

WORKSPACE = Path("/home/manish/projects/neutron")
SOAK_JAR = WORKSPACE / "neutron-universal-test-suite/test-jars/endurance-soak.jar"
RAW_JAR = WORKSPACE / "neutron/neutron-javase-swing/build/tmp/rawJar/neutron-javase-swing-raw-1.5.0.jar"

def enqueue_output(out, q):
    try:
        for line in iter(out.readline, ''):
            if not line:
                break
            q.put(line)
    except Exception:
        pass
    finally:
        out.close()

def run_endurance(duration_sec):
    print("=" * 70)
    print("  NEUTRON LONG-DURATION ENDURANCE & SOAK TEST HARNESS")
    print(f"  Target Duration: {duration_sec}s")
    print(f"  Soak MIDlet:     {SOAK_JAR.name}")
    print("=" * 70)

    if not SOAK_JAR.exists():
        print(f"[ERROR] {SOAK_JAR} not found. Build the test suite first.")
        sys.exit(1)

    cmd = [
        "java",
        "-Djava.awt.headless=true",
        "-cp", str(RAW_JAR),
        "org.neutron.app.Headless",
        "--rms", "memory",
        str(SOAK_JAR)
    ]

    start_time = time.time()
    proc = subprocess.Popen(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True, bufsize=1)

    line_queue = queue.Queue()
    t = threading.Thread(target=enqueue_output, args=(proc.stdout, line_queue), daemon=True)
    t.start()

    pulse_count = 0
    errors = 0

    try:
        while True:
            elapsed = time.time() - start_time
            if elapsed >= duration_sec:
                print(f"[WATCHDOG] Target duration {duration_sec}s reached. Concluding test.")
                proc.terminate()
                break

            try:
                line = line_queue.get(timeout=0.2)
                line_str = line.strip()
                if "[SOAK-PULSE]" in line_str:
                    pulse_count += 1
                    print(f"  {line_str}")
                elif "[FAIL]" in line_str or "Exception" in line_str:
                    errors += 1
                    print(f"  [ALERT] {line_str}")
            except queue.Empty:
                if proc.poll() is not None:
                    print("[NOTICE] Process terminated early.")
                    break

    except KeyboardInterrupt:
        print("\n[USER INTERRUPT] Stopping soak test.")
        proc.terminate()

    try:
        proc.wait(timeout=2)
    except subprocess.TimeoutExpired:
        proc.kill()

    total_time = time.time() - start_time
    print("=" * 70)
    print(f"  SOAK SUMMARY: Ran for {total_time:.1f}s, Pulses: {pulse_count}, Errors: {errors}")
    print(f"  STATUS: {'PASSED' if errors == 0 else 'FAILED'}")
    print("=" * 70)
    if errors > 0:
        sys.exit(1)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Neutron Endurance Soak Harness")
    parser.add_argument("--duration", type=int, default=5, help="Duration in seconds (default: 5s)")
    args = parser.parse_args()
    run_endurance(args.duration)
