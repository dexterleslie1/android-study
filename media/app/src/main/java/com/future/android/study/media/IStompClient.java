package com.future.android.study.media;

import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.ConnectionProvider;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 *
 * @author Dexterleslie.Chan
 */

public interface IStompClient {
    void connect();
    void disconnect();
    void send(String channel, String data) throws NotConnectedNetworkException;
    void subscribe(String channel, Consumer<StompMessage> consumer) throws NotConnectedNetworkException;
    void unsubscribe(String channel) throws NotConnectedNetworkException;
}