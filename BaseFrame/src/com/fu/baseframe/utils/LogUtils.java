package com.fu.baseframe.utils;

import android.util.Log;

/***
 * 日志工具
 *
 * @author fu
 */
public class LogUtils {
    private static String Tag = "fu.net";
    private static boolean showLog = true;

    public static void logDug(String msg) {
        if (!showLog) return;
        Log.d(Tag, msg);
    }

    public static void logError(String msg) {
        if (!showLog) return;
        Log.e(Tag, msg);
    }

    public static void logDug(String tag, String msg) {
        if (!showLog) return;
        Log.d(tag, msg);
    }

    public static void logError(String tag, String msg) {
        if (!showLog) return;
        Log.e(tag, msg);
    }

    public static void logDebug(byte[] datas) {
        if (datas == null) return;
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < datas.length; i++) {

            int hex = datas[i];
            sb.append(Integer.toHexString(hex) + ":");

            if (hex == 0x0d) {
                break;
            }

        }
        logDug(sb.toString());
    }

}
 