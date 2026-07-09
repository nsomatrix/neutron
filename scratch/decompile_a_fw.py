import subprocess
import re

output = subprocess.check_output(["javap", "-c", "-private", "build/decompiled/Class_cw.class"]).decode("utf-8")

# Find method a(Class_fw ...
pattern = r'\n\s*(?:public|private|protected|static|final|\s)+\s+void\s+a\(Class_fw'
matches = list(re.finditer(pattern, output))

with open("build/decompiled/a_fw_methods.txt", "w") as f:
    for idx, match in enumerate(matches):
        start = match.start()
        # Find next method or end of output
        end = len(output)
        if idx + 1 < len(matches):
            end = matches[idx + 1].start()
        else:
            # try to find the next public/private method
            next_method = re.search(r'\n\s*(?:public|private|protected|static|final|\s)+\s+\w+\s+\w+\(', output[start + 1:])
            if next_method:
                end = start + 1 + next_method.start()
        
        f.write(output[start:end])
        f.write("\n\n=====================================\n\n")

print("Extracted to build/decompiled/a_fw_methods.txt")
