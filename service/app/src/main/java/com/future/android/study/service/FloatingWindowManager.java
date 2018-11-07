package com.future.android.study.service;

import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

/**
 * @author Dexterleslie.Chan
 */
public class FloatingWindowManager {
    private final static String TAG = FloatingWindowManager.class.getSimpleName();

    private static FloatingWindowManager instance=new FloatingWindowManager();

    private View view;
    private WindowManager windowManager;

    /**
     *
     */
    private FloatingWindowManager(){

    }

    /**
     *
     * @return
     */
    public static FloatingWindowManager getInstance(){
        return instance;
    }

    /**
     *
     * @param context
     */
    public View show(Context context){
        synchronized (this){
            if(this.view == null){
                this.view = LayoutInflater.from(context).inflate(R.layout.floating_layout,null);
                this.windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            }
        }
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        synchronized (this) {
            if(this.view.getParent() == null) {
                Log.d(TAG,"View未添加到WindowManager");
                windowManager.addView(this.view, layoutParams);
            }else{
                Log.d(TAG,"View已添加到WindowManager");
            }
        }
        return this.view;
    }

    /**
     *
     */
    public void hide(){
        synchronized (this) {
            if (this.view != null) {
                windowManager.removeView(this.view);
            }
        }
    }
}
