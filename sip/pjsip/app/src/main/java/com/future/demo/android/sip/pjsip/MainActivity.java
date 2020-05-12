package com.future.demo.android.sip.pjsip;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pjsip.pjsua2.AccountConfig;
import org.pjsip.pjsua2.AuthCredInfo;
import org.pjsip.pjsua2.Endpoint;
import org.pjsip.pjsua2.EpConfig;
import org.pjsip.pjsua2.OnRegStateParam;
import org.pjsip.pjsua2.StringVector;
import org.pjsip.pjsua2.TransportConfig;
import org.pjsip.pjsua2.UaConfig;
import org.pjsip.pjsua2.pjsip_transport_type_e;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

    private final static String SharedPreferencesKeySipHost = "sipHost";
    private final static String SharedPreferencesKeySipPort = "sipPort";
    private final static String SharedPreferencesKeySipAccount = "sipAccount";
    private final static String SharedPreferencesKeySipAccountPassword = "sipAccountPassword";
    private final static String SharedPreferencesKeySipCallee = "sipCallee";

    private final static String PjsuaLibrary = "pjsua2";

    private Endpoint endpoint = null;

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

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O) {
//            int result = ContextCompat.checkSelfPermission(
//                    this, Manifest.permission.INTERNET | Manifest.permission.RECORD_AUDIO);
//            if(result!= PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this, new String[]{Manifest.permission.INTERNET,Manifest.permission.RECORD_AUDIO}, 200);
//            }
        }

        EventBus.getDefault().register(this);

        // 登录
        Button button = findViewById(R.id.buttonLogin);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setEnabled(false);
                try {
                    saveDataToSharedPreferences();

                    String sipHost;
                    int sipPort;
                    String sipAccount;
                    String sipAccountPassword;

                    EditText editText = findViewById(R.id.editTextSipHost);
                    sipHost = editText.getEditableText().toString();
                    editText = findViewById(R.id.editTextSipPort);
                    sipPort = Integer.parseInt(editText.getEditableText().toString());
                    editText = findViewById(R.id.editTextSipAccount);
                    sipAccount = editText.getEditableText().toString();
                    editText = findViewById(R.id.editTextSipAccountPassword);
                    sipAccountPassword = editText.getEditableText().toString();

                    AccountConfig accountConfig = new AccountConfig();
                    String idUri = String.format("sip:%s@%s", sipAccount, sipHost + ":" + sipPort);
                    accountConfig.setIdUri(idUri);

                    accountConfig.getRegConfig().setRegistrarUri("sip:" + sipHost + ":" + sipPort);
                    AuthCredInfo authCredInfo = new AuthCredInfo("digest", "*", sipAccount, 0, sipAccountPassword);
                    accountConfig.getSipConfig().getAuthCreds().add(authCredInfo);
//                    accountConfig.getNatConfig().setIceEnabled(true);
                    AccountExt account = new AccountExt(MainActivity.this);
                    account.create(accountConfig);
                    GlobalReference.accountExt = account;
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage(), ex);
                }
            }
        });

        // 退出登录
        button = findViewById(R.id.buttonLogout);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    releaseAccount();

                    Button button = findViewById(R.id.buttonLogin);
                    button.setEnabled(true);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });

        // 拨打电话
        button = findViewById(R.id.buttonMakeCall);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(GlobalReference.accountExt==null) {
                    Toast.makeText(MainActivity.this, "未登录", Toast.LENGTH_SHORT).show();
                    return;
                }

                EditText editText = findViewById(R.id.editTextCallee);
                String callee = editText.getEditableText().toString();
                if (TextUtils.isEmpty(callee)) {
                    Toast.makeText(MainActivity.this, "指定被叫用户", Toast.LENGTH_SHORT).show();
                    return;
                }

                saveDataSipCalleeToSharedPreferences();

                try {
                    String uri = GlobalReference.accountExt.getInfo().getUri();
                    String uriSuffix = uri.substring(uri.indexOf("@"));
                    String destinationUri = "sip:" + callee + uriSuffix;
                    GlobalReference.accountExt.makeCall(destinationUri);

                    OnInviteStateEarlyEvent event = new OnInviteStateEarlyEvent();
                    EventBus.getDefault().post(event);
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage(), ex);
                }
            }
        });

        loadDataFromSharedPreferences();
        // 初始化Sip
        System.loadLibrary(PjsuaLibrary);
        String message = String.format("成功加载 %s 库", PjsuaLibrary);
        Log.i(TAG, message);
        this.initPjsip();
    }

    /**
     * 初始化pjsip库
     */
    public synchronized void initPjsip() {
        if(GlobalReference.accountExt!=null) {
            GlobalReference.accountExt.delete();
        }
        if(this.endpoint!=null) {
            try {
                this.destroyPjsip();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        try {
            this.endpoint = new Endpoint();
            this.endpoint.libCreate();

            EpConfig epConfig = new EpConfig();
//            epConfig.getLogConfig().setLevel(5);
            epConfig.getUaConfig().setMaxCalls(1);
            UaConfig uaConfig = epConfig.getUaConfig();
            uaConfig.setUserAgent("Pjsua2 Android " + endpoint.libVersion().getFull());
            StringVector stunServers = new StringVector();
            stunServers.add("stun.pjsip.org");
            uaConfig.setStunServer(stunServers);
            endpoint.libInit(epConfig);

            TransportConfig transportConfig = new TransportConfig();
//            transportConfig.setPort(6060);
            endpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_UDP, transportConfig);
            endpoint.transportCreate(pjsip_transport_type_e.PJSIP_TRANSPORT_TCP, transportConfig);

            endpoint.libStart();

            Log.i(TAG, PjsuaLibrary + "已启动");
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    /**
     *
     */
    private void releaseAccount() throws Exception {
        if(GlobalReference.accountExt!=null) {
            CallExt call = GlobalReference.accountExt.getCurrentCall();
            if(call!=null) {
                call.hangup();
                GlobalReference.accountExt.resetCurrentCall();;
            }
            GlobalReference.accountExt.delete();
            GlobalReference.accountExt = null;
        }
    }


    /**
     *
     */
    public synchronized void destroyPjsip() throws Exception {
        releaseAccount();
        if(endpoint!=null) {
            try {
                endpoint.libDestroy();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            } finally {
                endpoint.delete();
            }
            endpoint = null;
        }
    }

    /**
     *
     * @return
     */
    public Endpoint getEndpoint() {
        return this.endpoint;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        EventBus.getDefault().unregister(this);

        // 销毁pjsip
        try {
            this.destroyPjsip();
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage(), ex);
        }
    }

    /**
     * APP初始化时从SharedPreferences加载以往保存数据
     */
    private void loadDataFromSharedPreferences() {
        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("data", MODE_PRIVATE);
        if(sharedPreferences.contains(SharedPreferencesKeySipHost)) {
            String value = sharedPreferences.getString(SharedPreferencesKeySipHost, "192.168.1.151");
            EditText editText = findViewById(R.id.editTextSipHost);
            editText.setText(value);
        }
        if(sharedPreferences.contains(SharedPreferencesKeySipPort)) {
            int value = sharedPreferences.getInt(SharedPreferencesKeySipPort, 5060);
            EditText editText = findViewById(R.id.editTextSipPort);
            editText.setText(String.valueOf(value));
        }
        if(sharedPreferences.contains(SharedPreferencesKeySipAccount)) {
            String value = sharedPreferences.getString(SharedPreferencesKeySipAccount, "1002");
            EditText editText = findViewById(R.id.editTextSipAccount);
            editText.setText(value);
        }
        if(sharedPreferences.contains(SharedPreferencesKeySipAccountPassword)) {
            String value = sharedPreferences.getString(SharedPreferencesKeySipAccountPassword, "");
            EditText editText = findViewById(R.id.editTextSipAccountPassword);
            editText.setText(value);
        }
        if(sharedPreferences.contains(SharedPreferencesKeySipCallee)) {
            String value = sharedPreferences.getString(SharedPreferencesKeySipCallee, "");
            EditText editText = findViewById(R.id.editTextCallee);
            editText.setText(value);
        }
    }

    /**
     * 保存数据到SharedPreferences
     */
    private void saveDataToSharedPreferences() {
        String sipHost;
        int sipPort;
        String sipAccount;
        String sipAccountPassword;

        EditText editText = findViewById(R.id.editTextSipHost);
        sipHost = editText.getEditableText().toString();
        editText = findViewById(R.id.editTextSipPort);
        sipPort = Integer.parseInt(editText.getEditableText().toString());
        editText = findViewById(R.id.editTextSipAccount);
        sipAccount = editText.getEditableText().toString();
        editText = findViewById(R.id.editTextSipAccountPassword);
        sipAccountPassword = editText.getEditableText().toString();

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SharedPreferencesKeySipHost, sipHost);
        editor.putInt(SharedPreferencesKeySipPort, sipPort);
        editor.putString(SharedPreferencesKeySipAccount, sipAccount);
        editor.putString(SharedPreferencesKeySipAccountPassword, sipAccountPassword);
        editor.commit();
    }

    /**
     * 保存callee信息到SharedPreferences
     */
    private void saveDataSipCalleeToSharedPreferences() {
        String callee;

        EditText editText = findViewById(R.id.editTextCallee);
        callee = editText.getEditableText().toString();

        SharedPreferences sharedPreferences = getBaseContext().getSharedPreferences("data", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(SharedPreferencesKeySipCallee, callee);
        editor.commit();
    }

    /**
     * 登录成功
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void subscribeSigninSuccessEvent(SigninSuccessEvent event) {
        Log.i(TAG, "用户登录成功");
    }

    /**
     * 登录失败
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void subscribeSigninFailEvent(SigninFailEvent event) {
        Button button = findViewById(R.id.buttonLogin);
        button.setEnabled(true);

        OnRegStateParam onRegStateParam = event.onRegStateParam;
        String statusText=PjsipStatusConstant.getLoginStatusTextByStatusCode(onRegStateParam.getCode());
        String errorMessage="用户登录失败，原因：" + statusText + "，错误代号：" + onRegStateParam.getCode();
        Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
    }

    /**
     * 已拨通被叫电话，等待被叫接听
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void subscribeOnInviteStateEarlyEvent(OnInviteStateEarlyEvent event) {
        Intent intent = new Intent(MainActivity.this, CallerActivity.class);
        String callee=((EditText)findViewById(R.id.editTextCallee)).getText().toString();
        intent.putExtra("callee",callee);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void subscribeOnIncomingEvent(OnIncomingCallEvent event) {
        Intent intent = new Intent(MainActivity.this, CalleeActivity.class);
        startActivity(intent);
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
