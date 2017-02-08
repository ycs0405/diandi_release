package com.cargps.android.model.activity;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.CoordinateConverter;
import com.amap.api.maps2d.CoordinateConverter.CoordType;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.maps2d.model.PolylineOptions;
import com.cargps.android.MyApplication;
import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.data.ConsumeOrder;
import com.cargps.android.data.PathInfo;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.PathResponse;
import com.fu.baseframe.utils.LogUtils;
import com.fu.baseframe.utils.SystemOpt;
import com.widget.android.utils.ConvertUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EActivity(R.layout.activity_run_path_layout)
public class RunPathActivity extends BaseActivity implements LocationSource, AMapLocationListener, OnCameraChangeListener {


    @ViewById
    TextView priceTv, dateTv, timeTv;

    @Extra
    public ConsumeOrder order;

    @ViewById
    MapView mMapView = null;
    AMap aMap;

    float zoomLv = 12;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    private OnLocationChangedListener mListener;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    Bundle savedInstanceState;

    public boolean isGetCatList = false;

    public static RunPathActivity myEleCarFragment;

    OnMarkerClickListener markerListener = new OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {
            marker.showInfoWindow();
            return true;
        }
    };

    OnClickListener zoomMaxClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            zoomLv = aMap.getCameraPosition().zoom;
            zoomLv += 0.2f;

            if (zoomLv > aMap.getMaxZoomLevel()) {
                zoomLv = aMap.getMaxZoomLevel();
            }

            aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLv));
        }
    };

    OnClickListener zoomMinClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

            zoomLv = aMap.getCameraPosition().zoom;
            zoomLv -= 0.2f;
            if (zoomLv < aMap.getMinZoomLevel()) {
                zoomLv = aMap.getMinZoomLevel();
            }
            aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLv));
        }
    };

    public void onCreate(Bundle savedInstanceState) {
        // 在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，实现地图生命周期管理
        super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
    }

    ;


    @Override
    @AfterViews
    public void initViews() {
        super.initViews();
        setTitleText("骑行轨迹");

        if (order != null) {
            priceTv.setText("￥" + order.consume + "元");
            dateTv.setText(order.endTime);
            timeTv.setText(order.minutes + "分钟");
        }
        aMap = mMapView.getMap();

        mMapView.onCreate(savedInstanceState);

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.icon_my_gps));// 设置小蓝点的图标

        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
        aMap.setOnMarkerClickListener(markerListener);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setOnMapLoadedListener(new OnMapLoadedListener() {

            @Override
            public void onMapLoaded() {
                UiSettings uiSettings = aMap.getUiSettings();
                uiSettings.setCompassEnabled(true);
                uiSettings.setMyLocationButtonEnabled(false);
                uiSettings.setZoomControlsEnabled(false);
                uiSettings.setScaleControlsEnabled(false);

            }
        });

        aMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
            }
        });


    }

    OnInfoWindowClickListener onInfoWindowClickListener = new OnInfoWindowClickListener() {

        @Override
        public void onInfoWindowClick(Marker arg0) {
            //showChargInfoWindow(arg0);
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        // 在activity执行onResume时执行mMapView.onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        // 在activity执行onPause时执行mMapView.onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // 在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState
        // (outState)，实现地图生命周期管理
        mMapView.onSaveInstanceState(outState);
    }


    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                if (!isGetCatList) {
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLv));
                    isGetCatList = true;
                    getListPath();
                }
                //app.mainActivity.city = amapLocation.getCity();
                //app.mainActivity.setTitleText(app.mainActivity.city);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        startPostion();
    }


    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    public void getListPath() {

        if (app.isLogin()) return;

        Map<String, String> params = new HashMap<String, String>();
        params.put("orderId", order.orderId);
        HttpRequest<PathResponse> httpRequest = new HttpRequest<PathResponse>(MyApplication.mainActivity, MyContacts.MAIN_URL + "v1.0/ebike/tracking", new HttpResponseListener<PathResponse>() {

            @Override
            public void onResult(PathResponse result) {
                if (result != null && result.data != null) {
                    if (aMap == null) return;

                    if (result.data.isEmpty()) {
                        showToast("没有数据!");
                        return;
                    }

                    PathInfo start = result.data.get(0);
                    PathInfo stop = result.data.get(result.data.size() - 1);


                    LatLng latLngStart = new LatLng(start.pgLatitude, start.pgLongitude);

                    if (latLngStart.latitude > 0 && latLngStart.longitude > 0) {
                        CoordinateConverter converter = new CoordinateConverter();
                        // CoordType.GPS 待转换坐标类型
                        converter.from(CoordType.GPS);
                        // sourceLatLng待转换坐标点 DPoint类型
                        converter.coord(latLngStart);
                        // 执行转换操作
                        LatLng desLatLngStart = converter.convert();

                        aMap.moveCamera(CameraUpdateFactory.newLatLng(desLatLngStart));
                        aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLv));

                        MarkerOptions optionsStart = new MarkerOptions();
                        optionsStart.title("");//title不设infowindow不显示
                        optionsStart.position(desLatLngStart).
                                icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap("", R.drawable.icon_start)));
                        aMap.addMarker(optionsStart);
                    }


                    LatLng latLngStop = new LatLng(stop.pgLatitude, stop.pgLongitude);

                    if (latLngStop.latitude > 0 && latLngStop.longitude > 0) {
                        CoordinateConverter converter = new CoordinateConverter();
                        // CoordType.GPS 待转换坐标类型
                        converter.from(CoordType.GPS);
                        // sourceLatLng待转换坐标点 DPoint类型
                        converter.coord(latLngStop);
                        // 执行转换操作
                        LatLng desLatLngStop = converter.convert();

                        MarkerOptions optionsStop = new MarkerOptions();
                        optionsStop.title("");//title不设infowindow不显示
                        optionsStop.position(desLatLngStop).
                                icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap("", R.drawable.icon_stop)));
                        aMap.addMarker(optionsStop);
                    }


                    List<LatLng> listPath = new ArrayList<LatLng>();

                    for (PathInfo pathInfo : result.data) {
                        LatLng latLng = new LatLng(pathInfo.pgLatitude, pathInfo.pgLongitude);

                        if (latLng.latitude <= 0 || latLng.longitude <= 0) {
                            continue;
                        }
                        CoordinateConverter converter = new CoordinateConverter();
                        // CoordType.GPS 待转换坐标类型
                        converter.from(CoordType.GPS);
                        // sourceLatLng待转换坐标点 DPoint类型
                        converter.coord(latLng);
                        // 执行转换操作
                        LatLng desLatLng = converter.convert();

                        listPath.add(desLatLng);
                    }

//	               Polyline polyline = 
                    aMap.addPolyline(new PolylineOptions().addAll(listPath).width(ConvertUtils.dip2px(RunPathActivity.this, 10f)).color(0x0bca45));
                }

            }

            @Override
            public void onFail(int code) {
                // TODO Auto-generated method stub

            }
        }, PathResponse.class, params, "POST", true);

        httpRequest.addHead("mobileNo", MyApplication.getInstance().rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", MyApplication.getInstance().rootUserInfo.accessToken);

        HttpExecute.getInstance().addRequest(httpRequest);

    }

    protected Bitmap getMyBitmap(String pm_val, int imgRes) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imgRes);
        Bitmap newbitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(newbitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);

        TextPaint textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(sp2px(12f));
        textPaint.setColor(Color.WHITE);

        Rect rect = new Rect();
        textPaint.getTextBounds(pm_val, 0, pm_val.length(), rect);

        int txtWidth = Math.abs(rect.right - rect.left);
        int txtHeight = Math.abs(rect.bottom - rect.top);


        canvas.drawText(pm_val, (bitmap.getWidth() - txtWidth) / 2,
                (bitmap.getHeight() + txtHeight) / 2 - (imgRes == R.drawable.icon_marker ? txtHeight / 4 * 2 : txtHeight / 4 * 3), textPaint);// 设置bitmap上面的文字位置
        return newbitmap;
    }

    public void startPostion() {
        if (mLocationClient == null) {
            mLocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);

            //获取一次定位结果：
            //该方法默认为false。
            mLocationOption.setOnceLocation(true);


//			mLocationOption.setInterval(3000);

            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mLocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mLocationClient.startLocation();
        }
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    private float sp2px(float spValue) {
        float fontScale = getResources().getDisplayMetrics().density;
        return (spValue * fontScale + 0.5f);
    }


    @Override
    public void onCameraChange(CameraPosition arg0) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onCameraChangeFinish(CameraPosition arg0) {
        LogUtils.logError("my ele map change");
    }

    @Override
    public boolean isLogin() {
        // TODO Auto-generated method stub
        return true;
    }


}
