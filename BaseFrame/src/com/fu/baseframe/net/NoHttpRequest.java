package com.fu.baseframe.net;

import android.content.Context;

import com.fu.baseframe.FrameApplication;
import com.fu.baseframe.R;
import com.fu.baseframe.utils.LogUtils;
import com.yolanda.nohttp.FileBinary;
import com.yolanda.nohttp.NoHttp;
import com.yolanda.nohttp.Request;
import com.yolanda.nohttp.RequestMethod;

import java.io.File;
import java.util.Map;


/***
 * NoHttp网络框架
 *
 * @author fu
 */
public class NoHttpRequest {
    public static int dialogLayout = R.layout.dialog_loading_layout;

    private static NoHttpRequest httpRequest;

    public static NoHttpRequest getInstance() {
        return httpRequest == null ? httpRequest = new NoHttpRequest() : httpRequest;
    }

    private NoHttpRequest() {

    }

    public void init(Context ctx) {
        NoHttp.init(FrameApplication.getInstance());
    }

    public <T> void requestPost(Context context, int what, Map<String, String> params, String interfaceUrl, Class<?> cls, HttpListener<T> callback, boolean isShowDialog) {
        LogUtils.logDug("net request start");
        LogUtils.logDug("request what =  " + what);
        LogUtils.logDug("request method = POST");
        LogUtils.logDug("request url = " + FrameApplication.getInstance().getNetUrl() + interfaceUrl);
        LogUtils.logDug("request params :");

        for (String key : params.keySet()) {
            LogUtils.logDug(key + " :" + params.get(key));
        }
//		 Type type = ((ParameterizedType) callback.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
//		 Class<T> cls = null;
//         if (type instanceof Class<?>) {
//        	 cls = ((Class<T>) type);
//         } else if (type instanceof ParameterizedType) {
//        	 cls =((Class<T>) ((ParameterizedType) type).getRawType());
//         }

        Request<T> request = new CustomDataRequest<T>(FrameApplication.getInstance().getNetUrl() + interfaceUrl, RequestMethod.POST, cls);
        request.add(params);
        CallServer.getRequestInstance().add(context, what, request, callback, FrameApplication.mainActivity, isShowDialog);

    }

    public <T> void requestGet(Context context, int what, String interfaceUrl, Class<?> cls, HttpListener<T> callback, boolean isShowDialog) {
        LogUtils.logDug("net request start");
        LogUtils.logDug("request what =  " + what);
        LogUtils.logDug("request method = Get");
        LogUtils.logDug("request url = " + FrameApplication.getInstance().getNetUrl() + interfaceUrl);


        Request<T> request = new CustomDataRequest<T>(FrameApplication.getInstance().getNetUrl() + interfaceUrl, RequestMethod.GET, cls);
        request.setConnectTimeout(60 * 1000);
        request.setReadTimeout(60 * 1000);
        CallServer.getRequestInstance().add(context, what, request, callback, FrameApplication.mainActivity, isShowDialog);

    }

    public <T> void requestGetHead(Context context, int what, String interfaceUrl, Map<String, String> params, Class<?> cls, HttpListener<T> callback, boolean isShowDialog) {
        LogUtils.logDug("net request start");
        LogUtils.logDug("request what =  " + what);
        LogUtils.logDug("request method = Get");
        LogUtils.logDug("request url = " + FrameApplication.getInstance().getNetUrl() + interfaceUrl);


        Request<T> request = new CustomDataRequest<T>(FrameApplication.getInstance().getNetUrl() + interfaceUrl, RequestMethod.GET, cls);
        request.setConnectTimeout(60 * 1000);
        request.setReadTimeout(60 * 1000);
        for (String key : params.keySet()) {
            request.addHeader(key, params.get(key));
        }
        CallServer.getRequestInstance().add(context, what, request, callback, FrameApplication.mainActivity, isShowDialog);
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
        LogUtils.logDug("net request start");
        LogUtils.logDug("request what =  " + what);
        LogUtils.logDug("request method = POST");
        LogUtils.logDug("request url = " + FrameApplication.getInstance().getNetUrl());
        LogUtils.logDug("request params :");
        LogUtils.logDug("filePath = " + fileAbspath);

        if (!(new File(fileAbspath).exists())) {
            LogUtils.logDug("文件不存在");
            return;
        }
        Request<T> request = new CustomDataRequest<T>(FrameApplication.getInstance().getNetUrl() + interfaceUrl, RequestMethod.POST, cls);
        request.add("file", new FileBinary(new File(fileAbspath)));
        CallServer.getRequestInstance().add(context, what, request, callback, FrameApplication.mainActivity, isShowDialog);
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
        LogUtils.logDug("net request start");
        LogUtils.logDug("request what =  " + what);
        LogUtils.logDug("request method = POST");
        LogUtils.logDug("request url = " + FrameApplication.getInstance().getNetUrl());
        LogUtils.logDug("request params :");
        LogUtils.logDug("filePath :");
        for (String string : fileAbspath) {
            if (string != null) {
                LogUtils.logDug(string);
            }
        }

        Request<T> request = new CustomDataRequest<T>(FrameApplication.getInstance().getNetUrl() + interfaceUrl, RequestMethod.POST, cls);


        for (int i = 0; i < fileAbspath.length; i++) {
            String path = fileAbspath[i];
            if (path != null) {
                request.add("file" + i, new FileBinary(new File(path)));
            }

        }
        CallServer.getRequestInstance().add(context, what, request, callback, FrameApplication.mainActivity, isShowDialog);
    }
}
