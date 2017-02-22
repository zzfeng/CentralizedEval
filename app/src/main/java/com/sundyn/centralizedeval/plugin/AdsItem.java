package com.sundyn.centralizedeval.plugin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnTouchListener;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView.ScaleType;
import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.activity.FillAdsAct;
import com.sundyn.centralizedeval.commen.CommenUnit;
import com.sundyn.centralizedeval.utils.ADUtil;
import com.sundyn.centralizedeval.utils.AdsUpdate;
import com.sundyn.centralizedeval.utils.PanoCache;
import com.sundyn.centralizedeval.views.AdsVideo;
import com.sundyn.centralizedeval.views.VerticalScrollTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/2/21.
 */

public class AdsItem extends Fragment implements ViewSwitcher.ViewFactory {

    private final String TAG = "AdsItem";
    private ImageSwitcher mSwitcher; // 用于控制ImageView中的广告图片动画切换
    private AdsVideo mVideo;
    private VerticalScrollTextView mReader;
    private boolean mIsVideo = false; // 当前媒体类型是否是视频
    // private boolean isRefresh;//是否刷新广告媒体
    private int mPlayPosition; // 当前播放位置
    private ArrayList<String> typeList; // 广告媒体类型
    private ArrayList<String> urlList; // 广告媒体路径
    private ArrayList<String> timeList; // 广告媒体播放时间
    private ArrayList<String> startList;
    private ArrayList<String> endList;
    private List<String> lst;// 文本广告媒体内容
    private int indexOfList;
    private Timer timer;
    private long mStartTime;
    private long mPauseTime;
    private int mAdTime;
    private BroadcastReceiver mReceiver; // 接收命令广播

    class AdTimerTask extends TimerTask {
        @Override
        public void run() {
            ADUtil.setOrderAnimation(mSwitcher);
            handler.sendEmptyMessage(0);
        }
    }

    private RadioGroup indiRadioGroup; // 显示指示器的布局.since1.3.3

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startTimer(indexOfList - 1);
        }

    };

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            mReader.threadPause();// 暂停线程

            // 列表为空则返回
            if (urlList.isEmpty()) {
                Log.i(TAG, "handleMessage : Ad list is empty");
                mSwitcher.setImageDrawable(null);
                mIsVideo = false;
                if (mVideo.isPlaying()) {
                    mVideo.stopPlayback();
                }
                mVideo.setVisibility(View.INVISIBLE);
                indiRadioGroup.setVisibility(View.GONE);
                return;
            }

            indiRadioGroup.setVisibility(View.VISIBLE);
            if (ADUtil.curPos != -1) {
                indexOfList = ADUtil.curPos - 1;
                ADUtil.curPos = -1;
            } else {
                // 列表循环
                if (indexOfList >= urlList.size()) {
                    indexOfList = 0;
                } else if (indexOfList < 0) {
                    indexOfList = urlList.size() - 1;
                }
            }

            String strTemp = typeList.get(indexOfList);
            indiRadioGroup.getChildAt(indexOfList).performClick();
            // Log.d("sssssss", a+"dddd");
            if (strTemp.toLowerCase().equalsIgnoreCase("img") || strTemp.equalsIgnoreCase("IMAGE")) {
                // 如果是图片
                mIsVideo = false;
                if (mVideo.isPlaying()) {
                    mVideo.stopPlayback();
                }
                mVideo.setVisibility(View.INVISIBLE);
                mReader.setVisibility(View.INVISIBLE);
                mSwitcher.setVisibility(View.VISIBLE);
                // 调整各控件z轴顺序
                mSwitcher.bringToFront();

                Bitmap b = PanoCache.getBitmapFromMem(indexOfList + "");
                if (b == null) {
                    b = CommenUnit.getLoacalBitmap(urlList.get(indexOfList), true,
                            mSwitcher.getLayoutParams().width);

                    if (b != null) {
                        PanoCache.addBitmapToMem(indexOfList + "", b);
                        mSwitcher.setImageDrawable(new BitmapDrawable(a.getResources(), b));
                    } else {
                        Log.e(TAG, "handleMessage : Not find imagefile " + urlList.get(indexOfList));
                    }

                } else {
                    mSwitcher.setImageDrawable(new BitmapDrawable(a.getResources(), b));
                }

                mAdTime = 5000;
                try {
                    mAdTime = Integer.parseInt(timeList.get(indexOfList++));
                    timer.schedule(new AdTimerTask(), mAdTime);
                } catch (Exception e) {
                    Log.i(TAG, "计时器重启失败" + e);
                }
                mStartTime = System.currentTimeMillis();

            } else if (strTemp.equalsIgnoreCase("video")) {
                mSwitcher.setImageResource(android.R.color.black);
                // 如果是视频
                mVideo.setVisibility(View.VISIBLE);
                mReader.setVisibility(View.INVISIBLE);
                mVideo.bringToFront();
                mVideo.setVideoPath(urlList.get(indexOfList));
                mVideo.start();
                mIsVideo = true;
                // 视频没有更新mMemBmp[0]，所以清除mMemBmp[1]
                // mMemBmp[1] = null;
                // 跳过本次计时器，视屏播放完触发下个广告
                indexOfList++;
            } else if (strTemp.equalsIgnoreCase("text")) {
                mIsVideo = false;
                if (mVideo.isPlaying()) {
                    mVideo.stopPlayback();
                }
                mVideo.setVisibility(View.INVISIBLE);
                mSwitcher.setVisibility(View.INVISIBLE);
                mReader.setVisibility(View.VISIBLE);
                // mReader.bringToFront();
                if (lst == null || lst.size() == 0) {
                    lst = ADUtil.readTxtFile(urlList.get(indexOfList));
                }
                // 给View传递数据
                mReader.setList(lst);
                // mReader.setContent(content);
                // 更新View
                mReader.updateUI(mHandler);
                // mReader.setText(ReadTxtFile(urlList.get(indexOfList)));
                // mReader.setMovementMethod(ScrollingMovementMethod.getInstance());
                indexOfList++;

            }

            super.handleMessage(msg);
        }
    };

    /**
     * 开启定时器
     */
    public void startTimer(int indexOfList) {
        mAdTime = 5000;
        try {
            mAdTime = Integer.parseInt(timeList.get(indexOfList));
            timer.schedule(new AdTimerTask(), mAdTime);
        } catch (Exception e) {
            Log.i(TAG, "计时器重启失败" + e);
        }
        mStartTime = System.currentTimeMillis();
    }

    /**
     * 初始化ads.xml对应的所有广告数据结构
     */

    private void reInitImageList() {
        // 重载缓存资源
        // mBmpMap = new HashMap<Integer, WeakReference<BitmapDrawable>>(); //
        // 广告图片弱引用管理器
        PanoCache.removeAll();
        lst = null;
        timer.cancel();
        ADUtil.initImageList(typeList, urlList, timeList, startList, endList);
        ADUtil.initIndicator(typeList.size(), 0, indiRadioGroup, getActivity());// 重新初始化指示器
        timer = new Timer();
        timer.schedule(new AdTimerTask(), 500);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View parent = inflater.inflate(R.layout.item_ads, null);
        indiRadioGroup = (RadioGroup)parent.findViewById(R.id.indicator);
        //initItem();

        // 初始化广告列表
        typeList = new ArrayList<String>();
        urlList = new ArrayList<String>();
        timeList = new ArrayList<String>();
        startList = new ArrayList<String>();
        endList = new ArrayList<String>();
        indexOfList = 0;
        ADUtil.initImageList(typeList, urlList, timeList, startList, endList);
        ADUtil.initIndicator(typeList.size(), 0, indiRadioGroup, getActivity());
        // 获取广告控件
        // mSwitcher = new ImageSwitcher(this);
        mSwitcher = (ImageSwitcher)parent.findViewById(R.id.idAdsSwicher);
        mSwitcher.setFactory(this);
        mVideo = (AdsVideo)parent.findViewById(R.id.idAdsPlayer);
        mReader = (VerticalScrollTextView)parent.findViewById(R.id.idAdsReader);

        mVideo.setOnErrorListener(new OnErrorListener() {

            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mVideo.stopPlayback();
                ADUtil.setOrderAnimation(mSwitcher);
                handler.sendEmptyMessage(0);

                return true;
            }
        });
        mVideo.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                ADUtil.setOrderAnimation(mSwitcher);
                handler.sendEmptyMessage(0);
            }
        });

        final GestureDetector gest = new GestureDetector(a, new OnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
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
                if (e1.getX() > e2.getX()) {
                    Log.i(TAG, "向左滑，下一张");
                    ADUtil.setOrderAnimation(mSwitcher);
                } else {
                    Log.i(TAG, "向右滑，上一张");
                    ADUtil.setInorderAnimation(mSwitcher);
                    indexOfList -= 2;
                }
                if (timer != null) {
                    timer.cancel();
                    timer = new Timer();
                }
                handler.sendEmptyMessage(0);
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }
        });

        // Item滑动事件响应
        parent.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (gest.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        });

        // Item单机事件响应
        parent.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (urlList.size() == 0) {
                    Toast.makeText(a, "广告媒体资源为空,请先添加资源", Toast.LENGTH_SHORT).show();
                    return;
                }
                ADUtil.curPos = indexOfList;
                Intent intent = new Intent(a, FillAdsAct.class);
                // clickAnim(intent);

            }
        });

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                reInitImageList();
            }
        };
        a.registerReceiver(mReceiver, new IntentFilter("refreshads"));

        AdsUpdate.getInstance().startAdsUpdate();

        return parent;
    }

    private Activity a;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        a = activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        long time = mStartTime + mAdTime - mPauseTime;
        if (time < 0) {
            time = 0;
        }

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        timer = new Timer();

        if (ADUtil.curPos == -1) {

            if (mIsVideo) {
                mVideo.setVideoPath(urlList.get(indexOfList - 1));
                mVideo.start();
                mVideo.seekTo(mPlayPosition);
            } else {
                timer.schedule(new AdTimerTask(), time);
            }
        } else {
            if (mIsVideo) {
                mVideo.setVideoPath(urlList.get(indexOfList - 1));
                mVideo.start();
                mVideo.seekTo(mPlayPosition);
            }
            // setOrderAnimation();
            handler.sendEmptyMessage(0);
            timer.schedule(new AdTimerTask(), 3000);
        }

    }

    @Override
    public void onPause() {
        mPauseTime = System.currentTimeMillis();
        if (timer != null) {
            timer.cancel();
        }
        if (mIsVideo) {
            mPlayPosition = mVideo.getCurrentPosition();
            mVideo.stopPlayback();
        }
        super.onPause();
    }

    @Override
    public View makeView() {
        ImageView iv = new ImageView(a);
        iv.setScaleType(ScaleType.FIT_XY);
        iv.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        return iv;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        a.unregisterReceiver(mReceiver);
    }


}
