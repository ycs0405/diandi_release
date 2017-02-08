package com.widget.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.widget.android.interfaces.OnLayoutSizeChange;

public class CustomLinearLayout extends LinearLayout {
    private OnLayoutSizeChange onLayoutSizeChange;
    public Scroller scroller;

    public CustomLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        scroller = new Scroller(context);
    }


    private CustomLinearLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        scroller = new Scroller(context);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (onLayoutSizeChange != null) {
            onLayoutSizeChange.changeSize(w, h, oldw, oldh);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    public OnLayoutSizeChange getOnLayoutSizeChange() {
        return onLayoutSizeChange;
    }

    public void setOnLayoutSizeChange(OnLayoutSizeChange onLayoutSizeChange) {
        this.onLayoutSizeChange = onLayoutSizeChange;
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
        Log.d("rp168", "x==" + getScrollX());
        super.computeScroll();
    }

    public void startScroll(int startX, int startY, int endX, int endY) {
        scroller.startScroll(startX, startY, endX, endY, 300);
        postInvalidate();
    }

}
