package com.example.dan.wifi_login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by DAN on 2017/4/8.
 */

public class LoginRespListener<T> implements com.android.volley.Response.Listener<T>{
    String cookie = "";
    int httpcode=0;
    String Servermaintenance="";

    LoginActivity context;

    public LoginRespListener (LoginActivity context) {
        this.context = context;
    }

    @Override
    public void onResponse(T response) {
System.out.print(Servermaintenance);
        try {

            JSONObject jsonResponse = new JSONObject((String)response);
            boolean success = jsonResponse.getBoolean("isLogin");
            System.out.println(response);
            if(success){

                // String phonenumber = jsonResponse.getString("phonenumber");
                Intent intent = new Intent(context, UserAreaActivity.class);
                //  intent.putExtra("phonenumber" , phonenumber);
               // System.out.print( jsonResponse.getString("uid")+"UIDUID:\n\n");
                JSONObject user = jsonResponse.getJSONObject("user");
                intent.putExtra("fullname",user.getString("fullname"));
                intent.putExtra("email" , user.getString("email"));
                intent.putExtra("cookie", cookie);
                intent.putExtra("LoginUID",user.getString("UID"));
                System.out.println("cookie:\t" + cookie);
                context.startActivity(intent);
            }else {
                setDefaults("email","",context);
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("帳號或密碼錯誤")
                        .setNegativeButton("Retray",null)
                        .create()
                        .show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.print("eeeeeeeeeeeeeeeeeesssssssssssssssssssssssasdasdasdasd555666asdasdasdasdasdasdasdasd");
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





}
