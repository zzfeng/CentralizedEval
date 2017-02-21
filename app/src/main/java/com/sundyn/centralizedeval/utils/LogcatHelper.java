package com.sundyn.centralizedeval.utils;

import android.content.Context;
import android.os.Environment;

import com.sundyn.centralizedeval.commen.CommenUnit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

/**
 * Created by Administrator on 2017/2/21.
 */

public class LogcatHelper {

    private static final long FILE_SIZE = 20 * 1024 * 1024;
    private static LogcatHelper INSTANCE = null;
    private static String PATH_LOGCAT;
    private LogDumper mLogDumper = null;
    private int mPId;

    /**
     * 初始化目录
     */
    public void init(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {// 优先保存到SD卡中
            PATH_LOGCAT = CommenUnit.LOG_DIR;
        } else {// 如果SD卡不存在，就保存到本应用的目录下
            PATH_LOGCAT = context.getFilesDir().getAbsolutePath() + File.separator + "m8log";
        }
        File file = new File(PATH_LOGCAT);
        if (!file.exists()) {
            file.mkdirs();
        }
        long fileSize = GetFileSizeUtil.getInstance().getFileSize(file);
        if (file.exists() && fileSize >= FILE_SIZE) {
            File flist[] = file.listFiles();
            for (int i = 0; i < flist.length; i++) {
                flist[i].delete();
            }
        }
    }

    public static LogcatHelper getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LogcatHelper(context);
        }
        return INSTANCE;
    }

    private LogcatHelper(Context context) {
        init(context);
        mPId = android.os.Process.myPid();
    }

    public void start() {
        if (mLogDumper == null)
            mLogDumper = new LogDumper(String.valueOf(mPId), PATH_LOGCAT);
        mLogDumper.start();
    }

    public void stop() {
        if (mLogDumper != null) {
            mLogDumper.stopLogs();
            mLogDumper = null;
        }
    }

    private class LogDumper extends Thread {

        private Process logcatProc;
        private BufferedReader mReader = null;
        private boolean mRunning = true;
        String cmds = null;
        private String mPID;
        private FileOutputStream out = null;

        public LogDumper(String pid, String dir) {
            mPID = pid;
            String date = DateUtil.dtSimpleFormat(new Date(System.currentTimeMillis()));
            try {
                out = new FileOutputStream(new File(dir, date + ".log"), true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            /**
             * 日志等级：*:v , *:d , *:w , *:e , *:f , *:s 显示当前mPID程序的 E和W等级的日志.
             */

            // cmds = "logcat *:e *:w | grep \"(" + mPID + ")\"";
            // cmds = "logcat  | grep \"(" + mPID + ")\"";//打印所有日志信息
            // cmds = "logcat -s way";//打印标签过滤信息
            cmds = "logcat *:e  | grep \"(" + mPID + ")\"";

        }

        public void stopLogs() {
            mRunning = false;
        }

        @Override
        public void run() {
            try {
                logcatProc = Runtime.getRuntime().exec(cmds);
                mReader = new BufferedReader(new InputStreamReader(logcatProc.getInputStream()),
                        1024);
                String line = null;
                while (mRunning && (line = mReader.readLine()) != null) {
                    if (!mRunning) {
                        break;
                    }
                    if (line.length() == 0) {
                        continue;
                    }
                    if (out != null && line.contains(mPID)) {
                        String time = DateUtil.hmsFormat(new Date(System.currentTimeMillis()));
                        out.write(("\r\n\r\n" + time + "\r\n" + line + "\r\n").getBytes());
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (logcatProc != null) {
                    logcatProc.destroy();
                    logcatProc = null;
                }
                if (mReader != null) {
                    try {
                        mReader.close();
                        mReader = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    out = null;
                }

            }

        }

    }


}
