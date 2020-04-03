package com.future.demo.android.broadcast.receiver.register;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *
 */
public class StaticBroadcastReceiver extends BroadcastReceiver {
    private final static String TAG = StaticBroadcastReceiver.class.getSimpleName();

    /**
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive");
    }
}
