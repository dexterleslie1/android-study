package com.future.android.study.media;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

/**
 * @author Dexterleslie.Chan
 */
public class Utils {
    /**
     *
     * @param activity
     * @return
     */
    public static String getIp(Activity activity){
        WifiManager wm = (WifiManager) activity.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }
}
