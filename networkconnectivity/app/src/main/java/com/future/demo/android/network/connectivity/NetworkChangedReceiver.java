package com.future.demo.android.network.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

/**
 *
 */
public class NetworkChangedReceiver extends BroadcastReceiver {
    private final static String TAG = NetworkChangedReceiver.class.getSimpleName();

    private Context context = null;

    /**
     *
     * @param context
     */
    public NetworkChangedReceiver(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            //获取联网状态的NetworkInfo对象
            NetworkInfo info = intent
                    .getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
            if (info != null) {
                //如果当前的网络连接成功并且网络连接可用
                if (NetworkInfo.State.CONNECTED == info.getState() && info.isAvailable()) {
                    Log.i(TAG, "已连接网络，类型：" + info.getTypeName());
                } else {
                    Log.i(TAG, "未连接网络");
                }
            } else {
                Log.i(TAG, "未连接网络");
            }
        }
    }
}
