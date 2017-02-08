package com.cargps.android.net;

import android.content.Context;
import android.os.Handler;

import com.fu.baseframe.utils.LogUtils;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class HttpRequest<T> {
    private final int TIME_OUT = 15 * 1000;

    private String urlStr;
    private HttpResponseImp<T> httpListener;
    private Map<String, String> param;
    private Handler handler = new Handler();
    private HashMap<String, String> headers = null;
    private String httpMethon;
    private Class<?> cls;

    public HttpRequest(Context ctx, String urlStr, HttpResponseListener<T> callback, Class<?> cls,
                       Map<String, String> param, String httpMethon, boolean isShowDialog) {
        this.urlStr = urlStr;
        this.param = param;
        httpListener = new HttpResponseImp<T>(ctx, callback, isShowDialog);
        this.cls = cls;
        this.httpMethon = httpMethon;
    }

    public void HttpHead() {
        try {
            if (httpMethon.equals("GET")) {
                httpGet();
            } else if (httpMethon.equals("POST")) {
                httpPost();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void httpGet() {
        handler.post(new Runnable() {
            public void run() {
                if (httpListener != null) {
                    httpListener.onStart();
                }
            }
        });

        LogUtils.logDug(urlStr);

        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIME_OUT);
        HttpGet httpGet = new HttpGet(urlStr);

        httpGet.setHeader("Content-Type", "application/json");

        if (headers != null) {
            for (String key : headers.keySet()) {
                httpGet.setHeader(key, headers.get(key));
            }
        }

        LogUtils.logDug("Request head = Content-Type:application/json");
        for (String key : headers.keySet()) {
            LogUtils.logDug("Request head = " + key + ":" + headers.get(key));
        }

        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
            int code = httpResponse.getStatusLine().getStatusCode();
            // 如果服务器成功地返回响应
            if (code == 200 || code == 201) {
                // 获取服务器响应字符串
                final String result = EntityUtils.toString(httpResponse.getEntity());
                LogUtils.logDug(result);
                handler.post(new Runnable() {
                    public void run() {
                        if (httpListener != null) {

                            httpListener.onFinish();
                            jsonToOjb(result);
                        }
                    }
                });

            } else {

                fail(code);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            fail(1000);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            fail(1000);
        }

    }

    private void httpPost() {
        HttpClient httpClient = new DefaultHttpClient();
        httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, TIME_OUT);
        httpClient.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);
        httpClient.getParams().setParameter(HTTP.CONTENT_ENCODING, HTTP.UTF_8);
        httpClient.getParams().setParameter(HTTP.CHARSET_PARAM, HTTP.UTF_8);
        httpClient.getParams().setParameter(HTTP.DEFAULT_PROTOCOL_CHARSET, HTTP.UTF_8);
        HttpPost httpPost = new HttpPost(urlStr);
        httpPost.getParams().setParameter("http.protocol.content-charset", HTTP.UTF_8);
        httpPost.getParams().setParameter(HTTP.CONTENT_ENCODING, HTTP.UTF_8);
        httpPost.getParams().setParameter(HTTP.CHARSET_PARAM, HTTP.UTF_8);
        httpPost.getParams().setParameter(HTTP.DEFAULT_PROTOCOL_CHARSET, HTTP.UTF_8);
        if (headers != null) {
            for (String key : headers.keySet()) {
                httpPost.setHeader(key, headers.get(key));
            }
        }

        try {

            LogUtils.logDug("Request url" + urlStr);
            LogUtils.logDug("Request methon:" + httpMethon);

            if (param != null) {
                for (String key : param.keySet()) {
                    LogUtils.logDug("Request param = " + key + ":" + param.get(key));
                }
            }

            handler.post(new Runnable() {
                public void run() {
                    if (httpListener != null) {
                        httpListener.onStart();
                    }
                }
            });

            String jsonStr = "";
            if (param != null) {

                if (param.containsKey("data")) {
                    jsonStr = param.get("data");
                } else {
                    JsonObject jsonObject = new JsonObject();
                    for (String key : param.keySet()) {
                        jsonObject.addProperty(key, param.get(key));
                    }
                    jsonStr = jsonObject.toString();
                }
            }


            httpPost.setHeader("Content-Type", "application/json");
            LogUtils.logDug("Request data" + jsonStr);

            StringEntity stringEntity = new StringEntity(jsonStr, "utf-8");
            stringEntity.setContentType("text/json;charset=UTF-8");
            stringEntity.setContentEncoding("utf-8");
            httpPost.setEntity(stringEntity);

            Header[] heads = httpPost.getAllHeaders();
            for (int i = 0; i < heads.length; i++) {
                Header header = heads[i];
                LogUtils.logDug("Request head = " + header.getName() + ":" + header.getValue());
            }

            HttpResponse httpResponse = httpClient.execute(httpPost);
            int code = httpResponse.getStatusLine().getStatusCode();

            if (code == 200 || code == 201) {
                final String result = EntityUtils.toString(httpResponse.getEntity());
                LogUtils.logDug(result);
                handler.post(new Runnable() {
                    public void run() {
                        if (httpListener != null) {

                            httpListener.onFinish();
                            jsonToOjb(result);
                        }
                    }
                });
            } else {
                fail(code);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            fail(1000);
        } catch (IOException e) {
            e.printStackTrace();
            fail(1001);
        }
    }

    private void fail(final int code) {
        LogUtils.logError("http error code = " + code);
        if (httpListener != null) {
            handler.post(new Runnable() {
                public void run() {
                    if (httpListener != null) {
                        httpListener.onFinish();
                        httpListener.onFail(code);
                    }
                }
            });

        }
    }

    public void addHead(String key, String val) {
        if (headers == null) {
            headers = new HashMap<String, String>();
        }
        headers.put(key, val);
    }

    @SuppressWarnings("unchecked")
    private void jsonToOjb(String json) {
        Gson gson = new Gson();
        Object response = gson.fromJson(json, cls);
        if (httpListener != null) {
            httpListener.onResult((T) response);
        }
    }


    public class PostMethodUTF8 extends HttpPost {

    }
}
