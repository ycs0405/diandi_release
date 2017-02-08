package com.cargps.android.model.activity;

import android.content.Intent;
import android.widget.TextView;

import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.data.ConsumeOrder;
import com.cargps.android.data.OrderDetail;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.OrderDetailResponse;
import com.fu.baseframe.utils.SystemOpt;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_order_detail_layout)
public class OrderDetailActivity extends BaseActivity {
    @ViewById
    TextView orderIdTv, consumeTv, startTimeTv, endTimeTv, runTimeTv, carTypeTv, priceTv;

    @Extra
    public ConsumeOrder order;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();
        setTitleText("订单详情");

        getData();

    }

    @Click
    public void pathImg() {
        if (order == null) return;
        com.cargps.android.model.activity.RunPathActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).
                order(order).start();
    }

    @Override
    public boolean isLogin() {
        return true;
    }

    private void getData() {
        if (app.isLogin()) return;

        String url = String.format(MyContacts.MAIN_URL + "v1.0/payment/getOrderInfo/%s", order.orderId);

        HttpRequest<OrderDetailResponse> httpRequest = new HttpRequest<OrderDetailResponse>(this, url, new HttpResponseListener<OrderDetailResponse>() {

            @Override
            public void onResult(OrderDetailResponse result) {
                if (result != null && result.data != null) {
                    OrderDetail orderDetail = result.data;
                    orderIdTv.setText(orderDetail.orderNo);
                    consumeTv.setText(orderDetail.consume + "元");
                    startTimeTv.setText(orderDetail.startTime);
                    endTimeTv.setText(orderDetail.endTime);
                    runTimeTv.setText(orderDetail.minutes + "分钟");
                    carTypeTv.setText(orderDetail.type);
                    priceTv.setText(orderDetail.price);
                }

            }

            @Override
            public void onFail(int code) {
                // TODO Auto-generated method stub

            }
        }, OrderDetailResponse.class, null, "GET", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);

        HttpExecute.getInstance().addRequest(httpRequest);
    }
}
