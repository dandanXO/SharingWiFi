package com.example.dan.wifi_login.sharing;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DAN on 2017/4/8.
 */

public class OpenApRespListener<T> implements com.android.volley.Response.Listener<T>{
    String cookie = "";
    int httpcode=0;
    String Servermaintenance="";
    String AllDATA="";
    Sharing context;
    Sharing sharing = new Sharing();
    public OpenApRespListener(Sharing context) {
        this.context = context;
    }

    @Override
    public void onResponse(T response) {
System.out.print(Servermaintenance);

        try {

            JSONObject jsonResponse = new JSONObject((String)response);
            boolean success = jsonResponse.getBoolean("hasData");
            System.out.println(response);

            if(success){

//                String email = jsonResponse.getString("email");
//                String SSID = jsonResponse.getString("SSID");
//                String password = jsonResponse.getString("password");
//                sharing.password=password;
//                sharing.SSID=SSID;
            System.out.print(jsonResponse.getJSONObject("wifi"));

                AllDATA=jsonResponse.getJSONObject("wifi").toString()+"??";
            }else {
                sharing.jadgewifi=false;
                setDefaults("email","",context);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("取得資料失敗")
                        .setNegativeButton("Retray",null)
                        .create()
                        .show();
                AllDATA=jsonResponse.getJSONObject("wifi").toString()+"??";
            }


        } catch (JSONException e) {
            e.printStackTrace();
            System.out.print("失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗失敗");
        }

    }




    public void setCookie(String cookie) {

        this.cookie = cookie;
    }
    public void sethttpcode(int httpcode) {

        this.httpcode = httpcode;
    }
    public void setServermaintenance(String Servermaintenance) {

        this.Servermaintenance = Servermaintenance;


    }
    public static void setDefaults(String key, String value, Context context) {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, value);
        editor.apply();
    }
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

}
