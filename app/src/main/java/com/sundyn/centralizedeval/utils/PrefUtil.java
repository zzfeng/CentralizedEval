package com.sundyn.centralizedeval.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * Created by Administrator on 2017/2/21.
 */

public class PrefUtil {

    private static PrefUtil instance = null;
    private Context context = UIUtil.getContext();

    public static final String PREFS = "setting";

    public static PrefUtil getInstance() {
        if (instance == null) {
            synchronized (PrefUtil.class) {
                if (instance == null) {
                    instance = new PrefUtil();
                }
            }
        }
        return instance;
    }

    private PrefUtil() {
    }

    /**
     * 本地k-v存储
     */
    public SharedPreferences getPrefs() {
        return context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public int getInt(String key, int def) {
        return TextUtils.isEmpty(key) ? def : getPrefs().getInt(key, def);
    }

    public long getLong(String key, long def) {
        return TextUtils.isEmpty(key) ? def : getPrefs().getLong(key, def);
    }

    public boolean getBoolean(String key, boolean def) {
        return getPrefs().getBoolean(key, def);
    }

    public String getString(String key, String def) {
        return getPrefs().getString(key, def);
    }

    public void putInt(String key, int value) {
        getPrefs().edit().putInt(key, value).commit();
    }

    public void putLong(String key, long value) {
        getPrefs().edit().putLong(key, value).commit();
    }

    public void putString(String key, String value) {
        getPrefs().edit().putString(key, value).commit();
    }

    public void putBoolean(String key, boolean value) {
        getPrefs().edit().putBoolean(key, value).commit();
    }

    public void clearData() {
        getPrefs().edit().clear().commit();
    }


}
