package com.future.android.study.media;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 多媒体缓存队列
 * @author Dexterleslie.Chan
 */
public class MediaDataQueue {
    private BlockingQueue<byte []> queue=new ArrayBlockingQueue<>(50);

    /**
     *
     * @param data
     */
    public boolean offer(byte [] data){
        if(data==null){
            return false;
        }
        return queue.offer(data);
    }

    /**
     *
     * @return
     */
    public byte[] poll(){
        byte []data = null;
        try {
            data = queue.poll(10, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
        return data;
    }

    /**
     *
     * @return
     */
    public int size(){
        int size=queue.size();
        return size;
    }
}
