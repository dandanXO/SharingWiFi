package com.example.dan.wifi_login.sharing;


import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DAN on 2017/3/12.
 */

public class OpenApRequest extends StringRequest {
    String cookie="";
    Sharing context;


    String volleyErrors="";
    private OpenApRespListener<String> listener;

    private static final String REGISTER_REQUEST_URL="http://163.18.22.94/api/online";
    private Map<String,String>params;
    Map<String, String> headers;


    public OpenApRequest(String email, String LoginUID, Double Latitude, Double Longitude, String cookie, Response.Listener<String>listener, Sharing context){


        super(Method.POST, REGISTER_REQUEST_URL,listener, null);
        this.context = context;
        params = new HashMap<>();
        this.cookie = cookie;
        params.put("UID",LoginUID);
        params.put("lat",Latitude.toString());
        params.put("lng",Longitude.toString());
        params.put("email",email);


     ;

        System.out.print("LOGINLOGINLOGINLOGINLOGINLOGINLOGINLOGINLOGIN");

    }


    @Override
    public Map<String, String> getParams() {

        return params;
    }



    @Override
    protected VolleyError parseNetworkError(VolleyError volleyError){
        if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
            VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
            volleyError = error;

        }

        return volleyError;
    }
    public Map<String, String> getHeaders() throws AuthFailureError {


        Map<String, String> _headers = new HashMap<>();
        _headers.put("cookie", cookie);

        return _headers;
    }



}
