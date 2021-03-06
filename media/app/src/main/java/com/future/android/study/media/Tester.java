package com.future.android.study.media;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Dexterleslie.Chan
 */
public class Tester {
    private final static String TAG=Tester.class.getSimpleName();

    private boolean isStop=false;
    private AcousticEchoCanceler acousticEchoCanceler;
    private AutomaticGainControl automaticGainControl;
    private NoiseSuppressor noiseSuppressor;

    /**
     * 录音和播放同时进行会产生回音
     * @throws Exception
     */
    public void testEcho() throws Exception {
        final ReentrantLock lock=new ReentrantLock(true);
        int sampleRateInHz=8000;
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
                    MediaRecorder.AudioSource.MIC,
                    sampleRateInHz,
                    channelConfig,
                    audioFormat,
                    bufferSize);
            if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
                throw new Exception("AudioRecord未正确初始化，可能是由于权限原因");
            }

            // 检查设备是否支持android回声消除特性
            boolean needAudioSession=false;
            int audioSessionId=recorder.getAudioSessionId();
            boolean isAecAvailable= AcousticEchoCanceler.isAvailable();
            if(isAecAvailable){
                acousticEchoCanceler=AcousticEchoCanceler.create(audioSessionId);
                acousticEchoCanceler.setEnabled(true);
                needAudioSession=true;
            }
            boolean isAgcAvailable = AutomaticGainControl.isAvailable();
            if(isAgcAvailable){
                automaticGainControl=AutomaticGainControl.create(audioSessionId);
                automaticGainControl.setEnabled(true);
                needAudioSession=true;
            }
            boolean isNSAvailable= NoiseSuppressor.isAvailable();
            if(isNSAvailable){
                noiseSuppressor=NoiseSuppressor.create(audioSessionId);
                noiseSuppressor.setEnabled(true);
                needAudioSession=true;
            }

            recorder.startRecording();

            if(needAudioSession){
                audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        sampleRateInHz,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        audioTrackBufferSize,
                        AudioTrack.MODE_STREAM,audioSessionId);
            }else {
                audioTrack = new AudioTrack(
                        AudioManager.STREAM_VOICE_CALL,
                        sampleRateInHz,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        audioTrackBufferSize,
                        AudioTrack.MODE_STREAM);
            }

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

                                if(datas.length!=0){
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

                if(this.acousticEchoCanceler!=null){
                    this.acousticEchoCanceler.setEnabled(false);
                    this.acousticEchoCanceler.release();
                    this.acousticEchoCanceler=null;
                }
                if(this.automaticGainControl!=null){
                    this.automaticGainControl.setEnabled(false);
                    this.automaticGainControl.release();
                    this.automaticGainControl=null;
                }
                if(this.noiseSuppressor!=null){
                    this.noiseSuppressor.setEnabled(true);
                    this.noiseSuppressor.release();
                    this.noiseSuppressor=null;
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

    /**
     *
     * @throws Exception
     */
    public void testAec() throws Exception {
        final ReentrantLock lock=new ReentrantLock(true);
        int sampleRateInHz=8000;
        int channelConfig= AudioFormat.CHANNEL_IN_MONO;
        int audioFormat=AudioFormat.ENCODING_PCM_16BIT;

//        final int bufferSize = AudioRecord.getMinBufferSize(
//                sampleRateInHz,
//                channelConfig,
//                audioFormat);
        int samples=60;
        final int bufferSize=sampleRateInHz*samples/1000*2;
        if(bufferSize<0){
            String error=String.format("AudioRecord with sampleRate=%s,channelConfig=%s,audioFormat=%s not supported and getMinBufferSize return value is %s",
                    sampleRateInHz,channelConfig,audioFormat,bufferSize);
            throw new Exception(error);
        }

        int audioTrackBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
//        int audioTrackBufferSize=bufferSize;

        AudioRecord recorder = null;
        HandlerThread handlerThread=null;
        Handler handler=null;
        AudioTrack audioTrack=null;
        try {
            SpeexJNI.init(sampleRateInHz*samples/1000,sampleRateInHz);

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

                            if(lastFrame!=null&&lastFrame.length!=0){
                                audioTrackInternalUsage.write(lastFrame, 0, lastFrame.length);
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
                                    if(lastFrame!=null) {
                                        short []aecDatas=new short[datas.length];
                                        SpeexJNI.cancellation(datas, lastFrame, aecDatas);
                                        datas=aecDatas;
                                    }
                                    lastFrame=datas;
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

            Thread.sleep(30000);
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

    public boolean wantStop = false;
    static final int frequency = 8000;
    static final int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
//    static final int audioChanel = AudioFormat.CHANNEL_CONFIGURATION_MONO;
    LinkedList<short[]> listAudio = new LinkedList<short[]>();
    public void test1(Context context){
        wantStop = false;
        int bufferSize = android.media.AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, audioEncoding);
        int bufferSizeRec = android.media.AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, audioEncoding);
        final AudioRecord audioRecord = new AudioRecord(
                MediaRecorder.AudioSource.VOICE_COMMUNICATION , frequency, AudioFormat.CHANNEL_IN_MONO,
                audioEncoding,	bufferSizeRec );

        audioRecord.startRecording();
        final AudioTrack audioPlay = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, audioEncoding, bufferSize,
                AudioTrack.MODE_STREAM);

        AcousticEchoCanceler aec = AcousticEchoCanceler.create(audioRecord.getAudioSessionId());
        if (aec != null) aec.setEnabled(true);


        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                boolean playStart = false;
                short data [] = new short[320];
                int	bufferRead;
                listAudio = new LinkedList<short[]>();
                while (!wantStop)
                {
                    bufferRead = audioRecord.read(data, 0, data.length);
                    short out [] = new short[bufferRead];
                    System.arraycopy(data, 0, out, 0, bufferRead);
                    listAudio.add(out);
                    if (playStart)
                    {
                        data = listAudio.removeFirst();
                        audioPlay.write(data, 0, data.length);
                    }
                    else
                    {
                        if (listAudio.size() > 10)
                        {
                            playStart = true;
                            audioPlay.play();
                        }
                    }
                }
                audioRecord.stop();
                audioPlay.stop();
            }
        });
        thread.start();

        wantStop=true;
    }
}
