package com.cargps.android.model.activity;

import android.annotation.SuppressLint;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cargps.android.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
@EActivity(R.layout.activity_webview_layout)
public class WebViewActivity extends BaseActivity {

    @ViewById
    WebView webView;

    @Extra
    String title, url;

    @Extra
    String fromDate, toDate, dayNum;

    @Extra
    int lv = 300;

    @Extra
    boolean callJS = false;

    @Extra
    boolean local = true;

    @AfterViews
    public void initView() {
        super.initViews();

        setTitleText(title);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebClient());
        webView.addJavascriptInterface(new JavaScriptInterface(), "load");

        if (local) {
            webView.loadUrl("file:///android_asset/" + url);
        } else {
            webView.loadUrl(url);
        }

    }

    public class JavaScriptInterface {

    }

    public class WebClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

            if (callJS) {

            }
        }
    }
}
