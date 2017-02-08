package com.fu.baseframe.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/***
 * 系统操作
 *
 * @author fu
 */
public class SystemOpt {
    public int heightPixels = 800;
    public int widthPixels = 480;
    private Context ctx;
    public AppSysInfo appSysInfo;

    private static SystemOpt systemOpt;

    public static SystemOpt getInstance() {
        return systemOpt == null ? systemOpt = new SystemOpt() : systemOpt;
    }

    private SystemOpt() {

    }

    public void init(Context ctx) {
        this.ctx = ctx;
        int[] screenSizeArray = getDisplayScreenResolution(ctx);
        widthPixels = screenSizeArray[0];
        heightPixels = screenSizeArray[1];
        LogUtils.logDug("screen width = " + widthPixels);
        LogUtils.logDug("screen height = " + heightPixels);
        LogUtils.logDug("screen width of height scale = " + ((float) widthPixels) / ((float) heightPixels));
        LogUtils.logDug("screen height of width scale = " + ((float) heightPixels) / ((float) widthPixels));

        getAppInfo();

    }

    @SuppressWarnings("deprecation")
    public void getAppInfo() {


        appSysInfo = new AppSysInfo();
        PackageManager pm = ctx.getPackageManager();

        try {
            PackageInfo packageInfo = pm.getPackageInfo(ctx.getPackageName(), 0);
            appSysInfo.setAppVersion(packageInfo.versionName);
            appSysInfo.setVersionCode(packageInfo.versionCode);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        appSysInfo.setManufacturer(android.os.Build.MANUFACTURER);
        appSysInfo.setPhoneType(android.os.Build.MODEL);
        appSysInfo.setSdkVersion(android.os.Build.VERSION.SDK);
        appSysInfo.setSystemVersion(android.os.Build.VERSION.RELEASE);

        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        appSysInfo.setDeviceId(tm.getDeviceId());
        appSysInfo.setPhone(tm.getLine1Number());


        Method methods[] = appSysInfo.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            try {
                String name = methods[i].getName();
                if (!name.startsWith("get")) continue;
                Method m = appSysInfo.getClass().getMethod(name);
                String value = String.valueOf(m.invoke(appSysInfo));    //调用getter方法获取属�?��??
                if (value != null) {
                    LogUtils.logDug(methods[i].getName() + "=" + value);
                }
            } catch (NoSuchMethodException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }


    public AppSysInfo getAppSysInfo() {
        return appSysInfo;
    }

    public int[] getDisplayScreenResolution(Context context) {
        int[] screenSizeArray = new int[2];

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);

        screenSizeArray[0] = dm.widthPixels;
        screenSizeArray[1] = dm.heightPixels;
        int ver = Build.VERSION.SDK_INT;
        if (ver < 13) {
            screenSizeArray[1] = dm.heightPixels;
        } else if (ver == 13) {
            try {
                Method mt = display.getClass().getMethod("getRealHeight");
                screenSizeArray[1] = (Integer) mt.invoke(display);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (ver > 13) {
            try {
                Method mt = display.getClass().getMethod("getRawHeight");
                screenSizeArray[1] = (Integer) mt.invoke(display);

            } catch (Exception e) {
                screenSizeArray[1] = dm.heightPixels;
            }
        }

        return screenSizeArray;
    }
}
