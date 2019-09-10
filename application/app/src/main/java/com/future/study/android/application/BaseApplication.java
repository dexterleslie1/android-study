package com.future.study.android.application;

import android.app.Application;
import android.util.Log;

/**
 *
 */
public class BaseApplication extends Application{
    private final static String TAG = BaseApplication.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "调用onCreate()函数");
    }
}
