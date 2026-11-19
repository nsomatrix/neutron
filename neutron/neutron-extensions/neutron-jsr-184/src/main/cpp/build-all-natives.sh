#!/bin/bash
# Multi-platform native cross-compiler for Nokia M3G (JSR-184)
# Produces:
#   - natives/linux-x86_64/libjavam3g.so
#   - natives/windows-x86_64/javam3g.dll
#   - natives/windows-x86/javam3g.dll
#   - natives/windows-arm64/javam3g.dll
#   - natives/macos-x86_64/libjavam3g.dylib
#   - natives/macos-arm64/libjavam3g.dylib
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
RESOURCES_DIR="$DIR/../resources/natives"

if [ -z "$JAVA_HOME" ]; then
    JAVA_HOME=$(dirname $(dirname $(readlink -f $(which javac 2>/dev/null || which java 2>/dev/null))))
fi

JAVA_INC="$JAVA_HOME/include"
M3G_ROOT="$DIR/m3g"
KHR_INC="$DIR/include"
BUILD_DIR="$DIR/build_all"
mkdir -p "$BUILD_DIR"

echo "=========================================================="
echo " Building Nokia M3G Multi-Platform Natives"
echo " JAVA_HOME: $JAVA_HOME"
echo "=========================================================="

# 1. Build Linux x86_64 using system GCC
echo "[1/6] Building Linux x86_64 native (libjavam3g.so)..."
mkdir -p "$RESOURCES_DIR/linux-x86_64"
"$DIR/build.sh"

# Find or download portable zig if not installed
ZIG="${ZIG:-zig}"
if ! command -v "$ZIG" &>/dev/null; then
    if [ -x "$HOME/.local/bin/zig" ]; then
        ZIG="$HOME/.local/bin/zig"
    elif [ -x "$HOME/.local/share/zig/zig" ]; then
        ZIG="$HOME/.local/share/zig/zig"
    elif [ -x "/tmp/zig_test/zig" ]; then
        ZIG="/tmp/zig_test/zig"
    else
        echo "Zig not found. Downloading portable Zig toolchain into ~/.local/share/zig..."
        mkdir -p "$HOME/.local/share/zig" "$HOME/.local/bin"
        curl -L -s https://ziglang.org/download/0.13.0/zig-linux-x86_64-0.13.0.tar.xz | tar -xJ -C "$HOME/.local/share/zig" --strip-components=1
        ln -sf "$HOME/.local/share/zig/zig" "$HOME/.local/bin/zig"
        ZIG="$HOME/.local/share/zig/zig"
        echo "Zig installed to $ZIG"
    fi
fi

# Common compilation flags
COMMON_INC="-I$M3G_ROOT/inc -I$KHR_INC -I$JAVA_INC"
COMMON_DEFS="-DM3G_TARGET_ANDROID -DM3G_GL_ES_1_1 -DKHRONOS_STATIC -Os -fdata-sections -ffunction-sections -fPIC"

# 2. Build Windows x86_64
echo "[2/6] Cross-compiling Windows x86_64 (javam3g.dll)..."
mkdir -p "$RESOURCES_DIR/windows-x86_64" "$BUILD_DIR/win64"
WIN_INC="$COMMON_INC -I$DIR/include/win32"

$ZIG cc -target x86_64-windows-gnu -c "$M3G_ROOT/src/m3g_core.c" $WIN_INC $COMMON_DEFS -Wno-incompatible-pointer-types -o "$BUILD_DIR/win64/core.o"
$ZIG cc -target x86_64-windows-gnu -c "$M3G_ROOT/src/miniz.c" -I"$M3G_ROOT/inc" -Os -fdata-sections -ffunction-sections -fPIC -o "$BUILD_DIR/win64/miniz.o"
$ZIG c++ -target x86_64-windows-gnu -c "$M3G_ROOT/CSynchronization.cpp" $WIN_INC $COMMON_DEFS -o "$BUILD_DIR/win64/csync.o"
$ZIG c++ -target x86_64-windows-gnu -c "$M3G_ROOT/src/m3g_android.cpp" $WIN_INC $COMMON_DEFS -o "$BUILD_DIR/win64/android.o"
$ZIG c++ -target x86_64-windows-gnu -c "$M3G_ROOT/src/m3g_android_gl.cpp" $WIN_INC $COMMON_DEFS -o "$BUILD_DIR/win64/android_gl.o"
$ZIG c++ -target x86_64-windows-gnu -c "$M3G_ROOT/m3g_android_java_api.cpp" $WIN_INC $COMMON_DEFS -fpermissive -o "$BUILD_DIR/win64/api.o"
$ZIG cc -target x86_64-windows-gnu -c "$DIR/m3g_desktop_gl.c" $WIN_INC $COMMON_DEFS -o "$BUILD_DIR/win64/desktop_gl.o"

$ZIG c++ -target x86_64-windows-gnu -shared -s -Wl,--gc-sections \
    "$BUILD_DIR/win64/core.o" "$BUILD_DIR/win64/miniz.o" "$BUILD_DIR/win64/csync.o" \
    "$BUILD_DIR/win64/android.o" "$BUILD_DIR/win64/android_gl.o" "$BUILD_DIR/win64/api.o" \
    "$BUILD_DIR/win64/desktop_gl.o" \
    -lopengl32 -lgdi32 -luser32 \
    -o "$RESOURCES_DIR/windows-x86_64/javam3g.dll"
rm -f "$RESOURCES_DIR/windows-x86_64/"*.lib "$RESOURCES_DIR/windows-x86_64/"*.pdb

# 3. Build Windows x86 (32-bit)
echo "[3/6] Cross-compiling Windows x86 32-bit (javam3g.dll)..."
mkdir -p "$RESOURCES_DIR/windows-x86" "$BUILD_DIR/win32"

$ZIG cc -target x86-windows-gnu -c "$M3G_ROOT/src/m3g_core.c" $WIN_INC $COMMON_DEFS -Wno-incompatible-pointer-types -o "$BUILD_DIR/win32/core.o"
$ZIG cc -target x86-windows-gnu -c "$M3G_ROOT/src/miniz.c" -I"$M3G_ROOT/inc" -Os -fdata-sections -ffunction-sections -fPIC -o "$BUILD_DIR/win32/miniz.o"
$ZIG c++ -target x86-windows-gnu -c "$M3G_ROOT/CSynchronization.cpp" $WIN_INC $COMMON_DEFS -o "$BUILD_DIR/win32/csync.o"
$ZIG c++ -target x86-windows-gnu -c "$M3G_ROOT/src/m3g_android.cpp" $WIN_INC $COMMON_DEFS -o "$BUILD_DIR/win32/android.o"
$ZIG c++ -target x86-windows-gnu -c "$M3G_ROOT/src/m3g_android_gl.cpp" $WIN_INC $COMMON_DEFS -o "$BUILD_DIR/win32/android_gl.o"
$ZIG c++ -target x86-windows-gnu -c "$M3G_ROOT/m3g_android_java_api.cpp" $WIN_INC $COMMON_DEFS -fpermissive -o "$BUILD_DIR/win32/api.o"
$ZIG cc -target x86-windows-gnu -c "$DIR/m3g_desktop_gl.c" $WIN_INC $COMMON_DEFS -o "$BUILD_DIR/win32/desktop_gl.o"

$ZIG c++ -target x86-windows-gnu -shared -s -Wl,--gc-sections \
    "$BUILD_DIR/win32/core.o" "$BUILD_DIR/win32/miniz.o" "$BUILD_DIR/win32/csync.o" \
    "$BUILD_DIR/win32/android.o" "$BUILD_DIR/win32/android_gl.o" "$BUILD_DIR/win32/api.o" \
    "$BUILD_DIR/win32/desktop_gl.o" \
    -lopengl32 -lgdi32 -luser32 \
    -o "$RESOURCES_DIR/windows-x86/javam3g.dll"
rm -f "$RESOURCES_DIR/windows-x86/"*.lib "$RESOURCES_DIR/windows-x86/"*.pdb

# 4. Build Windows ARM64
echo "[4/6] Cross-compiling Windows ARM64 (javam3g.dll)..."
mkdir -p "$RESOURCES_DIR/windows-arm64" "$BUILD_DIR/win_arm64"

$ZIG cc -target aarch64-windows-gnu -c "$M3G_ROOT/src/m3g_core.c" $WIN_INC $COMMON_DEFS -Wno-incompatible-pointer-types -o "$BUILD_DIR/win_arm64/core.o"
$ZIG cc -target aarch64-windows-gnu -c "$M3G_ROOT/src/miniz.c" -I"$M3G_ROOT/inc" -Os -fdata-sections -ffunction-sections -fPIC -o "$BUILD_DIR/win_arm64/miniz.o"
$ZIG c++ -target aarch64-windows-gnu -c "$M3G_ROOT/CSynchronization.cpp" $WIN_INC $COMMON_DEFS -o "$BUILD_DIR/win_arm64/csync.o"
$ZIG c++ -target aarch64-windows-gnu -c "$M3G_ROOT/src/m3g_android.cpp" $WIN_INC $COMMON_DEFS -o "$BUILD_DIR/win_arm64/android.o"
$ZIG c++ -target aarch64-windows-gnu -c "$M3G_ROOT/src/m3g_android_gl.cpp" $WIN_INC $COMMON_DEFS -o "$BUILD_DIR/win_arm64/android_gl.o"
$ZIG c++ -target aarch64-windows-gnu -c "$M3G_ROOT/m3g_android_java_api.cpp" $WIN_INC $COMMON_DEFS -fpermissive -o "$BUILD_DIR/win_arm64/api.o"
$ZIG cc -target aarch64-windows-gnu -c "$DIR/m3g_desktop_gl.c" $WIN_INC $COMMON_DEFS -o "$BUILD_DIR/win_arm64/desktop_gl.o"

$ZIG c++ -target aarch64-windows-gnu -shared -s -Wl,--gc-sections \
    "$BUILD_DIR/win_arm64/core.o" "$BUILD_DIR/win_arm64/miniz.o" "$BUILD_DIR/win_arm64/csync.o" \
    "$BUILD_DIR/win_arm64/android.o" "$BUILD_DIR/win_arm64/android_gl.o" "$BUILD_DIR/win_arm64/api.o" \
    "$BUILD_DIR/win_arm64/desktop_gl.o" \
    -lopengl32 -lgdi32 -luser32 \
    -o "$RESOURCES_DIR/windows-arm64/javam3g.dll"
rm -f "$RESOURCES_DIR/windows-arm64/"*.lib "$RESOURCES_DIR/windows-arm64/"*.pdb

# Prepare macOS OpenGL framework stubs for cross-compilation
echo "Preparing macOS OpenGL framework stubs..."
mkdir -p "$BUILD_DIR/mac_x64/frameworks/OpenGL.framework"
$ZIG cc -target x86_64-macos -shared -Wl,-install_name,/System/Library/Frameworks/OpenGL.framework/Versions/A/OpenGL \
    -o "$BUILD_DIR/mac_x64/frameworks/OpenGL.framework/OpenGL" -x c - <<< "void glClear(void){}"

mkdir -p "$BUILD_DIR/mac_arm64/frameworks/OpenGL.framework"
$ZIG cc -target aarch64-macos -shared -Wl,-install_name,/System/Library/Frameworks/OpenGL.framework/Versions/A/OpenGL \
    -o "$BUILD_DIR/mac_arm64/frameworks/OpenGL.framework/OpenGL" -x c - <<< "void glClear(void){}"

# 5. Build macOS x86_64
echo "[5/6] Cross-compiling macOS x86_64 (libjavam3g.dylib)..."
mkdir -p "$RESOURCES_DIR/macos-x86_64"
MAC_INC="$COMMON_INC -I$DIR/include/darwin"

$ZIG cc -target x86_64-macos -c "$M3G_ROOT/src/m3g_core.c" $MAC_INC $COMMON_DEFS -Wno-incompatible-pointer-types -o "$BUILD_DIR/mac_x64/core.o"
$ZIG cc -target x86_64-macos -c "$M3G_ROOT/src/miniz.c" -I"$M3G_ROOT/inc" -Os -fdata-sections -ffunction-sections -fPIC -o "$BUILD_DIR/mac_x64/miniz.o"
$ZIG c++ -target x86_64-macos -c "$M3G_ROOT/CSynchronization.cpp" $MAC_INC $COMMON_DEFS -o "$BUILD_DIR/mac_x64/csync.o"
$ZIG c++ -target x86_64-macos -c "$M3G_ROOT/src/m3g_android.cpp" $MAC_INC $COMMON_DEFS -o "$BUILD_DIR/mac_x64/android.o"
$ZIG c++ -target x86_64-macos -c "$M3G_ROOT/src/m3g_android_gl.cpp" $MAC_INC $COMMON_DEFS -o "$BUILD_DIR/mac_x64/android_gl.o"
$ZIG c++ -target x86_64-macos -c "$M3G_ROOT/m3g_android_java_api.cpp" $MAC_INC $COMMON_DEFS -fpermissive -o "$BUILD_DIR/mac_x64/api.o"
$ZIG cc -target x86_64-macos -c "$DIR/m3g_desktop_gl.c" $MAC_INC $COMMON_DEFS -o "$BUILD_DIR/mac_x64/desktop_gl.o"

$ZIG c++ -target x86_64-macos -shared -Wl,-dead_strip \
    "$BUILD_DIR/mac_x64/core.o" "$BUILD_DIR/mac_x64/miniz.o" "$BUILD_DIR/mac_x64/csync.o" \
    "$BUILD_DIR/mac_x64/android.o" "$BUILD_DIR/mac_x64/android_gl.o" "$BUILD_DIR/mac_x64/api.o" \
    "$BUILD_DIR/mac_x64/desktop_gl.o" \
    -F"$BUILD_DIR/mac_x64/frameworks" -framework OpenGL \
    -Wl,-install_name,@rpath/libjavam3g.dylib \
    -Wl,-undefined,dynamic_lookup \
    -o "$RESOURCES_DIR/macos-x86_64/libjavam3g.dylib"

# 6. Build macOS arm64
echo "[6/6] Cross-compiling macOS arm64 (libjavam3g.dylib)..."
mkdir -p "$RESOURCES_DIR/macos-arm64"

$ZIG cc -target aarch64-macos -c "$M3G_ROOT/src/m3g_core.c" $MAC_INC $COMMON_DEFS -Wno-incompatible-pointer-types -o "$BUILD_DIR/mac_arm64/core.o"
$ZIG cc -target aarch64-macos -c "$M3G_ROOT/src/miniz.c" -I"$M3G_ROOT/inc" -Os -fdata-sections -ffunction-sections -fPIC -o "$BUILD_DIR/mac_arm64/miniz.o"
$ZIG c++ -target aarch64-macos -c "$M3G_ROOT/CSynchronization.cpp" $MAC_INC $COMMON_DEFS -o "$BUILD_DIR/mac_arm64/csync.o"
$ZIG c++ -target aarch64-macos -c "$M3G_ROOT/src/m3g_android.cpp" $MAC_INC $COMMON_DEFS -o "$BUILD_DIR/mac_arm64/android.o"
$ZIG c++ -target aarch64-macos -c "$M3G_ROOT/src/m3g_android_gl.cpp" $MAC_INC $COMMON_DEFS -o "$BUILD_DIR/mac_arm64/android_gl.o"
$ZIG c++ -target aarch64-macos -c "$M3G_ROOT/m3g_android_java_api.cpp" $MAC_INC $COMMON_DEFS -fpermissive -o "$BUILD_DIR/mac_arm64/api.o"
$ZIG cc -target aarch64-macos -c "$DIR/m3g_desktop_gl.c" $MAC_INC $COMMON_DEFS -o "$BUILD_DIR/mac_arm64/desktop_gl.o"

$ZIG c++ -target aarch64-macos -shared -Wl,-dead_strip \
    "$BUILD_DIR/mac_arm64/core.o" "$BUILD_DIR/mac_arm64/miniz.o" "$BUILD_DIR/mac_arm64/csync.o" \
    "$BUILD_DIR/mac_arm64/android.o" "$BUILD_DIR/mac_arm64/android_gl.o" "$BUILD_DIR/mac_arm64/api.o" \
    "$BUILD_DIR/mac_arm64/desktop_gl.o" \
    -F"$BUILD_DIR/mac_arm64/frameworks" -framework OpenGL \
    -Wl,-install_name,@rpath/libjavam3g.dylib \
    -Wl,-undefined,dynamic_lookup \
    -o "$RESOURCES_DIR/macos-arm64/libjavam3g.dylib"

echo "=== All platform natives built successfully ==="
ls -lh "$RESOURCES_DIR"/*/*
