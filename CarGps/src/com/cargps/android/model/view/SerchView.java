package com.cargps.android.model.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.cargps.android.MyApplication;
import com.cargps.android.R;
import com.cargps.android.interfaces.IViewPoiBack;

import java.util.List;

/***
 * 搜索共用VIEW
 *
 * @author fu
 */
public class SerchView extends LinearLayout implements OnPoiSearchListener {
    public EditText addressEt;
    public String city;
    private PoiSearch poiSearch;// POI搜索
    private PoiSearch.Query query;// Poi查询条件类
    private IViewPoiBack poiBack;


    public SerchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public SerchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SerchView(Context context) {
        super(context);
        init();
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.serch_layout, this, true);
        addressEt = (EditText) findViewById(R.id.addressEt);

        addressEt.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    serchPOI();
                    return true;
                }
                return false;
            }
        });
    }

    private void serchPOI() {
        MyApplication.getInstance().HideKeyboard(addressEt);
        String addr = addressEt.getText().toString();
        if (TextUtils.isEmpty(addr)) return;
        addressEt.setText("");
        query = new PoiSearch.Query(addr, "", city);
        query.setPageSize(10);// 设置每页最多返回多少条poiitem
        query.setPageNum(0);// 设置查第一页
        query.setCityLimit(true);

        poiSearch = new PoiSearch(getContext(), query);
        poiSearch.setOnPoiSearchListener(this);
        poiSearch.searchPOIAsyn();
    }

    @Override
    public void onPoiItemSearched(PoiItem arg0, int arg1) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPoiSearched(PoiResult result, int code) {
        if (code == 1000) {
            if (result != null && result.getQuery() != null) {
                List<PoiItem> poiList = result.getPois();
                if (poiList != null && poiList.size() > 0) {
                    if (poiBack != null) {
                        poiBack.onPoiResult(1000, poiList, "");
                    }
                } else {
                    poiFail();
                }
            } else {
                poiFail();
            }
        } else {
            poiFail();
        }
    }


    public void poiFail() {
        poiBack.onPoiResult(1001, null, "没有搜索到。。。");
    }

    public void setPoiBack(IViewPoiBack poiBack) {
        this.poiBack = poiBack;
    }

}
