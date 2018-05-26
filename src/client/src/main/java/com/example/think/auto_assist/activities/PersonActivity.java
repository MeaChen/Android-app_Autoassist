package com.example.think.auto_assist.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.SearchViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.think.auto_assist.R;

public class PersonActivity extends AppCompatActivity {

    //private SearchView sv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        //sv=(SearchView)findViewById(R.id.sv);
        //sv.setIconifiedByDefault(false);
        //sv.setSubmitButtonEnabled(true);
        //sv.setQueryHint("search");
        SharedPreferences sp= getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
        String user_name = sp.getString("name","");
        TextView tv = (TextView)findViewById(R.id.nickname);
        tv.setText(user_name);

        Button b = (Button) findViewById(R.id.back);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });

        View car = findViewById(R.id.car);
        assert car != null;
        car.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                jumpCar();
            }
        });
        View o = findViewById(R.id.order);
        assert o != null;
        o.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                jumpOrder();
            }
        });
        View p = findViewById(R.id.personal);
        assert p != null;
        p.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                jumpPersonal();
            }
        });

        View i = findViewById(R.id.illegal);
        assert i != null;
        i.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                jumpIllegal();
            }
        });

        View m=findViewById(R.id.music);
        m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent kk=new Intent(PersonActivity.this,MusicActivity.class);
                startActivity(kk);
            }
        });
    }
    private void jumpCar(){
        Intent intent = new Intent(this, CarActivity.class);
        this.startActivity(intent);
    }
    private void jumpOrder(){
        Intent intent = new Intent(this, OrderActivity.class);
        this.startActivity(intent);
    }
    private void jumpIllegal(){
        Intent intent = new Intent(this, IllegalActivity.class);
        this.startActivity(intent);
    }
    private void jumpPersonal(){
        Intent intent = new Intent(this, PersonalActivity.class);
        this.startActivity(intent);
    }
    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {

        super.onStop();
    }

    private void goBack(){
        Intent intent = new Intent(this, HomeActivity.class);
        this.startActivity(intent);
    }
}
