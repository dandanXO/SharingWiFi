package com.example.dan.wifi_login.sharing;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DAN on 2017/3/11.
 */

public class KeepTalkingRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL="http://163.18.22.94/api/online";
    private Map<String,String>params;
String cookie ;
    public KeepTalkingRequest( Double Latitude, Double Longitude, String cookie , Response.Listener<String>listener){
        super(Method.PATCH, REGISTER_REQUEST_URL,listener, null);
        params = new HashMap<>();
        this.cookie=cookie;
        params.put("lat",Latitude.toString());
        params.put("lng",Longitude.toString());
       System.out.print("KEEPTALKINGNOTEFICATIOMSSERVICE");

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
