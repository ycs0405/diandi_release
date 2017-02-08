package com.cargps.android.model.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.amap.api.location.AMapLocation;
import com.amap.api.services.help.Inputtips;
import com.amap.api.services.help.Inputtips.InputtipsListener;
import com.amap.api.services.help.InputtipsQuery;
import com.amap.api.services.help.Tip;
import com.cargps.android.R;
import com.cargps.android.data.HistoryList;
import com.cargps.android.model.fragment.AllEleCarFrament;
import com.google.gson.Gson;
import com.widget.android.utils.SettingShareData;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.activity_serich_layout)
public class SerichActivity extends BaseActivity implements InputtipsListener {
    public static final int ADDR_RESULT_CODE = 0X10101;
    public static final int ADDR_REQUEST_CODE = 0X10102;
    protected static final String TAG = "SerichActivity";

    @ViewById
    TextView myLocationTv;

    @ViewById
    ListView listView;

    @ViewById
    AutoCompleteTextView addressEt;

    List<String> historyList = new ArrayList<String>();

    MyAdapter adapter;
    private List<String> tipsName = new ArrayList<String>();
    private InputtipsQuery inputquery;
    private Inputtips inputTips;
    private View claerView;

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    @Override
    @AfterViews
    public void initViews() {
        super.initViews();
        addressEt.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    String addr = addressEt.getText().toString();
                    if (TextUtils.isEmpty(addr)) {
                        showToast("地址不能为空！");
                        return true;
                    }
                    app.HideKeyboard(addressEt);
                    historyList.clear();
                    loadHistoryData();
                    if (!existAddr(addr)) {
                        historyList.add(0, addr);
                    }
                    saveHistoryData();
                    Log.w(TAG, "onEditorAction  history =" + historyList.toString());
                    Intent data = new Intent();
                    data.putExtra("addr", addr);
                    setResult(ADDR_RESULT_CODE, data);
                    finish();
                    return true;
                }
                return false;
            }
        });
        //add ---------- start

        initPoiTips();

        //add ---------- end
        claerView = LayoutInflater.from(this).inflate(R.layout.serich_clear_layout, null);
        claerView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                clearHistoryData();
                adapter.notifyDataSetChanged();
            }
        });
        listView.addFooterView(claerView);
        listView.setAdapter(adapter = new MyAdapter());
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.w(TAG, "position = " + position);

                app.HideKeyboard(addressEt);
                String addr = (String) adapter.getItem(position);
                Log.w(TAG, "click addr = " + addr + ",position = " + position);
                Log.w(TAG, "onItemClick  history =" + historyList.toString());
                historyList.clear();
                loadHistoryData();
                if (!existAddr(addr)) {
                    historyList.add(0, addr);
                }

                saveHistoryData();
                adapter.notifyDataSetChanged();
                Log.w(TAG, "onItemClick  history =" + historyList.toString());
                Intent data = new Intent();
                data.putExtra("addr", addr);
                setResult(ADDR_RESULT_CODE, data);
                SerichActivity.this.finish();
            }

        });

        loadHistoryData();

        if (historyList.isEmpty()) {
            claerView.setVisibility(View.GONE);
        } else {
            claerView.setVisibility(View.VISIBLE);
        }

        if (AllEleCarFrament.allEleCarFrament.getAmapLocationCurr() != null) {
            AMapLocation al = AllEleCarFrament.allEleCarFrament.getAmapLocationCurr();
            myLocationTv.setText(al.getCity() + " " + al.getRoad());
        } else {
            myLocationTv.setText("正在定位....");
        }
    }

    private void initPoiTips() {
        // TODO Auto-generated method stub
        //第二个参数传入null或者“”代表在全国进行检索，否则按照传入的city进行检索
        addressEt.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                Log.w(TAG, "onTextChanged s = " + s.toString() + " ,start = " + start + ",before = " + before + ",count = " + count);
                /*if(addressEt.getText().toString().length() == 0){
                    historyList.removeAll(tipsName);
					adapter.notifyDataSetChanged();
				}else{
					getTips();
				}*/

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
                Log.w(TAG, "beforeTextChanged s = " + s.toString() + " ,start = " + start + ",count = " + count + ",after = " + after);
            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.w(TAG, "afterTextChanged s = " + s.toString());
                if (addressEt.getText().toString().length() == 0) {
                    historyList.clear();
                    loadHistoryData();
                    adapter.notifyDataSetChanged();
                    if (historyList.isEmpty()) {
                        claerView.setVisibility(View.GONE);
                    } else {
                        claerView.setVisibility(View.VISIBLE);
                    }
                } else {
                    getTips();
                }


            }
        });


    }

    protected void getTips() {
        String city = "";

        if (AllEleCarFrament.allEleCarFrament.getAmapLocationCurr() != null) {
            AMapLocation al = AllEleCarFrament.allEleCarFrament.getAmapLocationCurr();
            city = al.getCity();
        } else {
            myLocationTv.setText("正在定位....");
        }

        inputquery = new InputtipsQuery(addressEt.getText().toString(), city);
        inputquery.setCityLimit(true);//限制在当前城市
        inputTips = new Inputtips(SerichActivity.this, inputquery);
        inputTips.setInputtipsListener(this);
        inputTips.requestInputtipsAsyn();
    }

    @Click
    public void cancelTv() {
        finish();
    }

    @SuppressLint("NewApi")
    private void loadHistoryData() {
        String json = SettingShareData.getInstance(this).getKeyValueString("serich", "");
        if (!json.isEmpty()) {
            HistoryList hlist = new Gson().fromJson(json, HistoryList.class);
            if (hlist != null) {
                historyList = hlist.list;
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void saveHistoryData() {
        HistoryList hl = new HistoryList();
        hl.list = historyList;
        SettingShareData.getInstance(this).setKeyValue("serich", new Gson().toJson(hl));
    }

    private void clearHistoryData() {
        historyList.clear();
        adapter.notifyDataSetChanged();
        SettingShareData.getInstance(this).setKeyValue("serich", "");
    }

    private boolean existAddr(String addr) {
        if (historyList.size() == 0) return false;
        for (String str : historyList) {
            if (addr.equals(str)) {
                return true;
            }
        }
        return false;
    }

    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return historyList.size();
        }

        @Override
        public Object getItem(int position) {
            return historyList.get(position);
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
                convertView = LayoutInflater.from(SerichActivity.this).inflate(R.layout.item_serich_layout, null);
                h.addrNameTv = (TextView) convertView.findViewById(R.id.addrNameTv);
                convertView.setTag(h);
            } else {
                h = (Holder) convertView.getTag();
            }
            h.addrNameTv.setText(historyList.get(position));
            return convertView;
        }

    }

    class Holder {
        TextView addrNameTv;
    }


    @Override
    public void onGetInputtips(List<Tip> tips, int code) {
        // TODO Auto-generated method stub
        Log.w(TAG, "tips = " + tips.size() + ",code = " + code);
        historyList.removeAll(tipsName);
        if (code == 1000) {
            if (tips.size() == 0) {
                tipsName.clear();
            } else {
                tipsName.clear();
                for (Tip tip : tips) {
                    if (tip.getName() != null) {
                        tipsName.add(tip.getName());
                    }
                }
            }
        } else {
            //failed
            tipsName.clear();
        }
        historyList.clear();
        historyList.addAll(tipsName);
        adapter.notifyDataSetChanged();

        if (historyList.isEmpty()) {
            claerView.setVisibility(View.GONE);
        } else {
            claerView.setVisibility(View.VISIBLE);
        }
    }


}
