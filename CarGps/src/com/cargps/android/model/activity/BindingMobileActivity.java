package com.cargps.android.model.activity;

import android.widget.EditText;

import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.ConsumeOrderResponse;
import com.cargps.android.utils.IDCard;
import com.fu.baseframe.utils.SystemOpt;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_bingding_mobile_layout)
public class BindingMobileActivity extends BaseActivity {
    @ViewById
    public EditText mobileEt, idcareEt;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();
        setTitleText("绑定手机号");
    }

    @Click
    public void submitBtn() {
        if (app.isLogin()) return;

        String mobile = mobileEt.getText().toString();
        String idCare = idcareEt.getText().toString();

        if (mobile.isEmpty()) {
            showToast("手机号不能为空！");
            return;
        }
        try {
            String error = IDCard.IDCardValidate(idCare);
            if (!error.equals("")) {
                showToast(error);
                return;
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String urlStr = MyContacts.MAIN_URL + "v1.0/rootUserInfo/updateMobile";

        Map<String, String> param = new HashMap<String, String>();
        param.put("identityNo", idCare);
        param.put("newMobileNo", mobile);
        HttpRequest<ConsumeOrderResponse> httpRequest = new HttpRequest<ConsumeOrderResponse>(this, urlStr, new HttpResponseListener<ConsumeOrderResponse>() {

            @Override
            public void onResult(ConsumeOrderResponse result) {

                if (result != null && result.statusCode == 200) {
                    finish();
                } else {
                    showToast(result.message);
                }
            }

            @Override
            public void onFail(int code) {
            }
        }, ConsumeOrderResponse.class, param, "POST", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);
        HttpExecute.getInstance().addRequest(httpRequest);
    }

    @Override
    public boolean isLogin() {
        // TODO Auto-generated method stub
        return true;
    }
}
