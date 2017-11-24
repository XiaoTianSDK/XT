package com.xiaotian.frameworkxt.android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;

import java.util.Set;

/**
 * @author XiaoTian
 * @version 1.0.0
 * @name UtilUseShareProperty
 * @description 用户共享数据工具
 * @date 2014-7-1
 * @link gtrstudio@qq.com
 * @copyright Copyright © 2010-2014 小天天 Studio, All Rights Reserved.
 */
@SuppressLint("NewApi")
public class UtilUseShareProperty {
    public static final String INIT = "com.xiaotian.framework.util.INIT";
    public static final String TOKEN = "com.xiaotian.framework.util.TOKEN";
    public static final String KEY_LONG = "com.xiaotian.framework.util.long";
    public static final String KEY_FLOAT = "com.xiaotian.framework.util.float";
    public static final String KEY_STRING = "com.xiaotian.framework.util.string";
    public static final String KEY_BOOLEAN = "com.xiaotian.framework.util.boolean";
    public static final String KEY_INTEGER = "com.xiaotian.framework.util.integer";
    public static final String KEY_STRING_SET = "com.xiaotian.framework.util.string_set";
    //
    protected SharedPreferences preferences;
    protected Context context;
    private UtilEnvironment mUtilEnvironment;

    //
    public UtilUseShareProperty(Context context) {
        this.context = context;
        mUtilEnvironment = new UtilEnvironment(context);
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    // GET
    public String getToken() {
        return preferences.getString(TOKEN, null);
    }

    public boolean getBooleanValue(String... keyWord) {
        return preferences.getBoolean(keyWord.length > 0 ? keyWord[0] : KEY_BOOLEAN, false);
    }

    public boolean getBooleanValue(Boolean defaultValue, String... keyWord) {
        return preferences.getBoolean(keyWord.length > 0 ? keyWord[0] : KEY_BOOLEAN, defaultValue);
    }

    public String getStringValue(String... keyWord) {
        return preferences.getString(keyWord.length > 0 ? keyWord[0] : KEY_STRING, null);
    }

    public int getIntValue(String... keyWord) {
        return preferences.getInt(keyWord.length > 0 ? keyWord[0] : KEY_INTEGER, Integer.MIN_VALUE);
    }

    public float getFloatValue(String... keyWord) {
        return preferences.getFloat(keyWord.length > 0 ? keyWord[0] : KEY_FLOAT, Float.MIN_VALUE);
    }

    public long getLongValue(String... keyWord) {
        return preferences.getLong(keyWord.length > 0 ? keyWord[0] : KEY_LONG, Long.MIN_VALUE);
    }

    public Set<String> getStringSetValue(String... keyWord) {
        return preferences.getStringSet(keyWord.length > 0 ? keyWord[0] : KEY_STRING_SET, null);
    }

    public void registerOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public void unregisterOnSharedPreferenceChangeListener(OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    // SET
    public Boolean setToken(String token) {
        Editor editor = preferences.edit();
        editor.putString(TOKEN, token);
        // editor.apply(); // 缓冲到内存,异步保存磁盘
        // 同步保存数据到磁盘
        return editor.commit();
    }

    public Boolean setBooleanValue(Boolean value, String... keyWord) {
        Editor editor = preferences.edit();
        editor.putBoolean(keyWord.length > 0 ? keyWord[0] : KEY_BOOLEAN, value);
        // editor.apply(); // 缓冲到内存,异步保存磁盘
        // 同步保存数据到磁盘
        return editor.commit();
    }

    public Boolean setStringValue(String value, String... keyWord) {
        Editor editor = preferences.edit();
        editor.putString(keyWord.length > 0 ? keyWord[0] : KEY_STRING, value);
        return editor.commit();
    }

    public Boolean setIntValue(int value, String... keyWord) {
        Editor editor = preferences.edit();
        editor.putInt(keyWord.length > 0 ? keyWord[0] : KEY_INTEGER, value);
        return editor.commit();
    }

    public Boolean setFloatValue(float value, String... keyWord) {
        Editor editor = preferences.edit();
        editor.putFloat(keyWord.length > 0 ? keyWord[0] : KEY_FLOAT, value);
        return editor.commit();
    }

    public Boolean setLongValue(long value, String... keyWord) {
        Editor editor = preferences.edit();
        editor.putLong(keyWord.length > 0 ? keyWord[0] : KEY_LONG, value);
        return editor.commit();
    }

    public Boolean setStringSetValue(Set<String> value, String... keyWord) {
        Editor editor = preferences.edit();
        editor.putStringSet(keyWord.length > 0 ? keyWord[0] : KEY_STRING_SET, value);
        return editor.commit();
    }

    // 已经初始化新版本
    public void setInitApplication() {
        int versionCode = mUtilEnvironment.getCurrentVersionCode();
        setBooleanValue(true, String.format(INIT + "_%1$d", versionCode));
    }

    public void setInitApplication(int versionCode) {
        setBooleanValue(true, String.format(INIT + "_%1$d", versionCode));
    }

    // 是否已经进入
    public boolean isInitApplication(int versionCode) {
        return getBooleanValue(String.format(INIT + "_%1$d", versionCode));
    }

    // 是否是卸载重新安装
    public boolean isReInstalledApplication() {
        int versionCode = mUtilEnvironment.getCurrentVersionCode();
        for (int i = versionCode; i > 100; i--) {
            if (getBooleanValue(String.format(INIT + "_%1$d", i))) return true;
        }
        return false;
    }

    // 当前版本是否已经初始化进入
    public boolean isInitApplication() {
        int versionCode = mUtilEnvironment.getCurrentVersionCode();
        return getBooleanValue(String.format(INIT + "_%1$d", versionCode));
    }
}
