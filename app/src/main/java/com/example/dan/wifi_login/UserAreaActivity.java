package com.example.dan.wifi_login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.dan.wifi_login.sharing.Sharing;
import com.example.dan.wifi_login.user.use;
import com.example.dan.wifi_login.user.useWifiRequest;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserAreaActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int REQUEST_LOCATION = 1;
    String emailAll;
    double Latitude ;//東西經度
    double Longitude ;
    long Time ; //時間
    double Bearing ;//方位
    ProgressDialog dialog;//LOADING畫面
    int counter = 0;
    private ShowcaseView showcaseView;

    public final String LM_GPS = LocationManager.GPS_PROVIDER;
    public final String LM_NETWORK = LocationManager.NETWORK_PROVIDER;
    private LocationManager mLocationManager;
    // 定位監聽器
    private LocationListener mLocationListener;

    private Context context;
    private TextView textView1;
    public Button buser;
    public Button bsharing;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_area);
        this.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        final TextView etemail = (TextView) findViewById(R.id.etemail);
        buser = (Button) findViewById(R.id.buser);
        buser.setEnabled(false);
        buser.setText("USER\nGPS搜尋中...");
        bsharing = (Button) findViewById(R.id.bsharing);
        bsharing.setEnabled(false);
        bsharing.setText("SHARING\nGPS搜尋中...");
        checkIfGPShasdata();

        //----------------------------------------------------***取得GPS***---------------------------------------------------
        context = this;
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        mLocationListener = new MyLocationListener();
        textView1 = (TextView) findViewById(R.id.TesttextView);
        openGPS(context);

        //----------------------------------------------------***取得GPS結束***--------------------------------------------------

        Intent intent = getIntent();
        String email = getSessionemil();//mail
        final String cookie = intent.getStringExtra("cookie");
        final String LoginUID = intent.getStringExtra("LoginUID");
        storeLoginUID(LoginUID);
        storecookie(cookie);


        showcaseView =  new ShowcaseView.Builder(this,true)
                .setTarget(Target.NONE)

                .setContentTitle("請按NEXT開始!!")
                .setOnClickListener(this)
                .setStyle(R.style.CustomShowcaseTheme2)
                .singleShot(75)
                .build();
        showcaseView.setButtonText("NEXT");

        //phonenumberAll =phonenumber;
        emailAll= email;


        etemail.setText("HI!  "+email);
bsharing.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(UserAreaActivity.this, Sharing.class);
        WifiManager wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(false);
        UserAreaActivity.this.startActivity(intent);
        Toast.makeText(UserAreaActivity.this,"Usharingssssd",Toast.LENGTH_LONG);
    }
});
        buser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Response.Listener<String> reponseListener = new  Response.Listener<String>(){
                            @Override
                            public void onResponse(String response) {
                                try {
                            JSONObject jsonResponse = new JSONObject(response);
                            boolean success = jsonResponse.getBoolean("hasData");
                            if(success){


                                JSONArray wifi = jsonResponse.getJSONArray("wifi");
                                Intent intent = new Intent(UserAreaActivity.this, use.class);
                                intent.putExtra("email" , emailAll);
                                intent.putExtra("Latitude" , Latitude);
                                intent.putExtra("Longitude" , Longitude);
                                intent.putExtra("Time" , Time);
                                intent.putExtra("Bearing" , Bearing);
                                intent.putExtra("cookie" , cookie);
                                intent.putExtra("wifi" , wifi.toString());

                                UserAreaActivity.this.startActivity(intent);

                            }else {
                                AlertDialog.Builder builder = new AlertDialog.Builder(UserAreaActivity.this);
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
                useWifiRequest loginrequest = new useWifiRequest(emailAll,Latitude,Longitude,Time,Bearing, cookie,reponseListener);
                RequestQueue queue = Volley.newRequestQueue(UserAreaActivity.this);
                queue.add(loginrequest);



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
            mLocationListener = new MyLocationListener();
        }
        // 獲得地理位置的更新資料 (GPS 與 NETWORK都註冊)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);

        }
        mLocationManager.requestLocationUpdates(LM_GPS, 20000, 5, mLocationListener);
        mLocationManager.requestLocationUpdates(LM_NETWORK, 20000, 5, mLocationListener);
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
    @Override
    public void onClick(View view) {
       // openGPS(context);
        switch (counter) {
            case 0:

                showcaseView.setShowcase(new ViewTarget(bsharing), true);
                showcaseView.setContentTitle("分享者");
                showcaseView.setContentText("提供網路給需求者 以共享經濟概念方式分享 絕對保密");
                showcaseView.setButtonText("NEXT");


                break;
            case 1:
                showcaseView.setShowcase(new ViewTarget(buser), true);
                showcaseView.setContentTitle("需求者/使用者");
                showcaseView.setContentText("存取他人網路來上 同時也保護雙方各資安全");
                showcaseView.setButtonText("CLOSE");
                break;
            case 2:
                showcaseView.hide();

                break;

        }
        counter++;

    }
    // 定位監聽器實作
    private class MyLocationListener implements LocationListener {
        // GPS位置資訊已更新
        public void onLocationChanged(Location location) {

            textView1.setText(
                    "緯度-Latitude：" + location.getLatitude() + "\n" +
                    "經度-Longitude：" + location.getLongitude() + "\n"
                  //  "精確度-Accuracy：" + location.getAccuracy() + "\n" +
                   // "標高-Altitude：" + location.getAltitude() + "\n" +
                   // "時間-Time：" + new Date(location.getTime()) + "\n" +
                  //  "速度-Speed：" + location.getSpeed() + "\n" +
                   // "方位-Bearing："+ location.getBearing()
                            );


            Latitude = location.getLatitude();
            Longitude =location.getLongitude();
              Time = location.getTime();
                 Bearing=location.getBearing() ;
            if(location.getLongitude() != 0){
                buser.setEnabled(true);
                buser.setText("USER");
                bsharing.setEnabled(true);
                bsharing.setText("SHARING");
            }
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
    @Override//阻止上一頁按鈕
    public void onBackPressed() {

    }
    public void  checkIfGPShasdata(){
        dialog = ProgressDialog.show(UserAreaActivity.this, "GPS搜尋中", "請稍後...", true);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Logout:
                Toast.makeText(this, " Logout", Toast.LENGTH_SHORT)
                        .show();
                SharedPreferences mSharedPrefernces = getSharedPreferences("Email",MODE_PRIVATE);
                SharedPreferences.Editor mEditor = mSharedPrefernces.edit();

                mEditor.remove("email");
                mEditor.commit();
                finish();
                break;


            default:
                break;
        }

        return true;
    }
    public void storecookie(String cookie){
        SharedPreferences mSharedPrefernces = getSharedPreferences("cookie",MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPrefernces.edit();
        mEditor.putString("cookie", cookie);
        mEditor.apply();
    }
    public String getSessionemil (){
        SharedPreferences mSharedPrefernces = getSharedPreferences("Email",MODE_PRIVATE);
        String EmailEseeion = mSharedPrefernces.getString("email", "");
        return EmailEseeion;

    }
    public void storeLoginUID(String LoginUID){
        SharedPreferences mSharedPrefernces = getSharedPreferences("storeLoginUID",MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPrefernces.edit();
        mEditor.putString("storeLoginUID", LoginUID);
        mEditor.apply();
    }


}

