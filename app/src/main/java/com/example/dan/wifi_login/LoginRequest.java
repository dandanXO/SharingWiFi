package com.example.dan.wifi_login;


import android.os.Looper;
import android.support.v7.app.AlertDialog;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DAN on 2017/3/12.
 */

public class LoginRequest extends StringRequest {
    String cookie="";
    LoginActivity context;


    String volleyErrors="";
    private LoginRespListener<String> listener;
    private ErrorListener mErrorListener;

    private static final String REGISTER_REQUEST_URL="http://163.18.22.94/api/user/login";
    private Map<String,String>params;
    Map<String, String> headers;


    public LoginRequest( String email, String password, LoginRespListener<String>listener,LoginActivity context){


        super(Method.POST, REGISTER_REQUEST_URL,listener, null);
        this.context = context;
        params = new HashMap<>();
        params.put("email",email);
        params.put("password",password);
        this.listener=listener;

        System.out.print("LOGINLOGINLOGINLOGINLOGINLOGINLOGINLOGINLOGIN");

    }


    public String getCookie() {return this.cookie;};

    @Override
    public Map<String, String> getParams() {

        return params;
    }
    @Override
    protected void deliverResponse(String response) {
       this.listener.onResponse(response);

    }
    @Override
    public Response<String> parseNetworkResponse(NetworkResponse response) {
        cookie = response.headers.get("set-cookie");
        this.listener.setCookie(cookie);

        int mStatusCode = response.statusCode;
        this.listener.sethttpcode(mStatusCode);

        return  super.parseNetworkResponse(response);

    }
    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError){
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;

        }
         volleyErrors=volleyError.toString();
        System.out.println(volleyErrors);
        this.listener.setServermaintenance(volleyErrors);
     if(volleyErrors!=""){
         Looper.prepare();
         AlertDialog.Builder builder = new AlertDialog.Builder(context);
         builder.setMessage("伺服器維修中")
                 .setNegativeButton("OK",null)
                 .create()
                 .show();
         Looper.loop();
     }
        return volleyError;
    }




public static class ResponseM {
        Map<String, String> headers;
        String response;
    }
}
