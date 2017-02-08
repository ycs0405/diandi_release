package com.cargps.android.model.activity;

import android.content.Intent;
import android.widget.TextView;

import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.BaseResponse;
import com.cargps.android.net.responseBean.UserInfoResponse;
import com.fu.baseframe.utils.SystemOpt;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_refund_money_layout)
public class RefundMoneyActivity extends BaseActivity {
    @ViewById
    TextView showMoenyTv;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();

        if (app.rootUserInfo != null) {
            if (app.rootUserInfo.account != null) {
                showMoenyTv.setText(app.rootUserInfo.account.deposit + "元");
                if (app.rootUserInfo.account.deposit <= 0) {
                    com.cargps.android.model.activity.PayActivity_.intent(RefundMoneyActivity.this).yajin(true).money(app.rootUserInfo.account.requireDeposit).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
                    finish();
                }
            }
        }

    }


    @Click
    public void submitBtn() {
        if (app.isLogin()) return;

        String urlStr = MyContacts.MAIN_URL + "v1.0/payment/refund";

        Map<String, String> param = new HashMap<String, String>();

        HttpRequest<UserInfoResponse> httpRequest = new HttpRequest<UserInfoResponse>(this, urlStr, new HttpResponseListener<UserInfoResponse>() {

            @Override
            public void onResult(UserInfoResponse result) {

                if (result != null && (result.statusCode == 200 || result.statusCode == 201) && result.data != null) { //201 200 weixin
                    app.rootUserInfo.account.deposit = 0;
                    app.userInfo = result.data;
                    app.setLocalUserinfo(app.rootUserInfo);
                    app.setUserInfo(result.data);
                    showToast(result.message);
                    finish();
                } else {
                    showToast(result.message);
                }
            }

            @Override
            public void onFail(int code) {
            }
        }, BaseResponse.class, param, "POST", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);
        HttpExecute.getInstance().addRequest(httpRequest);
    }

    @Override
    public boolean isLogin() {
        return true;
    }
}
