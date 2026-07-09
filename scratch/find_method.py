import subprocess
import re

output = subprocess.check_output(["javap", "-c", "-private", "build/decompiled/Class_cw.class"]).decode("utf-8")

matches = list(re.finditer(r'\n\s*.* a\(Class_fw, boolean, boolean\)', output))

with open("build/decompiled/a_fw_zz.txt", "w") as f:
    for idx, match in enumerate(matches):
        start = match.start()
        # Find next method or end of output
        next_method = re.search(r'\n\s*(?:public|private|protected|static|final|\s)+\s+\w+\s+\w+\(', output[start + 1:])
        if next_method:
            end = start + 1 + next_method.start()
        else:
            end = len(output)
        
        f.write(output[start:end])
        f.write("\n\n=====================================\n\n")

print(f"Found {len(matches)} matches and wrote to build/decompiled/a_fw_zz.txt")
