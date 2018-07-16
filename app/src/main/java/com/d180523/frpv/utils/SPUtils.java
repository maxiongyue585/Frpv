package com.d180523.frpv.utils;


import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SPUtils {

    private static SPUtils instance;

    private SharedPreferences preferences;

    public static final String TOKEN = "token";

    private SPUtils(Context context) {
        preferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
    }

    public static SPUtils getInstance() {
        return getInstance(null);
    }

    public static SPUtils getInstance(Context context) {

        if (instance == null) {

            synchronized (SPUtils.class) {
                instance = new SPUtils(context);
            }
        }
        return instance;
    }

    public void setProperty(String key, String value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void setProperty(String key, Set<String> strs) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(key, strs);
        editor.commit();
    }

    public void setProperty(String key, boolean value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public void setProperty(String key, int value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void setProperty(String key, long value) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }


    public String getProperty(String key) {
        return preferences.getString(key, null);
    }

    public String getProperty(String key, String defValue) {
        return preferences.getString(key, defValue);
    }

    public boolean hasProperty(String key) {
        return preferences.contains(key);
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean defValue) {
        return preferences.getBoolean(key, defValue);
    }


    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public int getInt(String key, int defValue) {
        return preferences.getInt(key, defValue);
    }

    public long getLong(String key) {
        return preferences.getLong(key, 0);
    }

    public long getLong(String key, Long defValue) {
        return preferences.getLong(key, defValue);
    }


    public void removeProperty(String... keys) {
        if (keys == null || keys.length == 0) {
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();

        for (String key : keys) {
            editor.remove(key);
        }
        editor.commit();
    }

    public void clearProperties() {

        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
}
