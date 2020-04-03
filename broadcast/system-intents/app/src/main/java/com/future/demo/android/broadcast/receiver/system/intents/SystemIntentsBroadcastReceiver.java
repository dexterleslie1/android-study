package com.future.demo.android.broadcast.receiver.system.intents;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 *
 */
public class SystemIntentsBroadcastReceiver extends BroadcastReceiver {
    private final static String TAG = SystemIntentsBroadcastReceiver.class.getSimpleName();

    /**
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "收到broadcast：" + intent.getAction());
    }
}
