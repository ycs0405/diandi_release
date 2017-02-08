package com.widget.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.widget.android.interfaces.OnLayoutSizeChange;
import com.widget.android.interfaces.OnScrollChangeListener;

@SuppressLint("NewApi")
public class OverWriteScrollView extends ScrollView {

    float currX;
    float lastX;
    float offset;
    float currY;
    float lastY;
    float downX;

    private boolean isTop, isBottom;
    private LinearLayout layoutHolder;
    private OnScrollChangeListener onScrollChangeListener;
    private OnLayoutSizeChange onLayoutSizeChange;
    private int layoutHeight = 0;

    public OnScrollChangeListener getOnScrollChangeListener() {
        return onScrollChangeListener;
    }

    public void setOnScrollChangeListener(OnScrollChangeListener onScrollChangeListener) {
        this.onScrollChangeListener = onScrollChangeListener;
    }

    public OnLayoutSizeChange getOnLayoutSizeChange() {
        return onLayoutSizeChange;
    }

    public void setOnLayoutSizeChange(OnLayoutSizeChange onLayoutSizeChange) {
        this.onLayoutSizeChange = onLayoutSizeChange;
    }

    public LinearLayout getLayoutHolder() {
        return layoutHolder;
    }

    public void setLayoutHolder(LinearLayout layoutHolder) {
        this.layoutHolder = layoutHolder;
    }

    public OverWriteScrollView(Context context) {
        super(context);

    }


    public OverWriteScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (android.os.Build.VERSION.SDK_INT >= 9) {
            setOverScrollMode(View.OVER_SCROLL_NEVER);
        }
        setScrollbarFadingEnabled(false);
        setVerticalScrollBarEnabled(false);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (layoutHolder != null) {
            layoutHeight = layoutHolder.getHeight() - getHeight();
            if (t == 0) {
                isTop = true;
                isBottom = false;
            } else if (t >= layoutHeight) {
                isTop = false;
                isBottom = true;
            } else {
                isTop = false;
                isBottom = false;
            }
        } else {
            isTop = false;
            isBottom = false;
        }
        if (onScrollChangeListener != null) {
            onScrollChangeListener.ScrollChange(l, t, oldl, oldt);
            onScrollChangeListener.topOrbottom(isTop, isBottom);
        }

    }

    public int getLayoutHeight() {
        return layoutHeight;
    }

    public void setLayoutHeight(int layoutHeight) {
        this.layoutHeight = layoutHeight;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {

        super.onSizeChanged(w, h, oldw, oldh);
        if (onLayoutSizeChange != null) {
            onLayoutSizeChange.changeSize(w, h, oldw, oldh);
        }
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
                break;
            case MotionEvent.ACTION_MOVE:
                float x = lastX - ev.getRawX();
                float y = lastY - ev.getRawY();
                if (Math.abs(y) < Math.abs(x)) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {

        return 0;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildAt(0) instanceof LinearLayout) {
            layoutHolder = (LinearLayout) getChildAt(0);
        }
    }
}
