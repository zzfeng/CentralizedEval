package com.sundyn.centralizedeval.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/21.
 */

public class VerticalScrollTextView extends TextView {
    private Paint mPaint;
    private float mX;// 屏幕X轴的中点，此值固定，保持字幕在X中间显示
    private Paint mPathPaint;
    private float step = 0f;
    public int index = 0;
    private List<String> list;
    private List<String> contents;
    private String content;
    public float mTouchHistoryY;
    private int mY;
    private float middleY;// y轴中间
    private float targetY;
    private float offsetY = 100; // 字幕在Y轴上的偏移量，此值会根据歌词的滚动变小
    private static boolean blLrc = false;
    private float touchY; // 当触摸字幕View时，保存为当前触点的Y轴坐标
    private float touchX;
    private float width;
    private float height;

    private static final int DY = 20; // 每一行的间隔
    private static final int TEXTSIZE = 20; // 字体大小
    private static final int TITLESIZE = 30; // 标题大小
    private static boolean isMove = true;
    private UpdateThread thread;
    private Handler handler;

    public VerticalScrollTextView(Context context) {
        super(context);
        init();
    }

    public VerticalScrollTextView(Context context, AttributeSet attr) {
        super(context, attr);
        init();
    }

    public VerticalScrollTextView(Context context, AttributeSet attr, int i) {
        super(context, attr, i);
        init();
    }

    private void init() {
        setFocusable(true);
        if (list == null) {
            contents = new ArrayList<String>();
            contents.add(0, "暂时没有资源");
        }
        // 非高亮部分
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setTextSize(TEXTSIZE);
        mPaint.setColor(Color.WHITE);
        mPaint.setTypeface(Typeface.SERIF);
        // mPathPaint.setTextAlign(Paint.Align.CENTER);

        // 高亮部分 当前字幕
        mPathPaint = new Paint();
        mPathPaint.setAntiAlias(true);
        mPathPaint.setColor(Color.WHITE);
        mPathPaint.setTextSize(TITLESIZE);
        mPathPaint.setTypeface(Typeface.SANS_SERIF);
        mPathPaint.setTextAlign(Paint.Align.CENTER);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException(
                    "ScrollLayout only canmCurScreen run at EXACTLY mode!");
        }
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(0xEFeffff);
        Paint p = mPaint;
        Paint p2 = mPathPaint;
        if (index == -1)
            return;

        // 先画当前行，之后再画他的前面和后面，这样就保持当前行在中间的位置
		/*
		 * offsetY = isMove ? offsetY : 0; float tempMiddleY = middleY; float
		 * middleY2 = tempMiddleY + offsetY;
		 */
		/*
		 * canvas.drawText(list.get(index).getName().trim(), 0, middleY2, p2);
		 * float tempY = middleY2; Log.i("tempY", tempY + ""); // 画出本句之前的句子 for
		 * (int i = index - 1; i >= 0; i--) { tempY = tempY - DY; if (tempY < 0)
		 * { break; } canvas.drawText(list.get(i).getName().trim(), 0, tempY,
		 * p); } tempY = middleY2; // 画出本句之后的句子 for (int i = index + 1; i <
		 * list.size(); i++) { // 往下推移 tempY = tempY + DY; if (tempY > mY) {
		 * break; } canvas.drawText(list.get(i).getName().trim(), 0,tempY, p); }
		 */

        for (int i = 0; i < contents.size(); i++) {
            if (i == 0) {
                canvas.drawText(contents.get(i).trim(), mX,
                        (i + 1) * (p2.getTextSize() + DY) - step, p2);
            } else {
                canvas.drawText(contents.get(i).trim(), 0,
                        (i + 1) * (p.getTextSize() + DY) - step, p);
            }

        }

    }

    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        mX = w * 0.5f;
        mY = h;
        middleY = h * 0.5f;
    }

    public long updateIndex(int index) {
        if (index == -1)
            return -1;
        this.index = index;
        return index;
    }

    public float updateStep(float step) {
        if (step == -1)
            return -1;
        this.step = step;
        return step;
    }

	/*
	 * public List<String> getList() { return list; }
	 */

    public void setList(List<String> list) {
        this.list = list;
        initContents(list);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    private void initContents(List<String> list) {
        float length = 0;
        if (list == null || list.size() == 0) {
            return;
        }
        // 下面的代码是根据宽度和字体大小，来计算textview显示的行数。
        contents.clear();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            String tempString = list.get(i);
            for (int j = 0; j < tempString.length(); j++) {
                if (length < width - 20) {
                    builder.append(tempString.charAt(j));
                    length += mPaint
                            .measureText(tempString.substring(j, j + 1));
                    if (j == tempString.length() - 1) {
                        contents.add(builder.toString());
                        builder.delete(0, builder.length());
                        length = 0;
                    } else if (tempString.charAt(j) == '\n') {
                        contents.add(builder.toString());
                        builder.delete(0, builder.length());
                        length = 0;
                    }
                } else {
                    contents.add(builder.toString().substring(0,
                            builder.toString().length()));
                    builder.delete(0, builder.length());
                    length = 0;
                    j--;
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    public void updateUI(Handler handler) {
        if (thread == null) {
            thread = new UpdateThread();
            thread.start();
        } else {
            step = 0f;
            thread.threadResume();

        }

        this.handler = handler;
    }

    /**
     * 停止线程
     */
    public void threadStop() {
        if (thread == null) {
            return;
        }
        thread.threadStop();
    }

    /**
     * 线程暂停
     */
    public void threadPause() {
        if (thread == null) {
            return;
        }
        thread.threadPause();
    }

    /**
     * 唤醒线程
     */
    public synchronized void threadResume() {
        if (thread == null) {
            return;
        }
        thread.threadResume();
    }

    class UpdateThread extends Thread {
        int i = 0;
        boolean isRun = true;
        boolean isWait = false;

        public void run() {
            // do {
			/*
			 * long sleeptime = updateIndex(i); time += sleeptime;
			 */

            while (isRun) {
                synchronized (this) {
                    while (isWait) {
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

                mHandler.post(mUpdateResults);

                float contentHeight = contents.size()
                        * (mPaint.getTextSize() + DY);

                if (step > contentHeight + 20 - height
                        || height >= contentHeight) {
                    // timer.cancel();
                    thread.threadPause();
                    handler.sendEmptyMessage(0);
                    // step = 0f;
                }
                try {
                    sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                step = step + 0.01f;

				/*
				 * //timer = new Timer(); //timer.schedule(new TimerTask() {
				 *
				 * @Override public void run() { mHandler.post(mUpdateResults);
				 *
				 *
				 * if (sleeptime == -1) return;
				 *
				 * step = step + 0.01f;
				 *
				 * if (step >= contents.size() * mPaint.getTextSize()) { step =
				 * 0f; isMove = false; // timer.cancel(); }
				 *
				 *
				 * i++; if (i == getList().size()){ i = 0; }
				 *
				 * } // }, 1, 1);
				 */}
        }

        /**
         * 停止线程
         */
        public void threadStop() {
            if (!isRun) {
                return;
            }
            isRun = false;
        }

        /**
         * 线程暂停
         */
        public void threadPause() {
            if (isWait) {
                return;
            }
            isWait = true;
        }

        /**
         * 唤醒线程
         */
        public synchronized void threadResume() {
            if (!isWait) {
                return;
            }
            isWait = false;
            thread.notify();
        }

    }

    Handler mHandler = new Handler();
    Runnable mUpdateResults = new Runnable() {
        public void run() {
            invalidate(); // 更新视图
        }
    };

	/*
	 * @Override public boolean onTouchEvent(MotionEvent event) { // TODO float
	 * tt = event.getY(); float y1 = 0; float y2 = 0; switch (event.getAction())
	 * { case MotionEvent.ACTION_DOWN: touchX = event.getX(); y1 = event.getY();
	 * break; case MotionEvent.ACTION_MOVE: touchY = tt - touchY; offsetY =
	 * offsetY + touchY; y2=event.getY(); Log.d("y1", y1+""); Log.d("y2",
	 * y2+""); offsetY=y1-y2;
	 *
	 * if (Math.abs(offsetY) >= height) {
	 *
	 * if (offsetY > 0) { offsetY = height; } else { offsetY = -height; } }
	 *
	 * step=step+offsetY; mHandler.post(mUpdateResults); // //index = (int)
	 * (-offsetY / DY); // middleY=middleY+offsetY; break; case
	 * MotionEvent.ACTION_UP: // blScrollView=false; y2 = event.getY(); offsetY=
	 * y1 - y2; Log.d("y2", y2+""); if (Math.abs(offsetY) >= height) {
	 *
	 * if (offsetY > 0) { offsetY = height; } else { offsetY = -height; } }
	 * //isMove = true; step=step+offsetY; mHandler.post(mUpdateResults); break;
	 * } touchY = tt; return true; }
	 */
}