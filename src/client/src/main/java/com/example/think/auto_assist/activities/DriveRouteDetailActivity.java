package com.example.think.auto_assist.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.example.think.auto_assist.R;
import com.example.think.auto_assist.utils.AMapUtil;
import java.util.ArrayList;
import java.util.List;

class DriveSegmentListAdapter extends BaseAdapter {
    private Context mContext;
    private List<DriveStep> mItemList = new ArrayList<DriveStep>();

    public DriveSegmentListAdapter(Context context, List<DriveStep> list) {
        this.mContext = context;
        mItemList.add(new DriveStep());
        for (DriveStep driveStep : list) {
            mItemList.add(driveStep);
        }
        mItemList.add(new DriveStep());
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return mItemList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return mItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(mContext, R.layout.item_bus_segment,
                    null);
            holder.driveDirIcon = (ImageView) convertView
                    .findViewById(R.id.bus_dir_icon);
            holder.driveLineName = (TextView) convertView
                    .findViewById(R.id.bus_line_name);
            holder.driveDirUp = (ImageView) convertView
                    .findViewById(R.id.bus_dir_icon_up);
            holder.driveDirDown = (ImageView) convertView
                    .findViewById(R.id.bus_dir_icon_down);
            holder.splitLine = (ImageView) convertView
                    .findViewById(R.id.bus_seg_split_line);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        final DriveStep item = mItemList.get(position);
        if (position == 0) {
            holder.driveDirIcon.setImageResource(R.drawable.dir_start);
            holder.driveLineName.setText("出发");
            holder.driveDirUp.setVisibility(View.GONE);
            holder.driveDirDown.setVisibility(View.VISIBLE);
            holder.splitLine.setVisibility(View.GONE);
            return convertView;
        } else if (position == mItemList.size() - 1) {
            holder.driveDirIcon.setImageResource(R.drawable.dir_end);
            holder.driveLineName.setText("到达终点");
            holder.driveDirUp.setVisibility(View.VISIBLE);
            holder.driveDirDown.setVisibility(View.GONE);
            holder.splitLine.setVisibility(View.VISIBLE);
            return convertView;
        } else {
            String actionName = item.getAction();
            int resID = AMapUtil.getDriveActionID(actionName);
            holder.driveDirIcon.setImageResource(resID);
            holder.driveLineName.setText(item.getInstruction());
            holder.driveDirUp.setVisibility(View.VISIBLE);
            holder.driveDirDown.setVisibility(View.VISIBLE);
            holder.splitLine.setVisibility(View.VISIBLE);
            return convertView;
        }

    }

    private class ViewHolder {
        TextView driveLineName;
        ImageView driveDirIcon;
        ImageView driveDirUp;
        ImageView driveDirDown;
        ImageView splitLine;
    }

}

public class DriveRouteDetailActivity extends AppCompatActivity {
    private DrivePath mDrivePath;
    private DriveRouteResult mDriveRouteResult;
    private TextView mTitle, mTitleDriveRoute, mDesDriveRoute;
    private ListView mDriveSegmentList;
    private DriveSegmentListAdapter mDriveSegmentListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_route_detail);

        getIntentData();
        init();
    }

    private void init() {
        mTitle = (TextView) findViewById(R.id.title_center);
        mTitleDriveRoute = (TextView) findViewById(R.id.firstline);
        mDesDriveRoute = (TextView) findViewById(R.id.secondline);
        mTitle.setText("驾车路线详情");
        String dur = AMapUtil.getFriendlyTime((int) mDrivePath.getDuration());
        String dis = AMapUtil.getFriendlyLength((int) mDrivePath
                .getDistance());
        mTitleDriveRoute.setText(dur + "(" + dis + ")");
        int taxiCost = (int) mDriveRouteResult.getTaxiCost();
        mDesDriveRoute.setText("打车约"+taxiCost+"元");
        mDesDriveRoute.setVisibility(View.VISIBLE);
        configureListView();
    }

    private void configureListView() {
        mDriveSegmentList = (ListView) findViewById(R.id.bus_segment_list);
        mDriveSegmentListAdapter = new DriveSegmentListAdapter(
                this.getApplicationContext(), mDrivePath.getSteps());
        mDriveSegmentList.setAdapter(mDriveSegmentListAdapter);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mDrivePath = intent.getParcelableExtra("drive_path");
        mDriveRouteResult = intent.getParcelableExtra("drive_result");
    }

    public void onBackClick(View view) {
        this.finish();
    }
}
