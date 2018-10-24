package com.future.android.study.media;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * @author Dexterleslie.Chan
 */
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

        final Communication communication=new Communication();
        final Button buttonStart=findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtonState();
                String receiverIp=((EditText)findViewById(R.id.receiverIp)).getText().toString();
                String receiverPort=((EditText)findViewById(R.id.receiverPort)).getText().toString();
                try {
                    communication.start(receiverIp,Integer.parseInt(receiverPort));
                } catch (Exception e) {
                    Log.e(TAG,e.getMessage(),e);
                }
            }
        });
        final Button buttonTerminate=findViewById(R.id.buttonTerminate);
        buttonTerminate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleButtonState();
                communication.stop();
            }
        });

        final String ip=Utils.getIp(this);
        ((TextView)findViewById(R.id.deviceIP)).setText(ip);

//        Tester tester=new Tester();
//        try {
//            tester.testEcho();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void toggleButtonState(){
        Button buttonStart=findViewById(R.id.buttonStart);
        Button buttonTerminate=findViewById(R.id.buttonTerminate);
        buttonStart.setEnabled(!buttonStart.isEnabled());
        buttonTerminate.setEnabled(!buttonTerminate.isEnabled());
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
