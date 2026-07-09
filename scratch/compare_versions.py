import subprocess
import os

def main():
    cwd = "/home/mackruize/neutron"
    
    # Run git diff --name-status 7b4e9cc
    try:
        res = subprocess.run(
            ["git", "diff", "--name-status", "7b4e9cc"],
            cwd=cwd,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            check=True
        )
        diff_lines = res.stdout.strip().split("\n")
    except Exception as e:
        print(f"Error running git diff: {e}")
        return

    # Check untracked files
    try:
        res_untracked = subprocess.run(
            ["git", "status", "--porcelain"],
            cwd=cwd,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            check=True
        )
        status_lines = res_untracked.stdout.strip().split("\n")
    except Exception as e:
        print(f"Error running git status: {e}")
        status_lines = []

    # Parse untracked files
    untracked_files = []
    for line in status_lines:
        if line.startswith("??"):
            untracked_files.append(line[3:])

    deleted_files = []
    added_files = []
    modified_files = []
    renamed_files = []

    for line in diff_lines:
        if not line.strip():
            continue
        parts = line.split("\t")
        status = parts[0]
        if status.startswith("D"):
            deleted_files.append(parts[1])
        elif status.startswith("A"):
            added_files.append(parts[1])
        elif status.startswith("M"):
            modified_files.append(parts[1])
        elif status.startswith("R"):
            renamed_files.append((parts[1], parts[2]))

    print(f"Total Deleted Files: {len(deleted_files)}")
    print(f"Total Added Files: {len(added_files)}")
    print(f"Total Modified Files: {len(modified_files)}")
    print(f"Total Renamed Files: {len(renamed_files)}")
    print(f"Total Untracked Files: {len(untracked_files)}")

    print("\n--- DELETED FILES ---")
    # Group by directories
    deleted_by_dir = {}
    for f in sorted(deleted_files):
        # Filter out compiler artifacts (bin/, build/, etc) to focus on src/ and config
        if any(x in f for x in ["/bin/", "/build/", "/.gradle/", "previous-compilation-data.bin", ".class"]):
            continue
        parts = f.split('/')
        dir_path = "/".join(parts[:-1]) if len(parts) > 1 else "root"
        deleted_by_dir.setdefault(dir_path, []).append(parts[-1])

    for d, files in sorted(deleted_by_dir.items()):
        print(f"\nDirectory: {d} ({len(files)} files)")
        for file in files:
            print(f"  - {file}")

    print("\n--- ADDED FILES ---")
    added_by_dir = {}
    for f in sorted(added_files):
        if any(x in f for x in ["/bin/", "/build/", "/.gradle/", "previous-compilation-data.bin", ".class"]):
            continue
        parts = f.split('/')
        dir_path = "/".join(parts[:-1]) if len(parts) > 1 else "root"
        added_by_dir.setdefault(dir_path, []).append(parts[-1])

    for d, files in sorted(added_by_dir.items()):
        print(f"\nDirectory: {d} ({len(files)} files)")
        for file in files:
            print(f"  - {file}")

    print("\n--- RENAMED FILES ---")
    for src, dest in sorted(renamed_files):
        if any(x in src or x in dest for x in ["/bin/", "/build/", "/.gradle/", "previous-compilation-data.bin", ".class"]):
            continue
        print(f"  - {src} -> {dest}")

    print("\n--- MODIFIED FILES (non-build) ---")
    for f in sorted(modified_files):
        if any(x in f for x in ["/bin/", "/build/", "/.gradle/", "previous-compilation-data.bin", ".class"]):
            continue
        print(f"  - {f}")

if __name__ == "__main__":
    main()
