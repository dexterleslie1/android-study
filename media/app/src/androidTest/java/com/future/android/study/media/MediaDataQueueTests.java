package com.future.android.study.media;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

/**
 * @author Dexterleslie.Chan
 */
@RunWith(AndroidJUnit4.class)
public class MediaDataQueueTests {
    private final static String TAG=MediaDataQueueTests.class.getSimpleName();
    private boolean isStop=false;

    @Test
    public void test() throws InterruptedException {
        MediaDataQueue queue=new MediaDataQueue();
        HandlerThread handlerThread=new HandlerThread("thread-testing");
        handlerThread.start();
        Handler handler=new Handler(handlerThread.getLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                while(!isStop){
                   queue.poll();
                   Log.d(TAG,""+new Date());
                }
            }
        });

        Thread.sleep(3000);
        Log.d(TAG,"placeholder");
        isStop=true;
    }

    @Test
    public void test1(){
        MediaDataQueue queue=new MediaDataQueue();
        for(int i=0;i<100;i++){
            queue.offer(new byte[]{(byte)i});
        }
        int size=queue.size();
        Assert.assertEquals(100,size);
        for(int i=0;i<size;i++){
            byte []data=queue.poll();
            Assert.assertEquals((byte)i,data[0]);
        }
    }
}
