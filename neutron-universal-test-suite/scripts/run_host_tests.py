#!/usr/bin/env python3
"""
Neutron Universal Host Test Runner
Executes host-side automated white-box tests against JVM internals, classloaders, and engine subsystems.
"""

import sys
import subprocess
from pathlib import Path

WORKSPACE = Path("/home/manish/projects/neutron")
HOST_TEST_DIR = WORKSPACE / "neutron-universal-test-suite/host-tests"
RAW_JAR = WORKSPACE / "neutron/neutron-javase-swing/build/tmp/rawJar/neutron-javase-swing-raw-1.5.0.jar"
MICRO3D_JAR = WORKSPACE / "neutron/neutron-extensions/neutron-micro3d/build/libs/neutron-micro3d-1.5.0.jar"
BUILD_DIR = HOST_TEST_DIR / "build"

def main():
    print("=" * 70)
    print("  RUNNING HOST-SIDE WHITE-BOX AUTOMATED TESTS")
    print("=" * 70)

    BUILD_DIR.mkdir(parents=True, exist_ok=True)
    src_file = HOST_TEST_DIR / "MasterHostTestSuite.java"
    cp = f"{BUILD_DIR}:{RAW_JAR}:{MICRO3D_JAR}"

    # Compile
    compile_cmd = [
        "javac", "-source", "1.8", "-target", "1.8", "-Xlint:-options",
        "-cp", f"{RAW_JAR}:{MICRO3D_JAR}",
        "-d", str(BUILD_DIR),
        str(src_file)
    ]
    c_res = subprocess.run(compile_cmd, capture_output=True, text=True)
    if c_res.returncode != 0:
        print(f"[FAIL] Host test compilation failed:\n{c_res.stderr}")
        sys.exit(1)

    # Run
    run_cmd = [
        "java", "-cp", cp,
        "org.neutron.hosttests.MasterHostTestSuite"
    ]
    r_res = subprocess.run(run_cmd, capture_output=True, text=True)
    print(r_res.stdout)
    if r_res.returncode != 0:
        print(r_res.stderr)
        sys.exit(1)

    print("[SUCCESS] All host-side tests passed.")

if __name__ == "__main__":
    main()

