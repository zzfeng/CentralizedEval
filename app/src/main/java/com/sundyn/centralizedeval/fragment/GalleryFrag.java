package com.sundyn.centralizedeval.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.lidroid.xutils.BitmapUtils;
import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.activity.EvalAct;
import com.sundyn.centralizedeval.bean.UserBean;
import com.sundyn.centralizedeval.utils.LocalData;

import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class GalleryFrag extends Fragment{


    // private Gallery mGallery;
    private GalleryView mGallery;
    private List<UserBean> users;
    private int deptPos;
    public static BitmapUtils mBitmapUtils;
    private GalleryAdapter mAdapter;

    /**
     * 画廊展示医师列表信息
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, null);
        mBitmapUtils = new BitmapUtils(getActivity());
        mGallery = (GalleryView) view.findViewById(R.id.gallery2);
        // users = (List<UserBean>) LocalData.organization.getDepartments()
        // .get(deptPos).getUsers();
        // 是否显示部门
        boolean showDEPT = LocalData.organization.isShowDept();
        if (showDEPT) {
            deptPos = (Integer) getArguments().get("deptPos");
            users = (List<UserBean>) LocalData.organization.getDepartments()
                    .get(deptPos).getUsers();
        } else {
            users = LocalData.users;
        }
        mAdapter = new GalleryAdapter(getActivity(), users);
        mGallery.setAdapter(mAdapter);

        mGallery.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), EvalAct.class);
                intent.putExtra("deptPos", deptPos);
                intent.putExtra("userPos", position);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                getActivity().finish();
            }
        });

        // 当接收到更换皮肤的广播时调用此方法更换界面皮肤
        changeSkin();

        return view;
    }

    // 更换界面皮肤
    public void changeSkin() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();

        }
    }
    // private ViewHolder mHolder;
    // class MyAdapter extends BaseAdapter {
    // @Override
    // public int getCount() {
    // return users.size();
    // }
    //
    // @Override
    // public Object getItem(int arg0) {
    // return null;
    // }
    //
    // @Override
    // public long getItemId(int position) {
    // return 0;
    // }
    //
    // @Override
    // public View getView(int position, View convertView, ViewGroup parent) {
    // mHolder = new ViewHolder();
    // if (convertView == null) {
    // convertView = View.inflate(getActivity(), R.layout.fragment_gallery_item,
    // null);
    // mHolder.image_view = (ImageView)convertView.findViewById(R.id.iv_user);
    // mHolder.tv_name = (TextView)convertView.findViewById(R.id.tv_realName);
    // mHolder.tv_jobTitle =
    // (TextView)convertView.findViewById(R.id.tv_jobTitle);
    // mHolder.tv_jobDesc = (TextView)convertView.findViewById(R.id.tv_jobDesc);
    // convertView.setTag(mHolder);
    // // mHolder.tv_job = (TextView) convertView
    // // .findViewById(R.id.tv_job);
    // } else {
    // mHolder = (ViewHolder)convertView.getTag();
    // }
    // if (users != null && users.size() > 0) {
    // // 读本地
    // String url = users.get(position).getPicPath();
    // Log.i("addView", url);
    // mBitmapUtils.display(mHolder.image_view, url);
    // mHolder.tv_name.setText(users.get(position).getRealName());
    // mHolder.tv_jobTitle.setText(users.get(position).getJobTitle());
    // mHolder.tv_jobDesc.setText(users.get(position).getJobDesc());
    // // mHolder.tv_job.setText(users.get(position).getJob());
    // }
    // return convertView;
    // }
    // }
    //
    // class ViewHolder {
    // ImageView image_view;
    // TextView tv_name;
    // TextView tv_jobTitle;
    // TextView tv_jobDesc;
    // // TextView tv_job;
    // }

}
