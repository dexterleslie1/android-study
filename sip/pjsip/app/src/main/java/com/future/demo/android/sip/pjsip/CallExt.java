package com.future.demo.android.sip.pjsip;

import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Handler;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.pjsip.pjsua2.AudioMedia;
import org.pjsip.pjsua2.Call;
import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.CallMediaInfo;
import org.pjsip.pjsua2.CallMediaInfoVector;
import org.pjsip.pjsua2.CallOpParam;
import org.pjsip.pjsua2.CallSetting;
import org.pjsip.pjsua2.Media;
import org.pjsip.pjsua2.OnCallMediaStateParam;
import org.pjsip.pjsua2.OnCallStateParam;
import org.pjsip.pjsua2.pjmedia_type;
import org.pjsip.pjsua2.pjsip_inv_state;
import org.pjsip.pjsua2.pjsip_role_e;
import org.pjsip.pjsua2.pjsip_status_code;
import org.pjsip.pjsua2.pjsua_call_flag;
import org.pjsip.pjsua2.pjsua_call_media_status;

/**
 *
 */
public class CallExt extends Call {
    private final static String TAG = CallExt.class.getSimpleName();

    private AccountExt account = null;
    private ToneGenerator toneGenerator = null;

    @Override
    public void onCallState(OnCallStateParam prm) {
        try {
            CallInfo ci = getInfo();
            pjsip_inv_state state = ci.getState();
            if (state == pjsip_inv_state.PJSIP_INV_STATE_DISCONNECTED) {
                pjsip_status_code statusCode = ci.getLastStatusCode();
                int swigValue = statusCode.swigValue();
                String text= "("+ swigValue +")"+PjsipStatusConstant.getCallStatusTextByStatusCode(statusCode, ci);
//                if(statusCode==pjsip_status_code.PJSIP_SC_TEMPORARILY_UNAVAILABLE) {
//                    text = "(" + swigValue + ") 用户不存在或者被叫无应答";
//                } else if(statusCode==pjsip_status_code.PJSIP_SC_FORBIDDEN) {
//                    text = "(" + swigValue + ") 呼叫被限制";
//                } else if(statusCode==pjsip_status_code.PJSIP_SC_BUSY_HERE) {
//                    text = "(" + swigValue + ") 用户忙";
//                } else if(statusCode==pjsip_status_code.PJSIP_SC_DECLINE){
//                    text = "(" + swigValue + ") 主叫拒绝";
//                } else if(statusCode==pjsip_status_code.PJSIP_SC_OK) {
//                    text = "(" + swigValue + ") 正常结束通话";
//                } else if(statusCode==pjsip_status_code.PJSIP_SC_REQUEST_TERMINATED) {
//                    text = "(" + swigValue + ") 主叫方中断呼叫";
//                }else {
//                    text = "(" + swigValue + ") 未知状态";
//                }
                final String message = "通话结束，原因：" + text;
                // 正常通话结束，被叫挂状态码200、主叫挂603
                Log.i(TAG,message+",当前状态："+swigValue);

                stopRingtone();

                this.account.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                CallDisconnectedEvent event = new CallDisconnectedEvent();
                                event.reason = message;
                                EventBus.getDefault().post(event);
                            }
                        },500);
                    }
                });

                this.account.resetCurrentCall();
            } else if(state == pjsip_inv_state.PJSIP_INV_STATE_CONNECTING) {
                // 被叫接听电话正在连接中
                pjsip_status_code statusCode = ci.getLastStatusCode();
                int swigValue = statusCode.swigValue();
                // 状态码200
                Log.i(TAG,"被叫接听电话正在连接中,当前状态："+swigValue);
            } else if(state == pjsip_inv_state.PJSIP_INV_STATE_CONFIRMED) {
                pjsip_status_code statusCode = ci.getLastStatusCode();
                int swigValue = statusCode.swigValue();
                Log.i(TAG,"已建立通话连接，可以开始说话,当前状态："+swigValue);
                stopRingtone();
                // 已建立通话连接，可以开始说话
                CallAcceptEvent event = new CallAcceptEvent();
                EventBus.getDefault().post(event);
            } else if(state == pjsip_inv_state.PJSIP_INV_STATE_CALLING) {
                // 正在拨通被叫电话
                Log.i(TAG, "正在拨通被叫电话");
            } else if(state == pjsip_inv_state.PJSIP_INV_STATE_EARLY) {
                // 已拨通被叫电话，等待被叫接听
                pjsip_status_code statusCode = ci.getLastStatusCode();
                int swigValue = statusCode.swigValue();
                // 状态码183
                Log.i(TAG, "已拨通被叫电话，等待被叫接听,当前状态："+swigValue);
//                pjsip_status_code statusCode = ci.getLastStatusCode();
                // check if 180 && call is outgoing (ROLE UAC)
                pjsip_role_e role = ci.getRole();
                if(role == pjsip_role_e.PJSIP_ROLE_UAC) {
                    stopRingtone();
                    if(statusCode == pjsip_status_code.PJSIP_SC_RINGING) {
                        // 拨打电话到sip
                        toneGenerator = new ToneGenerator(AudioManager.STREAM_VOICE_CALL, 100);
                        toneGenerator.startTone(ToneGenerator.TONE_SUP_RINGTONE);
                    } else if(statusCode == pjsip_status_code.PJSIP_SC_PROGRESS) {
                        // 拨打电话到pstn
                    }

//                    OnInviteStateEarlyEvent event = new OnInviteStateEarlyEvent();
//                    EventBus.getDefault().post(event);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private void stopRingtone() {
        if (toneGenerator != null){
            toneGenerator.stopTone();
            toneGenerator.release();
            toneGenerator = null;
        }
    }

    @Override
    public void onCallMediaState(OnCallMediaStateParam prm) {
        super.onCallMediaState(prm);
        try {
            CallInfo ci = getInfo();
            CallMediaInfoVector cmiv = ci.getMedia();

            for (int i = 0; i < cmiv.size(); i++) {
                CallMediaInfo cmi = cmiv.get(i);
                if (cmi.getType() == pjmedia_type.PJMEDIA_TYPE_AUDIO &&
                        (cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_ACTIVE ||
                                cmi.getStatus() == pjsua_call_media_status.PJSUA_CALL_MEDIA_REMOTE_HOLD)) {
                    // unfortunately, on Java too, the returned Media cannot be downcasted to AudioMedia
                    Media m = getMedia(i);
                    AudioMedia am = AudioMedia.typecastFromMedia(m);

                    // connect ports
                    try {
                        ((MainActivity)account.getActivity()).getEndpoint().audDevManager().getCaptureDevMedia().startTransmit(am);
                        am.startTransmit(((MainActivity)account.getActivity()).getEndpoint().audDevManager().getPlaybackDevMedia());
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    /**
     *
     * @param account
     */
    public CallExt(AccountExt account) {
        super(account, 0);
        this.account = account;
    }

    /**
     *
     * @param account
     * @param call_id
     */
    public CallExt(AccountExt account, int call_id) {
        super(account, call_id);
        this.account = account;
    }

    /**
     *
     * @param destinationUri sip:6001@192.168.1.145:5060
     */
    public void makeCall(String destinationUri) throws Exception {
        CallOpParam callOpParam = new CallOpParam();
        callOpParam.getOpt().setAudioCount(1);
        callOpParam.getOpt().setVideoCount(0);
        CallSetting callSetting = callOpParam.getOpt();
        callSetting.setFlag(pjsua_call_flag.PJSUA_CALL_INCLUDE_DISABLED_MEDIA.swigValue());
        super.makeCall(destinationUri, callOpParam);
    }

    /**
     *
     */
    public void hangup() throws Exception {
        CallOpParam param = new CallOpParam();
        param.setStatusCode(pjsip_status_code.PJSIP_SC_DECLINE);
        super.hangup(param);
    }

    /**
     *
     */
    public void accept() throws Exception {
        CallOpParam callOpParam = new CallOpParam();
        callOpParam.setStatusCode(pjsip_status_code.PJSIP_SC_OK);
        callOpParam.getOpt().setAudioCount(1);
        callOpParam.getOpt().setVideoCount(0);
        super.answer(callOpParam);
    }

    /**
     *
     */
    public void declineWithBusy() throws Exception {
        CallOpParam callOpParam = new CallOpParam();
        callOpParam.setStatusCode(pjsip_status_code.PJSIP_SC_BUSY_HERE);
        callOpParam.getOpt().setAudioCount(1);
        callOpParam.getOpt().setVideoCount(0);
        super.answer(callOpParam);
    }
}
