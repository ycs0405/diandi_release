package com.cargps.android.net;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.cargps.android.MyApplication;
import com.cargps.android.net.responseBean.BaseResponse;
import com.fu.baseframe.FrameApplication;
import com.fu.baseframe.R;

public class HttpResponseImp<T> implements HttpListener<T> {
    private HttpResponseListener<T> callback;
    private boolean isShowDialog;
    private Dialog dialog;
    private Context ctx;

    public HttpResponseImp(Context ctx, HttpResponseListener<T> callback, boolean isShowDialog) {
        super();
        this.callback = callback;
        this.ctx = ctx;
        this.isShowDialog = isShowDialog;
    }

    @Override
    public void onStart() {
        showLoadingDialog();
        if (callback != null) {
            callback.onStart();
        }
    }

    @Override
    public void onFinish() {
        cancelDialog();
        if (callback != null) {
            callback.onFinish();
        }
    }

    @Override
    public void onResult(T result) {
        if (callback != null) {
            callback.onResult(result);
            BaseResponse response = (BaseResponse) result;
            if (response.statusCode == 405) {
                if (!MyApplication.isShowLoginPage) {
                    MyApplication.isShowLoginPage = true;
                    com.cargps.android.model.activity.LoginActivity_.intent(ctx).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
                }
            }
        }
    }

    @Override
    public void onFail(int responseCode) {
        cancelDialog();
        if (callback != null) {
            callback.onFail(responseCode);
            if (responseCode == 400) {//服务器未能理解请求。
                showToast("错误的请求，服务器表示不能理解。");
            } else if (responseCode == 403) {// 请求的页面被禁止
                showToast("错误的请求，服务器表示不愿意。");
            } else if (responseCode == 404) {// 服务器无法找到请求的页面
                showToast("错误的请求，服务器表示找不到。");
            } else if (500 == responseCode) {
                showToast("服务器遇到不可预知的情况。");
            } else if (501 == responseCode) {
                showToast("服务器不支持的请求。");
            } else if (502 == responseCode) {
                showToast("服务器从上游服务器收到一个无效的响应。");
            } else if (503 == responseCode) {
                showToast("服务器临时过载或当机。");
            } else if (504 == responseCode) {
                showToast("网关超时。");
            } else if (505 == responseCode) {
                showToast("服务器不支持请求中指明的HTTP协议版本。");
            } else if (1001 == responseCode) {
                showToast("没有网络，请连接网络！");
            } else {
                showToast("服务器有问题。");
            }
        }
    }

    private void showLoadingDialog() {
        if (!isShowDialog) return;
        dialog = new Dialog(ctx, R.style.custom_dialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });

        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = FrameApplication.getInstance().widthPixels;
        lp.height = FrameApplication.getInstance().heightPixels;

        dialog.setContentView(R.layout.dialog_loading_layout);

        dialog.show();
    }

    private void cancelDialog() {
        if (dialog != null) dialog.dismiss();
    }

    private void showToast(String str) {
        Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
    }


}
