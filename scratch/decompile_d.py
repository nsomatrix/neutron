import subprocess
import re

output = subprocess.check_output(["javap", "-c", "-private", "build/decompiled/Class_cw.class"]).decode("utf-8")

methods = re.split(r'\n\s*(?:public|private|protected|static|final|\s)+\s+\w+\s+d\(', output)

with open("build/decompiled/d_methods.txt", "w") as f:
    for i, m in enumerate(methods[1:]):
        header = "method d(" + m.split("\n")[0]
        body = "\n".join(m.split("\n")[1:300]) # first 300 lines of method body
        f.write(f"=== {header} ===\n{body}\n\n")

print("Extracted d methods to build/decompiled/d_methods.txt")
