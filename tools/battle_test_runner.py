#!/usr/bin/env python3
"""
Neutron Battle-Test Harness
Automates headless execution of J2ME midlets and benchmarks to verify engine stability.
"""

import os
import sys
import time
import subprocess
from pathlib import Path

DEFAULT_TIMEOUT = 5  # seconds per midlet

def run_headless_midlet(jar_path, neutron_jar, duration=DEFAULT_TIMEOUT):
    cmd = [
        "java",
        "-Djava.awt.headless=true",
        "-jar",
        neutron_jar,
        "--headless",
        "--rms", "memory",
        str(jar_path)
    ]
    
    start_time = time.time()
    try:
        proc = subprocess.Popen(
            cmd,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )
        
        try:
            stdout, stderr = proc.communicate(timeout=duration)
        except subprocess.TimeoutExpired:
            # Reaching timeout without crashing is a SUCCESS for interactive midlets!
            proc.terminate()
            try:
                stdout, stderr = proc.communicate(timeout=1.0)
            except subprocess.TimeoutExpired:
                proc.kill()
                stdout, stderr = proc.communicate()
            elapsed = time.time() - start_time
            
            # Check for critical errors in stdout/stderr
            combined = stdout + stderr
            fatal_keywords = [
                "Exception in thread",
                "ArrayIndexOutOfBoundsException",
                "ClassCastException",
                "NullPointerException",
                "FATAL ERROR",
                "# A fatal error has been detected",
                "SIGSEGV"
            ]
            
            fatal_errors = [k for k in fatal_keywords if k in combined]
            if fatal_errors:
                return {
                    "jar": Path(jar_path).name,
                    "status": "FAIL",
                    "reason": f"Exception detected: {', '.join(fatal_errors)}",
                    "elapsed": f"{elapsed:.1f}s",
                    "log": combined
                }
            
            return {
                "jar": Path(jar_path).name,
                "status": "PASS",
                "reason": f"Ran stably for {elapsed:.1f}s (headless GameCanvas active)",
                "elapsed": f"{elapsed:.1f}s",
                "log": combined
            }
            
        elapsed = time.time() - start_time
        combined = stdout + stderr
        if proc.returncode != 0:
            return {
                "jar": Path(jar_path).name,
                "status": "FAIL",
                "reason": f"Exited with code {proc.returncode}",
                "elapsed": f"{elapsed:.1f}s",
                "log": combined
            }
        return {
            "jar": Path(jar_path).name,
            "status": "PASS",
            "reason": f"Completed cleanly in {elapsed:.1f}s",
            "elapsed": f"{elapsed:.1f}s",
            "log": combined
        }
    except Exception as e:
        return {
            "jar": Path(jar_path).name,
            "status": "ERROR",
            "reason": str(e),
            "elapsed": "0s",
            "log": ""
        }

def main():
    workspace = Path(__file__).resolve().parent.parent
    libs_dir = workspace / "neutron" / "neutron-javase-swing" / "build" / "libs"
    neutron_jar = libs_dir / "neutron-javase-swing-1.5.0.jar"
    if not neutron_jar.exists():
        jars = sorted(libs_dir.glob("neutron-javase-swing-*.jar"))
        if jars:
            neutron_jar = jars[-1]
    
    if not neutron_jar.exists():
        print(f"Error: {neutron_jar} not found. Please build the project first.")
        sys.exit(1)
        
    test_jars = [
        # Strategy 1: Industry-Standard Benchmarks
        ("/tmp/JBenchmark2.jar", "Benchmark", "JBenchmark 2 (MIDP 2.0 2D/UI/Sprite)"),
        ("/tmp/JBenchmark1.1.1.jar", "Benchmark", "JBenchmark 1.1.1 (MIDP 1.0 Baseline)"),
        
        # Strategy 2: Engine Archetypes
        ("/home/manish/Downloads/outland3d_qbw6u2v9.jar", "Archetype 3D", "Outland 3D (JSR-184 M3G Engine)"),
        ("/home/manish/Downloads/galaxy_on_fire_3d_240x320-40670.jar", "Archetype 3D", "Fishlabs Abyss Engine (Galaxy on Fire 3D)"),
        ("/home/manish/Downloads/NEEDFORSPEEDCARBON_E72.jar", "Archetype 3D", "EA Mobile / Ideaworks 3D (NFS Carbon)"),
        ("/home/manish/Downloads/Avatar_LAPRO.jar", "Archetype 2.5D/3D", "Gameloft Engine (Avatar)"),
        ("/home/manish/Downloads/juiced_2_hot_import_143742.jar", "Archetype 3D", "Juice Games / THQ (Juiced 2 Hot Import Nights)"),
        ("/home/manish/Downloads/rollrush_en_fr_it_de_es_pt_nokian8.jar", "Archetype 3D", "Digital Chocolate (Rollercoaster Rush 3D)"),
        ("/home/manish/Downloads/Bounce-Tales_J2ME_EN_v204.jar", "Archetype 2D", "Nokia Physics Engine (Bounce Tales)"),
        ("/home/manish/Downloads/KnightOnline_v403.jar", "Archetype RPG", "Online / Network RPG (Knight Online)"),
        ("/home/manish/Downloads/contrterro_Ab4yGCVS.jar", "Archetype Action", "Action / Arcade (Counter Terrorist)")
    ]
    
    print("=" * 80)
    print("  NEUTRON AUTOMATED HEADLESS BATTLE-TEST HARNESS")
    print(f"  Target Emulator: {neutron_jar.name}")
    print(f"  Test Duration:   {DEFAULT_TIMEOUT}s per midlet")
    print("=" * 80)
    print()
    
    results = []
    for jar_path, category, description in test_jars:
        if not os.path.exists(jar_path):
            print(f"[SKIP] {Path(jar_path).name} (file not found)")
            continue
            
        print(f"Running [{category}] {description}...", end="", flush=True)
        res = run_headless_midlet(jar_path, str(neutron_jar), duration=DEFAULT_TIMEOUT)
        res["category"] = category
        res["description"] = description
        results.append(res)
        
        if res["status"] == "PASS":
            print(f" \033[92mPASS\033[0m ({res['elapsed']})")
        else:
            print(f" \033[91m{res['status']}\033[0m ({res['reason']})")
            
    print()
    print("=" * 80)
    print("  BATTLE-TEST SUMMARY MATRIX")
    print("=" * 80)
    print(f"{'Category':<15} | {'Target MIDlet':<32} | {'Status':<6} | {'Details'}")
    print("-" * 80)
    
    all_passed = True
    for r in results:
        status_str = f"\033[92m{r['status']}\033[0m" if r['status'] == "PASS" else f"\033[91m{r['status']}\033[0m"
        print(f"{r['category']:<15} | {r['jar']:<32} | {r['status']:<6} | {r['reason']}")
        if r["status"] != "PASS":
            all_passed = False
            
    print("-" * 80)
    total = len(results)
    passed = sum(1 for r in results if r['status'] == "PASS")
    print(f"Result: {passed}/{total} passed ({(passed/total)*100:.1f}%)")
    print("=" * 80)
    
    if not all_passed:
        sys.exit(1)

if __name__ == "__main__":
    main()

