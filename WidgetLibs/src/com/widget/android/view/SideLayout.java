package com.widget.android.view;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class SideLayout extends LinearLayout {
    float currX;
    float lastX;
    float offset;
    float currY;
    float lastY;
    float downX;

    private Context mContext;
    private Scroller scroller;

    private int leftWidth, rightWidth;

    private Handler handler = new Handler();
    private long lastTime;

    public interface OnCustomClickListenrr {
        public void custiomClickLirtener();
    }

    public interface OnLeftRightListener {
        public void onLeftSide();

        public void onRightSide();
    }

    private OnCustomClickListenrr customClickListenrr;
    private OnLeftRightListener leftRightListener;

    public SideLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        scroller = new Scroller(context);

    }

    public void setCustomClickListenrr(OnCustomClickListenrr customClickListenrr) {
        this.customClickListenrr = customClickListenrr;
    }

    public void setOnLeftRightListener(OnLeftRightListener leftRightListener) {
        this.leftRightListener = leftRightListener;
    }

    private SideLayout(Context context) {
        super(context);
        mContext = context;
        scroller = new Scroller(context);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub


        currX = ev.getRawX();
        currY = ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = currX;
                lastY = currY;
                currX = ev.getRawX();
                currY = ev.getRawY();
                downX = currX;
                Log.d("touch", "side_down");

                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("touch", "side_move");
                float x = lastX - ev.getRawX();
                float y = lastY - ev.getRawY();

                if (Math.abs(x) > dip2px(mContext, 5) || Math.abs(y) > dip2px(mContext, 5)) {
                    if (Math.abs(y) > Math.abs(x)) {
                        Log.d("touch", "true");
                        return false;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                Log.d("touch", "side_up");
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        currX = event.getRawX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastTime = System.currentTimeMillis();
                lastX = currX;
                currX = event.getRawX();
                Log.d("touch", "side_down----------");
                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("touch", "side_move----------");
                offset = lastX - event.getRawX();
                lastX = event.getRawX();
                Log.d("touch", "move-----------");

                if (offset < 0) {
                    if (getScrollX() <= -rightWidth) {
                        offset = 0;
                    }
                } else {
                    if (getScrollX() >= leftWidth) {
                        offset = 0;
                    }
                }

                scrollBy((int) offset, 0);
                break;
            case MotionEvent.ACTION_UP:
                Log.d("touch", "side_up----------");
                lastX = event.getRawX();
                scrollView();
                if ((System.currentTimeMillis() - lastTime) < 100) {
                    if (customClickListenrr != null)
                        customClickListenrr.custiomClickLirtener();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                lastX = event.getRawX();
                scrollView();
                break;
        }

        return true;
    }

    @Override
    public void computeScroll() {
        // TODO Auto-generated method stub
        if (scroller.computeScrollOffset()) {

            scrollTo(scroller.getCurrX(), scroller.getCurrY());

            postInvalidate();
        }
        super.computeScroll();
    }

    /**
     * 根据手机的分辨率�?dp 的单�?转成�?px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    public void setLeftWidth(int leftWidth) {
        this.leftWidth = leftWidth;
    }


    public void setRightWidth(int rightWidth) {
        this.rightWidth = rightWidth;
    }

    private void scrollView() {
        if (getScrollX() <= 0) {
            scroller.startScroll(getScrollX(), 0, Math.abs(getScrollX()), 0, 500);
            if (Math.abs(getScrollX()) > dip2px(mContext, 2)) {
                if (leftRightListener != null) {
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            leftRightListener.onLeftSide();
                        }
                    }, 500);

                }
            }
        } else if (getScrollX() >= 0) {
            scroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 500);
            if (Math.abs(getScrollX()) > dip2px(mContext, 2)) {
                if (leftRightListener != null) {
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            leftRightListener.onRightSide();

                        }
                    }, 500);
                }
            }

        }
        postInvalidate();
    }
}
