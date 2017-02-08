package com.cargps.android.net;

public abstract class HttpResponseListener<T> {
    public abstract void onResult(T result);

    public abstract void onFail(int code);

    public void onStart() {
    }

    public void onFinish() {
    }
}
