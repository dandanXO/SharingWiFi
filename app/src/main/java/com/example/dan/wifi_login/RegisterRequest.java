package com.example.dan.wifi_login;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by DAN on 2017/3/11.
 */

public class RegisterRequest extends StringRequest {
    private static final String REGISTER_REQUEST_URL="http://163.18.22.94/api/user";
    private Map<String,String>params;
    RegisterActivit context;
    String volleyErrors="";
    private LoginRespListener<String> listener;
    private Response.ErrorListener mErrorListener;
    public RegisterRequest(String fullname, String email, String password, Response.Listener<String>listener){
        super(Method.POST, REGISTER_REQUEST_URL,listener, null);
        params = new HashMap<>();
        params.put("fullname",fullname);
        params.put("email",email);
        params.put("password",password);
       System.out.print("UserAreaActivityRegistUserAreaActivityRegistUserAreaActivityRegistUserAreaActivityRegistUserAreaActivityRegist");

    }

    @Override
    public Map<String, String> getParams() {

        return params;
    }
}
