package com.cargps.android.model.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import com.cargps.android.MyApplication;
import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.data.ConsumeOrder;
import com.cargps.android.data.PaymentRequest;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.AlipayResponse;
import com.fu.baseframe.utils.LogUtils;
import com.fu.baseframe.utils.SystemOpt;
import com.google.gson.Gson;
import com.pingplusplus.android.Pingpp;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.Map;

/***
 * 充值
 *
 * @author fu
 */
@EActivity(R.layout.activity_topup_layout)
public class PayActivity extends BaseActivity {
    /**
     * 微信支付渠道
     */
    private static final String CHANNEL_WECHAT = "wx";
    /**
     * 支付支付渠道
     */
    private static final String CHANNEL_ALIPAY = "alipay";


    @ViewById
    CheckBox alipayCb, weiXinCb;
    @ViewById
    public Button payBtn;
    @ViewById
    public TextView payHintTv, startTimeTv, stopTimeTv, sumTimeTv, showMoenyTv;
    @ViewById
    public View timeLayout;

    private String payType;

    private String channel = CHANNEL_ALIPAY;
    @Extra
    public double money;

    @Extra
    public boolean yajin;

    @Extra
    public ConsumeOrder order;
    @Extra
    public String code;
    private HttpRequest<AlipayResponse> httpRequest;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();
        Pingpp.enableDebugLog(true);
        if (yajin) {
            setTitleText("支付押金");
            payHintTv.setText("支付押金");
            timeLayout.setVisibility(View.GONE);
            payType = "1";
        } else {
            setTitleText("支付金额");
            payType = "2";
            payHintTv.setText("支付金额");
        }

        if (order != null) {
            startTimeTv.setText(order.startTime);
            stopTimeTv.setText(order.endTime);
            sumTimeTv.setText(order.minutes + "分钟");
            money = order.consume;
        }

        showMoenyTv.setText("￥" + money + "元");

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

        if (channel == CHANNEL_ALIPAY) {

        }
        double payNumber = money;

        payNumber = payNumber * 100f;
        int m = (int) payNumber;
        PaymentRequest paymentRequest = new PaymentRequest(channel, m, payType);
        Map<String, String> param = new HashMap<String, String>();
        param.put("data", new Gson().toJson(paymentRequest));
        HttpRequest<AlipayResponse> httpRequest = new HttpRequest<AlipayResponse>(this, MyContacts.MAIN_URL + "v1.0/payment/getCharge", new HttpResponseListener<AlipayResponse>() {

            @Override
            public void onResult(AlipayResponse result) {
                if (result != null && result.statusCode == 200) {
                    Pingpp.createPayment(PayActivity.this, new Gson().toJson(result.data), "qwalletXXXXXXX");
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

    /**
     * onActivityResult 获得支付结果，如果支付成功，服务器会收到ping++ 服务器发送的异步通知。
     * 最终支付成功根据异步通知为准
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //支付页面返回处理
        if (requestCode == Pingpp.REQUEST_CODE_PAYMENT) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getExtras().getString("pay_result");
                if (result.equals("success")) {
                    if (yajin) {
                        Intent intent = new Intent(this, CarCantrolActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        if (!TextUtils.isEmpty(code)) {
                            MyApplication.INPUT_IMEI = code;
                        }
                    }
                    showToast("支付成功!");
                    finish();
                } else {
                    if (yajin) {
                        showToast("押金支付失败，请重试。");
                    } else {
                        showToast("租金支付失败，请重试。");
                    }

                }
                LogUtils.logDug(result);
                /* 处理返回值
                 * "success" - payment succeed
                 * "fail"    - payment failed
                 * "cancel"  - user canceldk
                 * "invalid" - payment plugin not installed
                 */
//                String errorMsg = data.getExtras().getString("error_msg"); // 错误信息
//                String extraMsg = data.getExtras().getString("extra_msg"); // 错误信息
            }
        }
    }

    /*class PaymentRequest {
        String channel;
        int amount;
        String payType;

        public PaymentRequest(String channel, int amount, String payType) {
            this.channel = channel;
            this.amount = amount;
            this.payType = payType;
        }
    }*/

    @Override
    public boolean isLogin() {
        // TODO Auto-generated method stub
        return true;
    }
}
