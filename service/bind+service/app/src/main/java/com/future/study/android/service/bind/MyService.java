package com.future.study.android.service.bind;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.Date;

public class MyService extends Service {
    private final static String TAG = MyService.class.getSimpleName();

    private IBinder binder = new MyBinder();
    private Handler handler = new Handler();
    private boolean isStop = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        displayCurrentTime();

        setForegroundService();

        Log.i(TAG, "onCreate()");
    }

    private void setForegroundService() {
        String channelId = "testChannel";
        // Create notification default intent.
        Intent intent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

        builder.setContentTitle("喂喂")
                .setContentText("喂喂内容")
                .setContentIntent(pendingIntent);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(channelId, "channelName", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        // Build the notification.
        Notification notification = builder.build();

        // Start foreground service.
        startForeground(1, notification);
    }

    private void displayCurrentTime() {
        if(isStop) {
            return ;
        }

        Date date = new Date();
        Log.i(TAG, "Current time is " + date);

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                displayCurrentTime();
            }
        }, 1000);
    }

    @Override
    public int onStartCommand(Intent intentP, int flags, int startId) {
        return super.onStartCommand(intentP, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        isStop = true;

        Log.i(TAG, "onDestroy()");
    }

    /**
     *
     * @return
     */
    public Date getCurrentTime() {
        Date date = new Date();
        return date;
    }

    /**
     *
     */
    public class MyBinder extends Binder {
        /**
         *
         * @return
         */
        public MyService getService() {
            return MyService.this;
        }
    }

    /**
     *
     */
    public static class MyServiceConnection implements ServiceConnection {
        private MyBinder binder = null;
        private MyService service = null;

        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            this.binder = (MyBinder)binder;
            this.service = this.binder.getService();

            Log.i(TAG, "onServiceConnected()");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            this.binder = null;
            this.service = null;

            Log.i(TAG, "onServiceDisconnected()");
        }

        /**
         *
         * @return
         */
        public MyService getService() {
            return this.service;
        }
    }
}
