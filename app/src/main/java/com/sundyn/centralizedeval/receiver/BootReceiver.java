package com.sundyn.centralizedeval.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.sundyn.centralizedeval.activity.SplashAct;
import com.sundyn.centralizedeval.commen.CommenUnit;
import com.sundyn.centralizedeval.utils.DebugUtil;

/**
 * Created by Administrator on 2017/2/21.
 */

public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "BootReceiver";

    /*
     * (non-Javadoc)
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        while (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            SystemClock.sleep(1000);
        }

        String action = intent.getAction();
        if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals("BROADCAST_REBOOT")
                || action.equals("start")) {
            SystemClock.sleep(5000);
            Intent autoIntent = new Intent(context, SplashAct.class);
            autoIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // autoIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // autoIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            autoIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            context.startActivity(autoIntent);
            DebugUtil.i(TAG, "启动程序！");
        } else if (action.equals("exit")) {
            CommenUnit.destroyApp(context.getApplicationContext());
        } else if (action.equals("reboot")) {
            CommenUnit.powerup();
        } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            CommenUnit.isNetworkAvail = !intent.getBooleanExtra(
                    ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            Log.e(TAG, "当前网络状态为" + CommenUnit.isNetworkAvail);
        } else if (action.equals("refreshDept")) {
            CommenUnit.restartApk(context);
        }
    }

}
