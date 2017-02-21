package com.sundyn.centralizedeval.activity;

import android.content.Context;
import android.content.Intent;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.bean.LoginBean;
import com.sundyn.centralizedeval.commen.CommenUnit;
import com.sundyn.centralizedeval.plugin.FillAdvise;
import com.sundyn.centralizedeval.utils.DeviceUtil;
import com.sundyn.centralizedeval.utils.LocalData;

import java.util.ArrayList;

public class MainAct extends FragmentActivity implements AdapterView.OnItemClickListener {


    private static String TAG = "MainAct";
    // http://192.168.100.181:8080/estime/json_wechat/addWinAndConfig?deptId=13369348&mac=1473B245859C
    // 绑定这个mac设备 已绑定:success/未绑定:fail
    private String URL_BIND = "http://192.168.100.30:8080/estime/json_wechat/addWinAndConfig?";
    // 获取此mac的绑定状态 已绑定:success/未绑定:fail
    private String URL_BINDSTATE = "http://192.168.100.30:8080/estime/json_wechat/getBindMac?mac=";
    //
    private GestureLibrary gestureLibrary;
    private Button mBtn_pingjia;
    private Button mBtn_yijian;
    private TextView tvTitle;
    private RelativeLayout titleLayout;
    private LoginBean loginBean;
    private AlertView alertView, alertView2;
    private List<DetailBean> detailList;
    private ArrayList<String> departList, departIdList;
    private Context ctx;
    private String deviceName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        setContentView(R.layout.act_main);
        ctx = this;
        initData();

        try {
            initView();
            // createJson();
//			skinColorChange();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    private void initData() {
        requestQueue = Volley.newRequestQueue(ctx);
        getBindState();
        loginBean = (LoginBean) getIntent().getSerializableExtra("loginBean");
        detailList = loginBean.getDetail();
        int size = detailList.size();
        departList = new ArrayList<String>();
        departIdList = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            departList.add(detailList.get(i).getDepartment());
            departIdList.add(detailList.get(i).getId());
        }
        deviceName = DeviceUtil.getDeviceName(ctx);
        progressDialog = new CustomProgressDialog(ctx, "绑定中......");
        // String[] arr = (String[])list.toArray(new
        // String[size]);//使用了第二种接口，返回值和参数均为结果
        String[] departments = (String[]) departList.toArray(new String[size]);
        alertView = new AlertView("请选择需要绑定的部门", null, null, null, departments,
                this, AlertView.Style.ActionSheet, this);
        if(departList.size()>1 || (departList.size() == 1 && !flag)){
            alertView.show();
        }
    }//list=1,未绑定flag=false,显示;list>1,显示;list=1,已绑定flag=true,不显示;
    private String warningTitle = "提醒";
    private String warningContent = "你已绑定过设备，是否继续绑定";
    private boolean flag = false;

    private void getBindState() {
        URL_BINDSTATE = URL_BINDSTATE + CommenUnit.mID;
        bindStateRequest = new StringRequest(URL_BINDSTATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        String[] split = response.split(":");
                        if (split[1].contains("success")) {
                            flag = true;
                        } else {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ctx, "网络异常，请稍后重试", Toast.LENGTH_LONG)
                        .show();
                Log.i("VolleyError",
                        "VolleyErrorVolleyErrorVolleyError");
                return;
            }
        });
        requestQueue.add(bindStateRequest);
    }

    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        super.onStart();
        getData();
    }

    private void initView() {

        titleLayout = (RelativeLayout) findViewById(R.id.title_layout);

        tvTitle = (TextView) findViewById(R.id.title_text);
        mBtn_pingjia = (Button) findViewById(R.id.btn_pingjia);
        mBtn_yijian = (Button) findViewById(R.id.btn_yijian);
        //
        mBtn_pingjia.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalData.organization != null && LocalData.users != null
                        && LocalData.users.size() > 0) {
                    Intent intent = new Intent();
                    if (LocalData.organization.isShowDept()) {
                        intent.setClass(MainAct.this, DepartmentAct.class);
                    } else {
                        intent.setClass(MainAct.this, EmployeeAct.class);
                    }
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in,
                            R.anim.push_left_out);
                } else {
                    UIUtils.showToastSafe("没有人员数据！");
                }
            }
        });
        //
        mBtn_yijian.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MainAct.this, FillAdvise.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
            }
        });

        tvTitle.setText(LocalData.organization.getOrgTitle());
        // 加载手势库对象
        gestureLibrary = GestureLibraries.fromRawResource(this, R.raw.gestures);
        // 加载手势库
        gestureLibrary.load();
        GestureOverlayView overlayView = (GestureOverlayView) this
                .findViewById(R.id.gestureOverlayView);
        overlayView.addOnGesturePerformedListener(new GestureListener());
    }

    // private void createJson() {
    // List<UserBean> users = new ArrayList<UserBean>();
    // for (int i = 0; i < 20; i++) {
    // UserBean user = new UserBean();
    // user.setDepartment("某科室" + i);
    // user.setJob("某科室主任");
    // user.setJobTitle("主任医师");
    // user.setJobDesc("从事临床工作10余年从事临床工作10余年从事临床工作10余年从事临床工作10余年从事临床工作10余年");
    // user.setPicUrl("http://sundyn.cn:8080/1234.jpg");
    // user.setPicMd5("AB3234CD2341123EFA");
    // user.setUserName("U" + 1000 + i);
    // user.setRealName("某某某" + i);
    // user.setJobNum("");
    // users.add(user);
    // }
    //
    // List<Department> departments = new ArrayList<Department>();
    // for (int i = 0; i < 20; i++) {
    // Department department = new Department();
    // department.setUsers(users);
    // department.setDepartment("某科室" + i);
    // departments.add(department);
    // }
    //
    // Organization organization = new Organization();
    // organization.setSuccess(true);
    // organization.setShowDept(true);
    // organization.setMessage("成功");
    // organization.setOrgTitle("河南省郑州市中心医院");
    // organization.setDepartments(departments);
    //
    // LocalData.organization = organization;
    //
    // String ss = GsonUtils.bean2Json(organization);
    // FileUtils.saveFile(ss, CommenUnit.DEPT_DIR + "dept");
    // // DebugUtil.d(TAG, ss);
    // }

    private void getData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 评价按键
                String btnUrl = LocalData.packUrlGetGouters(CommenUnit.mID);
                StringBuffer buffer = new StringBuffer();
                CommenUnit.requestServerByGet(btnUrl, buffer, null);
                if (buffer.toString().startsWith("<?xml")) {
                    FileUtils.saveFile(buffer.toString(), CommenUnit.EVAL_DIR
                            + "button.xml");
                }

                // String deptUrl =
                // LoacalData.packUrlGetDeptAndEmp(CommenUnit.mID);
                // Request request = new
                // Request.Builder().tag(TAG).url(deptUrl).build();
                // OkHttpUtil.enqueue(request, new Callback() {
                // @Override
                // public void onResponse(Response arg0) throws IOException {
                // String result = arg0.body().string();
                // if (result != null && result.contains("\"success\":true")) {
                // Organization organization = GsonUtils.json2Bean(result,
                // Organization.class);
                // if (organization != null && organization.isSuccess()) {
                // // 保存 机构人员信息到文件
                // FileUtils.saveFile(result, CommenUnit.DEPT_DIR + "dept");
                // LoacalData.organization = organization;
                // tvTitle.setText(organization.getOrgTitle());
                // List<UserBean> userBeans = new ArrayList<UserBean>();
                //
                // }
                // } else {
                // Log.e(TAG, "getDeptAndEmp => " + result);
                // }
                // }
                //
                // @Override
                // public void onFailure(Request arg0, IOException arg1) {
                // UIUtils.showToastSafe("机构人员信息获取失败，使用本地机构人员信息!");
                //
                // }
                // });
            }
        }).start();

        // 机构人员
        DeptUpdate.getInstance().startDeptUpdate();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.sundyn.centralizedeval.BaseAct#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // OkHttpUtil.cancelRequest(TAG);
    }

    private final class GestureListener implements OnGesturePerformedListener {
        @Override
        public void onGesturePerformed(GestureOverlayView overlay,
                                       Gesture gesture) {
            // 识别用户输入的手势是否存在手势库中
            ArrayList<Prediction> predictions = gestureLibrary
                    .recognize(gesture);
            if (!predictions.isEmpty()) {
                Prediction prediction = predictions.get(0);// 得到匹配的手势
                if (prediction.score > 3) {
                    if ("s".equals(prediction.name)) {
                        startActivity(new Intent(MainAct.this, DeviceAct.class));
                        overridePendingTransition(R.anim.push_left_in,
                                R.anim.push_left_out);
                    } else if ("o".equals(prediction.name)) {

                    }
                }
            }
        }
    }

	/*
	 * (non-Javadoc)
	 *
	 * @see com.sundyn.centralizedeval.BaseAct#skinColorChange()
	 */
//	@Override
//	protected void skinColorChange() {
//		titleLayout.setBackgroundColor(LocalData.skinColor);
//	}

    private CustomProgressDialog progressDialog;
    private RequestQueue requestQueue;
    private StringRequest bindRequest, bindStateRequest;
    private String URL_BINDREQUEST;
    @Override
    public void onItemClick(Object o, final int position) {
        superDialog = (SuperDialog) new SuperDialog.Builder(this).setTitle(warningTitle).setMessage(warningContent)
                .setBackgroundColor(Color.WHITE)
                .setAlpha(1)//0:不透明;1:全透明
                .setNegativeButton("取消", new SuperDialog.OnClickNegativeListener() {

                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "点了取消", Toast.LENGTH_LONG).show();

                    }
                })
                .setPositiveButton("确定", new SuperDialog.OnClickPositiveListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "点了确定", Toast.LENGTH_LONG).show();
                        requestQueue.add(bindRequest);
                    }
                }).build();
        AlertView v = (AlertView) o;
        if(position == 0 && warningTitle.equals(v.getTitle())){
            requestQueue.add(bindRequest);
        }
        // -1点击的是取消按钮
        if (position == -1 && alertView2.isShowing()) {
            alertView2.dismiss();
        } else {
            progressDialog.show();
            URL_BINDREQUEST = URL_BIND + "deptId=" + departIdList.get(position)
                    + "&mac=" + CommenUnit.mID + "&deviceName=" + deviceName;
            bindRequest = new StringRequest(URL_BINDREQUEST,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            String[] split = response.split(":");
                            if (split[1].contains("success")) {
                                LocalData.readDepartment();
                                Toast.makeText(
                                        ctx,
                                        "您已成功绑定" + departList.get(position)
                                                + "部门", Toast.LENGTH_LONG)
                                        .show();

                                return;
                            } else {
                                Toast.makeText(ctx, "绑定失败，请重新绑定",
                                        Toast.LENGTH_LONG).show();
                                if(!alertView.isShowing()){
                                    alertView.show();
                                }
                                return;
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    Toast.makeText(ctx, "网络异常，请稍后重试", Toast.LENGTH_LONG)
                            .show();
                    Log.i("VolleyError",
                            "VolleyErrorVolleyErrorVolleyError");
                    if(!alertView.isShowing()){
                        alertView.show();
                    }
                    return;
                }
            });
            // 获取绑定状态state返回值，（已绑定:success,显示alertView2/未绑定:fail,不显示alertView2）判断是否显示alertView2是否显示
            if (flag) {
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

            } else {
                requestQueue.add(bindRequest);
            }
        }

    }
    private SuperDialog superDialog;
    @Override
    public void onDismiss(Object o) {
        // 确定取消alertView消失时调用
    }

}
