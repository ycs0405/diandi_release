package com.cargps.android.model.activity;

import android.widget.TextView;

import com.cargps.android.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_msg_detail_layout)
public class MsgDetailActivity extends BaseActivity {
    @ViewById
    TextView msgTv, dateTv, contentTv;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();

        setTitleText("消息详情");

    }

    @Override
    public boolean isLogin() {
        return true;
    }
}
