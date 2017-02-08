package com.cargps.android.model.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.cargps.android.MyApplication;
import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.model.fragment.AllEleCarFrament;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.UnlockInfoResponse;
import com.fu.baseframe.utils.SystemOpt;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_input_imei_layout)
public class InputImeiActivity extends BaseActivity {

    @ViewById
    public EditText imeiEt;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();

        setTitleText("输入编码");

        imeiEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (imeiEt.getText().length() == 6) {
                    app.HideKeyboard(imeiEt);
                    unlocakData(imeiEt.getText().toString());
                    imeiEt.setText("");
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    public void unlocakData(final String code) {

        if (app.isLogin()) return;

        AMapLocation location = AllEleCarFrament.getInstance().getAmapLocationCurr();
        if (location == null) {
            Toast.makeText(this, "正在定位。。。。", Toast.LENGTH_SHORT).show();
            return;
        }
        String urlStr = MyContacts.MAIN_URL + "v1.0/ebike/scanBike";

        Map<String, String> param = new HashMap<String, String>();
        param.put("imeiIdOrDeviceId", code);
        param.put("longitude", location.getLongitude() + "");
        param.put("latitude", location.getLatitude() + "");
        HttpRequest<UnlockInfoResponse> httpRequest = new HttpRequest<UnlockInfoResponse>(this, urlStr, new HttpResponseListener<UnlockInfoResponse>() {

            @Override
            public void onResult(UnlockInfoResponse result) {

                if (result != null && result.statusCode == 200) {
                    MyApplication.INPUT_IMEI = code;
                    Intent intent = new Intent(MyApplication.mainActivity, CarCantrolActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else if (result != null && result.statusCode == 403) {
                    showToast("租车需要押金，请支付押金才能租车。");
                    finish();
                    com.cargps.android.model.activity.PayActivity_.intent(InputImeiActivity.this).yajin(true).money(result.data).code(code).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();

                } else {
                    Toast.makeText(InputImeiActivity.this, result.message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(int code) {

            }
        }, UnlockInfoResponse.class, param, "POST", true);

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
