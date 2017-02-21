package com.sundyn.centralizedeval.utils;

import com.lidroid.xutils.util.LogUtils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by Administrator on 2017/2/21.
 */

public class IOUtil {

    /** 关闭流 */
    public static boolean close(Closeable io) {
        if (io != null) {
            try {
                io.close();
            } catch (IOException e) {
                LogUtils.e(e);
            }
        }
        return true;
    }

}
