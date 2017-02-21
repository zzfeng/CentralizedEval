package com.sundyn.centralizedeval.bean;

/**
 * Created by Administrator on 2017/2/21.
 */

public class SettingBean {

    // private static String TAG = "Settings";

    public String serverIp = "192.168.100.7";
    public String serverPort = "8080";
    public String orgUrl = "http://192.168.100.53:8088/";

    public String getServerHost() {
        return "http://" + serverIp + ":" + serverPort + "/";
    }

    public String getServerEstimeHost() {
        return getServerHost() + "estime/";
    }

}
