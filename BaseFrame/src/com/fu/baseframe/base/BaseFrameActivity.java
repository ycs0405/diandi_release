package com.fu.baseframe.base;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.fu.baseframe.net.HttpListener;
import com.fu.baseframe.net.NoHttpRequest;

import java.util.Map;

public class BaseFrameActivity extends Activity {
    public <T> void requestPost(int what, Map<String, String> params, String interfaceUrl, Class<?> cls, HttpListener<T> callback, boolean isShowDialog) {
        NoHttpRequest.getInstance().requestPost(this, what, params, interfaceUrl, cls, callback, isShowDialog);
    }

    public <T> void requestGet(Context context, int what, String interfaceUrl, Class<?> cls, HttpListener<T> callback, boolean isShowDialog) {
        NoHttpRequest.getInstance().requestGet(context, what, interfaceUrl, cls, callback, isShowDialog);
    }

    public <T> void requestGetHead(Context context, int what, String interfaceUrl, Map<String, String> params, Class<?> cls, HttpListener<T> callback, boolean isShowDialog) {
        NoHttpRequest.getInstance().requestGetHead(context, what, interfaceUrl, params, cls, callback, isShowDialog);
    }

    /****
     * 上传文件
     *
     * @param what
     * @param fileAbspath
     * @param interfaceUrl
     * @param callback
     * @param isShowDialog
     */
    public <T> void upFilePost(Context context, int what, String fileAbspath, String interfaceUrl, Class<?> cls, HttpListener<T> callback, boolean isShowDialog) {
        NoHttpRequest.getInstance().upFilePost(context, what, fileAbspath, interfaceUrl, cls, callback, isShowDialog);
    }

    /****
     * 上传文件
     *
     * @param what
     * @param fileAbspath
     * @param interfaceUrl
     * @param callback
     * @param isShowDialog
     */
    public <T> void upFilePost(Context context, int what, String fileAbspath[], String interfaceUrl, Class<?> cls, HttpListener<T> callback, boolean isShowDialog) {
        NoHttpRequest.getInstance().upFilePost(context, what, fileAbspath, interfaceUrl, cls, callback, isShowDialog);
    }

    public void showToast(String str) {
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }
}
