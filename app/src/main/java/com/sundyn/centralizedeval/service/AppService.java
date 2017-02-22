package com.sundyn.centralizedeval.service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.m8.update.AppUpdate;
import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.bean.SyncTime;
import com.sundyn.centralizedeval.commen.CommenUnit;
import com.sundyn.centralizedeval.commen.CommenUnit.SERVER_STATE;
import com.sundyn.centralizedeval.utils.GsonUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;

//import com.evalterminal.m8.commen.CommenUnit.SERVER_STATE;

/**
 * Created by Administrator on 2017/2/21.
 */

public class AppService extends Service {
    private static final String TAG = "AppService";
    // 用于onBind中返回给调用者，从而调用getService
    private IBinder mBinder = new BinderClass();
    private boolean mIsRunning; // 标识程序是否正在运行
    private boolean mTimeUpdated = false;
    private AppUpdate mUpdateInst = null; // 升级模块实例

    private ProgressDialog mDialog;
    private Handler mHand = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: // 升级进度显示
                    String prog = msg.getData().getString("progress");
                    if (prog != null) {
                        mDialog.setMessage(getString(R.string.update_message) + prog);
                    }
                    break;

                case 1: // 软件升级开始
                    mDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                    mDialog.show();
                    break;

                case 2: // 关闭升级对话框
                    if (mDialog.isShowing())
                        mDialog.dismiss();
                    if (0 == (Integer)msg.obj) { // 本地升级完毕
                        AlertDialog.Builder builder = new AlertDialog.Builder(AppService.this);
                        builder.setMessage(getString(R.string.update_done));
                        builder.setCancelable(false);
                        builder.setPositiveButton(getString(R.string.ok),
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        restartApp(new File(CommenUnit.WORK_DIR + "M8.apk"));
                                    }
                                });
                        builder.create().show();

                    } else if (1 == (Integer)msg.obj) { // 网络升级完毕

                        restartApp(new File(CommenUnit.WORK_DIR + "M8.apk"));
                    }
                    break;

            }
        }
    };

    public class BinderClass extends Binder {
        public AppService getService() {
            return AppService.this;
        }
    }

    /*
     * (non-Javadoc)
     * @see android.app.Service#onBind(android.content.Intent)
     */
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initDialog();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mIsRunning) {
            return START_STICKY_COMPATIBILITY;
        }
        mIsRunning = true;

        // 开启服务循环线程
        new Thread(new Runnable() {

            @Override
            public void run() {
                // 延时等待bind操作结束，获取到消息句柄为止
                while (mHand == null) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Log.e(TAG, "AppService.onStartCommand : " + e.toString());
                    }
                }

                // 检查本地外部存储卡是否有可用升级
                String sdPath = getSDPath().trim();
                if (!"null".equalsIgnoreCase(sdPath)) {
                    if (!"/mnt/shell/emulated".equals(sdPath)) {
                        Log.e(TAG, "存储卡存在" + sdPath);
                        File newZip = new File(CommenUnit.WORK_DIR + "update.zip");
                        File extZip = new File(sdPath + "/update.zip");
                        if (extZip.exists()) {
                            // 开始升级
                            if (mHand != null) {
                                mHand.sendEmptyMessage(1);
                            }
                            // 复制升级包至资源文件目录
                            try {
                                CommenUnit.copyFile(extZip, newZip);
                            } catch (Exception e) {
                                Log.e(TAG, "AppService : " + e.toString());
                            }

                            // 解包升级
                            CommenUnit.updateZip(newZip.getAbsolutePath());
                            // 升级完成
                            Message message = new Message();
                            message.what = 2;
                            message.obj = 0; // 标识升级成功
                            if (mHand != null) {
                                mHand.sendMessage(message);
                            }
                            return;
                        }
                    }

                }

                // 服务循环
                while (true) {
                    // 检测程序是否被覆盖
                    // 获取顶层activity名称
                    if (mIsRunning) {
                        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
                        List<RunningTaskInfo> forgroundActivity = activityManager
                                .getRunningTasks(1);
                        RunningTaskInfo currentActivity;
                        currentActivity = forgroundActivity.get(0);
                        String activityName = currentActivity.topActivity.getClassName();
                        if (!activityName.startsWith("com.evalterminal.m8")
                                && !activityName
                                .equals("com.android.systemui.usb.UsbPermissionActivity")) {
                            CommenUnit.restartApk(getApplicationContext());
                            stopSelf();// 关键，先停止服务

                        }

                        // 检查程序升级
                        updateApp();
                        // 如果网络连接
                        try {
                            if ("1".equals(CommenUnit.m8Config.type)) {
                                // 检测网络心跳，并在此处理一些与服务器交互的周期性操作
                                ServerInteractive();
                            }
                        } catch (Exception e) {
                            stopSelf();
                            startService(new Intent(AppService.this, AppService.class));
                        }

                    }

                    // 开关机检测
                    try {
                        Thread.sleep(10000);
                        // 获取系统当前时间
                        Calendar cal = Calendar.getInstance();
                        int hour = cal.get(Calendar.HOUR_OF_DAY);
                        int minute = cal.get(Calendar.MINUTE);
                        // 读取配置文件，获取开关机时间
                        // 关机检测
                        if (mIsRunning) {
                            if (!TextUtils.isEmpty(CommenUnit.m8Config.shutdown)) {
                                String[] strBuf = CommenUnit.m8Config.shutdown.split(":");
                                int shutHour = Integer.parseInt(strBuf[0].toString());
                                int shutMinute = Integer.parseInt(strBuf[1].toString());
                                if (hour == shutHour) {
                                    if (minute >= shutMinute && minute <= shutMinute + 1) {
                                        // 自动关机
                                        CommenUnit.setAlarmer();
                                        mIsRunning = false;
                                        // 关闭屏幕
                                        CommenUnit.shutDown();
                                    }
                                }
                            }

                        }

                    } catch (InterruptedException e) {
                        Log.e(TAG, "AppService.onStartCommand : " + e.toString());
                    }
                }
            }
        }).start();
        return Service.START_REDELIVER_INTENT;
    }

    /**
     * 检查网络心跳，获取游屏信息，仅在网络连接模式下启用
     */
    protected void ServerInteractive() {

        if (CommenUnit.isNetworkAvail) {
            StringBuffer url = new StringBuffer();
            // url.append(CommenUnit.m8Config.getServerAddr()).append("employeeHeart.action?mac=")
            // .append(CommenUnit.mID).append("&cardNum=");
            url.append(CommenUnit.m8Config.getServerAddr())
                    .append("estime/android_post/empOnline?mac=").append(CommenUnit.mID)
                    .append("&card=");
            if (CommenUnit.mUInfo.success) {
                url.append(CommenUnit.mUInfo.card);
            }

            // 请求心跳包
            StringBuffer answer = new StringBuffer();
            for (int i = 0; i < 2 && !answer.toString().equals("online"); i++) {
                CommenUnit.requestServerByGet(url.toString(), answer, null);
            }
            // 修改网络标识
            if (answer.toString().equals("online")) {
                // Log.e(TAG, "在线") ;
                CommenUnit.mServerState = SERVER_STATE.CONN;
            } else {
                // Log.e(TAG, "不在线") ;
                CommenUnit.mServerState = SERVER_STATE.DISCONN;
                // 查看有线mac
                String wireMac = "";
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                            .hasMoreElements();) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                                .hasMoreElements();) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress()
                                    && inetAddress instanceof Inet4Address) {
                                // MAC
                                byte[] mac = intf.getHardwareAddress();
                                // 把mac地址转成String
                                StringBuffer sb = new StringBuffer();
                                for (int i = 0; i < mac.length; i++) {
                                    // mac[i] & 0xFF 是为了把byte转化为正整数
                                    String s = Integer.toHexString(mac[i] & 0xFF);
                                    sb.append(s.length() == 1 ? 0 + s : s);
                                }
                                wireMac = sb.toString().toUpperCase();
                                break;
                            }
                        }
                    }
                } catch (SocketException ex) {
                    Log.e(TAG, "获取网络连接参数失败，" + ex.toString());
                }
                // 强制设置有线mac与无线mac一致
                if (!CommenUnit.mID.equals(wireMac)) {
                    String mac = String.format("%s:%s:%s:%s:%s:%s",
                            CommenUnit.mID.subSequence(0, 2), CommenUnit.mID.subSequence(2, 4),
                            CommenUnit.mID.subSequence(4, 6), CommenUnit.mID.subSequence(6, 8),
                            CommenUnit.mID.subSequence(8, 10), CommenUnit.mID.subSequence(10, 12));
                    CommenUnit.runRootCommand("ip link set eth0 down");
                    CommenUnit.runRootCommand("ip link set eth0 address " + mac);
                    CommenUnit.runRootCommand("ip link set eth0 up");
                }
                return;
            }

            // 检查数据升级
            int oldVersion = CommenUnit.m8Config.version;
            // try {
            // oldVersion = Integer.parseInt(CommenUnit.m8Config.version);
            // } catch (Exception e) {
            // Log.e(TAG, "AppService:本地版本号不是整数");
            // }
            // 请求新版本号
            url.delete(0, url.length());
            url.append(CommenUnit.m8Config.getServerAddr()).append("getUpdateVersion.action?mac=")
                    .append(CommenUnit.mID).append("&config=true");
            StringBuffer vers = new StringBuffer();
            CommenUnit.requestServerByGet(url.toString(), vers, null);
            try {
                int newVersion = Integer.parseInt(vers.toString().substring(
                        vers.toString().lastIndexOf("=") + 1));
                if (newVersion != oldVersion) {

                    // 如果本地版本号为1，则下载完成升级包一次更新
                    if (oldVersion == 1 || oldVersion > newVersion) {
                        // 显示升级对话框
                        Message message = new Message();
                        message.what = 1;
                        if (mHand != null) {
                            mHand.sendMessage(message);
                        }
                        // 请求完整升级包
                        String localZip = CommenUnit.WORK_DIR + "M7Update.zip";
                        // 组拼请求地址
                        url.delete(0, url.length());
                        url.append(CommenUnit.m8Config.getServerAddr())
                                .append("getUpdateVersion.action?mac=").append(CommenUnit.mID)
                                .append("&config=false");
                        boolean ret = CommenUnit.requestServerByGet(url.toString(), null, localZip);
                        if (ret) {
                            CommenUnit.m8Config.version = newVersion;
                            // CommenUnit.saveConfig() ;
                            CommenUnit.updateZip(localZip);

                            // 关闭升级对话框并重启
                            message = new Message();
                            message.what = 2;
                            message.obj = 1; // 标识升级成功
                            if (mHand != null) {
                                mHand.sendMessage(message);
                            }

                        } else {
                            // 关闭升级对话框
                            message = new Message();
                            message.what = 2;
                            message.obj = -1; // 标识升级失败
                            if (mHand != null) {
                                mHand.sendMessage(message);
                            }
                            Log.i(TAG, "请求升级包失败，跳过升级");
                        }

                    } else { // 如果本地版本号不为1，则循环增量升级
                        int orgVersion = oldVersion;
                        for (; oldVersion < newVersion; oldVersion++) {
                            // 显示升级对话框
                            Message message = new Message();
                            message.what = 1;
                            if (mHand != null) {
                                mHand.sendMessage(message);
                            }

                            // 请求增量升级包
                            String localZip = CommenUnit.WORK_DIR + "M7Update.zip";
                            // 拼合请求地址
                            url.delete(0, url.length());
                            url.append(CommenUnit.m8Config.getServerAddr())
                                    .append("getUpdateVersion.action?mac=").append(CommenUnit.mID)
                                    .append("&version=").append(oldVersion + 1);

                            boolean ret = CommenUnit.requestServerByGet(url.toString(), null,
                                    localZip);

                            if (ret) {
                                CommenUnit.updateZip(localZip);
                                // CommenUnit.saveConfig() ;
                            } else {
                                Log.i(TAG, "请求增量升级包失败，跳过升级");
                                break;
                            }
                        }

                        if (oldVersion != orgVersion) {
                            // 更新本地版本号
                            CommenUnit.m8Config.version = oldVersion;
                            // CommenUnit.saveConfig() ;
                            // CommenUnit.setCfgValueByName(CommenUnit.WORK_DIR
                            // + "config.xml", "Version",
                            // new String[] { String.valueOf(oldVersion) });

                            // 关闭升级对话框并重启
                            Message message = new Message();
                            message.what = 2;
                            message.obj = 1; // 标识升级成功
                            if (mHand != null) {
                                mHand.sendMessage(message);
                            }
                        } else {
                            // 关闭升级对话框
                            Message message = new Message();
                            message.what = 2;
                            message.obj = -1; // 标识升级失败
                            if (mHand != null) {
                                mHand.sendMessage(message);
                            }
                            Log.i(TAG, "请求升级包失败，跳过升级");
                        }

                    }
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "服务器返回的版本号不正确，不能转换为int类型");
            }

            // 同步时间
            if (!mTimeUpdated) {
                StringBuffer sTime = new StringBuffer();
                // 尝试请求服务器时间，同步本地时间
                // if
                // (CommenUnit.requestServerByGet(CommenUnit.m8Config.getServerAddr()
                // + "employeeServerTime.action", sTime, null)) {
                // String[] strArray = sTime.toString().split("\\|");
                // if (strArray.length == 6) {
                // CommenUnit.updateTime(strArray);
                // mTimeUpdated = true;
                // }
                // }
                if (CommenUnit.requestServerByGet(CommenUnit.m8Config.getServerAddr()
                        + "estime/android_post/syncTime", sTime, null)) {
                    SyncTime syncTime = GsonUtil.json2Bean(sTime.toString(), SyncTime.class);
                    if (syncTime != null && syncTime.success) {
                        CommenUnit.updateTime(syncTime.time);
                        mTimeUpdated = true;
                    } else {
                        mTimeUpdated = false;
                    }
                }
            }
        }
    }

    /**
     * 检查官网更新
     */
    void updateApp() {
        if (CommenUnit.isNetworkAvail) {

            // 官网服务器
            String web = "";
            ArrayList<String> webUrl = new ArrayList<String>();
            if (!CommenUnit.mDatabase.select(CommenUnit.DEVICE_TABLE, "1=1", "url", webUrl)) {
                Log.e(TAG, "官网地址为空");
            } else {
                web = webUrl.get(0);
            }
            // 实例化升级模块
            if (mUpdateInst == null) {
                mUpdateInst = AppUpdate.getInstance(CommenUnit.mContext, CommenUnit.WORK_DIR, web,
                        CommenUnit.mID);
            }
            if (mUpdateInst != null && mUpdateInst.checkUpdate()) { // 有升级
                // 显示升级对话框
                Message message = new Message();
                message.what = 1;
                if (mHand != null) {
                    mHand.sendMessage(message);
                }
                // 开始升级
                // 保存config.xml服务器配置和通讯方式
                // String server_ip = CommenUnit.m8Config.server.ip;
                // String server_port = CommenUnit.m8Config.server.port;
                // String conn_type = CommenUnit.m8Config.netWork.type;

                ArrayList<String> apk = new ArrayList<String>();
                if (mUpdateInst.startUpdate(apk, mHand)) {

                    /**
                     * 此处不再保存服务器地址
                     */

                    // 还原config.xml服务器配置和通讯方式
                    // CommenUnit.m8Config = (Config)
                    // CommenUnit.mXStream.fromXML(CommenUnit.configFile);
                    // CommenUnit.m8Config.server.ip = server_ip;
                    // CommenUnit.m8Config.server.port = server_port;
                    // CommenUnit.m8Config.netWork.type = conn_type;
                    // CommenUnit.saveConfig();
                    // 安装或重启应用
                    if (apk.size() > 0) {
                        mUpdateInst.installAppAndRestart(apk.get(0), "start");
                    } else {
                        mUpdateInst.installAppAndRestart(null, "start");
                    }
                } else {
                    Log.e(TAG, "升级失败");
                }
                // 关闭升级对话框
                message = new Message();
                message.what = 2;
                message.obj = -1; // 标识升级失败
                if (mHand != null) {
                    mHand.sendMessage(message);
                }
            }

        }
    }

    public void setHandler(Handler hand) {
        mHand = hand;
    }

    private void initDialog() {
        mDialog = new ProgressDialog(this, AlertDialog.THEME_HOLO_LIGHT);
        mDialog.setTitle(getString(R.string.update_title));
        mDialog.setMessage(getString(R.string.update_message));
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setCancelable(false);
    }

    private void restartApp(File apk) {
        CommenUnit.saveConfigToXml();
        stopSelf();// 关键，首先停止服务
        if (apk.exists()) {
            // 安装新程序
            CommenUnit.setupApk(getApplicationContext(), apk.getAbsolutePath());
        } else {
            // 关闭程序
            CommenUnit.restartApk(getApplicationContext());
        }
    }

    /**
     * 获取外置存储卡路径
     *
     * @return
     */
    @SuppressLint("SdCardPath")
    public String getSDPath() {
        String sdcard_path = "null";
        String sd_default = Environment.getExternalStorageDirectory().getAbsolutePath();
        // Log.d("text", sd_default);
        if (sd_default.endsWith("/")) {
            sd_default = sd_default.substring(0, sd_default.length() - 1);
        }
        // 得到路径
        try {
            Runtime runtime = Runtime.getRuntime();
            Process proc = runtime.exec("mount");
            InputStream is = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            String line;
            BufferedReader br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure"))
                    continue;
                if (line.contains("asec"))
                    continue;
                if (line.contains("fat") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                } else if (line.contains("fuse") && line.contains("/mnt/")) {
                    String columns[] = line.split(" ");
                    if (columns != null && columns.length > 1) {
                        if (sd_default.trim().equals(columns[1].trim())) {
                            continue;
                        }
                        sdcard_path = columns[1];
                    }
                }
            }
        } catch (Exception e) {
        }
        // Log.d("text", sdcard_path);
        return sdcard_path;
    }
}
