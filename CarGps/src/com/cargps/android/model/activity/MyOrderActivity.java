package com.cargps.android.model.activity;

import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.data.ConsumeOrder;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.ConsumeOrderResponse;
import com.fu.baseframe.utils.SystemOpt;
import com.widget.android.interfaces.OnRefreshFooterListener;
import com.widget.android.interfaces.OnRefreshHeadListener;
import com.widget.android.view.CustomScrollView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_order_layout)
public class MyOrderActivity extends BaseActivity implements OnRefreshFooterListener, OnRefreshHeadListener {
    @ViewById
    public CustomScrollView sv;

    @ViewById
    public ListView listView;

    public MyAdapter adapter;

    List<ConsumeOrder> list;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();

        setTitleText("账单");

        //sv.setFootView(true);
        //sv.setOnRefreshFooterListener(this);
        sv.setOnRefreshHeadListener(this);
        sv.setModelName(getClass().getName());
        listView.setAdapter(adapter = new MyAdapter());
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConsumeOrder order = (ConsumeOrder) adapter.getItem(position);
                if (order.orderStatus == 2) {
                    com.cargps.android.model.activity.PayActivity_.intent(MyOrderActivity.this).
                            flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).order(order).start();
                } else {
                    com.cargps.android.model.activity.OrderDetailActivity_.intent(MyOrderActivity.this).order(order).
                            flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!app.isLogin()) {
            sv.startDownRefresh();
        }
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder h = null;
            if (convertView == null) {
                h = new Holder();
                convertView = View.inflate(MyOrderActivity.this, R.layout.order_item_layout, null);
                h.showMoneyTv = (TextView) convertView.findViewById(R.id.showMoenyTv);
                h.statusTv = (TextView) convertView.findViewById(R.id.statusTv);
                h.runTimeTv = (TextView) convertView.findViewById(R.id.runTime);
                h.runDateTv = (TextView) convertView.findViewById(R.id.runDate);
                h.pathImg = (ImageView) convertView.findViewById(R.id.pathImg);
                convertView.setTag(h);
            } else {
                h = (Holder) convertView.getTag();
            }

            final ConsumeOrder consumeOrder = list.get(position);
            h.showMoneyTv.setText(consumeOrder.consume + "元");
            h.runTimeTv.setText("骑行时间：" + consumeOrder.minutes + "分");
            h.runDateTv.setText("骑行日期：" + consumeOrder.endTime);
            if (consumeOrder.orderStatus == 2) {
                h.statusTv.setText("待支付");
            } else {
                h.statusTv.setText("已支付");
            }
            h.pathImg.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    com.cargps.android.model.activity.RunPathActivity_.intent(MyOrderActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).
                            order(consumeOrder).start();
                }
            });
            return convertView;
        }

    }

    class Holder {
        public TextView showMoneyTv, statusTv, runTimeTv, runDateTv;
        public ImageView pathImg;
    }

    public void getData() {

        if (app.isLogin()) return;

        String urlStr = MyContacts.MAIN_URL + "v1.0/userInfo/getAccounts";

        Map<String, String> param = new HashMap<String, String>();
        HttpRequest<ConsumeOrderResponse> httpRequest = new HttpRequest<ConsumeOrderResponse>(this, urlStr, new HttpResponseListener<ConsumeOrderResponse>() {

            @Override
            public void onResult(ConsumeOrderResponse result) {

                if (result != null && result.statusCode == 200) {
                    sv.pullShow();
                    list = result.data;
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(MyOrderActivity.this, result.message, Toast.LENGTH_SHORT).show();
                    sv.pullShow();
                }
            }

            @Override
            public void onFail(int code) {
                sv.pullShow();
            }
        }, ConsumeOrderResponse.class, param, "GET", false);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);
        HttpExecute.getInstance().addRequest(httpRequest);
    }

    @Override
    public void headRefresh() {
        getData();
    }

    @Override
    public void refreshFooter() {
        getData();
    }

    @Override
    public boolean isLogin() {
        return true;
    }
}
