package com.cargps.android.model.view;

import android.content.Context;
import android.graphics.Color;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.WalkPath;

/**
 * Created by peter2 on 2017/1/21.
 */

public class MyRouteStyle extends WalkRouteOverlay {
    public int color = Color.rgb(83, 126, 220);
    public float width = 20f;

    public MyRouteStyle(Context context, AMap aMap, WalkPath walkPath, LatLonPoint latLonPoint, LatLonPoint latLonPoint1) {
        super(context, aMap, walkPath, latLonPoint, latLonPoint1);
    }

    @Override
    protected int getDriveColor() {
        return color;
    }

    @Override
    protected int getWalkColor() {
        return color;
    }

    @Override
    protected float getBuslineWidth() {
        // TODO Auto-generated method stub
        return width;
    }

    /**
     * 设置起点图标
     *
     * @return
     */
    @Override
    protected BitmapDescriptor getStartBitmapDescriptor() {
        return null;
    }

    /**
     * 设置终点图标
     *
     * @return
     */
    @Override
    protected BitmapDescriptor getEndBitmapDescriptor() {
        return null;
    }

    /**
     * 去掉起点 终点marker
     */
    @Override
    protected void addStartAndEndMarker() {
//        super.addStartAndEndMarker();
    }

    /**
     * 设置路途当中的图像
     *
     * @return
     */
    @Override
    protected BitmapDescriptor getWalkBitmapDescriptor() {
        return null;
    }

    /**
     * 是否显示站台图标
     *
     * @param b
     */
    @Override
    public void setNodeIconVisibility(boolean b) {
        super.setNodeIconVisibility(b);
    }
}
