package com.cargps.android.model.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.TextPaint;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.InfoWindowAdapter;
import com.amap.api.maps2d.AMap.OnCameraChangeListener;
import com.amap.api.maps2d.AMap.OnInfoWindowClickListener;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.AMapUtils;
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
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.amap.api.services.route.WalkStep;
import com.cargps.android.MyApplication;
import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.data.Bike;
import com.cargps.android.model.activity.CarCantrolActivity;
import com.cargps.android.model.activity.WalkTipsActivity;
import com.cargps.android.model.view.MyRouteStyle;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.BikesResponse;
import com.fu.baseframe.utils.LogUtils;
import com.fu.baseframe.utils.SystemOpt;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@EFragment(R.layout.fragment_all_ele_car_layout)
public class AllEleCarFrament extends BaseFragment implements LocationSource, AMapLocationListener, OnCameraChangeListener, OnRouteSearchListener {


    protected static final String TAG = "AllEleCarFrament";
    @ViewById
    ImageView zoomMaxImg, zoomMinImg, myLocationImg, carControlImg;
    @ViewById
    TextView currAddrTv, pricePerHalfHour, walk_mile, walk_time, walk_click;

    @ViewById
    LinearLayout ll_walk;


    private List<PoiItem> poiList;

    @ViewById
    MapView mMapView = null;
    AMap aMap;


    float zoomLv = 14;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    private OnLocationChangedListener mListener;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private AMapLocation amapLocationCurr;

    Marker currMarker = null;
    Bike tempBike = null;

    Bundle savedInstanceState;

    public boolean isGetCatList = false;

    public static AllEleCarFrament allEleCarFrament;

    private RouteSearch routeSearch;
    private WalkRouteResult mWalkRouteResultresult;
    private MyRouteStyle walkRouteOverlay;

    OnMarkerClickListener markerListener = new OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {
            if (walkRouteOverlay != null) {
                //移除路径
                walkRouteOverlay.removeFromMap();
                closeWalkTips();
            }
            if (!(marker.getObject() instanceof Bike)) return false;
            if (amapLocationCurr == null) return false;

            marker.showInfoWindow();
            // create route plan
            LatLonPoint from = new LatLonPoint(amapLocationCurr.getLatitude(), amapLocationCurr.getLongitude());
            LatLonPoint to = new LatLonPoint(marker.getPosition().latitude, marker.getPosition().longitude);
            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(from, to);
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, RouteSearch.WalkDefault);
            routeSearch.calculateWalkRouteAsyn(query);


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


    @AfterViews
    public void initViews() {

        allEleCarFrament = this;

        // 获取地图控件引用
        zoomMaxImg.setOnClickListener(zoomMaxClick);
        zoomMinImg.setOnClickListener(zoomMinClick);
        myLocationImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (amapLocationCurr != null) {
                    zoomLv = aMap.getCameraPosition().zoom;
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(amapLocationCurr.getLatitude(), amapLocationCurr.getLongitude())));
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLv));

                }
            }
        });

        carControlImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//				if(tempChrgInfo != null && tempMarker != null){
                Intent intent = new Intent(MyApplication.mainActivity, CarCantrolActivity.class);
//					intent.putExtra("carId", tempChrgInfo.chrg_vid);
//					intent.putExtra("iemi", tempChrgInfo.imei);
                startActivity(intent);
//				}
            }
        });


        aMap = mMapView.getMap();

        mMapView.onCreate(savedInstanceState);

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.icon_my_gps));// 设置小蓝点的图标

        aMap.setMyLocationStyle(myLocationStyle);
        aMap.setOnInfoWindowClickListener(onInfoWindowClickListener);
        aMap.setInfoWindowAdapter(infoWindowAdapter);
        aMap.setOnCameraChangeListener(this);
        aMap.setOnMarkerClickListener(markerListener);
        routeSearch = new RouteSearch(getActivity());
        routeSearch.setRouteSearchListener(this);

        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setOnMapLoadedListener(new OnMapLoadedListener() {

            @Override
            public void onMapLoaded() {
                UiSettings uiSettings = aMap.getUiSettings();
                uiSettings.setMyLocationButtonEnabled(false);
                uiSettings.setZoomControlsEnabled(false);
            }
        });

        aMap.setOnMapClickListener(new OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {
                hideWindow();
                //close walk tip
                if (walkRouteOverlay != null) {
                    //移除路径
                    walkRouteOverlay.removeFromMap();
                    closeWalkTips();
                }
            }
        });

        //	aMap.setOnMapTouchListener(new LeftTouch());

        walk_click.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (walkRouteOverlay != null) {
                    //移除路径
                    walkRouteOverlay.removeFromMap();
                    closeWalkTips();
                }
                if (!MyApplication.getInstance().isLogin()) {
                    Intent intent = new Intent(getActivity(), WalkTipsActivity.class);
                    intent.putExtra("walk_path", walkPath);
                    intent.putExtra("walk_result", mWalkRouteResultresult);
                    startActivity(intent);
                } else {
                    MyApplication.isShowLoginPage = true;
                    com.cargps.android.model.activity.LoginActivity_.intent(getActivity()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();

                }

            }
        });

    }

    OnInfoWindowClickListener onInfoWindowClickListener = new OnInfoWindowClickListener() {

        @Override
        public void onInfoWindowClick(Marker arg0) {
            //showChargInfoWindow(arg0);
        }
    };

    InfoWindowAdapter infoWindowAdapter = new InfoWindowAdapter() {

        @Override
        public View getInfoWindow(Marker marker) {

            View view = View.inflate(getActivity(), R.layout.car_info_window_layout, null);
            showChargInfoWindow(marker, view);
            return view;
        }

        @Override
        public View getInfoContents(Marker arg0) {

            return null;
        }
    };
    private WalkPath walkPath;


    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();

        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
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
        LogUtils.logDug("allEleFragment onLocationChanged");
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                amapLocationCurr = amapLocation;
                LatLonPoint latLonPoint = new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude());
                geoAddr(latLonPoint);
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                if (!isGetCatList) {
                    isGetCatList = true;
                    city = amapLocation.getCity();
                    getListCar();
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLv));
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
//			mLocationClient.stopLocation();
//			mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    public void getListCar() {

        Point point = new Point();
        point.x = 0;
        point.y = MyApplication.getInstance().heightPixels / 2;
        LatLng latLng2 = aMap.getProjection().fromScreenLocation(point);

        float radius = (AMapUtils.calculateLineDistance(aMap.getCameraPosition().target, latLng2)) / 1000f;
        LogUtils.logDug("getListCar()   radius = " + radius);
        Map<String, String> param = new HashMap<String, String>();

        param.put("longitude", String.valueOf(aMap.getCameraPosition().target.longitude));//经度
        param.put("latitude", String.valueOf(aMap.getCameraPosition().target.latitude));//纬度
        param.put("radius", String.valueOf(radius));

        HttpRequest<BikesResponse> httpRequest = new HttpRequest<BikesResponse>(getActivity(), MyContacts.MAIN_URL + "v1.0/geo/getBikes", new HttpResponseListener<BikesResponse>() {

            @Override
            public void onResult(BikesResponse result) {
                if (result != null && result.data != null) {
                    if (result.data.isEmpty()) return;


                    clearMarker();

                    for (Bike bike : result.data) {
                        LatLng latLng = new LatLng(bike.latitude, bike.longitude);

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

                        MarkerOptions options = new MarkerOptions();
                        options.title(bike.type);//title不设infowindow不显示
                        options.position(desLatLng).
                                icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(String.valueOf(bike.type), R.drawable.icon_marker)));
                        Marker marker = aMap.addMarker(options);
                        marker.setObject(bike);
                    }

                    if (poiList != null && !poiList.isEmpty()) {
                        PoiItem item = poiList.get(0);

                        LatLng latLng = new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());

                        if (latLng.latitude > 0 && latLng.longitude > 0) {
                            CoordinateConverter converter = new CoordinateConverter();
                            // CoordType.GPS 待转换坐标类型
                            converter.from(CoordType.GPS);
                            // sourceLatLng待转换坐标点 DPoint类型
                            converter.coord(latLng);
                            // 执行转换操作
                            LatLng desLatLng = converter.convert();

                            MarkerOptions options = new MarkerOptions();
                            options.title(item.getAdCode());//title不设infowindow不显示
                            options.position(desLatLng).
                                    icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap("", R.drawable.icon_marker)));
                            Marker marker = aMap.addMarker(options);
                            marker.setObject(item);
                        }


                    }
                }

            }

            @Override
            public void onFail(int code) {
                // TODO Auto-generated method stub

            }
        }, BikesResponse.class, param, "POST", false);

        httpRequest.addHead("mobileNo", "123456788594");
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", "");

        HttpExecute.getInstance().addRequest(httpRequest);

    }

    public void clearMarker() {
        List<Marker> markers = aMap.getMapScreenMarkers();
        for (Marker marker : markers) {
            if (marker.getObject() != null) {
                marker.remove();
            }
        }
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
            mLocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);

            //获取一次定位结果：
            //该方法默认为false。
            mLocationOption.setOnceLocation(false);


            mLocationOption.setInterval(3000);

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

    public void showChargInfoWindow(Marker marker, View view) {

        view.findViewById(R.id.infoTv).setVisibility(View.GONE);

        hideWindow();

        Bike bike = (Bike) marker.getObject();
        currMarker = marker;
        tempBike = bike;

        //marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(String.valueOf(bike.type), R.drawable.icon_marker_s)));
        StringBuffer sb = new StringBuffer();
        sb.append("车驾号:" + bike.deviceNo + "\n");
        sb.append("类型:" + bike.type + "\n");
        sb.append("电量:" + bike.battery + "\n");
        sb.append("经度:" + bike.longitude + "\n");
        sb.append("纬度:" + bike.latitude);
        TextView chargInfoTv = (TextView) view.findViewById(R.id.chargInfoTv);

        chargInfoTv.setText(sb.toString());

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
        getListCar();
        if (poiList != null && !poiList.isEmpty()) {

            List<Marker> markers = aMap.getMapScreenMarkers();
            for (Marker marker : markers) {
                if (marker.getObject() instanceof PoiItem) {
                    marker.remove();
                }
            }

            PoiItem item = poiList.get(0);
            MarkerOptions options = new MarkerOptions();
            options.title(item.getAdCode());//title不设infowindow不显示
            options.position(new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude())).
                    icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap("", R.drawable.icon_marker)));
            Marker marker = aMap.addMarker(options);
            marker.setObject(item);
        }
    }


    public void cancelOnMapChangeListener() {
        if (aMap != null) {
            aMap.setOnCameraChangeListener(null);
        }
    }

    public void addOnMapChangeListener() {
        if (aMap != null) {
            aMap.setOnCameraChangeListener(this);
        }
    }

    private void hideWindow() {
        if (currMarker != null && currMarker.isInfoWindowShown()) {

            currMarker.hideInfoWindow();
//			currMarker.remove();
//			
//			MarkerOptions options = new MarkerOptions();
//            options.title("");//title不设infowindow不显示
//            options.position( currMarker.getPosition()).
//            icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(String.valueOf(tempBike.type),R.drawable.icon_marker)));
//            Marker marker = aMap.addMarker(options);
//            marker.setObject(tempBike);

        }
    }


    public AMapLocation getAmapLocationCurr() {
        return amapLocationCurr;
    }

    public static AllEleCarFrament getInstance() {
        return allEleCarFrament;
    }

    @Override
    public void currAddr(String addr) {
        currAddrTv.setText(addr);
        if (MyEleCarFragment.myEleCarFragment != null) {
            MyEleCarFragment.myEleCarFragment.currAddr(addr);
        }

        if (ChargFargment.chargCarFrament != null) {
            ChargFargment.chargCarFrament.currAddr(addr);
        }
    }

    @Override
    public void onPoiResult(int code, List<PoiItem> poiList, String msg) {
        if (code == 1000) {
            PoiItem item = poiList.get(0);
            aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude())));
            aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLv));
            this.poiList = poiList;
        } else {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBusRouteSearched(BusRouteResult arg0, int arg1) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onDriveRouteSearched(DriveRouteResult arg0, int arg1) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onRideRouteSearched(RideRouteResult arg0, int arg1) {
        // TODO Auto-generated method stub

    }


    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int code) {
        dealWith(result, code);

    }


    private void dealWith(WalkRouteResult result, int rCode) {
        // TODO Auto-generated method stub
        Log.w(TAG, "onWalkRouteSearched rCode =" + rCode + ",result = " + result.toString());
        if (rCode == 1000) {
            if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
                walkPath = result.getPaths().get(0);
                //mile
                float distance = walkPath.getDistance();
                DecimalFormat df = new DecimalFormat("0.00");//格式化小数
                String dis = df.format(distance);
                //muni
                long duration = walkPath.getDuration();
                String time = df.format(duration / 60);
                //
                List<WalkStep> steps = walkPath.getSteps();
                for (int i = 0; i < steps.size(); i++) {
                }

                Log.w(TAG, "distance = " + dis + " 公里,duration = " + time + " 分钟,steps =" + steps.size());
                mWalkRouteResultresult = result;
                walkRouteOverlay = new MyRouteStyle(getActivity(), aMap, walkPath, result.getStartPos(), result.getTargetPos());
                walkRouteOverlay.setNodeIconVisibility(false);
                walkRouteOverlay.removeFromMap();
                walkRouteOverlay.addToMap();
                walkRouteOverlay.zoomToSpan();
                showWalkTips(dis + " 米", time + " 分钟");
            } else {
                Log.i(TAG, "对不起，没有搜索到相关数据！");
            }
        } else if (rCode == 27) {
            Log.i(TAG, "搜索失败,请检查网络连接！");
        } else if (rCode == 32) {
            Log.i(TAG, "key验证无效！");
        } else {
            Log.i(TAG, "未知错误，请稍后重试!错误码为" + rCode);
        }
    }

    public void closeWalkTips() {
        walk_click.setVisibility(View.GONE);
        ll_walk.setVisibility(View.GONE);
    }

    public void showWalkTips(String mi, String fenzhong) {
        if (!MyApplication.getInstance().isLogin()) {
            walk_click.setText("查看具体路线");
        } else {
            walk_click.setText("完成注册即可用车");
        }
        walk_mile.setText(mi);
        walk_time.setText(fenzhong);
        walk_click.setVisibility(View.VISIBLE);
        ll_walk.setVisibility(View.VISIBLE);
    }

}
