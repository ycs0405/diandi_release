package com.cargps.android.bluetooth;

import android.content.Context;
import android.content.SharedPreferences;


public class DeviceDB {
    public static class Record {
        public String name;
        public String identifier;
        public String key;

        public Record(String name, String identifier, String key) {
            this.name = name;
            this.identifier = identifier;
            this.key = key;
        }
    }

    // Private constants.
    private final static String BLUE_GUARD_SETTINGS = "blue_guard_settings";
    private final static String THE_BLUE_GUARD_ID = "blue_guard_id";
    private final static String THE_BLUE_GUARD_KEY = "blue_guard_key";
    private final static String THE_BLUE_GUARD_NAME = "blue_guard_name";

    // Persist last connected BlueGuard.
    public static void save(Context context, Record rec) {
        SharedPreferences sPreferences = context.getSharedPreferences(BLUE_GUARD_SETTINGS, 0);
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(THE_BLUE_GUARD_ID, rec.identifier);
        editor.putString(THE_BLUE_GUARD_KEY, rec.key);
        editor.putString(THE_BLUE_GUARD_NAME, rec.name);
        editor.apply();
    }

    public static void delete(Context context) {
        SharedPreferences sPreferences = context.getSharedPreferences(BLUE_GUARD_SETTINGS, 0);
        SharedPreferences.Editor editor = sPreferences.edit();
        editor.putString(THE_BLUE_GUARD_ID, null);
        editor.putString(THE_BLUE_GUARD_KEY, null);
        editor.putString(THE_BLUE_GUARD_NAME, null);
        editor.apply();
    }

    public static Record load(Context context) {
        SharedPreferences sPreferences = context.getSharedPreferences(BLUE_GUARD_SETTINGS, 0);
        String identifier = sPreferences.getString(THE_BLUE_GUARD_ID, "");
        String name = sPreferences.getString(THE_BLUE_GUARD_NAME, "");
        String key = sPreferences.getString(THE_BLUE_GUARD_KEY, "");

        if (identifier.length() == 0)
            return null;

        return new Record(name.length() > 0 ? name : null,
                identifier.length() > 0 ? identifier : null,
                key.length() > 0 ? key : null);
    }

}
