package com.future.demo.android.shared.preferences;

import android.content.Context;
import android.content.SharedPreferences;

/**
 *
 */
public class SharedPreferencesManager {
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
        this.context = context;
    }

    /**
     *
     */
    public void destroy() {
        this.context = null;
    }

    /**
     *
     * @param sharedPreferencesName
     * @param storeKey
     * @param storeValue
     */
    public void write(String sharedPreferencesName, String storeKey, String storeValue) {
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
    public String read(String sharedPreferencesName, String storeKey) {
        SharedPreferences sharedPreferences =
                this.context.getSharedPreferences(sharedPreferencesName, Context.MODE_PRIVATE);
        String storeValue = sharedPreferences.getString(storeKey, null);
        return storeValue;
    }
}
