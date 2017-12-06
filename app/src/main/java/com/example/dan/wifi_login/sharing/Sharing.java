package com.example.dan.wifi_login.sharing;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.dan.wifi_login.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Random;

public class Sharing extends Activity {
    public Context context;
    public Sharing Sharingcontext;
    private static final int REQUEST_LOCATION = 1;
    double Latitude;//東西經度
    double Longitude;
    String SSID="";
    String password="";
    boolean jadgewifi;
    public final String LM_GPS = LocationManager.GPS_PROVIDER;
    public final String LM_NETWORK = LocationManager.NETWORK_PROVIDER;
    // 定位管理器
    private LocationManager mLocationManager;
    // 定位監聽器
    private LocationListener mLocationListener;

    ToggleButton wifiapSwitch;
    Button bRecover;
    Button bApRandon;
    TextView Tsharing;
    ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context=this;
        this.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Tsharing=   (TextView) findViewById(R.id.Tsharing);
        Sharingcontext= this;
        setContentView(R.layout.activity_sharing);


        wifiapSwitch = (ToggleButton) findViewById(R.id.toggleButton);
        wifiapSwitch.setEnabled(false);
        bRecover = (Button)findViewById(R.id.bRecover);
        bRecover.setEnabled(true);
        bApRandon = (Button)findViewById(R.id.bApRandon);
        bApRandon.setEnabled(false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        dialog = ProgressDialog.show(Sharing.this, "GPS取得中", "請稍後...", true);
        //----------------------------------------------------***取得GPS***---------------------------------------------------


        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new Sharing.MyLocationListener();
        openGPS(context);

        //----------------------------------------------------***取得GPS結束***--------------------------------------------------


        final WifiManager wifiManager = (WifiManager)getApplicationContext(). getSystemService(Context.WIFI_SERVICE);


        final int apState;

        try {
            apState = (Integer) wifiManager.getClass().getMethod("getWifiApState").invoke(wifiManager);

            if (apState == 13) {
                // Ap Enabled
                wifiapSwitch.setChecked(true);
                bRecover.setEnabled(false);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        bRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setHotspotName(getlastAPName(),getlastAPPassword(),context);
            }
        });

        bApRandon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Sharing sd = new Sharing();
                char[] chars = "abcdefghi0j1k2l3m4n5o6p7q8r9stuvwxyz".toCharArray();
                StringBuilder sb = new StringBuilder();
                Random random = new Random();
                for (int i = 0; i < 8; i++) {
                    char c = chars[random.nextInt(chars.length)];
                    sb.append(c);
                }
                String output = sb.toString();
                System.out.println(""+output);

                setHotspotName(getSessionemil(),output,context);
                ApManager apManager = new ApManager();
                setWiFiApMode(true);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("您的隨機分享密碼是: \n"+output)
                        .setNegativeButton("OK",null)
                        .create()
                        .show();
            }
        });
        bApRandon.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("此功能產生8位數隨機密碼, 用意在於如果今天突然有朋友想要借網路,不想給自己的無線基地台密碼,而隨機產生一組給他人使用\n如果要恢復原本密碼可以按左方按鈕即可")
                        .setNegativeButton("OK",null)
                        .create()
                        .show();

                return true;
            }
        });
        wifiapSwitch.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public  boolean onLongClick(View v) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("此為主要功能 可以當一個陌生生的分享者分享網路 ")
                    .setNegativeButton("OK",null)
                    .create()
                    .show();

            return true;
        }
        });
        wifiapSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override

            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Response.Listener<String> reponseListener = new  Response.Listener<String>(){
                        @Override
                        public void onResponse(String response) {
                            try {

                                JSONObject jsonResponse = new JSONObject((String)response);
                                boolean success = jsonResponse.getBoolean("hasData");
                                //System.out.println(response);
                                bRecover.setEnabled(false);
                                bApRandon.setEnabled(false);

                                if(success){
                                    JSONObject wifi = jsonResponse.getJSONObject("wifi");
                                    String WifiSsids[] =new String[1];
                                    String WifPasswords[]= new String[1];
                                    WifiSsids[0] = wifi.getString("ssid");
                                    WifPasswords[0]=   wifi.getString("password");

                                    SSID= WifiSsids[0];
                                    password = WifPasswords[0];

                                    System.out.println(SSID+"SSIDSSIDSSIDSSIDSSIDSSID+1"+password);
                                    setHotspotName(SSID,password,context);
                                    //turnOnOffHotspot(context,true);
                                    ApManager apManager = new ApManager();
                                    //apManager.configApState(context);
                                    setWiFiApMode(true);
                                    //Tsharing.append("ON");

                                    storeHotspotPasswordAndHotspoSSID(SSID,password);
                                    //背景執行
                                    Intent intent = new Intent(Sharing.this,KeepTalkingNoteficationsService.class);
                                    startService(intent);
                                }else {

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage("取得資料失敗")
                                            .setNegativeButton("Retray",null)
                                            .create()
                                            .show();
                                    wifiapSwitch.setChecked(false);


                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                                System.out.print("失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗"+e);
                            }
                        }
                    };
                    OpenApRequest req = new OpenApRequest(getSessionemil(),getLoginUID(),  Latitude, Longitude ,getCookie(),reponseListener,Sharingcontext);
                    RequestQueue queue = Volley.newRequestQueue(Sharing.this);
                    queue.add(req);

                    dialog = ProgressDialog.show(Sharing.this, "處理中", "請稍後...", true);
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try{
                                Thread.sleep(1200);

                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }
                            finally{

                                dialog.dismiss();

                            }
                        }
                    }).start();



                    if(!jadgewifi){turnOnOffHotspot(context,false);
                        bRecover.setEnabled(true);
                        bApRandon.setEnabled(true);
                    }

                } else {
                    bRecover.setEnabled(true);
                    bApRandon.setEnabled(true);
                    Response.Listener<String> reponseListener = new  Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                    System.out.print(response+"deletedeletedeletedeletedeletedeletedeletedeletedeletedeletedeletedeletedeletedeletedeletedeletedeletedelete");
                        }
                    };
                    CloseApRequest req = new CloseApRequest(getCookie(),reponseListener,Sharingcontext);
                    RequestQueue queue = Volley.newRequestQueue(Sharing.this);
                    queue.add(req);
                   // Tsharing.append("OFF");
                    turnOnOffHotspot(context,false);
                    Intent intent = new Intent(Sharing.this,KeepTalkingNoteficationsService.class);
                    stopService(intent);


                }
            }


        });


    }

    //GPS處理參數設定
    // 在 resume 階段設定 mLocationListener 監聽器，以獲得地理位置的更新資料
    @Override
    protected void onResume() {

        if (mLocationManager == null) {
            mLocationManager =
                    (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            mLocationListener = new Sharing.MyLocationListener();
        }
        // 獲得地理位置的更新資料 (GPS 與 NETWORK都註冊)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);

        }
        mLocationManager.requestLocationUpdates(LM_GPS, 5000, 5, mLocationListener);
        mLocationManager.requestLocationUpdates(LM_NETWORK, 5000, 5, mLocationListener);

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

        Toast toast = Toast.makeText(Sharing.this,
                "GPS : " + gps + ", Network : " + network, Toast.LENGTH_LONG);
        toast.show();

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
            TextView Tsharing = (TextView) findViewById(R.id.Tsharing);
            Tsharing.setText(
                    "緯度-Latitude：" + location.getLatitude() + "\n" +
                    "經度-Longitude：" + location.getLongitude() + "\n"
                    //  "精確度-Accuracy：" + location.getAccuracy() + "\n" +
                    // "標高-Altitude：" + location.getAltitude() + "\n" +
                    // "時間-Time：" + new Date(location.getTime()) + "\n" +
                    //  "速度-Speed：" + location.getSpeed() + "\n" +
                    // "方位-Bearing："+ location.getBearing()
            );
            dialog.dismiss();
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();



                wifiapSwitch.setEnabled(true);


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
    public static void turnOnOffHotspot(Context context, boolean isTurnToOn) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiApControl apControl = WifiApControl.getApControl(wifiManager);
        if (apControl != null) {

            // TURN OFF YOUR WIFI BEFORE ENABLE HOTSPOT
            //if (isWifiOn(context) && isTurnToOn) {
            //  turnOnOffWifi(context, false);
            //}

            apControl.setWifiApEnabled(apControl.getWifiApConfiguration(),
                    isTurnToOn);
        }
    }

    public static boolean setHotspotName(String newName,String password, Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            Method getConfigMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
            WifiConfiguration wifiConfig = (WifiConfiguration) getConfigMethod.invoke(wifiManager);
            wifiConfig.preSharedKey=password;
            wifiConfig.SSID = newName;

            Method setConfigMethod = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            setConfigMethod.invoke(wifiManager, wifiConfig);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }


//           wifiConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
//            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//            wifiConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);



    }
    public String getCookie (){
        SharedPreferences mSharedPrefernces = getSharedPreferences("cookie",MODE_PRIVATE);
        String EmailEseeion = mSharedPrefernces.getString("cookie", "");
        return EmailEseeion;
    }
    public String getSessionemil (){
        SharedPreferences mSharedPrefernces = getSharedPreferences("Email",MODE_PRIVATE);
        String EmailEseeion = mSharedPrefernces.getString("email", "");
        return EmailEseeion;

    }
    public void storeHotspotPasswordAndHotspoSSID(String HotspotSSID, String Hotspotpassword){
        SharedPreferences mSharedPrefernces = getSharedPreferences("storeHotspotPasswordAndHotspoSSID",MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPrefernces.edit();
        mEditor.putString("HotspotPassword", Hotspotpassword);
        mEditor.putString("HotspotSSID", HotspotSSID);
        mEditor.apply();
    }
    public String getLoginUID (){
        SharedPreferences mSharedPrefernces = getSharedPreferences("storeLoginUID",MODE_PRIVATE);
        String EmailEseeion = mSharedPrefernces.getString("storeLoginUID", "");
        return EmailEseeion;

    }
    public boolean jadegeAndroidSdkVersionIsBiggerThan5Point1 (){

        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
            System.out.println("比5.1小或等於" + Build.VERSION.SDK_INT+"  "+ Build.VERSION_CODES.LOLLIPOP_MR1);

                return false;
        }
        else{System.out.println("比5.1大 " + Build.VERSION.SDK_INT);

            return true;
        }
    }
    public void setWiFiApMode(boolean mode) {
        if (context == null) return;
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) return;
        try {
            //Method setWifiApEnabled = WifiManager.class.getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            Method setWifiApEnabled = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            setWifiApEnabled.invoke(wifiManager, null, mode);
        } catch (Exception e) {
        }
    }

    public String getlastAPName (){
        SharedPreferences mSharedPrefernces = getSharedPreferences("lastAP",MODE_PRIVATE);
        String EmailEseeion = mSharedPrefernces.getString("ApName", "");
        return EmailEseeion;

    }

    public String getlastAPPassword (){
        SharedPreferences mSharedPrefernces = getSharedPreferences("lastAP",MODE_PRIVATE);
        String EmailEseeion = mSharedPrefernces.getString("password", "");
        return EmailEseeion;

    }


}


