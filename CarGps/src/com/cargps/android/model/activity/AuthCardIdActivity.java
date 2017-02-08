package com.cargps.android.model.activity;

import android.widget.EditText;

import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.LoginResponse;
import com.cargps.android.utils.IDCard;
import com.fu.baseframe.utils.SystemOpt;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_auth_card_id_layout)
public class AuthCardIdActivity extends BaseActivity {
    @ViewById
    EditText nameEt, idcareEt;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();
        setTitleText("身份认证");

        if (app.rootUserInfo != null) {
            nameEt.setText(app.rootUserInfo.realName);
            idcareEt.setText(app.rootUserInfo.idNo);
        }
    }

    @Click
    public void submitBtn() {

        if (app.isLogin()) return;

        String name = nameEt.getText().toString();
        String idCare = idcareEt.getText().toString();

        if (name.isEmpty()) {
            showToast("姓名不能为空！");
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
        String urlStr = MyContacts.MAIN_URL + "v1.0/userInfo/addIdentify";

        Map<String, String> param = new HashMap<String, String>();
        param.put("idNo", idCare);
        param.put("realName", name);
        HttpRequest<LoginResponse> httpRequest = new HttpRequest<LoginResponse>(this, urlStr, new HttpResponseListener<LoginResponse>() {

            @Override
            public void onResult(LoginResponse result) {

                if (result != null && result.statusCode == 200 && result.data != null) {
                    app.setLocalUserinfo(result.data);
                    finish();
                } else {
                    showToast(result.message);
                }
            }

            @Override
            public void onFail(int code) {
            }
        }, LoginResponse.class, param, "POST", true);

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
