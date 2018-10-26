package com.future.android.study.media;

/**
 * 音频编码器
 * @author Dexterleslie.Chan
 */
public abstract class AudioEncoder {
    /**
     *
     */
    public abstract void start() throws AudioException;

    /**
     *
     */
    public abstract void stop();

    /**
     *
     * @param data
     * @return
     */
    public abstract Object[] encode(Object[] data);

    /**
     * 解码Sample Rate
     * @return
     */
    public abstract int getSampleRateInHz();
}
