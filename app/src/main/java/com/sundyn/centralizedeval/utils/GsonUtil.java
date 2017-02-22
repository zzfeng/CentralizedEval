package com.sundyn.centralizedeval.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017/2/21.
 * gson工具类
 */

public class GsonUtil {
    private static String TAG = "GsonUtils";

    private static Gson gson = new GsonBuilder().create();

    public static String jsonFormatter(String uglyJsonStr) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(uglyJsonStr);
        String prettyJsonString = gson.toJson(je);
        return prettyJsonString;
    }

    public static String bean2Json(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T json2Bean(String gsonString, Class<T> objClass) {
        return gson.fromJson(gsonString, objClass);
    }

    public static <T> List<T> json2List(String gsonString, Class<T> cls) {
        List<T> list = gson.fromJson(gsonString, new TypeToken<List<T>>() {
        }.getType());
        return list;
    }

    public static <T> List<Map<String, T>> json2ListMaps(String gsonString) {
        List<Map<String, T>> list = null;
        list = gson.fromJson(gsonString, new TypeToken<List<Map<String, T>>>() {
        }.getType());
        return list;
    }

    public static <T> Map<String, T> json2Maps(String gsonString) {
        Map<String, T> map = null;
        map = gson.fromJson(gsonString, new TypeToken<Map<String, T>>() {
        }.getType());
        return map;
    }
}
