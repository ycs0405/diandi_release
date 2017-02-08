package com.cargps.android.model.activity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cargps.android.MyApplication;
import com.cargps.android.interfaces.ILogin;
import com.cargps.android.utils.TitleBar;
import com.fu.baseframe.base.BaseFrameActivity;

import cn.jpush.android.api.JPushInterface;

/***
 * 基类Activity
 *
 * @author fu
 * @create date 2016.05.11
 */
public class BaseActivity extends BaseFrameActivity implements TitleBar.TitleCallBack, ILogin {
    public MyApplication app;
    public TitleBar titleBar = new TitleBar();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (MyApplication) getApplication();

        registerReceiver(broadcastReceiver, new IntentFilter("close"));

        //app.setSystemBar(this);


    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("close")) {

            }
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
    }


    public void initViews() {

        if (isLogin()) {
            if (MyApplication.getInstance().isLogin()) {
                finish();
                MyApplication.isShowLoginPage = true;
                com.cargps.android.model.activity.LoginActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        }

        titleBar.initView(getWindow().getDecorView().getRootView(), true);
        titleBar.addTitleCallBack(this);

        //com.cargps.android.model.activity.LoginActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();

        //finish();

    }


    /***
     * 返回按钮
     */
    @Override
    public void backClick(ImageView backImg) {
        finish();
    }


    /***
     * 右边按钮
     */
    @Override
    public void rightClick(ImageView rightImg) {

    }

    public void setTitleText(String title) {
        if (titleBar != null) {
            titleBar.setTitle(title);
        }
    }


    public void findEditTextEnable(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view instanceof ViewGroup) {
                findEditTextEnable((ViewGroup) view);
            } else if (view instanceof EditText) {
                ((EditText) view).setEnabled(false);
            }
        }
    }


    @Override
    public void leftImg(ImageView backImg) {
        // TODO Auto-generated method stub

    }


    @Override
    public void rightImg(ImageView rightImg) {
        // TODO Auto-generated method stub

    }


    @Override
    public void rightTv(TextView rightTv) {
        // TODO Auto-generated method stub

    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        JPushInterface.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        JPushInterface.onPause(this);
    }


    @Override
    public boolean isLogin() {
        // TODO Auto-generated method stub
        return false;
    }

}
