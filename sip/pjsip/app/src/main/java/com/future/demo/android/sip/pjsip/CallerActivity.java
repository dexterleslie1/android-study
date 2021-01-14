package com.future.demo.android.sip.pjsip;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * 主叫版面
 */
public class CallerActivity extends AppCompatActivity {
    private final static String TAG = CallerActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caller_layout);
        EventBus.getDefault().register(this);

        TextView textViewInfo = findViewById(R.id.textViewInfo);
        textViewInfo.setText("尝试呼叫 " + getIntent().getStringExtra("callee") + " 中...");

        Button button = findViewById(R.id.buttonCallerCancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GlobalReference.accountExt!=null) {
                    try {
                        GlobalReference.accountExt.hangup();
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }

                    CallerActivity.this.finish();
                }
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

    // 对方接听成功后改变按钮信息
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void callAcceptEvent(CallAcceptEvent event) {
        ((Button)findViewById(R.id.buttonCallerCancel)).setText("挂断");
        TextView textViewInfo = findViewById(R.id.textViewInfo);
        textViewInfo.setText("正在与 " + getIntent().getStringExtra("callee") + " 通话中...");
    }
}
