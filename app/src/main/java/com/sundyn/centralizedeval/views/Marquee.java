package com.sundyn.centralizedeval.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/2/21.
 */

public class Marquee extends TextView {


    public Marquee(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onFocusChanged(boolean focused, int direction,
                                  Rect previouslyFocusedRect) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean isFocused() {
        // TODO Auto-generated method stub
        return true;
    }

    public Marquee(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public Marquee(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }


}
