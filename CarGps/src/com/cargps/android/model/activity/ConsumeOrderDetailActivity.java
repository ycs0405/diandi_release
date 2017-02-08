package com.cargps.android.model.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.data.ConsumeOrder;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.AlipayResponse;
import com.fu.baseframe.utils.SystemOpt;
import com.google.gson.Gson;
import com.pingplusplus.android.Pingpp;

import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

@EActivity(R.layout.activity_consume_order_detail_layout)
public class ConsumeOrderDetailActivity extends BaseActivity {
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";
    @ViewById
    public TextView showMoenyTv, runTimeTv, runDateTv;
    @ViewById
    CheckBox alipayCb, weiXinCb;
    @ViewById
    public Button payBtn;

    private String channel = CHANNEL_ALIPAY;

    public double money;

    private String payType;

    @Extra
    public ConsumeOrder consumeOrder;

    @Override
    public void initViews() {
        super.initViews();
        setTitleText("账单详情");

        alipayCb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alipayCb.setChecked(true);
                weiXinCb.setChecked(false);

                channel = CHANNEL_ALIPAY;
            }
        });

        weiXinCb.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                weiXinCb.setChecked(true);
                alipayCb.setChecked(false);
                channel = CHANNEL_WECHAT;
            }
        });
    }

    @Click
    public void payBtn() {

        money = money * 100f;
        int m = (int) money;
        PaymentRequest paymentRequest = new PaymentRequest(channel, m, payType);
        Map<String, String> param = new HashMap<String, String>();
        param.put("data", new Gson().toJson(paymentRequest));
        HttpRequest<AlipayResponse> httpRequest = new HttpRequest<AlipayResponse>(this, MyContacts.MAIN_URL + "v1.0/payment/getCharge", new HttpResponseListener<AlipayResponse>() {

            @Override
            public void onResult(AlipayResponse result) {
                if (result != null && result.statusCode == 200) {
                    Pingpp.createPayment(ConsumeOrderDetailActivity.this, new Gson().toJson(result.data), "qwalletXXXXXXX");
                }

            }

            @Override
            public void onFail(int code) {

            }
        }, AlipayResponse.class, param, "POST", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);

        HttpExecute.getInstance().addRequest(httpRequest);
    }

    class PaymentRequest {
        String channel;
        int amount;
        String payType;

        public PaymentRequest(String channel, int amount, String payType) {
            this.channel = channel;
            this.amount = amount;
            this.payType = payType;
        }
    }

    @Override
    public boolean isLogin() {
        // TODO Auto-generated method stub
        return true;
    }
}
