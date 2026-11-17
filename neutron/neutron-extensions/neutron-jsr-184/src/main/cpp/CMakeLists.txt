cmake_minimum_required(VERSION 3.12)
project(javam3g LANGUAGES C CXX)

set(CMAKE_C_STANDARD 99)
set(CMAKE_CXX_STANDARD 11)

find_package(JNI REQUIRED)
find_package(ZLIB REQUIRED)
find_package(OpenGL REQUIRED)

include_directories(
    ${JNI_INCLUDE_DIRS}
    ${CMAKE_CURRENT_SOURCE_DIR}/m3g/inc
    ${CMAKE_CURRENT_SOURCE_DIR}/include
)

add_definitions(
    -DM3G_TARGET_ANDROID
    -DM3G_GL_ES_1_1
)

set(SOURCES
    m3g/src/m3g_core.c
    m3g/CSynchronization.cpp
    m3g/src/m3g_android.cpp
    m3g/src/m3g_android_gl.cpp
    m3g/m3g_android_java_api.cpp
)

add_library(javam3g SHARED ${SOURCES})

target_link_libraries(javam3g
    ${JNI_LIBRARIES}
    ${ZLIB_LIBRARIES}
    ${OPENGL_LIBRARIES}
)

if (UNIX AND NOT APPLE)
    target_link_libraries(javam3g EGL)
endif()

set_target_properties(javam3g PROPERTIES
    PREFIX "lib"
    OUTPUT_NAME "javam3g"
)

