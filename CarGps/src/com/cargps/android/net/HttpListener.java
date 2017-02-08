package com.cargps.android.net;

public interface HttpListener<T> {
    void onStart();

    void onFinish();

    void onResult(T result);

    void onFail(int code);
}
