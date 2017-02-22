package com.sundyn.centralizedeval.plugin;

import android.content.Intent;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.activity.FillAdviseAct;
import com.sundyn.centralizedeval.commen.CommenUnit;

import java.io.File;

/**
 * Created by Administrator on 2017/2/21.
 */

public class AdviseItem extends Fragment{

    // 定时请求服务器通知信息
    private Handler mAdviseLoop = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    // 角标动画
                    if (getView() == null) {
                        return;
                    }
                    ImageView iv = (ImageView)getView().findViewById(R.id.idImage);
                    Rotate3dAnimation ra = new Rotate3dAnimation(0f, -360f, iv.getWidth() / 2f,
                            iv.getHeight() / 2f, 0f, false);
                    ra.setDuration(4000);
                    iv.startAnimation(ra);

                    if ("1".equals(CommenUnit.m8Config.type) && CommenUnit.isNetworkAvail) { // 如果是网络连接模式，直接从服务器获取
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 合成服务器地址
                                // String url =
                                // CommenUnit.m8Config.getServerAddr() +
                                // "adviceDownload";
                                String url = CommenUnit.m8Config.getServerAddr()
                                        + "estime/android_enquest/questions?mac=" + CommenUnit.mID;
                                boolean b = CommenUnit.requestServerByGet(url, null,
                                        CommenUnit.ADVISE_DIR + "advise_new.xml");
                                File adviseOld = new File(CommenUnit.ADVISE_DIR + "advise.xml");
                                File adviseNew = new File(CommenUnit.ADVISE_DIR + "advise_new.xml");
                                // // 如果接收到了advise.db，删除协议发送的XML
                                // SharedPreferences sp =
                                // getActivity().getApplicationContext()
                                // .getSharedPreferences("DemoItem",
                                // Context.MODE_PRIVATE);
                                // String adviceFileName =
                                // sp.getString("advise", "advise.xml");
                                // File adviseXML = new File(CommenUnit.WORK_DIR
                                // + adviceFileName);
                                if (b && adviseNew.exists()) {
                                    adviseOld.delete();
                                    adviseNew.renameTo(adviseOld);
                                    // if (adviseXML.exists()) {
                                    // adviseXML.delete();
                                    // }
                                } else {
                                    adviseNew.delete();
                                }
                                mAdviseLoop.sendEmptyMessageDelayed(0, 2 * 60 * 1000);
                            }
                        }).start();

                    }
                    break;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View parent = inflater.inflate(R.layout.item_advise, null);
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FillAdviseAct.class);
                clickAnim(intent);
            }
        });

        mAdviseLoop.sendEmptyMessage(0);

        //initItem();

        return parent;
    }
    public void clickAnim(final Intent intent) {
        final AnimationSet set = new AnimationSet(true);
        ScaleAnimation zoomIn = new ScaleAnimation(1f, 0.95f, 1f, 0.95f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        zoomIn.setDuration(200);
        zoomIn.setFillAfter(true);

        AlphaAnimation aa = new AlphaAnimation(1f, 0.3f);
        aa.setDuration(200);
        aa.setFillAfter(true);

        // ScaleAnimation zoomOut = new ScaleAnimation(0.95f, 1.01f, 0.95f,
        // 1.01f,
        // Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
        // 0.5f);
        // zoomOut.setDuration(200);
        // zoomOut.setStartOffset(200);
        // zoomOut.setFillAfter(true);

        set.addAnimation(zoomIn);
        set.addAnimation(aa);
        // set.addAnimation(zoomOut);
        set.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                startActivity(intent);
            }
        });
        getView().startAnimation(set);

    }
    /**
     * 自定义3d旋转动画
     *
     * @author Paul
     */
    private class Rotate3dAnimation extends Animation {
        // 开始角度
        private final float mFromDegrees;
        // 结束角度
        private final float mToDegrees;
        // 中心点
        private final float mCenterX;
        private final float mCenterY;
        private final float mDepthZ;
        // 是否需要扭曲
        private final boolean mReverse;
        // 摄像头
        private Camera mCamera;

        public Rotate3dAnimation(float fromDegrees, float toDegrees, float centerX, float centerY,
                                 float depthZ, boolean reverse) {
            mFromDegrees = fromDegrees;
            mToDegrees = toDegrees;
            mCenterX = centerX;
            mCenterY = centerY;
            mDepthZ = depthZ;
            mReverse = reverse;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            mCamera = new Camera();
        }

        // 生成Transformation
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            final float fromDegrees = mFromDegrees;
            // 生成中间角度
            float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

            final float centerX = mCenterX;
            final float centerY = mCenterY;
            final Camera camera = mCamera;

            final Matrix matrix = t.getMatrix();

            camera.save();
            if (mReverse) {
                camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
            } else {
                camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
            }
            camera.rotateY(degrees);
            // 取得变换后的矩阵
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }


}
