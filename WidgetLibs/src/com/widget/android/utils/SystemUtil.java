package com.widget.android.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;
import android.util.Log;

public class SystemUtil {

    /**
     * 得到分辨率高度
     */
    public static int heightPs = -1;
    /**
     * 得到分辨率宽度
     */
    public static int widthPs = -1;
    /**
     * 得到屏幕密度
     */
    public static int densityDpi = -1;
    /**
     * 得到X轴密度
     */
    public static float Xdpi = -1;
    /**
     * 得到Y轴密度
     */
    public static float Ydpi = -1;

    public static int versionCode;
    public static String versionName;


    /***
     * 得到手机的屏幕基本信息
     *
     * @param context
     */
    public static void getScreen(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        heightPs = metrics.heightPixels;
        widthPs = metrics.widthPixels;
        densityDpi = metrics.densityDpi;
        Xdpi = metrics.xdpi;
        Ydpi = metrics.ydpi;
        getVersion(context);
        Log.i("手机分辨率", "分辨率：" + widthPs + "X" + heightPs + "    屏幕密度：" + densityDpi + "    宽高密度：" + Xdpi + "X" + Ydpi);
    }

    /***
     * 获取客户端版本
     *
     * @param context
     * @return
     */
    public static void getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(),
                    0);
            versionName = info.versionName;
            versionCode = info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 把密度dip单位转化为像数px单位
     *
     * @param context
     * @param dip
     * @return
     */
    public static int dipToPx(Context context, int dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    /***
     * 把像数px转化为密度dip单位
     *
     * @param context
     * @param px
     * @return
     */
    public static int pxToDip(Context context, int px) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f * (px >= 0 ? 1 : -1));
    }


}
