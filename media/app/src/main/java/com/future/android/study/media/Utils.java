package com.future.android.study.media;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

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

    /**
     *
     * @param data
     * @param offset
     * @param length
     * @return
     */
    public static byte[] shortArrayToByteArray(short data[],int offset,int length){
        if(data == null){
            return null;
        }
        ByteBuffer byteBuffer = ByteBuffer.allocate(2 * data.length).order(ByteOrder.LITTLE_ENDIAN);
        for (int i = offset; i < length; i++) {
            byteBuffer.putShort(data[i]);
        }
        return byteBuffer.array();
    }
}
