package com.sundyn.centralizedeval.utils;

import android.util.Log;

import com.sundyn.centralizedeval.bean.Organization;
import com.sundyn.centralizedeval.bean.SettingBean;
import com.sundyn.centralizedeval.bean.UserBean;
import com.sundyn.centralizedeval.commen.CommenUnit;
import com.thoughtworks.xstream.XStream;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class LocalData {
    private static String TAG = "LocalData";

    public static final String ACTION_SKIN_CHANGE = "skin_change";
    public static final String ACTION_DEPT_CHANGE = "dept_change";

    public static int skinColor = 0;

    // 后台地址
    // private static String HOST = "http://192.168.100.7:8080/";
    // 获取评价选项
    private static String GET_GOUTERS = "estime/android_post/getGouters?mac=%s";
    // 上传评价数据
    private static String ADD_METIER = "estime/android_post/addMetier?empNum=%s&macNum=%s&time=%s&gid=%s&msg=%s&second=0&rname=&typeId=1";
    // 获取机构人员信息
    private static String GET_DEPT_EMP = "estime/android_post/getDeptAndEmp?mac=%s";
    // 机构人员
    public static Organization organization = new Organization();
    public static String deptJson = "";
    // 所有机构人员
    public static List<UserBean> users = new ArrayList<UserBean>();
    // 软件配置
    public static SettingBean settings = new SettingBean();

    /**
     * 上传滞留评价数据
     */
    public static void uploadOldEvalData() {
        ArrayList<String> val = new ArrayList<String>();
        CommenUnit.mDatabase.select(CommenUnit.DIRECT_TABLE, "1=1", "data", val);
        // 循环上传,成功则删除本条数据
        int count = val.size();
        for (int i = 0; i < count; i++) {
            if (CommenUnit.requestServerByGet(settings.getServerHost() + val.get(i), null, null)) {
                CommenUnit.mDatabase.delete(CommenUnit.DIRECT_TABLE, "data", val.get(i));
            }
        }
    }

    /**
     * 上传失败保存评价数据
     *
     * @param strAction
     */
    public static void saveEvalData(String strAction) {
        CommenUnit.mDatabase.insert(CommenUnit.DIRECT_TABLE, new String[] {
                "data"
        }, new String[] {
                strAction.toString()
        });
    }

    public static void readDepartment() {
        try {
            String result = FileUtil.getStringFromFile(CommenUnit.DEPT_DIR + "dept");
            if (result != null && !result.isEmpty()) {
                LocalData.deptJson = result;
                Organization organization = GsonUtil.json2Bean(result, Organization.class);
                if (organization != null && organization.isSuccess()) {
                    LocalData.organization = organization;
                    LocalData.users = organization.getAllUserBeans();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * 读取配置
     */
    public static boolean readSettings() {
        boolean ret = false;
        File file = new File(CommenUnit.WORK_DIR + "settings.xml");
        try {
            XStream xStream = new XStream();
            xStream.alias("settings", SettingBean.class);
            xStream.aliasField("serverIp", SettingBean.class, "serverIp");
            xStream.aliasField("serverPort", SettingBean.class, "serverPort");
            xStream.aliasField("orgUrl", SettingBean.class, "orgUrl");
            SettingBean settingBean = (SettingBean)xStream.fromXML(file);
            settings = settingBean;
            ret = true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            ret = false;
        }
        CommenUnit.m8Config.ip = LocalData.settings.serverIp;
        CommenUnit.m8Config.port = LocalData.settings.serverPort;
        CommenUnit.m8Config.type = "1";

        return ret;
    }

    /**
     * 保存配置
     */
    public static void saveSettings() {
        try {
            XStream xStream = new XStream();
            xStream.alias("settings", SettingBean.class);
            xStream.toXML(settings, new FileOutputStream(CommenUnit.WORK_DIR + "settings.xml"));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    // /**
    // * 更新host及相关地址
    // *
    // * @param host
    // */
    // public static void updateHost(String host) {
    // HOST = host;
    // GET_GOUTERS = HOST + "estime/android_post/getGouters?mac=%s";
    // GET_DEPT_EMP = HOST + "estime/android_post/getDeptAndEmp?mac=%s";
    // ADD_METIER =
    // HOST+"estime/android_post/addMetier?empNum=%s&macNum=%s&time=%s&gid=%s&msg=%s&second=0&rname=&typeId=1";
    // }

    public static String getAddMetierAction(String userName, String mac, String gid, String msg) {
        String action = "";
        try {
            String time = DateUtil.dateToString(new Date(), DateUtil.yyyyMMddHHmmss);
            action = String.format(ADD_METIER, userName, mac, time, gid, msg);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return action;
    }

    /**
     * 组合提交评价数据url
     *
     * @param action getAddMetierAction返回
     * @return
     */
    public static String packUrlAddMetier(String action) {
        String url = "";
        try {
            url = settings.getServerHost() + action;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return url;
    }

    /**
     * 组合提交评价数据url
     *
     * @param userName
     * @param mac
     * @param gid 评价按键key值
     * @param msg 附加消息
     * @return
     */
    public static String packUrlAddMetier(String userName, String mac, String gid, String msg) {
        String url = "";
        try {
            url = settings.getServerHost() + getAddMetierAction(userName, mac, gid, msg);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return url;
    }

    /**
     * 组合获取评价按键选项url
     *
     * @param mac
     * @return
     */
    public static String packUrlGetGouters(String mac) {
        String url = "";
        url = settings.getServerHost() + String.format(GET_GOUTERS, mac);
        return url;
    }

    /**
     * 组合获取机构人员信息url
     *
     * @param mac
     * @return
     */
    public static String packUrlGetDeptAndEmp(String mac) {
        String url = "";
        url = settings.getServerHost() + String.format(GET_DEPT_EMP, mac);
        return url;
    }
}
