package com.future.android.study.rxjava;

import android.util.Log;

import junit.framework.Assert;

import org.junit.Test;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * @author Dexterleslie.Chan
 */
public class RxjavaThreadTests {
    private final static String TAG=RxjavaThreadTests.class.getSimpleName();

    /**
     * 在主线程执行
     */
    @Test
    public void mainThread() throws InterruptedException {
        Disposable disposable=Observable.just(1)
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Assert.assertEquals("Instr: android.support.test.runner.AndroidJUnitRunner",Thread.currentThread().getName());
                    }
                });
        synchronized (disposable){
            disposable.wait(1000);
        }
    }

    @Test
    public void observeOn() throws InterruptedException {
        Disposable disposable=Observable.just(1)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        String currentThreadName=Thread.currentThread().getName();
                        Assert.assertTrue(currentThreadName.startsWith("RxCached"));
                    }
                });
        synchronized (disposable){
            disposable.wait(1000);
        }
    }
}
