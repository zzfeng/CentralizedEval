package com.sundyn.centralizedeval.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.bean.Question;
import com.sundyn.centralizedeval.bean.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class QuestionWidget extends LinearLayout implements android.view.View.OnClickListener{
    private static String TAG = "QuestionWidget";

    private Context context;
    private TextView tvIndex;
    private TextView tvQuestion;
    private GridLayout gridLayout;
    private Question question;

    private List<View> btnList = new ArrayList<View>();

    /**
     * @param context
     */
    public QuestionWidget(Context context) {
        super(context);
        this.context = context;

        try {
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param context
     * @param attrs
     */
    public QuestionWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        try {
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initView() throws Exception {
        LayoutInflater.from(context).inflate(R.layout.advise_item, this);
        tvIndex = (TextView)findViewById(R.id.idIndex);
        tvQuestion = (TextView)findViewById(R.id.idQuestion);
        gridLayout = (GridLayout)findViewById(R.id.advise_item_gridlayout);
    }

    /**
     * 初始化数据和VIEW
     *
     * @param index 答案
     * @param question 问题
     * @return void
     */
    public void setData(int index, Question question) {
        if (question == null)
            return;
        this.question = question;
        btnList.clear();
        gridLayout.removeAllViews();
        tvIndex.setText(index + 1 + "");
        tvQuestion.setText(question.getTitle());
        if ("单选题".equals(question.getType())) {
            List<Result> results = question.getResults();
            for (int i = 0; i < results.size(); i++) {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.advise_radio_button, null);
                RadioButton rdBtn = (RadioButton)view.findViewById(R.id.idRadioButton);
                Result result = results.get(i);
                rdBtn.setTag(result);
                rdBtn.setText(result.getResult());
                rdBtn.setOnClickListener(this);
                gridLayout.addView(rdBtn, GridLayout.LayoutParams.WRAP_CONTENT,
                        GridLayout.LayoutParams.WRAP_CONTENT);
                btnList.add(rdBtn);

                if (results.get(i).isSelect()) {
                    setRadioChecked(rdBtn);
                }
            }
        } else if ("多选题".equals(question.getType())) {
            List<Result> results = question.getResults();
            for (int i = 0; i < results.size(); i++) {
                View view = LayoutInflater.from(context).inflate(R.layout.advise_check_box, null);
                CheckBox checkBox = (CheckBox)view.findViewById(R.id.idCheckBox);
                Result result = results.get(i);
                checkBox.setTag(result);
                checkBox.setText(result.getResult());
                checkBox.setOnClickListener(this);
                gridLayout.addView(checkBox, GridLayout.LayoutParams.WRAP_CONTENT,
                        GridLayout.LayoutParams.WRAP_CONTENT);
                if (result.isSelect()) {
                    checkBox.setChecked(true);
                }
            }

        } else if ("评级题".equals(question.getType())) {
            View view = LayoutInflater.from(context).inflate(R.layout.advise_ratingbar, null);
            RatingBar ratingBar = (RatingBar)view.findViewById(R.id.advise_ratingbar);
            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    setRatingBarRating(ratingBar, (int)rating);
                }
            });
            gridLayout.addView(ratingBar, GridLayout.LayoutParams.WRAP_CONTENT,
                    GridLayout.LayoutParams.WRAP_CONTENT);
            List<Result> results = question.getResults();
            boolean haveResult = false;
            for (int i = 0; i < results.size(); i++) {
                Result result = results.get(i);
                if (result.isSelect()) {
                    int star = Integer.parseInt(result.getResult());
                    ratingBar.setRating(star);
                    ratingBar.setTag(question);
                    haveResult = true;
                    break;
                }
            }
            if (!haveResult) {
                setRatingBarRating(ratingBar, 1);
            }
        }
    }

    /**
     * 刷新数据源，改变选中项
     *
     * @param view
     * @return void
     */
    private void setRadioChecked(View view) {
        for (int i = 0; i < btnList.size(); i++) {
            RadioButton rdBtn = (RadioButton)btnList.get(i);
            Result result = (Result)rdBtn.getTag();
            if (rdBtn == view) {
                rdBtn.setChecked(true);
                result.setSelect(true);
            } else {
                rdBtn.setChecked(false);
                result.setSelect(false);
            }
        }
    }

    /**
     * 刷新数据源，改变选中项
     *
     * @param view
     * @return void
     */
    private void setBoxChecked(View view) {
        CheckBox checkBox = (CheckBox)view;
        Result result = (Result)checkBox.getTag();
        if (checkBox.isChecked()) {
            result.setSelect(true);
        } else {
            result.setSelect(false);
        }
    }

    /**
     * 设置 RatingBar
     *
     * @param view
     * @param rating
     */
    private void setRatingBarRating(View view, int rating) {
        RatingBar ratingBar = (RatingBar)view;
        List<Result> results = question.getResults();
        if (results != null) {
            for (int i = 0; i < results.size(); i++) {
                Result result = results.get(i);
                if (result.getResult().equals(rating + "")) {
                    result.setSelect(true);
                } else {
                    result.setSelect(false);
                }
            }
        }
    }

    /**
     * 获取答案
     *
     * @param @return
     * @return int 答案值
     */
    public int getSelectValue() {
        int value = 0;
        // for (int i = 0; i < btnList.size(); i++) {
        // RadioButton rdBtn = btnList.get(i);
        // if (rdBtn.isChecked()) {
        // value = (Integer)rdBtn.getTag();
        // break;
        // }
        // }
        return value;
    }

    @Override
    public void onClick(View v) {
        if (v instanceof RadioButton)
            setRadioChecked(v);
        if (v instanceof CheckBox)
            setBoxChecked(v);
    }
}
