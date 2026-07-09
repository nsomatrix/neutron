import subprocess

def main():
    cwd = "/home/mackruize/neutron"
    
    # Run git log --name-status to see all file statuses across all commits
    try:
        res = subprocess.run(
            ["git", "log", "--name-status", "--oneline", "7b4e9cc..HEAD"],
            cwd=cwd,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True,
            check=True
        )
        lines = res.stdout.strip().split("\n")
    except Exception as e:
        print(f"Error: {e}")
        return

    deleted_files = set()
    current_commit = ""
    
    for line in lines:
        if not line.strip():
            continue
        if not (line.startswith("D\t") or line.startswith("R\t") or line.startswith("M\t") or line.startswith("A\t")):
            current_commit = line
            continue
        
        parts = line.split("\t")
        status = parts[0]
        if status == "D":
            file_path = parts[1]
            if not any(x in file_path for x in ["/bin/", "/build/", "/.gradle/", "previous-compilation-data.bin", ".class"]):
                deleted_files.add(file_path)

    print(f"All historically deleted files (excluding binary/compile artifacts): {len(deleted_files)}")
    for f in sorted(deleted_files):
        print(f"  - {f}")

if __name__ == "__main__":
    main()
