package com.cargps.android.model.fragment;

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

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationClientOption.AMapLocationMode;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMap.OnMapClickListener;
import com.amap.api.maps2d.AMap.OnMapLoadedListener;
import com.amap.api.maps2d.AMap.OnMarkerClickListener;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.cargps.android.R;
import com.cargps.android.data.ChrgInfo;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.fragment_make_car_layout)
public class MakeCarFragment extends BaseFragment implements LocationSource, AMapLocationListener {
    @ViewById
    View chraglayout;

    @ViewById
    ImageView zoomMaxImg, zoomMinImg, myLocationImg, carControlImg;
    @ViewById
    TextView chargInfoTv;

    @ViewById
    MapView mMapView = null;
    AMap aMap;

    float zoomLv = 10;

    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    private OnLocationChangedListener mListener;

    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;

    private AMapLocation amapLocationCurr;

    Marker tempMarker = null;
    ChrgInfo tempChrgInfo = null;

    Bundle savedInstanceState;

    OnMarkerClickListener markerListener = new OnMarkerClickListener() {

        @Override
        public boolean onMarkerClick(Marker marker) {

//			ChrgInfo chrgInfo  = (ChrgInfo) marker.getObject();
//			
//			if(tempMarker != null && tempChrgInfo != null){
//				tempMarker.setIcon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(tempChrgInfo.chrg_num, R.drawable.icon_marker)));
//			}
//			
//			marker.setIcon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(chrgInfo.chrg_num, R.drawable.icon_marker_s)));
//			showChargInfoWindow(chrgInfo);
//			
//			tempMarker = marker;
//			tempChrgInfo = chrgInfo;

            return true;
        }
    };

    OnClickListener zoomMaxClick = new OnClickListener() {

        @Override
        public void onClick(View v) {

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

        // 获取地图控件引用

        zoomMaxImg.setOnClickListener(zoomMaxClick);
        zoomMinImg.setOnClickListener(zoomMinClick);
        myLocationImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (amapLocationCurr != null) {
                    aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(amapLocationCurr.getLatitude(), amapLocationCurr.getLongitude())));
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLv));
                }
            }
        });

        carControlImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//				if(tempChrgInfo != null && tempMarker != null){
//					Intent intent = new Intent(app.mainActivity, CarCantrolActivity.class);
//					intent.putExtra("carId", tempChrgInfo.chrg_vid);
//					intent.putExtra("iemi", tempChrgInfo.imei);
//					startActivity(intent);
//				}
            }
        });


        aMap = mMapView.getMap();

        mMapView.onCreate(savedInstanceState);

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
                .fromResource(R.drawable.icon_my_gps));// 设置小蓝点的图标

        aMap.setMyLocationStyle(myLocationStyle);

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
                if (chraglayout.getVisibility() == View.VISIBLE) {
                    chraglayout.setVisibility(View.GONE);
//					if(tempMarker != null && tempChrgInfo != null){
//						tempMarker.setIcon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(tempChrgInfo.chrg_num, R.drawable.icon_marker)));
//						tempMarker = null;
//						tempChrgInfo = null;
//					}
                }
            }
        });
        getListChrg();

    }

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
                amapLocationCurr = amapLocation;
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
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

    public void getListChrg() {
//		String interfaceUrl = String.format("device=%s&action=%s", "860012222233333","listchrg");
//		NoHttpRequest.getInstance().requestGet(getActivity(), interfaceUrl.hashCode(), interfaceUrl, GetListchrgResponse.class, new HttpListener<GetListchrgResponse>() {
//
//			@Override
//			public void onSucceed(int what, Response<GetListchrgResponse> response) {
//				if(response.isSucceed()){
//					if(response.get() != null && response.get().message.equals("success")){
//						if(response.get().data != null && !response.get().data.isEmpty()){
//							ChrgInfo firstChrgInfo = response.get().data.get(0);
////							LatLngBounds bounds = new LatLngBounds.Builder().include(new LatLng(chrgInfo.lat, chrgInfo.lng)).build();
//							aMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(firstChrgInfo.lat, firstChrgInfo.lng)));
//							aMap.moveCamera(CameraUpdateFactory.zoomTo(zoomLv));
//							
//							for (int i = 0; i < response.get().data.size(); i++) {
//								ChrgInfo chrgInfo = response.get().data.get(i);
//								
//								 //绘制marker
//						        Marker marker = aMap.addMarker(new MarkerOptions()
//						                .position(new LatLng(chrgInfo.lat,chrgInfo.lng))
//						                .title(chrgInfo.chrg_vid)
//						                .snippet("DefaultMarker")
//						                .icon(BitmapDescriptorFactory.fromBitmap(getMyBitmap(chrgInfo.chrg_num,R.drawable.icon_marker)))
//						                );
//						        marker.setObject(chrgInfo);
//							}
//						
//						}
//						
//					}
//				}
//			}
//
//			@Override
//			public void onFailed(int what, String url, Object tag, Exception exception, int responseCode,
//					long networkMillis) {
//				
//			}
//		}, true);
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

            //该方法默认为false。
            mLocationOption.setOnceLocation(true);

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

    public void showChargInfoWindow(ChrgInfo chrgInfo) {


//		String status = "";
//		if(chrgInfo.chrg_status.equals("0")){
//			status = "空闲";
//		}else if(chrgInfo.chrg_status.equals("1") || chrgInfo.chrg_status.equals("2")){
//			status = "在充";
//		}else if(chrgInfo.chrg_status.equals("3")){
//			status = "充电结束";
//		}
//		StringBuffer sb = new StringBuffer();
//		sb.append("IMEI：" + chrgInfo.imei+"\r\n");
//		sb.append("充电电压 ：" + chrgInfo.chrg_vol+"V\r\n");
//		sb.append("充电电流：" + chrgInfo.chrg_cur+"A\r\n");
//		sb.append("状态 :" + status+"\r\n");
//		sb.append("编号 ： " + chrgInfo.chrg_num+"\r\n");
//		sb.append("车辆编号 ： " + chrgInfo.chrg_vid);
//		
//		chargInfoTv.setText(sb.toString());
//		
//		chraglayout.setVisibility(View.VISIBLE);
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
}
