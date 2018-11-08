package com.future.android.study.media;

/**
 * @author Dexterleslie.Chan
 */
public class OpusEngine {
    static{
        System.loadLibrary("nativejni");
    }

    public static native int open(int compression);
    public static native int getFrameSize();
    public static native void close();
}
