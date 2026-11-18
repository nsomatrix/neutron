@echo off
setlocal enabledelayedexpansion

echo =======================================================
echo  Building Nokia M3G (JSR-184) Native Engine for Windows
echo =======================================================

set "SCRIPT_DIR=%~dp0"
set "OUTPUT_DIR=%SCRIPT_DIR%..\resources\natives\windows-x86_64"
set "BUILD_DIR=%SCRIPT_DIR%build"

if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"
if not exist "%BUILD_DIR%" mkdir "%BUILD_DIR%"

:: Check JAVA_HOME
if "%JAVA_HOME%"=="" (
    for /d %%D in ("%ProgramFiles%\Java\jdk*") do set "JAVA_HOME=%%D"
    if "%JAVA_HOME%"=="" (
        for /d %%D in ("%ProgramFiles%\Eclipse Adoptium\jdk*") do set "JAVA_HOME=%%D"
    )
)

if "%JAVA_HOME%"=="" (
    echo [WARNING] JAVA_HOME not detected. Using bundled JNI headers from include/win32.
) else (
    echo [INFO] JAVA_HOME: %JAVA_HOME%
)

:: Try CMake if available
where cmake >nul 2>nul
if %ERRORLEVEL% equ 0 (
    echo [INFO] CMake detected. Configuring and building with CMake...
    cd /d "%SCRIPT_DIR%"
    cmake -B "%BUILD_DIR%" -S . -DCMAKE_BUILD_TYPE=Release
    if %ERRORLEVEL% neq 0 (
        echo [ERROR] CMake configuration failed.
        exit /b 1
    )
    cmake --build "%BUILD_DIR%" --config Release
    if %ERRORLEVEL% neq 0 (
        echo [ERROR] CMake build failed.
        exit /b 1
    )
    if exist "%BUILD_DIR%\Release\javam3g.dll" (
        copy /y "%BUILD_DIR%\Release\javam3g.dll" "%OUTPUT_DIR%\javam3g.dll" >nul
    ) else if exist "%BUILD_DIR%\javam3g.dll" (
        copy /y "%BUILD_DIR%\javam3g.dll" "%OUTPUT_DIR%\javam3g.dll" >nul
    )
    echo [SUCCESS] Windows native engine built: %OUTPUT_DIR%\javam3g.dll
    exit /b 0
)

:: Fallback: Try MinGW GCC
where gcc >nul 2>nul
if %ERRORLEVEL% equ 0 (
    echo [INFO] GCC detected. Building via MinGW...
    set "M3G_ROOT=%SCRIPT_DIR%m3g"
    set "INC_FLAGS=-I"%M3G_ROOT%\inc" -I"%SCRIPT_DIR%include" -I"%SCRIPT_DIR%include\win32""
    if not "%JAVA_HOME%"=="" (
        set "INC_FLAGS=!INC_FLAGS! -I"%JAVA_HOME%\include" -I"%JAVA_HOME%\include\win32""
    )
    set "CFLAGS=-DM3G_TARGET_ANDROID -DM3G_GL_ES_1_1 -DKHRONOS_STATIC -O3 -fPIC"

    gcc -c "%M3G_ROOT%\src\m3g_core.c" !INC_FLAGS! !CFLAGS! -fpermissive -Wno-incompatible-pointer-types -o "%BUILD_DIR%\m3g_core.o"
    gcc -c "%M3G_ROOT%\src\miniz.c" -I"%M3G_ROOT%\inc" -O3 -fPIC -o "%BUILD_DIR%\miniz.o"
    g++ -c "%M3G_ROOT%\CSynchronization.cpp" !INC_FLAGS! !CFLAGS! -o "%BUILD_DIR%\CSynchronization.o"
    g++ -c "%M3G_ROOT%\src\m3g_android.cpp" !INC_FLAGS! !CFLAGS! -o "%BUILD_DIR%\m3g_android.o"
    g++ -c "%M3G_ROOT%\src\m3g_android_gl.cpp" !INC_FLAGS! !CFLAGS! -o "%BUILD_DIR%\m3g_android_gl.o"
    g++ -c "%M3G_ROOT%\m3g_android_java_api.cpp" !INC_FLAGS! !CFLAGS! -fpermissive -o "%BUILD_DIR%\m3g_android_java_api.o"
    gcc -c "%SCRIPT_DIR%m3g_desktop_gl.c" !INC_FLAGS! !CFLAGS! -o "%BUILD_DIR%\m3g_desktop_gl.o"

    g++ -shared -o "%OUTPUT_DIR%\javam3g.dll" ^
        "%BUILD_DIR%\m3g_core.o" ^
        "%BUILD_DIR%\miniz.o" ^
        "%BUILD_DIR%\CSynchronization.o" ^
        "%BUILD_DIR%\m3g_android.o" ^
        "%BUILD_DIR%\m3g_android_gl.o" ^
        "%BUILD_DIR%\m3g_android_java_api.o" ^
        "%BUILD_DIR%\m3g_desktop_gl.o" ^
        -lopengl32 -lgdi32 -luser32 -Wl,--kill-at

    if %ERRORLEVEL% equ 0 (
        echo [SUCCESS] Windows native engine built: %OUTPUT_DIR%\javam3g.dll
        exit /b 0
    )
)

echo [ERROR] Neither CMake nor MinGW GCC was found in PATH.
echo Please install Visual Studio / MSVC build tools, CMake, or MinGW-w64.
exit /b 1

