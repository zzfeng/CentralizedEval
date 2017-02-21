package com.sundyn.centralizedeval.plugin;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.sundyn.centralizedeval.R;
import com.sundyn.centralizedeval.utils.ADUtil;
import com.sundyn.centralizedeval.views.AdsVideo;
import com.sundyn.centralizedeval.views.VerticalScrollTextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/2/21.
 */

public class FillAdvise extends Activity implements ViewFactory{
    private final String TAG = "FillAds";
    // private BroadcastReceiver mReceiver;

    private RadioGroup indiRadioGroup; // 锟斤拷示指示锟斤拷锟侥诧拷锟斤拷

    private ImageSwitcher mSwitcher; // 锟斤拷锟节匡拷锟斤拷ImageView锟叫的癸拷锟酵计拷锟斤拷锟斤拷谢锟�
    private FrameLayout frameLayout;
    private VerticalScrollTextView mReader;
    private AdsVideo mVideo;
    private boolean mIsVideo = false; // 锟斤拷前媒锟斤拷锟斤拷锟斤拷锟角凤拷锟斤拷锟斤拷频
    private int mPlayPosition; // 锟斤拷前锟斤拷锟斤拷位锟斤拷
    private ArrayList<String> typeList; // 锟斤拷锟矫斤拷锟斤拷锟斤拷锟�
    private ArrayList<String> urlList; // 锟斤拷锟矫斤拷锟铰凤拷锟�
    private ArrayList<String> timeList; // 锟斤拷锟矫斤拷宀ワ拷锟绞憋拷锟�
    private ArrayList<String> startList;
    private ArrayList<String> endList;
    private List<String> lst;// 锟侥憋拷锟斤拷锟矫斤拷锟斤拷锟斤拷锟�
    private int indexOfList;
    private Timer timer;
    private long mStartTime;
    private long mPauseTime;
    private int mAdTime;
    // private Bitmap[] mMemBmp = new Bitmap[] { null, null }; // 锟斤拷证锟斤拷时锟斤拷锟秸ｏ拷锟斤拷止锟节达拷锟斤拷锟�
    // private Map mBmpMap = new HashMap<Integer,
    // WeakReference<BitmapDrawable>>(); // 锟斤拷锟酵计拷锟斤拷锟斤拷霉锟斤拷锟斤拷锟�
    private Button mBtnRet; // 锟斤拷锟截帮拷锟斤拷
    private boolean mIsBtnShow = true; // 锟斤拷示锟斤拷锟截硷拷
    private GestureDetector mGd; // 锟斤拷锟斤拷锟斤拷锟狡ｏ拷锟斤拷示锟斤拷锟截硷拷
    private BroadcastReceiver mReceiver; // 锟斤拷锟斤拷锟斤拷锟斤拷悴�
    private Handler mBtnHand = new Handler() { // 锟斤拷锟截凤拷锟截帮拷锟斤拷

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
                    mBtnRet.setClickable(false);
                }
            });
            mBtnRet.startAnimation(taOut);
            super.handleMessage(msg);
        }

    };

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            startTimer(indexOfList - 1);
        }

    };

    class AdTimerTask extends TimerTask {
        @Override
        public void run() {
            ADUtil.setOrderAnimation(mSwitcher);
            handler.sendEmptyMessage(0);
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // // 锟斤拷时锟斤拷锟斤拷息锟斤拷锟斤拷锟斤拷锟斤拷图片锟叫伙拷
            // // 锟斤拷锟斤拷图片锟斤拷锟斤拷时锟酵凤拷
            // if (mMemBmp[1] != null) {
            // if (!mMemBmp[1].isRecycled()) {
            // mMemBmp[1].recycle();
            // mMemBmp[1] = null;
            // }
            // }
            // mMemBmp[1] = mMemBmp[0];

            mReader.threadPause();// 锟斤拷停锟竭筹拷

            // 锟叫憋拷为锟斤拷锟津返伙拷
            if (urlList.isEmpty()) {
                Log.i(TAG, "handleMessage : Ad list is empty");
                mSwitcher.setImageDrawable(null);
                mIsVideo = false;
                if (mVideo.isPlaying()) {
                    mVideo.stopPlayback();
                }
                frameLayout.setVisibility(View.INVISIBLE);
                indiRadioGroup.setVisibility(View.GONE);
                return;
            }
            indiRadioGroup.setVisibility(View.VISIBLE);

            if (ADUtil.curPos != -1) {
                indexOfList = ADUtil.curPos - 1;
                ADUtil.curPos = -1;
            } else {
                // 锟叫憋拷循锟斤拷
                if (indexOfList >= urlList.size()) {
                    indexOfList = 0;
                } else if (indexOfList < 0) {
                    indexOfList = urlList.size() - 1;
                }
            }

            String strTemp = typeList.get(indexOfList);
            indiRadioGroup.getChildAt(indexOfList).performClick();
            if (strTemp.toLowerCase().equalsIgnoreCase("img") || strTemp.equalsIgnoreCase("IMAGE")) {
                // 锟斤拷锟斤拷锟酵计�
                mIsVideo = false;
                if (mVideo.isPlaying()) {
                    mVideo.stopPlayback();
                }
                frameLayout.setVisibility(View.INVISIBLE);
                mSwitcher.setVisibility(View.VISIBLE);
                // 锟斤拷锟斤拷锟斤拷丶锟絲锟斤拷顺锟斤拷
                mSwitcher.bringToFront();
                mBtnRet.bringToFront();

                Bitmap b = PanoCache.getBitmapFromMem(indexOfList + "");
                if (b == null) {
                    b = CommenUnit.getLoacalBitmap(urlList.get(indexOfList), true,
                            mSwitcher.getLayoutParams().width);
                    if (b != null) {
                        PanoCache.addBitmapToMem(indexOfList + "", b);
                        mSwitcher.setImageDrawable(new BitmapDrawable(getResources(), b));
                    } else {
                        Log.e(TAG, "handleMessage : Not find imagefile " + urlList.get(indexOfList));
                    }

                } else {
                    mSwitcher.setImageDrawable(new BitmapDrawable(getResources(), b));
                }
                // WeakReference<BitmapDrawable> weakBmp =
                // (WeakReference<BitmapDrawable>) mBmpMap
                // .get(indexOfList);
                // Bitmap bmp = CommenUnit.getLoacalBitmap(
                // urlList.get(indexOfList), true,
                // mSwitcher.getLayoutParams().width);
                // if (bmp != null) {
                // BitmapDrawable bmpDraw = new BitmapDrawable(bmp);
                // weakBmp = new WeakReference<BitmapDrawable>(bmpDraw);
                // mBmpMap.put(indexOfList, weakBmp);
                // } else {
                // Log.e(TAG,
                // "handleMessage : Not find imagefile "
                // + urlList.get(indexOfList));
                // }
                // if (weakBmp != null) {
                // mSwitcher.setImageDrawable(weakBmp.get());
                // }

                mAdTime = 5000;
                try {
                    mAdTime = Integer.parseInt(timeList.get(indexOfList++));
                    timer.schedule(new AdTimerTask(), mAdTime);
                } catch (Exception e) {
                    Log.i(TAG, "锟斤拷时锟斤拷锟斤拷锟斤拷失锟斤拷" + e);
                }
                mStartTime = System.currentTimeMillis();

            } else if (strTemp.equalsIgnoreCase("video")) {
                mSwitcher.setImageResource(android.R.color.black);
                // 锟斤拷锟斤拷锟斤拷锟狡�
                frameLayout.setVisibility(View.VISIBLE);
                mVideo.setVisibility(View.VISIBLE);
                // mReader.setVisibility(View.INVISIBLE);
                frameLayout.bringToFront();
                mBtnRet.bringToFront();
                mVideo.setVideoPath(urlList.get(indexOfList));
                mVideo.start();
                mIsVideo = true;
                // 锟斤拷频没锟叫革拷锟斤拷mMemBmp[0]锟斤拷锟斤拷锟斤拷锟斤拷锟絤MemBmp[1]
                // mMemBmp[1] = null;
                // 锟斤拷锟轿硷拷时锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟疥触锟斤拷锟铰革拷锟斤拷锟�
                indexOfList++;
            } else if (strTemp.equalsIgnoreCase("text")) {
                mIsVideo = false;
                if (mVideo.isPlaying()) {
                    mVideo.stopPlayback();
                }
                frameLayout.setVisibility(View.INVISIBLE);
                // mVideo.setVisibility(View.INVISIBLE);
                mSwitcher.setVisibility(View.INVISIBLE);
                mReader.setVisibility(View.VISIBLE);
                frameLayout.bringToFront();
                mBtnRet.bringToFront();
                // mReader.bringToFront();
                if (lst == null || lst.size() == 0) {
                    lst = ADUtil.readTxtFile(urlList.get(indexOfList));
                }
                // 锟斤拷View锟斤拷锟斤拷锟斤拷锟�
                mReader.setList(lst);
                // mReader.setContent(content);
                // 锟斤拷锟斤拷View
                mReader.updateUI(mHandler);
                // mReader.setText(ReadTxtFile(urlList.get(indexOfList)));
                mReader.setTextColor(Color.WHITE);
                mReader.setTextSize(20);
                // mReader.setMovementMethod(ScrollingMovementMethod.getInstance());
                indexOfList++;
            }

            super.handleMessage(msg);
        }
    };

    /**
     * 锟斤拷锟斤拷锟斤拷时锟斤拷
     */
    public void startTimer(int indexOfList) {
        mAdTime = 5000;
        try {
            mAdTime = Integer.parseInt(timeList.get(indexOfList));
            timer.schedule(new AdTimerTask(), mAdTime);
        } catch (Exception e) {
            Log.i(TAG, "锟斤拷时锟斤拷锟斤拷锟斤拷失锟斤拷" + e);
        }
        mStartTime = System.currentTimeMillis();
    }

    /**
     * 锟斤拷始锟斤拷ads.xml锟斤拷应锟斤拷锟斤拷锟叫癸拷锟斤拷锟捷结构
     */

    @Override
    public void onResume() {
        super.onResume();
        long time = mStartTime + mAdTime - mPauseTime;
        if (time < 0) {
            time = 0;
        }
        timer = new Timer();
        if (mIsVideo) {
            mVideo.start();
            mVideo.seekTo(mPlayPosition);
        } else {
            timer.schedule(new AdTimerTask(), time);
        }
    }

    @Override
    public void onPause() {
        mPauseTime = System.currentTimeMillis();
        timer.cancel();
        if (mIsVideo) {
            mPlayPosition = mVideo.getCurrentPosition();
            mVideo.pause();
        }
        super.onPause();
    }

    @Override
    public View makeView() {
        ImageView iv = new ImageView(FillAds.this);

        iv.setScaleType(ScaleType.FIT_XY);
        iv.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        return iv;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 锟斤拷锟斤拷锟睫憋拷锟解及全锟斤拷
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.fill_ads);

        // 锟斤拷始锟斤拷锟斤拷锟斤拷斜锟�
        typeList = new ArrayList<String>();
        urlList = new ArrayList<String>();
        timeList = new ArrayList<String>();
        startList = new ArrayList<String>();
        endList = new ArrayList<String>();
        indexOfList = 0;

        ADUtil.initImageList(typeList, urlList, timeList, startList, endList);

        indiRadioGroup = (RadioGroup)findViewById(R.id.indicator);
        ADUtil.initIndicator(typeList.size(), 0, indiRadioGroup, this);
        // 锟斤拷取锟斤拷锟截硷拷
        // mSwitcher = new ImageSwitcher(this);
        mSwitcher = (ImageSwitcher)findViewById(R.id.idAdsSwicher);
        mSwitcher.setFactory(this);
        // mVideo = new AdsVideo(this);
        mVideo = (AdsVideo)findViewById(R.id.idAdsPlayer);
        // 锟斤拷态锟斤拷锟酵计拷偷锟接帮拷锟斤拷趴丶锟�
        RelativeLayout layout = (RelativeLayout)findViewById(R.id.main_layout);
        frameLayout = (FrameLayout)findViewById(R.id.FrameLayout2);
        mReader = (VerticalScrollTextView)findViewById(R.id.idAdsReader);
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

        // Item锟斤拷锟斤拷锟铰硷拷锟斤拷应
        layout.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mGd.onTouchEvent(event)) {
                    return true;
                }
                return false;
            }
        });

        // 锟斤拷锟斤拷锟斤拷拥锟斤拷锟斤拷锟斤拷锟�
        CommenUnit.activityManager.put("FillAds", this);

        // 锟斤拷始锟斤拷锟斤拷锟截硷拷
        mBtnRet = CommenUnit.addReturnBtn(mBtnRet, layout, this);

        mBtnRet.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ADUtil.curPos = indexOfList;
                CommenUnit.clickExitAnim(v, FillAds.this);
            }
        });
        // 锟斤拷时锟斤拷锟截凤拷锟截硷拷
        mBtnHand.sendEmptyMessage(0);

        // 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
        mGd = new GestureDetector(this, new OnGestureListener() {

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                if (!mIsBtnShow) {
                    TranslateAnimation taIn = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0,
                            Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 1,
                            Animation.RELATIVE_TO_SELF, 0);
                    taIn.setDuration(500);
                    mBtnRet.startAnimation(taIn);
                    mIsBtnShow = true;
                    mBtnRet.setClickable(true);
                    taIn.setAnimationListener(new AnimationListener() {

                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            // 锟斤拷时锟斤拷锟截凤拷锟截硷拷
                            mBtnHand.sendEmptyMessage(0);
                        }
                    });
                }
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
                if (e1.getX() > e2.getX()) {
                    Log.i(TAG, "锟斤拷锟襟滑ｏ拷锟斤拷一锟斤拷");
                    ADUtil.setOrderAnimation(mSwitcher);
                } else {
                    Log.i(TAG, "锟斤拷锟揭伙拷锟斤拷锟斤拷一锟斤拷");
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

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                reInitImageList();
            }
        };
        registerReceiver(mReceiver, new IntentFilter("refreshads"));

    }

    /**
     * 锟斤拷锟斤拷锟斤拷锟皆�
     */
    private void reInitImageList() {
        // 锟斤拷锟截伙拷锟斤拷锟斤拷源
        // mBmpMap = new HashMap<Integer, WeakReference<BitmapDrawable>>(); //
        // 锟斤拷锟酵计拷锟斤拷锟斤拷霉锟斤拷锟斤拷锟�
        PanoCache.removeAll();
        lst = new ArrayList<String>();// 锟斤拷锟斤拷锟侥憋拷锟斤拷源
        timer.cancel();
        ADUtil.initImageList(typeList, urlList, timeList, startList, endList);
        ADUtil.initIndicator(typeList.size(), 0, indiRadioGroup, this);
        timer = new Timer();
        timer.schedule(new AdTimerTask(), 500);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // 锟斤拷锟斤拷锟绞撅拷锟斤拷丶锟�
        mGd.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onDestroy() {
        CommenUnit.activityManager.remove("FillAds");
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

}
