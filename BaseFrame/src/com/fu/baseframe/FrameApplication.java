package com.fu.baseframe;


import android.app.Application;

import com.fu.baseframe.net.NoHttpRequest;
import com.fu.baseframe.utils.SystemOpt;

/***
 * 扩展application
 *
 * @author fu
 */
public class FrameApplication extends Application {
    public int heightPixels = 800;
    public int widthPixels = 480;
    public SystemOpt systemOpt = SystemOpt.getInstance();

    public static MainActivity mainActivity;

    private String netUrl = "";

    private static FrameApplication application;

    public static FrameApplication getInstance() {
        return application;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;

        initSystem();
    }

    public final void initSystem() {
        systemOpt.init(this);//读取系统信息

        NoHttpRequest.getInstance().init(this);

        widthPixels = systemOpt.widthPixels;
        heightPixels = systemOpt.heightPixels;

    }

    public void setNetUrl(String url) {
        this.netUrl = url;
    }

    public String getNetUrl() {
        return netUrl;
    }


}
