package com.cargps.android.model.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.data.MsgInfo;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.MsgResponse;
import com.fu.baseframe.utils.SystemOpt;
import com.widget.android.interfaces.OnRefreshFooterListener;
import com.widget.android.interfaces.OnRefreshHeadListener;
import com.widget.android.view.CustomScrollView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EActivity(R.layout.activity_msg_layout)
public class MsgActivity extends BaseActivity implements OnRefreshFooterListener, OnRefreshHeadListener {
    @ViewById
    public CustomScrollView sv;

    @ViewById
    public ListView listView;

    public MyAdapter adapter;

    List<MsgInfo> list;

    @Override
    @AfterViews
    public void initViews() {
        super.initViews();

        setTitleText("消息");

        //sv.setFootView(true);
        //sv.setOnRefreshFooterListener(this);
        sv.setOnRefreshHeadListener(this);
        sv.setModelName(getClass().getName());
        listView.setAdapter(adapter = new MyAdapter());
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });


        sv.startDownRefresh();

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
                convertView = View.inflate(MsgActivity.this, R.layout.item_msg_layout, null);
                h.msgTv = (TextView) convertView.findViewById(R.id.msgTv);
                h.dateTv = (TextView) convertView.findViewById(R.id.dateTv);
                convertView.setTag(h);
            } else {
                h = (Holder) convertView.getTag();
            }

            MsgInfo msgInfo = list.get(position);
            h.msgTv.setText(msgInfo.record);
            h.dateTv.setText(msgInfo.date);

            return convertView;
        }

    }

    class Holder {
        public TextView msgTv, dateTv;
    }

    public void getData() {
        if (app.isLogin()) return;

        String urlStr = MyContacts.MAIN_URL + "v1.0/userInfo/getMessage";

        HttpRequest<MsgResponse> httpRequest = new HttpRequest<MsgResponse>(this, urlStr, new HttpResponseListener<MsgResponse>() {

            @Override
            public void onResult(MsgResponse result) {
                sv.pullShow();
                if (result.statusCode == 200) {
                    if (result != null && !result.data.isEmpty()) {
                        list = result.data;
                        adapter.notifyDataSetChanged();
                    }

                } else {
                    showToast(result.message);
                }
            }

            @Override
            public void onFail(int code) {
                sv.pullShow();
            }
        }, MsgResponse.class, null, "GET", true);

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
    }

    @Override
    public boolean isLogin() {
        return true;
    }
}
