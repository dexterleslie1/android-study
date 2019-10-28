package com.future.study.android.broadcast.local;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * @author dexterleslie@gmail.com
 */
public class MyBroadcastReceiver extends BroadcastReceiver {
    private final static String TAG = MyBroadcastReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive with intent:" + intent);
    }
}
