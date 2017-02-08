package com.widget.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

@SuppressLint({"ClickableViewAccessibility"})
public class LayoutFrameView extends LinearLayout {
    private Context mContext;

    float downX, downY, lastX, lastY, currX, currY;

    private Scroller scroller;


    public LayoutFrameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public LayoutFrameView(Context context) {
        super(context);
        mContext = context;
        init();
    }

    @SuppressLint("NewApi")
    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        scroller = new Scroller(mContext);

        TextView textView = new TextView(mContext);
        textView.setBackgroundColor(0xffff0000);
        addView(textView, new LayoutParams(-1, -1));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        currX = event.getX();
        currY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                lastX = currX;
                lastY = currY;
                currX = event.getX();
                currY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                float offset = lastX - currX;
                if (getScrollX() == 0 && offset < 0) {
                    offset = 0;
                } else if (getScrollX() + offset < 0) {
                    offset = -getScrollX();
                }
                if (Math.abs(offset) > 0) {

                    scrollBy((int) offset, 0);
                }

                lastX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                handlerDistance();
                break;
            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        currX = ev.getX();
        currY = ev.getY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                lastX = currX;
                lastY = currY;
                currX = ev.getX();
                currY = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
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

                break;
            case MotionEvent.ACTION_CANCEL:

                break;
        }
        return true;
    }

    /**
     * 根据手机的分辨率�?dp 的单�?转成�?px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {

            scrollTo(scroller.getCurrX(), scroller.getCurrY());

            postInvalidate();
        }
        super.computeScroll();
    }

    public void handlerDistance() {
        if (downX - lastX > 0) {
            if (getScrollX() > getMeasuredWidth() / 3) {
                scroller.startScroll(getScrollX(), 0, getMeasuredWidth() - getScrollX(), 0, 500);
            } else {
                scroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 500);

            }
        } else {

            if (getScrollX() < getMeasuredWidth() / 3 * 2) {
                scroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 500);
            } else {
                scroller.startScroll(getScrollX(), 0, getMeasuredWidth() - getScrollX(), 0, 500);
            }
        }
        postInvalidate();

    }
}
