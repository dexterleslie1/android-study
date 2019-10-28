package com.future.study.android.wakeupshowui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 *
 */
public class MyBroadcastRecevier extends BroadcastReceiver {
    private Activity activity = null;

    /**
     *
     * @param activity
     */
    public MyBroadcastRecevier(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(this.activity!=null) {
            this.activity.finish();
        }
    }
}
