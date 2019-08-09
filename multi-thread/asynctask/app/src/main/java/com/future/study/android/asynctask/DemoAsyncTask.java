package com.future.study.android.asynctask;

import android.os.AsyncTask;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 *
 */
public class DemoAsyncTask extends AsyncTask<String, Integer, Long> {
    private MainActivity activity = null;
    private ProgressBar progressBar = null;
    private TextView textViewStatus = null;

    public DemoAsyncTask(MainActivity activity) {
        this.activity = activity;
        this.progressBar = this.activity.findViewById(R.id.progressBar1);
        this.textViewStatus = this.activity.findViewById(R.id.textViewStatus);
    }

    @Override
    protected Long doInBackground(String... objects) {
        // 模拟耗时任务
        for(int i=0; i<100; i++) {
            publishProgress(i);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                //
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        this.textViewStatus.setText("异步任务开始");
    }

    @Override
    protected void onPostExecute(Long o) {
        this.textViewStatus.setText("异步任务结束");
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        this.progressBar.setProgress(values[0]);
        this.textViewStatus.setText("加载..." + values[0] + "%");
    }

    @Override
    protected void onCancelled() {
        this.textViewStatus.setText("异步任务被取消");
    }
}
