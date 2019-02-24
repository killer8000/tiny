package jnitest;

public class JNIUtil {
    static {
        System.loadLibrary("native-lib");
    }
    public  native String stringFromJNI();
}
