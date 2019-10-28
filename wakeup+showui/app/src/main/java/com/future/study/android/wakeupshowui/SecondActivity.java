package com.future.study.android.wakeupshowui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class SecondActivity extends Activity {
    private final static String TAG = SecondActivity.class.getSimpleName();

    private BroadcastReceiver broadcastReceiver = null;
    private PowerManager.WakeLock wakeLock = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使该Activity在锁屏界面上面显示，别忘了给视频通话的Activity也加上
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        setContentView(R.layout.second_activity_layout);

        //点亮屏幕
        wakeUpAndUnlock(this);

        Button button = findViewById(R.id.buttonClick1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "点击我了。。。");
            }
        });

        button = findViewById(R.id.buttonClose);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SecondActivity.this.finish();
            }
        });

        broadcastReceiver = new MyBroadcastRecevier(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.future.broadcast.event1");
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, intentFilter);
    }

    private void wakeUpAndUnlock(Context context){
//        //屏锁管理器
//        KeyguardManager km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
//        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
//        //解锁
//        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP |
                PowerManager.SCREEN_DIM_WAKE_LOCK,"myapp:kkkk1");
        //点亮屏幕
        wakeLock.acquire();
//        //释放
//        wl.release();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(wakeLock==null) {
            wakeLock.release();
            wakeLock = null;
        }

        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
    }
}
