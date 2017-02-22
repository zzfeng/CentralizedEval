package com.sundyn.centralizedeval.activity;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.base.BaseAct;
import com.sundyn.centralizedeval.bean.Department;
import com.sundyn.centralizedeval.fragment.GalleryFrag;
import com.sundyn.centralizedeval.fragment.GridFrag;
import com.sundyn.centralizedeval.fragment.ListFrag;
import com.sundyn.centralizedeval.utils.LocalData;
import com.sundyn.centralizedeval.utils.UIUtil;

/**
 * Created by Administrator on 2017/2/21.
 */

public class EmployeeAct extends BaseAct implements View.OnClickListener{

    private static String TAG = "EmployeeAct";
    private Button mBtn_list, mBtn_grid, mBtn_gallery, btnAdd;
    private FragmentTransaction mTransaction;
    private ListFrag mFragmentList;
    private GridFrag mFragmentGrid;
    private GalleryFrag mFragmentGallery;
    private int deptPos;
    // 封装position信息
    private Bundle mBundle;
    // 得到被点击的某个具体部门
    private Department dept;
    private android.app.FragmentManager mManager;
    private RelativeLayout titleLayout;
    private boolean isClick = false;
    private boolean isAnim = false;

    /*
     * (non-Javadoc)
     *
     * @see com.sundyn.centralizedeval.BaseAct#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_showemployee);
        // 初始化控件
        init();
        // 替换界面
        replace();
        // 修改界面皮肤
        skinColorChange();
    }

    // fragment替换界面,默认以列表方式显示
    private void replace() {
        mManager = getFragmentManager();
        mTransaction = mManager.beginTransaction();
        mTransaction.replace(R.id.fl, mFragmentList);
        mTransaction.commit();
    }

    private void init() {
        // createJson();
        titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
        btnAdd = (Button) findViewById(R.id.btn_add);
        mBtn_list = (Button) findViewById(R.id.btn_list);
        mBtn_grid = (Button) findViewById(R.id.btn_grid);
        mBtn_gallery = (Button) findViewById(R.id.btn_gallery);
        btnAdd.setOnClickListener(this);
        mBtn_list.setOnClickListener(this);
        mBtn_grid.setOnClickListener(this);
        mBtn_gallery.setOnClickListener(this);
        mFragmentList = new ListFrag();
        mFragmentGrid = new GridFrag();
        mFragmentGallery = new GalleryFrag();
        // 获取上一个界面传递过来的数据
        boolean showDEPT = LocalData.organization.isShowDept();
        if (showDEPT) {
            deptPos = getIntent().getExtras().getInt("deptPos");
            // dept = LoacalData.organization.getDepartments().get(deptPos);
            // 封装位置信息传递到fragment界面
            mBundle = new Bundle();
            mBundle.putInt("deptPos", deptPos);
            mFragmentList.setArguments(mBundle);
            mFragmentGrid.setArguments(mBundle);
            mFragmentGallery.setArguments(mBundle);
        }
    }

    // 处理条目的点击事件
    @Override
    public void onClick(View v) {
        android.app.FragmentManager mManager = getFragmentManager();
        mTransaction = mManager.beginTransaction();
        switch (v.getId()) {
            // 替换界面
            case R.id.btn_list:
                mTransaction.replace(R.id.fl, mFragmentList);
                break;
            case R.id.btn_grid:
                mTransaction.replace(R.id.fl, mFragmentGrid);
                break;
            case R.id.btn_gallery:
                mTransaction.replace(R.id.fl, mFragmentGallery);
                break;
            case R.id.btn_add:
                if (!isClick) {
                    if (!isAnim) {
                        showBtnAnimation();
                    }
                } else {
                    if (!isAnim) {
                        hideBtnAnimation();
                    }
                }
                break;
        }
        mTransaction.commit();
    }

    // 处理后退的点击事件
    public void back(View v) {
        // Intent intent = new Intent();
        // intent.setClass(EmployeeAct.this, DepartmentAct.class);
        // startActivity(intent);
        // 关闭此界面
        finish();
    }

    private void showBtnAnimation() {
        isAnim = true;
        isClick = true;
        AnimationSet gallerySet = (AnimationSet) AnimationUtils.loadAnimation(
                this, R.anim.show_gallery);
        mBtn_gallery.startAnimation(gallerySet);
        AnimationSet gridSet = (AnimationSet) AnimationUtils.loadAnimation(
                this, R.anim.show_grid);
        mBtn_grid.startAnimation(gridSet);
        AnimationSet listSet = (AnimationSet) AnimationUtils.loadAnimation(
                this, R.anim.show_list);
        mBtn_list.startAnimation(listSet);

        gallerySet.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params = (LayoutParams) mBtn_gallery
                        .getLayoutParams();
                params.bottomMargin += 100;
                mBtn_gallery.setLayoutParams(params);
                mBtn_gallery.clearAnimation();
            }
        });

        gridSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params = (LayoutParams) mBtn_grid
                        .getLayoutParams();
                params.bottomMargin += 80;
                params.rightMargin += 80;
                mBtn_grid.setLayoutParams(params);
                mBtn_grid.clearAnimation();
                btnAdd.setBackgroundResource(R.drawable.btn_minus);
                isAnim = false;
            }
        });

        listSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params = (LayoutParams) mBtn_list
                        .getLayoutParams();
                params.rightMargin += 100;
                mBtn_list.setLayoutParams(params);
                mBtn_list.clearAnimation();
            }
        });
    }

    private void hideBtnAnimation() {
        isAnim = true;
        isClick = false;
        AnimationSet gallerySet = (AnimationSet) AnimationUtils.loadAnimation(
                this, R.anim.hide_gallery);
        mBtn_gallery.startAnimation(gallerySet);
        AnimationSet gridSet = (AnimationSet) AnimationUtils.loadAnimation(
                this, R.anim.hide_grid);
        mBtn_grid.startAnimation(gridSet);
        AnimationSet listSet = (AnimationSet) AnimationUtils.loadAnimation(
                this, R.anim.hide_list);
        mBtn_list.startAnimation(listSet);

        gallerySet.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params = (LayoutParams) mBtn_gallery
                        .getLayoutParams();
                params.bottomMargin -= 100;
                mBtn_gallery.setLayoutParams(params);
                mBtn_gallery.clearAnimation();
            }
        });

        gridSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params = (LayoutParams) mBtn_grid
                        .getLayoutParams();
                params.bottomMargin -= 80;
                params.rightMargin -= 80;
                mBtn_grid.setLayoutParams(params);
                mBtn_grid.clearAnimation();
                btnAdd.setBackgroundResource(R.drawable.btn_add);
                isAnim = false;
            }
        });

        listSet.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                RelativeLayout.LayoutParams params = (LayoutParams) mBtn_list
                        .getLayoutParams();
                params.rightMargin -= 100;
                mBtn_list.setLayoutParams(params);
                mBtn_list.clearAnimation();
            }
        });
    }

    @Override
    protected void skinColorChange() {
        // TODO Auto-generated method stub
        titleLayout.setBackgroundColor(LocalData.skinColor);

        UIUtil.postDelayed(new Runnable() {

            @Override
            public void run() {
                mFragmentList.changeSkin();
                mFragmentGrid.changeSkin();
                mFragmentGallery.changeSkin();
            }
        }, 2000);
    }


}
