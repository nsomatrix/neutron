import subprocess

output = subprocess.check_output(["javap", "-c", "-private", "build/decompiled/Class_cw.class"]).decode("utf-8")

lines = output.splitlines()

# print lines 14497 to 15000
with open("build/decompiled/a_fw_zz_full.txt", "w") as f:
    for idx in range(14497, min(15000, len(lines))):
        f.write(lines[idx] + "\n")

print("Wrote to build/decompiled/a_fw_zz_full.txt")
