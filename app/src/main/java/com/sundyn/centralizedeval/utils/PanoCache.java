package com.sundyn.centralizedeval.utils;

import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.Log;
import android.util.LruCache;

/**
 * Created by Administrator on 2017/2/21.
 */

public class PanoCache {
    private static final String TAG = "PanoCache";

    private static LruCache<String, Bitmap> lruCache;

    public static void init() {

        final int allMem = (int)Runtime.getRuntime().maxMemory();

        lruCache = new LruCache<String, Bitmap>(allMem / 8) { // 设置图片缓存为软件可用最大内存的八分之一
            protected int sizeOf(String key, Bitmap value) {
                if (value != null) {
                    return value.getByteCount();
                }
                return 0;
            }
        };
    }

    /**
     * 将图片加入图片缓存
     *
     * @param name 图片名称
     * @param bitmap 要加入的图片
     * @return 成功返回TRUE 否则返回FALSE
     */
    public static boolean addBitmapToMem(String name, Bitmap bitmap) {
        if (name == null || bitmap == null) {
            Log.e(TAG, "参数不能为空");
            return false;
        }
        synchronized (lruCache) {
            lruCache.put(name, bitmap);
        }
        return true;
    }

    /**
     * 从内存中获取图片
     *
     * @param name 要获取的图片名称
     * @return 找到返回bitmap否则返回null
     */
    public static Bitmap getBitmapFromMem(String name) {
        if (TextUtils.isEmpty(name))
            return null;

        Bitmap bitmap = null;
        synchronized (lruCache) {
            bitmap = lruCache.get(name);
            if (bitmap != null) {
                lruCache.remove(name);
                lruCache.put(name, bitmap);
            }
        }
        return bitmap;
    }

    public static void removeAll() {
        lruCache.evictAll();
    }
}
