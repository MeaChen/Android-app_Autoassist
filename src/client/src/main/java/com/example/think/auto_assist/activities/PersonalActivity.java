package com.example.think.auto_assist.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.think.auto_assist.R;
import com.example.think.auto_assist.serivices.NotifyService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class PersonalActivity extends AppCompatActivity{

    ToggleButton toggle1,toggle2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal);

        toggle1 = (ToggleButton) findViewById(R.id.musicLaunch);
        toggle2 = (ToggleButton) findViewById(R.id.errorTip);

        init();

        CompoundButton.OnCheckedChangeListener listener1 = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    toggle1.setChecked(true);
                    changeMusic(1);
                }else{
                    toggle1.setChecked(false);
                    changeMusic(0);
                }
            }
        };
        CompoundButton.OnCheckedChangeListener listener2 = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    toggle2.setChecked(true);
                    changeError(1);
                }else{
                    toggle2.setChecked(false);
                    changeError(0);
                }
            }
        };

        toggle1.setOnCheckedChangeListener(listener1);
        toggle2.setOnCheckedChangeListener(listener2);

        Button r = (Button)findViewById(R.id.returnTo);
        r.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent t = new Intent(PersonalActivity.this, PersonActivity.class);
                startActivity(t);
            }
        });
    }

    private void init()
    {
        SharedPreferences sp= getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
        String music = sp.getString("music_setting","");
        String error = sp.getString("error_setting","");
        if(music.equals("1"))
            toggle1.setChecked(true);
        else
            toggle1.setChecked(false);
        if(error.equals("1"))
            toggle2.setChecked(true);
        else
            toggle2.setChecked(false);
    }

    private void changeMusic(final int flag)
    {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                //System.out.println("thread");
                try{
                    Socket s=new Socket(getString(R.string.ipaddress),4800);
                    PrintWriter send=new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
                    SharedPreferences sp= getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
                    String userid =sp.getString("id", "");
                    send.println("onMusic"+","+userid+","+flag);
                    String line = br.readLine();
                    br.close();
                    send.close();
                    s.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        t.start();
    }

    private void changeError(final int flag)
    {
        if(flag==1){
            Intent tt=new Intent(this,NotifyService.class);
            startService(tt);
        }
        else if(flag==0)
        {
            Intent tt = new Intent(this,NotifyService.class);
            stopService(tt);
        }
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        Socket s=new Socket(getString(R.string.ipaddress),4800);
                        //System.out.println("threadjinlaile");
                        PrintWriter send=new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
                        BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
                        SharedPreferences sp= getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
                        String userid =sp.getString("id", "");
                        send.println("onError"+","+userid+","+flag);
                        String line = br.readLine();
                        br.close();
                        send.close();
                        s.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }

                }
            });
            t.start();

    }
}
