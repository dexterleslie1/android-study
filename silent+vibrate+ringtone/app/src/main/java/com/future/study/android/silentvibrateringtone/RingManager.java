package com.future.study.android.silentvibrateringtone;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;

/**
 * 铃声管理
 */
public class RingManager {
    private static RingManager manager = null;

    private Context context = null;
    private HandlerThread handlerThread = null;
    private Handler handler = null;

    private Ringtone ringtone = null;

    /**
     *
     * @param context
     */
    private RingManager(Context context) {
        this.context = context;
    }

    /**
     *
     * @param context
     */
    public synchronized static void init(Context context) {
        if(manager == null) {
            manager = new RingManager(context);
        }
    }

    /**
     *
     * @return
     */
    public static RingManager getInstance() {
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
//            this.handler.post(new Runnable() {
//                @Override
//                public void run() {
                    if(ringtone==null) {
                        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
                        if(uri!=null) {
                            ringtone = RingtoneManager.getRingtone(context, uri);
                            if(ringtone!=null) {
                                ringtone.play();
                            }
                        }
                    }
//                }
//            });
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
    public synchronized void stop() {
        if(ringtone!=null) {
            ringtone.stop();
            ringtone = null;
        }
        if(this.handler!=null) {
            this.handlerThread.quit();
            this.handlerThread = null;
            this.handler = null;
        }
    }
}
