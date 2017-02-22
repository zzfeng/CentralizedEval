package com.sundyn.centralizedeval.views;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.utils.SystemUtil;
import com.sundyn.centralizedeval.utils.UIUtil;

/**
 * Created by Administrator on 2017/2/22.
 */

public class EvalDlg extends Dialog {

    private static String TAG = "EvalDlg";

    private Button btnOk;
    private EditText etMsg;
    private OnOkBtnClickListener onOkBtnClickListener;

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            btnOk.performClick();
        }
    };

    /**
     * @param context
     */
    public EvalDlg(Context context) {
        super(context, R.style.dlg_blackbg_style);
        setCancelable(false);
    }

    public interface OnOkBtnClickListener {
        public void onClick(String msg);
    }

    /*
     * (non-Javadoc)
     * @see android.app.Dialog#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dlg_eval);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        try {
            initView();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

    }

    private void initView() throws Exception {
        btnOk = (Button)findViewById(R.id.btn_ok);
        etMsg = (EditText)findViewById(R.id.et_msg);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = etMsg.getText().toString().trim();
                if (onOkBtnClickListener != null) {
                    UIUtil.removeCallbacks(runnable);
                    SystemUtil.hideSoftInputMethod(etMsg);
                    onOkBtnClickListener.onClick(msg);
                    dismiss();
                }
            }
        });
        etMsg.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                UIUtil.removeCallbacks(runnable);
                UIUtil.postDelayed(runnable, 30000);
            }
        });
        UIUtil.postDelayed(runnable, 30000);
    }

    public void setOkBtnClickListener(OnOkBtnClickListener onOkBtnClickListener) {
        this.onOkBtnClickListener = onOkBtnClickListener;
    }

    @Override
    protected void onStart() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onStart();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_APP_SWITCH) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static final int FLAG_HOMEKEY_DISPATCHED = 0x80000000;

    @Override
    public void onAttachedToWindow() {
        this.getWindow().addFlags(FLAG_HOMEKEY_DISPATCHED);
        // getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        super.onAttachedToWindow();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onWindowFocusChanged(boolean)
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!hasFocus) {
            ActivityManager am = (ActivityManager)getContext().getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

            if (cn != null
                    && cn.getClassName().equals("com.android.systemui.recent.RecentsActivity")) {
                toggleRecents();
            }
        }
    }

    private void toggleRecents() {
        Intent closeRecents = new Intent("com.android.systemui.recent.action.TOGGLE_RECENTS");
        closeRecents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        ComponentName recents = new ComponentName("com.android.systemui",
                "com.android.systemui.recent.RecentsActivity");
        closeRecents.setComponent(recents);
        getContext().getApplicationContext().startActivity(closeRecents);
    }


}
