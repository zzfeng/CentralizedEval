package com.sundyn.centralizedeval.utils;

import android.util.Log;

/**
 * Created by Administrator on 2017/2/21.
 * debug模式下输出log信息，发布时设置 debug=false;
 */

public class DebugUtil {

    private static String TAG = "DebugUtil";
    private static boolean debug = true;

    public static void v(String tag, String msg) {
        if (debug) {
            Log.v(tag, msg);
        }
    }

    public static void d(String tag, String msg) {
        if (debug) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (debug) {
            Log.i(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (debug) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (debug) {
            Log.e(tag, msg);
        }
    }

}
