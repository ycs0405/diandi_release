package com.widget.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.widget.android.R;

/**
 * @author fu
 *         平均设置宽度控件
 */
public class WeightLayout extends LinearLayout {

    private int width;
    private int avg = -1;

    public WeightLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

        init(context, attrs);
    }

    public WeightLayout(Context context) {
        super(context);

    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.weight_layout);
        String avgStr = typedArray.getString(R.styleable.weight_layout_avg);
        if (!TextUtils.isEmpty(avgStr)) {
            if (isNum(avgStr)) {
                avg = Integer.valueOf(avgStr);
            }
        }
    }

    public boolean isNum(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);


        this.width = MeasureSpec.getSize(widthMeasureSpec);

        width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);

        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            LinearLayout.LayoutParams params = (LayoutParams) v.getLayoutParams();
            params.width = width / (avg == -1 ? 4 : avg);

        }
    }

    public int getViewWidth() {
        return width;
    }


}
