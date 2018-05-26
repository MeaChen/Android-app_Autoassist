package com.example.think.auto_assist.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.view.View.OnClickListener;
import com.example.think.auto_assist.R;
import com.karics.library.zxing.android.CaptureActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class CarActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SCAN = 0x0000;
    ExpandableListView list;
    View delView=null;
    TextView del=null;
    TextView cancel=null;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car);

        delView=findViewById(R.id.linear_del);
        del=(TextView)findViewById(R.id.tv_del);
        cancel=(TextView)findViewById(R.id.tv_cancel);

        String line="";
        try{
            Socket s=new Socket(getString(R.string.ipaddress),4800);
            PrintWriter send=new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
            SharedPreferences sharedPreferences= getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
            String userid =sharedPreferences.getString("id", "");
            send.println("getinfos,"+userid);

            line=br.readLine();
            //System.out.println(line);
        }catch(Exception e){
            e.printStackTrace();
        }
        String[] all=line.split(",");
        final int number=Integer.valueOf(all[all.length-1]);
        final String[] car_ids=new String[number];
        //设置组视图的图片
        final int[] logos = new int[number];
        //设置组视图的显示文字
        final String[] category = new String[number];
        //子视图显示文字
        final String[][] subcategory=new String[number][7];
        for(int i=0;i< number; ++i){
            //System.out.println("hhhhh");
            //System.out.println(all[10*i]);
            car_ids[i]=all[10*i];
            String a=all[10*i+1];
            String carid=all[10*i];
            String vv=all[10*i+2];
            if(a.equals("aodi")) {
                logos[i] = R.drawable.aodi;
                category[i]=carid+"  奥迪"+vv;
            }
            else if(a.equals("baoma")) {
                logos[i] = R.drawable.baoma;
                category[i]=carid+"  宝马"+vv;
            }
            else if(a.equals("baoshijie")) {
                logos[i] = R.drawable.baoshijie;
                category[i]=carid+"  保时捷"+vv;
            }
            else if(a.equals("benchi")) {
                logos[i] = R.drawable.benchi;
                category[i]=carid+"  奔驰"+vv;
            }
            else if(a.equals("fengtian")) {
                logos[i] = R.drawable.fengtian;
                category[i]=carid+"  丰田"+vv;
            }
            else if(a.equals("fute")) {
                logos[i] = R.drawable.fute;
                category[i]=carid+"    福特"+vv;
            }
            else if(a.equals("dazhong")) {
                logos[i] = R.drawable.dazhong;
                category[i]=carid+"  大众"+vv;
            }
            else if(a.equals("falali")) {
                logos[i] = R.drawable.falali;
                category[i]=carid+"  法拉利"+vv;
            }
            subcategory[i][0]="发动机号："+all[10*i+3];
            String level=all[10*i+4];
            String[] p=level.split("\\.");
            subcategory[i][1]="车身级别："+p[0]+"车"+p[1]+"座";
            subcategory[i][2]="已行驶里程数："+all[10*i+5]+"千米";
            subcategory[i][3]="剩余汽油量："+all[10*i+6]+"%";
            if(all[10*i+7].equals("1"))
                subcategory[i][4]="发动机状态："+"好";
            else
                subcategory[i][4]="发动机状态："+"异常";
            if(all[10*i+8].equals("1"))
                subcategory[i][5]="变速器状态："+"好";
            else
                subcategory[i][5]="发动机状态："+"异常";
            if(all[10*i+9].equals("1"))
                subcategory[i][6]="车灯状态："+"好";
            else
                subcategory[i][6]="发动机状态："+"坏";
        }

        list=(ExpandableListView)findViewById(R.id.list);
        //创建一个BaseExpandableListAdapter对象
        final ExpandableListAdapter adapter=new BaseExpandableListAdapter() {

            //子视图图片
            private int sublogos[][]=new int[][]{{},{},{}};
            //定义一个显示文字信息的方法
            TextView getTextView(){
                AbsListView.LayoutParams lp=new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,64);
                TextView textView=new TextView(CarActivity.this);
                //设置 textView控件的布局
                textView.setLayoutParams(lp);
                //设置该textView中的内容相对于textView的位置
                textView.setGravity(Gravity.CENTER_VERTICAL);
                //设置txtView的内边距
                textView.setPadding(36, 0, 0, 0);
                //设置文本颜色
                textView.setTextColor(Color.BLACK);
                //textView.setTextSize(15);
                return textView;

            }
            @Override
            public boolean isChildSelectable(int groupPosition, int childPosition) {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public boolean hasStableIds() {
                // TODO Auto-generated method stub
                return true;
            }
            //取得用于显示给定分组的视图. 这个方法仅返回分组的视图对象
            @Override
            public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                //定义一个LinearLayout用于存放ImageView、TextView
                LinearLayout ll=new LinearLayout(CarActivity.this);

                //设置子控件的显示方式为水平
                ll.setOrientation(LinearLayout.HORIZONTAL);

                //定义一个ImageView用于显示列表图片
                ImageView logo=new ImageView(CarActivity.this);
                logo.setPadding(60, 0, 0, 0);
                //设置logo的大小(50（padding）+46=96)
                AbsListView.LayoutParams lparParams=new AbsListView.LayoutParams(106,46);
                logo.setLayoutParams(lparParams);
                logo.setImageResource(logos[groupPosition]);
                ll.addView(logo);
                TextView textView=getTextView();
                textView.setTextSize(20);
                textView.setText(category[groupPosition]);
                textView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        //System.out.println("chufa");
                        delView.setVisibility(View.VISIBLE);
                        cancel.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                delView.setVisibility(View.GONE);
                            }
                        });

                        del.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String id=car_ids[groupPosition];
                                try{
                                    Socket s=new Socket(getString(R.string.ipaddress),4800);
                                    PrintWriter send=new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
                                    BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
                                    send.println("delcar,"+id);
                                    String answer = br.readLine();
                                    if(answer.equals("ok"))
                                    {
                                        Intent x = new Intent(CarActivity.this,CarActivity.class);
                                        startActivity(x);

                                    }else{
                                        delView.setVisibility(View.GONE);
                                        Toast.makeText(CarActivity.this,"删除失败",Toast.LENGTH_SHORT).show();
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                            }
                        });
                        return true;
                    }
                });
                ll.addView(textView);
                return ll;
            }
            //取得指定分组的ID.该组ID必须在组中是唯一的.必须不同于其他所有ID（分组及子项目的ID）.
            @Override
            public long getGroupId(int groupPosition) {
                // TODO Auto-generated method stub
                return groupPosition;
            }
            //取得分组数
            @Override
            public int getGroupCount() {
                // TODO Auto-generated method stub
                return category.length;
            }
            //取得与给定分组关联的数据
            @Override
            public Object getGroup(int groupPosition) {
                // TODO Auto-generated method stub
                return category[groupPosition];
            }
            //取得指定分组的子元素数.
            @Override
            public int getChildrenCount(int groupPosition) {
                // TODO Auto-generated method stub
                return subcategory[groupPosition].length;
            }
            //取得显示给定分组给定子位置的数据用的视图
            @Override
            public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
                // TODO Auto-generated method stub
                //定义一个LinearLayout用于存放ImageView、TextView
                LinearLayout ll=new LinearLayout(CarActivity.this);
                //设置子控件的显示方式为水平
                ll.setOrientation(LinearLayout.HORIZONTAL);

                TextView textView=getTextView();
                textView.setText(subcategory[groupPosition][childPosition]);
                ll.addView(textView);
                return ll;
            }
            //取得给定分组中给定子视图的ID. 该组ID必须在组中是唯一的.必须不同于其他所有ID（分组及子项目的ID）.
            @Override
            public long getChildId(int groupPosition, int childPosition) {
                // TODO Auto-generated method stub
                return childPosition;
            }

            @Override
            public Object getChild(int groupPosition, int childPosition) {
                // TODO Auto-generated method stub
                return subcategory[groupPosition][childPosition];
            }

        };

        list.setAdapter(adapter);

        //list.invalidate();
        //为ExpandableListView的子列表单击事件设置监听器
        list.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                //Toast.makeText(CarActivity.this, "你单击了：" +adapter.getChild(groupPosition, childPosition), Toast.LENGTH_LONG).show();
                return true;
            }
        });

        Button back=(Button)findViewById(R.id.go);
        back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CarActivity.this, PersonActivity.class);
                startActivity(intent);
            }
        });

        Button scanner=(Button)findViewById(R.id.scan);
        scanner.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                Intent intent = new Intent(CarActivity.this, CaptureActivity.class);
                //startActivityForResult(intent, REQUEST_CODE_SCAN);
                startActivity(intent);
            }
         }
        );

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

