package com.example.think.auto_assist.activities;

import com.example.think.auto_assist.serivices.MusicService;
import com.example.think.auto_assist.serivices.NotifyService;
import com.example.think.auto_assist.utils.AMapUtil;
import com.karics.library.zxing.encode.CodeCreator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.ArrayList;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.LocationSource;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMap.InfoWindowAdapter;
import com.amap.api.maps.AMap.OnMapClickListener;
import com.amap.api.maps.AMap.OnInfoWindowClickListener;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.amap.api.services.poisearch.PoiSearch.SearchBound;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import android.view.View.OnClickListener;
import com.amap.api.maps.CameraUpdateFactory;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.think.auto_assist.R;
import com.example.think.auto_assist.utils.ToastUtil;
//GeocodeSearch.OnGeocodeSearchListener,
public class HomeActivity extends AppCompatActivity implements LocationSource, AMapLocationListener,
        OnMapClickListener, OnInfoWindowClickListener, InfoWindowAdapter, OnMarkerClickListener, OnPoiSearchListener{
    private AMap aMap;
    private MapView mMapView = null;
    private LocationSource.OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    //private GeocodeSearch geocoderSearch;
    private LatLonPoint latLonPoint;
    private String lcity;

    //private Marker geoMarker;
    //private Marker regeoMarker;
    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private Marker locationMarker; // 选择的点
    private Marker detailMarker;
    private Marker mlastMarker;
    private PoiSearch poiSearch;
    private myPoiOverlay poiOverlay;// poi图层
    private List<PoiItem> poiItems;// poi数据
    private PoiItem mPoi;

    private RelativeLayout mPoiDetail;
    private TextView mPoiName, mPoiAddress;

    private ProgressBar pb;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        mMapView.onCreate(savedInstanceState);
        aMap=mMapView.getMap();
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);

        Button p = (Button) findViewById(R.id.title_bar_left_menu);
        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gep();
            }
        });

        Button book=(Button)findViewById(R.id.booking);
        book.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                booking();
            }
        });

        Button rr=(Button)findViewById(R.id.route);
        rr.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                searchRoute();
            }
        });

        Button ss=(Button)findViewById(R.id.station);
        ss.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                showPoint();
            }
        });

        pb=(ProgressBar)findViewById(R.id.progressBar);
    }

    private void booking(){

        int index = poiOverlay.getPoiIndex(mlastMarker);
        LatLonPoint a=poiItems.get(index).getLatLonPoint();
        //System.out.println(a.getLatitude()+" "+a.getLongitude());
        try{
            Socket s=new Socket(getString(R.string.ipaddress),4800);
            //System.out.println("connected");
            PrintWriter send=new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
            SharedPreferences sharedPreferences= getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
            String userid =sharedPreferences.getString("id", "");
            send.println("book,"+a.getLatitude()+","+a.getLongitude()+","+userid);
            //System.out.println(password);
            String line=br.readLine();
            //System.out.println(line);
            if(line.equals("fail")) {
                Toast tt=Toast.makeText(HomeActivity.this,"预约失败",Toast.LENGTH_SHORT);
                tt.show();
            }else{

                final String[] types=line.split(",");
                //System.out.println(types);
                final int carnumber=Integer.valueOf(types[types.length-1]);
                if(carnumber==0){
                    //Toast.makeText(this,"请绑定车辆",Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder biubiu = new AlertDialog.Builder(HomeActivity.this);
                    biubiu.setTitle("提示");
                    biubiu.setMessage("您还没有绑定任何车辆！");
                    biubiu.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    biubiu.show();
                    return;
                }
                pb.setVisibility(View.VISIBLE);
                pb.bringToFront();
                final int sid=types.length-3-carnumber;
                final String stationid=types[sid];
                final String stationname=types[sid+1];
                //System.out.println(stationname);
                AlertDialog.Builder builder=new AlertDialog.Builder(HomeActivity.this);
                builder.setTitle("预约加油");
                final View view1= LayoutInflater.from(HomeActivity.this).inflate(R.layout.booking, null);
                final RadioGroup moretypes=(RadioGroup)view1.findViewById(R.id.type);
                final RadioGroup morecars=(RadioGroup)view1.findViewById(R.id.car);
                for(int j=sid+2;j<types.length-1;++j)
                {
                    RadioButton rb=new RadioButton(this);
                    rb.setText(types[j]);
                    morecars.addView(rb);
                }
                for(int i=1;i<sid;++i){
                    RadioButton rb=new RadioButton(this);
                    rb.setText(types[i]);
                    System.out.println(types[i]);
                    moretypes.addView(rb);
                }
                //System.out.println("show");
                builder.setView(view1);
                builder.setPositiveButton("确定",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        EditText et=(EditText)view1.findViewById(R.id.name);
                        String name=et.getText().toString();
                        //System.out.println(name);
                        DatePicker dp=(DatePicker)view1.findViewById(R.id.dp);
                        int y=dp.getYear();
                        int m=dp.getMonth()+1;
                        int d=dp.getDayOfMonth();
                        //System.out.println("y:"+y+",m:"+m+",d:"+d);
                        int id=moretypes.getCheckedRadioButtonId();
                        int car_id=morecars.getCheckedRadioButtonId();
                        System.out.println("type_id:"+id);
                        Spinner s1=(Spinner)view1.findViewById(R.id.spin);
                        //System.out.println(s1.getSelectedItem().toString());

                        SharedPreferences sharedPreferences= getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
                        String userid =sharedPreferences.getString("id", "");
                        System.out.println("the selected car is "+types[sid+1+car_id]);
                        System.out.println("the selected type is "+types[id-carnumber]);
                        String sending="appointment"+","+userid+","+name+","+y+"-"+m+"-"+d+","+stationid+","+stationname+","+types[id-carnumber]+","+s1.getSelectedItem().toString()+","+types[sid+1+car_id];
                        String url="Name:"+name+"\r\n"+"Time:"+y+"-"+m+"-"+d+"\r\n"+"Type:"+types[id]+"\r\n"+"Number:"+s1.getSelectedItem().toString()+"\r\n"+"Station Name:"+stationname+"\r\n";
                        try{
                            Socket s=new Socket(getString(R.string.ipaddress),4800);
                            PrintWriter send1=new PrintWriter(new OutputStreamWriter(s.getOutputStream(),"UTF-8"),true);
                            BufferedReader br1 = new BufferedReader(new InputStreamReader(s.getInputStream(),"UTF-8"));
                            send1.println(sending);
                            String ss=br1.readLine();
                            send1.close();
                            br1.close();
                            s.close();
                            if(ss.equals("ok")){
                                AlertDialog.Builder builder2 = new AlertDialog.Builder(HomeActivity.this);
                                builder2.setTitle("您的二维码");	//设置对话框标题
                                Bitmap bitmap = CodeCreator.createQRCode(url);
                                ImageView iv=new ImageView(HomeActivity.this);
                                iv.setImageBitmap(bitmap);
                                builder2.setView(iv);
                                builder2.setPositiveButton("确定", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which){
                                        Toast t=Toast.makeText(HomeActivity.this,"您可以去订单中查看",Toast.LENGTH_LONG);
                                        t.show();
                                    }
                                });

                                builder2.show();
                            }else{
                                Toast t=Toast.makeText(HomeActivity.this,"发生错误",Toast.LENGTH_LONG);
                                t.show();
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                });
                builder.setNegativeButton("取消",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which){
                        Toast t=Toast.makeText(HomeActivity.this,"关闭对话框",Toast.LENGTH_SHORT);
                        t.show();
                    }
                });
                pb.setVisibility(View.GONE);
                builder.show();
            }
            br.close();
            send.close();
            s.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void searchRoute(){
        Intent sss=new Intent(this,SearchActivity.class);
        startActivity(sss);
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            //mLocationOption.setInterval(100000);
            mLocationOption.setOnceLocationLatest(true);
            //mLocationOption.setOnceLocation(true);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
            SharedPreferences sharedPreferences= getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
            //String music_setting =sharedPreferences.getString("music_setting", "");
            //String error_setting = sharedPreferences.getString("error_setting", "");

        }
    }
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                //mLocationErrText.setVisibility(View.GONE);
                //mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                latLonPoint=new LatLonPoint(amapLocation.getLatitude(),amapLocation.getLongitude());
                lcity=amapLocation.getCity();
                aMap.addMarker(new MarkerOptions()
                        .position(AMapUtil.convertToLatLng(latLonPoint))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.point4)));
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(AMapUtil.convertToLatLng(latLonPoint), 14));
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
                //mLocationErrText.setVisibility(View.VISIBLE);
                //mLocationErrText.setText(errText);
                ToastUtil.show(this,errText);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }
    private void gep(){
        Intent intent = new Intent(this, PersonActivity.class);
        this.startActivity(intent);
    }
    private void showPoint(){
        if(aMap==null){
            aMap=mMapView.getMap();
        }
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setOnInfoWindowClickListener(this);
        aMap.setInfoWindowAdapter(this);
        locationMarker = aMap.addMarker(new MarkerOptions()
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.point4)))
                .position(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude())));
        locationMarker.showInfoWindow();
        mPoiName = (TextView) findViewById(R.id.poi_name);
        mPoiAddress = (TextView) findViewById(R.id.poi_address);
        mPoiDetail = (RelativeLayout) findViewById(R.id.poi_detail);

        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude()), 14));
        currentPage = 0;
        query = new PoiSearch.Query("加油站", "", lcity);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页
        if (latLonPoint != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new SearchBound(latLonPoint, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {   // 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();  // 取得第一页的poiitem数据，页数从数字0开始

                    List<SuggestionCity> suggestionCities = poiResult.getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        //清除POI信息显示
                        whetherToShowDetailInfo(false);
                        //并还原点击marker样式
                        if (mlastMarker != null) {
                            resetlastmarker();
                        }
                        //清理之前搜索结果的marker
                        if (poiOverlay !=null) {
                            poiOverlay.removeFromMap();
                        }
                        aMap.clear();
                        poiOverlay = new myPoiOverlay(aMap, poiItems);
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

                        aMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(BitmapFactory.decodeResource(
                                                getResources(), R.drawable.point4)))
                                .position(new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude())));

                        aMap.addCircle(new CircleOptions()
                                .center(new LatLng(latLonPoint.getLatitude(),
                                        latLonPoint.getLongitude())).radius(5000)
                                .strokeColor(Color.BLUE)
                                .fillColor(Color.argb(50, 1, 1, 1))
                                .strokeWidth(2));

                    } else if (suggestionCities != null && suggestionCities.size() > 0) {
                        showSuggestCity(suggestionCities);
                    } else {
                        ToastUtil.show(this, "您的附近没有加油站，搜索失败");
                    }
                }
            } else {
                ToastUtil.show(this, "您的附近没有加油站，搜索失败");
            }
        }
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        //System.out.println("2333");
        if (marker.getObject() != null) {
            whetherToShowDetailInfo(true);
            try {
                PoiItem mCurrentPoi = (PoiItem) marker.getObject();
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetlastmarker();
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.poi_marker_pressed)));

                setPoiItemDisplayContent(mCurrentPoi);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }else {
            whetherToShowDetailInfo(false);
            resetlastmarker();
        }


        return true;
    }
    @Override
    public void onPoiItemSearched(PoiItem arg0, int arg1) {
        // TODO Auto-generated method stub

    }
    // 将之前被点击的marker置为原来的状态
    private void resetlastmarker() {
        int index = poiOverlay.getPoiIndex(mlastMarker);
        if (index < 10) {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), markers[index])));
        }else {
            mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                    BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight)));
        }
        mlastMarker = null;

    }


    private void setPoiItemDisplayContent(final PoiItem mCurrentPoi) {
        mPoiName.setText(mCurrentPoi.getTitle());
        mPoiAddress.setText(mCurrentPoi.getSnippet());
    }


    @Override
    public View getInfoContents(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public View getInfoWindow(Marker arg0) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void onInfoWindowClick(Marker arg0) {
        // TODO Auto-generated method stub

    }
    private int[] markers = {R.drawable.poi_marker_1,
            R.drawable.poi_marker_2,
            R.drawable.poi_marker_3,
            R.drawable.poi_marker_4,
            R.drawable.poi_marker_5,
            R.drawable.poi_marker_6,
            R.drawable.poi_marker_7,
            R.drawable.poi_marker_8,
            R.drawable.poi_marker_9,
            R.drawable.poi_marker_10
    };

    private void whetherToShowDetailInfo(boolean isToShow) {
        if (isToShow) {
            mPoiDetail.setVisibility(View.VISIBLE);

        } else {
            mPoiDetail.setVisibility(View.GONE);

        }
    }


    @Override
    public void onMapClick(LatLng arg0) {
        whetherToShowDetailInfo(false);
        if (mlastMarker != null) {
            resetlastmarker();
        }
    }

    /**
     * poi没有搜索到数据，返回一些推荐城市的信息
     */
    private void showSuggestCity(List<SuggestionCity> cities) {
        String infomation = "推荐城市\n";
        for (int i = 0; i < cities.size(); i++) {
            infomation += "城市名称:" + cities.get(i).getCityName() + "城市区号:"
                    + cities.get(i).getCityCode() + "城市编码:"
                    + cities.get(i).getAdCode() + "\n";
        }
        ToastUtil.show(this, infomation);

    }

    private class myPoiOverlay {
        private AMap mamap;
        private List<PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
        public myPoiOverlay(AMap amap ,List<PoiItem> pois) {
            mamap = amap;
            mPois = pois;
        }

        /**
         * 添加Marker到地图中
         */
        public void addToMap() {
            for (int i = 0; i < mPois.size(); i++) {
                Marker marker = mamap.addMarker(getMarkerOptions(i));
                PoiItem item = mPois.get(i);
                marker.setObject(item);
                mPoiMarks.add(marker);
            }
        }

        /**
         * 去掉PoiOverlay上所有的Marker。
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }
        /**
         * 移动镜头到当前的视角。
         */
        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mamap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < mPois.size(); i++) {
                b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                        mPois.get(i).getLatLonPoint().getLongitude()));
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(
                            new LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getSnippet();
        }

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 10) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), markers[arg0]));
                return icon;
            }else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight));
                return icon;
            }
        }
    }

}
