package com.cargps.android.model.activity;

import android.widget.EditText;

import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.BaseResponse;
import com.fu.baseframe.utils.SystemOpt;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_feedback_layout)
public class FeedBackActivity extends BaseActivity {
    @ViewById
    EditText contentEt;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();
        setTitleText("意见反馈");
    }


    @Click
    public void submitBtn() {
        if (app.isLogin()) return;

        app.HideKeyboard(contentEt);
        String content = contentEt.getText().toString();
        if (content.isEmpty()) {
            showToast("意见内容不能为空！");
            return;
        }

        String urlStr = MyContacts.MAIN_URL + "v1.0/userInfo/feedback";

        Map<String, String> param = new HashMap<String, String>();
        param.put("feedback", content);
        HttpRequest<BaseResponse> httpRequest = new HttpRequest<BaseResponse>(this, urlStr, new HttpResponseListener<BaseResponse>() {

            @Override
            public void onResult(BaseResponse result) {

                if (result != null && result.statusCode == 200) {
                    showToast("反馈成功！");
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
