package com.future.study.android.alertdialog;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private AlertDialog.Builder builder;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button btnTwo = findViewById(R.id.btn_two);
        Button btnThree = findViewById(R.id.btn_three);
        Button btnList = findViewById(R.id.btn_list);
        Button btnMultiSelect = findViewById(R.id.btn_multi_select);
        Button btnSingleSelect = findViewById(R.id.btn_single_select);
        Button btnWaiting = findViewById(R.id.btn_waiting);
        Button btnLoading = findViewById(R.id.btn_loading);
        Button btnInput = findViewById(R.id.btn_input);
        Button btnMyStyle = findViewById(R.id.btn_my_style);


        btnTwo.setOnClickListener(this);
        btnThree.setOnClickListener(this);
        btnList.setOnClickListener(this);
        btnMultiSelect.setOnClickListener(this);
        btnSingleSelect.setOnClickListener(this);
        btnWaiting.setOnClickListener(this);
        btnLoading.setOnClickListener(this);
        btnInput.setOnClickListener(this);
        btnMyStyle.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btn_two://最普通dialog
                showTwo();
                break;
            case R.id.btn_three://三个按钮dialog
                showThree();
                break;
            case R.id.btn_list://列表样式dialog
                showList();
                break;
            case R.id.btn_multi_select://多选dialog
                showMultiSelect();
                break;
            case R.id.btn_single_select://单选dialog
                showSingSelect();
                break;
            case R.id.btn_waiting://等待dialog
                showWaiting();
                break;
            case R.id.btn_loading://加载进度dialog
                showLoading();
                break;
            case R.id.btn_input://输入框dialog
                showInput();
                break;
            case R.id.btn_my_style://自定义dialog
                showMyStyle();
                break;
            default:
        }
    }

    /**
     * 两个按钮的 dialog
     */
    private void showTwo() {
        builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("最普通dialog")
                .setMessage("我是最简单的dialog").setPositiveButton("确定（积极）", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        Toast.makeText(MainActivity.this, "确定按钮", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("取消（消极）", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        Toast.makeText(MainActivity.this, "关闭按钮", Toast.LENGTH_LONG).show();
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * 三个按钮的 dialog
     */
    private void showThree() {
        builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("三个按钮dialog")
                .setMessage("三个按钮dialog").setPositiveButton("确定（积极）", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        Toast.makeText(MainActivity.this, "确定按钮", Toast.LENGTH_LONG).show();
                    }
                }).setNeutralButton("你猜（中立）", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "你猜按钮", Toast.LENGTH_LONG).show();
                    }
                }).setNegativeButton("取消（消极）", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //ToDo: 你想做的事情
                        Toast.makeText(MainActivity.this, "关闭按钮", Toast.LENGTH_LONG).show();
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }

    /**
     * 列表 dialog
     */
    private void showList() {
        final String[] items = {"item 1", "item 2", "item 3", "item 4", "item 5", "item 6"};
        builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher)
                .setTitle("列表dialog")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "你点击的内容为： " + items[i], Toast.LENGTH_LONG).show();

                    }
                });
        builder.create().show();
    }

    /**
     * 多选 dialog
     */
    private void showMultiSelect() {
        final List<Integer> choice = new ArrayList<>();
        final String[] items = {"多选1", "多选2", "多选3", "多选4", "多选5", "多选6"};
        //默认都未选中
        boolean[] isSelect = {false, false, false, false, false, false};

        builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher)
                .setTitle("多选dialog")
                .setMultiChoiceItems(items, isSelect, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {

                        if (b) {
                            choice.add(i);
                        } else {
                            choice.remove(i);
                        }

                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StringBuilder str = new StringBuilder();

                        for (int j = 0; j < choice.size(); j++) {
                            str.append(items[choice.get(j)]);
                        }
                        Toast.makeText(MainActivity.this, "你选择了" + str, Toast.LENGTH_LONG).show();
                    }
                });

        builder.create().show();
    }

    private int choice;

    /**
     * 单选 dialog
     */
    private void showSingSelect() {
        //默认选中第一个
        final String[] items = {"单选1", "单选2", "单选3", "单选4", "单选5", "单选6"};
        choice = -1;
        builder = new AlertDialog.Builder(this).setIcon(R.mipmap.ic_launcher).setTitle("单选列表")
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        choice = i;
                    }
                }).setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (choice != -1) {
                            Toast.makeText(MainActivity.this, "你选择了" + items[choice], Toast.LENGTH_LONG).show();
                        }
                    }
                });
        builder.create().show();
    }

    /**
     * 圆圈加载进度的 dialog
     */
    private void showWaiting() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle("加载dialog");
        progressDialog.setMessage("加载中...");
        progressDialog.setIndeterminate(true);// 是否形成一个加载动画  true表示不明确加载进度形成转圈动画  false 表示明确加载进度
        progressDialog.setCancelable(true);//点击返回键或者dialog四周是否关闭dialog  true表示可以关闭 false表示不可关闭
        progressDialog.show();

    }

    /**
     * 带有进度的 dialog
     */
    private void showLoading() {
        final int MAX_VALUE = 100;
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgress(0);
        progressDialog.setTitle("带有加载进度dialog");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(MAX_VALUE);
        progressDialog.show();
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress = 0;
                while (progress < MAX_VALUE) {
                    try {
                        Thread.sleep(100);
                        progress++;
                        progressDialog.setProgress(progress);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //加载完毕自动关闭dialog
                progressDialog.cancel();
            }
        }).start();
    }

    /**
     * 一个输入框的 dialog
     */
    private void showInput() {
        final EditText editText = new EditText(this);
        builder = new AlertDialog.Builder(this).setTitle("输入框dialog").setView(editText)
                .setPositiveButton("读取输入框内容", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "输入内容为：" + editText.getText().toString()
                                , Toast.LENGTH_LONG).show();
                    }
                });
        builder.create().show();
    }

    /**
     * 原生自定义 dialog
     */
    private void showMyStyle() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(this).inflate(R.layout.layout_test, null);
        final EditText etUsername = view.findViewById(R.id.et_username);
        final EditText etPassword = view.findViewById(R.id.et_password);

        builder = new AlertDialog.Builder(this).setView(view).setTitle("自定义dialog——登录").setIcon(R.mipmap.ic_launcher)
                .setPositiveButton("登录", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(MainActivity.this, "账号： " + etUsername.getText().toString() + "  密码： " + etPassword.getText().toString()
                                , Toast.LENGTH_LONG).show();
                    }
                });

        builder.create().show();
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
