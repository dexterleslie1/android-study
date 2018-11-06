package com.future.android.study.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

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

        Button button=this.findViewById(R.id.buttonStartService);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,StartService.class);
                startService(intent);
            }
        });
        button=this.findViewById(R.id.buttonStopService);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,StartService.class);
                stopService(intent);
            }
        });

        final List<ServiceConnection> serviceConnectionList=new ArrayList<>();
        button=this.findViewById(R.id.buttonBindService);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServiceConnection serviceConnection=new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {

                    }

                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {

                    }
                };
                serviceConnectionList.add(serviceConnection);
                Intent intent=new Intent(MainActivity.this,BindService.class);
                bindService(intent,serviceConnection, Context.BIND_AUTO_CREATE);

                Log.d(TAG,"serviceConnectionList长度："+serviceConnectionList.size());
            }
        });
        button=this.findViewById(R.id.buttonUnbindService);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(serviceConnectionList.size() == 0){
                    return;
                }
                try {
                    ServiceConnection serviceConnection=serviceConnectionList.get(serviceConnectionList.size()-1);
                    unbindService(serviceConnection);
                    serviceConnectionList.remove(serviceConnection);
                }catch(Exception ex){
                    Log.e(TAG,ex.getMessage(),ex);
                }
                Log.d(TAG,"serviceConnectionList长度："+serviceConnectionList.size());
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
