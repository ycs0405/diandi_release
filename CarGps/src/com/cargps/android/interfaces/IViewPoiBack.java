package com.cargps.android.interfaces;

import com.amap.api.services.core.PoiItem;

import java.util.List;

public interface IViewPoiBack {
    public void onPoiResult(int code, List<PoiItem> poiList, String msg);
}
