package com.widget.android.view;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

import com.widget.android.interfaces.OnRefreshFooterListener;
import com.widget.android.interfaces.OnRefreshHeadListener;

@SuppressLint("NewApi")
public class RefreshView extends LinearLayout {
    private float xDistance, yDistance, xLast, yLast;
    private final String TAG = "refresh";
    private float lastY;
    private float currY;
    private FooterView footerView;
    private HeadView headView;
    private Context mContext;
    private float offset = 0;
    private Scroller scroller;
    private boolean upOrDonw;
    private Handler mHandler;
    private boolean headEnable = true;
    private boolean footerEnable;
    private boolean lock = true;
    private int headViewUpHight;
    private int headViewHight;
    private int footerViewHeght;
    private boolean interiorView = false;
    private ListView mListView;
    private OnRefreshHeadListener mOnRefreshHeadListener;
    private OnRefreshFooterListener mOnRefreshFooterListener;

    public RefreshView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;
        initView();
    }

//	public RefreshView(Context context, AttributeSet attrs, int defStyle) {
//		super(context, attrs, defStyle);
//		// TODO Auto-generated constructor stub
//		mContext=context;
//		initView();
//	}

    public RefreshView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        initView();
    }

    public void setOnRefreshHeadListener(OnRefreshHeadListener onRefreshHeadListener) {

        mOnRefreshHeadListener = onRefreshHeadListener;
    }

    public void setOnRefreshFooterListener(OnRefreshFooterListener onRefreshFooterListener) {
        mOnRefreshFooterListener = onRefreshFooterListener;
    }

    public void setmListView(ListView mListView) {
        this.mListView = mListView;
    }

    private void initView() {
        setOrientation(LinearLayout.VERTICAL);
        mHandler = new Handler();
        scroller = new Scroller(mContext);
        addHeadView(true);
        if (headEnable) {
            headView.setVisibility(View.VISIBLE);
        } else {
            headView.setVisibility(View.GONE);
        }

//		addHeadView(true, img.getMeasuredHeight());


    }

    public void setHeadEnable(boolean headEnable) {
        this.headEnable = headEnable;

        if (headEnable) {
            headView.setVisibility(View.VISIBLE);
        } else {
            headView.setVisibility(View.GONE);
        }
    }

    /***
     * @param add=false 重新改变大小
     *                  change=true 添加view
     */
    private void addHeadView(boolean add) {

        if (add) {
            headView = new HeadView(mContext);
            measureView(headView);
            LinearLayout.LayoutParams headParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            headParams.topMargin = -headView.getMeasuredHeight();
            headView.setLayoutParams(headParams);
            measureView(headView.getUpLayout());
            measureView(headView.getExeLayout());
            headViewUpHight = headView.getUpLayout().getMeasuredHeight();
            headViewHight = headView.getMeasuredHeight();
            addView(headView);
        } else {
            measureView(headView);
            LinearLayout.LayoutParams headParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            headParams.topMargin = -headView.getMeasuredHeight();
            headView.setLayoutParams(headParams);
            measureView(headView.getUpLayout());
            measureView(headView.getExeLayout());
            headViewUpHight = headView.getUpLayout().getMeasuredHeight();
            headViewHight = headView.getMeasuredHeight();
        }


    }

    /***
     * 自定义XML
     *
     * @param layout
     */
    public void addUserHeadView(int layout) {
        if (headView != null) {
            this.removeView(headView);
        }
        headView = new HeadView(mContext);
        measureView(headView);
        LinearLayout.LayoutParams headParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        headParams.topMargin = -headView.getMeasuredHeight();
        headView.setLayoutParams(headParams);
        measureView(headView.getUpLayout());
        measureView(headView.getExeLayout());
        headViewUpHight = headView.getUpLayout().getMeasuredHeight();
        headViewHight = headView.getMeasuredHeight();
        addView(headView, 0);
    }

    /***
     * 自定义VIEW
     *
     * @param v
     */
    public void addUserHeadView(View v) {

    }

    /***
     * 顶部置顶
     *
     * @param isShow 嵌套的显示与隐藏
     */
    public void setScrollTop(boolean isShow) {
        if (isShow) {
            measureView(headView.getInfohead());
            scrollTo(0, -headView.getInfohead().getMeasuredHeight());
        } else {
            addHeadView(false);
            scrollTo(0, -headViewUpHight);
        }


    }

    /***
     * 添加内部VIEW
     *
     * @param v
     */
    public void addInteriorView(View v) {
        setIsInterior(true);
        headView.addInteriorView(v);
        addHeadView(false);
    }

    public void setFooterEnable(boolean footerEnable) {
        this.footerEnable = footerEnable;
        if (footerEnable) {
            footerView.setVisibility(View.VISIBLE);
        } else {
            footerView.setVisibility(View.GONE);
        }
    }

    private void footerView() {
    }

    private void headView() {
        if (headView.isAnimation()) {
            headView.StartArrowAnimation();
        }
        headView.setAnimation(false);
    }

    private void headRestroView() {
        if (headView.isExeAnimation()) {
            headView.restroArrowAnimation();
        }
        headView.setExeAnimation(false);
    }

    private void addFooter() {

        footerView = new FooterView(mContext);
        LinearLayout.LayoutParams footerParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        measureView(footerView);
        footerParams.bottomMargin = -footerView.getMeasuredHeight();
        footerViewHeght = footerView.getMeasuredHeight();
        footerView.setLayoutParams(footerParams);
        addView(footerView);
        if (footerEnable) {
            footerView.setVisibility(View.VISIBLE);
        } else {
            footerView.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onFinishInflate() {
        // TODO Auto-generated method stub
        super.onFinishInflate();
        addFooter();
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

    @Override
    public void computeScroll() {
        // TODO Auto-generated method stub
        if (scroller.computeScrollOffset()) {

            scrollTo(scroller.getCurrX(), scroller.getCurrY());

            postInvalidate();
        }
        super.computeScroll();
    }

    private boolean getChildTop() {
        if (-headViewHight >= getScrollY()) {
            return true;
        }
        return false;

    }

    private int downX;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        currY = ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastY = currY;
                currY = ev.getY();
                downX = (int) ev.getX();
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;

                if (xDistance > yDistance) {
                    return false;
                }
                offset = lastY - currY;
                Log.d(TAG, "x==" + (Math.abs(ev.getX() - downX)));
                Log.d("TAG", "y==" + (Math.abs(offset)));
                lastY = currY;

                if (offset < -1) {
                    if (mListView != null) {
                        if (mListView.getFirstVisiblePosition() == 0) {
                            return true;
                        }
                    }

                }
                if (offset > 1) {
                    if (mListView != null) {
                        if (interiorView) {
                            if (mListView.getLastVisiblePosition() == mListView.getCount() - 1 || getScrollY() < 0) {
                                return true;
                            }
                        } else {
                            if (mListView.getLastVisiblePosition() == mListView.getCount() - 1) {
                                return true;
                            }
                        }

                    }
                }
                break;

            case MotionEvent.ACTION_UP:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "y===" + getScrollY());
        if (lock) {
            currY = event.getRawY();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastY = currY;
                    currY = event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    offset = lastY - currY;
                    if (offset < -1) {
                        upOrDonw = true;
                        if (getChildTop() && headEnable) {
                            headView();
                            headView.instanceRestroAnimation();
                        }

                    } else if (offset > 1) {
                        upOrDonw = false;
                        headView.instanceAnimation();
                        headRestroView();
                        if (mListView != null) {
                            if (mListView.getLastVisiblePosition() == mListView.getCount() - 1) {
                                footerView.setVisibility(View.VISIBLE);
                            } else {
                                footerView.setVisibility(View.GONE);
                            }
                        } else {
                            footerView.setVisibility(View.VISIBLE);
                        }
                    }
                    lastY = currY;
                    scrollBy(0, (int) offset);
                    Log.d(TAG, "offset===" + offset);
                    break;
                case MotionEvent.ACTION_UP:
                    Refreshfnish();
                    break;
            }
        }
        return true;


    }

    private void setIsInterior(boolean b) {
        this.interiorView = b;
    }

    /****
     * 刷新
     *
     * @param
     */
    private void refrishView() {
        if (upOrDonw) {
            if (getChildTop() && headEnable) {
                this.lock = false;
                headView.executeView();
                scroller.startScroll(0, getScrollY(), 0, Math.abs(getScrollY()) - headViewHight, 500);
                postInvalidate();
                if (mOnRefreshHeadListener != null)
                    mOnRefreshHeadListener.headRefresh();
            } else {
                headView.setArrow();
                headView.instanceAnimation();
                if (getScrollY() > 0) {
                    scroller.startScroll(0, getScrollY(), 0, -getScrollY(), 500);
                } else {
                    scroller.startScroll(0, getScrollY(), 0, Math.abs(getScrollY()) - headViewHight + headViewUpHight, 500);
                }
                postInvalidate();
            }

        }
        if (!upOrDonw) {
            if (mListView != null) {
                if (footerEnable && getScrollY() > footerViewHeght && mListView.getLastVisiblePosition() == mListView.getCount() - 1) {
                    this.lock = false;
                    footerView.exeFooter();
                    scroller.startScroll(0, getScrollY(), 0, -(getScrollY() - footerViewHeght), 500);
                    postInvalidate();
                    if (mOnRefreshFooterListener != null)
                        mOnRefreshFooterListener.refreshFooter();
                } else {
                    if (getScrollY() > footerViewHeght) {
                        scrollTo(0, 0);
                    } else {
                        scroller.startScroll(0, getScrollY(), 0, -getScrollY(), 500);
                        postInvalidate();
                    }

                }
            } else {
                if (footerEnable && getScrollY() > footerViewHeght) {
                    this.lock = false;
                    footerView.exeFooter();
                    scroller.startScroll(0, getScrollY(), 0, -(getScrollY() - footerViewHeght), 500);
                    postInvalidate();
                    if (mOnRefreshFooterListener != null)
                        mOnRefreshFooterListener.refreshFooter();
                } else {
                    if (getScrollY() > footerViewHeght) {
                        scrollTo(0, 0);
                    } else {
                        scroller.startScroll(0, getScrollY(), 0, -getScrollY(), 500);
                        postInvalidate();
                    }

                }
            }
        }

        Log.d(TAG, "updown=" + upOrDonw);
    }

    /***
     * 刷新
     */
    private void Refreshfnish() {
        refrishView();
    }

    public void completeHeadRefresh() {


        this.lock = true;
        mHandler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                headView.restoreView();
                headView.setArrow();
                headView.instanceAnimation();
                scroller.startScroll(0, getScrollY(), 0, headViewUpHight, 500);
                postInvalidate();
            }
        }, 500);


    }

    public void completeFooterRefresh() {
        footerView.restroFooter();
        this.lock = true;
        scroller.startScroll(0, getScrollY(), 0, -getScrollY(), 500);
        postInvalidate();
        postInvalidate();

    }
}
