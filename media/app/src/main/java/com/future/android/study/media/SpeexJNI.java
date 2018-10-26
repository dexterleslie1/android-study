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
     * @param frameSize
     * @param sampleRateInHz
     */
    public static native void init(int frameSize,int sampleRateInHz);

    /**
     *
     * @param inputFrame
     * @param echoFrame
     * @param outFrame
     */
    public static native void cancellation(short []inputFrame,short []echoFrame,short[] outFrame);

    /**
     *
     */
    public static native void destroy();
}
