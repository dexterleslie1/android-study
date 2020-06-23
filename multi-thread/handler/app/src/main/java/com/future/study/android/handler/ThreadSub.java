package com.future.study.android.handler;

import android.os.Looper;

/**
 * handler子线程
 */
public class ThreadSub extends Thread {
    private Looper looper = null;

    /**
     *
     */
    public ThreadSub() {
        super("handler子线程");
    }

    /**
     *
     * @return
     */
    public Looper getLooper() {
        return looper;
    }

    /**
     *
     */
    @Override
    public void run() {
        Looper.prepare();
        looper = Looper.myLooper();
        Looper.loop();
    }
}
