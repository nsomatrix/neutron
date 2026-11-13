#ifndef ANDROID_BITMAP_H
#define ANDROID_BITMAP_H
#include <jni.h>

#define ANDROID_BITMAP_RESULT_SUCCESS 0

typedef struct {
    uint32_t width;
    uint32_t height;
    uint32_t stride;
    int32_t  format;
    uint32_t flags;
} AndroidBitmapInfo;

static inline int AndroidBitmap_lockPixels(JNIEnv* env, jobject jbuffer, void** addrPtr) {
    if (!jbuffer) return -1;
    *addrPtr = env->GetDirectBufferAddress(jbuffer);
    return (*addrPtr != NULL) ? 0 : -1;
}

static inline int AndroidBitmap_unlockPixels(JNIEnv* env, jobject jbuffer) {
    return 0;
}

static inline int AndroidBitmap_getInfo(JNIEnv* env, jobject jbuffer, AndroidBitmapInfo* info) {
    return 0;
}
#endif
