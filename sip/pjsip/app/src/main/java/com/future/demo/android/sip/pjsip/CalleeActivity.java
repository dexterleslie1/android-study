package com.future.demo.android.sip.pjsip;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 被叫版面
 */
public class CalleeActivity extends AppCompatActivity {
    private final static String TAG = CalleeActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.callee_layout);
        EventBus.getDefault().register(this);

        final CallExt callExt = GlobalReference.accountExt.getCurrentCall();
        String caller = "未知";
        if(callExt!=null) {
            try {
                caller = callExt.getInfo().getRemoteUri();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        TextView textViewCaller = findViewById(R.id.textViewInfo);
        textViewCaller.setText("来自 " + caller + " 呼叫...");

        // 挂断
        Button button = findViewById(R.id.buttonCalleeReject);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
//                    callExt.declineWithBusy();
                    callExt.hangup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                CalleeActivity.this.finish();
            }
        });

        // 接听
        Button buttonCalleeAccept = findViewById(R.id.buttonCalleeAccept);
        buttonCalleeAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    callExt.accept();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(callExt!=null) {
                    try {
                        String caller = callExt.getInfo().getRemoteUri();
                        TextView textViewCaller = findViewById(R.id.textViewInfo);
                        textViewCaller.setText("正在与 " + caller + " 通话中...");
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }

                v.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);

        GlobalReference.accountExt.resetCurrentCall();
    }

    // 对方挂断、拒接、无应答时
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void subscribeCallDisconnectedEvent(CallDisconnectedEvent event) {
        finish();
    }
}
