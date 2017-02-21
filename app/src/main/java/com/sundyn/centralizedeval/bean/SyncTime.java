package com.sundyn.centralizedeval.bean;

/**
 * Created by Administrator on 2017/2/21.
 */

public class SyncTime {
    private static String TAG = "SyncTime";

    // {"message":"获取当前服务器时间","time":1448517832679,"success":true}

    public String message = "";
    public long time = 0;
    public boolean success = false;
}
