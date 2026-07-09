import subprocess

output = subprocess.check_output(["javap", "-c", "-private", "build/decompiled/Class_cw.class"]).decode("utf-8")

for i, line in enumerate(output.splitlines()):
    if "a(Class_fw" in line:
        print(f"Line {i}: {line}")
        # print subsequent 15 lines
        for j in range(1, 15):
            print(f"  +{j}: {output.splitlines()[i+j]}")
