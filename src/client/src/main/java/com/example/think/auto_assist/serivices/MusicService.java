package com.example.think.auto_assist.serivices;

import android.app.ActivityManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.think.auto_assist.R;
import com.example.think.auto_assist.activities.MusicActivity;

import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

public class MusicService extends Service {
    private int index=0;
    private int[] list;
    private String[] titles;

    private boolean isPlaying=true;
    private MediaPlayer mediaPlayer; // 媒体播放器对象
    private View mView = null;
    private ImageView mPre = null;
    private ImageView mStartStop = null;
    private ImageView mNext = null;
    private TextView tv=null;
    private ActivityManager mActivityManager;
    private LayoutParams mLayoutParams = null;
    private WindowManager mWindowManager;
    private int statusBarHeight;
    private boolean isViewAdd = false;
    private float mTouchStartX;
    private float mTouchStartY;
    private Button closing;

    MyReceiver mr;
    public MusicService() {
    }
    @Override
    public void onCreate(){
        super.onCreate();
        init();

        mediaPlayer=MediaPlayer.create(this, list[index]);
        mView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.floating, null);
        mView.setVisibility(View.GONE);
        mPre = (ImageView)mView.findViewById(R.id.pre);
        mStartStop = (ImageView)mView.findViewById(R.id.start_stop);
        mNext = (ImageView)mView.findViewById(R.id.next);
        tv = (TextView) mView.findViewById((R.id.title));
        closing=(Button)mView.findViewById(R.id.close);
        closing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWindowManager.removeView(mView);
                //mediaPlayer.stop();
                //isPlaying=false;
                isViewAdd=false;
            }
        });
        mPre.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    mPre.setBackgroundColor(0xff0000ff);
                    //mContext.sendBroadcast(new Intent("com.android.music.musicservicecommand.previous"));
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    mPre.setBackgroundColor(0x88000000);
                    if(index==0)
                        index=11;
                    else
                        --index;
                    mediaPlayer.release();
                    mediaPlayer=MediaPlayer.create(MusicService.this, list[index]);
                    mediaPlayer.start();
                    Intent i = new Intent("action.activity_receiver");
                    i.putExtra("number",index);
                    i.putExtra("type",1);
                    getApplicationContext().sendBroadcast(i);
                    isPlaying=true;
                    mStartStop.setImageResource(R.drawable.icon_pause);
                    tv.setText(titles[index]);
                }
                return true;
            }
        });

        mStartStop.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    mStartStop.setBackgroundColor(0xff0000ff);
                    if(isPlaying){
                        mStartStop.setImageResource(R.drawable.icon_play);
                        mediaPlayer.pause();
                        isPlaying=false;
                        Intent i = new Intent("action.activity_receiver");
                        i.putExtra("number",index);
                        i.putExtra("type",0);
                        getApplicationContext().sendBroadcast(i);
                    }else{
                        mStartStop.setImageResource(R.drawable.icon_pause);
                        //System.out.println("started");
                        mediaPlayer.start();
                        isPlaying=true;
                        Intent i = new Intent("action.activity_receiver");
                        i.putExtra("number",index);
                        i.putExtra("type",1);
                        getApplicationContext().sendBroadcast(i);
                    }
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    mStartStop.setBackgroundColor(0x88000000);
                }
                return true;
            }
        });
        mNext.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    mNext.setBackgroundColor(0xff0000ff);
                    //mContext.sendBroadcast(new Intent("com.android.music.musicservicecommand.next"));
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    mNext.setBackgroundColor(0x88000000);
                    if(index==11)
                        index=0;
                    else
                        ++index;
                    mediaPlayer.release();

                    mediaPlayer=MediaPlayer.create(MusicService.this, list[index]);
                    mediaPlayer.start();
                    Intent i = new Intent("action.activity_receiver");
                    i.putExtra("number",index);
                    i.putExtra("type",1);
                    getApplicationContext().sendBroadcast(i);
                    isPlaying=true;
                    mStartStop.setImageResource(R.drawable.icon_pause);
                    tv.setText(titles[index]);
                }
                return true;
            }
        });
        mView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        mTouchStartX = event.getX();
                        mTouchStartY = event.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        refreshView((int)(event.getRawX() - mTouchStartX), (int)(event.getRawY() - mTouchStartY));
                        break;
                    case MotionEvent.ACTION_UP:
                        mTouchStartX = 0;
                        mTouchStartY = 0;
                        break;
                }
                return false;
            }
        });
        mWindowManager = (WindowManager) this.getSystemService(WINDOW_SERVICE);

        mLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                LayoutParams.TYPE_SYSTEM_ERROR, LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSPARENT);
        mLayoutParams.gravity = Gravity.LEFT | Gravity.TOP;
        refreshView();
        mView.setVisibility(View.VISIBLE);
        tv.setText(titles[index]);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer m) {
                //m.release();
                if(index==11)
                    index=0;
                else
                    ++index;
                mediaPlayer.release();
                System.out.println("released!!");
                mediaPlayer=MediaPlayer.create(MusicService.this, list[index]);
                mediaPlayer.start();
                isPlaying=true;
            }
        });

        mr=new MyReceiver();
        IntentFilter fil=new IntentFilter();
        fil.addAction("action.service_receiver");
        registerReceiver(mr,fil);

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //mWindowManager.addView(mView,mLayoutParams);
        mediaPlayer.start();
        return 1;

    }
    private void init(){
        list=new int[]{R.raw.beautiful_night ,R.raw.breaking_my_heart, R.raw.every_breath, R.raw.fengzheng, R.raw.flower_dance,
                R.raw.guangyin, R.raw.liuxing, R.raw.to_be, R.raw.nufang,R.raw.yanyangt, R.raw.what_are_words, R.raw.wonderful_world};
        titles= new String[]{"Beautiful night","Breaking my heart","Every breath you take","风筝","Flower dance","光阴的故事","流星","To be with you","怒放的生命","艳阳天","What are words","What a wonderful world"};
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    private void refreshView(int x, int y) {
        if (statusBarHeight == 0) {
            View rootView = mView.getRootView();
            Rect r = new Rect();
            rootView.getWindowVisibleDisplayFrame(r);
            statusBarHeight = r.top;
        }
        mLayoutParams.x = x;
        mLayoutParams.y = y - statusBarHeight;
        refreshView();
    }

    private void refreshView() {
        if (isViewAdd) {
            mWindowManager.updateViewLayout(mView, mLayoutParams);
        } else {
            mWindowManager.addView(mView, mLayoutParams);
            isViewAdd = true;
        }
    }

    @Override
    public void onDestroy() {
        if(mediaPlayer !=null){
            mediaPlayer.release();
            //sendBC4UpdateUI(2);//更新界面
        }
        super.onDestroy();
    }

    public class MyReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(final Context context,Intent intent){
            int i=intent.getIntExtra("number",-1);
            if(mediaPlayer!=null)
                mediaPlayer.release();
            mediaPlayer=MediaPlayer.create(MusicService.this,list[i]);
            index=i;
            tv.setText(titles[index]);
            mediaPlayer.start();
            mStartStop.setImageResource(R.drawable.icon_pause);
            isPlaying=true;

            Intent intent1 = new Intent("action.activity_receiver");
            intent1.putExtra("number",index);
            intent1.putExtra("type",1);
            getApplicationContext().sendBroadcast(intent1);
        }
    }
}
