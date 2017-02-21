package com.sundyn.centralizedeval.bean;

/**
 * Created by Administrator on 2017/2/21.
 */

public class M8Config {
    public int version;
    public String shutdown;
    public String boot;
    public int welcomeTime;
    public int apprTime;

    public String ip;
    public String port;
    public String type;

    /**
     * 获得服务器地址
     *
     * @return 完整的服务器地址，例如：http://192.168.100.226:80/
     */
    public String getServerAddr() {
        return "http://" + ip + ":" + port + "/";
    }

    @Override
    public String toString() {
        return version + "/" + shutdown + "/" + boot + "/" + welcomeTime + "/" + apprTime + "/"
                + getServerAddr() + "==" + type;
    }
}
