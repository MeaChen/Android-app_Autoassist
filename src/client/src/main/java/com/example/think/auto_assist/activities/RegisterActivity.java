package com.example.think.auto_assist.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.think.auto_assist.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.io.OutputStreamWriter;
public class RegisterActivity extends AppCompatActivity {

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button registering=(Button)findViewById(R.id.regi);
        registering.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText u=(EditText)findViewById(R.id.regi_u);
                String username=u.getText().toString();
                EditText p=(EditText)findViewById(R.id.regi_p);
                String pw=p.getText().toString();
                EditText ee=(EditText)findViewById(R.id.regi_e);
                String email=ee.getText().toString();
                if(!isEmailValid(email) || (!isPasswordValid(pw) || !isNameValid(username))){
                    Toast toast=Toast.makeText(RegisterActivity.this, "注册失败",Toast.LENGTH_SHORT);
                    toast.show();
                    setResult(RESULT_CANCELED);
                }
                try{

                    Socket s=new Socket(getString(R.string.ipaddress),4800);
                    PrintWriter send=new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
                    send.println("register,"+username+","+pw+","+email);

                    String line=br.readLine();

                    if(line.equals("ok,register successfully")) {
                        //Intent intent = getIntent();
                        setResult(RESULT_OK);

                    }else{
                        Toast toast=Toast.makeText(RegisterActivity.this, "注册失败",Toast.LENGTH_SHORT);
                        toast.show();
                        setResult(RESULT_CANCELED);
                    }
                    br.close();
                    send.close();
                    s.close();
                    finish();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    private boolean isNameValid(String name){
        return name.length() > 4;
    }
}
