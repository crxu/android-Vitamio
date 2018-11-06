package com.gsgd.live.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;

/**
 * SharedPreferences工具
 */
public class SPUtil {

    public static synchronized int getInt(Context context, String preferencesName, String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static synchronized void putInt(Context context, String preferencesName, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }

    public static synchronized long getLong(Context context, String preferencesName, String key, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultValue);
    }

    public static synchronized void putLong(Context context, String preferencesName, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.apply();
    }

    public static synchronized String getString(Context context, String preferencesName, String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static synchronized void putString(Context context, String preferencesName, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static synchronized boolean getBoolean(Context context, String preferencesName, String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public static synchronized void putBoolean(Context context, String preferencesName, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static synchronized void remove(Context context, String preferencesName, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static synchronized void clearAll(Context context, String preferencesName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    public static synchronized Map<String, ?> getAll(Context context, String preferencesName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(preferencesName, Context.MODE_PRIVATE);
        return sharedPreferences.getAll();
    }

}
