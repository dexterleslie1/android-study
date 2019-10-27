package com.future.study.android.silentvibrateringtone;

import android.content.Context;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private Ringtone ringtone = null;
    private Vibrator vibrator = null;

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

        Button button = findViewById(R.id.buttonStartRingtone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AudioManager audioManager = (AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                int ringerMode = audioManager.getRingerMode();
                if(ringerMode==AudioManager.RINGER_MODE_SILENT) {
                    Log.i(TAG, "Silent mode");
                    startVibrate();
                } else if(ringerMode==AudioManager.RINGER_MODE_VIBRATE) {
                    Log.i(TAG, "Vibrate mode");
                    startVibrate();
                } else if(ringerMode==AudioManager.RINGER_MODE_NORMAL) {
                    startRing();
                    try {
                        int result = Settings.System.getInt(getContentResolver(), Settings.System.VIBRATE_WHEN_RINGING);
                        if(result==1) {
                            // ring+vibrate
                            startVibrate();
                        }
                    } catch (Settings.SettingNotFoundException e) {
                        Log.e(TAG, e.getMessage(), e);
                    }
                }
            }
        });

        button = findViewById(R.id.buttonStopRingtone);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRing();
                stopVibrate();
            }
        });
    }

    private void startRing() {
        stopRing();
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);
        if(uri!=null) {
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
            if(ringtone!=null) {
                ringtone.play();
            }
        }
    }

    private void stopRing() {
        if(ringtone!=null) {
            ringtone.stop();
            ringtone = null;
        }
    }

    private void startVibrate() {
        stopVibrate();
        vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        VibrationEffect vibrationEffect = VibrationEffect.createWaveform(new long[] {0,1000,1000,1000,1000}, 1);
        vibrator.vibrate(vibrationEffect);
    }

    private void stopVibrate() {
        if(vibrator!=null) {
            vibrator.cancel();
            vibrator = null;
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
