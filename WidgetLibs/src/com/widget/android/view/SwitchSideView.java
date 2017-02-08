package com.widget.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.widget.android.R;

public class SwitchSideView extends FrameLayout {
    private SideLayout sideLayout;

    private View leftView, rightView;


    public SwitchSideView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    private SwitchSideView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }


    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        sideLayout = (SideLayout) findViewById(R.id.side_view);

        leftView = findViewById(R.id.left_view);
        rightView = findViewById(R.id.right_view);

//		int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//		int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
//		
//		leftView.measure(w, h);
//		rightView.measure(w, h);
        measureView(leftView);
        measureView(rightView);
//		
        sideLayout.setLeftWidth(leftView.getMeasuredWidth());
        sideLayout.setRightWidth(rightView.getMeasuredWidth());
    }


    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }


}
