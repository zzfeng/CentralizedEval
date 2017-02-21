package com.sundyn.centralizedeval.base;

import android.app.Fragment;
import android.content.Intent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;

/**
 * Created by Administrator on 2017/2/21.
 */

public class BaseItem extends Fragment {

    public AnimationSet mAnimIn;
    public AnimationSet mAnimOut;

    // private BroadcastReceiver mReceiver;

    /**
     * 初始化本色块的动画变量 1.3.2开始去除位移动画 by gcy
     */
    public void initItem() {
        // mAnimIn = new AnimationSet(true);
        // mAnimIn.setDuration(1000);
        // mAnimIn.setFillBefore(true);
        // mAnimIn.addAnimation(new
        // TranslateAnimation(Animation.RELATIVE_TO_SELF,
        // 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
        // 0f, Animation.RELATIVE_TO_SELF, 0f));
        // mAnimIn.addAnimation(new AlphaAnimation(0f, 1f));
        //
        // mAnimOut = new AnimationSet(true);
        // mAnimOut.setDuration(1000);
        // mAnimOut.setFillAfter(true);
        // mAnimOut.addAnimation(new TranslateAnimation(
        // Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1f,
        // Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f));
        // mAnimOut.addAnimation(new AlphaAnimation(1f, 0f));
        // // 渐隐广播监听
        // mReceiver = new BroadcastReceiver() {
        //
        // @Override
        // public void onReceive(Context context, Intent intent) {
        // String act = intent.getAction();
        // if (act.equals("dismiss")) {
        // screenOut();
        // } else if (act.equals("appear")) {
        // screenIn();
        // }
        // }
        // };
        // IntentFilter filter = new IntentFilter();
        // filter.addAction("dismiss");
        // filter.addAction("appear");
        // getActivity().registerReceiver(mReceiver, filter);
    }

    // /**
    // * 色块动画移出主屏幕
    // */
    // private void screenOut() {
    // //getView().startAnimation(mAnimOut);
    // }
    //
    // /**
    // * 色块动画移入主屏幕
    // */
    // private void screenIn() {
    // //getView().startAnimation(mAnimIn);
    // }

    /**
     * 点击效果
     *
     * @param intent 点击后出发的intent
     */
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

    @Override
    public void onDestroy() {
        // getActivity().unregisterReceiver(mReceiver);
        super.onDestroy();
    }


}
