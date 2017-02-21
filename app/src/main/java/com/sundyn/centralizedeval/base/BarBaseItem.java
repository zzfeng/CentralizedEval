package com.sundyn.centralizedeval.base;

import android.app.Fragment;
import android.view.animation.AnimationSet;

/**
 * Created by Administrator on 2017/2/21.
 */

public class BarBaseItem extends Fragment {
    public AnimationSet mAnimIn;
    public AnimationSet mAnimOut;

    /**
     * 初始化本色块的动画变量 1.3.2开始去除位移动画
     */
    public void initItem() {
//		mAnimIn = new AnimationSet(true);
//		mAnimIn.setDuration(300);
//		mAnimIn.setFillBefore(true);
//		mAnimIn.setInterpolator(new OvershootInterpolator());
//		mAnimIn.addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF,
//				4f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
//				0f, Animation.RELATIVE_TO_SELF, 0f));
//		mAnimIn.addAnimation(new AlphaAnimation(0f, 1f));
//
//		mAnimOut = new AnimationSet(true);
//		mAnimOut.setDuration(300);
//		mAnimOut.setFillAfter(true);
//		mAnimOut.addAnimation(new TranslateAnimation(
//				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 4f,
//				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f));
//		mAnimOut.addAnimation(new AlphaAnimation(1f, 0f));
    }

    /**
     * 色块动画移出下边栏
     */
    public void barOut() {
        //getView().startAnimation(mAnimOut);
    }

    /**
     * 色块动画移入下边栏
     */
    public void barIn() {
        //getView().startAnimation(mAnimIn);
    }

}
