package com.cargps.android.model.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.amap.api.services.route.WalkPath;
import com.cargps.android.R;
import com.cargps.android.utils.AMapUtil;

public class WalkTipsActivity extends Activity {
    private WalkPath mWalkPath;
    private TextView mTitle, mTitleWalkRoute;
    private ListView mWalkSegmentList;
    private WalkSegmentListAdapter mWalkSegmentListAdapter;
    private ImageView backImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_detail);
        getIntentData();
        mTitle = (TextView) findViewById(R.id.titleTv);
        backImage = (ImageView) findViewById(R.id.backimg);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle.setText("步行路线详情");
        mTitleWalkRoute = (TextView) findViewById(R.id.firstline);
        String dur = AMapUtil.getFriendlyTime((int) mWalkPath.getDuration());
        String dis = AMapUtil
                .getFriendlyLength((int) mWalkPath.getDistance());
        mTitleWalkRoute.setText(dur + "(" + dis + ")");
        mWalkSegmentList = (ListView) findViewById(R.id.bus_segment_list);
        mWalkSegmentListAdapter = new WalkSegmentListAdapter(
                this.getApplicationContext(), mWalkPath.getSteps());
        mWalkSegmentList.setAdapter(mWalkSegmentListAdapter);

    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent == null) {
            return;
        }
        mWalkPath = intent.getParcelableExtra("walk_path");
    }

}
