package com.future.study.android.startservice;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 *
 */
public class Service1 extends Service {
    private final static String TAG = Service1.class.getSimpleName();

    private HandlerThread handlerThread;
    private Handler handler;
    private boolean isStop=false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate 被调用");
        super.onCreate();

        handlerThread=new HandlerThread("thread#"+Service1.class.getSimpleName()+"#print");
        handlerThread.start();
        handler=new Handler(handlerThread.getLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                while(!isStop){
                    Log.i(TAG,Service1.class.getSimpleName()+" 正在运行");
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
        Log.i(TAG, "onStartCommand 被调用");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "onDestroy 被调用");
        super.onDestroy();

        isStop=true;
        if(this.handlerThread!=null){
            this.handlerThread.quit();
            this.handlerThread=null;
        }
    }
}
