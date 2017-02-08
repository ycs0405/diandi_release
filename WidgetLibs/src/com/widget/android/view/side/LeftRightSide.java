package com.widget.android.view.side;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class LeftRightSide extends LinearLayout {
    private int vWidth, vHeight;

    private int sideWidth = vWidth / 6 * 5;

    private Scroller scroller;

    private View leftView;


    public LeftRightSide(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        scroller = new Scroller(context);
    }

    public void addLeftView(View view, int width) {
        leftView = view;
        sideWidth = (width / 6 * 5) / 2;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
        params.leftMargin = -sideWidth;
        addView(leftView, params);

    }

    public void setLeftSize(int width) {
        if (leftView == null) return;
        LinearLayout.LayoutParams params = (LayoutParams) leftView.getLayoutParams();
        if (params != null) {
            params.width = width;
        } else {
            params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);
        }
        leftView.setLayoutParams(params);
        postInvalidate();
    }

    //	@Override
//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		super.onLayout(changed, l, t, r, b);
//		
//		if(!isexsitView && leftView != null){
//			isexsitView = true;
//			removeAllViews();
//			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//			params.leftMargin = -sideWidth;
//			addView(leftView,params);
//		}
//		
//	}
//	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
//		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
//		
//		if (widthMode != MeasureSpec.EXACTLY|| heightMode != MeasureSpec.EXACTLY)
// 
//		{
//			throw new IllegalStateException(
//					"ApplicationsStackLayout can only be used with "
//							+ "measure spec mode=EXACTLY");
//		}
//		
//		this.vWidth = MeasureSpec.getSize(widthMeasureSpec);
//		this.vHeight = MeasureSpec.getSize(heightMeasureSpec);
//		sideWidth = (vWidth/4*3)/2;
//	}
//	
    public void scrollByOffset(int offset) {
        scrollBy(offset, 0);
        //Log.d("rp168", "x=="+getScrollX());
    }

    public void openLeft() {
        scroller.startScroll(getScrollX(), 0, -sideWidth, 0, 500);
        postInvalidate();
    }

    public void closeLeft() {
        scroller.startScroll(getScrollX(), 0, sideWidth, 0, 500);
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
        super.computeScroll();
    }

}
