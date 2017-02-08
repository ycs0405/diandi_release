package com.widget.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.widget.android.interfaces.ITouchClick;

/***
 * @author fu
 *         滑动删除控件
 */
@SuppressLint("ClickableViewAccessibility")
public class TouchDelView extends LinearLayout {
    float currX;
    float lastX;
    float offset;
    float currY;
    float lastY;
    float downX;
    @SuppressWarnings("unused")
    private View slideViewleft;
    private View slideViewRight;
    private Scroller scroller;
    private OnDelListener delListener;
    private Context mContext;
    private boolean status = false; //true left;false right;
    private boolean isIntercept = true;
    private boolean del;
    private long downTime = 0;

    ITouchClick click;

    public TouchDelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        scroller = new Scroller(context);
        setOrientation(LinearLayout.HORIZONTAL);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        slideViewleft = getChildAt(0);
    }

    public View getSlideViewRight() {
        return slideViewRight;
    }

    public void setSlideViewRight(View slideViewRight) {
        this.slideViewRight = slideViewRight;
        measureView(slideViewRight);

        LinearLayout.LayoutParams footParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        footParams.rightMargin = -slideViewRight.getMeasuredWidth();
        slideViewRight.setLayoutParams(footParams);
        addView(slideViewRight);

    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (isIntercept) {
            currX = ev.getRawX();
            currY = ev.getRawY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = currX;
                    lastY = currY;
                    currX = ev.getRawX();
                    currY = ev.getRawY();
                    downX = currX;
                    downTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float x = lastX - ev.getRawX();
                    float y = lastY - ev.getRawY();
                    if (Math.abs(x) > dip2px(mContext, 5) || Math.abs(y) > dip2px(mContext, 5)) {
                        if (Math.abs(y) < Math.abs(x)) {
                            return true;
                        }
                    }

                    break;
                case MotionEvent.ACTION_UP:
                    if ((System.currentTimeMillis() - downTime) > 500) {
                        return true;
                    }

                    break;
                case MotionEvent.ACTION_CANCEL:
                    break;
            }
        }

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isIntercept) {
            return super.onTouchEvent(event);
        }

        currX = event.getRawX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                lastX = currX;
                currX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:

                if (slideViewRight != null) {
                    offset = lastX - event.getRawX();
                    lastX = event.getRawX();
                    if (offset > 0) {
                        if (getScrollX() < slideViewRight.getMeasuredWidth() && (getScrollX() + offset) < slideViewRight.getMeasuredWidth()) {
                            scrollBy((int) offset, 0);
                        }
                    } else {
                        if (getScrollX() > 0 && (getScrollX() + offset) > 0) {
                            scrollBy((int) offset, 0);
                        }
                    }

                }

                break;
            case MotionEvent.ACTION_UP:

                if (slideViewRight != null && getScrollX() > 0 && getScrollX() < slideViewRight.getMeasuredWidth()) {
                    lastX = event.getRawX();
                    scroolView();
                }
                if (Math.abs(offset) < dip2px(mContext, 1)) {
                    if (System.currentTimeMillis() - downTime < 100) {
                        if (click != null) {
                            click.click();
                        }
                    }
                }


                break;
            case MotionEvent.ACTION_CANCEL:
                if (slideViewRight != null) {
                    lastX = event.getRawX();
                    scroolView();
                }
                break;
        }

        return true;
    }


    @SuppressWarnings("unused")
    private void scroolView() {
        float w = slideViewRight.getMeasuredWidth() * 0.6f;

        if ((downX - lastX) > 0) {//left
            if (Math.abs(getScrollX()) >= slideViewRight.getMeasuredWidth() / 5) {

                scroller.startScroll(getScrollX(), 0, slideViewRight.getMeasuredWidth() - Math.abs(getScrollX()), 0, 500);
                del = true;
            } else {
                scroller.startScroll(Math.abs(getScrollX()), 0, -getScrollX(), 0, 500);
                del = false;
            }
        } else {//right;
            if ((slideViewRight.getMeasuredWidth() - Math.abs(getScrollX())) >= slideViewRight.getMeasuredWidth() / 5) {
                scroller.startScroll(getScrollX(), 0, -getScrollX(), 0, 500);
                del = false;
            } else {
                scroller.startScroll(getScrollX(), 0, slideViewRight.getMeasuredWidth() - Math.abs(getScrollX()), 0, 500);
                del = true;
            }


        }

        if (delListener != null) {
            delListener.invokingUi(del);
        }


        postInvalidate();
    }

    public void startScroll(int sx, int tx) {
        scroller.startScroll(sx, 0, tx, 0, 500);
    }

    public Scroller getScroller() {
        return scroller;
    }

    public void setScroller(Scroller scroller) {
        this.scroller = scroller;
    }

    @Override
    public void computeScroll() {

        if (scroller.computeScrollOffset()) {

            scrollTo(scroller.getCurrX(), scroller.getCurrY());

            postInvalidate();
        }

//        if(delListener != null){
//        	status = getScrollX() >= slideViewRight.getMeasuredWidth() ? true : false;
//			delListener.invokingUi(status);
//		}
        super.computeScroll();
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

    /**
     * 显示删除
     */
    public void sligeDel() {
        del = true;
        scroller.startScroll(getScrollX(), 0, slideViewRight.getMeasuredWidth(), 0, 0);
        postInvalidate();
    }

    /**
     * 隐藏删除
     */
    public void sligeGoneDel() {
        del = false;
        scroller.startScroll(getScrollX(), 0, -slideViewRight.getMeasuredWidth(), 0, 0);
        postInvalidate();
    }

    /**
     * 隐藏删除
     */
    public void sligeGoneDelAnim() {
        del = false;
        scroller.startScroll(getScrollX(), 0, -slideViewRight.getMeasuredWidth(), 0, 500);
        postInvalidate();
    }

    public void setDelListener(OnDelListener delListener) {
        this.delListener = delListener;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public boolean isIntercept() {
        return isIntercept;
    }

    public void setIntercept(boolean isIntercept) {
        this.isIntercept = isIntercept;
    }

    public boolean isDel() {
        return del;
    }

    public void setDel(boolean del) {
        this.del = del;
    }

    public void setClick(ITouchClick click) {
        this.click = click;
    }


}
