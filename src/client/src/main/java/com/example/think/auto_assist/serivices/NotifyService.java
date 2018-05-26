package com.example.think.auto_assist.serivices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v7.app.NotificationCompat;

import com.example.think.auto_assist.R;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class NotifyService extends Service {
    public NotifyService() {
    }

    private Thread tt;
    @SuppressLint("NewApi")
    @Override
    public void onCreate(){
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        //System.out.println("the service is stopped");
        tt.interrupt();
        //tt.stop();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            tt=new Thread(new Runnable() {

                final NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                @Override
                public void run() {
                    while(true) {
                        try {
                            Socket s = new Socket(getString(R.string.ipaddress), 4800);
                            PrintWriter send = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "UTF-8"), true);
                            BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "UTF-8"));
                            SharedPreferences sharedPreferences = getSharedPreferences("userinfo", Activity.MODE_PRIVATE);
                            String userid = sharedPreferences.getString("id", "");
                            send.println("check," + userid);
                            String line = br.readLine();
                            System.out.println(line);
                            String[] answer = line.split(",");
                            if (answer[0].equals("fail")) {
                                int n = Integer.valueOf(answer[1]);
                                for (int i = 0; i < n; ++i) {
                                    String carid = answer[2 + 6 * i];
                                    //System.out.println(carid);
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(NotifyService.this);

                                    // params
                                    int smallIconId = R.drawable.warning;
                                    Bitmap largeIcon = ((BitmapDrawable) getResources().getDrawable(R.drawable.warning)).getBitmap();
                                    String info = "";
                                    if (answer[3 + 6 * i].equals("1"))
                                        info += "里程过长，需要维护；";
                                    if (answer[4 + 6 * i].equals("1"))
                                        info += "油量不足，尽快加油！";
                                    if (answer[5 + 6 * i].equals("1"))
                                        info += "发动机性能异常，请去维修！";
                                    if (answer[6 + 6 * i].equals("1"))
                                        info += "变速器性能异常，请去维修！";
                                    if (answer[7 + 6 * i].equals("1"))
                                        info += "车灯性能异常，请去维修！";

                                    builder.setLargeIcon(largeIcon)
                                            .setSmallIcon(smallIconId)
                                            .setContentTitle(carid)
                                            .setContentText(info)
                                            .setTicker(carid);
                                    //.setContentIntent(PendingIntent.getActivity(Notifyservice.this, 0, intent, 0));

                                    Notification nn = builder.build();
                                    nm.notify(carid.hashCode(), nn);
                                }
                            }
                            //System.out.println("run again");
                            Thread.sleep(15000);
                        } catch (Exception e) {
                            e.printStackTrace();
                            //Thread.interrupted();
                            break;
                        }
                    }
                }
            });
            tt.start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }
}
