package com.example.dan.wifi_login;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setTitle("帳號申請");
        RegisterActivit.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        final EditText etfullName = (EditText) findViewById(R.id.etPhoneNumber);
        final EditText etEmail = (EditText) findViewById(R.id.etEmail);
        final EditText etpassword = (EditText)findViewById(R.id.etpassword);
        final Button bRegist=(Button) findViewById(R.id.bRegister);
        final EditText etpassword2 = (EditText) findViewById(R.id.etpassword2);


        bRegist.setOnClickListener(new View.OnClickListener(){
            @Override

            public void onClick(View v){
                final String fullName = etfullName.getText().toString();
                final String email = etEmail.getText().toString();
                final String password = etpassword.getText().toString();
                final String password2 = etpassword2.getText().toString();
                if(checkpasswordAndAll(fullName,email,password,password2)){
               Response.Listener<String> responseLinsener = new Response.Listener<String>(){
                   @Override
                   public void onResponse(String response) {
                       try {
                           JSONObject jsonresonse = new JSONObject(response);
                           int state= jsonresonse.getInt("state");
                           String message = jsonresonse.getString("description");
                           if(state==200){
                               Intent intent = new Intent(RegisterActivit.this, LoginActivity.class);
                               RegisterActivit.this.startActivity(intent);
                               AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivit.this);
                               builder.setMessage("Signup Success\n"+message)
                                       .setNegativeButton("OK",null)
                                       .create()
                                       .show();
                           }else {
                               AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivit.this);
                               builder.setMessage("Register failed\n錯誤訊息:" +
                                       ""+message)
                                       .setNegativeButton("Retray",null)
                                       .create()
                                       .show();
                           }
                       } catch (JSONException e) {
                           e.printStackTrace();
                         etEmail.setText(e.getMessage());
                       }
                   }
               };


                RegisterRequest registerRequest = new RegisterRequest(fullName,email,password,responseLinsener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivit.this);
                queue.add(registerRequest);

            }
            else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivit.this);
                    builder.setMessage("輸入錯誤或是第二組密碼不正確")
                            .setNegativeButton("OK",null)
                            .create()
                            .show();
                }
        }
        }
        );

    }

    public boolean checkpasswordAndAll (String Fullname,String email,String password,String password2){

        if((password.equals( password2)) && !Fullname.equals("") && !email.equals("") && !password.equals("")){
            return true;
        }else {return false;}

    }
}
