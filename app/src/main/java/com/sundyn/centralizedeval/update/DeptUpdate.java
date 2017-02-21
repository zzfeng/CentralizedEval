package com.sundyn.centralizedeval.update;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.sundyn.centralizedeval.bean.Organization;
import com.sundyn.centralizedeval.bean.UserBean;
import com.sundyn.centralizedeval.commen.CommenUnit;
import com.sundyn.centralizedeval.utils.GsonUtil;
import com.sundyn.centralizedeval.utils.LocalData;
import com.sundyn.centralizedeval.utils.MD5Util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Administrator on 2017/2/21.
 */

public class DeptUpdate {

    private static String TAG = "DeptUpdate";

    public static final int STATUS_NO_CHANGE = 0;
    public static final int STATUS_SECCESS = 1;
    public static final int STATUS_FAIL = 2;

    private static final int MSG_UPDATE_START = 200;
    private static final int MSG_UPDATE_FINISH = 201;

    private static DeptUpdate DeptUpdate;
    private boolean isUpdating = false;
    private int retry = 0;
    private String deptJson = "";
    private Organization organization;

    private Handler handler = new Handler() {
        /*
         * (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            switch (what) {
                case MSG_UPDATE_START:
                    updateDept();
                    break;
                case MSG_UPDATE_FINISH:
                    LocalData.readDepartment();
                    CommenUnit.mContext.sendBroadcast(new Intent("refreshDept"));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public static DeptUpdate getInstance() {
        if (DeptUpdate == null)
            DeptUpdate = new DeptUpdate();
        return DeptUpdate;
    }

    private DeptUpdate() {
    }

    public void startDeptUpdate() {
        handler.removeMessages(MSG_UPDATE_START);
        handler.sendEmptyMessageAtTime(MSG_UPDATE_START, 3000);
    }

    private void updateDept() {
        if (isUpdating) {
            Log.i(TAG, "正在更新中...");
            return;
        }
        isUpdating = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                deptJson = getDeptUpdateJson();
                if (LocalData.deptJson.equals(deptJson)) {
                    Log.i(TAG, "机构人员无更新！");
                    isUpdating = false;
                    return;
                }
                if (deptJson == null || deptJson.isEmpty()
                        || !deptJson.contains("\"success\":true")) {
                    isUpdating = false;
                    return;
                }
                try {
                    organization = GsonUtil.json2Bean(deptJson, Organization.class);
                    if (organization != null && organization.getDepartments() != null) {
                        List<UserBean> userBeans = organization.getAllUserBeans();
                        int status = downLoadFiles(userBeans);
                        if (status == STATUS_NO_CHANGE) {
                            // if (!LocalData.deptJson.equals(deptJson)) {
                            LocalData.deptJson = deptJson;
                            saveJson();
                            // Message msg = new Message();
                            // msg.what = MSG_UPDATE_FINISH;
                            // msg.arg1 = STATUS_NO_CHANGE;
                            // handler.sendMessage(msg);
                            handler.sendEmptyMessage(MSG_UPDATE_FINISH);
                            // }
                        }
                        if (status == STATUS_SECCESS) {
                            removeFilesNotInFileBens(userBeans);
                            LocalData.deptJson = deptJson;
                            saveJson();
                            handler.sendEmptyMessage(MSG_UPDATE_FINISH);
                        }
                    }
                    isUpdating = false;
                } catch (Exception e) {
                    isUpdating = false;
                    Log.e(TAG, e.toString());
                }
            }
        }).start();
        handler.sendEmptyMessageDelayed(MSG_UPDATE_START, 120 * 1000);
    }

    private String getDeptUpdateJson() {
        Log.d(TAG, "获取机构人员列表！");
        String json = "";
        String url = LocalData.packUrlGetDeptAndEmp(CommenUnit.mID);
        StringBuffer retBuf = new StringBuffer();
        CommenUnit.requestServerByGet(url, retBuf, null);
        json = retBuf.toString();
        return json;
    }

    /**
     * @param fileBeans
     * @return
     */
    private int downLoadFiles(List<UserBean> fileBeans) {
        for (UserBean fileBean : fileBeans) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
            boolean success = downLoadFile(fileBean);
            while (!success) {
                retry++;
                if (retry > 5) {
                    retry = 0;
                    break;
                } else {
                    Log.i(TAG, "=>" + retry + "重新下载：" + LocalData.settings.getServerEstimeHost()
                            + fileBean.getPicUrl());
                }
                success = downLoadFile(fileBean);
                if (success) {
                    retry = 0;
                }
            }
        }
        int status = STATUS_NO_CHANGE;
        for (UserBean fileBean : fileBeans) {
            if (fileBean.upStatus == STATUS_SECCESS) {
                status = STATUS_SECCESS;
                break;
            }
        }
        for (UserBean fileBean : fileBeans) {
            if (fileBean.upStatus == STATUS_FAIL) {
                status = STATUS_FAIL;
                break;
            }
        }
        // return (status == STATUS_FAIL) ? false : true;
        return status;
    }

    /**
     * 下载文件
     *
     * @param fileBean
     * @return 成功true 失败false
     */
    private boolean downLoadFile(UserBean fileBean) {
        try {
            if (fileBean != null && TextUtils.isEmpty(fileBean.getPicUrl().trim())) {
                fileBean.upStatus = STATUS_NO_CHANGE;
                return true;
            }
            File dir = new File(CommenUnit.DEPT_DIR);
            File file = new File(fileBean.getPicPath());
            if (!dir.exists())
                dir.mkdirs();
            if (!compareMD5(file, fileBean.getPicMd5())) {
                if (file.exists())
                    file.delete();
                file.createNewFile();
            } else {
                fileBean.upStatus = STATUS_NO_CHANGE;
                Log.i(TAG, "=> 文件已存在，不下载：" + file.getAbsolutePath());
                return true;
            }

            URL url = new URL(LocalData.settings.getServerEstimeHost() + fileBean.getPicUrl());
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.connect();
            InputStream is = conn.getInputStream();
            OutputStream os = new FileOutputStream(file);
            int nRead = 0;
            byte buf[] = new byte[1024];
            while ((nRead = is.read(buf, 0, buf.length)) > 0) {
                os.write(buf, 0, nRead);
            }
            is.close();
            os.close();

            if (!compareMD5(file, fileBean.getPicMd5())) {
                Log.i(TAG, "=> 文件下载失败,hashcode错误：" + file.getAbsolutePath());
                fileBean.upStatus = STATUS_FAIL;
                return false;
            }

            Log.i(TAG, "=> 文件下载成功：" + file.getAbsolutePath());
            fileBean.upStatus = STATUS_SECCESS;
            return true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            fileBean.upStatus = STATUS_FAIL;
            return false;
        }
    }

    /**
     * @param file
     * @param hashCode
     * @return
     */
    private boolean compareMD5(String file, String hashCode) {
        String md5;
        try {
            md5 = MD5Util.getFileMD5String(file);
            if (!md5.equals("") && md5.equalsIgnoreCase(hashCode)) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, e.toString());
            return false;
        }
    }

    /**
     * @param file
     * @param hashCode
     * @return
     */
    private boolean compareMD5(File file, String hashCode) {
        String md5;
        try {
            if (file != null && !file.exists()) {
                return false;
            }
            md5 = MD5Util.getFileMD5String(file);
            if (!md5.equals("") && md5.equalsIgnoreCase(hashCode)) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            Log.e(TAG, "MD5比较：" + e.toString());
            return false;
        }
    }

    /**
     * @param fileBeans
     */
    private void removeFilesNotInFileBens(List<UserBean> fileBeans) {
        if (fileBeans == null)
            return;
        Log.i(TAG, "删除不在列表中的文件！");
        List<String> list = new ArrayList<String>();
        for (UserBean fileBean : fileBeans) {
            list.add(fileBean.getPicName());
        }
        File dir = new File(CommenUnit.DEPT_DIR);
        if (dir.exists()) {
            File[] listFile = dir.listFiles();
            for (int i = 0; i < listFile.length; i++) {
                File file = listFile[i];
                String fName = file.getName();
                if (!list.contains(fName)) {
                    file.delete();
                }
            }
        }
    }

    /**
     * 保存播放列表
     */
    private void saveJson() {
        try {
            Log.i(TAG, "保存结构人员！");
            saveStringToFile(deptJson, CommenUnit.DEPT_DIR + "dept");
        } catch (IOException e) {
            Log.e(TAG, "保存结构人员失败：" + e.toString());
        }
    }

    /**
     * 保存字符串到文件
     *
     * @param src 字符串
     * @param fileName 文件名全路径
     * @throws IOException
     */
    private static void saveStringToFile(String src, String fileName) throws IOException {
        File file = new File(fileName);
        File dir = new File(file.getParent());
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (file.exists()) {
            file.delete();
        }
        FileWriter fw = new FileWriter(file);
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(src);
        bw.close();
    }


}
