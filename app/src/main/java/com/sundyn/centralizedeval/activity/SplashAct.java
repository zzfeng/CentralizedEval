package com.sundyn.centralizedeval.activity;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.widget.ImageView;

import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.base.BaseAct;
import com.sundyn.centralizedeval.commen.CommenUnit;
import com.sundyn.centralizedeval.utils.DatabaseUtil;
import com.sundyn.centralizedeval.utils.FileUtil;
import com.sundyn.centralizedeval.utils.LocalData;
import com.sundyn.centralizedeval.utils.PrefUtil;
import com.sundyn.centralizedeval.utils.SystemUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * Created by Administrator on 2017/2/21.
 */

public class SplashAct extends BaseAct {

    private static String TAG = "SplashAct";

    protected Handler mHand = new Handler() {
        /*
         * (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {
//            int what = msg.what;
//            switch (what) {
//                case 0:
//                    // 证书验证失败
//                    File fCert = new File(CommenUnit.WORK_DIR + CommenUnit.mID + ".cer");
//                    if (fCert.exists()) {
//                        fCert.delete();
//                    }
//                    File fCert2 = new File(CommenUnit.WORK_DIR + "." + CommenUnit.mID + ".cer");
//                    if (fCert2.exists()) {
//                        fCert2.delete();
//                    }
//                    Toast.makeText(getApplicationContext(), "证书验证失败！", Toast.LENGTH_LONG).show();
//                    String serverUrl = PrefUtils.getInstance().getString("url",
//                            "http://192.168.100.53:8088/");
//                    final EditText etUrl = new EditText(SplashAct.this);
//                    etUrl.setInputType(InputType.TYPE_CLASS_TEXT);
//                    etUrl.setSingleLine();
//                    etUrl.setText(serverUrl);
//                    AlertDialog alertDialog = new AlertDialog.Builder(SplashAct.this)
//                            .setTitle("输入官网地址ַ").setView(etUrl)
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    diaIsShow = false;
//                                    String web = etUrl.getText().toString().trim();
//                                    if (!web.startsWith("http")) {
//                                        web = "http://" + web;
//                                    }
//                                    if (!web.endsWith("/")) {
//                                        web += "/";
//                                    }
//                                    web = web.replace(" ", "");
//                                    // 保存地址
//                                    // PrefUtils.getInstance().putString("url",
//                                    // web);
//                                    LocalData.settings.orgUrl = web;
//                                    LocalData.saveSettings();
//                                    CommenUnit.restartApk(getApplicationContext());
//                                }
//                            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    diaIsShow = false;
//                                    CommenUnit.destroyApp(getApplicationContext());
//                                }
//                            }).create();
//                    alertDialog.getWindow().setSoftInputMode(
//                            WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
//                    alertDialog.setCancelable(false);
//                    alertDialog.show();
//                    diaIsShow = true;
//
//                    break;
//                case 1://
//                       // startActivity(new Intent(SplashAct.this,
//                       // MainAct.class));
//                    break;
//
//                default:
//                    break;
//            }
//
//            super.handleMessage(msg);
        }

    };

    protected boolean mIsLegal = true;
    protected boolean mIsChecked = false;
    private boolean diaIsShow = false;
    private ImageView bg;

    /*
     * (non-Javadoc)
     * @see com.sundyn.centralizedeval.BaseAct#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_splash);
        bg = (ImageView)findViewById(R.id.bg);
        CommenUnit.mID = SystemUtil.getMacAddress();
        CommenUnit.preInitApp(getApplicationContext());
        // 初始化数据库
        initDatabase();
        // 初始化配置
        initConfig();
        // 读取机构数据
        LocalData.readDepartment();
        // 创建快捷方式
        createShortCut();
        // 证书验证
        certificateCheck();

        // 首页动画效果
        AnimationSet set = new AnimationSet(true);
        Animation animIn = new AlphaAnimation(0, 1);
        animIn.setDuration(1000);
        Animation animOut = new AlphaAnimation(1, 0);
        animOut.setDuration(1000);
        animOut.setStartOffset(3000);
        set.addAnimation(animIn);
        set.addAnimation(animOut);
        set.setFillAfter(true);
        bg.setAnimation(set);
        set.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
//            	大手机+微信 暂时不使用证书功能
//                while (!mIsChecked) {// 必须等待m8读取配置完成后才能进行下一步
//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//
//                    }
//                }
                // // 如果对话框处于显示状态,禁止进入主界面
                // if (diaIsShow) {
                // return;
                // }
                // 大手机+微信 暂时不使用证书功能
//                if (mIsLegal) {
                if(true){
                    // 动画结束后进入主界面
//                    Intent intent = new Intent(SplashAct.this, MainAct.class);
//                	SharedPreferences sp = getSharedPreferences("logstate",MODE_PRIVATE);
//                	boolean flag = sp.getBoolean("login",false);
//                	if(flag){
//                		intent = new Intent(SplashAct.this, MainAct.class);
//                	}else{
                    intent = new Intent(SplashAct.this,LoginAct.class);
//                	}
                    startActivity(intent);
                    SplashAct.this.finish();
                }
            }
        });
        set.start();
    }
    private Intent intent;
    private void initDatabase() {
        String path = CommenUnit.WORK_DIR + "data.db";
        CommenUnit.mDatabase = new DatabaseUtil(CommenUnit.mContext);
        if (new File(path).exists()) {
            CommenUnit.mDatabase.openOrCreate(path);
            // Log.i(TAG, "数据库版本：" + CommenUnit.mDatabase.getDbVersion());
            // CommenUnit.mDatabase.onUpgrade(0, -1, null, null, null, null);
            return;
        }

        try {
            FileUtil.copyAssetsFile(this, "data.db", path);
            CommenUnit.mDatabase.openOrCreate(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initConfig() {
        LocalData.skinColor = Color.parseColor(PrefUtil.getInstance().getString("skinColor",
                "#FF0893A8"));
        if (!LocalData.readSettings()) {
            try {
                FileUtil.copyAssetsFile(this, "settings.xml", CommenUnit.WORK_DIR + "settings.xml");
                LocalData.readSettings();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        // LocalData.writeSettings();
    }

    /**
     * 获得Certificate
     *
     * @return Certificate 证书
     * @throws Exception
     */
    private Certificate getCertificate(InputStream in) throws Exception {
        // 实例化证书工厂
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        // 生成证书
        Certificate certificate = certificateFactory.generateCertificate(in);
        return certificate;
    }

    /**
     * 验证证书有效性
     *
     * @param rootIn 根证书名
     * @param certIn 客户端证书路径
     * @return
     */
    private boolean verify(InputStream rootIn, InputStream certIn) {
        try {
            // 获得证书
            X509Certificate x509Certificate = (X509Certificate)getCertificate(certIn);
            // x509Certificate.checkValidity(); // 检查证书目前是否有效。
            X509Certificate rootCertificate = (X509Certificate)getCertificate(rootIn);
            x509Certificate.verify(rootCertificate.getPublicKey());
            rootIn.close();
            certIn.close();
            String str = x509Certificate.getSubjectDN().getName();
            return str.substring(3, str.indexOf(',')).equals(CommenUnit.mID);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 检验证书有效性
     */
    private void certificateCheck() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File fCert = new File(CommenUnit.WORK_DIR + CommenUnit.mID + ".cer");
                File fCertBak = new File(CommenUnit.WORK_DIR + "." + CommenUnit.mID + ".cer");
                if (!fCert.exists() && !fCertBak.exists()) {
                    // 合成官网服务器验证地址
                    // String orgUrl =
                    // PrefUtils.getInstance().getString("orgUrl",
                    // "http://192.168.100.53:8088/");
                    String orgUrl = LocalData.settings.orgUrl;
                    StringBuffer url = new StringBuffer();
                    url.append(orgUrl).append("resources/application/root/").append(CommenUnit.mID)
                            .append(".cer");
                    // 请求证书
                    if (!CommenUnit.requestServerByGet(url.toString(), null,
                            fCert.getAbsolutePath())) {
                        mHand.sendEmptyMessage(0);
                        mIsLegal = false;
                    }
                } else if (!fCert.exists() && fCertBak.exists()) {
                    try {
                        CommenUnit.copyFile(fCertBak, fCert);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    fCert = new File(CommenUnit.WORK_DIR + CommenUnit.mID + ".cer");
                }
                try {
                    InputStream rootIs = getAssets().open("sundynroot.cer");
                    FileInputStream certIs = new FileInputStream(fCert);
                    if (!verify(rootIs, certIs)) {
                        mHand.sendEmptyMessage(0);
                        mIsLegal = false;
                    }
                } catch (IOException e) {
                    mHand.sendEmptyMessage(0);
                    mIsLegal = false;
                    e.printStackTrace();
                }
                mIsChecked = true;
                // 验证成功，备份证书
                if (!fCertBak.exists()) {
                    try {
                        CommenUnit.copyFile(fCert, fCertBak);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 创建桌面快捷方式
     */
    private void createShortCut() {
        if (hasShortcut()) {
            return;
        }
        final Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        final Parcelable icon = Intent.ShortcutIconResource.fromContext(this,
                R.drawable.ic_launcher); // 获取快捷键的图标
        addIntent.putExtra("duplicate", false);
        final Intent myIntent = new Intent(getApplicationContext(), SplashAct.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, this.getString(R.string.app_name));// 快捷方式的标题
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);// 快捷方式的图标
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, myIntent);// 快捷方式的动作
        sendBroadcast(addIntent);
    }

    /**
     * 判断桌面快捷方式是否存在
     *
     * @return
     */
    private boolean hasShortcut() {
        boolean hasShortcut = false;
        final ContentResolver cr = this.getContentResolver();
        final String AUTHORITY = "com.android.launcher2.settings";
        final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/favorites?notify=true");
        Cursor c = cr.query(CONTENT_URI, new String[] {
                "title"
        }, "title=?", new String[] {
                this.getString(R.string.app_name).trim()
        }, null);
        if (c != null && c.getCount() > 0) {
            hasShortcut = true;
        }
        return hasShortcut;
    }

    /*
     * (non-Javadoc)
     * @see com.sundyn.centralizedeval.BaseAct#skinColorChange()
     */
    @Override
    protected void skinColorChange() {

    }


}
