package com.example.dan.wifi_login.user;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.example.dan.wifi_login.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DAN on 2017/5/4.
 */

public class NoteficationsService extends Service {

//GPS設定
    private static final String TAG = "BOOMBOOMTESTGPS";
    private LocationManager mLocationManager = null;
    private static final int LOCATION_INTERVAL = 1000;
    private static final float LOCATION_DISTANCE = 10f;
    Location mLastLocation;
    double Latitude ;//東西經度
    double Longitude ;
    //GPS設定結束
    String UID ="";
    String cookie="";

    final static String ACTION = "NotifyServiceAction";
    final static String STOP_SERVICE = "";
    final static int RQS_STOP_SERVICE = 1;
    NotifyServiceReceiver notifyServiceReceiver;

    private static final int MY_NOTIFICATION_ID=9;
    private NotificationManager notificationManager;
    private Notification myNotification;
    String distnase="";
    String SSID="";

    int i = 0;
     Handler handler=new Handler();
    Runnable runnable=new Runnable(){
        @Override
        public void run() {
            // TODO Auto-generated method stub
            //System.out.println("一直執行RR一直執行RRR  handler.postDelayed(this, 2000");
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION);
            registerReceiver(notifyServiceReceiver, intentFilter);
// Send Notification
            notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            Context context = getApplicationContext();
            final Notification.Builder builder = new Notification.Builder(context);

            myNotification =  builder.setSmallIcon(R.drawable.wifisharing).setContentTitle("WIFI/0運行狀態").setContentText("距離"+SSID+":"+distnase+"M").build();

            //持續監控手機距離
            Response.Listener<String> responseLinsener = new Response.Listener<String>(){
                @Override

                public void onResponse(String response) {
                    System.out.println(response+"\n\n");
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("hasData");

                        if(success){
                            JSONArray wifi = jsonResponse.getJSONArray("wifi");
                            String WifiDistance[]=new String[wifi.length()];
                            String WifiSSID[]=new String[wifi.length()];
                            JSONObject jsonObject = wifi.getJSONObject(0);
                            WifiDistance[0]= jsonObject.getString("distance");
                            WifiSSID[0]= jsonObject.getString("ssid");
                            distnase = WifiDistance[0].toString();
                            SSID = WifiSSID[0].toString();
                            System.out.println(WifiDistance[0]);
                            System.out.println("有連上 \nDIS:    "+distnase+"\nLAT\n"+Latitude);
                        }else {
                            System.out.println("QQQQQQQQQQQQQQQQQQQQQQQQQQQ玫連上 ");
                    }

                    } catch (JSONException e) {
                        e.printStackTrace();


                    }
                }
            };
            KeepTestDistanceRequest KeepTestDistanceRequest = new KeepTestDistanceRequest(UID,Latitude, Longitude,cookie,responseLinsener);
            RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
            queue.add(KeepTestDistanceRequest);

            //持續監控手機距離結束

            myNotification.defaults = 0;
            myNotification.flags |= Notification.FLAG_ONGOING_EVENT;
            notificationManager.notify(MY_NOTIFICATION_ID, myNotification);
i++;
            handler.postDelayed(this, 5000);
        }
    };

    public NoteficationsService() throws JSONException {
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate()
    {
        // TODO Auto-generated method stub
        notifyServiceReceiver = new NotifyServiceReceiver();

        super.onCreate();
        Log.e(TAG, "onCreate");
        initializeLocationManager();
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, LOCATION_INTERVAL, LOCATION_DISTANCE,
                    mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            cookie = getCookie();
            UID =getUID();

        handler.postDelayed(runnable,5000); // 開始Timer
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public boolean onUnbind(Intent intent)
    {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy()
    {
// TODO Auto-generated method stub
        handler.removeCallbacks(runnable);

        notificationManager.cancel(MY_NOTIFICATION_ID);
        this.unregisterReceiver(notifyServiceReceiver);
        super.onDestroy();

    }
    public class NotifyServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            int rqs = arg1.getIntExtra("RQS", 0);
            if (rqs == RQS_STOP_SERVICE){
                stopSelf();
            }
        }
    }

    private class LocationListener implements android.location.LocationListener
    {


        public LocationListener(String provider)
        {
            Log.e(TAG, "LocationListener " + provider);
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location)
        {
            Log.e(TAG, "onLocationChanged: " + location);
            mLastLocation.set(location);
            Latitude = location.getLatitude();
            Longitude =location.getLongitude();
        }

        @Override
        public void onProviderDisabled(String provider)
        {
            Log.e(TAG, "onProviderDisabled: " + provider);
        }

        @Override
        public void onProviderEnabled(String provider)
        {
            Log.e(TAG, "onProviderEnabled: " + provider);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            Log.e(TAG, "onStatusChanged: " + provider);
        }
    }

    LocationListener[] mLocationListeners = new LocationListener[] {
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };
    private void initializeLocationManager() {
        Log.e(TAG, "initializeLocationManager");
        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
    }
    public String getCookie (){
        SharedPreferences mSharedPrefernces = getSharedPreferences("cookie",MODE_PRIVATE);
        String EmailEseeion = mSharedPrefernces.getString("cookie", "");
        return EmailEseeion;

    }
    public String getUID (){
        SharedPreferences mSharedPrefernces = getSharedPreferences("UID",MODE_PRIVATE);
        String EmailEseeion = mSharedPrefernces.getString("UID", "");
        return EmailEseeion;

    }
}
