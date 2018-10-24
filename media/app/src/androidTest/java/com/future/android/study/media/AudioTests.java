package com.future.android.study.media;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Dexterleslie.Chan
 */
@RunWith(AndroidJUnit4.class)
public class AudioTests {
    @Test
    public void testEcho() throws Exception {
        Tester tester=new Tester();
        tester.testEcho();
    }

    @Test
    public void testAec() throws Exception {
        Tester tester=new Tester();
        tester.testAec();
    }
}
