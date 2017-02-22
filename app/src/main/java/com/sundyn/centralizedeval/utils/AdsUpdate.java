package com.sundyn.centralizedeval.utils;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.m8.update.MD5Util;
import com.sundyn.centralizedeval.bean.AdsBean;
import com.sundyn.centralizedeval.bean.Advertise;
import com.sundyn.centralizedeval.commen.CommenUnit;
import com.thoughtworks.xstream.XStream;

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
 * 该类是提供广告更新的工具
 * www.sundyn.cn/estime/android_advertise/resources?mac=010203040506
 */

public class AdsUpdate {
    private static String TAG = "AdsUpdate";

    public static final int STATUS_NO_CHANGE = 0;
    public static final int STATUS_SECCESS = 1;
    public static final int STATUS_FAIL = 2;

    private static final int MSG_UPDATE_START = 200;
    private static final int MSG_UPDATE_FINISH = 201;

    private static AdsUpdate adsUpdate;
    private Advertise advertise;
    private boolean isUpdating = false;
    private int retry = 0;
    private String xml = "";

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
                    updateAds();
                    break;
                case MSG_UPDATE_FINISH:
                    CommenUnit.mContext.sendBroadcast(new Intent("refreshads"));
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public static AdsUpdate getInstance() {
        if (adsUpdate == null)
            adsUpdate = new AdsUpdate();
        return adsUpdate;
    }

    private AdsUpdate() {
    }

    public void startAdsUpdate() {
        handler.removeMessages(MSG_UPDATE_START);
        handler.sendEmptyMessageAtTime(MSG_UPDATE_START, 5000);
    }

    private void updateAds() {
        if (isUpdating)
            return;
        isUpdating = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                final String xml = getAdsUpdateXml();
                if (xml == null || xml.isEmpty() || xml.equals(AdsUpdate.this.xml)) {
                    isUpdating = false;
                    Log.i(TAG, "广告无更新！");
                    return;
                }
                XStream xStream = new XStream();
                xStream.alias("advertise", Advertise.class);
                xStream.alias("resources", ArrayList.class);
                xStream.alias("resource", AdsBean.class);
                try {
                    advertise = (Advertise)xStream.fromXML(xml);
                    if (advertise != null && advertise.resources != null
                            && advertise.resources.size() > 0) {
                        ArrayList<AdsBean> adsBeans = advertise.resources;
                        boolean b = downLoadFiles(adsBeans);
                        if (b) {
                            removeFilesNotInFileBens(adsBeans);
                            AdsUpdate.this.xml = xml;
                            saveXml();
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
        handler.sendEmptyMessageDelayed(MSG_UPDATE_START, 60 * 1000);
    }

    private String getAdsUpdateXml() {
        Log.d(TAG, "获取广告列表！");
        String xml = "";
        String url = CommenUnit.m8Config.getServerAddr()
                + "estime/android_advertise/resources?mac=" + CommenUnit.mID;
        StringBuffer retBuf = new StringBuffer();
        CommenUnit.requestServerByGet(url, retBuf, null);
        xml = retBuf.toString();
        return xml;
    }

    /**
     * @param fileBeans
     * @return
     */
    private boolean downLoadFiles(ArrayList<AdsBean> fileBeans) {
        for (AdsBean fileBean : fileBeans) {
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
                    Log.i(TAG, "=>" + retry + "重新下载：" + fileBean.path);
                }
                success = downLoadFile(fileBean);
                if (success) {
                    retry = 0;
                }
            }
        }
        int status = STATUS_SECCESS;
        for (AdsBean fileBean : fileBeans) {
            if (fileBean.upStatus == STATUS_FAIL) {
                status = STATUS_FAIL;
                break;
            }
        }

        return (status == STATUS_FAIL) ? false : true;
    }

    /**
     * 下载文件
     *
     * @param fileBean
     * @return 成功true 失败false
     */
    private boolean downLoadFile(AdsBean fileBean) {
        try {
            File dir = new File(CommenUnit.ADS_DIR);
            File file = new File(CommenUnit.ADS_DIR + fileBean.keyName);
            if (!dir.exists())
                dir.mkdirs();
            if (!compareMD5(file, fileBean.md5)) {
                if (file.exists())
                    file.delete();
                file.createNewFile();
            } else {
                fileBean.upStatus = STATUS_NO_CHANGE;
                Log.i(TAG, "=> 文件已存在，不下载：" + file.getAbsolutePath());
                return true;
            }

            URL url = new URL(fileBean.path);
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

            if (!compareMD5(file, fileBean.md5)) {
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
     * @param fileBeans
     */
    private void removeFilesNotInFileBens(ArrayList<AdsBean> fileBeans) {
        if (fileBeans == null)
            return;
        Log.i(TAG, "删除不在广告列表中的文件！");
        List<String> list = new ArrayList<String>();
        for (AdsBean fileBean : fileBeans) {
            list.add(fileBean.keyName);
        }
        File dir = new File(CommenUnit.ADS_DIR);
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
    private void saveXml() {
        try {
            Log.i(TAG, "保存xml！");
            saveStringToFile(xml, CommenUnit.ADS_DIR + "ads.xml");
        } catch (IOException e) {
            Log.e(TAG, "保存xml失败：" + e.toString());
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
