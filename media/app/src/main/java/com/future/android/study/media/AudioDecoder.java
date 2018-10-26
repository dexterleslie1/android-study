package com.future.android.study.media;

/**
 * 音频解码器
 * @author Dexterleslie.Chan
 */
public abstract class AudioDecoder {
    /**
     *
     */
    public void start() throws AudioException{

    }

    /**
     *
     */
    public void stop(){

    }

    /**
     *
     * @param data
     * @return
     */
    public abstract Object[] decode(Object [] data);
}
