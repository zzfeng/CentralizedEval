package com.sundyn.centralizedeval.views;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Gallery;
import android.os.Handler;

/**
 * Created by Administrator on 2017/2/21.
 */

public class AdsGallery extends Gallery {
    private Handler msgHandle;

    public AdsGallery(Context context, Handler hand) {
        super(context);
        msgHandle = hand;
        setSpacing(0);
        setSoundEffectsEnabled(false);
        setFadingEdgeLength(0);
        Gallery.LayoutParams params = new Gallery.LayoutParams(
                Gallery.LayoutParams.MATCH_PARENT,
                Gallery.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
    }

    public AdsGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AdsGallery(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {
        int kEvent;
        if (isScrollingLeft(e1, e2)) {
            kEvent = KeyEvent.KEYCODE_DPAD_LEFT;
            msgHandle.sendEmptyMessage(1);
        } else {
            kEvent = KeyEvent.KEYCODE_DPAD_RIGHT;
            msgHandle.sendEmptyMessage(0);
        }
        onKeyDown(kEvent, null);
        return true;
    }

    private boolean isScrollingLeft(MotionEvent e1, MotionEvent e2) {
        return e2.getX() > e1.getX();
    }

    public void scrollToNext() {
        MotionEvent e1 = MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 300.0f,
                265.33334f, 0);
        MotionEvent e2 = MotionEvent.obtain(SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 89.333336f,
                238.00003f, 0);

        onFling(e1, e2, -800, 0);
    }
}
