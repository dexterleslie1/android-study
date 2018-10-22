package com.future.android.study.media;

/**
 * @author Dexterleslie.Chan
 */
public class SpeexJNI {
    static{
        System.loadLibrary("speexjni");
    }

    /**
     *
     * @param sampleRateInHz
     */
    public static native void init(int sampleRateInHz);

//    /**
//     *
//     * @param inputFrame
//     * @param echoFrame
//     * @param outFrame
//     */
//    public static native void cancellation(short []inputFrame,short []echoFrame,short[] outFrame);
    public static native short[] cancellation(short []inputFrame,short []echoFrame);

    /**
     *
     */
    public static native void destroy();
}
