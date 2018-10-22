package com.future.android.study.media;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import java.util.concurrent.locks.ReentrantLock;

public class MainActivity extends AppCompatActivity {
    private final static String TAG=MainActivity.class.getSimpleName();

    private boolean isStop=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        try {
            final ReentrantLock lock=new ReentrantLock(true);
            int sampleRateInHz=8000;
            int channelConfig= AudioFormat.CHANNEL_IN_MONO;
            int audioFormat=AudioFormat.ENCODING_PCM_16BIT;

//        final int bufferSize = AudioRecord.getMinBufferSize(
//                sampleRateInHz,
//                channelConfig,
//                audioFormat);
            final int bufferSize=2000;
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
//                                    short []shortData=new short[datas.length/2];
//                                    ByteBuffer.wrap(datas, 0, datas.length).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortData);
                                        if(lastFrame==null){
                                            lastFrame=datas;
                                        }
                                        short []aecDatas=null;
                                        if(lastFrame!=null) {
                                            aecDatas=SpeexJNI.cancellation(datas, lastFrame);
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
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
