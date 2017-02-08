package com.cargps.android.model.activity;

import android.content.Intent;

import com.cargps.android.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

@EActivity(R.layout.activity_more_layout)
public class MoreActivity extends BaseActivity {
    @Override
    @AfterViews
    public void initViews() {
        super.initViews();
        setTitleText("更多");
    }

    @Click
    public void probLemayout() {
        com.cargps.android.model.activity.WebViewActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).title("常用问题帮助").url("fuwutiaokuan.html").start();
    }

    @Click
    public void clausLayout() {
        com.cargps.android.model.activity.WebViewActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).title("用户条款").url("bangzhu.html").start();
    }

    @Click
    public void aboutLayout() {
        com.cargps.android.model.activity.WebViewActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).title("关于我们").url("about.html").start();
    }

    @Click
    public void lvLayout() {
        com.cargps.android.model.activity.WebViewActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).title("电滴信用评分").url("pf.html").start();
    }

    @Click
    public void refundLayout() {
        com.cargps.android.model.activity.WebViewActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).title("还车方式").url("hc.html").start();

    }
}
 