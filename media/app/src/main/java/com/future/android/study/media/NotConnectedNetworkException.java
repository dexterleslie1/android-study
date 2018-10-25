package com.future.android.study.media;

/**
 * @author Dexterleslie.Chan
 */

public class NotConnectedNetworkException extends NetworkException {
    public NotConnectedNetworkException(){
        super("未连接网络");
    }
}
