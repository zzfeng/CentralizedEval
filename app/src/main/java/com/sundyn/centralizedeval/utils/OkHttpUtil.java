package com.sundyn.centralizedeval.utils;

/**
 * Created by Administrator on 2017/2/21.
 */

public class OkHttpUtil {

//
//    private static String TAG = "OkHttpUtil";
//
//    private static final String CHARSET_NAME = "UTF-8";
//
//    private static final OkHttpClient mOkHttpClient = new OkHttpClient();
//
//    static {
//        mOkHttpClient.setConnectTimeout(10, TimeUnit.SECONDS);
//        mOkHttpClient.setReadTimeout(10, TimeUnit.SECONDS);
//        mOkHttpClient.setWriteTimeout(10, TimeUnit.SECONDS);
//    }
//
//    public static void cancelRequest(String tag) {
//        mOkHttpClient.cancel(tag);
//    }
//
//    /**
//     * 同步线程。
//     *
//     * @param request
//     * @return
//     * @throws IOException
//     */
//    public static Response execute(Request request) throws IOException {
//        return mOkHttpClient.newCall(request).execute();
//    }
//
//    /**
//     * 异步线程访问网络
//     *
//     * @param request
//     * @param responseCallback
//     */
//    public static void enqueue(Request request, Callback responseCallback) {
//        mOkHttpClient.newCall(request).enqueue(responseCallback);
//    }
//
//    /**
//     * 异步线程访问网络, 且不在意返回结果（实现空callback）
//     *
//     * @param request
//     */
//    public static void enqueue(Request request) {
//        mOkHttpClient.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onResponse(Response arg0) throws IOException {
//            }
//
//            @Override
//            public void onFailure(Request arg0, IOException arg1) {
//            }
//        });
//    }
//
//    public static String getStringFromServer(String url) throws IOException {
//        Request request = new Request.Builder().url(url).build();
//        Response response = execute(request);
//        if (response.isSuccessful()) {
//            String responseUrl = response.body().string();
//            return responseUrl;
//        } else {
//            throw new IOException("Unexpected code " + response);
//        }
//    }
//
//    /**
//     * 这里使用了HttpClinet的API
//     *
//     * @param params
//     * @return
//     */
//    public static String formatParams(List<BasicNameValuePair> params) {
//        return URLEncodedUtils.format(params, CHARSET_NAME);
//    }
//
//    /**
//     * 为HttpGet 的 url 方便的添加多个name value 参数。
//     *
//     * @param url
//     * @param params
//     * @return
//     */
//    public static String attachHttpGetParams(String url, List<BasicNameValuePair> params) {
//        return url + "?" + formatParams(params);
//    }
//
//    /**
//     * 为HttpGet 的 url 方便的添加1个name value 参数。
//     *
//     * @param url
//     * @param name
//     * @param value
//     * @return
//     */
//    public static String attachHttpGetParam(String url, String name, String value) {
//        return url + "?" + name + "=" + value;
//    }

}
