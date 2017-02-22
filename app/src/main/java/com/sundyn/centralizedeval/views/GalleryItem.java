package com.sundyn.centralizedeval.views;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.bean.UserBean;
import com.sundyn.centralizedeval.fragment.GalleryFrag;
import com.sundyn.centralizedeval.utils.LocalData;

/**
 * Created by Administrator on 2017/2/22.
 */

public class GalleryItem extends RelativeLayout {

    private static String TAG = "GalleryItem";

    private Context context;
    private ImageView ivUser;
    private TextView tvRealName;
    private TextView tvJobTitle;
    private TextView tvJobDesc;
    private TextView tvJob;
    private ImageView iv;
    private UserBean user = new UserBean();

    /**
     * @param context
     */
    public GalleryItem(Context context) {
        super(context);
        this.context = context;

        try {
            initView();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void initView() throws Exception {
        LayoutInflater.from(context).inflate(R.layout.fragment_gallery_item,
                this);
        ivUser = (ImageView) findViewById(R.id.iv_user);
        tvRealName = (TextView) findViewById(R.id.tv_realName);
        tvJob = (TextView) findViewById(R.id.tv_job);
        tvJobTitle = (TextView) findViewById(R.id.tv_jobTitle);
        tvJobDesc = (TextView) findViewById(R.id.tv_jobDesc);
        iv = (ImageView) findViewById(R.id.imageView1);
    }

    public void setUser(UserBean user) {
        if (user != null) {
            this.user = user;
            GalleryFrag.mBitmapUtils.display(ivUser, user.getPicPath());
            tvJob.setText(user.getJob());
            tvJobTitle.setText(user.getJobTitle());
            tvJobDesc.setText(user.getJobDesc());
            tvRealName.setText(user.getRealName());
            // 修改界面
            iv.setBackgroundColor(LocalData.skinColor);
            tvJobTitle.setTextColor(LocalData.skinColor);
            tvJob.setBackgroundColor(LocalData.skinColor);
        }
    }

}
