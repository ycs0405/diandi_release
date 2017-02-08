package com.cargps.android.bluetooth;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.xiaofu_yan.blux.blue_guard.BlueGuard;
import com.xiaofu_yan.blux.smart_bike.SmartBike;
import com.xiaofu_yan.blux.smart_bike.SmartBikeManager;
import com.xiaofu_yan.blux.smart_bike.SmartBikeServerConnection;

/**
 * Created by jpj on 2016/12/26.
 * 操作ble sdk的工具类
 */
public class BleSdkUtils {
    private static final String TAG = BleSdkUtils.class.getSimpleName();
    private Context mContext;
    //sdk
    private SmartBikeServerConnection mConnection;
    private SmartBikeManager mSmartBikeManager;
    private SmartBike mSmartBike;


    /**
     * 初始化
     */

    public void initBleSdk(Context context) {
        mContext = context;
        mConnection = new SmartBikeServerConnection();
        mConnection.delegate = new ServerConnectionDelegate();
    }

    public SmartBike getSmartBike() {
        return mSmartBike;
    }

    public SmartBikeManager getSmartBikeManager() {
        return mSmartBikeManager;
    }

    /**
     * 绑定服务
     * return true :success false: failed
     */
    public boolean bindService(Activity activity, ServerConnectListerner mServerConnectListerner) {
        this.mServerConnectListerner = mServerConnectListerner;
        boolean connect = false;
        if (mConnection != null) {
            connect = mConnection.connect(activity);
        }
        Log.w(TAG, "bind service result:" + connect);
        return connect;
    }

    /**
     * 断开服务
     */
    public void unBindService() {
        if (mConnection != null) {
            mConnection.disconnect();
        }
        Log.w(TAG, "unbind service ");
    }

    /**
     * 扫描蓝牙设备
     */
    public boolean scanBleDevice(ScanSmartListener mScanSmartListener) {
        this.mScanSmartListener = mScanSmartListener;
        boolean connect = false;
        if (mSmartBikeManager != null) {
            connect = mSmartBikeManager.scanSmartBike();
            Log.w(TAG, "scanSmartBike()   connect =" + connect);
        }
        Log.w(TAG, "scanBleDevice  mSmartBikeManager = null ");
        return connect;
    }

    /**
     * 停止扫描蓝牙设备
     */
    public boolean stopScanBleDevice() {
        boolean connect = false;
        if (mSmartBikeManager != null) {
            connect = mSmartBikeManager.stopScan();
        }
        Log.w(TAG, "stopScanBleDevice   connect =" + connect);
        return connect;
    }

    /**
     * 获取smart bike
     *
     * @param identifier identifier
     *                   return true:success
     */
    public boolean connectDeviceByIdentifier(String identifier, ScanSmartListener mScanSmartListener) {
        this.mScanSmartListener = mScanSmartListener;
        boolean connect = false;
        if (mSmartBikeManager != null) {
            connect = mSmartBikeManager.getDevice(identifier);
//            connectDevice();
        }
        Log.w(TAG, "connectDeviceByIdentifier   connect =" + connect);
        return connect;
    }

    /**
     * 配对码连接
     *
     * @param pair
     */
    public void connectDeviceByPair(String pair, SmartBikeListerner mSmartBikeListerner) {
        this.mSmartBikeListerner = mSmartBikeListerner;
        if (mSmartBike != null) {
            mSmartBike.pair(Integer.decode(pair));
        }
        Log.w(TAG, "connectDeviceByPair   pair =" + pair);
    }

    /**
     * key 直连
     *
     * @param key 秘钥
     */
    public void connectDeviceBykey(String key, SmartBikeListerner mSmartBikeListerner) {
        this.mSmartBikeListerner = mSmartBikeListerner;
        if (mSmartBike != null) {
            mSmartBike.setConnectionKey(key);
            mSmartBike.connect();
        }
        Log.w(TAG, "connectDeviceBykey   key =" + key);
    }

    /**
     * 连接smartbike
     */
    public void connectDevice() {
        if (mSmartBike != null) {
            mSmartBike.connect();
        }
    }

    /**
     * 断开smartbike
     */

    public void disConnectDevice() {
        if (!isSmartBikeAvailable()) return;
        mSmartBike.cancelConnect();
    }

    /**
     * smart bike 是否可用
     *
     * @return
     */
    public boolean isSmartBikeAvailable() {
        boolean connect = (mSmartBike != null && mSmartBike.connected());
        Log.w(TAG, "isSmartBikeAvailable connect=" + connect);
        return connect;
    }

    /**
     * 布防
     */
    public void lock() {
        if (!isSmartBikeAvailable()) return;
        if (!isRunning()) {
            mSmartBike.setState(BlueGuard.State.ARMED);
        } else {
            Toast.makeText(mContext, "行驶中，无法使用遥控功能!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 撤防
     */
    public void unlock() {
        if (!isSmartBikeAvailable()) return;
        if (!isRunning()) {
            mSmartBike.setState(BlueGuard.State.STOPPED);
        } else {
            Toast.makeText(mContext, "行驶中，无法使用撤防!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 打开后座
     */
    public void openSeat() {
        if (!isSmartBikeAvailable()) return;
        if (!isRunning()) {
            mSmartBike.openTrunk();
        } else {
            Toast.makeText(mContext, "行驶中，无法打开后备箱!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 上电
     */
    public void start() {
        if (!isSmartBikeAvailable()) return;
        if (!isRunning()) {
            mSmartBike.setState(BlueGuard.State.STARTED);
        } else {
            Toast.makeText(mContext, "行驶中，无法上电!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 断电
     */
    public void stop() {
        if (!isSmartBikeAvailable()) return;
        if (!isRunning()) {
            mSmartBike.setState(BlueGuard.State.STOPPED);
        } else {
            Toast.makeText(mContext, "行驶中，无法断电!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 寻车报警
     */
    public void findBikeAlarm() {
        if (!isSmartBikeAvailable()) return;
        if (!isRunning()) {
            mSmartBike.playSound(BlueGuard.Sound.FIND);
        } else {
            Toast.makeText(mContext, "行驶中，无法寻车!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * smart bike 是否在骑行（started running）
     *
     * @return true :state is stated or running
     */
    public boolean isRunning() {
        boolean running = (mSmartBike.state() == BlueGuard.State.RUNNING) || (mSmartBike.state() == BlueGuard.State.STARTED);
        Log.w(TAG, "isRunning() = " + running);
        if (running) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 扫描是否在继续
     *
     * @return
     */
    public boolean isScanning() {
        boolean scanIsRunning = false;
        if (mSmartBikeManager != null) {
            scanIsRunning = mSmartBikeManager.isScanning();
        }
        Log.w(TAG, "isScanning :" + scanIsRunning);
        return scanIsRunning;
    }

    /**
     * 服务连接的回调接口，表示服务有没有定成功
     */
    // BluxConnection delegate
    class ServerConnectionDelegate extends SmartBikeServerConnection.Delegate {

        /**
         * 服务连接成功
         *
         * @param smartBikeManager
         */
        @Override
        public void smartBikeServerConnected(SmartBikeManager smartBikeManager) {
            Log.w(TAG, "smartBikeServerConnected success");
            mSmartBikeManager = smartBikeManager;
            mSmartBikeManager.delegate = new SmartBikeManagerDelegate();
//            mSmartBikeManager.setContext(mContext);
            mServerConnectListerner.serverConnectted(smartBikeManager);

        }

        /**
         * 服务连接失败
         */
        @Override
        public void smartBikeServerDisconnected() {
            Log.w(TAG, "smartBikeServerDisconnected failed");
            mServerConnectListerner.serverDisConnectted();
        }
    }

    /**
     * SmartBikeManager 的回调接口
     */
    // SmartGuardManager delegate
    class SmartBikeManagerDelegate extends SmartBikeManager.Delegate {
        /**
         * find smart bike
         *
         * @param identifier
         * @param name
         */
        @Override
        public void smartBikeManagerFoundSmartBike(String identifier, String name) {
            Log.w(TAG, "smartBikeManagerFoundSmartBike  identifier:" + identifier + "  device name:" + name);
            mScanSmartListener.foundSmartBike(identifier, name);
        }

        /**
         * mSmartBikeManager.getDevice 的回调接口
         *
         * @param smartBike 蓝牙设备实例
         */
        @Override
        public void smartBikeManagerGotSmartBike(SmartBike smartBike) {
            Log.w(TAG, "smartBikeManagerGotSmartBike");
            mSmartBike = smartBike;
            mSmartBike.delegate = new SmartBikeDelegate();
            mScanSmartListener.getSmartBike(smartBike);
        }


    }

    // SmartBike delegate
    private class SmartBikeDelegate extends SmartBike.Delegate {

        @Override
        public void blueGuardConnected(BlueGuard blueGuard) {
            mSmartBike.getAccountManager(); //
            // TODO: 2016/12/26
            Log.w(TAG, "blueGuardConnected");
            mSmartBikeListerner.smartBikeConnected();
        }

        @Override
        public void blueGuardDisconnected(BlueGuard blueGuard, BlueGuard.DisconnectReason reason) {
            // TODO: 2016/12/26
            Log.w(TAG, "blueGuardDisconnected  reason : " + reason);
            mSmartBikeListerner.smartBikeDisConnectted(reason);
        }

        @Override
        public void blueGuardName(BlueGuard blueGuard, String name) {
            // TODO: 2016/12/26
            Log.w(TAG, "blueGuardName  name : " + name);
            mSmartBikeListerner.smartBikeName(name);
        }

        @Override
        public void blueGuardState(BlueGuard blueGuard, BlueGuard.State state) {
            // TODO: 2016/12/26
            Log.w(TAG, "blueGuardState  state : " + state);
            mSmartBikeListerner.smartBikeState(state);
        }


        @Override
        public void blueGuardPairResult(BlueGuard blueGuard, BlueGuard.PairResult result, String key) {
            Log.w(TAG, "BlueGuard.PairResult  result : " + result + "  key:" + key);
            mSmartBikeListerner.smartBikePairResult(blueGuard, result, key);
        }
    }

    /**
     * 各种操作的回调接口
     */
    public interface ScanSmartListener {
        void foundSmartBike(String identifier, String name);

        void getSmartBike(SmartBike smartBike);
    }

    private ScanSmartListener mScanSmartListener;

    public void setScanSmartListener(ScanSmartListener scanSmartListener) {
        this.mScanSmartListener = scanSmartListener;
    }

    //interface server
    public interface ServerConnectListerner {
        void serverConnectted(SmartBikeManager smartBikeManager);

        void serverDisConnectted();
    }

    private ServerConnectListerner mServerConnectListerner;

    public void setServerConnectListerner(ServerConnectListerner serverConnectListerner) {
        this.mServerConnectListerner = serverConnectListerner;
    }

    //smartbike
    public interface SmartBikeListerner {
        void smartBikeConnected();

        void smartBikeDisConnectted(BlueGuard.DisconnectReason reason);

        void smartBikeName(String name);

        void smartBikeState(BlueGuard.State state);

        void smartBikePairResult(BlueGuard blueGuard, BlueGuard.PairResult result, String key);
    }

    private SmartBikeListerner mSmartBikeListerner;

    public void setServerConnectListerner(SmartBikeListerner smartBikeListerner) {
        this.mSmartBikeListerner = smartBikeListerner;
    }

}
