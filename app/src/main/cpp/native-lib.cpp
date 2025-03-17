#include <jni.h>
#include <string>

extern "C"
JNIEXPORT jstring JNICALL
Java_com_twt_launcheros_utils_Constants_urlWallpapers(JNIEnv *env, jobject thiz) {
    std::string hello = "https://gist.githubusercontent.com/tanujnotes/85e2d0343ace71e76615ac346fbff82b/raw";
    return env->NewStringUTF(hello.c_str());
}