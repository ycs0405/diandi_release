package com.cargps.android.model.fragment;

import android.content.Intent;
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
import android.widget.ImageView;
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
import com.cargps.android.MyApplication;
import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.data.Bike;
import com.cargps.android.interfaces.ILogin;
import com.cargps.android.model.activity.CarCantrolActivity;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.BikeResponse;
import com.fu.baseframe.utils.LogUtils;
import com.fu.baseframe.utils.SystemOpt;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.List;

@EFragment(R.layout.fragment_my_ele_car_layout)
public class MyEleCarFragment extends BaseFragment implements LocationSource, AMapLocationListener, OnCameraChangeListener, ILogin {


    @ViewById
    ImageView zoomMaxImg, zoomMinImg, myLocationImg, carControlImg;
    @ViewById
    TextView currAddrTv;
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

    public static MyEleCarFragment myEleCarFragment;

    OnMarkerClickListener markerListener = new OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {
            if (!(marker.getObject() instanceof Bike)) return false;
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


    @AfterViews
    public void initViews() {

        myEleCarFragment = this;

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
            }
        });

//		aMap.setOnMapTouchListener(new OnMapTouchListener() {
//			
//			@Override
//			public void onTouch(MotionEvent arg0) {
//				if(currMarker != null && currMarker.isInfoWindowShown()){
//					currMarker.setIcon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(String.valueOf(tempBike.type), R.drawable.icon_marker)));
//					currMarker.hideInfoWindow();
//					currMarker = null;
//				}
//			}
//		});


    }

    OnInfoWindowClickListener onInfoWindowClickListener = new OnInfoWindowClickListener() {

        @Override
        public void onInfoWindowClick(Marker arg0) {
            //showChargInfoWindow(arg0);
        }
    };

    InfoWindowAdapter infoWindowAdapter = new InfoWindowAdapter() {

        @Override
        public View getInfoWindow(Marker arg0) {
            View view = View.inflate(getActivity(), R.layout.car_info_window_layout, null);
            showChargInfoWindow(arg0, view);
            return view;
        }

        @Override
        public View getInfoContents(Marker arg0) {

            return null;
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        if (mLocationClient != null) {
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
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null && amapLocation.getErrorCode() == 0) {
                amapLocationCurr = amapLocation;
                LatLonPoint latLonPoint = new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude());
                geoAddr(latLonPoint);
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                if (!isGetCatList) {
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLv));
                    city = amapLocation.getCity();
                    isGetCatList = true;
                    getListCar();
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
//		if (mLocationClient != null) {
//			mLocationClient.stopLocation();
//			mLocationClient.onDestroy();
//		}
        mLocationClient = null;
    }

    public void getListCar() {

        if (MyApplication.getInstance().isLogin()) return;

        HttpRequest<BikeResponse> httpRequest = new HttpRequest<BikeResponse>(MyApplication.mainActivity, MyContacts.MAIN_URL + "v1.0/ebike/getEBikeInfo", new HttpResponseListener<BikeResponse>() {

            @Override
            public void onResult(BikeResponse result) {
                if (result != null && result.data != null) {
                    if (aMap == null) return;

                    List<Marker> markers = aMap.getMapScreenMarkers();
                    for (Marker marker : markers) {
                        if (marker.getObject() != null) {
                            marker.remove();
                        }
                    }

                    LatLng latLngtemp = new LatLng(result.data.latitude, result.data.longitude);

                    if (latLngtemp.latitude > 0 && latLngtemp.longitude > 0) {
                        CoordinateConverter converter = new CoordinateConverter();
                        // CoordType.GPS 待转换坐标类型
                        converter.from(CoordType.GPS);
                        // sourceLatLng待转换坐标点 DPoint类型
                        converter.coord(latLngtemp);
                        // 执行转换操作
                        LatLng desLatLng = converter.convert();

                        MarkerOptions options = new MarkerOptions();
                        options.title("");//title不设infowindow不显示
                        options.position(desLatLng).
                                icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(String.valueOf(result.data.type), R.drawable.icon_marker)));
                        Marker marker = aMap.addMarker(options);
                        marker.setObject(result.data);
                    }


                    if (poiList != null && !poiList.isEmpty()) {
                        PoiItem item = poiList.get(0);

                        LatLng latLng = new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());

                        if (latLng.latitude == 0 || latLng.longitude == 0) {
                            return;
                        }

                        if (latLng.latitude < 0 || latLng.longitude < -1) {
                            return;
                        }

                        CoordinateConverter converter = new CoordinateConverter();
                        // CoordType.GPS 待转换坐标类型
                        converter.from(CoordType.GPS);
                        // sourceLatLng待转换坐标点 DPoint类型
                        converter.coord(latLng);
                        // 执行转换操作
                        LatLng desLatLng = converter.convert();

                        MarkerOptions options1 = new MarkerOptions();
                        options1.title(item.getAdCode());//title不设infowindow不显示
                        options1.position(desLatLng).
                                icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap("", R.drawable.icon_marker)));
                        Marker marker1 = aMap.addMarker(options1);
                        marker1.setObject(item);
                    }

                }

            }

            @Override
            public void onFail(int code) {
                // TODO Auto-generated method stub

            }
        }, BikeResponse.class, null, "GET", false);

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
            mLocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mLocationClient.setLocationListener(this);

            //获取一次定位结果：
            //该方法默认为false。
            mLocationOption.setOnceLocation(true);


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

        hideWindow();

        Bike bike = (Bike) marker.getObject();
        currMarker = marker;
        tempBike = bike;

        //marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(String.valueOf(bike.type), R.drawable.icon_marker_s)));
        StringBuffer sb = new StringBuffer();
        sb.append("车驾号:" + bike.deviceNo + "\n");
        sb.append("类型:" + bike.type + "\n");
        sb.append("经度:" + bike.longitude + "\n");
        sb.append("纬度:" + bike.latitude);
        TextView chargInfoTv = (TextView) view.findViewById(R.id.chargInfoTv);

        chargInfoTv.setText(sb.toString());

        //marker.showInfoWindow();
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
//		getListCar();
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
//		if(aMap != null){
//			aMap.setOnCameraChangeListener(this);
//		}
        getListCar();
    }

    private void hideWindow() {
        if (currMarker != null && currMarker.isInfoWindowShown()) {

            currMarker.hideInfoWindow();
            //currMarker.remove();

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

    public static MyEleCarFragment getInstance() {
        return myEleCarFragment;
    }

    @Override
    public void currAddr(String addr) {
        LogUtils.logDug("MyEleFragment currAddr");
        currAddrTv.setText(addr);
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
    public boolean isLogin() {
        return true;
    }
}
