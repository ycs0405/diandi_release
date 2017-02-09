package com.cargps.android.model.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cargps.android.MyApplication;
import com.cargps.android.MyContacts;
import com.cargps.android.R;
import com.cargps.android.data.UserInfo;
import com.cargps.android.interfaces.ICallBack;
import com.cargps.android.net.HttpExecute;
import com.cargps.android.net.HttpRequest;
import com.cargps.android.net.HttpResponseListener;
import com.cargps.android.net.responseBean.StringResponse;
import com.cargps.android.utils.ImageLoadOptions;
import com.cargps.android.utils.SdUtil;
import com.fu.baseframe.utils.AppSysInfo;
import com.fu.baseframe.utils.SystemOpt;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class LeftMenu extends LinearLayout {
    public TextView nikeName, yajingTv, versionTv, regAndLogoutTv;
    public ImageView headImg;


    public LeftMenu(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LeftMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LeftMenu(Context context) {
        super(context);

        init();
    }

    public void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_left_menu_layout, this, true);

        regAndLogoutTv = (TextView) findViewById(R.id.regAndLogoutTv);
        nikeName = (TextView) findViewById(R.id.nikeName);
        yajingTv = (TextView) findViewById(R.id.yajingTv);
        versionTv = (TextView) findViewById(R.id.versionTv);
        headImg = (ImageView) findViewById(R.id.headImg);


        findViewById(R.id.headFl).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                com.cargps.android.model.activity.PersionInfoActivity_.intent(getContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        });

        findViewById(R.id.moneyLayout).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                com.cargps.android.model.activity.RefundMoneyActivity_.intent(getContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        });

        findViewById(R.id.orderLayout).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                com.cargps.android.model.activity.MyOrderActivity_.intent(getContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        });

        findViewById(R.id.msgLayout).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                com.cargps.android.model.activity.MsgActivity_.intent(getContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        });

        findViewById(R.id.callPhoneLayout).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + "4001787007"));
                getContext().startActivity(intent);
            }
        });

        findViewById(R.id.calcPriceLayout).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                com.cargps.android.model.activity.WebViewActivity_.intent(getContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).title("计费说明").url("jf.html").start();
            }
        });


        findViewById(R.id.feedBackLayout).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                com.cargps.android.model.activity.FeedBackActivity_.intent(getContext()).flags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        });


        findViewById(R.id.moreLayout).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                com.cargps.android.model.activity.MoreActivity_.intent(getContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();

            }
        });

        findViewById(R.id.sharedLayout).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ShareSDK.initSDK(getContext());
                OnekeyShare oks = new OnekeyShare();
                //关闭sso授权
                oks.disableSSOWhenAuthorize();
                // 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
                //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
                // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
                oks.setTitle("电滴出行");
                // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
                oks.setTitleUrl("http://www.qdigo.com");
                // text是分享文本，所有平台都需要这个字段
                oks.setText("风景太多，时间太少？电滴带你领略更多的风景！租电动车，就找电滴出行！http://www.qdigo.com");
                // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
                oks.setImagePath(SdUtil.getFile("dd_logo.png", true).getAbsolutePath());//确保SDcard下面存在此张图片
                // url仅在微信（包括好友和朋友圈）中使用
                oks.setUrl("http://www.qdigo.com");
                // comment是我对这条分享的评论，仅在人人网和QQ空间使用
                //oks.setComment("我是测试评论文本");
                // site是分享此内容的网站名称，仅在QQ空间使用
                oks.setSite("电滴出行");
                // siteUrl是分享此内容的网站地址，仅在QQ空间使用
                oks.setSiteUrl("http://www.qdigo.com");
                // 启动分享GUI
                oks.show(getContext());
            }
        });


        findViewById(R.id.updateLayout).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                updateApp();
            }
        });

        AppSysInfo appInfo = SystemOpt.getInstance().getAppSysInfo();
        if (appInfo != null) {
            versionTv.setText("版本更新 ( " + appInfo.getAppVersion() + " ) ");
        }
    }

    public void showUserInfo() {
        if (MyApplication.getInstance().userInfo != null) {
            regAndLogoutTv.setText("注销");
            UserInfo userInfo = MyApplication.getInstance().userInfo;
            nikeName.setText(userInfo.userName);
            yajingTv.setText(userInfo.myWallet + "元");
//            ImageLoader.getInstance().clearMemoryCache();
//            ImageLoader.getInstance().clearDiskCache();
            ImageLoader.getInstance().displayImage(userInfo.userImgurl, headImg, ImageLoadOptions.getListOptions(R.drawable.icon_defalut_head));
            nikeName.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                }
            });

            findViewById(R.id.loginOut).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    MyApplication.getInstance().loginOut(new ICallBack() {

                        @Override
                        public void callback() {
                            clearViewData();
                        }
                    });
                }
            });

        } else {
            nikeName.setText("登陆");
            regAndLogoutTv.setText("注册");
            nikeName.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    MyApplication.isShowLoginPage = true;
                    com.cargps.android.model.activity.LoginActivity_.intent(getContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
                }
            });

            clearViewData();
        }

    }

    public void clearViewData() {
        regAndLogoutTv.setText("注册");
        nikeName.setText("登陆");
        yajingTv.setText("0元");
        headImg.setImageResource(R.drawable.icon_defalut_head);

        findViewById(R.id.loginOut).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

            }
        });

        nikeName.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                MyApplication.isShowLoginPage = true;
                com.cargps.android.model.activity.LoginActivity_.intent(getContext()).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
            }
        });
    }

    private void updateApp() {
        if (MyApplication.getInstance().isLogin()) return;
        MyApplication app = MyApplication.getInstance();
        String urlStr = MyContacts.MAIN_URL + "v1.0/operation/getNewVersion";
        AppSysInfo appInfo = SystemOpt.getInstance().getAppSysInfo();

        Map<String, String> param = new HashMap<String, String>();
        param.put("osType", "android");
        param.put("currentVersion", appInfo.getVersionCode() + "");
        HttpRequest<StringResponse> httpRequest = new HttpRequest<StringResponse>(getContext(), urlStr, new HttpResponseListener<StringResponse>() {

            @Override
            public void onResult(StringResponse result) {

                if (result != null && result.statusCode == 200) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(result.data.startsWith("http") ? result.data : "http://" + result.data);
                    intent.setData(content_url);
                    getContext().startActivity(intent);
                } else {
                    Toast.makeText(getContext(), result.message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFail(int code) {
            }
        }, StringResponse.class, param, "POST", true);

        httpRequest.addHead("mobileNo", app.rootUserInfo.mobileNo);
        httpRequest.addHead("mobiledeviceId", SystemOpt.getInstance().getAppSysInfo().getDeviceId());
        httpRequest.addHead("accesstoken", app.rootUserInfo.accessToken);
        HttpExecute.getInstance().addRequest(httpRequest);
    }

}
