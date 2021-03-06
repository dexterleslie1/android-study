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
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
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
public class UDP {
    private final static String TAG=UDP.class.getSimpleName();

    private int port=8080;
//    private DatagramSocket socket=null;
    private boolean isStop=false;
    private HandlerThread handlerThread=null;
    private Handler handler=null;
    private HandlerThread handlerThreadSend=null;
    private Handler handlerSend=null;
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

    /**
     *
     * @param port
     * @param receiverIp
     * @param receiverPort
     * @param userId
     */
    public UDP(int port,String receiverIp,int receiverPort,String userId){
        this.port=port;
        this.receiverIp=receiverIp;
        this.receiverPort=receiverPort;
        this.client=new NaikSoftwareStompClient(this.receiverIp,this.receiverPort,userId);
    }

    /**
     *
     * @throws Exception
     */
    public void start() throws Exception {
        this.client.connect();

        int sampleRateInHz=8000;
        int channelInConfig= AudioFormat.CHANNEL_IN_MONO;
        int channelOutConfig=AudioFormat.CHANNEL_OUT_MONO;
        int audioFormat=AudioFormat.ENCODING_PCM_16BIT;

        this.encoder=new MediaCodecAudioEncoder(sampleRateInHz);
        this.encoder.start();
        this.decoder=new MediaCodecAudioDecoder(sampleRateInHz);
        this.decoder.start();

//        final int bufferSize = AudioRecord.getMinBufferSize(
//                sampleRateInHz,
//                channelInConfig,
//                audioFormat);
        int samples=512;
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

//        this.socket=new DatagramSocket(port);

        handlerThread=new HandlerThread("thread-udp");
        handlerThread.start();
        handler=new Handler(handlerThread.getLooper());
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                while(true){
//                    try {
//                        lock.lock();
//                        if (isStop) {
//                            break;
//                        }
//
//                        byte []data=new byte[2048];
//                        DatagramPacket packet=new DatagramPacket(data,data.length);
//                        UDP.this.socket.receive(packet);
//                        int length=packet.getLength();
//                        Log.d(TAG,"接收到数据长度："+length);
//
//                        ByteBuffer byteBuffer=ByteBuffer.wrap(packet.getData(),0,length).order(ByteOrder.LITTLE_ENDIAN);
//                        short []echoData=new short[length/2];
//                        byteBuffer.asShortBuffer().get(echoData);
//                        audioTrackInternalUsage.write(echoData,0,echoData.length);
//
//                        short []datas=new short[bufferSize/2];
//                        int result = recorderInternalUsage.read(datas, 0, datas.length);
//                        if (result >= 0) {
//                            // 成功读取audiorecord，但没有数据返回
//                            if (result == AudioRecord.SUCCESS) {
//                                Log.i(TAG, "成功读取AudioRecord，但没有数据返回");
//                                break;
//                            }
//
//                            if(datas.length!=0){
//                                short []aecDatas=new short[bufferSize/2];
//                                SpeexJNI.cancellation(datas, echoData, aecDatas);
////                                short []aecDatas=datas;
//                                byteBuffer=ByteBuffer.allocate(2*aecDatas.length).order(ByteOrder.LITTLE_ENDIAN);
//                                for(int i=0;i<aecDatas.length;i++){
//                                    byteBuffer.putShort(aecDatas[i]);
//                                }
//                                try {
//                                    send(byteBuffer.array());
//                                } catch (UnknownHostException e) {
//                                    Log.e(TAG,e.getMessage(),e);
//                                }
//                            }
//                        } else {
//                            String error = null;
//                            if (result == AudioRecord.ERROR_INVALID_OPERATION) {
//                                error = "AudioRecord object isn't properly initialized";
//                                Log.e(TAG, error);
//                            } else if (result == AudioRecord.ERROR_BAD_VALUE) {
//                                error = "Parameters don't resolve to valid data and indexes";
//                                Log.e(TAG, error);
//                            } else if (result == AudioRecord.ERROR_DEAD_OBJECT) {
//                                error = "AudioRecord dead object,need to recreated it";
//                                Log.e(TAG, error);
//                            } else if (result == AudioRecord.ERROR) {
//                                error = "AudioRecord error,error code:[" + result + "]";
//                                Log.e(TAG, error);
//                            }
//                            throw new RuntimeException(error);
//                        }
//                    } catch (IOException e) {
//                        //Log.e(TAG,e.getMessage(),e);
//                    } finally {
//                        lock.unlock();
//                    }
//                }
//            }
//        });

        handlerThreadSend=new HandlerThread("thread-udp-send");
        handlerThreadSend.start();
        handlerSend=new Handler(handlerThreadSend.getLooper());
//        handlerSend.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    send(new byte[]{0,0});
//                } catch (UnknownHostException e) {
//                    //
//                }
//            }
//        });
        this.client.subscribe("/user/queue/receiveVoiceData", new Consumer<StompMessage>() {
            @Override
            public void accept(StompMessage stompMessage) throws Exception {
                String payload=stompMessage.getPayload();
                byte [] data=Base64.decode(payload,Base64.DEFAULT);

//                // Decorder支持
//                ByteBuffer byteBuffer=ByteBuffer.allocate(data.length).order(ByteOrder.LITTLE_ENDIAN);
//                byteBuffer.put(data);
//                byteBuffer.rewind();
//                while(byteBuffer.hasRemaining()) {
//                    int dataLength=byteBuffer.getInt();
//                    if (dataLength == 2) {
//                        byte []bytesTemp = new byte[dataLength];
//                        byteBuffer.get(bytesTemp);
//                        ((MediaCodecAudioDecoder) decoder).configCodecSpecificData(bytesTemp);
//                        continue;
//                    }
//                    if (dataLength != 0 && dataLength != 4) {
//                        byte []audioData = new byte[dataLength];
//                        byteBuffer.get(audioData);
//                        Log.d(TAG, "接收到数据长度：" + audioData.length);
//
//                        Object[] objectTemp = decoder.decode(new Object[]{audioData});
//                        if (objectTemp != null && objectTemp.length != 0) {
//                            for (int i = 0; i < objectTemp.length; i++) {
//                                audioData = (byte[]) objectTemp[i];
//                                audioTrackInternalUsage.write(audioData, 0, audioData.length);
//                            }
//                        }
//                    }
//                }

                audioTrackInternalUsage.write(data, 0, data.length);

                short []datas=new short[bufferSize/2];
                int result = recorderInternalUsage.read(datas, 0, datas.length);
                if (result >= 0) {
                    // 成功读取audiorecord，但没有数据返回
                    if (result == AudioRecord.SUCCESS) {
                        Log.i(TAG, "成功读取AudioRecord，但没有数据返回");
                        datas=new short[]{0,0};
                    }

                    if(datas.length!=0) {
                        short[] aecDatas = null;
//                        if (!isAecAvailable) {
//                            aecDatas = new short[datas.length];
//                            SpeexJNI.cancellation(datas, echoData, aecDatas);
//                        } else {
                            aecDatas = datas;
//                        }

                        ByteBuffer byteBuffer=ByteBuffer.allocate(2*aecDatas.length).order(ByteOrder.LITTLE_ENDIAN);
                        for(int i=0;i<aecDatas.length;i++){
                            byteBuffer.putShort(aecDatas[i]);
                        }
                        byte[] bytes=byteBuffer.array();

//                        // Encoder支持
//                        Object []objectTemp=encoder.encode(new Object[]{bytes});
//                        if(objectTemp!=null&&objectTemp.length!=0) {
//                            List<byte[]> listBytes=new ArrayList<>();
//                            int allocateTotalBytes=0;
//                            for(int i=0;i<objectTemp.length;i++) {
//                                bytes = (byte[]) objectTemp[i];
//                                if (bytes.length != 2) {
//                                    byte[] bytesTemp = new byte[bytes.length + 7];
//                                    System.arraycopy(bytes, 0, bytesTemp, 7, bytes.length);
//                                    bytes = bytesTemp;
//                                    AACAdtsUtils.addADTStoPacket(encoder.getSampleRateInHz(), bytes, bytes.length);
//                                }
//                                listBytes.add(bytes);
//                                allocateTotalBytes=allocateTotalBytes+bytes.length+4;
//                            }
//                            byteBuffer=ByteBuffer.allocate(allocateTotalBytes).order(ByteOrder.LITTLE_ENDIAN);
//                            for(int i=0;i<listBytes.size();i++){
//                                byteBuffer.putInt(listBytes.get(i).length);
//                                byteBuffer.put(listBytes.get(i));
//                            }
//                            bytes=byteBuffer.array();
//                            String voiceData=Base64.encodeToString(bytes,Base64.DEFAULT);
//                            JSONObject object=new JSONObject();
//                            object.put("voiceData",voiceData);
//                            send("/app/sendVoiceData",object);
//                        }else{
//                            sendEmptyVoiceFrame(4);
//                        }

                        String voiceData=Base64.encodeToString(bytes,Base64.DEFAULT);
                        JSONObject object=new JSONObject();
                        object.put("voiceData",voiceData);
                        send("/app/sendVoiceData",object);
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
            }
        });

        this.sendEmptyVoiceFrame(4);
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
//            if (this.socket != null) {
//                this.socket.close();
//            }

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

            if(this.client!=null){
                this.client.disconnect();
                this.client=null;
            }

            if (this.handlerThread != null) {
                this.handlerThread.quit();
                this.handlerThread = null;
            }

            if (this.handlerThreadSend != null) {
                this.handlerThreadSend.quit();
                this.handlerThreadSend = null;
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
//        if(this.socket==null){
//            return;
//        }
//
//        InetAddress inetAddress=InetAddress.getByName(this.receiverIp);
//        final DatagramPacket packet=new DatagramPacket(data,data.length,inetAddress,this.receiverPort);
//        handlerSend.post(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if(UDP.this.socket!=null) {
//                        UDP.this.socket.send(packet);
//                    }
//                } catch (IOException e) {
//                    Log.e(TAG,e.getMessage(),e);
//                }
//            }
//        });
        if(this.client==null){
            return;
        }
        this.client.send(channel,data.toString());
    }
}
