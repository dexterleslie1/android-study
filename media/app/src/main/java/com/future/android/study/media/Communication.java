package com.future.android.study.media;

/**
 * @author Dexterleslie.Chan
 */
public class Communication {
    private final static String TAG=Communication.class.getSimpleName();

    private UDP udp=null;

    /**
     *
     * @param receiverIp
     * @param receiverPort
     * @param userId
     * @throws Exception
     */
    public void start(final String receiverIp, final int receiverPort,String userId) throws Exception {
        this.udp=new UDP(8080,receiverIp,receiverPort,userId);
        this.udp.start();
    }

    /**
     *
     */
    public void stop(){
        if(this.udp!=null){
            this.udp.stop();
            this.udp=null;
        }
    }
}
