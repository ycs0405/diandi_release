package com.cargps.android.model.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.cargps.android.MyApplication;
import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.bluetooth.BleSdkUtils;
import com.cargps.android.bluetooth.BleSdkUtils.ScanSmartListener;
import com.cargps.android.bluetooth.BleSdkUtils.ServerConnectListerner;
import com.cargps.android.bluetooth.BleSdkUtils.SmartBikeListerner;
import com.cargps.android.bluetooth.DeviceAdapter;
import com.cargps.android.bluetooth.DeviceDB;
import com.cargps.android.data.CarInfo;
import com.cargps.android.data.ConsumeOrder;
import com.cargps.android.data.OrderInfo;
import com.cargps.android.interfaces.DialogCallback;
import com.cargps.android.model.view.CustomDialog;
import com.cargps.android.model.view.SwitchButton;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.BaseResponse;
import com.cargps.android.net.responseBean.CarInfoResponse;
import com.cargps.android.net.responseBean.OrderResponse;
import com.cargps.android.utils.ButtonUtils;
import com.cargps.android.utils.DialogUtils;
import com.cargps.android.utils.SPUtils;
import com.fu.baseframe.utils.SystemOpt;
import com.xiaofu_yan.blux.blue_guard.BlueGuard;
import com.xiaofu_yan.blux.blue_guard.BlueGuard.DisconnectReason;
import com.xiaofu_yan.blux.blue_guard.BlueGuard.PairResult;
import com.xiaofu_yan.blux.blue_guard.BlueGuard.State;
import com.xiaofu_yan.blux.smart_bike.SmartBike;
import com.xiaofu_yan.blux.smart_bike.SmartBikeManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class CarCantrolActivity extends BaseActivity implements OnClickListener {

    TextView titleTv;
    FrameLayout lockFl, centerFl, repayCarFl, checkCarFl;

    boolean isLock = true;
    boolean isStart = false;

    ImageView lockImg, startImg, checkImg;

    MediaPlayer mediaPlayer;
    //ble
    private SwitchButton mSwitchButton;
    private TextView modeText;
    private int useMode = 0;// 0 - GPS 1—— ble
    private static final int REQUEST_ENABLE_BT = 0;
    private static final String TAG = CarCantrolActivity.class.getSimpleName();
    private BleSdkUtils mBleSdkUtils;
    private Dialog dialog;
    private DeviceAdapter adapter;
    // devices
    private List<DeviceDB.Record> mDevices = new ArrayList<DeviceDB.Record>();
    private DeviceDB.Record mCurrentDevice;
    private boolean isShowingDeviceListDialog = false;
    private EditText mPassEditView;
    private AlertDialog pairDialog;
    private Context context;
    private Timer timer;
    private State currentState;//ble 当前状态
    private boolean isKeyDirect = false;
    private CustomDialog cusdialog;
    private SwipeMenuListView mListView;
    private String pairResult;
    private Dialog directLoading;
    private Dialog pairLoading;
    private Timer pairTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_car_cantrol_layout);

        mediaPlayer = new MediaPlayer();

        if (TextUtils.isEmpty(MyApplication.INPUT_IMEI)) {
            showToast("请扫描或输入车辆号!");
            finish();
        }

        titleTv = (TextView) findViewById(R.id.titleTv);
        titleTv.setText("车辆控制");

        titleTv.setCompoundDrawables(null, null, null, null);


        findViewById(R.id.rightimg).setVisibility(View.GONE);

        ((ImageView) findViewById(R.id.backimg)).setImageResource(R.drawable.icon_back);
        findViewById(R.id.backimg).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                saveCarImei();
                finish();
            }
        });

        lockFl = (FrameLayout) findViewById(R.id.lockFl);
        lockFl.setOnClickListener(this);
        centerFl = (FrameLayout) findViewById(R.id.centerFl);
        centerFl.setOnClickListener(this);
        repayCarFl = (FrameLayout) findViewById(R.id.repayCarFl);
        repayCarFl.setOnClickListener(this);
        checkCarFl = (FrameLayout) findViewById(R.id.checkCarFl);
        checkCarFl.setOnClickListener(this);

        lockImg = (ImageView) findViewById(R.id.lockImg);
        startImg = (ImageView) findViewById(R.id.startImg);
        checkImg = (ImageView) findViewById(R.id.checkImg);

        mediaPlayer.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mSwitchButton = (SwitchButton) findViewById(R.id.switchButton);
        modeText = (TextView) findViewById(R.id.text_mode);
        updateMode();
        mSwitchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //fast click
                boolean isFast = ButtonUtils.isFastDoubleClick(mSwitchButton.getId(), 1000);
                if (isFast) {
                    Toast.makeText(context, "小主点击太快了！", Toast.LENGTH_SHORT).show();
                    if (isChecked) {
                        mSwitchButton.setChecked(!isChecked);
                    } else {
                        mSwitchButton.setChecked(isChecked);
                    }
                    return;
                }

                if (isChecked) {
                    useMode = 1;
                    modeText.setText("蓝牙模式");
                    boolean isServerConnect = mBleSdkUtils.bindService(CarCantrolActivity.this, new ServerConnectListerner() {

                        @Override
                        public void serverDisConnectted() {
                            // TODO Auto-generated method stub
                            mSwitchButton.setChecked(false);
                            updateMode();
                        }

                        @Override
                        public void serverConnectted(SmartBikeManager smartBikeManager) {
                            // TODO Auto-generated method stub
                            openBluetooth();
                        }
                    });
                    if (!isServerConnect) {
                        mSwitchButton.setChecked(false);
                        updateMode();
                        Toast.makeText(context, "服务绑定失败，请重试", Toast.LENGTH_SHORT).show();
                    }

                } else {

                    useMode = 0;
                    modeText.setText("GPS模式");
                    mBleSdkUtils.disConnectDevice();
                    mBleSdkUtils.unBindService();
//					Toast.makeText(context, "小主，蓝牙模式车辆失去控制", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mBleSdkUtils = new BleSdkUtils();
        mBleSdkUtils.initBleSdk(this);
        getCurrentDevice();
        context = this;
    }

    @SuppressLint("NewApi")
    protected void openBluetooth() {
        //判断设备是否支持ble
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "不支持BLE蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        // 初始化蓝牙适配器
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        // 确保蓝牙在手机上可以开启
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {
            scanBluetooth();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "开启蓝牙成功!", Toast.LENGTH_SHORT).show();
                scanBluetooth();
                // mBluetoothAdapter.enable(); 此方法不需要询问用户，直接开启 但需要admin的权限
            } else {
                mSwitchButton.setChecked(false);
                Toast.makeText(this, "开启蓝牙失败,请重试!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void scanBluetooth() {
        //已在扫描 暂停
        if (mBleSdkUtils.isScanning()) {
            mBleSdkUtils.stopScanBleDevice();
        }
        /*//亦可用
        if(mBleSdkUtils.isSmartBikeAvailable()){
			mSwitchButton.setChecked(true);
			BlueGuard.State state = mBleSdkUtils.getSmartBike().state();
			Log.w(TAG, "smart 可用  , state = "+state);
			
			if(state == State.ARMED){
				//布防
				mBleSdkUtils.lock();
			}else if(state == State.RUNNING ||state == State.STARTED){
				mBleSdkUtils.start();
			}else if(state == State.STOPPED){
				//撤防
				mBleSdkUtils.unlock();
//				lockImg.setImageResource(R.drawable.icon_unlock_car);
//				startImg.setImageResource(R.drawable.icon_stop_status);
			}else if(state == State.UNKNOWN){
				mBleSdkUtils.stop();
			}
			currentState = state;
			return;
		}*/
        if (getCurrentDevice()) {
            directLoading = DialogUtils.createLoadingDialog(CarCantrolActivity.this, "车辆连接中...");
            directLoading.show();
            isKeyDirect = true;
            mBleSdkUtils.connectDeviceByIdentifier(mCurrentDevice.identifier, new MyScanListener() {

                @Override
                public void getSmartBike(SmartBike smartBike) {
                    // TODO Auto-generated method stub
                    mBleSdkUtils.connectDeviceBykey(mCurrentDevice.key, new MySmartBikeListerner() {

                        @Override
                        public void smartBikeState(State state) {
                            // TODO Auto-generated method stub
                            Log.w(TAG, "直连 state ：" + state);

                            if (state == State.ARMED) {
                                //布防
                                lockImg.setImageResource(R.drawable.icon_loak_car);
                            } else if (state == State.RUNNING) {
                                //行驶
                                startImg.setImageResource(R.drawable.icon_start_status);
                            } else if (state == State.STARTED) {
                                //上电
                                startImg.setImageResource(R.drawable.icon_start_status);
                            } else if (state == State.STOPPED) {
                                //撤防
                                lockImg.setImageResource(R.drawable.icon_unlock_car);
                                startImg.setImageResource(R.drawable.icon_stop_status);
                            }
                            currentState = state;
                        }
                    });

                }
            });
        } else {
            isKeyDirect = false;
            //扫描 配对连接
            showDeviceDialog();
            if (timer == null) {
                timer = new Timer();
            }
            timer.schedule(new ScanTask(), 10 * 1000);

            mBleSdkUtils.scanBleDevice(new MyScanListener() {

                @Override
                public void foundSmartBike(String identifier, String name) {
                    // TODO Auto-generated method stub
                    if (!TextUtils.isEmpty(identifier) && !TextUtils.isEmpty(name)) {
                        Log.w(TAG, "device identifier:" + identifier + "  device name:" + name);
//					            mBleSdkUtils.stopScanBleDevice();
//					            mBleSdkUtils.connectDeviceByIdentifier(identifier);
                        DeviceDB.Record rec = new DeviceDB.Record(name, identifier, null);
                        if (dialog != null && dialog.isShowing()) {
//			                Toast.makeText(context,"find new devices",Toast.LENGTH_SHORT).show();
                            dialog.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
                        }
                        if (timer != null) {
                            timer.cancel();
                            timer = null;
                        }
                        if (adapter != null) {
                            adapter.addDevice(rec);
                        }

                    }
                }
            });

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        getCarState();
        updateMode();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.release();
        }
        //unbind
        if (mSwitchButton.isChecked()) {
            mSwitchButton.setChecked(false);
        }
        saveCarImei();
    }

    @Override
    public void onClick(View v) {
        boolean isCan = mBleSdkUtils.isSmartBikeAvailable();
        Log.w(TAG, "onClick -- useMode = " + useMode + ", mBleSdkUtils.isSmartBikeAvailable() = " + isCan);
        // click fast
        boolean isFast = ButtonUtils.isFastDoubleClick(v.getId(), 2000);
        if (isFast) {
            Toast.makeText(context, "小主，点的太快，我反应不过来了！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (useMode == 0) {
            if (v == lockFl) {
                if (isLock) {
                    playMedia("js.wav");
                    unlocakData();
                } else {
                    playMedia("ss.wav");
                    locakData();
                }
            } else if (v == centerFl) {
                if (isStart) {
                    playMedia("dd.wav");
                    closeBike();
                } else {
                    playMedia("sd.wav");
                    startBike();
                }

            } else if (v == repayCarFl) {
                playMedia("hc.wav");
                createOrder();
            } else if (v == checkCarFl) {
                checkCar();
            }

        } else if (useMode == 1 && isCan) {
            BlueGuard.State state = mBleSdkUtils.getSmartBike().state();
            Log.w(TAG, "state = " + state);
            if (v == lockFl) {
                if (state == State.ARMED) {
                    //开锁
                    playMedia("js.wav");
                    mBleSdkUtils.unlock();
                } else if (state == State.STOPPED) {
                    //上锁
                    playMedia("ss.wav");
                    mBleSdkUtils.lock();

                }
            } else if (v == centerFl) {
                if (state == State.STARTED) {
                    playMedia("dd.wav");
                    mBleSdkUtils.stop();
                } else if (state == State.STOPPED) {
                    playMedia("sd.wav");
                    mBleSdkUtils.start();
                }
            } else if (v == repayCarFl) {
                //还车
                playMedia("hc.wav");
                mBleSdkUtils.stop();
                mBleSdkUtils.lock();
                mBleSdkUtils.disConnectDevice();
                createOrder();
            } else if (v == checkCarFl) {
                //检测车辆
                checkCar();
            }
        }
    }

    private void playMedia(String fileName) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        try {
            AssetManager assetManager = this.getAssets();
            AssetFileDescriptor fileDesc = assetManager.openFd(fileName);
            mediaPlayer.reset();
            mediaPlayer.setDataSource(fileDesc.getFileDescriptor(),
                    fileDesc.getStartOffset(),
                    fileDesc.getLength()
            );
            mediaPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void unlocakData() {
        String urlStr = MyContacts.MAIN_URL + "v1.0/ebike/unlockBike";

        Map<String, String> param = new HashMap<String, String>();
        param.put("imeiIdOrDeviceId", MyApplication.INPUT_IMEI);

        HttpRequest<BaseResponse> httpRequest = new HttpRequest<BaseResponse>(this, urlStr, new HttpResponseListener<BaseResponse>() {

            @Override
            public void onResult(BaseResponse result) {
                if (result != null && result.statusCode == 200) {
                    isLock = false;
                    lockImg.setImageResource(R.drawable.icon_loak_car);
                } else {
                    Toast.makeText(CarCantrolActivity.this, result.message, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFail(int code) {

            }
        }, BaseResponse.class, param, "POST", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);
        HttpExecute.getInstance().addRequest(httpRequest);
    }

    public void locakData() {

        String urlStr = MyContacts.MAIN_URL + "v1.0/ebike/lockBike";

        Map<String, String> param = new HashMap<String, String>();
        param.put("imeiIdOrDeviceId", MyApplication.INPUT_IMEI);

        HttpRequest<BaseResponse> httpRequest = new HttpRequest<BaseResponse>(this, urlStr, new HttpResponseListener<BaseResponse>() {

            @Override
            public void onResult(BaseResponse result) {
                if (result != null && result.statusCode == 200) {
                    isLock = true;
                    lockImg.setImageResource(R.drawable.icon_unlock_car);
                } else {
                    Toast.makeText(CarCantrolActivity.this, result.message, Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFail(int code) {

            }
        }, BaseResponse.class, param, "POST", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);
        HttpExecute.getInstance().addRequest(httpRequest);
    }

    public void startBike() {

        String urlStr = MyContacts.MAIN_URL + "v1.0/ebike/startBike";

        Map<String, String> param = new HashMap<String, String>();
        param.put("imeiIdOrDeviceId", MyApplication.INPUT_IMEI);

        HttpRequest<BaseResponse> httpRequest = new HttpRequest<BaseResponse>(this, urlStr, new HttpResponseListener<BaseResponse>() {

            @Override
            public void onResult(BaseResponse result) {
                if (result != null && result.statusCode == 200) {
                    isStart = true;
                    startImg.setImageResource(R.drawable.icon_stop_status);
                } else {
                    Toast.makeText(CarCantrolActivity.this, result.message, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFail(int code) {

            }
        }, BaseResponse.class, param, "POST", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);
        HttpExecute.getInstance().addRequest(httpRequest);
    }

    public void closeBike() {

        String urlStr = MyContacts.MAIN_URL + "v1.0/ebike/closeBike";

        Map<String, String> param = new HashMap<String, String>();
        param.put("imeiIdOrDeviceId", MyApplication.INPUT_IMEI);

        HttpRequest<BaseResponse> httpRequest = new HttpRequest<BaseResponse>(this, urlStr, new HttpResponseListener<BaseResponse>() {

            @Override
            public void onResult(BaseResponse result) {
                if (result != null && result.statusCode == 200) {
                    isStart = false;
                    startImg.setImageResource(R.drawable.icon_start_status);
                } else {
                    Toast.makeText(CarCantrolActivity.this, result.message, Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFail(int code) {

            }
        }, BaseResponse.class, param, "POST", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);
        HttpExecute.getInstance().addRequest(httpRequest);
    }


    public void createOrder() {

        String urlStr = MyContacts.MAIN_URL + "v1.0/ebike/endBike";

        Map<String, String> param = new HashMap<String, String>();
        param.put("imeiIdOrDeviceId", MyApplication.INPUT_IMEI);
        Log.w(TAG, "param = " + param.toString());
        HttpRequest<OrderResponse> httpRequest = new HttpRequest<OrderResponse>(this, urlStr, new HttpResponseListener<OrderResponse>() {

            @Override
            public void onResult(OrderResponse result) {
                if (result != null && result.data != null) {
                    if (result.statusCode == 200) {
                        final OrderInfo orderInfo = result.data;
                        final ConsumeOrder consumeOrder = new ConsumeOrder();
                        consumeOrder.orderId = orderInfo.orderId;
                        consumeOrder.startTime = orderInfo.startTime;
                        consumeOrder.endTime = orderInfo.endTime;
                        consumeOrder.minutes = orderInfo.countTime;
                        consumeOrder.consume = orderInfo.orderAmount;
                        DialogUtils.getInstance().showPushMsgDialog(CarCantrolActivity.this, "还车", consumeOrder, new DialogCallback() {
                            @Override
                            public void confirm() {
                                super.confirm();
                                MyApplication.INPUT_IMEI = null;
                                saveCarImei();
                                finish();
                                com.cargps.android.model.activity.PayActivity_.intent(MyApplication.mainActivity).
                                        flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).order(consumeOrder).start();
                            }

                            @Override
                            public void cancle() {
                                super.cancle();
                                MyApplication.INPUT_IMEI = null;
                                finish();
                                //com.cargps.android.model.activity.MyOrderActivity_.intent(CarCantrolActivity.this).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK).start();

                            }
                        });
                    }
                }
                if(result !=null && result.statusCode == 400){
                    MyApplication.INPUT_IMEI = null;
                    finish();
                }
            }

            @Override
            public void onFail(int code) {

            }
        }, OrderResponse.class, param, "POST", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);
        HttpExecute.getInstance().addRequest(httpRequest);
    }

    public void checkCar() {
        String urlStr = MyContacts.MAIN_URL + "v1.0/ebike/faultDetect";

        Map<String, String> param = new HashMap<String, String>();
        param.put("imeiIdOrDeviceId", MyApplication.INPUT_IMEI);

        HttpRequest<BaseResponse> httpRequest = new HttpRequest<BaseResponse>(this, urlStr, new HttpResponseListener<BaseResponse>() {

            @Override
            public void onResult(BaseResponse result) {
                if (result != null) {
                    if (result.statusCode == 402) {
                        checkImg.setImageResource(R.drawable.icon_check_car);
                    }
                    showToast(result.message);
                }
            }

            @Override
            public void onFail(int code) {

            }
        }, BaseResponse.class, param, "POST", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);
        HttpExecute.getInstance().addRequest(httpRequest);
    }


    private void getCarState() {


        String strUrl = String.format("http://api.qdigo.net/v1.0/ebike/getEBikeDetail/%s", MyApplication.INPUT_IMEI);

        HttpRequest<CarInfoResponse> httpRequest = new HttpRequest<CarInfoResponse>(this, strUrl, new HttpResponseListener<CarInfoResponse>() {

            @Override
            public void onResult(CarInfoResponse result) {
                if (result != null && result.statusCode == 200) {
                    CarInfo carInfo = result.data;
                    isLock = carInfo.locked;
                    isStart = carInfo.fired;
                    ;
                    if (isLock) {
                        lockImg.setImageResource(R.drawable.icon_unlock_car);
                    } else {
                        lockImg.setImageResource(R.drawable.icon_loak_car);
                    }

                    if (isStart) {
                        startImg.setImageResource(R.drawable.icon_stop_status);
                    } else {
                        startImg.setImageResource(R.drawable.icon_start_status);
                    }
                }
            }

            @Override
            public void onFail(int code) {

            }
        }, CarInfoResponse.class, null, "GET", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);
        HttpExecute.getInstance().addRequest(httpRequest);


    }

    @Override
    public boolean isLogin() {
        // TODO Auto-generated method stub
        return true;
    }

	/*@Override
    public void serviceConnectResult(boolean connect) {
		if(connect){
            getCurrentDevice();
            if(mCurrentDevice != null){
                if(!TextUtils.isEmpty(mCurrentDevice.identifier)){
                    mBleSdkUtils.connectDeviceByIdentifier(mCurrentDevice.identifier);
                }
            }else{
            	Log.w(TAG, "serviceConnectResult ---------- mCurrentDevice = NULL");
//            	scanBluetooth();
                
            }
        }else{
            Toast.makeText(context, "Server connect failed and bind again", Toast.LENGTH_SHORT).show();
        }
		
	}

	@Override
	public void scanSuccess(String identifier, String name) {
		// TODO Auto-generated method stub
		if(!TextUtils.isEmpty(identifier) && !TextUtils.isEmpty(name)){
            Log.w(TAG,"device identifier:"+identifier+"  device name:"+name);
//            mBleSdkUtils.stopScanBleDevice();
//            mBleSdkUtils.connectDeviceByIdentifier(identifier);
            DeviceDB.Record rec = new DeviceDB.Record(name, identifier, null);
            if(dialog!=null&&dialog.isShowing()){
                Toast.makeText(this,"find new devices",Toast.LENGTH_SHORT).show();
                dialog.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            }
            if(timer !=null){
            	timer.cancel();
            	timer=null;
            }
            if(adapter !=null){
                adapter.addDevice(rec);
            }

        }
	}

	@Override
	public void getSmartBikeInstance() {
		// TODO Auto-generated method stub
		if(isKeyDirect){
			isKeyDirect = false;
			return;
		}
		if(mCurrentDevice!= null){
            // from dialog
                if(!TextUtils.isEmpty(mCurrentDevice.key)){
                	if(dialog !=null && dialog.isShowing()){
                		dialog.dismiss();
                	}
                    mBleSdkUtils.connectDeviceBykey(mCurrentDevice.key);
                }
        }else{
        	Log.w(TAG, "pairDialog");
            pairDialog();
        }
	}

	@Override
	public void smartBikePairResult(BlueGuard blueGuard, PairResult result,
			String key) {
		// TODO Auto-generated method stub
		Log.w(TAG,"smartBikePairResult result:"+result+"  key:"+result);
        if(result == BlueGuard.PairResult.SUCCESS){
            //save db
            if(!TextUtils.isEmpty(key)){
                DeviceDB.Record rec = new DeviceDB.Record(blueGuard.name(), blueGuard.identifier(), key);
                DeviceDB.save(this, rec);
                useMode = 1;
            }else{
            	useMode = 0;
                Toast.makeText(this,"pair failed __ key is not ok",Toast.LENGTH_SHORT).show();
            }
        }else if(result == BlueGuard.PairResult.ERROR_KEY){
        	useMode = 0;
            DeviceDB.delete(this);
        }else if (result == BlueGuard.PairResult.ERROR_PERMISSION ){
        	useMode = 0;
            Toast.makeText(this,"重启硬件",Toast.LENGTH_SHORT).show();
            DeviceDB.delete(this);
            //pair show
        }else{
        	useMode = 0;
            Toast.makeText(this,"pair failed",Toast.LENGTH_SHORT).show();
        }
        if(dialog !=null && dialog.isShowing()){
        	dialog.dismiss();
        }
	}

	@Override
	public void updateState(State state) {
		// TODO Auto-generated method stub
		Log.w(TAG,"updateState : "+state);
		currentState = state;
		if(state == State.ARMED){
			//布防
			lockImg.setImageResource(R.drawable.icon_loak_car);
		}else if(state == State.RUNNING){
			//行驶
		}else if(state == State.STARTED){
			//上电
			startImg.setImageResource(R.drawable.icon_start_status);
		}else if(state == State.STOPPED){
			//撤防
			lockImg.setImageResource(R.drawable.icon_unlock_car);
			startImg.setImageResource(R.drawable.icon_stop_status);
		}
	}
	
	@Override
	public void blueGuardDisconnected(BlueGuard blueGuard,DisconnectReason reason) {
		Toast.makeText(context, "失去车辆连接："+reason, Toast.LENGTH_SHORT).show();
		DeviceDB.delete(this);
		mSwitchButton.setChecked(false);
		updateMode();
		
	}*/

    public boolean getCurrentDevice() {
        boolean isEnable = false;
        DeviceDB.Record currentDevice = DeviceDB.load(this);
        if (currentDevice != null) {
            if (!TextUtils.isEmpty(currentDevice.identifier) && !TextUtils.isEmpty(currentDevice.name) && !TextUtils.isEmpty(currentDevice.key)) {
//                mDevices.add(currentDevice);
                isEnable = true;
                mCurrentDevice = currentDevice;
            }
        }
        return isEnable;
    }

    protected void showDeviceDialog() {
        if (dialog != null) {
            if (mDevices.size() != 0) {
                dialog.findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            } else {
                dialog.findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
            }
            dialog.show();
            return;
        }
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.device_list2, null);
        mListView = (SwipeMenuListView) view.findViewById(R.id.paired_devices);

        ProgressBar progressbar = (ProgressBar) view.findViewById(R.id.progressBar);
        initMenu();
        adapter = new DeviceAdapter(context, mDevices);
        mListView.setAdapter(adapter);
        dialog = new Dialog(this, R.style.dialog);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(false);
        dialog.show();
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                mBleSdkUtils.stopScanBleDevice();
                mCurrentDevice = mDevices.get(position);
                mBleSdkUtils.connectDeviceByIdentifier(mCurrentDevice.identifier, new MyScanListener() {

                    @Override
                    public void getSmartBike(SmartBike smartBike) {
                        // TODO Auto-generated method stub
                        if (!TextUtils.isEmpty(mCurrentDevice.key)) {
                            mBleSdkUtils.connectDeviceBykey(mCurrentDevice.key, new MySmartBikeListerner() {

                                @Override
                                public void smartBikeState(State state) {
                                    // TODO Auto-generated method stub
                                    Log.w(TAG, "Piar 连接 , state :" + state);
                                    if (state == State.ARMED) {
                                        //布防
                                        lockImg.setImageResource(R.drawable.icon_loak_car);
                                    } else if (state == State.RUNNING) {
                                        //行驶
                                    } else if (state == State.STARTED) {
                                        //上电
                                        startImg.setImageResource(R.drawable.icon_start_status);
                                    } else if (state == State.STOPPED) {
                                        //撤防
                                        lockImg.setImageResource(R.drawable.icon_unlock_car);
                                        startImg.setImageResource(R.drawable.icon_stop_status);
                                    }
                                    currentState = state;
                                }

                                @Override
                                public void smartBikeConnected() {
                                    // TODO Auto-generated method stub
                                    Log.w(TAG, "showdevice -- MySmartBikeListerner, smartBikeConnected ");
                                    useMode = 1;
                                    mSwitchButton.setChecked(true);
                                    updateMode();
                                }

                                @Override
                                public void smartBikeDisConnectted(DisconnectReason reason) {
                                    // TODO Auto-generated method stub
                                    Log.w(TAG, "showdevice -- MySmartBikeListerner, smartBikeDisConnectted , reason = " + reason);
                                    useMode = 0;
                                    mSwitchButton.setChecked(false);
                                    DeviceDB.delete(context);
                                    updateMode();
                                }
                            });
                        } else {
                            Log.w(TAG, "pairDialog");
                            pairDialog();
                        }
                    }

                });
                dialog.dismiss();


            }
        });
    }

    private void initMenu() {
        // TODO Auto-generated method stub
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {

                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        // set creator
        mListView.setMenuCreator(creator);

        // step 2. listener item click event
        mListView.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {

                switch (index) {
                    case 0:
                        // delete
                        mDevices.remove(mDevices.get(position));
                        adapter.notifyDataSetChanged();
                        if (mDevices.size() == 0) {
                            if (dialog != null && dialog.isShowing()) {
                                dialog.dismiss();
                                mSwitchButton.setChecked(false);
                            }
                        }
                        break;

                }
                return false;
            }
        });
    }

    /**
     * 扫描 不到设备 十秒内自动关闭
     *
     * @author jpj
     */
    class ScanTask extends TimerTask {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (mBleSdkUtils.isScanning()) {
                mBleSdkUtils.stopScanBleDevice();
            }
            if (dialog != null && dialog.isShowing()) {
                dialog.dismiss();
            }
            useMode = 0;
            if (mBleSdkUtils.isSmartBikeAvailable()) {
                useMode = 1;
            }
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(context, "10秒，未搜索到车辆请重试！", Toast.LENGTH_SHORT).show();
                    mSwitchButton.setChecked(false);
                    updateMode();
                }

            });
        }

    }

    class PairTask extends TimerTask {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            if (pairLoading != null && pairLoading.isShowing()) {
                pairLoading.dismiss();
            }
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    Toast.makeText(context, "10秒，未配对到车辆！", Toast.LENGTH_SHORT).show();
                    mSwitchButton.setChecked(false);
                    updateMode();
                }

            });
        }

    }

    private void updateMode() {
        if (mSwitchButton.isChecked()) {
            useMode = 1;
            modeText.setText("蓝牙模式");
        } else {
            useMode = 0;
            modeText.setText("GPS模式");
        }

    }

    private void pairDialog() {
        CustomDialog.Builder builder = new CustomDialog.Builder(context);
        cusdialog = builder.create();
        cusdialog.setClickListener(new DialogInterface.OnClickListener() {


            @Override
            public void onClick(DialogInterface dialog, int which) {
                pairLoading = DialogUtils.createLoadingDialog(CarCantrolActivity.this, "车辆配对中...");
                pairLoading.show();
                if (pairTimer == null) {
                    pairTimer = new Timer();
                }
                pairTimer.schedule(new PairTask(), 10000);
                String carCode = ((CustomDialog) dialog).getEdittext();

                mBleSdkUtils.stopScanBleDevice();
                if ("000000".equals(carCode)) {
                    dialog.dismiss();
                    mBleSdkUtils.connectDeviceByPair(carCode, new MySmartBikeListerner() {


                        @Override
                        public void smartBikeState(State state) {
                            // TODO Auto-generated method stub
                            Log.w(TAG, "pair 连接 , state = " + state);
                            if (state == State.ARMED) {
                                //布防
                                lockImg.setImageResource(R.drawable.icon_loak_car);
                            } else if (state == State.RUNNING) {
                                //行驶
                            } else if (state == State.STARTED) {
                                //上电
                                startImg.setImageResource(R.drawable.icon_start_status);
                            } else if (state == State.STOPPED) {
                                //撤防
                                lockImg.setImageResource(R.drawable.icon_unlock_car);
                                startImg.setImageResource(R.drawable.icon_stop_status);
                            }
                            currentState = state;
                        }

                        @Override
                        public void smartBikePairResult(BlueGuard blueGuard, PairResult result, String key) {
                            // TODO Auto-generated method stub
                            Log.w(TAG, "smartBikePairResult result:" + result + "  key:" + result);
                            pairLoading.dismiss();
                            if (pairTimer != null) {
                                pairTimer.cancel();
                                pairTimer = null;
                            }
                            if (result == BlueGuard.PairResult.SUCCESS) {
                                //save db
                                if (!TextUtils.isEmpty(key)) {
                                    DeviceDB.Record rec = new DeviceDB.Record(blueGuard.name(), blueGuard.identifier(), key);
                                    DeviceDB.save(context, rec);
                                    Log.w(TAG, "小主连接车辆成功！");
                                    useMode = 1;
                                } else {
                                    useMode = 0;
                                    Log.w(TAG, "小主连接车辆成功,但是key == null");
                                    Toast.makeText(context, "pair 成功，但是key == null", Toast.LENGTH_SHORT).show();
                                }
                            } else if (result == BlueGuard.PairResult.ERROR_KEY) {
                                useMode = 0;
                                Log.w(TAG, "小主连接车辆失败，原因：ERROR_KEY");
                                DeviceDB.delete(context);
                                mDevices.clear();
                            } else if (result == BlueGuard.PairResult.ERROR_PERMISSION) {
                                useMode = 0;
                                Log.w(TAG, "小主连接车辆失败，原因：ERROR_PERMISSION");
                                DeviceDB.delete(context);
                                mDevices.clear();
                                //pair show
                            } else if (result == BlueGuard.PairResult.ERROR) {
                                useMode = 0;
                                Log.w(TAG, "小主连接车辆失败，原因：ERROR");
                            }

                        }


                    });
                } else {
                    pairLoading.dismiss();
                    Toast.makeText(CarCantrolActivity.this, "配对码错误", Toast.LENGTH_SHORT).show();
                    //close
                    mSwitchButton.setChecked(false);
                    updateMode();
                }


            }
        });
        cusdialog.setCanceledOnTouchOutside(false);
        cusdialog.setCancelable(false);
        cusdialog.show();

    }


    class MyScanListener implements ScanSmartListener {

        @Override
        public void foundSmartBike(String identifier, String name) {
            // TODO Auto-generated method stub

        }

        @Override
        public void getSmartBike(SmartBike smartBike) {
            // TODO Auto-generated method stub

        }

    }

    class MySmartBikeListerner implements SmartBikeListerner {

        @Override
        public void smartBikeConnected() {
            // TODO Auto-generated method stub
            Log.w(TAG, "MySmartBikeListerner, smartBikeConnected ");
            if (pairLoading != null && pairLoading.isShowing()) {
                pairLoading.dismiss();
            }
            if (cusdialog != null && cusdialog.isShowing()) {
                cusdialog.dismiss();
            }

            if (isKeyDirect) {
                if (directLoading != null && directLoading.isShowing()) {
                    directLoading.dismiss();
                }
                pairResult = "小主，车辆连接直连成功！";
                isKeyDirect = false;
            } else {
                pairResult = "小主，车辆连接成功！";
            }
            Toast.makeText(context, pairResult, Toast.LENGTH_SHORT).show();
            useMode = 1;
            mSwitchButton.setChecked(true);
            updateMode();
        }

        @Override
        public void smartBikeDisConnectted(DisconnectReason reason) {
            // TODO Auto-generated method stub
            Log.w(TAG, "MySmartBikeListerner, smartBikeDisConnectted , reason = " + reason);

            if (pairLoading != null && pairLoading.isShowing()) {
                pairLoading.dismiss();
            }
            if (directLoading != null && directLoading.isShowing()) {
                directLoading.dismiss();
            }
            if (cusdialog != null && cusdialog.isShowing()) {
                cusdialog.dismiss();
            }

            if (isKeyDirect) {
                pairResult = "小主，车辆连接直连失败！";
                isKeyDirect = false;
            } else {
                pairResult = "小主，车辆连接失败";
            }
            Toast.makeText(context, pairResult, Toast.LENGTH_SHORT).show();
            useMode = 0;
            mSwitchButton.setChecked(false);
            DeviceDB.delete(context);
            mDevices.clear();
            updateMode();
        }

        @Override
        public void smartBikeName(String name) {
            // TODO Auto-generated method stub

        }

        @Override
        public void smartBikeState(State state) {
            Log.w(TAG, "smartBikeState state = " + state);

        }

        @Override
        public void smartBikePairResult(BlueGuard blueGuard, PairResult result,
                                        String key) {
            // TODO Auto-generated method stub

        }

    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }

    public void saveCarImei() {
        Log.w(TAG, "saveCarImei ()  IMEI = " + MyApplication.INPUT_IMEI);
        if (!TextUtils.isEmpty(MyApplication.INPUT_IMEI)) {
            SPUtils.put(context, "CAR_IMEI", MyApplication.INPUT_IMEI);
        } else {
            SPUtils.put(context, "CAR_IMEI", "");
        }
    }

}
