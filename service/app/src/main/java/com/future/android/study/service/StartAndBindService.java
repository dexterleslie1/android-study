package com.future.android.study.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * @author Dexterleslie.Chan
 */
public class StartAndBindService extends Service {
    private final static String TAG = StartAndBindService.class.getSimpleName();

    private HandlerThread handlerThread;
    private Handler handler;
    private boolean isStop=false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG,"onBind被调用");
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG,"onUnbind被调用");
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG,"onRebind被调用");
        super.onRebind(intent);
    }

    @Override
    public void onCreate(){
        Log.d(TAG,"onCreate被调用");
        handlerThread=new HandlerThread("thread#"+StartService.class.getSimpleName()+"#print");
        handlerThread.start();
        handler=new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                while(!isStop){
                    Log.d(TAG,"正在运行");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        //
                    }
                }
            }
        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand被调用");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy被调用");
        super.onDestroy();

        isStop=true;
        if(this.handlerThread!=null){
            this.handlerThread.quit();
            this.handlerThread=null;
        }
    }
}
