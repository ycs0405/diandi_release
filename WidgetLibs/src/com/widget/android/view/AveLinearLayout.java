package com.widget.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class AveLinearLayout extends LinearLayout {
    private int vWidth;
    private int vHeight;

    public AveLinearLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public AveLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        if (widthMode == MeasureSpec.EXACTLY) {
            vWidth = MeasureSpec.getSize(widthMeasureSpec);
        }
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.EXACTLY) {
            vHeight = MeasureSpec.getSize(heightMeasureSpec);
        }


    }
}
