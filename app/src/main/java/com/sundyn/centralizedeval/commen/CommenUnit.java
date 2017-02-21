package com.sundyn.centralizedeval.commen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.bean.Employee;
import com.sundyn.centralizedeval.bean.EmployeeDAO;
import com.sundyn.centralizedeval.bean.EmployeeInfoSet;
import com.sundyn.centralizedeval.bean.M8Config;
import com.sundyn.centralizedeval.service.AppService;
import com.sundyn.centralizedeval.utils.DatabaseUtil;
import com.sundyn.centralizedeval.utils.ShellUtil;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by Administrator on 2017/2/21.
 */

public class CommenUnit { // 员工信息配置
        public static EmployeeInfoSet empInfoSet = new EmployeeInfoSet();

// 员工信息数据结构
public static class UserInfo {
    public String picUrl = "";
    public String department = "";
    public String star = "";
    public String jobNum = "";
    // public int card;
    public String userName;
    public String card;
    public String window = "";
    public String company = "";
    public String phone = "";
    public String message = "";
    public String jobDesc = "";
    public String name = "";
    public boolean success = false;
    public String picBase64 = "";
    public String picMd5 = "";

    public String picturePath = "";
}

public static class LoginInfo {
    public String id = "";
    public String info = "";
    // public int position;
    public String title = "";

}

// 服务器连接状态
public static enum SERVER_STATE {
    CONN, DISCONN
}

// 客户端连接状态
public static enum CLIENT_STATE {
    CONN, DISCONN
}

    public static final String TAG = "CommenUnit";
    public static final String DIRECT_TABLE = "DirectTable"; // 直接传输评价结果缓存数据表
    public static final String INDIRECT_TABLE = "IndirectTable"; // 间接传输评价结果缓存数据表
    public static final String USER_TABLE = "UserInfo"; // 登陆员工信息数据表
    public static final String DEVICE_TABLE = "DeviceTable"; // 设备ID信息数据表
    public static final String SDCARD = Environment.getExternalStorageDirectory().toString(); // 设备机身存储器路径

    public static final String WORK_DIR = SDCARD + "/sundyn/"; // 工作目录
    public static final String ADS_DIR = WORK_DIR + "ads/"; // 广告资源目录
    public static final String EVAL_DIR = WORK_DIR + "eval/"; // 评价按键资源目录
    public static final String IMAGE_DIR = WORK_DIR + "image/"; // 当前风格图片资源目录
    public static final String RECORDER_DIR = WORK_DIR + "recorder/"; // 录音录像目录
    public static final String DEPT_DIR = WORK_DIR + "dept/"; // 机构目录
    public static final String SOUND_DIR = WORK_DIR + "sound/"; // 音频资源目录
    public static final String ADVISE_DIR = WORK_DIR + "advise/"; // 服务查询资源目录
    // public static final String QUARY_DIR = WORK_DIR + "quary/"; // 服务查询资源目录
    public static final String LOG_DIR = WORK_DIR + "m8log/"; // 服务查询资源目录
    public static final String USERINFO_DIR = WORK_DIR + "user/"; // 用户照片目录资源目录
    // public static final String QUARY_RES ="quary_net.xml"; // 服务查询资源

    public static Context mContext;
    public static Map<String, Activity> activityManager = new HashMap<String, Activity>();
    private static MediaPlayer mPlayer = new MediaPlayer();
    public static TextToSpeech mTTS = null; // 文字转语音
    public static HashMap<String, String> mTTSParam = new HashMap<String, String>(); // 为TTS绑定音量类型
    public static HashMap<Integer, CommenUnit.LoginInfo> loginInfos = new HashMap<Integer, CommenUnit.LoginInfo>();
    public static UserInfo mUInfo = new UserInfo();
    /**
     * 所有员工的信息，1.3.3新增
     */
    // public static SparseArray<UserInfo> allUserInfos = new
    // SparseArray<CommenUnit.UserInfo>();
    public static HashMap<String, UserInfo> allUserInfos = new HashMap<String, CommenUnit.UserInfo>();
    public static int currentIndex = -1; // 当前登陆员工在员工信息集合中的索引
    public static ArrayList<Employee> employees = new ArrayList<Employee>();

    public static String mID;
    public static DatabaseUtil mDatabase;
    private static PowerManager.WakeLock mWake; // 控制屏幕是否长亮
    public static SERVER_STATE mServerState = SERVER_STATE.DISCONN;
    public static CLIENT_STATE mClientState = CLIENT_STATE.DISCONN;

    public static final File configFile = new File(WORK_DIR + "CONFIG.xml");

    public static EmployeeDAO empDAO;

    public static boolean isNetworkAvail = true; // 网络连接是否可用

    public static M8Config m8Config = new M8Config();

    public static void preInitApp(Context ctx) {
        if (mContext == null) {
            mContext = ctx;
        }
        // 激活超级权限
        runRootCommand("ls");

        empDAO = new EmployeeDAO(mContext);

        ConnectivityManager cm = (ConnectivityManager)mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        isNetworkAvail = (info != null && info.isConnectedOrConnecting());
        Log.e(TAG, "当前网络状态为" + isNetworkAvail);

        // 禁用home键
        mContext.sendBroadcast(new Intent("home_off"));

        // 屏幕长亮
        PowerManager pm = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
        mWake = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP, TAG);
        mWake.acquire();
        // 隐藏任务栏
        hideTheBar();

        // if (!configFile.exists()) // 若Config文件不存在，从asset中拷贝一份出来
        // copyConfigFile();
        //
        // readConfigXml();

    }

    public static void destroyApp(Context ctx) {
        if (mContext == null) {
            mContext = ctx;
        }
        // 启用home键
        // SystemProperties.set("persist.sys.key_disable", "false");
        mContext.sendBroadcast(new Intent("home_on"));

        mContext.stopService(new Intent(ctx, AppService.class));
        // 关闭屏幕长亮功能
        if (mWake != null && mWake.isHeld())
            mWake.release();

        // 显示任务栏
        CommenUnit.showTheBar();
        if (CommenUnit.mTTS != null)
            CommenUnit.mTTS.shutdown();

        try {
            // 退出程序
            Set<String> keys = CommenUnit.activityManager.keySet();
            Iterator<String> it = keys.iterator();
            for (int i = 0; i < CommenUnit.activityManager.size(); i++) {
                Activity act = CommenUnit.activityManager.get(it.next());
                if (!act.isFinishing()) {
                    act.finish();
                }
            }
            CommenUnit.activityManager.clear();
            android.os.Process.killProcess(android.os.Process.myPid());
        } catch (Exception e) {
        }
    }

    /**
     * 加载本地图片,可以通过缩放的控制，防止大图片载入引起内存溢出
     *
     * @param path 文件绝对路径
     * @param isScale 是否缩放后再载入内存
     * @param width 缩放后的宽
     * @return 位图句柄
     */
    public static Bitmap getLoacalBitmap(String path, boolean isScale, int width) {
        Bitmap bmp = null;
        InputStream is = null;
        try {
            is = new FileInputStream(path);
        } catch (FileNotFoundException e) {
            return null;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;// 允许可清除
        options.inInputShareable = true;// 以上options的两个属性必须联合使用才会有效果
        if (isScale) {
            // 第一次载入，通过inJustDecodeBounds置位，只获取图片的宽高，并不实际载入
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            // 第二次载入，通过inJustDecodeBounds清位，获取缩放后的图片,固定为屏幕分辨率
            options.inJustDecodeBounds = false;
            // 计算缩放比
            int scale = options.outWidth / width;
            if (scale <= 0) {
                // 如果异常或无需缩放，则不缩放
                scale = 1;
            }
            options.inSampleSize = scale;
        }
        bmp = BitmapFactory.decodeStream(is, null, options);
        Log.d(TAG, "bitmap file: " + path);

        return bmp;
    }

    /**
     * 根据手机的分辨率从 dip(dp设备独立像素) 的单位 转成为 px(像素)
     *
     * @param //context
     * @param dipValue
     * @return
     */
    public static int dipToPx(int dipValue) {
        if (mContext == null) {
            return 0;
        }
        final int scale = mContext.getResources().getDisplayMetrics().densityDpi;
        return dipValue * scale / 160;
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dip(dp设备独立像素)
     *
     * @param //context
     * @param pxValue
     * @return
     */
    public static int pxToDip(int pxValue) {
        if (mContext == null) {
            return 0;
        }
        final int scale = mContext.getResources().getDisplayMetrics().densityDpi;
        return pxValue * 160 / scale;
    }

    /**
     * 获取xml文件中与指定节点匹配的所有值，如果节点有重复，按顺序依次取出
     *
     * @param path 文件绝对路径
     * @param node 节点名称
     * @param value 返回的节点值
     * @return 成功返回true，失败返回false
     */
    public static boolean getCfgValueByName(String path, String node, String[] value) {
        DocumentBuilderFactory docBuilderFactory = null;
        DocumentBuilder docBuilder = null;
        Document doc = null;

        try {
            docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docBuilderFactory.newDocumentBuilder();
            FileInputStream fIStream = new FileInputStream(path);
            doc = docBuilder.parse(fIStream);
            fIStream.close();
            // root element
            Element root = doc.getDocumentElement();
            // Do something here
            // get a NodeList by tagname
            NodeList nodeList = root.getElementsByTagName(node);
            if (nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength() && i < value.length; i++) {
                    value[i] = nodeList.item(i).getTextContent();
                }
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.toString());

            // 为value声明实例，避免空指针
            for (int i = 0; i < value.length; i++) {
                value[i] = "";
            }
            // 恢复该文件的隐藏备份文件
            int index = path.lastIndexOf("/") + 1;
            File hideFile = new File(path.substring(0, index) + "." + path.substring(index));
            File brokenFile = new File(path);
            if (hideFile.exists()) {
                if (brokenFile.exists()) {
                    brokenFile.delete();
                }
                hideFile.renameTo(brokenFile);
            }

            // if (path.substring(path.lastIndexOf('/') +
            // 1).equals("config.xml")) {
            // // 释放asset下默认的CONFIG.XML
            // try {
            // File fConfig = new File(CommenUnit.WORK_DIR + "config.xml");
            // if (fConfig.exists()) {
            // fConfig.delete();
            // }
            // InputStream is = mContext.getAssets().open("CONFIG.XML");
            // OutputStream op = new FileOutputStream(CommenUnit.WORK_DIR
            // + "config.xml");
            // BufferedInputStream bis = new BufferedInputStream(is);
            // BufferedOutputStream bos = new BufferedOutputStream(op);
            // byte[] bt = new byte[8192];
            // int len = bis.read(bt);
            // while (len != -1) {
            // bos.write(bt, 0, len);
            // len = bis.read(bt);
            // }
            // bis.close();
            // bos.close();
            // } catch (IOException e1) {
            // // TODO Auto-generated catch block
            // e1.printStackTrace();
            // }
            // }
        } finally {
            doc = null;
            docBuilder = null;
            docBuilderFactory = null;
        }
        return false;
    }

    /**
     * 将值写入xml文件指定节点，如果节点名有重复，按顺序依次写入
     *
     * @param path xml文件路径
     * @param node 节点名称
     * @param value 需要写入的值
     * @return
     */
    public static boolean setCfgValueByName(String path, String node, String[] value) {
        DocumentBuilderFactory docBuilderFactory = null;
        DocumentBuilder docBuilder = null;
        Document doc = null;

        try {
            docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docBuilderFactory.newDocumentBuilder();
            FileInputStream fIStream = new FileInputStream(path);
            doc = docBuilder.parse(fIStream);
            // root element
            Element root = doc.getDocumentElement();
            // Do something here
            // get a NodeList by tagname
            NodeList nodeList = root.getElementsByTagName(node);
            if (nodeList.getLength() > 0) {
                for (int i = 0; i < nodeList.getLength() && i < value.length; i++) {
                    nodeList.item(i).setTextContent(value[i]);
                }
                // 保存修改内容到临时隐藏文件
                int index = path.lastIndexOf("/") + 1;
                String newPath = path.substring(0, index) + "." + path.substring(index);
                FileOutputStream fileOut = new FileOutputStream(newPath);
                TransformerFactory factory = TransformerFactory.newInstance();
                Transformer tf = factory.newTransformer();
                DOMSource source = new DOMSource(doc);
                StreamResult result = new StreamResult(fileOut);
                tf.transform(source, result);
                fileOut.close();
                // 替换原xml文件
                File srcXml = new File(newPath);
                File dstXml = new File(path);
                srcXml.renameTo(dstXml);
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception : " + e.toString());
        } finally {
            doc = null;
            docBuilder = null;
            docBuilderFactory = null;
        }
        Log.e(TAG, "Config node not be found");
        return false;
    }

    /**
     * 播放语音文件
     *
     * @param path 语音文件路径
     * @param isLoop 是否循环播放
     */
    public static void playSound(final String path, boolean isLoop) {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        try {
            mPlayer.reset();
            mPlayer.setDataSource(path);
            mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mPlayer.setLooping(isLoop);
            mPlayer.prepare();
            mPlayer.setOnErrorListener(new OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    mp.stop();
                    mp.release();
                    mPlayer = new MediaPlayer();
                    return true;
                }
            });
            // mPlayer.setOnCompletionListener(new OnCompletionListener() {
            //
            // public void onCompletion(MediaPlayer mp) {
            // // 实时播放虚拟背景音（无任何声音），以免录音造成播放其他声音延迟
            // playSound(SOUND_DIR + "bgm.wav", true);
            // }
            // });
            mPlayer.start();
        } catch (Exception e) {
            Log.e(TAG, "CommenUnit.playSound : 播放音频失败");
        }
    }

    /**
     * 通过"GET"方式请求服务器
     *
     * @param url 完整URL地址
     * @param retBuf 服务器返回的数据返回的临时缓冲区
     * @param path 服务器返回的数据存入的文件路径
     * @return 成功返回true，失败返回false
     */
    public static boolean requestServerByGet(String url, StringBuffer retBuf, String path) {
        if (!isNetworkAvail) {
            Log.e(TAG, "requestServerByGet当前网络不可用");
            return false;
        }

        try {
            // HttpGet连接对象
            HttpGet httpRequest = new HttpGet(url);
            // 设置超时并取得HttpClient对象
            HttpParams httpParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
            HttpConnectionParams.setSoTimeout(httpParams, 5000);
            HttpClient httpClient = new DefaultHttpClient(httpParams);
            // 请求HttpClient，取得HttpResponse
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            // 请求成功
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                if (retBuf == null && path == null) {
                    return true;
                }
                // 取得返回的数据
                if (path == null) {
                    String strResult = EntityUtils.toString(httpResponse.getEntity());
                    retBuf.append(strResult);
                } else {
                    File file = new File(path);
                    File dir = file.getParentFile();
                    if (!dir.exists())
                        dir.mkdirs();
                    // byte[] byteData = EntityUtils.toByteArray(httpResponse
                    // .getEntity());
                    FileOutputStream fileStream = new FileOutputStream(path);
                    // long length =
                    // httpResponse.getEntity().getContentLength();
                    InputStream is = httpResponse.getEntity().getContent();
                    long readBytes = 0;
                    int recieve = 0;
                    int progress;
                    int oldProg = 0;
                    byte[] byteData = new byte[1024];
                    while ((recieve = is.read(byteData, 0, 1024)) != -1) {
                        readBytes += recieve;
                        // // 设置进度
                        // if (M7Splash.mDialog != null
                        // && M7Splash.mDialog.isShowing()) {
                        // progress = (int) (readBytes * 100 / length);
                        // // 循环递增，使进度条更平滑
                        // for (; oldProg <= progress; oldProg++) {
                        // M7Splash.mDialog.setProgress(oldProg);
                        // }
                        // }
                        // 写入文件
                        fileStream.write(byteData, 0, recieve);
                    }
                    fileStream.close();
                }
            } else {
                Log.e(TAG, "M7Commen.requestServerByGet:请求服务器出错");
                return false;
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.e(TAG, "M7Commen.requestServerByGet:" + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 解压缩zip文件
     *
     * @param zipFile 输入zip压缩包文件全局路径
     * @param targetDir 解压目录全局路径
     */
    public static void Unzip(String zipFile, String targetDir) {
        int BUFFER = 4096; // 这里缓冲区我们使用4KB，
        String strEntry; // 保存每个zip的条目名称

        try {
            BufferedOutputStream dest = null; // 缓冲输出流
            FileInputStream fis = new FileInputStream(zipFile);
            ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
            ZipEntry entry; // 每个zip条目的实例

            while ((entry = zis.getNextEntry()) != null) {

                try {
                    Log.i("Unzip: ", "=" + entry);
                    int count;
                    byte data[] = new byte[BUFFER];
                    strEntry = entry.getName();

                    File entryFile = new File(targetDir + strEntry);
                    File entryDir = new File(entryFile.getParent());
                    if (!entryDir.exists()) {
                        entryDir.mkdirs();
                    } else {
                        if (!entryDir.isDirectory()) {
                            entryDir.delete();
                            entryDir.mkdir();
                        }
                    }

                    FileOutputStream fos = new FileOutputStream(entryFile);
                    dest = new BufferedOutputStream(fos, BUFFER);
                    while ((count = zis.read(data, 0, BUFFER)) != -1) {
                        dest.write(data, 0, count);
                    }
                    dest.flush();
                    dest.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            zis.close();
        } catch (Exception cwj) {
            cwj.printStackTrace();
        }
    }

    /**
     * 复制文件
     *
     * @param src 源文件
     * @param tar 目标文件
     * @throws Exception 异常
     */
    public static void copyFile(File src, File tar) throws Exception {
        if (!src.exists()) {
            throw new FileNotFoundException();
        }
        File tarPare = tar.getParentFile();
        if (tarPare.exists()) {
            if (tar.exists()) {
                tar.delete();
            }
        } else {
            tarPare.mkdirs();
        }
        if (src.isFile()) {
            InputStream is = new FileInputStream(src);
            OutputStream op = new FileOutputStream(tar);
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(op);
            byte[] bt = new byte[8192];
            int len = bis.read(bt);
            while (len != -1) {
                bos.write(bt, 0, len);
                len = bis.read(bt);
            }
            bis.close();
            bos.close();
        }
        if (src.isDirectory()) {
            File[] f = src.listFiles();
            tar.mkdir();
            for (int i = 0; i < f.length; i++) {
                copyFile(f[i].getAbsoluteFile(), new File(tar.getAbsoluteFile() + File.separator
                        + f[i].getName()));
            }
        }
    }

    /**
     * 将方矩形图片改为圆角矩形图片
     *
     * @param bitmap 需要修改的图片
     * @param //pixels 圆角半径像素值
     * @return 修改后的圆角矩形图片
     */
    public static Bitmap getRoundCornerBmp(Bitmap bitmap) {
        if (bitmap == null || bitmap.isRecycled()) {
            return null;
        }
        Bitmap output = Bitmap
                .createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_4444);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = bitmap.getWidth() * 0.07f;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    /**
     * 通过Canvas绘字，支持自动换行
     *
     * @param text
     * @param r
     * @param canv
     * @param paint
     */
    public static void drawText(String text, Rect r, Canvas canv, Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        int realLine = 0;
        int fontHeight = (int)Math.ceil(fm.descent - fm.top) + 2;
        char ch;
        int w = 0;
        int istart = 0;

        Vector<String> vecStr = new Vector<String>();
        // 处理换行
        for (int i = 0; i < text.length(); i++) {
            ch = text.charAt(i);
            float[] widths = new float[1];
            String srt = String.valueOf(ch);
            paint.getTextWidths(srt, widths);

            if (ch == '\n') {
                realLine++;
                vecStr.addElement(text.substring(istart, i));
                istart = i + 1;
                w = 0;
            } else {
                w += (int)(Math.ceil(widths[0]));
                if (w > r.width()) {
                    realLine++;
                    vecStr.addElement(text.substring(istart, i));
                    istart = i;
                    i--;
                    w = 0;
                } else {
                    if (i == (text.length() - 1)) {
                        realLine++;
                        vecStr.addElement(text.substring(istart, text.length()));
                    }
                }
            }
        }

        // 绘字
        for (int i = 0; i < realLine; i++) {
            canv.drawText(vecStr.elementAt(i), r.left, r.top + fontHeight * (i + 1), paint);
        }
    }

    /**
     * @param command 命令
     * @return 成功返回true，失败返回false
     */
    public static boolean runRootCommand(String command) {
        Process process = null;
        DataOutputStream os = null;
        try {
            process = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(process.getOutputStream());
            // 4.0系统切换为root用户执行pm命令出现段错误，重新export相关变量
            os.writeBytes("export LD_LIBRARY_PATH=/vendor/lib:/system/lib\n");

            os.writeBytes(command + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();

        } catch (Exception e) {
            Log.e(TAG, "the device is not rooted, error message: " + e.getMessage());
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {

            }
        }
        return true;
    }

    public static void showTheBar() {

        if (mContext == null) {
            return;
        }
        mContext.sendBroadcast(new Intent("show_bar"));
    }

    public static void hideTheBar() {
        if (mContext == null) {
            return;
        }
        // SystemProperties.set("mbx.hideStatusBar.enable", "true");
        mContext.sendBroadcast(new Intent("hide_bar"));
    }

    /**
     * 获取当前设备屏幕像素，4.0以后的系统必须通过反射机制获取
     *
     * @param resolution 存放获取的像素宽、高
     */
    @SuppressLint("NewApi")
    public static void getDisplayScreenResolution(int[] resolution) {
        if (mContext == null) {
            return;
        }
        int ver = android.os.Build.VERSION.SDK_INT;
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
        android.view.Display display = wm.getDefaultDisplay();
        display.getMetrics(dm);

        resolution[0] = dm.widthPixels;

        if (ver < 13) {
            resolution[1] = dm.heightPixels;
        } else if (ver == 13) {
            try {
                Method mt = display.getClass().getMethod("getRealHeight");
                resolution[1] = (Integer)mt.invoke(display);
            } catch (Exception e) {
            }
        } else if (ver > 13 && ver < 17) {
            try {
                Method mt = display.getClass().getMethod("getRawHeight");
                resolution[1] = (Integer)mt.invoke(display);

            } catch (Exception e) {
            }
        } else {
            DisplayMetrics dmReal = new DisplayMetrics();
            display.getRealMetrics(dmReal);
            resolution[1] = dmReal.heightPixels;
        }
        if (resolution[1] == 0) {
            resolution[1] = dm.heightPixels;
            // 获取statusbar高度
            Class<?> c = null;
            Object obj = null;
            Field field = null;
            int x = 0, sbar = 0;
            try {
                c = Class.forName("com.android.internal.R$dimen");
                obj = c.newInstance();
                field = c.getField("status_bar_height");
                x = Integer.parseInt(field.get(obj).toString());
                sbar = mContext.getResources().getDimensionPixelSize(x);
            } catch (Exception e1) {
                Log.w("get bar height fail", TAG);
            }

            resolution[1] += sbar;
        }
    }

    /**
     * 同步系统时间，需要root权限才能起作用
     *
     * @param date 时间数组，年月日时分秒
     */
    public static void updateTime(String[] date) {
        if (mContext == null) {
            return;
        }
        // 发送广播通知DeviceControll修改系统时间
        Calendar cal = Calendar.getInstance();
        try {
            // 月份必须-1
            cal.set(Integer.parseInt(date[0]), Integer.parseInt(date[1]) - 1,
                    Integer.parseInt(date[2]), Integer.parseInt(date[3]),
                    Integer.parseInt(date[4]), Integer.parseInt(date[5]));
        } catch (Exception e) {
            Log.e(TAG, "AppService.updateTime:请求服务器时间不能转化为整形");
        }
        long millis = cal.getTimeInMillis();
        Intent intent = new Intent("system_time");
        intent.putExtra("time", millis);
        mContext.sendBroadcast(intent);

    }

    /**
     * 同步系统时间，需要root权限才能起作用
     *
     * @param date 时间数组，年月日时分秒
     */
    public static void updateTime(long date) {
        if (mContext == null) {
            return;
        }
        // 发送广播通知DeviceControll修改系统时间
        long millis = date;
        Intent intent = new Intent("system_time");
        intent.putExtra("time", millis);
        mContext.sendBroadcast(intent);
    }

    /**
     * 升级主程序
     *
     * @param path 升级包绝对路径
     */
    public static void updateZip(String path) {
        // 检查升级包是否存在
        File upd = new File(path);
        if (!upd.exists()) {
            Log.e(TAG, "CommenUnit.updateZip : " + path + "不存在，无法完成升级");
            return;
        }

        // 解压升级包
        Unzip(path, CommenUnit.WORK_DIR);

        // 升级完成，删除升级包
        upd.delete();

        String server_ip = m8Config.ip;

        String server_port = m8Config.port;
        // 如果解压后存在newConfig.xml，则覆盖config.xml
        File fConfig = new File(WORK_DIR + "NewConfig.xml");
        if (fConfig.exists()) {
            File fDest = new File(WORK_DIR + "config.xml");
            if (fDest.exists()) {
                fDest.delete();
                boolean isRename = fConfig.renameTo(fDest);
                if (isRename) {
                    readConfigXml();
                    // 保存config.xml服务器配置和通讯方式
                    m8Config.ip = server_ip;
                    m8Config.port = server_port;
                    saveConfigToXml();
                }
            }
        }
    }

    /**
     * 静默安装程序
     *
     * @param apk 等待安装的apk绝对路径
     */
    public static void setupApk(Context ctx, String apk) {
        if (mContext == null)
            mContext = ctx;

        AlarmManager mAlarm = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("start");
        PendingIntent pend = PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        mAlarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pend);
        // 在已破解的机器上，使用pm命令实现静默安装
        ShellUtil.execCommand("pm install -r " + apk, true);

        // runRootCommand("pm install -r " + apk);
    }

    /**
     * 重启应用程序
     */
    public static void restartApk(Context ctx) {
        if (mContext == null) {
            mContext = ctx;
        }
        AlarmManager alarm = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("start");
        PendingIntent pend = PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000, pend);
        // 退出程序
        destroyApp(ctx);
    }

    /**
     * 自动关机
     */
    public static void shutDown() {
        if (mContext == null) {
            return;
        }
        // 关闭屏幕长亮，禁止深度休眠
        mWake.release();
        PowerManager pm = (PowerManager)mContext.getSystemService(Context.POWER_SERVICE);
        mWake = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWake.acquire();

        // 显示任务栏
        CommenUnit.showTheBar();
        // 启用home键
        mContext.sendBroadcast(new Intent("home_on"));
        // 模拟home键
        Intent i = new Intent(Intent.ACTION_MAIN);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addCategory(Intent.CATEGORY_HOME);
        mContext.startActivity(i);
        // 关闭屏幕
        mContext.sendBroadcast(new Intent("close_screen"));
        // PowerManager pm = (PowerManager) mContext
        // .getSystemService(Context.POWER_SERVICE);
        // pm.goToSleep(SystemClock.uptimeMillis());
    }

    /**
     * 设置自动开机（唤醒屏幕并启动程序）定时器
     */
    public static void setAlarmer() {
        AlarmManager alarm = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent("reboot");
        PendingIntent pend = PendingIntent.getBroadcast(mContext, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        // 提取开机时间
        String[] strBuf = new String[2];

        strBuf = CommenUnit.m8Config.boot.split(":");
        int bootHour = Integer.parseInt(strBuf[0].toString());
        int bootMinute = Integer.parseInt(strBuf[1].toString());

        // 计算时间
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        int minute = cal.get(Calendar.MINUTE);
        int diffHour = bootHour - hour;
        int diffminute = bootMinute - minute;
        if (diffHour < 0 || (diffHour == 0 && diffminute < 0)) {
            diffHour += 24;
        }
        cal.add(Calendar.HOUR_OF_DAY, diffHour);
        cal.add(Calendar.MINUTE, diffminute);
        long mills = cal.getTimeInMillis();

        alarm.set(AlarmManager.RTC_WAKEUP, mills, pend);

    }

    /**
     * 自动开机
     */
    public static void powerup() {
        // 重启机器
        runRootCommand("reboot");
    }

    /**
     * 登陆用户名密码验证
     *
     * @param userName
     * @param pasw
     * @return
     */
    public static String login(String userName, String pasw) {
        StringBuilder url = new StringBuilder();

        // url.append(CommenUnit.m8Config.getServerAddr()).append("employeeLogin2.action?name=").append(userName)
        // .append("&psw=").append(pasw);
        url.append(CommenUnit.m8Config.getServerAddr())
                .append("estime/android_post/login?username=").append(userName)
                .append("&password=").append(pasw);
        StringBuffer retBuf = new StringBuffer();
        // Log.i("login", "服务URL:" + url.toString());
        if (!CommenUnit.requestServerByGet(url.toString(), retBuf, null)) {
            // 联网登陆失败，本地进行登陆
            Employee emp = new Employee();
            emp.ext2 = userName;
            emp.PassWord = pasw;

            int result = CommenUnit.empDAO.login(emp);
            if (result == -1) {
                retBuf.append("loginError");
                Log.e(TAG, "本地离线登陆失败");
            } else {
                Log.e(TAG, "本地离线登陆成功。卡号为" + result);
                return "loginSuccess|" + result;
            }

        }

        return retBuf.toString();

    }

    /**
     * 为当前Activity添加返回按钮
     *
     * @param mBtnRet 要定义的返回按键
     * @param layout 当前界面主布局
     * @param a 当前Activity
     */
    public static Button addReturnBtn(Button mBtnRet, RelativeLayout layout, final Activity a) {
        // 初始化返回键
        mBtnRet = new Button(a);
        mBtnRet.setBackgroundResource(R.drawable.return_up);
        mBtnRet.setText(a.getString(R.string.return_button));
        mBtnRet.setTextSize(60);
        mBtnRet.setTextColor(Color.WHITE);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                android.view.ViewGroup.LayoutParams.MATCH_PARENT,
                android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        mBtnRet.setLayoutParams(lp);

        layout.addView(mBtnRet);
        mBtnRet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                clickExitAnim(v, a);
                // FillWelcome.this.finish();
            }
        });

        return mBtnRet;
    }

    /**
     * 点击缩放效果，点击后退出当前Activity
     */
    public static void clickExitAnim(View v, final Activity a) {
        final AnimationSet set = new AnimationSet(true);
        ScaleAnimation zoomIn = new ScaleAnimation(1f, 0.8f, 1f, 0.8f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoomIn.setDuration(200);
        zoomIn.setFillAfter(true);
        set.addAnimation(zoomIn);
        ScaleAnimation zoomOut = new ScaleAnimation(0.6f, 1.2f, 0.6f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoomOut.setDuration(450);
        zoomOut.setFillAfter(true);
        zoomOut.setStartOffset(200);
        set.addAnimation(zoomOut);
        set.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                a.finish();

            }
        });
        v.startAnimation(set);

    }

    /**
     * 释放asset下默认的CONFIG.XML
     */
    private static void copyConfigFile() {

        try {

            if (configFile.exists()) {
                configFile.delete();
            }
            InputStream is = mContext.getAssets().open("CONFIG.XML");
            OutputStream op = new FileOutputStream(CommenUnit.WORK_DIR + "config.xml");
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(op);
            byte[] bt = new byte[8192];
            int len = bis.read(bt);
            while (len != -1) {
                bos.write(bt, 0, len);
                len = bis.read(bt);
            }
            bis.close();
            bos.close();
        } catch (Exception e1) {
            CommenUnit.destroyApp(mContext);
        }

    }

    private static void readConfigXml() {
        InputStream is = null;
        try {
            is = new FileInputStream(new File(CommenUnit.WORK_DIR + "config.xml"));
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(is, "UTF-8");

            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if ("Version".equals(parser.getName())) {
                            try {
                                m8Config.version = Integer.parseInt(parser.nextText());
                            } catch (Exception e) {
                                m8Config.version = 0;
                            }
                        } else if ("Welcometime".equals(parser.getName())) {
                            try {
                                m8Config.welcomeTime = Integer.parseInt(parser.nextText());
                            } catch (Exception e) {
                                m8Config.welcomeTime = 5; // 出错默认欢迎光临时长为5s
                            }
                        } else if ("Approvertime".equals(parser.getName())) {
                            try {
                                m8Config.apprTime = Integer.parseInt(parser.nextText());
                            } catch (Exception e) {
                                m8Config.apprTime = 30; //
                            }
                        } else if ("Shutdown".equals(parser.getName())) {
                            m8Config.shutdown = parser.nextText();
                        } else if ("Boot".equals(parser.getName())) {
                            m8Config.boot = parser.nextText();
                        } else if ("IP".equals(parser.getName())) {
                            m8Config.ip = parser.nextText();
                        } else if ("Port".equals(parser.getName())) {
                            m8Config.port = parser.nextText();
                        } else if ("Type".equals(parser.getName())) {
                            m8Config.type = parser.nextText();
                        }
                        break;
                }

                event = parser.next();
            } // end while

        } catch (Exception e) {
            copyConfigFile();
            restartApk(mContext);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }

        }
    }

    /**
     * 保存配置到CONFIG.xml
     */
    public static void saveConfigToXml() {
        String charset = "UTF-8";
        OutputStream os = null;
        try {
            os = new FileOutputStream(CommenUnit.WORK_DIR + "config.xml");
            XmlSerializer serial = Xml.newSerializer();
            serial.setOutput(os, charset);
            serial.startDocument(charset, false);

            serial.startTag(null, "M7Config");
            // <Software>
            serial.startTag(null, "Software");

            serial.startTag(null, "Version");
            serial.text(String.valueOf(m8Config.version));
            serial.endTag(null, "Version");

            serial.startTag(null, "Welcometime");
            serial.text(String.valueOf(m8Config.welcomeTime));
            serial.endTag(null, "Welcometime");

            serial.startTag(null, "Approvertime");
            serial.text(String.valueOf(m8Config.apprTime));
            serial.endTag(null, "Approvertime");

            serial.startTag(null, "Shutdown");
            serial.text(m8Config.shutdown);
            serial.endTag(null, "Shutdown");

            serial.startTag(null, "Boot");
            serial.text(m8Config.boot);
            serial.endTag(null, "Boot");

            serial.startTag(null, "ShowEmployeePage");
            serial.text("1");
            serial.endTag(null, "ShowEmployeePage");

            serial.endTag(null, "Software");
            // <Software>

            // <Server>
            serial.startTag(null, "Server");

            serial.startTag(null, "IP");
            serial.text(m8Config.ip);
            serial.endTag(null, "IP");

            serial.startTag(null, "Port");
            serial.text(m8Config.port);
            serial.endTag(null, "Port");

            serial.endTag(null, "Server");
            // <Server>

            // <Network>
            serial.startTag(null, "Network");

            serial.startTag(null, "Type");
            serial.text(m8Config.type);
            serial.endTag(null, "Type");

            serial.endTag(null, "Network");
            // <Network>

            serial.endTag(null, "M7Config");

            serial.flush();
        } catch (Exception e) {

        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (Exception e) {
                }
            }
        }

    }
}