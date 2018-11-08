package com.future.android.study.media;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dexterleslie.Chan
 */
@RunWith(AndroidJUnit4.class)
public class SpeexJNITests {
    @Test
    public void initAndDestroy(){
        SpeexJNI.init(640,8000);
        SpeexJNI.destroy();
    }
}
