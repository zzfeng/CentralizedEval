package com.sundyn.centralizedeval.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;

import com.sundyn.centralizedeval.commen.CommenUnit;
import com.sundyn.centralizedeval.service.AppService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/21.
 * 该类是提供程序崩溃时未捕获的异常处理，将异常信息日志保存到.log文件中
 */

public class CrashHandler implements UncaughtExceptionHandler {


    /** Debug Log Tag */
    public static final String TAG = "CrashHandler";
    private static final int FILE_SIZE = 10 * 1024 * 1024;
    // private static final boolean DEBUG = true;
    private static CrashHandler INSTANCE;
    /** 系统默认的UncaughtException处理类 */
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    /** log文件路径 */
    private static String PATH_LOGCAT;

    private Context mCtx;

    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {
    }

    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        if (INSTANCE == null)
            INSTANCE = new CrashHandler();
        return INSTANCE;
    }

    /**
     * 初始化,注册Context对象, 获取系统默认的UncaughtException处理器, 设置该CrashHandler为程序的默认处理器
     *
     * @param ctx
     */
    public void init(Context ctx) {

        mCtx = ctx;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            PATH_LOGCAT = CommenUnit.LOG_DIR;
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            PATH_LOGCAT = ctx.getFilesDir().getAbsolutePath() + File.separator + "m8log";
        }
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
        long fileSize = GetFileSizeUtil.getInstance().getFileSize(file);
        if (file.exists() && fileSize >= FILE_SIZE) {
            File flist[] = file.listFiles();
            Arrays.sort(flist, new FileComparator());
            int factor = (int)(flist.length * 0.7 + 1);
            for (int i = 0; i < factor; i++) { // 删除掉70%的文件
                flist[i].delete();
            }
        }
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            // Sleep一会后结束程序
            // 来让线程停止一会是为了显示Toast信息给用户，然后Kill程序
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "Error : ", e);
            }
            // android.os.Process.killProcess(android.os.Process.myPid());
            // System.exit(10);
            mCtx.stopService(new Intent(mCtx, AppService.class));
            CommenUnit.restartApk(mCtx);
        }
    }

    /**
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成. 开发者可以根据自己的情况来自定义异常处理逻辑
     *
     * @param ex
     * @return true:如果处理了该异常信息;否则返回false
     */
    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return true;
        }
        new Thread() {
            @Override
            public void run() {
                // Toast 显示需要出现在一个线程的消息队列中
                Looper.prepare();
                saveFile(ex);
                Looper.loop();
            }
        }.start();
        return true;
    }

    /**
     * 保存到日志文件
     *
     * @param @param ex
     * @return void
     */
    private void saveFile(Throwable ex) {

        if (ex == null) {
            return;
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        ex.printStackTrace(pw);
        String message = sw.toString();

        // StackTraceElement[] stacks = ex.getStackTrace();
        // String message = ex.getMessage();
        // String msg = ex.getLocalizedMessage();

        String date = DateUtil.dtSimpleFormat(new Date(System.currentTimeMillis()));
        try {
            FileOutputStream fos = new FileOutputStream(new File(PATH_LOGCAT, date + ".log"), true);
            // fos.write(message.getBytes());
            StringBuilder sb = new StringBuilder();
            String time = DateUtil.hmsFormat(new Date(System.currentTimeMillis()));
            sb.append("\r\n\r\n").append(time).append("\r\n");
            sb.append(message).append("\r\n");
            fos.write(sb.toString().getBytes());
            fos.flush();
            fos.close();
            Log.e(TAG, message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class FileComparator implements Comparator<File> {

        @Override
        public int compare(File lhs, File rhs) {
            if (lhs.lastModified() > rhs.lastModified())
                return 1;
            else if (lhs.lastModified() < rhs.lastModified())
                return -1;
            else
                return 0;
        }

    }


}
