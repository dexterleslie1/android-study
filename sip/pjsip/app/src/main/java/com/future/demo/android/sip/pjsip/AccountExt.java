package com.future.demo.android.sip.pjsip;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.pjsip.pjsua2.Account;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.OnIncomingCallParam;
import org.pjsip.pjsua2.OnRegStartedParam;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.pjsip_status_code;

/**
 *
 */
public class AccountExt extends Account {
    private final static String TAG = AccountExt.class.getSimpleName();

//    private SipService sipService = null;
    private Activity activity = null;

//    private Map<Integer, CallExt> activeCalls = new HashMap<>();
    private CallExt currentCall = null;

    /**
     *
     * @param activity
     */
    public AccountExt(/*SipService sipService*/Activity activity) {
//        this.sipService = sipService;
        this.activity = activity;
    }

    @Override
    public void onIncomingCall(OnIncomingCallParam prm) {
        Log.i(TAG, "有来电");
        try {
            if(this.currentCall!=null) {
                CallExt call = new CallExt(this, prm.getCallId());
                call.declineWithBusy();
            } else {
                CallExt call = new CallExt(this, prm.getCallId());
                CallOpParam callOpParam = new CallOpParam();
                callOpParam.setStatusCode(pjsip_status_code.PJSIP_SC_RINGING);
                call.answer(callOpParam);
                this.currentCall = call;

                OnIncomingCallEvent event = new OnIncomingCallEvent();
                EventBus.getDefault().post(event);
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    @Override
    public void onRegState(OnRegStateParam prm) {
        super.onRegState(prm);

        if(prm.getCode()== pjsip_status_code.PJSIP_SC_OK) {
            SigninSuccessEvent event = new SigninSuccessEvent();
            event.onRegStateParam = prm;
            EventBus.getDefault().post(event);
        } else {
            SigninFailEvent event = new SigninFailEvent();
            event.onRegStateParam = prm;
            EventBus.getDefault().post(event);
        }
    }

    /**
     *
     * @param destinationUri
     * @return
     */
    public CallExt makeCall(String destinationUri) throws Exception {
        // 判断是否正在通话中
        if(this.currentCall!=null) {
            Log.i(TAG, "正在通话中");
            return null;
        }

        CallExt call = new CallExt(this);
        call.makeCall(destinationUri);
        this.currentCall = call;

        return call;
    }

    /**
     *
     */
    public void hangup() throws Exception {
        if(this.currentCall!=null) {
            this.currentCall.hangup();
        }
    }

    /**
     *
     */
    public void declineWithBusy() throws Exception {
        if(this.currentCall!=null) {
            this.currentCall.declineWithBusy();
        }
    }

    /**
     *
     */
    public void accept() throws Exception {
        if(this.currentCall!=null) {
            this.currentCall.accept();
        }
    }

    /**
     *
     */
    public void resetCurrentCall() {
        this.currentCall = null;
    }

    /**
     *
     * @return
     */
    public CallExt getCurrentCall() {
        return this.currentCall;
    }

    /**
     *
     * @return
     */
    public Activity getActivity() {
        return this.activity;
    }
}
