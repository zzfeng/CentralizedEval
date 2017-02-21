package com.sundyn.centralizedeval.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.base.BaseAct;
import com.sundyn.centralizedeval.commen.CommenUnit;
import com.sundyn.centralizedeval.utils.LocalData;

/**
 * Created by Administrator on 2017/2/21.
 */

public class DeviceAct extends BaseAct implements View.OnClickListener {

    private static String TAG = "DeviceAct";

    private Button btnExit;
    private Button btnBack;
    private TextView tvMac;
    private TextView tvVer;
    private TextView tvIp;
    private EditText etServerIp;
    private EditText etServerPort;
    private EditText etOrgUrl;

    private final String mCode = "sundyn"; // 管理员密码
    private Dialog mRootDialog; // 管理员确认对话框
    private int mNextAction; // 确认管理员后的动作,0返回，1退出，2升级
    private boolean mIsRoot; // 是否输入过正确的密码

    private RelativeLayout titleLayout;
    private ImageView ivSkin1;
    private ImageView ivSkin2;
    private ImageView ivSkin3;
    private ImageView ivSkin4;

    /*
     * (non-Javadoc)
     * @see com.sundyn.centralizedeval.BaseAct#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_device);

        try {
            initView();
            initData();
            skinColorChange();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void initView() throws Exception {
        titleLayout = (RelativeLayout)findViewById(R.id.title_layout);

        btnExit = (Button)findViewById(R.id.btn_exit);
        btnBack = (Button)findViewById(R.id.btn_back);

        tvMac = (TextView)findViewById(R.id.idMac);
        tvVer = (TextView)findViewById(R.id.idVer);
        tvIp = (TextView)findViewById(R.id.idIp);
        etServerIp = (EditText)findViewById(R.id.idServerIp);
        etServerPort = (EditText)findViewById(R.id.idServerPort);
        etOrgUrl = (EditText)findViewById(R.id.idOrgUrl);

        ivSkin1 = (ImageView)findViewById(R.id.iv_skin1);
        ivSkin2 = (ImageView)findViewById(R.id.iv_skin2);
        ivSkin3 = (ImageView)findViewById(R.id.iv_skin3);
        ivSkin4 = (ImageView)findViewById(R.id.iv_skin4);

        ivSkin1.setOnClickListener(this);
        ivSkin2.setOnClickListener(this);
        ivSkin3.setOnClickListener(this);
        ivSkin4.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        ((Button)findViewById(R.id.btn_test)).setOnClickListener(this);

        // 创建管理员提示对话框
        final EditText codeEt = new EditText(this);
        codeEt.setSingleLine();
        codeEt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mRootDialog = new AlertDialog.Builder(DeviceAct.this).setTitle("请输入管理密码")
                .setIcon(android.R.drawable.ic_dialog_info).setView(codeEt)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 对比密码
                        if (mCode.equals(codeEt.getText().toString())) {
                            mIsRoot = true;
                            // 隐藏软键盘
                            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(codeEt.getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);
                            if (mNextAction == 0) {
                                // 保存并返回主界面
                                saveConfig();
                            } else if (mNextAction == 1) {
                                // 退出程序
                                CommenUnit.destroyApp(getApplicationContext());
                            } else if (mNextAction == 2) {
                                // 开始检查升级
                                // updateApp();
                            }
                        } else {
                            // 显示密码报错对话框
                            new AlertDialog.Builder(DeviceAct.this)
                                    .setTitle("密码错误！")
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .setPositiveButton(getString(R.string.ok),
                                            new android.content.DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog,
                                                                    int which) {
                                                    mRootDialog.show();
                                                }

                                            }).show();
                        }
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(codeEt.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                        // if (mNextAction == 0) {
                        // FillDevice.this.finish();
                        // }
                    }
                }).create();
    }

    private void initData() {
        tvMac.setText(SystemUtils.getMacAddress());
        tvVer.setText(PackageUtils.getVersionName());
        tvIp.setText(SystemUtils.getLocalIpAddress());
        etServerIp.setText(LocalData.settings.serverIp);
        etServerPort.setText(LocalData.settings.serverPort);
        etOrgUrl.setText(LocalData.settings.orgUrl);
    }

    private void saveConfig() {
        LocalData.settings.orgUrl = etOrgUrl.getText().toString().trim();
        LocalData.settings.serverIp = etServerIp.getText().toString().trim();
        LocalData.settings.serverPort = etServerPort.getText().toString().trim();

        LocalData.saveSettings();
        // LocalData.updateHost(LocalData.settings.getServerHost());
    }

    /**
     * 比较配置信息是否被修改
     *
     * @return
     */
    private boolean getChangeStatus() {
        // 比较服务器IP
        if (!LocalData.settings.serverIp.equals(etServerIp.getText().toString())) {
            return true;
        }
        // 比较服务器端口
        if (!LocalData.settings.serverPort.equals(etServerPort.getText().toString().trim())) {
            return true;
        }
        // 比较官网服务器地址
        if (!LocalData.settings.orgUrl.equals(etOrgUrl.getText().toString().trim())) {
            return true;
        }
        return false;
    }

    /**
     * 检查官网更新
     */
    void updateApp() {
        // 显示检测对话框
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_back:
                if (getChangeStatus()) {
                    if (!mIsRoot) {
                        mNextAction = 0;
                        // 输入密码
                        mRootDialog.show();
                    } else {
                        saveConfig();
                    }
                } else {
                    DeviceAct.this.finish();
                }
                break;
            case R.id.btn_exit:
                if (!mIsRoot) {
                    mNextAction = 1;
                    // 输入密码
                    mRootDialog.show();
                } else {
                    CommenUnit.destroyApp(getApplicationContext());
                }
                break;

            case R.id.btn_test:
                // EvalDlg evalDlg = new EvalDlg(DeviceAct.this);
                // evalDlg.setOkBtnClickListener(new
                // EvalDlg.OnOkBtnClickListener() {
                // @Override
                // public void onClick(String msg) {
                // // UIUtils.showToastSafe(msg);
                // }
                // });
                // evalDlg.show();

                break;

            case R.id.iv_skin1:
            case R.id.iv_skin2:
            case R.id.iv_skin3:
            case R.id.iv_skin4:
                changeSkin(v);
                break;
            default:
                break;
        }
    }

    private void changeSkin(View view) {
        String color = view.getTag().toString();
        LocalData.skinColor = Color.parseColor(color);
        PrefUtils.getInstance().putString("skinColor", color);
        sendBroadcast(new Intent(LocalData.ACTION_SKIN_CHANGE));
    }

    @Override
    protected void skinColorChange() {
        titleLayout.setBackgroundColor(LocalData.skinColor);
    }


}
