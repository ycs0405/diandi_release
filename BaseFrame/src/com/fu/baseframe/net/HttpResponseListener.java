/*
 * Copyright © YOLANDA. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fu.baseframe.net;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.Toast;

import com.fu.baseframe.FrameApplication;
import com.fu.baseframe.R;
import com.fu.baseframe.utils.LogUtils;
import com.yolanda.nohttp.OnResponseListener;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.Response;
import com.yolanda.nohttp.error.ClientError;
import com.yolanda.nohttp.error.NetworkError;
import com.yolanda.nohttp.error.NotFoundCacheError;
import com.yolanda.nohttp.error.ServerError;
import com.yolanda.nohttp.error.TimeoutError;
import com.yolanda.nohttp.error.URLError;
import com.yolanda.nohttp.error.UnKnownHostError;

/**
 * Created in Nov 4, 2015 12:02:55 PM.
 *
 * @author YOLANDA;
 */
public class HttpResponseListener<T> implements OnResponseListener<T> {


    private Dialog dialog;

    @SuppressWarnings("unused")
    private Request<?> mRequest;

    private Context ctx;

    private boolean isShowDialog;

    /**
     * 结果回调.
     */
    private HttpListener<T> callback;


    /**
     * @param context      context用来实例化dialog.
     * @param request      请求对象.
     * @param httpCallback 回调对象.
     */
    public HttpResponseListener(Context context, Request<?> request, HttpListener<T> httpCallback, Context ctx, boolean isShowDialog) {
        this.mRequest = request;
        this.callback = httpCallback;
        this.ctx = context;
        this.isShowDialog = isShowDialog;
    }

    /**
     * 开始请求, 这里显示一个dialog.
     */
    @Override
    public void onStart(int what) {
        if (this.ctx instanceof Activity) {
            if (((Activity) ctx).isDestroyed()) {
                return;
            }
        }
        if (!isShowDialog) return;
        showLoadingDialog();
    }

    /**
     * 结束请求, 这里关闭dialog.
     */
    @Override
    public void onFinish(int what) {
        if (!isShowDialog) return;
        cancelDialog();
    }

    /**
     * 成功回调.
     */
    @Override
    public void onSucceed(int what, Response<T> response) {
        if (callback != null) {
            callback.onSucceed(what, response);
//            if(response.isSucceed()){
//            	if(response.get() != null){
//            		BaseResponse baseResponse = (BaseResponse) response.get();
//            		if(!baseResponse.Success){
//            			showToast(baseResponse.Msg);
//            			if(baseResponse.Code == 100){
//            				
//            				
//            				CallServer.getRequestInstance().cancelAll();
//            				ctx.sendBroadcast(new Intent("close"));
//            				MyApplication.mainActivity.finish();
//            				
//            			}
//            		}
//            	}
//            }
        }


    }

    /**
     * 失败回调.
     */
    @Override
    public void onFailed(int what, String url, Object tag, Exception exception, int responseCode, long networkMillis) {
        if (exception instanceof ClientError) {// 客户端错误
            if (responseCode == 400) {//服务器未能理解请求。
                showToast("错误的请求，服务器表示不能理解。");
            } else if (responseCode == 403) {// 请求的页面被禁止
                showToast("错误的请求，服务器表示不愿意。");
            } else if (responseCode == 404) {// 服务器无法找到请求的页面
                showToast("错误的请求，服务器表示找不到。");
            } else {// 400-417都是客户端错误，开发者可以自己去查询噢
                showToast("错误的请求，服务器表示伤不起。");
            }
        } else if (exception instanceof ServerError) {// 服务器错误
            if (500 == responseCode) {
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
            } else {
                showToast("服务器有问题。");
            }
        } else if (exception instanceof NetworkError) {// 网络不好
            showToast("请检查网络。");
        } else if (exception instanceof TimeoutError) {// 请求超时
            showToast("请求超时，网络不好或者服务器不稳定。");
        } else if (exception instanceof UnKnownHostError) {// 找不到服务器
            showToast("未发现指定服务器。");
        } else if (exception instanceof URLError) {// URL是错的
            showToast("URL错误。");
        } else if (exception instanceof NotFoundCacheError) {
            // 这个异常只会在仅仅查找缓存时没有找到缓存时返回
            showToast("没有发现缓存。");
        } else {
            showToast("未知错误。");
        }
        LogUtils.logError(exception.getMessage());
        if (callback != null)
            callback.onFailed(what, url, tag, exception, responseCode, networkMillis);
    }

    private void showLoadingDialog() {
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

        dialog.setContentView(NoHttpRequest.dialogLayout);

        dialog.show();
    }

    private void cancelDialog() {
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
    }

    private void showToast(String str) {
        Toast.makeText(ctx, str, Toast.LENGTH_SHORT).show();
    }

}
