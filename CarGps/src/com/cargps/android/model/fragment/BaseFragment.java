package com.cargps.android.model.fragment;

import android.os.Bundle;
import android.view.View;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.poisearch.PoiSearch.OnPoiSearchListener;
import com.cargps.android.MyApplication;
import com.cargps.android.interfaces.IViewPoiBack;
import com.cargps.android.utils.TitleBar;
import com.fu.baseframe.base.BaseFrameFragment;

import java.util.List;

public class BaseFragment extends BaseFrameFragment implements IViewPoiBack, OnPoiSearchListener {
    public TitleBar titleBar = new TitleBar();
    public MyApplication app;
    private PoiSearch poiSearch;// POI搜索
    private PoiSearch.Query query;// Poi查询条件类
    public String city = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = MyApplication.getInstance();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        titleBar.initView(getView(), false);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onPoiResult(int code, List<PoiItem> poiList, String msg) {

    }

    public void serchPOI(String addr) {
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
                    onPoiResult(1000, poiList, "");
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
        onPoiResult(1001, null, "没有搜索到。。。");
    }

    public void geoAddr(LatLonPoint latLonPoint) {
        GeocodeSearch geocoderSearch = new GeocodeSearch(getActivity());
        geocoderSearch.setOnGeocodeSearchListener(new OnGeocodeSearchListener() {

            @Override
            public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
                if (rCode == 1000) {
                    if (result != null && result.getRegeocodeAddress() != null
                            && result.getRegeocodeAddress().getFormatAddress() != null) {
                        String addressName = result.getRegeocodeAddress().getFormatAddress();
                        currAddr(addressName);
                    } else {
                    }
                } else {
                }
            }

            @Override
            public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
                // TODO Auto-generated method stub

            }
        });

        // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系

        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);

        geocoderSearch.getFromLocationAsyn(query);
    }

    public void currAddr(String addr) {
    }

}
