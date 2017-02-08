package com.fu.baseframe.net;

import com.yolanda.nohttp.Headers;
import com.yolanda.nohttp.RestRequest;

public class HeadHttpRequest<T> extends RestRequest<T> {

    public HeadHttpRequest(String url) {
        super(url);
        // TODO Auto-generated constructor stub
    }

    @Override
    public T parseResponse(String url, Headers responseHeaders, byte[] responseBody) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getAccept() {
        // TODO Auto-generated method stub
        return null;
    }

}
