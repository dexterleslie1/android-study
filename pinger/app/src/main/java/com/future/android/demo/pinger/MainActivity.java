package com.future.android.demo.pinger;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.potterhsu.Pinger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static String Host = "www.baidu.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    200);
        }

        Button button = findViewById(R.id.buttonPingSynchronization);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Pinger pinger = new Pinger();
                    boolean reachable = pinger.ping(Host, 5);
                    if(reachable) {
                        Toast.makeText(MainActivity.this, Host + " 可达", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, Host + " 不可达", Toast.LENGTH_LONG).show();
                    }
                } catch (InterruptedException e) {
                    // 忽略错误
                }
            }
        });

        button = findViewById(R.id.buttonPingAsynUntilSucceed);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pinger pinger = new Pinger();
                pinger.setOnPingListener(new Pinger.OnPingListener() {
                    @Override
                    public void onPingSuccess() {
                        Log.i(TAG, "Ping " + Host + " 成功");
                    }

                    @Override
                    public void onPingFailure() {
                        Log.i(TAG, "Ping " + Host + " 失败");
                    }

                    @Override
                    public void onPingFinish() {
                        Log.i(TAG, "Ping " + Host + " 结束");
                    }
                });
                pinger.pingUntilSucceeded(Host, 2000);
            }
        });

        button = findViewById(R.id.buttonPingAsynUntilFailed);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pinger pinger = new Pinger();
                pinger.setOnPingListener(new Pinger.OnPingListener() {
                    @Override
                    public void onPingSuccess() {
                        Log.i(TAG, "Ping " + Host + " 成功");
                    }

                    @Override
                    public void onPingFailure() {
                        Log.i(TAG, "Ping " + Host + " 失败");
                    }

                    @Override
                    public void onPingFinish() {
                        Log.i(TAG, "Ping " + Host + " 结束");
                    }
                });
                pinger.pingUntilFailed(Host, 2000);
            }
        });
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
