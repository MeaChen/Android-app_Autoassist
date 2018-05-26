package com.example.think.auto_assist.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.think.auto_assist.R;
import com.example.think.auto_assist.serivices.MusicService;
import com.example.think.auto_assist.serivices.NotifyService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;


    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        Button regis=(Button)findViewById(R.id.register);
        regis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2=new Intent(MainActivity.this,RegisterActivity.class);
                startActivityForResult(intent2, 0);

            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == RESULT_OK) {
            Toast t=Toast.makeText(MainActivity.this, "请登录",Toast.LENGTH_LONG);
            t.show();
        }
        else if(requestCode==0 && resultCode==RESULT_CANCELED){

        }
    }

    private void attemptLogin(){
        //Intent intent = new Intent(this, HomeActivity.class);
        //this.startActivity(intent);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        if(email.equals("") || password.equals("") ){
            Toast.makeText(this,"用户名或密码不能为空",Toast.LENGTH_SHORT).show();
            return;
        }
        try{
            Socket s=new Socket(getString(R.string.ipaddress),4800);
            PrintWriter send=new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
            send.println("login,"+email+","+password);
            System.out.println(password);
            String line=br.readLine();
            String[] re=line.split(",");
            if(re[0].equals("ok")) {
                //实例化SharedPreferences对象（第一步）
                SharedPreferences sp= getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
                //实例化SharedPreferences.Editor对象（第二步）
                SharedPreferences.Editor editor = sp.edit();
                //System.out.println(re[1]);
                //用putString的方法保存数据
                editor.putString("id", re[1]);
                editor.putString("name",re[2]);
                editor.putString("music_setting",re[3]);
                editor.putString("error_setting",re[4]);
                editor.apply();  //提交当前数据

                Intent intent = new Intent(this, HomeActivity.class);
                this.startActivity(intent);
                if(re[4].equals("1")){
                    Intent ss=new Intent(this,NotifyService.class);
                    startService(ss);
                }
                if(re[3].equals("1")){
                    //System.out.println("hhh");
                    Intent music=new Intent(this, MusicService.class);
                    startService(music);
                }
            }else{
                Toast toast=Toast.makeText(MainActivity.this, "登录失败",Toast.LENGTH_SHORT);
                toast.show();
            }
            br.close();
            send.close();
            s.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
