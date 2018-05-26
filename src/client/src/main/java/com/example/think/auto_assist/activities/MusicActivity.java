package com.example.think.auto_assist.activities;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.think.auto_assist.R;
import com.example.think.auto_assist.serivices.MusicService;
import com.karics.library.zxing.encode.CodeCreator;

import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicActivity extends AppCompatActivity {
    //public static final String SERVICE_RECEIVER="action.service_receiver";
    //public static final String ACTIVITY_RECEIVER="action.activity_receiver";
    private Button back;
    private List<Map<String, Object>> list;
    ActivityReceiver ar;
    private musicAdapter ma;
    private ListView listview=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        back=(Button)findViewById(R.id.rett);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intt=new Intent(MusicActivity.this, PersonActivity.class);
                startActivity(intt);
            }
        });
        init();
        listview=(ListView)findViewById(R.id.listview);
        ma = new musicAdapter(this,list);
        listview.setAdapter(ma);
        //ma.getItem(0).
        ar=new ActivityReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction("action.activity_receiver");
        registerReceiver(ar,filter);

    }

    private void init(){
        list=new ArrayList<>();
        Map<String, Object> map=new HashMap<String, Object>();
        map.put("name","Beautiful Night");
        list.add(map);
        map=new HashMap<>();
        map.put("name","Breaking My Heart");
        list.add(map);
        map=new HashMap<>();
        map.put("name","Every Breath You Take");
        list.add(map);
        map=new HashMap<>();
        map.put("name","风筝");
        list.add(map);
        map=new HashMap<>();
        map.put("name","Flower Dance");
        list.add(map);
        map=new HashMap<>();
        map.put("name","光阴的故事");
        list.add(map);
        map=new HashMap<>();
        map.put("name","流星");
        list.add(map);
        map=new HashMap<>();
        map.put("name","To Be With You");
        list.add(map);
        map=new HashMap<>();
        map.put("name","怒放的生命");
        list.add(map);
        map=new HashMap<>();
        map.put("name","艳阳天");
        list.add(map);
        map=new HashMap<>();
        map.put("name","What Are Words");
        list.add(map);
        map=new HashMap<>();
        map.put("name","What A Wonderful World");
        list.add(map);

    }

    public class ActivityReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent){
            int i=intent.getIntExtra("number",-1);
            int type=intent.getIntExtra("type",-1);
            for(int j=0;j<list.size();++j)
            {
                TextView t=(TextView)listview.getChildAt(j).findViewById(R.id.name);
                t.setTextColor(Color.WHITE);

                listview.getChildAt(j).findViewById(R.id.play).setBackgroundResource(R.drawable.icon_play);
            }
            if(type==1)
            {
                TextView tv = (TextView)listview.getChildAt(i).findViewById(R.id.name);
                tv.setTextColor(android.graphics.Color.RED);
                listview.getChildAt(i).findViewById(R.id.play).setBackgroundResource(R.drawable.icon_pause);
            }

        }
    }
}

class musicAdapter extends BaseAdapter {
    private List<Map<String, Object>> data;
    private LayoutInflater layoutInflater;
    private Context context;
    public musicAdapter(Context context,List<Map<String, Object>> data){
        this.context=context;
        this.data=data;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public final class Detail{
        public TextView name;
        public Button play;
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
            convertView=layoutInflater.inflate(R.layout.musiclist, null);

            dd.name=(TextView)convertView.findViewById(R.id.name);
            dd.play=(Button)convertView.findViewById(R.id.play);
            //dd.play.setGravity();
            //zujian.info=(TextView)convertView.findViewById(R.id.info);
            convertView.setTag(dd);
        }else{
            dd=(Detail)convertView.getTag();
        }
        //绑定数据
        dd.name.setText((String)data.get(position).get("name"));
        dd.name.setTextSize(16);

        dd.play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isServiceRunning(context,"MusicService"))
                {
                    Intent music=new Intent(context, MusicService.class);
                    context.startService(music);
                }

                dd.play.setBackgroundResource(R.drawable.icon_pause);

                dd.name.setTextColor(Color.RED);
                //parent.getChildAt(0)
                Intent i=new Intent("action.service_receiver");
                i.putExtra("number",position);
                i.putExtra("type",1);
                context.sendBroadcast(i);

                //if(dd.play.getBackground()==R.drawable.icon_pause)
            }
        });
        return convertView;
    }

    public static boolean isServiceRunning(Context mcontext, String serviceClassName){
        final ActivityManager activityManager = (ActivityManager) mcontext.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {
            if (runningServiceInfo.service.getClassName().equals(serviceClassName)){
                return true;
            }
        }
        return false;
    }
}

