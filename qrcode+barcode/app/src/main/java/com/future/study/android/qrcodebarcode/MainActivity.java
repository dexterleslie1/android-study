package com.future.study.android.qrcodebarcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

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

        Button button = findViewById(R.id.buttonScan);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ifGranted = PermissionUtils.checkIfPermissionGranted(MainActivity.this, "android.permission.CAMERA");
                if(!ifGranted) {
                    PermissionUtils.requestPermission(MainActivity.this, "android.permission.CAMERA");
                    return;
                }else {
                    Intent intent = new Intent(MainActivity.this, SimpleScannerActivity.class);
                    startActivity(intent);
                }
            }
        });

        // 生成二维码按钮
        button = findViewById(R.id.buttonGenerate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editTextQRCode = findViewById(R.id.editTextQRCode);
                String qrCode = editTextQRCode.getText().toString();
                if(TextUtils.isEmpty(qrCode)) {
                    Toast.makeText(MainActivity.this, "请填入生成二维码字符串", Toast.LENGTH_LONG).show();
                    return;
                }

                Bitmap bitmap = CodeUtils.createQRImage(qrCode, 300, 300);
                ImageView imageViewQRCode = findViewById(R.id.imageViewQRCode);
                imageViewQRCode.setImageBitmap(bitmap);
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
