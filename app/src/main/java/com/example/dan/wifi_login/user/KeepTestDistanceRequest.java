package com.example.dan.wifi_login.user;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DAN on 2017/3/11.
 */

public class KeepTestDistanceRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL="http://163.18.22.94/api/getwifi";
    private Map<String,String>params;
String cookie ;
    public KeepTestDistanceRequest(String UID, Double Latitude,Double Longitude,  String cookie ,Response.Listener<String>listener){
        super(Method.POST, REGISTER_REQUEST_URL,listener, null);
        params = new HashMap<>();
        this.cookie=cookie;
        params.put("UID",UID);
        params.put("lat",Latitude.toString());
        params.put("lng",Longitude.toString());
       System.out.print("UserAreaActivityRegistUserAreaActivityRegistUserAreaActivityRegistUserAreaActivityRegistUserAreaActivityRegist");

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {


        Map<String, String> _headers = new HashMap<>();
        _headers.put("cookie", cookie);

        return _headers;
    }
}
