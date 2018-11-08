package com.future.android.study.media;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dexterleslie.Chan
 */
@RunWith(AndroidJUnit4.class)
public class OpusEngineTests {
    @Test
    public void openAndClose(){
        int result = OpusEngine.open(10);
        Assert.assertEquals(1,result);
        OpusEngine.close();
    }

    @Test
    public void getFrameSize(){
        int frameSize = OpusEngine.getFrameSize();
        Assert.assertEquals(160,frameSize);
    }
}
