package com.widget.android.view.side;


import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class MainSide extends LinearLayout {

    private float currX, currY, lastX, lastY;

    private Scroller mainScroller;

    private int vWidth = 800, vHeight = 480;

    private int sideWidth = vWidth / 6 * 5;

    private boolean leftStatus = false;
    private boolean rightStatus = false;

    private boolean sideDir = false;
    private float sideOffset = 0f;

    private boolean isLeft = false;
    private boolean isRight = false;


    private Context mContext;

    public MainSide(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mainScroller = new Scroller(context);
    }

    public void openLeft() {
        rightStatus = false;
        leftStatus = true;
        mainScroller.startScroll(0, 0, -sideWidth, 0, 500);
        postInvalidate();
    }

    public void closeLeft() {
        leftStatus = false;
        mainScroller.startScroll(-sideWidth, 0, sideWidth, 0, 500);
        postInvalidate();
    }

    public void openRight() {
        rightStatus = true;
        leftStatus = false;
        mainScroller.startScroll(0, 0, sideWidth, 0, 500);
        postInvalidate();
    }

    public void closeRight() {
        rightStatus = false;
        mainScroller.startScroll(sideWidth, 0, -sideWidth, 0, 500);
        postInvalidate();
    }

    public void autoRight() {
        if (rightStatus) {
            closeRight();
        } else {
            openRight();
        }
    }

    public boolean autoLeft() {
        if (leftStatus) {
            closeLeft();
        } else {
            openLeft();
        }
        return leftStatus;
    }

    @Override
    public void computeScroll() {
        if (mainScroller.computeScrollOffset()) {
            scrollTo(mainScroller.getCurrX(), mainScroller.getCurrY());
            postInvalidate();
        }
//		if(getScrollX() == 0){
//			leftStatus = false;
//			rightStatus = false;
//		}
        Log.d("rp168", "x==" + getScrollX());
        super.computeScroll();
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
    }


    public int touchEvent(MotionEvent event) {
        currX = event.getRawX();
        currY = event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = currX;
                currX = event.getRawX();
                lastY = currY;
                currY = event.getRawY();

                break;
            case MotionEvent.ACTION_UP:
                checkDistance();
                break;
            case MotionEvent.ACTION_MOVE:
                float offset = lastX - currX;
                float x = lastX - currX;
                float y = lastY - currY;

                if (Math.abs(x) < Math.abs(y)) {
                    return 0;
                }

                lastX = currX;
                lastY = currY;

                if (offset > 0) {
                    sideDir = true;
                } else {
                    sideDir = false;
                }
                offset = handlerOffset(offset);
                sideOffset = offset;

                scrollBy((int) offset, 0);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;

        }
        return (int) sideOffset;
    }

    private void checkDistance() {

        if (sideDir) {//left
            if (getScrollX() <= 0) {//左侧还原
                if (Math.abs(getScrollX()) < sideWidth / 4 * 3) {
                    mainScroller.startScroll(getScrollX(), 0, Math.abs(getScrollX()), 0, 500);
                } else {
                    mainScroller.startScroll(getScrollX(), 0, -sideWidth - getScrollX(), 0, 500);
                }
            } else if (getScrollX() >= 0) {//打开右边菜单
                if (getScrollX() > sideWidth / 4) {
                    leftStatus = false;
                    rightStatus = true;
                    mainScroller.startScroll(getScrollX(), 0, sideWidth - getScrollX(), 0, 500);
                } else {
                    mainScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 500);
                }

            }
        } else {
            if (getScrollX() <= 0) {
                if (Math.abs(getScrollX()) > sideWidth / 4) {
                    leftStatus = true;
                    rightStatus = false;
                    mainScroller.startScroll(getScrollX(), 0, -sideWidth - getScrollX(), 0, 500);
                } else {
                    mainScroller.startScroll(getScrollX(), 0, Math.abs(getScrollX()), 0, 500);
                }
            } else if (getScrollX() >= 0) {
                if (getScrollX() > sideWidth / 4 * 3) {

                    mainScroller.startScroll(getScrollX(), 0, sideWidth - getScrollX(), 0, 500);
                } else {

                    mainScroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 500);
                }
            }

        }
        postInvalidate();
    }

    private float handlerOffset(float offset) {
        if (sideDir) {
            if (getScrollX() >= 0) {
                if (!isRight) {
                    return 0;
                }
                if (getScrollX() >= sideWidth) {
                    return 0;
                }
            } else {
                if (getScrollX() >= 0) {
                    return 0;
                }
            }

        } else {
            if (getScrollX() <= 0) {
                if (Math.abs(getScrollX()) >= sideWidth) {
                    return 0;
                }
            } else {
                if (getScrollX() <= 0) {
                    return 0;
                }
            }

        }

        return offset;
    }

    public boolean isLeftStatus() {
        return leftStatus;
    }

    public void setLeftStatus(boolean leftStatus) {
        this.leftStatus = leftStatus;
    }

    public boolean isRightStatus() {
        return rightStatus;
    }

    public void setRightStatus(boolean rightStatus) {
        this.rightStatus = rightStatus;
    }

//	@Override
//	public boolean onTouchEvent(MotionEvent event) {
//		// TODO Auto-generated method stub
//		return !(leftStatus || rightStatus);
//	}
}
