package com.future.android.study.media;

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
public class MediaCodecAudioDecoder extends AudioDecoder{
    private final static String TAG=MediaCodecAudioEncoder.class.getSimpleName();

    private final static int TimeoutUsMicroseconds=1000;

    private int sampleRateInHz;
    private MediaCodec codec;
    private boolean isStarted=false;
    private boolean isConfigCSD=false;

    /**
     *
     * @param sampleRateInHz
     */
    public MediaCodecAudioDecoder(int sampleRateInHz){
        this.sampleRateInHz=sampleRateInHz;
    }

    /**
     *
     * @return
     */
    public int getSampleRateInHz(){
        return this.sampleRateInHz;
    }

    /**
     *
     * @throws AudioException
     */
    public synchronized void start() throws AudioException {
        if(isStarted) {
            throw new AudioException("解码器已启动");
        }
        isStarted=true;

        try {
            int sampleRateInHz = this.getSampleRateInHz();
            codec = MediaCodec.createDecoderByType(MediaFormat.MIMETYPE_AUDIO_AAC);
            MediaFormat format = new MediaFormat();
            format.setString(MediaFormat.KEY_MIME, MediaFormat.MIMETYPE_AUDIO_AAC);
            format.setInteger(MediaFormat.KEY_CHANNEL_COUNT, 1);
            format.setInteger(MediaFormat.KEY_SAMPLE_RATE, sampleRateInHz);
            format.setInteger(MediaFormat.KEY_BIT_RATE, 64*1024);
            format.setInteger(MediaFormat.KEY_IS_ADTS, 1);
            codec.configure(format, null, null, 0);
            codec.start();
        }catch(IOException ex){
            throw new AudioException("启动MediaCodec发生IO错误",ex);
        }
    }

    /**
     *
     */
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

    public void configCodecSpecificData(byte []datas){
        if(this.isConfigCSD){
            return;
        }
        if(datas==null||datas.length!=2){
            throw new IllegalArgumentException("请提供正确的codec specific data数据");
        }
        int result = codec.dequeueInputBuffer(TimeoutUsMicroseconds);
        ByteBuffer inputBuffer = codec.getInputBuffer(result);
        inputBuffer.put(datas);
        codec.queueInputBuffer(result, 0, datas.length, 0, MediaCodec.BUFFER_FLAG_CODEC_CONFIG);
        this.isConfigCSD=true;
    }

    /**
     *
     * @param data
     * @return
     */
    @Override
    public Object[] decode(Object []data){
        if (data == null
                || data.length == 0
                || data[0]==null
                || !(data[0] instanceof byte[])) {
            return null;
        }

        if(!isConfigCSD){
            return data;
        }

        byte []inputBytes=(byte[])data[0];

        int inputBufferId = codec.dequeueInputBuffer(TimeoutUsMicroseconds);
        if (inputBufferId >= 0) {
            ByteBuffer inputBuffer = codec.getInputBuffer(inputBufferId);
            inputBuffer.put(inputBytes);
            codec.queueInputBuffer(inputBufferId, 0, inputBytes.length, 0, 0);
        }

        MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
        int outputBufferId = codec.dequeueOutputBuffer(bufferInfo, 10);
        List<Object> frames=new ArrayList<>();
        while (outputBufferId >= 0) {
            ByteBuffer outputBuffer = codec.getOutputBuffer(outputBufferId);
            int tempLength = outputBuffer.remaining();
            byte []bytesDecoded = new byte[tempLength];
            outputBuffer.get(bytesDecoded);
            outputBuffer.clear();
            codec.releaseOutputBuffer(outputBufferId, false);

            if(bytesDecoded.length!=0){
                frames.add(bytesDecoded);
            }

            bufferInfo = new MediaCodec.BufferInfo();
            outputBufferId=codec.dequeueOutputBuffer(bufferInfo, 10);
        }
        if(frames.size()==0){
            return null;
        }
        return frames.toArray(new Object[]{});
    }
}
