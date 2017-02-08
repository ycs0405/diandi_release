package com.widget.android.view.side;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

@SuppressLint("DrawAllocation")
public class CustomSideView extends FrameLayout {

    private float currX, currY, lastX, lastY;

    /***
     * 左右侧边栏
     */
    private LeftRightSide leftRightSide;

    private MainSide sideMain;

    private Context mContext;

    private int vWidth = 800, vHeight = 480;

    private int sideWidth = vWidth / 6 * 5;

    private boolean isLeft = true;
    private boolean isRight = false;
    private boolean isTouchOffsetMode = false;

    public CustomSideView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    private void init() {
        initLeftRightView();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY)

        {
            throw new IllegalStateException(
                    "ApplicationsStackLayout can only be used with "
                            + "measure spec mode=EXACTLY");
        }

        this.vWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.vHeight = MeasureSpec.getSize(heightMeasureSpec);
        sideWidth = vWidth / 6 * 5;
        if (vWidth != 800 && leftRightSide != null) {
            leftRightSide.setLeftSize(sideWidth);
        }

    }

    /***
     * 添加
     * 左右侧边栏
     */
    private void initLeftRightView() {
        leftRightSide = new LeftRightSide(mContext);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        addView(leftRightSide, 0, params);
    }

    private void initMainView() {
        sideMain = (MainSide) getChildAt(1);

    }

    public void addLeftMenu(View view) {
        isLeft = true;
        leftRightSide.addLeftView(view, vWidth);
    }

    public void openLeft() {

        sideMain.openLeft();
    }

    public boolean autoLeft() {
        boolean b = sideMain.autoLeft();
        boolean left = !b;
        if (left) {
            leftRightSide.closeLeft();
        } else {
            leftRightSide.openLeft();
        }
        return b;
    }

    public void closeLeft() {
        sideMain.closeLeft();
    }

    public void openRight() {
        sideMain.openRight();
    }

    public void closeRight() {
        sideMain.closeRight();
    }

    public boolean IsLeft() {
        return sideMain.isLeftStatus();
    }

    public void autoRight() {
        sideMain.autoRight();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initMainView();
    }


//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		int offset = 0;
//		if(sideMain != null && isTouchOffsetMode){
//			offset = sideMain.touchEvent(event);
//		}
////		if(leftRightSide != null){
////			leftRightSide.scrollByOffset(offset);
////		}
//		
//		switch (event.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			lastX = currX;
//			currX = event.getRawX();
//			lastY = currY;
//			currY = event.getRawY();
//			
//			break;
//		case MotionEvent.ACTION_UP:
//			float x = lastX - event.getRawX();
//			float y = lastY - event.getRawY();
//
//			if(Math.abs(x) > Math.abs(y)){
//				offset = (int) (lastX - event.getRawX());
//				if(offset > 0){
//					if(sideMain.isLeftStatus()){
//						autoLeft();
//						return true;
//					}
//				}else{
//					
//				}
//			}
//			break;
//		case MotionEvent.ACTION_MOVE:
//		
//			break;
//		case MotionEvent.ACTION_CANCEL:
//			break;
//		
//		}
//		return false;
//	}


//
//	@Override
//	public boolean onInterceptTouchEvent(MotionEvent ev) {
//		currX = ev.getRawX();
//		currY = ev.getRawY();
//		switch (ev.getAction()) {
//		case MotionEvent.ACTION_DOWN:
//			lastX = currX;
//			currX = ev.getRawX();
//			lastY = currY;
//			currY = ev.getRawY();
//			
//			break;
//		case MotionEvent.ACTION_UP:
//			break;
//		case MotionEvent.ACTION_MOVE:
//			float  x = lastX - currX;
//			float y = lastY - currY;
//			
//			if(Math.abs(y) < Math.abs(x)){
//				if(Math.abs(x) > ConvertUtils.dip2px(mContext, 10) && sideMain.isLeftStatus()){
//					return true;
//				}
//			}
//			lastX = currX;
//			lastY = currY;
//			break;
//		case MotionEvent.ACTION_CANCEL:
//			break;
//		}
//		return super.onInterceptTouchEvent(ev);
//	}

    public boolean getLeftStatus() {
        return sideMain.isLeftStatus();
    }

    public boolean getRightStatus() {
        return sideMain.isRightStatus();
    }
}
