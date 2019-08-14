package com.future.study.android.mediarecorder;

import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class AmrRecorder {
    private final static String TAG = AmrRecorder.class.getSimpleName();
    private MediaRecorder mediaRecorder = null;
    private HandlerThread handlerThread = null;
    private Handler handler = null;
//    private Thread thread = null;

    public AmrRecorder() {
        handlerThread = new HandlerThread("amr-recorder");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    /**
     *
     */
    public void start() throws IOException {
//        thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    mediaRecorder = new MediaRecorder();
//                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
//                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "sample.amr";
//                    File file = new File(filePath);
//                    if(file.exists()) {
//                        file.delete();
//                    }
//                    Log.i(TAG, "Amr file path:" + filePath);
//                    mediaRecorder.setOutputFile(filePath);
//                    mediaRecorder.prepare();
//                    mediaRecorder.start();
//                    System.out.println("kdjfdjflldlfd");
//                }catch (Exception ex) {
//                    Log.e(TAG, ex.getMessage(), ex);
////                    if(mediaRecorder != null) {
////                        stop();
////                    }
//                }
//            }
//        });
//        thread.start();
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
                try {
                    mediaRecorder = new MediaRecorder();
                    mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "sample.amr";
                    File file = new File(filePath);
                    if(file.exists()) {
                        file.delete();
                    }
                    Log.i(TAG, "Amr file path:" + filePath);
                    mediaRecorder.setOutputFile(filePath);
                    mediaRecorder.prepare();
                    mediaRecorder.start();
                    System.out.print("kdjfljdlfjl");
                }catch (Exception ex) {
                    Log.e(TAG, ex.getMessage(), ex);
//                    if(mediaRecorder != null) {
//                       stop();
//                    }
                }
//            }
//        });
    }

    /**
     *
     */
    public void stop() {
        try {
            if (mediaRecorder != null) {
                mediaRecorder.setOnErrorListener(null);
                mediaRecorder.setOnInfoListener(null);
                mediaRecorder.setPreviewDisplay(null);
                mediaRecorder.stop();
                mediaRecorder.release();
            }
        }catch(Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }finally {
            mediaRecorder = null;
        }
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                if(mediaRecorder != null) {
//                    mediaRecorder.setOnErrorListener(null);
//                    mediaRecorder.setOnInfoListener(null);
//                    mediaRecorder.setPreviewDisplay(null);
//                    mediaRecorder.stop();
//                    mediaRecorder.release();
//                    mediaRecorder = null;
//                }
//            }
//        });
    }
}
