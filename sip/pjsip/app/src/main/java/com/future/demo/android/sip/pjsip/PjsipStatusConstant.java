package com.future.demo.android.sip.pjsip;

import android.text.TextUtils;

import org.pjsip.pjsua2.CallInfo;
import org.pjsip.pjsua2.pjsip_status_code;

public class PjsipStatusConstant {

    public static String getCallStatusTextByStatusCode(pjsip_status_code statusCode, CallInfo callInfo){
        String statusText = null;
        String reason = callInfo==null?"":callInfo.getLastReason();
        if(!TextUtils.isEmpty(reason)) {
            statusText = reason;
        }
        if(TextUtils.isEmpty(statusText)) {
            if (statusCode == pjsip_status_code.PJSIP_SC_TEMPORARILY_UNAVAILABLE)
                statusText = "无应答";
            else if (statusCode == pjsip_status_code.PJSIP_SC_FORBIDDEN)
                statusText = "呼叫被限制";
            else if (statusCode == pjsip_status_code.PJSIP_SC_BUSY_HERE)
                statusText = "用户忙";
            else if (statusCode == pjsip_status_code.PJSIP_SC_DECLINE)
                statusText = "通话拒绝";
            else if (statusCode == pjsip_status_code.PJSIP_SC_OK)
                statusText = "正常结束通话";
            else if (statusCode == pjsip_status_code.PJSIP_SC_REQUEST_TERMINATED)
                statusText = "呼叫中断";
        }
        if(TextUtils.isEmpty(statusText)) {
            statusText = "未知状态";
        }

        return statusText;
    }

    public static String getLoginStatusTextByStatusCode(pjsip_status_code statusCode){
        String statusText = "未知状态";
        if(statusCode== pjsip_status_code.PJSIP_SC_FORBIDDEN)
            statusText="账号或密码错误";
        else if(statusCode== pjsip_status_code.PJSIP_SC_BAD_GATEWAY||
                statusCode== pjsip_status_code.PJSIP_SC_REQUEST_TIMEOUT)
             statusText="网络异常";
        return statusText;
    }
}
