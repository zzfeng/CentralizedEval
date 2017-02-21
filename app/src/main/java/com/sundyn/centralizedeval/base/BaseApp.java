package com.sundyn.centralizedeval.base;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.sundyn.centralizedeval.commen.CommenUnit;
import com.sundyn.centralizedeval.utils.CrashHandler;
import com.sundyn.centralizedeval.utils.LogcatHelper;
import com.sundyn.centralizedeval.utils.PanoCache;

/**
 * Created by Administrator on 2017/2/21.
 */

public class BaseApp extends Application {
    private static String TAG = "BaseApp";

    /**
     * 获取到主线程的上下文
     */
    private static BaseApp mContext;
    /**
     * 获取到主线程的handler
     */
    private static Handler mMainThreadHandler;
    /**
     * 获取到主线程
     */
    private static Thread mMainThread;
    /**
     * 获取到主线程的轮询器
     */
    private static Looper mMainThreadLooper;
    /**
     * 获取到主线程id
     */
    private static int mMainTheadId;

    /*
     * (non-Javadoc)
     * @see android.app.Application#onCreate()
     */
    @Override
    public void onCreate() {
        super.onCreate();
        CommenUnit.mContext = this;
        BaseApp.mContext = this;
        BaseApp.mMainThreadHandler = new Handler();
        BaseApp.mMainThread = Thread.currentThread();
        BaseApp.mMainThreadLooper = getMainLooper();
        BaseApp.mMainTheadId = android.os.Process.myTid();

        // 异常处理机制
        CrashHandler.getInstance().init(this);
        LogcatHelper.getInstance(getApplicationContext()).start();
        PanoCache.init();
    }

    public static BaseApp getApplication() {
        return mContext;
    }

    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    public static Thread getMainThread() {
        return mMainThread;
    }

    public static Looper getMainThreadLooper() {
        return mMainThreadLooper;
    }

    public static int getMainThreadId() {
        return mMainTheadId;
    }

}
