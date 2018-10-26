package com.future.android.study.media;

/**
 * @author Dexterleslie.Chan
 */
public class AudioException extends Exception{

    /**
     *
     * @param error
     */
    public AudioException(String error){
        super(error);
    }

    /**
     *
     * @param error
     * @param throwable
     */
    public AudioException(String error,Throwable throwable){
        super(error,throwable);
    }
}
