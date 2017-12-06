package com.example.dan.wifi_login.user;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.dan.wifi_login.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class use extends Activity {
    Context context = this;
    public SwipeRefreshLayout laySwipe;

    String wifis = "";
    String cookie = "";
    double Latitude ;//東西經度
    double Longitude ;
    public final String LM_GPS = LocationManager.GPS_PROVIDER;
    public final String LM_NETWORK = LocationManager.NETWORK_PROVIDER;
    private LocationManager mLocationManager;
    private static final int REQUEST_LOCATION = 1;
    // 定位監聽器
    private LocationListener mLocationListener;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        wifis = intent.getStringExtra("wifi");
        cookie = intent.getStringExtra("cookie");
        setContentView(R.layout.activity_use);

        Button bStopwifi = (Button) findViewById(R.id.bStopwifi);
        dialog = ProgressDialog.show(use.this, "GPS取得中", "請稍後...", true);
        bStopwifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(use.this, NoteficationsService.class);
                stopService(intent);
                context.stopService(new Intent(use.this, WifiBroadcastReceiver.class));
                WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                List<WifiConfiguration> list = wifi.getConfiguredNetworks();
                for (WifiConfiguration i : list) {
                    wifi.removeNetwork(i.networkId);
                    wifi.saveConfiguration();
                }
            }
        });
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new MyLocationListener();
        openGPS(context);
    }





    public String [] sumWifi(String[]ssid,String[]password,String[]distanve,String[] UID){

        String result[] = new String[ssid.length];
        for(int i =0;i<ssid.length;i++){
            if (Double.parseDouble(distanve[i])>=30) {
                result[i]="SSID: "+"\n"+ssid[i]+"\n密碼: \n"+password[i]+"\n距離:"+distanve[i]+"M\n "+"超過30M不建議連線"+"\n唯一編號:\n"+UID[i];
            }
           else{
                result[i]="SSID: "+"\n"+ssid[i]+"\n密碼: \n"+password[i]+"\n距離: "+distanve[i]+"M\n唯一編號:\n"+UID[i];
            }

        }

        return result ;
    }


        public void connectToWifi (String ssid, String password){
            WifiManager wifi =(WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (wifi.isWifiEnabled()){
                List<WifiConfiguration> list = wifi.getConfiguredNetworks();
                for( WifiConfiguration i : list ) {
                    wifi.removeNetwork(i.networkId);
                    wifi.saveConfiguration();
                }

                WifiConfiguration conf = new WifiConfiguration();
                conf.SSID = String.format("\"%s\"", ssid);
                conf.preSharedKey = String.format("\"%s\"", password);

                WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                wifiManager.addNetwork(conf);

                int netId = wifiManager.addNetwork(conf);
                wifiManager.disconnect();
                wifiManager.enableNetwork(netId, true);
                wifiManager.reconnect();
                setMobileDataEnabled(this,false);
            }else{
                Toast.makeText(use.this,"Please Turn on your Wi-fi",Toast.LENGTH_LONG).show();
            }

        }
    private void setMobileDataEnabled(Context context, boolean enabled) {
        AlertDialog.Builder builder = new AlertDialog.Builder(use.this);
        builder.setMessage("若發現成功連上WIFI請記得將3G/4G網路關閉")
                .setNegativeButton("OK",null)
                .create()
                .show();
        final ConnectivityManager conman = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            final Class conmanClass = Class.forName(conman.getClass().getName());
            final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
            iConnectivityManagerField.setAccessible(true);
            final Object iConnectivityManager = iConnectivityManagerField.get(conman);
            final Class iConnectivityManagerClass = Class.forName(
                    iConnectivityManager.getClass().getName());
            final Method setMobileDataEnabledMethod = iConnectivityManagerClass
                    .getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
            setMobileDataEnabledMethod.setAccessible(true);

            setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    public void storeUID(String UID){
        SharedPreferences mSharedPrefernces = getSharedPreferences("UID",MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPrefernces.edit();
        mEditor.putString("UID", UID);
        mEditor.apply();
    }
    public  void makelistAndgetIntent (){
       // dialog = ProgressDialog.show(use.this, "GPS取得中", "請稍後...", true);
//分系JSON字串
        try {

            JSONArray array = new JSONArray(wifis.toString());
            String WifiSsids[] =new String[array.length()];
            String WifPasswords[]= new String[array.length()];
            String WifiDistance[]=new String[array.length()];
            String WifiUID[]=new String[array.length()];
            for(int i=0; i<array.length(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                WifiSsids[i] = jsonObject.getString("ssid");
                WifPasswords[i]=   jsonObject.getString("password");
                WifiDistance[i]= jsonObject.getString("distance");
                WifiUID[i]=jsonObject.getString("UID");


            }
//分系JSON字串結束
            laySwipe = (SwipeRefreshLayout)findViewById(R.id.wifilaySwipe);
            laySwipe.setOnRefreshListener(onSwipeToRefresh);
            laySwipe.setColorSchemeResources(
                    android.R.color.holo_red_light,
                    android.R.color.holo_blue_light,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light);
            ListAdapter wifiAdapter = new CustomAdapterWifiListView(this,sumWifi(WifiSsids,WifPasswords,WifiDistance,WifiUID));
            ListView wifiListview = (ListView) findViewById(R.id.wifiListView);
            wifiListview.setAdapter(wifiAdapter);
            wifiListview.setOnScrollListener(onListScroll);
            wifiListview.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            boolean nos = NotificationManagerCompat.from(context).areNotificationsEnabled();
                            if(nos){
                                Intent intent = new Intent(use.this, NoteficationsService.class);
                                stopService(intent);
                              context.stopService(new Intent(use.this, WifiBroadcastReceiver.class));
                            }
                            String ssidAndPassword = String.valueOf(parent.getItemAtPosition(position));
                            String[] ssidAndPasswordSplit = ssidAndPassword.split("\n");
                            connectToWifi(ssidAndPasswordSplit[1],ssidAndPasswordSplit[3]);
                            WifiManager wifiMgr = (WifiManager)getApplicationContext(). getSystemService(Context.WIFI_SERVICE);
                                WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                                if( wifiInfo.getNetworkId() == -1 ){
                                   // Not connected to an access point
                                }else {
                                    // Connected to an access point
                                    //背景執行
                                    Intent intent = new Intent(use.this, NoteficationsService.class);
//                                                      intent.putExtra("cookie",cookie);
//                                                      intent.putExtra("UID",ssidAndPasswordSplit[3]);
                                    storeUID(ssidAndPasswordSplit[7]);
                                    startService(intent);

                                    BroadcastReceiver broadcastReceiver = new WifiBroadcastReceiver(ssidAndPasswordSplit[1]);
                                    IntentFilter intentFilter = new IntentFilter();
                                    intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
                                    context.registerReceiver(broadcastReceiver, intentFilter);
                                    //背景執行
                                    }
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public SwipeRefreshLayout.OnRefreshListener onSwipeToRefresh = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            laySwipe.setRefreshing(true);
            MyLocationListener ML = new MyLocationListener();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Response.Listener<String> reponseListener = new  Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonResponse = new JSONObject(response);
                                boolean success = jsonResponse.getBoolean("hasData");
                                if(success){
                                    JSONArray wifi = jsonResponse.getJSONArray("wifi");
                                    wifis=wifi.toString();
                                }else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(use.this);
                                    builder.setMessage("Failed")
                                            .setNegativeButton("Retray",null)
                                            .create()
                                            .show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                System.out.print("eeeeeeeeeeeeeeeeeesssssssssssssssssssssss");
                            }
                        }
                    };
                    Intent intent = getIntent();
                    useWifiRequest loginrequest = new useWifiRequest(intent.getStringExtra("email"),Latitude,Longitude,Long.parseLong("20170510"),Double.parseDouble("23.32"), cookie,reponseListener);
                    RequestQueue queue = Volley.newRequestQueue(use.this);
                    queue.add(loginrequest);
                    makelistAndgetIntent();
                    laySwipe.setRefreshing(false);
                    Toast.makeText(getApplicationContext(), "Refresh done!", Toast.LENGTH_SHORT).show();
                }
            }, 3000);
        }
    };
    private AbsListView.OnScrollListener onListScroll = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
        }
        @Override
        public void onScroll(AbsListView view, int firstVisibleItem,
                             int visibleItemCount, int totalItemCount) {
            if (firstVisibleItem == 0) {
                laySwipe.setEnabled(true);
            }else{
                laySwipe.setEnabled(false);
            }
        }
    };
    @Override
    protected void onResume() {

        if (mLocationManager == null) {
            mLocationManager =
                    (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mLocationListener = new use.MyLocationListener();
        }
        // 獲得地理位置的更新資料 (GPS 與 NETWORK都註冊)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);

        }
        mLocationManager.requestLocationUpdates(LM_GPS, 100, 5, mLocationListener);
        mLocationManager.requestLocationUpdates(LM_NETWORK, 100, 5, mLocationListener);
        // setTitle("onResume ...");
        super.onResume();
    }

    // 在 pause 階段關閉 mLocationListener 監聽器不再獲得地理位置的更新資料
    @Override
    protected void onPause() {
        if (mLocationManager != null) {
            // 移除 mLocationListener 監聽器
            mLocationManager.removeUpdates(mLocationListener);
            mLocationManager = null;
        }
        //setTitle("onPause ...");
        super.onPause();
    }
    // 開啟 GPS
    public void openGPS(Context context) {
        boolean gps = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean network = mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Toast.makeText(context, "GPS : " + gps + ", Network : " + network,
                Toast.LENGTH_SHORT).show();
        if (gps || network) {
            return;
        } else {
            // 開啟手動GPS設定畫面
            Intent gpsOptionsIntent = new Intent(
                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsOptionsIntent);
        }
    }
    public void onClick(View view) {
        openGPS(context);
    }
    // 定位監聽器實作
    private class MyLocationListener implements LocationListener {
        // GPS位置資訊已更新
        public void onLocationChanged(Location location) {
            Latitude = location.getLatitude();
            Longitude =location.getLongitude();
            makelistAndgetIntent();
            dialog.dismiss();
        }
        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }
        // GPS位置資訊的狀態被更新
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }
//GPS處理參數設定結束
}

