package com.sundyn.centralizedeval.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.activity.EvalAct;
import com.sundyn.centralizedeval.bean.UserBean;
import com.sundyn.centralizedeval.utils.LocalData;

import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class ListFrag extends Fragment {

    private ListView mListView;
    private List<UserBean> users;
    private ViewHolder mHolder;
    private BitmapUtils mBitmapUtils;
    private MyAdapter mAdapter;
    private int deptPos;
    private Button mBtn_back;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // users = (List<UserBean>) LocalData.organization.getDepartments()
        // .get(deptPos).getUsers();
        // 是否显示部门
        boolean showDEPT = LocalData.organization.isShowDept();
        if (showDEPT) {
            deptPos = (Integer)getArguments().get("deptPos");
            users = (List<UserBean>)LocalData.organization.getDepartments().get(deptPos).getUsers();
        } else {
            users = LocalData.users;
        }
        View view = inflater.inflate(R.layout.fragment_list, null);
        mBitmapUtils = new BitmapUtils(getActivity());
        mBtn_back = (Button)view.findViewById(R.id.btn_back);
        mAdapter = new MyAdapter();
        mListView = (ListView)view.findViewById(R.id.listView);
        mListView.setSelector(new ColorDrawable(Color.parseColor("#CCEDC7")));
        mListView.setAdapter(mAdapter);


        // 创建数据bean
        mListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                Intent intent = new Intent(getActivity(), EvalAct.class);
                intent.putExtra("deptPos", deptPos);
                intent.putExtra("userPos", position);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                getActivity().finish();
            }
        });

        // 更换界面
        changeSkin();

        return view;
    }

    public void changeSkin() {
        if(mAdapter != null){
            mAdapter.notifyDataSetChanged();
        }
    }

    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            mHolder = new ViewHolder();
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.fragment_list_item, null);
                mHolder.image_view = (ImageView)convertView.findViewById(R.id.iv_head);
                mHolder.tv_name = (TextView)convertView.findViewById(R.id.tv_name);
                mHolder.tv_job = (TextView)convertView.findViewById(R.id.tv_job);
                mHolder.tv_jobTitle = (TextView)convertView.findViewById(R.id.tv_jobTitle);
                mHolder.tv_jobDesc = (TextView)convertView.findViewById(R.id.tv_desc);
                convertView.setTag(mHolder);
            } else {
                mHolder = (ViewHolder)convertView.getTag();
            }
            if (users != null && users.size() > 0) {

                String url = users.get(position).getPicPath();
                Log.i("test", url);
                mBitmapUtils.display(mHolder.image_view, url);
                mHolder.tv_name.setText(users.get(position).getRealName());
                mHolder.tv_job.setText(users.get(position).getJob());
                mHolder.tv_jobTitle.setText(users.get(position).getJobTitle());
                mHolder.tv_jobDesc.setText(users.get(position).getJobDesc());

                // 修改字体颜色
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
