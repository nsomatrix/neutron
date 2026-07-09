#!/bin/bash
for commit in 18ac0fa 3013a47 94fa70b 2ff07e6 526a8ae d3ce27d 6ab7512; do
  echo "=== Commit: $(git log -1 --format='%h - %s' $commit) ==="
  git diff-tree --no-commit-id --name-status -r $commit | grep -v -E "(\.class|/bin/|/\.gradle/|/build/|\.jar|\.tar|\.zip|previous-compilation-data\.bin)"
done
