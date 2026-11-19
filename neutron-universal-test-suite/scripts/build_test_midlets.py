#!/usr/bin/env python3
"""
Neutron Universal Test Suite Builder
Compiles and packages all test MIDlet applications into installable J2ME JAR and JAD bundles.
"""

import os
import sys
import shutil
import subprocess
from pathlib import Path

WORKSPACE = Path("/home/manish/projects/neutron")
TEST_SUITE_DIR = WORKSPACE / "neutron-universal-test-suite"
MIDLETS_SRC_DIR = TEST_SUITE_DIR / "test-midlets"
JARS_OUT_DIR = TEST_SUITE_DIR / "test-jars"
JADS_OUT_DIR = TEST_SUITE_DIR / "test-jads"

def get_classpath():
    jars = []
    # Core dependencies
    cldc_jar = WORKSPACE / "neutron/neutron-cldc/build/libs/neutron-cldc-1.5.0.jar"
    midp_jar = WORKSPACE / "neutron/neutron-midp/build/libs/neutron-midp-1.5.0.jar"
    if cldc_jar.exists():
        jars.append(str(cldc_jar))
    if midp_jar.exists():
        jars.append(str(midp_jar))
        
    # Extension jars
    for jar_path in (WORKSPACE / "neutron/neutron-extensions").glob("*/build/libs/*.jar"):
        if jar_path.is_file() and not jar_path.name.endswith("-sources.jar"):
            jars.append(str(jar_path))
            
    # Core jars
    for jar_path in (WORKSPACE / "neutron/api").glob("*/build/libs/*.jar"):
        if jar_path.is_file():
            jars.append(str(jar_path))
            
    # Swing raw jar as fallback
    raw_jar = WORKSPACE / "neutron/neutron-javase-swing/build/tmp/rawJar/neutron-javase-swing-raw-1.5.0.jar"
    if raw_jar.exists():
        jars.append(str(raw_jar))
        
    return ":".join(jars)

def build_midlet(midlet_dir, classpath):
    name = midlet_dir.name
    src_dir = midlet_dir / "src"
    java_files = list(src_dir.glob("**/*.java"))
    if not java_files:
        return True, "No source files found, skipping."

    classes_dir = midlet_dir / "build" / "classes"
    if classes_dir.exists():
        shutil.rmtree(classes_dir)
    classes_dir.mkdir(parents=True, exist_ok=True)

    # 1. Compile
    cmd = [
        "javac",
        "-source", "1.8",
        "-target", "1.8",
        "-Xlint:-options",
        "-cp", classpath,
        "-d", str(classes_dir)
    ] + [str(f) for f in java_files]

    proc = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    if proc.returncode != 0:
        return False, f"javac compilation failed:\n{proc.stderr}\n{proc.stdout}"

    # 2. Discover main MIDlet class
    main_class = None
    for jf in java_files:
        content = jf.read_text(encoding="utf-8", errors="ignore")
        if "extends MIDlet" in content:
            # extract package and class
            pkg_match = [line for line in content.splitlines() if line.strip().startswith("package ")]
            pkg = pkg_match[0].strip().split()[1].rstrip(";") if pkg_match else ""
            cls_name = jf.stem
            main_class = f"{pkg}.{cls_name}" if pkg else cls_name
            break

    if not main_class:
        main_class = f"org.neutron.tests.{name.replace('-', '.')}.MainMIDlet"

    # 3. Create Manifest
    manifest_file = midlet_dir / "build" / "MANIFEST.MF"
    manifest_content = (
        "Manifest-Version: 1.0\n"
        f"MIDlet-1: {name},,{main_class}\n"
        f"MIDlet-Name: {name}\n"
        "MIDlet-Vendor: Neutron Universal Testing Framework\n"
        "MIDlet-Version: 1.0.0\n"
        "MicroEdition-Configuration: CLDC-1.1\n"
        "MicroEdition-Profile: MIDP-2.0\n"
        "Created-By: Neutron Test Builder\n"
    )
    manifest_file.write_text(manifest_content, encoding="utf-8")

    # 4. Package JAR
    jar_file = JARS_OUT_DIR / f"{name}.jar"
    jar_cmd = [
        "jar",
        "cfm",
        str(jar_file),
        str(manifest_file),
        "-C", str(classes_dir),
        "."
    ]
    # Check for resources
    res_dir = midlet_dir / "resources"
    if res_dir.exists():
        jar_cmd.extend(["-C", str(res_dir), "."])

    jar_proc = subprocess.run(jar_cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
    if jar_proc.returncode != 0:
        return False, f"jar packaging failed:\n{jar_proc.stderr}"

    jar_size = jar_file.stat().st_size

    # 5. Create JAD
    jad_file = JADS_OUT_DIR / f"{name}.jad"
    jad_content = (
        f"MIDlet-Jar-URL: {name}.jar\n"
        f"MIDlet-Jar-Size: {jar_size}\n"
        f"MIDlet-Name: {name}\n"
        "MIDlet-Vendor: Neutron Universal Testing Framework\n"
        "MIDlet-Version: 1.0.0\n"
        f"MIDlet-1: {name},,{main_class}\n"
        "MicroEdition-Configuration: CLDC-1.1\n"
        "MicroEdition-Profile: MIDP-2.0\n"
    )
    jad_file.write_text(jad_content, encoding="utf-8")

    return True, f"Built {jar_file.name} ({jar_size} bytes)"

def main():
    print("=" * 70)
    print("  NEUTRON UNIVERSAL TEST MIDLET COMPILER & PACKAGER")
    print("=" * 70)

    JARS_OUT_DIR.mkdir(parents=True, exist_ok=True)
    JADS_OUT_DIR.mkdir(parents=True, exist_ok=True)

    cp = get_classpath()
    print(f"Discovered Classpath entries: {len(cp.split(':'))}")

    midlet_dirs = sorted([d for d in MIDLETS_SRC_DIR.iterdir() if d.is_dir()])
    print(f"Target MIDlet Suites: {len(midlet_dirs)}")
    print("-" * 70)

    success_count = 0
    fail_count = 0

    for d in midlet_dirs:
        ok, msg = build_midlet(d, cp)
        if ok:
            print(f"[OK]   {d.name:<30} -> {msg}")
            success_count += 1
        else:
            print(f"[FAIL] {d.name:<30} -> {msg}")
            fail_count += 1

    print("-" * 70)
    print(f"Build Result: {success_count} succeeded, {fail_count} failed")
    print("=" * 70)
    if fail_count > 0:
        sys.exit(1)

if __name__ == "__main__":
    main()

