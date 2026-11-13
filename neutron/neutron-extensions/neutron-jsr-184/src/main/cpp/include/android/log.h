#ifndef ANDROID_LOG_SHIM_H
#define ANDROID_LOG_SHIM_H
#include <stdio.h>
#include <stdarg.h>
#define ANDROID_LOG_UNKNOWN 0
#define ANDROID_LOG_DEFAULT 1
#define ANDROID_LOG_VERBOSE 2
#define ANDROID_LOG_DEBUG   3
#define ANDROID_LOG_INFO    4
#define ANDROID_LOG_WARN    5
#define ANDROID_LOG_ERROR   6
#define ANDROID_LOG_FATAL   7
#define ANDROID_LOG_SILENT  8

static inline int __android_log_vprint(int prio, const char *tag, const char *fmt, va_list ap) {
    fprintf(stderr, "[%s] ", tag ? tag : "M3G");
    int ret = vfprintf(stderr, fmt, ap);
    fprintf(stderr, "\n");
    return ret;
}
static inline int __android_log_print(int prio, const char *tag, const char *fmt, ...) {
    va_list ap;
    va_start(ap, fmt);
    int ret = __android_log_vprint(prio, tag, fmt, ap);
    va_end(ap);
    return ret;
}
#endif
