package com.example.think.auto_assist.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.think.auto_assist.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IllegalActivity extends AppCompatActivity {

    List<Map<String, Object>> list;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_illegal);
        //String carinfo="";
        //String result=wz.getInfo(carinfo);
        Button baba=(Button)findViewById(R.id.getback);
        baba.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(IllegalActivity.this, PersonActivity.class);
                startActivity(i);
            }
        });
        getData();
        ListView lv=(ListView)findViewById(R.id.illlv);
        lv.setAdapter(new illegalAdapter(this,list));

    }
    private void getData(){
        list=new ArrayList<>();
        String line="";
        try{
            Socket s=new Socket(getString(R.string.ipaddress),4800);
            PrintWriter send1=new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
            BufferedReader br1 = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
            SharedPreferences sharedPreferences= getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
            String userid =sharedPreferences.getString("id", "");
            send1.println("getinfos,"+userid);

            line= br1.readLine();
            System.out.println(line);

        }catch(Exception e){
            e.printStackTrace();
        }
        String[] ans=line.split(",");
        int number=Integer.valueOf(ans[ans.length-1]);
        for(int i=0;i<number;++i){
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("name",ans[10*i]);
            list.add(map);
            /*String a=ans[10*i]+ans[10*i+3];
            SharedPreferences sp= getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("car"+i, a);
            editor.apply();*/
        }
    }
    private String convert(String brand){
        if(brand=="aodi")
            return "奥迪";
        else if(brand=="baoma")
            return "宝马";
        else if(brand=="baoshijie")
            return "保时捷";
        else if(brand=="falali")
            return "法拉利";
        else if(brand=="fute")
            return "福特";
        else if(brand=="dazhong")
            return "大众";
        else if(brand=="fengtian")
            return "丰田";
        else if(brand=="benchi")
            return "奔驰";
        else
            return "";
    }
}

class illegalAdapter extends BaseAdapter{
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public illegalAdapter(Context context,List<Map<String, Object>> data){
        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public final class Detail{
        public TextView car;
        public Button chaxun;
        //public TextView info;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    //获得某一位置的数据
    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Detail dd;
        if(convertView==null){
            dd=new Detail();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.illegallist, null);

            dd.car=(TextView)convertView.findViewById(R.id.car);
            dd.chaxun=(Button)convertView.findViewById(R.id.chaxun);

            //zujian.info=(TextView)convertView.findViewById(R.id.info);
            convertView.setTag(dd);
        }else{
            dd=(Detail)convertView.getTag();
        }

        dd.car.setText((String)data.get(position).get("name"));
        dd.car.setTextSize(16);
        //zujian.info.setText((String)data.get(position).get("info"));
        //Button b=dd.play;
        dd.chaxun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent x=new Intent(context,ResultActivity.class);
                //x.putExtra("info",result);
                x.putExtra("car",(String)data.get(position).get("name"));
                context.startActivity(x);
            }
        });
        return convertView;
    }
}
