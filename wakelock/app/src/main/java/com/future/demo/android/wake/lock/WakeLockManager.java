package com.future.demo.android.wake.lock;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;

/**
 *
 */
public class WakeLockManager {
    private static WakeLockManager manager = null;

    private Context context = null;
    private HandlerThread handlerThread = null;
    private Handler handler = null;

    private PowerManager.WakeLock wakeLock = null;

    /**
     *
     * @param context
     */
    private WakeLockManager(Context context) {
        this.context = context;
    }

    /**
     * 初始化
     * @param context
     */
    public synchronized static void init(Context context) {
        if(manager == null) {
            manager = new WakeLockManager(context);
        }
    }

    /**
     *
     * @return
     */
    public static WakeLockManager getInstance() {
        return manager;
    }

    /**
     *
     * @param timeoutSeconds
     */
    public synchronized void acquire(int timeoutSeconds) {
        if(timeoutSeconds<=0) {
            throw new IllegalArgumentException("timeoutSeconds小于等于0");
        }
        if(handler == null) {
            this.handlerThread = new HandlerThread("thread-vibration-manager");
            this.handlerThread.start();
            this.handler = new Handler(this.handlerThread.getLooper());

            //获取电源管理器对象
            PowerManager pm=(PowerManager)context.getSystemService(Context.POWER_SERVICE);
            //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
            wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                    PowerManager.SCREEN_DIM_WAKE_LOCK,"myapp:kkkk1");
            //点亮屏幕
            wakeLock.acquire((timeoutSeconds*1000-500));

            this.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    release();
                }
            }, timeoutSeconds*1000);
        }
    }

    /**
     *
     */
    public synchronized void release() {
        if(wakeLock != null) {
            wakeLock = null;
        }
        if(this.handler!=null) {
            this.handlerThread.quit();
            this.handlerThread = null;
            this.handler = null;
        }
    }
}
