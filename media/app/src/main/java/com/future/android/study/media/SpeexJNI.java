package com.future.android.study.media;

/**
 * @author Dexterleslie.Chan
 */
public class SpeexJNI {
    static{
        System.loadLibrary("nativejni");
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

    public static native void open(int quality);
    public static native int getFrameSize();
    public static native int encode(short []data,byte []encodedData);
    public static native int decode(byte []encodedData,short []decodedData,int encodedSize);
    public static native void  close();
}
