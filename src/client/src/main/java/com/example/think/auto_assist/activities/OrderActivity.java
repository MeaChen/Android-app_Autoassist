package com.example.think.auto_assist.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.content.Context;
import android.view.Menu;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import com.example.think.auto_assist.R;
import com.karics.library.zxing.encode.CodeCreator;


public class OrderActivity extends AppCompatActivity {;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ListView lv=(ListView)findViewById(R.id.lv);
        //lv.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, strs));
        List<Map<String, Object>> list=getData();
        lv.setAdapter(new Ladapter(this, list));

        Button getback=(Button)findViewById(R.id.ret);
        getback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(OrderActivity.this, PersonActivity.class);
                startActivity(intent);
            }
        });
    }


    public List<Map<String, Object>> getData(){
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
        String line="";

        try{
            Socket s=new Socket(getString(R.string.ipaddress),4800);
            PrintWriter send1=new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
            BufferedReader br1 = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
            SharedPreferences sharedPreferences= getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
            String userid =sharedPreferences.getString("id", "");
            send1.println("getorders,"+userid);

            line= br1.readLine();
            System.out.println(line);


        }catch(Exception e){
            e.printStackTrace();
        }
        String[] ans=line.split(",");
        int number=Integer.valueOf(ans[0]);
        //urls=new String[number];
        for (int i = 0; i < number; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("title", ans[6*i+2]+","+ans[6*i+3]+","+ans[6*i+6]);
            map.put("info", ans[6*i+1]+","+ans[6*i+4]+","+ans[6*i+5]);
            String a="Carno:"+ans[6*i+6]+"\r\n"+"Name:"+ans[6*i+1]+"\r\n"+"Time:"+ans[6*i+2]+"\r\n"+"Type:"+ans[6*i+4]+"\r\n"+"Number:"+ans[6*i+5]+"\r\n"+"Station Name:"+ans[6*i+3]+"\r\n";
            list.add(map);
            SharedPreferences sp= getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString("order"+i, a);
            editor.apply();

        }
        return list;
    }
}

class Ladapter extends BaseAdapter{
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public Ladapter(Context context,List<Map<String, Object>> data){
        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public final class Zujian{
        public TextView title;
        public Button view;
        public TextView info;
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
        Zujian zujian=null;
        if(convertView==null){
            zujian=new Zujian();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.list, null);

            zujian.title=(TextView)convertView.findViewById(R.id.title);
            zujian.view=(Button)convertView.findViewById(R.id.view);

            zujian.info=(TextView)convertView.findViewById(R.id.info);
            convertView.setTag(zujian);
        }else{
            zujian=(Zujian)convertView.getTag();
        }
        //绑定数据

        zujian.title.setText((String)data.get(position).get("title"));
        zujian.title.setTextSize(16);
        zujian.info.setText((String)data.get(position).get("info"));
        zujian.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPreferences= context.getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
                String in =sharedPreferences.getString("order"+position, "");
                System.out.println(in);
                try {
                    Bitmap bitmap = CodeCreator.createQRCode(in);
                    ImageView iv=new ImageView(context);
                    iv.setImageBitmap(bitmap);
                    new AlertDialog.Builder(context)
                            .setTitle("二维码详情")
                            .setView(iv)
                            .setPositiveButton("确定", null)
                            .show();
                }catch(Exception e){
                    e.printStackTrace();
                }
                //System.out.println(OrderActivity.urls[position]);

            }
        });
        return convertView;
    }
}