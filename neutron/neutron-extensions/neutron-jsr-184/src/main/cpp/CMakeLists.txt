cmake_minimum_required(VERSION 3.12)
project(javam3g LANGUAGES C CXX)

set(CMAKE_C_STANDARD 99)
set(CMAKE_CXX_STANDARD 11)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

find_package(JNI QUIET)

include_directories(
    ${CMAKE_CURRENT_SOURCE_DIR}/m3g/inc
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)

if (JNI_FOUND)
    include_directories(${JNI_INCLUDE_DIRS})
endif()

if (WIN32)
    include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include/win32)
    add_definitions(-D_CRT_SECURE_NO_WARNINGS -DWIN32_LEAN_AND_MEAN)
elseif (APPLE)
    include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include/darwin)
endif()

add_definitions(
    -DM3G_TARGET_ANDROID
    -DM3G_GL_ES_1_1
)

set(SOURCES
    m3g/src/m3g_core.c
    m3g/src/miniz.c
    m3g/CSynchronization.cpp
    m3g/src/m3g_android.cpp
    m3g/src/m3g_android_gl.cpp
    m3g/m3g_android_java_api.cpp
)

if (WIN32 OR APPLE)
    list(APPEND SOURCES m3g_desktop_gl.c)
    add_definitions(-DKHRONOS_STATIC)
endif()

add_library(javam3g SHARED ${SOURCES})

if (WIN32)
    target_link_libraries(javam3g opengl32 gdi32 user32)
    set_target_properties(javam3g PROPERTIES
        PREFIX ""
        OUTPUT_NAME "javam3g"
        SUFFIX ".dll"
    )
elseif (APPLE)
    find_library(OPENGL_FRAMEWORK OpenGL REQUIRED)
    target_link_libraries(javam3g ${OPENGL_FRAMEWORK})
    set_target_properties(javam3g PROPERTIES
        PREFIX "lib"
        OUTPUT_NAME "javam3g"
        SUFFIX ".dylib"
    )
else()
    find_package(OpenGL REQUIRED)
    target_link_libraries(javam3g ${OPENGL_LIBRARIES} EGL)
    set_target_properties(javam3g PROPERTIES
        PREFIX "lib"
        OUTPUT_NAME "javam3g"
        SUFFIX ".so"
    )
endif()

# Automatically copy built library to corresponding natives directory
if (WIN32)
    set(NATIVE_DEST "${CMAKE_CURRENT_SOURCE_DIR}/../resources/natives/windows-x86_64")
elseif (APPLE)
    if (CMAKE_SYSTEM_PROCESSOR MATCHES "arm64|aarch64")
        set(NATIVE_DEST "${CMAKE_CURRENT_SOURCE_DIR}/../resources/natives/macos-arm64")
    else()
        set(NATIVE_DEST "${CMAKE_CURRENT_SOURCE_DIR}/../resources/natives/macos-x86_64")
    endif()
else()
    set(NATIVE_DEST "${CMAKE_CURRENT_SOURCE_DIR}/../resources/natives/linux-x86_64")
endif()

add_custom_command(TARGET javam3g POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E make_directory "${NATIVE_DEST}"
    COMMAND ${CMAKE_COMMAND} -E copy_if_different "$<TARGET_FILE:javam3g>" "${NATIVE_DEST}/"
    COMMENT "Copying binary to ${NATIVE_DEST}"
)

