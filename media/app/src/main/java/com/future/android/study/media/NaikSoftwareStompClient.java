package com.future.android.study.media;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.LifecycleEvent;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompHeader;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 *
 * @author Dexterleslie.Chan
 */
public class NaikSoftwareStompClient implements IStompClient {
    private final static String TAG="NaikSoftwareStompClient";

    private StompClient client;
    private boolean isConnectStarted=false;
    private int intervalInMilliseconds=5000;
    private Handler repeatChecker=null;
    private String userId;
    private Map<String,Consumer<StompMessage>> consumerMap=new HashMap<>();
    private Object waitAndNotifyObject=new Object();
    private Map<String,Disposable> disposableMap=new HashMap<>();

    private Runnable runnable=new Runnable() {
        @Override
        public void run() {
            try {
                if (NaikSoftwareStompClient.this.client != null
                        && !NaikSoftwareStompClient.this.client.isConnecting()
                        && !NaikSoftwareStompClient.this.client.isConnected()) {
                    Log.d(TAG, "未连接网络，尝试重新连接");
                    NaikSoftwareStompClient.this.connect();
                } else {
                    if (NaikSoftwareStompClient.this.client != null) {
                        if (NaikSoftwareStompClient.this.client.isConnecting()) {
                            Log.d(TAG, "正在连接网络中");
                        }
                    }
                }
            }catch(Exception ex){
                Log.e(TAG,ex.getMessage(),ex);
            }finally {
                repeatChecker.postDelayed(runnable, intervalInMilliseconds);
            }
        }
    };

    /**
     *
     */
    public NaikSoftwareStompClient(String host,int port,String userId){
        this.userId=userId;
        String uri=String.format(
                "ws://%s:%s/portfolio/websocket",
                host,
                port);
        this.client=Stomp.over(Stomp.ConnectionProvider.OKHTTP,uri);
        this.client.lifecycle().subscribe(new Consumer<LifecycleEvent>() {
            @Override
            public void accept(LifecycleEvent lifecycleEvent) throws Exception {
                Log.d(TAG,"Lifecycle事件："+lifecycleEvent.getType());
                synchronized(waitAndNotifyObject){
                    waitAndNotifyObject.notifyAll();
                }
            }
        });
    }

    @Override
    public void connect() {
        if(!this.isConnectStarted) {
            StompHeader header1 = new StompHeader("userId", userId);
            client.connect(Arrays.asList(header1));
        }else{
            client.reconnect();
        }

        if(repeatChecker==null) {
            HandlerThread handlerThread = new HandlerThread("NaikSoftwareStompClientHandlerThread");
            handlerThread.start();
            repeatChecker = new Handler(handlerThread.getLooper());
            repeatChecker.postDelayed(runnable, intervalInMilliseconds);
        }

        if(isConnectStarted){
            try {
                this.checkIfConnected();
                if(consumerMap!=null&&consumerMap.size()>0) {
                    synchronized (this.waitAndNotifyObject) {
                        for (String path : this.consumerMap.keySet()) {
                            Log.d(TAG, "Stomp client重新注册订阅器[" + path + "]");
                            Disposable disposable = this.disposableMap.get(path);
                            if (disposable != null) {
                                disposable.dispose();
                            }
                            disposable = this.client.topic(path).subscribe(this.consumerMap.get(path));
                            this.disposableMap.put(path, disposable);
                        }
                    }
                }
            } catch (NotConnectedNetworkException e) {
                Log.e(TAG,e.getMessage());
            }
        }
        isConnectStarted=true;
    }

    @Override
    public void disconnect(){
        if(client!=null) {
            client.disconnect();
        }
        if(repeatChecker!=null) {
            repeatChecker.getLooper().quit();
            repeatChecker=null;
        }
        isConnectStarted=false;
    }

    @Override
    public void send(String channel,String data) throws NotConnectedNetworkException {
        this.checkIfConnected();
        client.send(channel,data).subscribe();
    }

    @Override
    public void subscribe(String channel,Consumer<StompMessage> consumer) throws NotConnectedNetworkException {
        this.checkIfConnected();
        this.consumerMap.put(channel,consumer);
        Disposable disposable=client.topic(channel).subscribe(consumer);
        this.disposableMap.put(channel,disposable);
    }

    @Override
    public void unsubscribe(String channel) throws NotConnectedNetworkException {
        Log.d(TAG,"取消订阅["+channel+"]");
        if(this.disposableMap.containsKey(channel)){
            Disposable disposable=this.disposableMap.get(channel);
            if(disposable!=null){
                disposable.dispose();
            }
            this.disposableMap.remove(channel);
        }
        this.consumerMap.remove(channel);
    }

    /**
     * 判断是否已连接网络
     */
    private void checkIfConnected() throws NotConnectedNetworkException {
        if(this.client.isConnecting()){
            Log.d(TAG, "正在连接网络中，进入等待服务器回应状态");
            synchronized (this.waitAndNotifyObject){
                try {
                    this.waitAndNotifyObject.wait(20000);
                } catch (InterruptedException e) {
                }
            }
        }

        int counter=1;
        while(!this.client.isConnected()&&counter<=5){
            if(counter>=5) {
                throw new NotConnectedNetworkException();
            }
            synchronized (this.waitAndNotifyObject){
                try {
                    this.waitAndNotifyObject.wait(300);
                } catch (InterruptedException e) {
                    // 忽略
                }
            }
            counter++;
        }
    }

    /**
     * 取消订阅所有
     */
    public void unsubscribeAll(){
        this.consumerMap.clear();
        for(String key:this.disposableMap.keySet()){
            Disposable disposable=this.disposableMap.get(key);
            if(disposable!=null){
                disposable.dispose();
            }
        }
        this.disposableMap.clear();
    }
}
