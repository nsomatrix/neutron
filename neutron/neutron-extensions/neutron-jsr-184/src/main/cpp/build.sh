#!/bin/bash
# Builds libjavam3g.so for desktop Linux (x86_64) using GCC/G++ and system GL/EGL/zlib
set -e

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"
BUILD_DIR="$DIR/build"
OUTPUT_DIR="$DIR/../resources/natives/linux-x86_64"

mkdir -p "$BUILD_DIR"
mkdir -p "$OUTPUT_DIR"

if [ -z "$JAVA_HOME" ]; then
    JAVA_HOME=$(dirname $(dirname $(readlink -f $(which javac))))
fi

JAVA_INC="$JAVA_HOME/include"
M3G_ROOT="$DIR/m3g"
KHR_INC="$DIR/include"

echo "=== Building Nokia M3G (JSR-184) Native Engine ==="
echo "JAVA_HOME: $JAVA_HOME"

echo "1. Compiling m3g_core.c..."
gcc -c "$M3G_ROOT/src/m3g_core.c" \
    -I"$M3G_ROOT/inc" \
    -I"$KHR_INC" \
    -I"$JAVA_INC" -I"$JAVA_INC/linux" \
    -DM3G_TARGET_ANDROID -DM3G_GL_ES_1_1 -Os -fdata-sections -ffunction-sections -fPIC -fpermissive -Wno-incompatible-pointer-types \
    -o "$BUILD_DIR/m3g_core.o"

echo "1b. Compiling miniz.c..."
gcc -c "$M3G_ROOT/src/miniz.c" \
    -I"$M3G_ROOT/inc" \
    -Os -fdata-sections -ffunction-sections -fPIC \
    -o "$BUILD_DIR/miniz.o"

echo "2. Compiling CSynchronization.cpp..."
g++ -c "$M3G_ROOT/CSynchronization.cpp" \
    -I"$M3G_ROOT/inc" \
    -I"$KHR_INC" \
    -I"$JAVA_INC" -I"$JAVA_INC/linux" \
    -DM3G_TARGET_ANDROID -DM3G_GL_ES_1_1 -Os -fdata-sections -ffunction-sections -fPIC \
    -o "$BUILD_DIR/CSynchronization.o"

echo "3. Compiling m3g_android.cpp..."
g++ -c "$M3G_ROOT/src/m3g_android.cpp" \
    -I"$M3G_ROOT/inc" \
    -I"$KHR_INC" \
    -I"$JAVA_INC" -I"$JAVA_INC/linux" \
    -DM3G_TARGET_ANDROID -DM3G_GL_ES_1_1 -Os -fdata-sections -ffunction-sections -fPIC \
    -o "$BUILD_DIR/m3g_android.o"

echo "4. Compiling m3g_android_gl.cpp..."
g++ -c "$M3G_ROOT/src/m3g_android_gl.cpp" \
    -I"$M3G_ROOT/inc" \
    -I"$KHR_INC" \
    -I"$JAVA_INC" -I"$JAVA_INC/linux" \
    -DM3G_TARGET_ANDROID -DM3G_GL_ES_1_1 -Os -fdata-sections -ffunction-sections -fPIC \
    -o "$BUILD_DIR/m3g_android_gl.o"

echo "5. Compiling m3g_android_java_api.cpp (JNI layer)..."
g++ -c "$M3G_ROOT/m3g_android_java_api.cpp" \
    -I"$M3G_ROOT/inc" \
    -I"$KHR_INC" \
    -I"$JAVA_INC" -I"$JAVA_INC/linux" \
    -DM3G_TARGET_ANDROID -DM3G_GL_ES_1_1 -Os -fdata-sections -ffunction-sections -fPIC -fpermissive \
    -o "$BUILD_DIR/m3g_android_java_api.o"

echo "6. Linking libjavam3g.so..."
LIB_DIR="$BUILD_DIR/lib"
mkdir -p "$LIB_DIR"
for lib in libGL libEGL libGLESv2; do
    if [ ! -f "$LIB_DIR/$lib.so" ]; then
        FOUND=$(find /usr/lib/x86_64-linux-gnu /usr/lib64 /usr/lib -name "$lib.so*" 2>/dev/null | head -n 1)
        if [ -n "$FOUND" ]; then
            ln -sf "$FOUND" "$LIB_DIR/$lib.so"
        fi
    fi
done

g++ -shared -Wl,--gc-sections -s -o "$OUTPUT_DIR/libjavam3g.so" \
    "$BUILD_DIR/m3g_core.o" \
    "$BUILD_DIR/miniz.o" \
    "$BUILD_DIR/CSynchronization.o" \
    "$BUILD_DIR/m3g_android.o" \
    "$BUILD_DIR/m3g_android_gl.o" \
    "$BUILD_DIR/m3g_android_java_api.o" \
    -L"$LIB_DIR" -lGL -lEGL

strip --strip-unneeded "$OUTPUT_DIR/libjavam3g.so" 2>/dev/null || true

echo "Build complete: $OUTPUT_DIR/libjavam3g.so"
ls -lh "$OUTPUT_DIR/libjavam3g.so"
