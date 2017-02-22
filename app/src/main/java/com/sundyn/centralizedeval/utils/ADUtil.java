package com.sundyn.centralizedeval.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageSwitcher;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.commen.CommenUnit;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by Administrator on 2017/2/21.
 * 该类提供广告播放的工具类，提供读取广告文件，左滑和右滑时切换广告图片
 */

public class ADUtil {
    private static final String TAG = "ADUtil";

    public static int curPos = -1; // 传递的广告当前播放位置

    /**
     * 初始化ads.xml对应的所有广告数据结构
     */
    public static void initImageList(ArrayList<String> typeList, ArrayList<String> urlList,
                                     ArrayList<String> timeList, ArrayList<String> startList, ArrayList<String> endList) {
        DocumentBuilderFactory docBuilderFactory = null;
        DocumentBuilder docBuilder = null;
        Document doc = null;
        typeList.clear();
        urlList.clear();
        timeList.clear();
        startList.clear();
        endList.clear();
        try {
            docBuilderFactory = DocumentBuilderFactory.newInstance();
            docBuilder = docBuilderFactory.newDocumentBuilder();

            // 从配置文件中获取所有图片路径，载入ArrayList中
            FileInputStream fIStream = null;
            try {
                fIStream = new FileInputStream(CommenUnit.ADS_DIR + "ads.xml");
            } catch (Exception e) {
                Log.i(TAG, "M7Main.initImageList : " + e.toString());
                return;
            }
            doc = docBuilder.parse(fIStream);
            Element root = doc.getDocumentElement();
            // NodeList nodeType = root.getElementsByTagName("type");
            // NodeList nodeUrl = root.getElementsByTagName("url");
            // NodeList nodeTime = root.getElementsByTagName("time");
            NodeList nodeType = root.getElementsByTagName("type");
            NodeList nodeUrl = root.getElementsByTagName("keyName");
            NodeList nodeTime = root.getElementsByTagName("playtime");
            NodeList nodeStart = root.getElementsByTagName("startTime");
            NodeList nodeEnd = root.getElementsByTagName("endTime");
            // for (int i = 0; i < nodeType.getLength(); i++) {
            // typeList.add(nodeType.item(i).getTextContent());
            // }
            // for (int i = 0; i < nodeUrl.getLength(); i++) {
            // urlList.add(CommenUnit.ADS_DIR +
            // nodeUrl.item(i).getTextContent());
            // }
            // for (int i = 0; i < nodeTime.getLength(); i++) {
            // timeList.add(nodeTime.item(i).getTextContent());
            // }
            // for (int i = 0; i < nodeStart.getLength(); i++) {
            // startList.add(nodeStart.item(i).getTextContent());
            // }
            // for (int i = 0; i < nodeEnd.getLength(); i++) {
            // endList.add(nodeEnd.item(i).getTextContent());
            // }

            long time = System.currentTimeMillis();
            for (int i = 0; i < nodeType.getLength(); i++) {
                long start = 0;
                long end = 0;
                String strStart = nodeStart.item(i).getTextContent();
                String strEnd = nodeEnd.item(i).getTextContent();
                try {
                    start = Long.parseLong(strStart);
                } catch (Exception e) {
                    start = time;
                    Log.e(TAG, e.toString());
                }
                try {
                    end = Long.parseLong(strEnd);
                } catch (Exception e) {
                    end = time + 365 * 24 * 60 * 60 * 1000;
                    Log.e(TAG, e.toString());
                }
                strEnd = "" + end;
                if (start <= time && time <= end) {
                    typeList.add(nodeType.item(i).getTextContent());
                    urlList.add(CommenUnit.ADS_DIR + nodeUrl.item(i).getTextContent());
                    timeList.add(nodeTime.item(i).getTextContent());
                    startList.add(nodeStart.item(i).getTextContent());
                    endList.add(strEnd);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "initImageList Exception : " + e.toString());
        } finally {
            doc = null;
            docBuilder = null;
            docBuilderFactory = null;
        }
    }

    /**
     * @param size
     * @param checked 当前需要选中的按钮
     */
    @SuppressLint("NewApi")
    public static void initIndicator(int size, int checked, RadioGroup indiRadioGroup, Activity a) {
        if (size == 0) {
            indiRadioGroup.setVisibility(View.GONE);
            return;
        }
        indiRadioGroup.setVisibility(View.VISIBLE);
        indiRadioGroup.removeAllViews();
        // RadioGroup.LayoutParams layoutParams = new
        // RadioGroup.LayoutParams(15, 15);
        // RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams(
        // RadioGroup.LayoutParams.WRAP_CONTENT,
        // RadioGroup.LayoutParams.WRAP_CONTENT);
        // layoutParams.leftMargin = layoutParams.rightMargin =
        // CommenUnit.dipToPx(10);
        RadioGroup.LayoutParams layoutParams = new RadioGroup.LayoutParams((int)CommenUnit.mContext
                .getResources().getDimension(R.dimen.ads_state_width), (int)CommenUnit.mContext
                .getResources().getDimension(R.dimen.ads_state_height));
        layoutParams.leftMargin = layoutParams.rightMargin = (int)CommenUnit.mContext
                .getResources().getDimension(R.dimen.ads_state_margin);
        for (int i = 0; i < size; ++i) {
            RadioButton button = new RadioButton(a);
            // button.setClickable(false);
            button.setId(i);
            button.setChecked(false);
            // button.setPadding(15, 15, 15, 15);
            button.setBackground(a.getResources().getDrawable(R.drawable.stated));
            button.setButtonDrawable(android.R.color.transparent);
            // button.setText("测试");
            // button.setTextSize(10);
            indiRadioGroup.addView(button, layoutParams);

            if (i == checked) {
                button.setChecked(true);
            }
        }
    }

    // 读取文本文件中的内容
    public static List<String> readTxtFile(String strFilePath) {
        String path = strFilePath;
        String[] value1 = new String[1];
        CommenUnit.getCfgValueByName(path, "title", value1);

        String[] value2 = new String[1];
        CommenUnit.getCfgValueByName(path, "text", value2);

        List<String> lst = new ArrayList<String>();

        lst.add(value1[0] + "\n");
        lst.add(value2[0] + "\n");

        return lst;
    }

    /**
     * 设置顺序广告切换特效
     */
    public static void setOrderAnimation(ImageSwitcher mSwitcher) {
        TranslateAnimation animIn = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        animIn.setDuration(500);
        mSwitcher.setInAnimation(animIn);
        TranslateAnimation animOut = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, -1, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        animOut.setDuration(500);
        mSwitcher.setOutAnimation(animOut);
    }

    /**
     * 设置逆序广告切换特效
     */
    public static void setInorderAnimation(ImageSwitcher mSwitcher) {
        TranslateAnimation animIn = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -1,
                Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        animIn.setDuration(500);
        mSwitcher.setInAnimation(animIn);
        TranslateAnimation animOut = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0,
                Animation.RELATIVE_TO_SELF, 0);
        animOut.setDuration(500);
        mSwitcher.setOutAnimation(animOut);
    }
}
