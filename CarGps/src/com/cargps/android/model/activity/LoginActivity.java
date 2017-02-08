package com.cargps.android.model.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cargps.android.MyApplication;
import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.data.LoginUserInfo;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.BaseResponse;
import com.cargps.android.net.responseBean.LoginResponse;
import com.fu.baseframe.utils.SystemOpt;
import com.google.gson.Gson;
import com.widget.android.utils.SettingShareData;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

@EActivity(R.layout.activity_login_activity)
public class LoginActivity<T> extends BaseActivity {
    @ViewById
    public EditText phoneEt, codeEt;
    @ViewById
    public CheckBox xyCk;
    @ViewById
    public TextView readTv;
    public static final String TAG = "LoginActivity";
    public Timer timer;
    int time = 0;

    @ViewById
    public Button codeButton, loginBtn;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();
        setTitleText("登陆");
    }

    @Override
    public void leftImg(ImageView backImg) {
        backImg.setVisibility(View.GONE);
    }

    @Click
    public void codeButton() {
        String phone = phoneEt.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            showToast("手机号不能为空！");
            return;
        }
        getNetCode(phone, "abd");
        startTimer();
    }

    @Click
    public void loginBtn() {

        String phone = phoneEt.getText().toString();
        String code = codeEt.getText().toString();

        if (TextUtils.isEmpty(phone)) {
            showToast("手机号不能为空！");
            return;
        }

        if (TextUtils.isEmpty(code)) {
            showToast("验证码不能为空！");
            return;
        }

        if (!xyCk.isChecked()) {
            showToast("阅读并同意电滴服务协议！");
            return;
        }

        final String interfaceUrl = MyContacts.MAIN_URL + "v1.0/user/registerAndLogin";
        Map<String, String> param = new HashMap<String, String>();
        param.put("mobileNo", phone);
        param.put("pinCode", code);
        HttpRequest<LoginResponse> httpRequest = new HttpRequest<LoginResponse>(this, interfaceUrl, new HttpResponseListener<LoginResponse>() {


            @Override
            public void onFail(int code) {

            }

            @Override
            public void onResult(LoginResponse result) {
                if (null != result) {
                    if (result.statusCode == 200) {

                        loginBtn.setEnabled(false);
                        SettingShareData.getInstance(LoginActivity.this).setKeyValue("login_userInfo", new Gson().toJson(result.data));
                        LoginUserInfo info = app.getLocalUserinfo();
                        app.setPushAlias(info.mobileNo);
                        finish();

                        app.getUserInfoData();
                    }
                }
            }

        }, LoginResponse.class, param, "POST", true);

        httpRequest.addHead("mobileNo", phone);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());

        HttpExecute.getInstance().addRequest(httpRequest);


    }

    public void startTimer() {
        time = 30;
        codeButton.setEnabled(false);

        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        if (time < 0) {
                            stopTimer();
                            return;
                        }
                        codeButton.setText((time--) + "s");
                    }
                });
            }
        }, 0, 1000);
    }

    public void stopTimer() {
        codeButton.setEnabled(true);
        codeButton.setText("获取验证码");
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void getNetCode(String phone, String devId) {
        final String interfaceUrl = MyContacts.MAIN_URL + "v1.0/user/getPinCode";
        HttpRequest<BaseResponse> httpRequest = new HttpRequest<BaseResponse>(this, interfaceUrl, new HttpResponseListener<BaseResponse>() {


            @Override
            public void onFail(int code) {
                Log.w(TAG, "getNetCode == " + code);
            }

            @Override
            public void onResult(BaseResponse result) {
                Log.w(TAG, "getNetCode == " + result.message + " ;" + result.statusCode);
            }


        }, BaseResponse.class, null, "GET", false);

        httpRequest.addHead("mobileNo", phone);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());

        HttpExecute.getInstance().addRequest(httpRequest);


    }

    @Click
    public void readTv() {
        com.cargps.android.model.activity.WebViewActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).title("用户条款").url("fuwutiaokuan.html").start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApplication.isShowLoginPage = false;
        stopTimer();
    }
}
