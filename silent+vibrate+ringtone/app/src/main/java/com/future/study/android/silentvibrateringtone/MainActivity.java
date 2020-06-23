package com.future.study.android.silentvibrateringtone;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
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

        VibrationManager.init(getApplicationContext());
        RingManager.init(getApplicationContext());

        Button button = findViewById(R.id.buttonStartRingtone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
////                AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
////                int ringerMode = audioManager.getRingerMode();
////                if(ringerMode==AudioManager.RINGER_MODE_SILENT) {
////                    Log.i(TAG, "Silent mode");
////                    startVibrate();
////                } else if(ringerMode==AudioManager.RINGER_MODE_VIBRATE) {
////                    Log.i(TAG, "Vibrate mode");
////                    startVibrate();
////                } else if(ringerMode==AudioManager.RINGER_MODE_NORMAL) {
//                    startRing();
////                    try {
////                        int result = Settings.System.getInt(getContentResolver(), Settings.System.VIBRATE_WHEN_RINGING);
////                        Log.i(TAG, "Vibrate when ringing:" + result);
////                        if(result==1) {
//                            // ring+vibrate
////                            startVibrate();
////                        }
////                    } catch (Settings.SettingNotFoundException e) {
////                        Log.e(TAG, e.getMessage(), e);
////                    }
////                }
                RingManager.getInstance().start(30);
            }
        });

        button = findViewById(R.id.buttonStopRingtone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                stopRing();
                RingManager.getInstance().stop();
            }
        });

        // 开始震动
        button = findViewById(R.id.buttonStartVibration);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrationManager.getInstance().start(30);
            }
        });
        // 停止震动
        button = findViewById(R.id.buttonStopVibration);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VibrationManager.getInstance().stop();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();


    }
}
