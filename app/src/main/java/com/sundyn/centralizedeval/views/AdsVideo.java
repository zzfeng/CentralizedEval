package com.sundyn.centralizedeval.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by Administrator on 2017/2/21.
 */

public class AdsVideo extends VideoView {

    public AdsVideo(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public AdsVideo(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public AdsVideo(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(4, widthMeasureSpec);
        int height = getDefaultSize(3, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    // public void setVideoScale(int width, int height) {
    // LayoutParams lp = getLayoutParams();
    // lp.height = height;
    // lp.width = width;
    // setLayoutParams(lp);
    // }
}