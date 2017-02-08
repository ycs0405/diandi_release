//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.cargps.android.model.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import com.cargps.android.R.id;
import com.cargps.android.R.layout;
import org.androidannotations.api.builder.ActivityIntentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

@SuppressLint({
    "SetJavaScriptEnabled",
    "JavascriptInterface"
})
public final class WebViewActivity_
    extends WebViewActivity
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();
    public final static String LOCAL_EXTRA = "local";
    public final static String LV_EXTRA = "lv";
    public final static String FROM_DATE_EXTRA = "fromDate";
    public final static String DAY_NUM_EXTRA = "dayNum";
    public final static String CALL_JS_EXTRA = "callJS";
    public final static String URL_EXTRA = "url";
    public final static String TO_DATE_EXTRA = "toDate";
    public final static String TITLE_EXTRA = "title";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
        setContentView(layout.activity_webview_layout);
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
        injectExtras_();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    @Override
    public void setContentView(View view, LayoutParams params) {
        super.setContentView(view, params);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static WebViewActivity_.IntentBuilder_ intent(Context context) {
        return new WebViewActivity_.IntentBuilder_(context);
    }

    public static WebViewActivity_.IntentBuilder_ intent(android.app.Fragment fragment) {
        return new WebViewActivity_.IntentBuilder_(fragment);
    }

    public static WebViewActivity_.IntentBuilder_ intent(android.support.v4.app.Fragment supportFragment) {
        return new WebViewActivity_.IntentBuilder_(supportFragment);
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        webView = ((WebView) hasViews.findViewById(id.webView));
        initView();
    }

    private void injectExtras_() {
        Bundle extras_ = getIntent().getExtras();
        if (extras_!= null) {
            if (extras_.containsKey(LOCAL_EXTRA)) {
                local = extras_.getBoolean(LOCAL_EXTRA);
            }
            if (extras_.containsKey(LV_EXTRA)) {
                lv = extras_.getInt(LV_EXTRA);
            }
            if (extras_.containsKey(FROM_DATE_EXTRA)) {
                fromDate = extras_.getString(FROM_DATE_EXTRA);
            }
            if (extras_.containsKey(DAY_NUM_EXTRA)) {
                dayNum = extras_.getString(DAY_NUM_EXTRA);
            }
            if (extras_.containsKey(CALL_JS_EXTRA)) {
                callJS = extras_.getBoolean(CALL_JS_EXTRA);
            }
            if (extras_.containsKey(URL_EXTRA)) {
                url = extras_.getString(URL_EXTRA);
            }
            if (extras_.containsKey(TO_DATE_EXTRA)) {
                toDate = extras_.getString(TO_DATE_EXTRA);
            }
            if (extras_.containsKey(TITLE_EXTRA)) {
                title = extras_.getString(TITLE_EXTRA);
            }
        }
    }

    @Override
    public void setIntent(Intent newIntent) {
        super.setIntent(newIntent);
        injectExtras_();
    }

    public static class IntentBuilder_
        extends ActivityIntentBuilder<WebViewActivity_.IntentBuilder_>
    {

        private android.app.Fragment fragment_;
        private android.support.v4.app.Fragment fragmentSupport_;

        public IntentBuilder_(Context context) {
            super(context, WebViewActivity_.class);
        }

        public IntentBuilder_(android.app.Fragment fragment) {
            super(fragment.getActivity(), WebViewActivity_.class);
            fragment_ = fragment;
        }

        public IntentBuilder_(android.support.v4.app.Fragment fragment) {
            super(fragment.getActivity(), WebViewActivity_.class);
            fragmentSupport_ = fragment;
        }

        @Override
        public void startForResult(int requestCode) {
            if (fragmentSupport_!= null) {
                fragmentSupport_.startActivityForResult(intent, requestCode);
            } else {
                if (fragment_!= null) {
                    if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                        fragment_.startActivityForResult(intent, requestCode, lastOptions);
                    } else {
                        fragment_.startActivityForResult(intent, requestCode);
                    }
                } else {
                    if (context instanceof Activity) {
                        Activity activity = ((Activity) context);
                        ActivityCompat.startActivityForResult(activity, intent, requestCode, lastOptions);
                    } else {
                        if (VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                            context.startActivity(intent, lastOptions);
                        } else {
                            context.startActivity(intent);
                        }
                    }
                }
            }
        }

        public WebViewActivity_.IntentBuilder_ local(boolean local) {
            return super.extra(LOCAL_EXTRA, local);
        }

        public WebViewActivity_.IntentBuilder_ lv(int lv) {
            return super.extra(LV_EXTRA, lv);
        }

        public WebViewActivity_.IntentBuilder_ fromDate(String fromDate) {
            return super.extra(FROM_DATE_EXTRA, fromDate);
        }

        public WebViewActivity_.IntentBuilder_ dayNum(String dayNum) {
            return super.extra(DAY_NUM_EXTRA, dayNum);
        }

        public WebViewActivity_.IntentBuilder_ callJS(boolean callJS) {
            return super.extra(CALL_JS_EXTRA, callJS);
        }

        public WebViewActivity_.IntentBuilder_ url(String url) {
            return super.extra(URL_EXTRA, url);
        }

        public WebViewActivity_.IntentBuilder_ toDate(String toDate) {
            return super.extra(TO_DATE_EXTRA, toDate);
        }

        public WebViewActivity_.IntentBuilder_ title(String title) {
            return super.extra(TITLE_EXTRA, title);
        }

    }

}