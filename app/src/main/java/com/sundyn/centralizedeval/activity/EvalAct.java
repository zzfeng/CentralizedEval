package com.sundyn.centralizedeval.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import android.widget.LinearLayout.LayoutParams;
import com.lidroid.xutils.BitmapUtils;
import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.base.BaseAct;
import com.sundyn.centralizedeval.bean.ButtonBean;
import com.sundyn.centralizedeval.bean.UserBean;
import com.sundyn.centralizedeval.commen.CommenUnit;
import com.sundyn.centralizedeval.utils.ButtonParser;
import com.sundyn.centralizedeval.utils.LocalData;

/**
 * Created by Administrator on 2017/2/21.
 */

public class EvalAct extends BaseAct implements ButtonParser,View.OnClickListener{

    private static String TAG = "EvalAct";
    private Button mBtn_back;
    private Context ctx;
    private String path;
    private BitmapUtils mBitMapUtils;
    private List<ButtonBean> mList = new ArrayList<ButtonBean>();
    private String picPath;
    private ImageView imageView;
    private LinearLayout linearLayout1, linearLayout2;
    private int userPos;
    private int deptPos;
    private UserBean user;
    // 用户名 职称 职位 擅长领域
    private TextView user_name, user_jobTitle, user_job, user_jobDesc, text;
    private LinearLayout.LayoutParams param1, param2;
    private RelativeLayout titleLayout;
    private View vLine;

    /*
     * (non-Javadoc)
     *
     * @see com.sundyn.centralizedeval.BaseAct#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_eval);
        userPos = getIntent().getExtras().getInt("userPos");
        // 是否显示部门
        boolean showDEPT = LocalData.organization.isShowDept();
        if (showDEPT) {
            deptPos = getIntent().getExtras().getInt("deptPos");
            user = LocalData.organization.getDepartments().get(deptPos)
                    .getUsers().get(userPos);
        } else {
            user = LocalData.organization.getAllUserBeans().get(userPos);
        }

        init();
        setLayout();

        skinColorChange();
    }

    private void init() {
        ctx = this;
        // 实例化控件
        mBitMapUtils = new BitmapUtils(ctx);
        titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
        linearLayout1 = (LinearLayout) findViewById(R.id.linearLayout1);
        linearLayout2 = (LinearLayout) findViewById(R.id.linearLayout2);
        text = (TextView) findViewById(R.id.textView2);
        vLine = findViewById(R.id.line);

        // 设置用户信息
        imageView = (ImageView) findViewById(R.id.userImage);

        String picUrl = user.getPicPath();
        mBitMapUtils.display(imageView, picUrl);

        user_name = (TextView) findViewById(R.id.tv_name);
        user_name.setText(user.getRealName());

        user_jobTitle = (TextView) findViewById(R.id.tv_jobTitle);
        user_jobTitle.setText(user.getJobTitle());

        user_job = (TextView) findViewById(R.id.tv_job);
        user_job.setText(user.getJob());

        user_jobDesc = (TextView) findViewById(R.id.tv_jobDesc);
        user_jobDesc.setText(user.getJobDesc());
        user_jobDesc.setMovementMethod(ScrollingMovementMethod.getInstance());

        // 解析xml
        File file = new File(CommenUnit.EVAL_DIR + "button.xml");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            mList = parser(fis);
            if (mList == null) {
                Toast.makeText(EvalAct.this, "请配置button文件", Toast.LENGTH_LONG)
                        .show();
                Intent intent = new Intent();
                intent.setClass(EvalAct.this, DepartmentAct.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in,
                        R.anim.push_left_out);
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        picPath = CommenUnit.EVAL_DIR;
        // 给控件添加点击事件
        // mBtn_feichangmanyi.setOnClickListener(this);
        // mBtn_manyi.setOnClickListener(this);
        // mBtn_yiban.setOnClickListener(this);
        // mBtn_bumanyi.setOnClickListener(this);
        // mBtn_feichangcha.setOnClickListener(this);
        mBtn_back = (Button) findViewById(R.id.btn_back);
    }

    // 给控件设置布局
    private void setLayout() {
        // 给控件设置布局
        // linearLayout1.removeAllViews();
        // linearLayout2.removeAllViews();
        String url = null;
        linearLayout1.removeAllViews();
        linearLayout2.removeAllViews();
        for (int i = 0; i < mList.size(); i++) {
            String y = mList.get(i).getY();
            ButtonBean button = mList.get(i);
            url = button.getImg();
            Button btn = new Button(ctx);
            btn.setId(1000 + i);
            btn.setOnClickListener(this);
            btn.setText(button.getText());
            int size = Integer.parseInt(button.getSize());
            btn.setTextColor(Color.rgb(Integer.parseInt(button.getR()),
                    Integer.parseInt(button.getG()),
                    Integer.parseInt(button.getB())));
            btn.setTextColor(Color.WHITE);
            btn.setTextSize(18);
            btn.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
            btn.setPadding(0, 0, 0, 30);

            if (y.equals("0")) {
                // param1 = (android.widget.LinearLayout.LayoutParams)new
                // LayoutParams(
                // ViewGroup.LayoutParams.WRAP_CONTENT,
                // ViewGroup.LayoutParams.WRAP_CONTENT);
                param1 = (android.widget.LinearLayout.LayoutParams) new LayoutParams(
                        (int) CommenUnit.mContext.getResources().getDimension(
                                R.dimen.eval_icon_width),
                        (int) CommenUnit.mContext.getResources().getDimension(
                                R.dimen.eval_icon_height));
                param1.setMargins(45, 0, 45, 0);

                linearLayout1.addView(btn, param1);
                btn.setBackgroundResource(R.drawable.eval_0);
                //mBitMapUtils.display(btn,getString(R.drawable.eval_0));
                //Log.i("addView", "1");

            } else {
                param2 = (android.widget.LinearLayout.LayoutParams) new LayoutParams(
                        (int) CommenUnit.mContext.getResources().getDimension(
                                R.dimen.eval_icon_width),
                        (int) CommenUnit.mContext.getResources().getDimension(
                                R.dimen.eval_icon_height));
                param2.setMargins(50, 0, 50, 0);
                btn.setBackgroundResource(R.drawable.eval_1);
                //mBitMapUtils.display(btn,getString(R.drawable.eval_1));

                linearLayout2.addView(btn, param2);
                //Log.i("addView", picPath + url);

            }
        }
    }

    // 解析button.xml文件,得到list集合
    @Override
    public List<ButtonBean> parser(InputStream is) throws Exception {
        ArrayList<ButtonBean> list = new ArrayList<ButtonBean>();
        ButtonBean btnBean = null;
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(is, "UTF-8");
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            switch (eventType) {
                case XmlPullParser.START_DOCUMENT:

                    break;
                case XmlPullParser.START_TAG:
                    if (parser.getName().equals("button")) {
                        btnBean = new ButtonBean();
                    } else if (parser.getName().equals("img")) {
                        eventType = parser.next();
                        btnBean.setImg(parser.getText());
                    } else if (parser.getName().equals("key")) {
                        eventType = parser.next();
                        btnBean.setKey(parser.getText());
                    } else if (parser.getName().equals("text")) {
                        eventType = parser.next();
                        btnBean.setText(parser.getText());
                    } else if (parser.getName().equals("size")) {
                        eventType = parser.next();
                        btnBean.setSize(parser.getText());
                    } else if (parser.getName().equals("x")) {
                        eventType = parser.next();
                        btnBean.setX(parser.getText());
                    } else if (parser.getName().equals("y")) {
                        eventType = parser.next();
                        btnBean.setY(parser.getText());
                    } else if (parser.getName().equals("r")) {
                        eventType = parser.next();
                        btnBean.setR(parser.getText());
                    } else if (parser.getName().equals("g")) {
                        eventType = parser.next();
                        btnBean.setG(parser.getText());
                    } else if (parser.getName().equals("b")) {
                        eventType = parser.next();
                        btnBean.setB(parser.getText());
                    } else if (parser.getName().equals("index")) {
                        eventType = parser.next();
                        btnBean.setIndex(parser.getText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    if (parser.getName().equals("button")) {
                        list.add(btnBean);
                        btnBean = null;
                    }
                    break;
            }
            eventType = parser.next();
        }
        return list;
    }

    // 对按钮添加点击事件
    @Override
    public void onClick(View v) {
        int id = v.getId();
        showEvalDlg(id - 1000);
    }

    // 显示评价对话框
    public void showEvalDlg(final int id) {
        EvalDlg evalDlg = new EvalDlg(EvalAct.this);
        evalDlg.setOkBtnClickListener(new OnOkBtnClickListener() {
            @Override
            public void onClick(String msg) {
                ButtonBean bean = mList.get(id);
                final String strAction = LocalData.getAddMetierAction(
                        user.getUserName(), CommenUnit.mID, bean.getKey(), msg)
                        .replace(" ", "%20");
                final String url = LocalData.packUrlAddMetier(strAction);
                // // 提交评价数据
                // HttpUtils http = new HttpUtils();
                // http.send(HttpMethod.GET, url, new RequestCallBack<String>()
                // {
                // @Override
                // public void onFailure(HttpException arg0, String arg1) {
                // LocalData.saveEvalData(strAction);
                // finish();
                // }
                //
                // @Override
                // public void onSuccess(ResponseInfo<String> arg0) {
                // LocalData.uploadOldEvalData();
                // finish();
                // }
                // });

                // 提交评价数据
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        StringBuffer retBuf = new StringBuffer();
                        boolean b = CommenUnit.requestServerByGet(url, retBuf,
                                null);
                        if (!b) {
                            LocalData.saveEvalData(strAction);
                        } else {
                            LocalData.uploadOldEvalData();
                        }
                        // 返回上一个界面
                        EvalAct.this.startActivity(new Intent(EvalAct.this,
                                MainAct.class));
                        overridePendingTransition(R.anim.push_left_in,
                                R.anim.push_left_out);
                        EvalAct.this.finish();
                    }
                }).start();
            }
        });
        evalDlg.show();
        // evalDlg.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);//
        // FLAG_HOMEKEY_DISPATCHED
    }

    // 处理返回按键
    public void back(View view) {
        // Intent intent = new Intent();
        // intent.setClass(EvalAct.this, DepartmentAct.class);
        // startActivity(intent);
        finish();
    }

    @Override
    protected void skinColorChange() {
        titleLayout.setBackgroundColor(LocalData.skinColor);
        user_jobTitle.setTextColor(LocalData.skinColor);
        user_jobDesc.setTextColor(LocalData.skinColor);
        text.setTextColor(LocalData.skinColor);
        vLine.setBackgroundColor(LocalData.skinColor);
    }


}
