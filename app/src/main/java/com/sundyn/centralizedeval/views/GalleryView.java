package com.sundyn.centralizedeval.views;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import android.widget.Gallery;

/**
 * Created by Administrator on 2017/2/22.
 */

public class GalleryView extends Gallery {

    private static String TAG = "GalleryView";

    private Camera mCamera = new Camera();
    private int mMaxRotationAngle = 30; // 最大旋转角度30
    private int mCoveflowCenter;

    public GalleryView(Context context) {
        super(context);
        this.setStaticTransformationsEnabled(true);
        setGallery();
    }

    public GalleryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setStaticTransformationsEnabled(true);
        setGallery();
    }

    public GalleryView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.setStaticTransformationsEnabled(true);
        setGallery();
    }

    private void setGallery() {
        setSpacing(10);
        setFadingEdgeLength(0);
    }

    public int getMaxRotationAngle() {
        return mMaxRotationAngle;
    }

    public void setMaxRotationAngle(int maxRotationAngle) {
        mMaxRotationAngle = maxRotationAngle;
    }

    /** 获取Gallery的中心x */
    private int getCenterOfCoverflow() {
        return (getWidth() - getPaddingLeft() - getPaddingRight()) / 2 + getPaddingLeft();
    }

    /** 获取View的中心x */
    private static int getCenterOfView(View view) {
        return view.getLeft() + view.getWidth() / 2;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCoveflowCenter = getCenterOfCoverflow();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected boolean getChildStaticTransformation(View child, Transformation trans) {
        super.getChildStaticTransformation(child, trans);
        final int childCenter = getCenterOfView(child);
        final int childWidth = child.getWidth();
        int rotationAngle = 0;
        trans.clear();
        // alpha 和matrix都变换
        trans.setTransformationType(Transformation.TYPE_BOTH);
        if (child == getSelectedView()) {
            transformView((GalleryItem)child, trans, 0);
        } else {
            rotationAngle = (int)(((float)(mCoveflowCenter - childCenter) / childWidth) * mMaxRotationAngle);
            if (rotationAngle > 0) {
                rotationAngle = -20;
            } else {
                rotationAngle = 20;
            }
            transformView((GalleryItem)child, trans, rotationAngle);
        }
        return true;
    }

    private void transformView(GalleryItem child, Transformation trans, int rotationAngle) {
        mCamera.save();
        final Matrix imageMatrix = trans.getMatrix();
        final int imageWidth = child.getWidth();
        mCamera.rotateY(rotationAngle);
        mCamera.getMatrix(imageMatrix);
        if (rotationAngle < 0) {
            imageMatrix.preTranslate(-imageWidth, 0);
            imageMatrix.postTranslate(imageWidth, 0);
        }
        mCamera.restore();
    }


}
