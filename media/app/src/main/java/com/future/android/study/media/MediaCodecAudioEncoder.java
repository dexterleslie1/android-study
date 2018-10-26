package com.future.android.study.media;

import android.media.AudioFormat;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Dexterleslie.Chan
 */
public class MediaCodecAudioEncoder extends AudioEncoder{
    private final static String TAG=MediaCodecAudioEncoder.class.getSimpleName();

    private final static int TimeoutUsMicroseconds=1000;
    private boolean isStarted=false;
    private MediaCodec codec;
    private int sampleRateInHz;

    /**
     *
     * @param sampleRateInHz
     */
    public MediaCodecAudioEncoder(int sampleRateInHz){
        this.sampleRateInHz=sampleRateInHz;
    }

    @Override
    public int getSampleRateInHz(){
        return this.sampleRateInHz;
    }

    @Override
    public synchronized void start() throws AudioException{
        if(isStarted) {
            throw new AudioException("编码器已启动");
        }
        isStarted=true;

        try {
            int sampleRateInHz = this.getSampleRateInHz();
            codec = MediaCodec.createEncoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            MediaFormat format = new MediaFormat();
            format.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC);
            format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRateInHz);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 64 * 1024);
            format.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            format.setInteger(MediaFormat.KEY_PCM_ENCODING, AudioFormat.ENCODING_PCM_16BIT);
            codec.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            codec.start();
        }catch(IOException ex){
            throw new AudioException("启动MediaCodec发生IO错误",ex);
        }
    }

    @Override
    public void stop() {
        if(codec!=null) {
            try {
                codec.stop();
            }catch(IllegalStateException ex){
                //
            }
            codec.release();
            codec = null;
        }
    }

    @Override
    public Object[] encode(Object [] data) {
        if (data == null
                || data.length == 0
                || data[0]==null
                || !(data[0] instanceof byte[])) {
            return null;
        }

        byte []inputBytes=(byte[])data[0];
        int inputBufferId = codec.dequeueInputBuffer(TimeoutUsMicroseconds);
        if (inputBufferId >= 0) {
            ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferId);
            inputBuffer.put(inputBytes);
            codec.queueInputBuffer(inputBufferId, 0, inputBytes.length, 0, 0);
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferId=codec.dequeueOutputBuffer(bufferInfo, TimeoutUsMicroseconds);
        List<Object> frames=new ArrayList<>();
        while (outputBufferId >= 0) {
            ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferId);
            int encodedLength = outputBuffer.remaining();
            byte []bytesEncoded = new byte[encodedLength];
            outputBuffer.get(bytesEncoded);
            outputBuffer.clear();
            codec.releaseOutputBuffer(outputBufferId, false);

            if(bytesEncoded.length!=0){
                frames.add(bytesEncoded);
            }

            bufferInfo = new MediaCodec.BufferInfo();
            outputBufferId = codec.dequeueOutputBuffer(bufferInfo, TimeoutUsMicroseconds);
        }
        if(frames.size()==0){
            return null;
        }
        return frames.toArray(new Object[]{});
    }

//    /**
//     *
//     */
//    public void end(){
//        try {
//            int result = codec.dequeueInputBuffer(TimeoutUsMicroseconds);
//            while (result == MediaCodec.INFO_TRY_AGAIN_LATER) {
//                result = codec.dequeueInputBuffer(TimeoutUsMicroseconds);
//            }
//            codec.queueInputBuffer(result, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
//        }catch(IllegalStateException ex){
//            Log.e(TAG,ex.getMessage(),ex);
//        }
//    }
}
