#include <jni.h>
#include <string>

extern "C" JNIEXPORT jstring JNICALL
Java_jnitest_JNIUtil1_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "String from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_jnitest_JNIUtil_stringFromJNI(JNIEnv *env, jobject instance) {

    std::string hello = "String from C++";


    return env->NewStringUTF(hello.c_str());
}