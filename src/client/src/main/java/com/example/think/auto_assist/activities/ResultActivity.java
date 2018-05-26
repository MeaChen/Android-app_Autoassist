package com.example.think.auto_assist.activities;

import android.annotation.SuppressLint;
import android.content.Context;
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

public class ResultActivity extends AppCompatActivity {
    List<Map<String, String>> listt;
    private String car_id;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //result=this.getIntent().getExtras().getString("info");
        car_id=this.getIntent().getExtras().getString("car");
        //System.out.println(result);
        init();

        TextView v=(TextView)findViewById(R.id.carnum);
        v.setText(car_id);

        ListView lv=(ListView)findViewById(R.id.result);
        lv.setAdapter(new ResultAdapter(this,listt));

        Button button=(Button)findViewById(R.id.go);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent m=new Intent(ResultActivity.this,IllegalActivity.class);
                //startActivity(m);
                finish();
            }
        });

    }
    private void init(){
        listt=new ArrayList<>();
        try{
            Socket s=new Socket(getString(R.string.ipaddress),4800);
            PrintWriter send=new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
            send.println("weizhang,"+car_id);
            String line=br.readLine();
            String[] res=line.split(",");
            String taitou="共违章"+res[2]+"次，计"+res[0]+"分，罚款"+res[1]+"元";
            TextView tt=(TextView)findViewById(R.id.total);
            tt.setText(taitou);
            int number=Integer.valueOf(res[2]);
            //System.out.println(number);
            for(int i=0;i<number;++i){
                Map<String, String> map=new HashMap<String, String>();
                map.put("data",res[5*i+3]);
                //System.out.println("data:"+res[4*i+3]);
                String x=res[5*i+4];
                //System.out.println(x);
                map.put("money","罚款"+x+"元，共计"+res[5*i+7]+"分");
                //System.out.println("money:"+res[4*i+4]);
                map.put("area",res[5*i+5]);
                //System.out.println("area"+res[4*i+5]);
                map.put("info",res[5*i+6]);

                listt.add(map);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

class ResultAdapter extends BaseAdapter{

    private List<Map<String, String>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public ResultAdapter(Context context,List<Map<String, String>> data){
        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public final class Detail{
        public TextView timed;
        public TextView money;
        public TextView address;
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
        final Detail dd;
        if(convertView==null){
            dd=new Detail();
            //获得组件，实例化组件
            convertView=layoutInflater.inflate(R.layout.result, null);

            dd.timed=(TextView)convertView.findViewById(R.id.wz_time);
            dd.money=(TextView) convertView.findViewById(R.id.wz_money);
            dd.address=(TextView)convertView.findViewById(R.id.wz_addr);
            dd.info=(TextView)convertView.findViewById(R.id.wz_info);
            //zujian.info=(TextView)convertView.findViewById(R.id.info);
            convertView.setTag(dd);
        }else{
            dd=(Detail)convertView.getTag();
        }
        //System.out.println((String)data.get(position).get("data"));
        dd.timed.setText((String)data.get(position).get("data"));
        //System.out.println((String)data.get(position).get("money"));
        dd.money.setText((String)data.get(position).get("money"));
        dd.money.setTextSize(12);
        //System.out.println((String)data.get(position).get("area"));
        dd.address.setText((String)data.get(position).get("area"));
        //System.out.println((String)data.get(position).get("info"));
        dd.info.setText((String)data.get(position).get("info"));

        return convertView;
    }
}
