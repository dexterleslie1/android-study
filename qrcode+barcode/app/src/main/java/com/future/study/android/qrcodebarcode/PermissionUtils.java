package com.future.study.android.qrcodebarcode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;

public class PermissionUtils {
    public final static String[] Permissions=new String[]{
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE",
            "android.permission.RECORD_AUDIO",
            "android.permission.INTERNET",
            "android.permission.VIBRATE",
            "android.permission.WAKE_LOCK",
            "android.permission.CAMERA"
    };

    /**
     * 判断权限是否已授权
     * @param permission
     * @return
     */
    public static boolean checkIfPermissionGranted(Activity activity, String permission){
        int res = activity.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * 请求授权
     * @param permission
     */
    @TargetApi(23)
    public static void requestPermission(Activity activity,String permission){
        activity.requestPermissions(new String[]{permission},200);
    }
}
