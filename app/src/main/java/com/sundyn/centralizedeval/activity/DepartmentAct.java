package com.sundyn.centralizedeval.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import com.lidroid.xutils.BitmapUtils;
import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.base.BaseAct;
import com.sundyn.centralizedeval.bean.Department;
import com.sundyn.centralizedeval.bean.UserBean;
import com.sundyn.centralizedeval.utils.LocalData;

import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class DepartmentAct extends BaseAct {

    private static String TAG = "DepartmentAct";
    private GridView mGridView;
    private List<Department> mDepart_List;
    private List<UserBean> mUser_List;
    private UserBean user;
    private List<Department> departments;
    private Context ctx;
    private BitmapUtils mBitMapUtils;
    private Button btn_close;
    private RelativeLayout titleLayout;

    /*
     * (non-Javadoc)
     * @see com.sundyn.centralizedeval.BaseAct#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_dept);
        ctx = this;
        mBitMapUtils = new BitmapUtils(ctx);
        departments = LocalData.organization.getDepartments();
        // 初始化控件
        init();
        // 修改皮肤
        skinColorChange();
    }

    private void init() {
        // 初始化控件，GridView
        titleLayout = (RelativeLayout)findViewById(R.id.title_layout);
        btn_close = (Button)findViewById(R.id.close);
        mGridView = (GridView)findViewById(R.id.gridView);
        mGridView.setSelector(new ColorDrawable(Color.parseColor("#CCEDC7")));
        mGridView.setAdapter(new MyAdapter());
        mGridView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                Intent intent = new Intent();
                // 将条目位置信息绑定到Intent传递到下一个界面
                intent.putExtra("deptPos", position);
                // 跳转到显示员工界面
                intent.setClass(DepartmentAct.this, EmployeeAct.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
            }
        });
    }

    // 自定义适配器
    class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return departments.size();
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
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = View.inflate(ctx, R.layout.depart_item, null);
                holder.tvDept = (TextView)convertView.findViewById(R.id.tvDept);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder)convertView.getTag();
            }
            // 展示部门图片，url是图片的地址
            String url = "";
            if (departments != null && departments.size() > 0) {

                Department depart = departments.get(position);
                holder.tvDept.setPadding(5, 0, 5, 0);
                holder.tvDept.setText(depart.getDepartment());

            }
            return convertView;
        }

    }

    // holder类
    class ViewHolder {
        TextView tvDept;
    }

    //
    public void close(View v) {
        Intent intent = new Intent();
        intent.setClass(DepartmentAct.this, MainAct.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in,
                R.anim.push_left_out);
        finish();
    }

    @Override
    protected void skinColorChange() {
        // TODO Auto-generated method stub
        titleLayout.setBackgroundColor(LocalData.skinColor);
    }


}
