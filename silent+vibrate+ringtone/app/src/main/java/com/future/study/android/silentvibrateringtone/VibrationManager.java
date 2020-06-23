package com.future.study.android.silentvibrateringtone;

import android.content.ContentProvider;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.VibrationEffect;
import android.os.Vibrator;

/**
 * 震动管理
 */
public class VibrationManager {
    private static VibrationManager manager = null;

    private Context context = null;
    private HandlerThread handlerThread = null;
    private Handler handler = null;

    /**
     *
     */
    private VibrationManager(Context context) {
        this.context = context;
    }

    /**
     * 初始化
     * @param context
     */
    public synchronized static void init(Context context) {
        if(manager == null) {
            manager = new VibrationManager(context);
        }
    }

    /**
     *
     * @return
     */
    public static VibrationManager getInstance() {
        return manager;
    }

    /**
     *
     * @param timeoutSeconds
     */
    public synchronized void start(int timeoutSeconds) {
        if(timeoutSeconds<=0) {
            throw new IllegalArgumentException("timeoutSeconds小于等于0");
        }
        if(handler == null) {
            this.handlerThread = new HandlerThread("thread-vibration-manager");
            this.handlerThread.start();
            this.handler = new Handler(this.handlerThread.getLooper());
            this.handler.post(new Runnable() {
                @Override
                public void run() {
                    Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        VibrationEffect vibrationEffect = VibrationEffect.createWaveform(new long[] {1000,1000,1000,1000}, 0);
                        vibrator.vibrate(vibrationEffect);
                    }else{
                        vibrator.vibrate(new long[]{1000,1000,1000,1000},0);
                    }
                }
            });
            this.handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    stop();
                }
            }, timeoutSeconds*1000);
        }
    }

    /**
     *
     */
    public void stop() {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.cancel();
        if(this.handler!=null) {
            this.handlerThread.quit();
            this.handlerThread = null;
            this.handler = null;
        }
    }
}
