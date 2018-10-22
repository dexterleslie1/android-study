package com.future.android.study.media;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.HandlerThread;
import android.provider.MediaStore;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Dexterleslie.Chan
 */
@RunWith(AndroidJUnit4.class)
public class AudioTests {
    private final static String TAG=AudioTests.class.getSimpleName();

    private boolean isStop=false;

    /**
     * 录音和播放同时进行会产生回音
     * @throws Exception
     */
    @Test
    public void testEcho() throws Exception {
        final ReentrantLock lock=new ReentrantLock(true);
        int sampleRateInHz=44100;
        int channelConfig= AudioFormat.CHANNEL_IN_MONO;
        int audioFormat=AudioFormat.ENCODING_PCM_16BIT;

        final int bufferSize = AudioRecord.getMinBufferSize(
                sampleRateInHz,
                channelConfig,
                audioFormat);
        if(bufferSize<0){
            String error=String.format("AudioRecord with sampleRate=%s,channelConfig=%s,audioFormat=%s not supported and getMinBufferSize return value is %s",
                    sampleRateInHz,channelConfig,audioFormat,bufferSize);
            throw new Exception(error);
        }

        int audioTrackBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);

        AudioRecord recorder = null;
        HandlerThread handlerThread=null;
        Handler handler=null;
        AudioTrack audioTrack=null;
        try {
            handlerThread=new HandlerThread("thread-audio-testing-echo");
            handlerThread.start();
            handler=new Handler(handlerThread.getLooper());

            recorder=new AudioRecord(
                    MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                    sampleRateInHz,
                    channelConfig,
                    audioFormat,
                    bufferSize);
            if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                throw new Exception("AudioRecord未正确初始化，可能是由于权限原因");
            }
            recorder.startRecording();

            audioTrack = new AudioTrack(
                    AudioManager.STREAM_VOICE_CALL,
                    sampleRateInHz,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    audioTrackBufferSize,
                    AudioTrack.MODE_STREAM);

            final AudioRecord recorderInternalUsage=recorder;
            final AudioTrack audioTrackInternalUsage=audioTrack;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    audioTrackInternalUsage.play();
                    while(true){
                        try {
                            lock.lock();
                            if(isStop){
                                break;
                            }
//                            byte[] datas = new byte[bufferSize];
                            short []datas=new short[bufferSize/2];
                            int result = recorderInternalUsage.read(datas, 0, datas.length);

                            if (result >= 0) {
                                // 成功读取audiorecord，但没有数据返回
                                if (result == AudioRecord.SUCCESS) {
                                    Log.i(TAG, "成功读取AudioRecord，但没有数据返回");
                                    break;
                                }

                                if(datas!=null&&datas.length!=0){
                                    audioTrackInternalUsage.write(datas, 0, datas.length);
                                }
                            } else {
                                String error = null;
                                if (result == AudioRecord.ERROR_INVALID_OPERATION) {
                                    error = "AudioRecord object isn't properly initialized";
                                    Log.e(TAG, error);
                                } else if (result == AudioRecord.ERROR_BAD_VALUE) {
                                    error = "Parameters don't resolve to valid data and indexes";
                                    Log.e(TAG, error);
                                } else if (result == AudioRecord.ERROR_DEAD_OBJECT) {
                                    error = "AudioRecord dead object,need to recreated it";
                                    Log.e(TAG, error);
                                } else if (result == AudioRecord.ERROR) {
                                    error = "AudioRecord error,error code:[" + result + "]";
                                    Log.e(TAG, error);
                                }
                                throw new RuntimeException(error);
                            }
                        }finally{
                            lock.unlock();
                        }
                    }
                }
            });

            System.out.println("placeholder");
        }finally {
            try {
                lock.lock();
                isStop=true;
                if (handlerThread != null) {
                    handlerThread.quitSafely();
                    handlerThread = null;
                }
                if (null != recorder) {
                    try {
                        if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                            recorder.stop();
                        }
                    } catch (IllegalStateException ex) {
                        Log.w(TAG, ex.getMessage(), ex);
                    }
                    recorder.release();
                    recorder = null;
                }

                if (audioTrack != null) {
                    try {
                        audioTrack.stop();
                    } catch (IllegalStateException ex) {
                        //
                    }
                    audioTrack.release();
                    audioTrack = null;
                }
            }finally{
                lock.unlock();
            }
        }
    }

    @Test
    public void testAEC() throws Exception {
        final ReentrantLock lock=new ReentrantLock(true);
        int sampleRateInHz=44100;
        int channelConfig= AudioFormat.CHANNEL_IN_MONO;
        int audioFormat=AudioFormat.ENCODING_PCM_16BIT;

//        final int bufferSize = AudioRecord.getMinBufferSize(
//                sampleRateInHz,
//                channelConfig,
//                audioFormat);
        final int bufferSize=sampleRateInHz*20/1000*2;
        if(bufferSize<0){
            String error=String.format("AudioRecord with sampleRate=%s,channelConfig=%s,audioFormat=%s not supported and getMinBufferSize return value is %s",
                    sampleRateInHz,channelConfig,audioFormat,bufferSize);
            throw new Exception(error);
        }

//        int audioTrackBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz,
//                AudioFormat.CHANNEL_OUT_MONO,
//                AudioFormat.ENCODING_PCM_16BIT);
        int audioTrackBufferSize=bufferSize;

        AudioRecord recorder = null;
        HandlerThread handlerThread=null;
        Handler handler=null;
        AudioTrack audioTrack=null;
        try {
            SpeexJNI.init(sampleRateInHz);

            handlerThread=new HandlerThread("thread-audio-testing-echo");
            handlerThread.start();
            handler=new Handler(handlerThread.getLooper());

            recorder=new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    sampleRateInHz,
                    channelConfig,
                    audioFormat,
                    bufferSize);
            if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                throw new Exception("AudioRecord未正确初始化，可能是由于权限原因");
            }
            recorder.startRecording();

            audioTrack = new AudioTrack(
                    AudioManager.STREAM_MUSIC,
                    sampleRateInHz,
                    AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    audioTrackBufferSize,
                    AudioTrack.MODE_STREAM);

            final AudioRecord recorderInternalUsage=recorder;
            final AudioTrack audioTrackInternalUsage=audioTrack;
            handler.post(new Runnable() {
                @Override
                public void run() {
                    audioTrackInternalUsage.play();
                    short [] lastFrame=null;
                    while(true){
                        try {
                            lock.lock();
                            if(isStop){
                                break;
                            }

                            short []datas=new short[bufferSize/2];
                            int result = recorderInternalUsage.read(datas, 0, datas.length);
                            if (result >= 0) {
                                // 成功读取audiorecord，但没有数据返回
                                if (result == AudioRecord.SUCCESS) {
                                    Log.i(TAG, "成功读取AudioRecord，但没有数据返回");
                                    break;
                                }

                                if(datas.length!=0){
                                    if(lastFrame==null){
                                        lastFrame=datas;
                                    }
                                    short []aecDatas=new short[bufferSize/2];
                                    if(lastFrame!=null) {
                                        SpeexJNI.cancellation(datas, lastFrame, aecDatas);
//                                        aecDatas=datas;
                                    }else{
                                        aecDatas=datas;
                                    }
                                    audioTrackInternalUsage.write(aecDatas, 0, aecDatas.length);
                                    lastFrame=aecDatas;
                                }
                            } else {
                                String error = null;
                                if (result == AudioRecord.ERROR_INVALID_OPERATION) {
                                    error = "AudioRecord object isn't properly initialized";
                                    Log.e(TAG, error);
                                } else if (result == AudioRecord.ERROR_BAD_VALUE) {
                                    error = "Parameters don't resolve to valid data and indexes";
                                    Log.e(TAG, error);
                                } else if (result == AudioRecord.ERROR_DEAD_OBJECT) {
                                    error = "AudioRecord dead object,need to recreated it";
                                    Log.e(TAG, error);
                                } else if (result == AudioRecord.ERROR) {
                                    error = "AudioRecord error,error code:[" + result + "]";
                                    Log.e(TAG, error);
                                }
                                throw new RuntimeException(error);
                            }
                        }finally{
                            lock.unlock();
                        }
                    }
                }
            });

            System.out.println("placeholder");
        }finally {
            try {
                lock.lock();
                isStop=true;

                SpeexJNI.destroy();

                if (handlerThread != null) {
                    handlerThread.quitSafely();
                    handlerThread = null;
                }
                if (null != recorder) {
                    try {
                        if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
                            recorder.stop();
                        }
                    } catch (IllegalStateException ex) {
                        Log.w(TAG, ex.getMessage(), ex);
                    }
                    recorder.release();
                    recorder = null;
                }

                if (audioTrack != null) {
                    try {
                        audioTrack.stop();
                    } catch (IllegalStateException ex) {
                        //
                    }
                    audioTrack.release();
                    audioTrack = null;
                }
            }finally{
                lock.unlock();
            }
        }
    }
}
