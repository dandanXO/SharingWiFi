package com.example.dan.wifi_login;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_LOCATION = 1 ;
    LoginActivity context = this;
    String tempCookie = "";
    ProgressDialog dialog;
    String LoginUID ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Wifi 除以 0");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQUEST_LOCATION);

        }else{

        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etpassword = (EditText)findViewById(R.id.etpassword);
        final Button blogin=(Button) findViewById(R.id.blogin);
        final TextView registerLink = (TextView)findViewById(R.id.tvRegisterHere);
        context = this;
        if(!ifSession().equals(""))    {

            LoginRespListener resL = new LoginRespListener(this);
            LoginRequest loginrequest = new LoginRequest(getSessionemil(),getSessionpassoword(),resL,this);
            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
            queue.add(loginrequest);


        }
        Target viewTarget = new ViewTarget(R.id.tvRegisterHere, this);
        new ShowcaseView.Builder(this)
                .setTarget(viewTarget)
                .setContentTitle("HI!!")
                .setStyle(R.style.CustomShowcaseTheme2)
                .setContentText("如果沒有帳號可以先註冊唷")
                .singleShot(42)
                .build();


        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivit.class);
                LoginActivity.this.startActivity(registerIntent);
            }
        });

        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final WifiManager wifiManager = (WifiManager)getApplicationContext(). getSystemService(Context.WIFI_SERVICE);

                WifiConfiguration netConfig = new WifiConfiguration();
                    try {
                        Method getWifiApConfigurationMethod = wifiManager.getClass().getMethod("getWifiApConfiguration");
                        netConfig = (WifiConfiguration) getWifiApConfigurationMethod.invoke(wifiManager);
                        System.out.println("CLIENT\nSSID:" + netConfig.SSID + "\nPassword:" + netConfig.preSharedKey + "\n");
                    } catch (Exception e) {
                        System.out.println(this.getClass().toString());
                    }
                    System.out.print("CLIENT\nSSID:" + netConfig.SSID + "\nPassword:" + netConfig.preSharedKey + "\n");
                    storelastAP(netConfig.SSID, netConfig.preSharedKey);

                dialog = ProgressDialog.show(LoginActivity.this, "登入中", "請稍後...", true);
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try{
                            Thread.sleep(4500);
                            final String email = etEmail.getText().toString();
                            final String password = etpassword.getText().toString();
                            storeSession(email,password);
                            LoginRespListener resL = new LoginRespListener(context);
                            LoginRequest loginrequest = new LoginRequest(email,password,resL,context);
                            RequestQueue queue = Volley.newRequestQueue(LoginActivity.this);
                            queue.add(loginrequest);
                            dialog.dismiss();

                        }
                        catch(Exception e){
                            e.printStackTrace();
                        }
                        finally{

                        }
                    }
                }).start();



            }
        });
    }



    public void storeSession(String email ,String password){
        SharedPreferences mSharedPrefernces = getSharedPreferences("Email",MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPrefernces.edit();
        mEditor.putString("email", email);
       // mEditor.putString("phonenumber", phonenumber);
        mEditor.putString("password", password);
        mEditor.apply();
    }

    public String ifSession (){
        SharedPreferences mSharedPrefernces = getSharedPreferences("Email",MODE_PRIVATE);
        String EmailEseeion = mSharedPrefernces.getString("email", "");
        System.out.println(EmailEseeion+"asdasdasdNosessionNosessionNosessionNosessionNosessionNosessionNosessionNosessionNosessionNosessionNosessionNosessionNosession");
        return EmailEseeion;

    }
    public String getSessionemil (){
        SharedPreferences mSharedPrefernces = getSharedPreferences("Email",MODE_PRIVATE);
        String EmailEseeion = mSharedPrefernces.getString("email", "");
        return EmailEseeion;

    }
    public String getSessionpassoword (){
        SharedPreferences mSharedPrefernces = getSharedPreferences("Email",MODE_PRIVATE);
        String EmailEseeion = mSharedPrefernces.getString("password", "");
        return EmailEseeion;

}
    public void storelastAP(String ApName ,String password){
        SharedPreferences mSharedPrefernces = getSharedPreferences("lastAP",MODE_PRIVATE);
        SharedPreferences.Editor mEditor = mSharedPrefernces.edit();
        mEditor.putString("ApName", ApName);
        mEditor.putString("password", password);
        mEditor.apply();
    }

    public static String md5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

}
