package com.future.android.study.media;

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
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import io.reactivex.functions.Consumer;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * @author Dexterleslie.Chan
 */
public class RecorderAndPlayer {
    private final static String TAG=RecorderAndPlayer.class.getSimpleName();

    private boolean isStop=false;
    private HandlerThread handlerThread=null;
    private Handler handler=null;
    private ReentrantLock lock=new ReentrantLock(true);
    private AudioRecord recorder;
    private AudioTrack audioTrack;
    private String receiverIp;
    private int receiverPort;
    private AcousticEchoCanceler acousticEchoCanceler;
    private AutomaticGainControl automaticGainControl;
    private NoiseSuppressor noiseSuppressor;
    private NaikSoftwareStompClient client;
    private boolean isAecAvailable=false;
    private AudioEncoder encoder;
    private AudioDecoder decoder;
    private MediaDataQueue queue=new MediaDataQueue();
    private HandlerThread handlerThreadPrintQueueSize=null;
    private Handler handlerPrintQueueSize=null;
    private List<byte[]> sendList=new ArrayList<>();

    /**
     *
     * @param receiverIp
     * @param receiverPort
     * @param userId
     */
    public RecorderAndPlayer(String receiverIp, int receiverPort, String userId){
        this.receiverIp=receiverIp;
        this.receiverPort=receiverPort;
        this.client=new NaikSoftwareStompClient(this.receiverIp,this.receiverPort,userId);
    }

    /**
     *
     * @param isRecorder 是否录用设备
     * @throws Exception
     */
    public void start(boolean isRecorder) throws Exception {
        this.client.connect();

        int sampleRateInHz=8000;
        int channelInConfig= AudioFormat.CHANNEL_IN_MONO;
        int channelOutConfig=AudioFormat.CHANNEL_OUT_MONO;
        int audioFormat=AudioFormat.ENCODING_PCM_16BIT;

        this.encoder=new MediaCodecAudioEncoder(sampleRateInHz);
        this.encoder.start();
        this.decoder=new MediaCodecAudioDecoder(sampleRateInHz);
        this.decoder.start();

        SpeexJNI.open(10);

//        final int bufferSize = AudioRecord.getMinBufferSize(
//                sampleRateInHz,
//                channelInConfig,
//                audioFormat);
        int frameSize=SpeexJNI.getFrameSize();
        int samples=frameSize*1000/sampleRateInHz;
        final int bufferSize=sampleRateInHz*samples/1000*2;
        if(bufferSize<0){
            String error=String.format("AudioRecord with sampleRate=%s,channelConfig=%s,audioFormat=%s not supported and getMinBufferSize return value is %s",
                    sampleRateInHz,channelInConfig,audioFormat,bufferSize);
            throw new Exception(error);
        }

        int audioTrackBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz,
                channelOutConfig,
                audioFormat);
//        int audioTrackBufferSize=bufferSize;

        SpeexJNI.init(sampleRateInHz*samples/1000,sampleRateInHz);

        recorder=new AudioRecord(
                MediaRecorder.AudioSource.VOICE_COMMUNICATION,
                sampleRateInHz,
                channelInConfig,
                audioFormat,
                bufferSize);
        if (recorder.getState() != AudioRecord.STATE_INITIALIZED) {
            throw new Exception("AudioRecord未正确初始化，可能是由于权限原因");
        }

        // 检查设备是否支持android回声消除特性
        boolean needAudioSession=false;
        int audioSessionId=this.recorder.getAudioSessionId();
        isAecAvailable= AcousticEchoCanceler.isAvailable();
        if(isAecAvailable){
            acousticEchoCanceler=AcousticEchoCanceler.create(audioSessionId);
            acousticEchoCanceler.setEnabled(true);
            needAudioSession=true;
        }
//        boolean isAgcAvailable = AutomaticGainControl.isAvailable();
//        if(isAgcAvailable){
//            automaticGainControl=AutomaticGainControl.create(audioSessionId);
//            automaticGainControl.setEnabled(true);
//            needAudioSession=true;
//        }
//        boolean isNSAvailable= NoiseSuppressor.isAvailable();
//        if(isNSAvailable){
//            noiseSuppressor=NoiseSuppressor.create(audioSessionId);
//            noiseSuppressor.setEnabled(true);
//            needAudioSession=true;
//        }

        recorder.startRecording();

//        while(true) {
//            byte data[] = new byte[bufferSize];
//            int result = recorder.read(data, 0, data.length);
//            if (result < 0) {
//                String error = "AudioRecord error,error code:[" + result + "]";
//                throw new Exception(error);
//            }
//            Object objectReturn[]=encoder.encode(new Object[]{data});
//            if(objectReturn!=null&&objectReturn.length!=0){
//                boolean isBreak=false;
//                for(int i=0;i<objectReturn.length;i++) {
//                    data = (byte[]) objectReturn[i];
//                    if (data != null && data.length == 2) {
//                        ((MediaCodecAudioDecoder) decoder).configCodecSpecificData(data);
//                        isBreak=true;
//                    }
//                }
//                if(isBreak){
//                    break;
//                }
//            }
//        }

        if(needAudioSession){
            audioTrack = new AudioTrack(
                    AudioManager.STREAM_VOICE_CALL,
                    sampleRateInHz,
                    channelOutConfig,
                    audioFormat,
                    audioTrackBufferSize,
                    AudioTrack.MODE_STREAM,audioSessionId);
        }else {
            audioTrack = new AudioTrack(
                    AudioManager.STREAM_VOICE_CALL,
                    sampleRateInHz,
                    channelOutConfig,
                    audioFormat,
                    audioTrackBufferSize,
                    AudioTrack.MODE_STREAM);
        }

        final AudioRecord recorderInternalUsage=recorder;
        final AudioTrack audioTrackInternalUsage=audioTrack;
        audioTrackInternalUsage.play();

        handlerThread=new HandlerThread("thread#"+RecorderAndPlayer.class.getSimpleName()+"#recorder");
        handlerThread.start();
        handler=new Handler(handlerThread.getLooper());
        if(isRecorder) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            lock.lock();
                            if (isStop) {
                                break;
                            }

                            byte [] data=null;
                            if(queue != null){
                                data = queue.poll();
                                if (data != null && data.length != 0) {
                                    audioTrackInternalUsage.write(data, 0, data.length);
                                }
                            }

                            short[] datas = new short[bufferSize / 2];
                            int result = recorderInternalUsage.read(datas, 0, datas.length);
                            if (result >= 0) {
                                if (result == AudioRecord.SUCCESS) {
                                    Log.i(TAG, "成功读取AudioRecord，但没有数据返回");
                                    datas=null;
                                }

                                if (datas != null && datas.length != 0) {
                                    short [] aecData=new short[datas.length];
                                    if(data != null) {
                                        ByteBuffer byteBuffer = ByteBuffer.allocate(data.length).order(ByteOrder.LITTLE_ENDIAN);
                                        byteBuffer.put(data);
                                        short [] echoData=new short[data.length%2==0?(data.length/2):(data.length-1)/2];
                                        try {
                                            byteBuffer.rewind();
                                            byteBuffer.asShortBuffer().get(echoData);
                                        }catch(BufferUnderflowException ex){
                                            Log.e(TAG,"长度："+data.length+"#"+echoData.length);
                                            throw ex;
                                        }
                                        SpeexJNI.cancellation(datas, echoData, aecData);
                                    }
                                    datas=aecData;

//                                    ByteBuffer byteBuffer = ByteBuffer.allocate(2 * datas.length).order(ByteOrder.LITTLE_ENDIAN);
//                                    for (int i = 0; i < datas.length; i++) {
//                                        byteBuffer.putShort(datas[i]);
//                                    }
//                                    byte[] bytes = byteBuffer.array();
//
//                                    String voiceData = Base64.encodeToString(bytes, Base64.DEFAULT);
//                                    JSONObject object = new JSONObject();
//                                    object.put("voiceData", voiceData);
//                                    send("/app/sendVoiceData", object);

                                    byte encodedData[]=new byte[datas.length*2];
                                    int length=SpeexJNI.encode(datas,encodedData);
                                    byte []dataTemp=new byte[length];
                                    System.arraycopy(encodedData,0,dataTemp,0,length);
                                    sendList.add(dataTemp);
                                    if(sendList.size()>=3) {
                                        int allocateLength=0;
                                        for(int i=0;i<sendList.size();i++){
                                            allocateLength+=sendList.get(i).length+2;
                                        }
                                        ByteBuffer byteBuffer=ByteBuffer.allocate(allocateLength).order(ByteOrder.LITTLE_ENDIAN);
                                        for(int i=0;i<sendList.size();i++) {
                                            length=sendList.get(i).length;
                                            byteBuffer.putShort((short) length);
                                            byteBuffer.put(sendList.get(i));
                                        }
                                        encodedData=byteBuffer.array();
                                        sendList.clear();
                                        String voiceData = Base64.encodeToString(encodedData,Base64.DEFAULT);
                                        JSONObject object = new JSONObject();
                                        object.put("voiceData", voiceData);
                                        send("/app/sendVoiceData", object);
                                    }

//                                    Object objectReturn[]=encoder.encode(new Object[]{bytes});
//                                    if(objectReturn!=null && objectReturn.length!=0) {
//                                        for(int i=0;i<objectReturn.length;i++) {
//                                            bytes=(byte[])objectReturn[i];
//                                            byte[] bytesTemp = new byte[bytes.length + 7];
//                                            System.arraycopy(bytes, 0, bytesTemp, 7, bytes.length);
//                                            bytes = bytesTemp;
//                                            AACAdtsUtils.addADTStoPacket(sampleRateInHz, bytes, bytes.length);
//                                            String voiceData = Base64.encodeToString(bytes, Base64.DEFAULT);
//                                            JSONObject object = new JSONObject();
//                                            object.put("voiceData", voiceData);
//                                            send("/app/sendVoiceData", object);
//                                        }
//                                    }
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
                        } catch (JSONException | NotConnectedNetworkException e) {
                            Log.e(TAG, e.getMessage(), e);
                        } finally {
                            lock.unlock();
                        }
                    }
                }
            });
        }

//        if(!isRecorder) {
            this.client.subscribe("/user/queue/receiveVoiceData", new Consumer<StompMessage>() {
                @Override
                public void accept(StompMessage stompMessage) throws Exception {
                    String payload = stompMessage.getPayload();
                    byte[] data = Base64.decode(payload, Base64.DEFAULT);
//                    queue.offer(data);
//                    if(decoder!=null) {
//                        Object objectReturn[] = decoder.decode(new Object[]{data});
//                        if (objectReturn != null && objectReturn.length != 0) {
//                            for (int i = 0; i < objectReturn.length; i++) {
//                                data = (byte[]) objectReturn[i];
//                                queue.offer(data);
//                            }
//                        }
//                    }

                    ByteBuffer byteBuffer=ByteBuffer.allocate(data.length).order(ByteOrder.LITTLE_ENDIAN);
                    byteBuffer.put(data);
                    byteBuffer.rewind();
                    while(byteBuffer.hasRemaining()){
                        short length=byteBuffer.getShort();
                        byte audioData[]=new byte[length];
                        byteBuffer.get(audioData);
                        short decodedData[]=new short[bufferSize/2];
                        int decodeLength=SpeexJNI.decode(audioData,decodedData,audioData.length);
                        data=Utils.shortArrayToByteArray(decodedData,0,decodeLength);
                        queue.offer(data);
                    }
                }
            });
//        }

        this.handlerThreadPrintQueueSize=new HandlerThread("thread#"+TAG+"#printQueueSize");
        this.handlerThreadPrintQueueSize.start();
        this.handlerPrintQueueSize=new Handler(this.handlerThreadPrintQueueSize.getLooper());
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                if(queue!=null){
                    Log.d(TAG,"媒体数据队列长度："+queue.size());
                }
                handlerPrintQueueSize.postDelayed(this,1000);
            }
        };
        this.handlerPrintQueueSize.postDelayed(runnable,1000);
    }

    private void sendEmptyVoiceFrame(int bufferSize) throws JSONException, NotConnectedNetworkException {
        JSONObject object=new JSONObject();
        ByteBuffer byteBuffer=ByteBuffer.allocate(4+bufferSize).order(ByteOrder.LITTLE_ENDIAN);
        byteBuffer.putInt(bufferSize);
        byteBuffer.put(new byte[bufferSize]);
        object.put("voiceData",Base64.encodeToString(byteBuffer.array(),Base64.DEFAULT));
        this.send("/app/sendVoiceData",object);
    }

    /**
     *
     */
    public void stop(){
        try {
            lock.lock();
            isStop = true;

            if(this.encoder!=null){
                this.encoder.stop();
                this.encoder=null;
            }
            if(this.decoder!=null){
                this.decoder.stop();
                this.decoder=null;
            }

            SpeexJNI.destroy();
            SpeexJNI.close();

            if(this.client!=null){
                this.client.disconnect();
                this.client=null;
            }

            if (this.handlerThread != null) {
                this.handlerThread.quit();
                this.handlerThread = null;
            }

            if(this.handlerThreadPrintQueueSize!=null){
                this.handlerThreadPrintQueueSize.quit();
                this.handlerThreadPrintQueueSize=null;
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

    /**
     *
     * @param channel
     * @param data
     * @throws NotConnectedNetworkException
     */
    public void send(String channel, JSONObject data) throws NotConnectedNetworkException {
        if(data==null){
            return;
        }
        if(this.client==null){
            return;
        }
        this.client.send(channel,data.toString());
    }
}
