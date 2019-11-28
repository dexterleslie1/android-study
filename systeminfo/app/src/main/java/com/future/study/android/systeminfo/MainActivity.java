package com.future.study.android.systeminfo;

import android.Manifest;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
            int result = ContextCompat.checkSelfPermission(
                    this, Manifest.permission.READ_PHONE_STATE);
            if(result!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.READ_PHONE_STATE}, 200);
            }
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button button = findViewById(R.id.buttonObtainSystemInfo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringBuilder builder = new StringBuilder();

                String serial;
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    try {
                        serial = Build.getSerial();
                    } catch (SecurityException ex) {
                        serial = "权限不足";
                    }
                } else {
                    serial = Build.SERIAL;
                }
                builder.append("Serial: " + serial + "\n");
                String model = Build.MODEL;
                builder.append("Model: " + model + "\n");
                String buildId = Build.ID;
                builder.append("ID: " + buildId + "\n");
                String manufacturer = Build.MANUFACTURER;
                builder.append("Manufacturer: " + manufacturer + "\n");
                String brand = Build.BRAND;
                builder.append("Brand: " + brand + "\n");
                String type = Build.TYPE;
                builder.append("Type: " + type + "\n");
                String user = Build.USER;
                builder.append("User: " + user + "\n");
                String incremental = Build.VERSION.INCREMENTAL;
                builder.append("Incremental: " + incremental + "\n");
                int sdkInt = Build.VERSION.SDK_INT;
                builder.append("SdkInt: " + sdkInt + "\n");
                String board = Build.BOARD;
                builder.append("Board: " + board + "\n");
                String host = Build.HOST;
                builder.append("Host: " + host + "\n");
                String fingerPrint = Build.FINGERPRINT;
                builder.append("FingerPrint: " + fingerPrint + "\n");
                String release = Build.VERSION.RELEASE;
                builder.append("Release: " + release + "\n");

                Log.i(TAG, builder.toString());

                TextView textView = findViewById(R.id.textViewSystemInfo);
                textView.setText(builder.toString());
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
