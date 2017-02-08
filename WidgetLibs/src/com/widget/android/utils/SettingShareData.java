package com.widget.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

/***
 * 数据存储
 *
 * @author tim.fu
 */
public class SettingShareData {
    private static SettingShareData setting = null;
    private static SharedPreferences read = null;
    private static SharedPreferences.Editor write = null;
    private String SETTING = "jzspark_preferences";
    private Context context;

    public SettingShareData(Context context) {
        super();
        this.context = context;
        SETTING = context.getPackageName();
        getShared();
        getWrite();
    }

    public static SettingShareData getInstance(Context context) {
        if (setting == null) {
            setting = new SettingShareData(context);
        }
        return setting;
    }

    public static SettingShareData getInstance(Context context, String xmlName) {
        if (setting == null) {
            setting = new SettingShareData(xmlName, context);
        } else {
            setting.setSETTING(xmlName);
        }

        return setting;
    }

    public SettingShareData(String sETTING, Context context) {
        super();
        getShared();
        getWrite();
        SETTING = sETTING;
        this.context = context;
    }

    public SharedPreferences getShared() {
        read = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE);
        return read;
    }

    public SharedPreferences.Editor getWrite() {
        write = context.getSharedPreferences(SETTING, Context.MODE_PRIVATE).edit();
        return write;
    }

    public SharedPreferences.Editor getWrite(String setting) {
        write = context.getSharedPreferences(setting, Context.MODE_PRIVATE).edit();
        return write;
    }

    public String getKeyValueString(String key, String defalut) {
        return read.getString(key, defalut);
    }

    public boolean containsKey(String key) {
        return read.contains(key);
    }

    public boolean getKeyValueBoolean(String key, boolean b) {
        return read.getBoolean(key, b);
    }

    public int getKeyValueInt(String key, int index) {
        return read.getInt(key, index);
    }

    public long getKeyValueLong(String key, int index) {
        return read.getLong(key, index);
    }

    public float getKeyValueFloat(String key) {
        return read.getFloat(key, 0f);
    }


    /**
     * 存String类型
     *
     * @param key
     * @param value
     */
    public void setKeyValue(String key, String value) {
        write.putString(key, value);
        write.commit();
    }

    /**
     * boolean 类型
     *
     * @param key
     * @param value
     */
    public void setKeyValue(String key, boolean value) {
        write.putBoolean(key, value);
        write.commit();
    }

    /**
     * int类型
     *
     * @param key
     * @param value
     */
    public void setKeyValue(String key, int value) {
        write.putInt(key, value);
        write.commit();
    }

    /**
     * float类型
     *
     * @param key
     * @param value
     */
    public void setKeyValue(String key, float value) {
        write.putFloat(key, value);
        write.commit();
    }

    public void setKeyValue(String key, long value) {
        write.putLong(key, value);
        write.commit();
    }

    public String getSETTING() {
        return SETTING;
    }

    public void setSETTING(String sETTING) {
        SETTING = sETTING;

        getShared();
        getWrite();
    }

}
