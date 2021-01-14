package com.future.demo.android.sip.pjsip;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 *
 */
public class SharedPreferencesManager {
    private final static String TAG = SharedPreferencesManager.class.getSimpleName();

    private final static SharedPreferencesManager INSTANCE = new SharedPreferencesManager();

    private Context context;

    /**
     *
     */
    private SharedPreferencesManager() {

    }

    /**
     *
     * @return
     */
    public static SharedPreferencesManager getInstance() {
        return INSTANCE;
    }

    /**
     *
     * @param context
     */
    public void init(Context context) {
        Log.i(TAG, "初始化SharedPreferencesManager");
        this.context = context;
    }

    /**
     *
     */
    public void destroy() {
        Log.i(TAG,"销毁SharedPreferencesManager");
        this.context = null;
    }

    /**
     *
     * @param sharedPreferencesName
     * @param storeKey
     * @param storeValue
     */
    public void putString(String sharedPreferencesName, String storeKey, String storeValue) {
        SharedPreferences sharedPreferences =
                this.context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(storeKey, storeValue);
        editor.apply();
    }

    /**
     *
     * @param sharedPreferencesName
     * @param storeKey
     * @return
     */
    public String getString(String sharedPreferencesName, String storeKey) {
        SharedPreferences sharedPreferences =
                this.context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        String storeValue = sharedPreferences.getString(storeKey, null);
        return storeValue;
    }
}

