package com.sundyn.centralizedeval.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.bean.Question;
import com.sundyn.centralizedeval.bean.Questionnaire;
import com.sundyn.centralizedeval.bean.Result;
import com.sundyn.centralizedeval.commen.CommenUnit;
import com.sundyn.centralizedeval.views.QuestionWidget;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class FillAdviseAct extends Activity{

    private final String TAG = "FillAdvise";
    private final int MAX_RADIO = 9;
    private BroadcastReceiver mReceiver;
    // private Button mBtnRet; // 返回按键
    private GestureDetector mGd; // 单击手势，显示返回键
    private boolean mIsBtnShow = true; // 显示返回键
    // ResultData mData = new ResultData();
    private List<Integer> mAnswer = new ArrayList<Integer>();
    private List<Boolean> rdBtnList = new ArrayList<Boolean>();
    private Questionnaire questionnaire = new Questionnaire();

    private Handler mBtnHand = new Handler() { // 隐藏返回按键
        @Override
        public void handleMessage(Message msg) {
            TranslateAnimation taOut = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                    Animation.RELATIVE_TO_SELF, 1);
            taOut.setStartOffset(3000);
            taOut.setDuration(500);
            taOut.setFillAfter(true);
            taOut.setAnimationListener(new AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    mIsBtnShow = false;
                    // mBtnRet.setClickable(false);
                }
            });
            // mBtnRet.startAnimation(taOut);
            super.handleMessage(msg);
        }

    };
    private boolean booleanExtra;

    /** 问卷调查adapter **/
    private class AdviseAdapter extends BaseAdapter {
        private LayoutInflater mInflater;
        private Context context;
        private View[] views;
        private ArrayList<String> mQuestionArray = new ArrayList<String>();
        private ArrayList<String[]> mAnswerArray = new ArrayList<String[]>();

        public AdviseAdapter(Context context) {
            this.context = context;
            mInflater = LayoutInflater.from(context);
            SharedPreferences sp = context.getApplicationContext().getSharedPreferences("DemoItem",
                    Context.MODE_PRIVATE);
            String adviceFileName = sp.getString("advise", "advise.xml");
            File adviseXML = new File(CommenUnit.ADVISE_DIR + adviceFileName);
            if (adviseXML.exists()) {
                // try {
                // readXML(context, CommenUnit.WORK_DIR + adviceFileName);
                // } catch (IOException e) {
                // Log.e(TAG, e.toString());
                // } catch (XmlPullParserException e) {
                // Log.e(TAG, e.toString());
                // }
                questionnaire = FillAdviseAct.this.readXML(adviseXML.getAbsolutePath());
            } else {
                // try {
                // // 打开advise.db数据库
                // DatabaseManager db = new DatabaseManager(FillAdvise.this);
                // db.openOrCreate(CommenUnit.WORK_DIR + "advise.db");
                // // 载入所有问题及答案
                // SQLiteDatabase sql = db.getDatabase();
                // Cursor curQues, curAnsw;
                // curQues = sql.query("question", new String[] {
                // "id", "content"
                // }, null, null, null, null, null);
                // if (curQues.moveToFirst()) {
                // String[] label = new String[MAX_RADIO];
                // for (int i = 0; i < MAX_RADIO; i++) {
                // label[i] = (char)('A' + i) + "、";
                // }
                // int i = 1;
                // do {
                // int index = curQues.getColumnIndex("content");
                // mQuestionArray.add(i++ + "." + curQues.getString(index));
                // index = curQues.getColumnIndex("id");
                // String fatherId = curQues.getString(index);
                // // 根据fatherid获取答案选项
                // curAnsw = sql.query("answer", new String[] {
                // "content"
                // }, "fatherid=?", new String[] {
                // fatherId
                // }, null, null, null);
                // if (curAnsw.moveToFirst()) {
                // int rowCount = curAnsw.getCount();
                // String[] row = new String[rowCount];
                // int j = 0;
                //
                // do {
                // row[j] = label[j] + curAnsw.getString(0);
                // j++;
                // } while (curAnsw.moveToNext());
                // mAnswerArray.add(row);
                // curAnsw.close();
                // }
                // curAnsw.close();
                // } while (curQues.moveToNext());
                // }
                // curQues.close();
                // db.close();
                //
                // } catch (Exception e) {
                // Log.e(TAG, "FillAdvise:数据库advise.db异常");
                // }
            }
            views = new View[mQuestionArray.size()];
        }

        @Override
        public int getCount() {
            // return mQuestionArray.size();
            return questionnaire.getQuestions().size();
        }

        @Override
        public Object getItem(int position) {
            // return views[position];
            return questionnaire.getQuestions().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Question question = questionnaire.getQuestions().get(position);

            QuestionWidget questionWidget = null;
            if (convertView == null) {
                questionWidget = new QuestionWidget(context);
                convertView = questionWidget;
            } else {
                questionWidget = (QuestionWidget)convertView;
            }
            questionWidget.setData(position, question);

            return convertView;
        }

        // /**
        // * 获取答案
        // *
        // * @param @return
        // * @return List<Integer>
        // */
        // public List<Integer> getAnswer() {
        // List<Integer> answers = new ArrayList<Integer>();
        // if (views != null && views.length > 0) {
        // for (int i = 0; i < views.length; i++) {
        // int answer = 0;
        // if (views[i] != null) {// 如果views[i]=null，view未刷新
        // answer = ((QuestionWidget)views[i]).getSelectValue();
        // }
        // answers.add(answer);
        // }
        // }
        // return answers;
        // }
    }

    public Questionnaire readXML(String xmlPath) {
        Questionnaire questionnaire = new Questionnaire();
        try {
            String strData = "";
            ArrayList<Question> questions = null;
            ArrayList<Result> results = null;
            Question question = null;
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser parser = factory.newPullParser();
            FileInputStream fIStream = new FileInputStream(xmlPath);
            parser.setInput(fIStream, "UTF-8");
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        questionnaire = new Questionnaire();
                        break;
                    case XmlPullParser.START_TAG:
                        if ("Questionnaire".equals(parser.getName())) {
                            System.out.println(parser.getAttributeValue(0));
                            questionnaire.setId(parser.getAttributeValue(0));
                        }
                        if ("name".equals(parser.getName())) {
                            questionnaire.setName(parser.nextText());
                        }
                        if ("startTime".equals(parser.getName())) {
                            strData = parser.nextText().trim();
                            questionnaire.setStartTime(Long.parseLong(strData));
                        }
                        if ("endTime".equals(parser.getName())) {
                            strData = parser.nextText().trim();
                            if (strData.isEmpty()) {
                                questionnaire.setEndTime(System.currentTimeMillis() + 365 * 24 * 60
                                        * 60 * 1000);
                            } else {
                                questionnaire.setEndTime(Long.parseLong(strData));
                            }
                        }
                        if ("content".equals(parser.getName())) {
                            questions = new ArrayList<Question>();
                            questionnaire.setQuestions(questions);
                        }
                        if ("question".equals(parser.getName())) {
                            question = new Question();
                            question.setId(parser.getAttributeValue(0));
                            questions.add(question);
                        }
                        if ("title".equals(parser.getName())) {
                            question.setTitle(parser.nextText());
                        }
                        if ("type".equals(parser.getName())) {
                            question.setType(parser.nextText());
                        }
                        if ("results".equals(parser.getName())) {
                            results = new ArrayList<Result>();
                            question.setResults(results);
                        }
                        if ("result".equals(parser.getName())) {
                            Result result = new Result();
                            result.setId(parser.getAttributeValue(0));
                            result.setResult(parser.nextText());
                            results.add(result);
                        }
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                eventType = parser.next();
            }
            fIStream.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        Log.d(TAG, questionnaire.toString());
        return questionnaire;
    }

    protected void skinColorChange() {
        String colorString = getApplicationContext().getSharedPreferences("setting",
                Context.MODE_PRIVATE).getString("skinColor", "#FF0893A8");
        int color = Color.parseColor(colorString);
        RelativeLayout relativeLayout = (RelativeLayout)findViewById(R.id.title_layout);
        TextView tvAdvise = (TextView)findViewById(R.id.tv_advise);
        View line = findViewById(R.id.line);
        relativeLayout.setBackgroundColor(color);
        tvAdvise.setTextColor(color);
        line.setBackgroundColor(color);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fill_advise);
        booleanExtra = getIntent().getBooleanExtra("hide", false);
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.main_layout);

        ListView lv = (ListView)findViewById(R.id.idListView);
        final AdviseAdapter la = new AdviseAdapter(this);
        lv.setAdapter(la);

        Button btn = (Button)findViewById(R.id.idAdviseCommit);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // mAnswer = la.getAnswer();// 获取答案
                // 线程上传结果
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadAdvise();
                    }
                }).start();
                FillAdviseAct.this.finish();
            }
        });

        Button btnFinish = (Button)findViewById(R.id.btn_finish);
        btnFinish.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FillAdviseAct.this.finish();
            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action.equals("dismiss")) {
                    FillAdviseAct.this.finish();
                } else if (action.equals("skin_change")) {

                }
            }
        };

        // 注册广播接收器
        IntentFilter filter = new IntentFilter();
        filter.addAction("dismiss");
        filter.addAction("skin_change");
        registerReceiver(mReceiver, filter);

        // 添加到界面管理器
        // CommenUnit.activityManager.put("FillAdvise", FillAdvise.this);
        CommenUnit.activityManager.put(this + "", this);

        // 初始化返回键
        // mBtnRet = CommenUnit.addReturnBtn(mBtnRet, layout, this);

        if (booleanExtra) {
            // mBtnRet.setVisibility(View.GONE);
        }
        // 延时隐藏返回键
        mBtnHand.sendEmptyMessage(0);

        // 单击手势
        mGd = new GestureDetector(this, new OnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onShowPress(MotionEvent e) {
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
        });

        skinColorChange();
    }

    /*
     * (non-Javadoc)
     * @see android.app.Activity#onStart()
     */
    @Override
    protected void onStart() {
        // 沉浸模式
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        super.onStart();
    }

    /**
     * 上传调查结果
     */
    private void uploadAdvise() {
        // 如果网络连接
        if ("1".equals(CommenUnit.m8Config.type)) {
            // 合成服务器地址
            StringBuffer url = new StringBuffer(CommenUnit.m8Config.getServerAddr()
                    + "estime/android_enquest/postResults?mac=").append(CommenUnit.mID)
                    .append("&qrid=").append(questionnaire.getId()).append("&date=")
                    .append(new Date().getTime()).append("&results=");

            List<Question> questions = questionnaire.getQuestions();
            for (int i = 0; i < questions.size(); i++) {
                Question question = questions.get(i);
                List<Result> results = question.getResults();
                for (int j = 0; j < results.size(); j++) {
                    Result result = results.get(j);
                    if (result.isSelect()) {
                        url.append(question.getId()).append("%7C").append(result.getId())
                                .append(",");
                    }
                }
            }

            if (url.lastIndexOf(",") == url.length() - 1)
                url.deleteCharAt(url.length() - 1);
            StringBuffer sb = new StringBuffer();
            CommenUnit.requestServerByGet(url.toString(), sb, null);
            // Log.d(TAG, "问卷提交结果：" + sb.toString());
        } else {// USB连接
            Intent intent = new Intent("advise_upload");
            StringBuffer data = new StringBuffer();
            for (Integer i : mAnswer) {
                data.append(i.intValue());
            }
            intent.putExtra("advise", data.toString());
            sendBroadcast(intent);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if (booleanExtra) {
            if (mGd.onTouchEvent(ev)) {
                mBtnHand.removeMessages(100);
                mBtnHand.sendEmptyMessageDelayed(100, 60000);
                // DebugUtil.w(TAG, "dispatchTouchEvent");
            }
            return super.dispatchTouchEvent(ev);
        }

        // 点击显示返回键
        if (mGd.onTouchEvent(ev)) {
            if (!mIsBtnShow) {
                TranslateAnimation taIn = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                        Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                        Animation.RELATIVE_TO_SELF, 0);
                taIn.setDuration(500);
                // mBtnRet.startAnimation(taIn);
                mIsBtnShow = true;
                // mBtnRet.setClickable(true);
                taIn.setAnimationListener(new AnimationListener() {

                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // 延时隐藏返回键
                        mBtnHand.sendEmptyMessage(0);
                    }
                });
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        // CommenUnit.activityManager.remove("FillAdvise");
        CommenUnit.activityManager.remove(this + "");
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

}
