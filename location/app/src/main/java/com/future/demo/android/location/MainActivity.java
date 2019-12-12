package com.future.demo.android.location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getSimpleName();

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

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
            //获取权限（如果没有开启权限，会弹出对话框，询问是否开启权限）
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //请求权限
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION}, 200);
            }
        }

        Button button = findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LocationManager locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                List<String> providers = locationManager.getProviders(true);

                if(providers!=null && providers.size()>0) {
                    for(String provider : providers) {
                        Log.i(TAG, "定位提供者：" + provider);
                    }
                }

                String locationProvider = null;
                if (providers.contains(LocationManager.GPS_PROVIDER)) {
                    //如果是GPS
                    locationProvider = LocationManager.GPS_PROVIDER;
                } else if (providers.contains(LocationManager.NETWORK_PROVIDER)) {
                    //如果是Network
                    locationProvider = LocationManager.NETWORK_PROVIDER;
                } else {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                    for(String provider : providers) {
//                        Location location = locationManager.getLastKnownLocation(provider);
//                        if (location == null) {
//                            continue;
//                        }
//                        double latitude = location.getLatitude();
//                        double longtitude = location.getLongitude();
//                        Log.i(TAG, "定位经度：" + longtitude + "，纬度：" + latitude);
//                    }

//                    Location location = getLastKnownLocation();
//                    if(location!=null) {
//                        double latitude = location.getLatitude();
//                        double longtitude = location.getLongitude();
//                        Log.i(TAG, "定位经度：" + longtitude + "，纬度：" + latitude);
//                    }

//                    LocationListener locationListener = new LocationListener() {
//
//                        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
//                        @Override
//                        public void onStatusChanged(String provider, int status, Bundle extras) {
//
//                        }
//
//                        // Provider被enable时触发此函数，比如GPS被打开
//                        @Override
//                        public void onProviderEnabled(String provider) {
//
//                        }
//
//                        // Provider被disable时触发此函数，比如GPS被关闭
//                        @Override
//                        public void onProviderDisabled(String provider) {
//
//                        }
//
//                        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
//                        @Override
//                        public void onLocationChanged(Location location) {
////                            double latitude = location.getLatitude();
////                            double longtitude = location.getLongitude();
////                            Log.i(TAG, "定位经度：" + longtitude + "，纬度：" + latitude);
//                        }
//                    };
//                    locationManager.requestLocationUpdates(locationProvider, 3000, 1, locationListener);

                    Location location = locationManager.getLastKnownLocation(locationProvider);
                    if(location!=null) {
                        double latitude = location.getLatitude();
                        double longtitude = location.getLongitude();
                        Log.i(TAG, "定位经度：" + longtitude + "，纬度：" + latitude);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "没有定位权限", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

//    private Location getLastKnownLocation() {
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return null;
//        }
//        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        List<String> providers = locationManager.getAllProviders();
//        Location bestLocation = null;
//        for (String provider : providers) {
//            Location l = locationManager.getLastKnownLocation(provider);
//            if (l == null) {
//                continue;
//            }
//            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
//                // Found best last known location: %s", l);
//                bestLocation = l;
//            }
//        }
//        return bestLocation;
//    }

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
