package com.cargps.android.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.baidu.wallet.core.utils.LogUtil;
import com.cargps.android.MyApplication;
import com.cargps.android.data.ConsumeOrder;
import com.cargps.android.data.OrderInfo;
import com.cargps.android.interfaces.DialogCallback;
import com.cargps.android.utils.DialogUtils;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class JPushReceive extends BroadcastReceiver {
    private static final String TAG = "JPush";

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
            //send the Registration Id to your server...

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
            processCustomMessage(context, bundle);

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);
            if (MyApplication.isActivity) {
                processCustomMessage(context, bundle);
            }

        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

            if (!MyApplication.isActivity) {
                com.cargps.android.MainActivity_.intent(context).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
            }

        } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
            Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
            //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

        } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
            boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
            Log.w(TAG, "[MyReceiver]" + intent.getAction() + " connected state change to " + connected);
        } else {
            Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
        }
    }

    // 打印所有的 intent extra 数据
    private static String printBundle(Bundle bundle) {
        StringBuilder sb = new StringBuilder();
        for (String key : bundle.keySet()) {
            if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
            } else if (key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)) {
                sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
            } else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
                if (bundle.getString(JPushInterface.EXTRA_EXTRA).isEmpty()) {
                    Log.i(TAG, "This message has no Extra data");
                    continue;
                }

                try {
                    JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
                    @SuppressWarnings("unchecked")
                    Iterator<String> it = json.keys();

                    while (it.hasNext()) {
                        String myKey = it.next().toString();
                        sb.append("\nkey:" + key + ", value: [" +
                                myKey + " - " + json.optString(myKey) + "]");
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Get message extra JSON error!");
                }

            } else {
                sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
            }
        }
        return sb.toString();
    }

    //send msg to MainActivity
    private void processCustomMessage(Context context, Bundle bundle) {
        OrderInfo order = null;
        if (MyApplication.isActivity) {
            String json = bundle.getString(JPushInterface.EXTRA_EXTRA);
            if (json.isEmpty()) return;
            try {
                PushOrder pushOrder = new Gson().fromJson(json, PushOrder.class);
                order = pushOrder.jsonObject;
            } catch (Exception e) {
                LogUtil.errord(e.getMessage());
            }

            if (order != null) {
                final ConsumeOrder consumeOrder = new ConsumeOrder();
                consumeOrder.orderId = order.orderId;
                consumeOrder.consume = order.orderAmount;
                consumeOrder.startTime = order.startTime;
                consumeOrder.endTime = order.endTime;
                consumeOrder.minutes = order.countTime;
                DialogUtils.getInstance().showPushMsgDialog(MyApplication.mainActivity, "通知", consumeOrder, new DialogCallback() {
                    @Override
                    public void confirm() {
                        super.confirm();
                        if (MyApplication.getInstance().isLogin()) {
                            MyApplication.isShowLoginPage = true;
                            com.cargps.android.model.activity.LoginActivity_.intent(MyApplication.mainActivity).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
                        } else {

                            com.cargps.android.model.activity.PayActivity_.intent(MyApplication.mainActivity).
                                    flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).order(consumeOrder).start();
                        }
                    }

                    @Override
                    public void cancle() {
                        super.cancle();

                        com.cargps.android.model.activity.MyOrderActivity_.intent(MyApplication.mainActivity).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();

                    }


                });
            }
        }
    }
}
