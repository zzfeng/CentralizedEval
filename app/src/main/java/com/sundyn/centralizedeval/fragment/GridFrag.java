package com.sundyn.centralizedeval.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.activity.EvalAct;
import com.sundyn.centralizedeval.bean.UserBean;
import com.sundyn.centralizedeval.utils.LocalData;

import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class GridFrag extends Fragment {

    private GridView mGridView;
    private List<UserBean> users;
    private BitmapUtils mBitmapUtils;
    private int deptPos;
    private MyAdapter mAdapter;
    private BitmapDisplayConfig displayConfig;
    private Config config;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_grid, null);
        mGridView = (GridView) view.findViewById(R.id.gridView_depart);
        mGridView.setSelector(new ColorDrawable(Color.parseColor("#CCEDC7")));
        // 是否显示部门
        boolean showDEPT = LocalData.organization.isShowDept();
        if (showDEPT) {
            deptPos = (Integer) getArguments().get("deptPos");
            users = (List<UserBean>) LocalData.organization.getDepartments()
                    .get(deptPos).getUsers();
        } else {
            users = LocalData.users;
        }
        displayConfig = new BitmapDisplayConfig();

//		displayConfig.setBitmapConfig(bitmapConfig);
        mBitmapUtils = new BitmapUtils(getActivity());
        mAdapter = new MyAdapter();
        mGridView.setAdapter(mAdapter);

        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(getActivity(), EvalAct.class);
                intent.putExtra("userPos", position);
                intent.putExtra("deptPos", deptPos);
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

    // 为gridview设置适配器
    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder mHolder = null;
            if (convertView == null) {
                mHolder = new ViewHolder();
                convertView = View.inflate(getActivity(),
                        R.layout.fragment_grid_item, null);
                mHolder.image_view = (ImageView) convertView
                        .findViewById(R.id.image_url);
                mHolder.tv_name = (TextView) convertView
                        .findViewById(R.id.tv_name);
                mHolder.tv_jobTitle = (TextView) convertView
                        .findViewById(R.id.tv_jobTitle);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder) convertView.getTag();
            }
            if (users != null && users.size() > 0) {
                String url = users.get(position).getPicPath();
                mBitmapUtils.display(mHolder.image_view, url);
                mHolder.tv_name.setText(users.get(position).getRealName());
                mHolder.tv_jobTitle.setText(users.get(position).getJobTitle());

                mHolder.tv_jobTitle.setTextColor(LocalData.skinColor);
            }
            return convertView;
        }
    }

    class ViewHolder {
        ImageView image_view;
        TextView tv_name;
        TextView tv_jobTitle;
        TextView tv_job;
        TextView tv_jobDesc;
    }

}
