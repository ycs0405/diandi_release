package com.widget.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Scroller;

import com.widget.android.interfaces.OnRefreshFooterListener;
import com.widget.android.interfaces.OnRefreshHeadListener;
import com.widget.android.interfaces.OnScrollChangeListener;

/***
 * @author fu
 */
public class CustomScrollView extends LinearLayout implements OnScrollChangeListener {
    private String TAG = getClass().getName();
    private float xCurr, yCurr, xLast, yLast, offset;

    // 滚动监听
    private OnScrollChangeListener onScrollChangeListenerl;

    // 下拉监听
    private OnRefreshHeadListener onRefreshHeadListener;
    // 上拉监听
    private OnRefreshFooterListener onRefreshFooterListener;

    // --------------- 内部放的控件---------------//
    private OverWriteScrollView overWriteScrollView;// ScrollView
    private ExpandableListView expandableListView;// 扩展LISTVIEW
    private ListView listView;
    private WebView webView;
    // ---------------------------------------------

    public Scroller scroller;
    private boolean directionXY = false; // true 上拉 false 下拉
    private boolean interceptTouch = false;
    private String pullStatus = "normal";
    private boolean touchStatus = false;

    // 顶部与底部VIEW
    private CustomHeadView customHeadView;
    private CustomFootView customFootView;

    private boolean lock = true;// 拖动锁

    // 支持顶部或者底部刷新
    private boolean headView = true;//
    private boolean footView = false;

    private boolean endablePull = true;// 是否启用上拉下拉

    private String modelName = "time";
    private Context mContext;

    public enum Status {
        down, up, normal;
    }

    public String getPullStatus() {
        return pullStatus;
    }

    public void setPullStatus(String pullStatus) {
        this.pullStatus = pullStatus;
    }

    public CustomScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        setOrientation(VERTICAL);
        scroller = new Scroller(context);
        customHeadView = new CustomHeadView(context);
        customHeadView.setTimeKey(modelName);
        customHeadView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        LinearLayout.LayoutParams headParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        headParams.topMargin = -customHeadView.getMeasuredHeight();
        customHeadView.setLayoutParams(headParams);
        addView(customHeadView);

        customFootView = new CustomFootView(context);
        customFootView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        LinearLayout.LayoutParams footParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                customFootView.getMeasuredHeight() + dip2px(mContext, 10));
        footParams.bottomMargin = -customFootView.getMeasuredHeight();
        customFootView.setLayoutParams(footParams);
    }

    public boolean isHeadView() {
        return headView;
    }

    public void setHeadView(boolean headView) {
        this.headView = headView;
        if (this.headView) {
            customHeadView.setVisibility(View.VISIBLE);
        } else {
            customHeadView.setVisibility(View.GONE);
        }
    }

    public boolean isFootView() {
        return footView;
    }

    public void setFootView(boolean footView) {
        this.footView = footView;
        if (this.footView) {
            customFootView.setVisibility(View.VISIBLE);
        } else {
            customFootView.setVisibility(View.GONE);
        }
    }

    public boolean isEndablePull() {
        return endablePull;
    }

    public void setEndablePull(boolean endablePull) {
        this.endablePull = endablePull;
    }

    public OverWriteScrollView getOverWriteScrollView() {
        return overWriteScrollView;
    }

    @Override
    public void ScrollChange(int l, int t, int oldl, int oldt) {

    }

    public OnRefreshHeadListener getOnRefreshHeadListener() {
        return onRefreshHeadListener;
    }

    public void setOnRefreshHeadListener(OnRefreshHeadListener onRefreshHeadListener) {
        this.onRefreshHeadListener = onRefreshHeadListener;
    }

    public OnRefreshFooterListener getOnRefreshFooterListener() {
        return onRefreshFooterListener;
    }

    public void setOnRefreshFooterListener(OnRefreshFooterListener onRefreshFooterListener) {
        this.onRefreshFooterListener = onRefreshFooterListener;
    }

    @Override
    public void topOrbottom(boolean top, boolean bottom) {

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    public void setOnScrollChangeListenerl(OnScrollChangeListener onScrollChangeListenerl) {
        this.onScrollChangeListenerl = onScrollChangeListenerl;
    }

    @Override
    protected void onFinishInflate() {
        findView(this);
        addView(customFootView);
        ShowHeadAndFoot();
        super.onFinishInflate();
    }

    private void findView(ViewGroup v) {
        for (int i = 0; i < v.getChildCount(); i++) {
            View child = v.getChildAt(i);
            if (child instanceof OverWriteScrollView) {
                overWriteScrollView = (OverWriteScrollView) child;
                overWriteScrollView.setOnScrollChangeListener(this);
            } else if (child instanceof ExpandableListView) {
                expandableListView = (ExpandableListView) child;
            } else if (child instanceof ListView) {
                listView = (ListView) child;
            } else if (child instanceof WebView) {
                webView = (WebView) child;
            }
            if (child instanceof ViewGroup && ((ViewGroup) child).getChildCount() > 0) {
                findView((ViewGroup) child);
            }

        }
    }

    /***
     * 显示上拉和下拉VIEW
     */

    private void ShowHeadAndFoot() {
        if (this.headView) {
            customHeadView.setVisibility(View.VISIBLE);
        } else {
            customHeadView.setVisibility(View.GONE);
        }
        if (this.footView) {
            customFootView.setVisibility(View.VISIBLE);
        } else {
            customFootView.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        System.out.println("scrollview view ..." + ev.getAction() + "lock = " + lock);

        if (!lock) {
            return true;
        }

        xCurr = ev.getRawX();
        yCurr = ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xLast = xCurr;
                yLast = yCurr;
                xCurr = ev.getRawX();
                yCurr = ev.getRawY();

                break;
            case MotionEvent.ACTION_MOVE:

                float x = xLast - ev.getRawX();
                float y = yLast - ev.getRawY();

                if (Math.abs(y) < Math.abs(x)) {
                    return false;
                }

                offset = yLast - ev.getRawY();
                yLast = yCurr;
                interceptTouch = false;

                if (offset > 0) {// 向上拉
                    pullStatus = Status.up.toString();
                    // 判断滚动到最底部
                    if (overWriteScrollView != null) {
                        interceptTouch = overWriteScrollView.getScrollY() >= overWriteScrollView.getLayoutHolder().getHeight() - overWriteScrollView.getHeight();
                    } else if (expandableListView != null && expandableListView.getVisibility() == View.VISIBLE) {
                        interceptTouch = expandableListView.getLastVisiblePosition() == expandableListView.getCount() - 1;
                    } else if (listView != null && listView.getVisibility() == View.VISIBLE) {
                        interceptTouch = ((AbsListView) listView).getChildCount() == 0 ? true : listView.getLastVisiblePosition() == listView.getCount() - 1
                                && ((AbsListView) listView).getChildAt(((AbsListView) listView).getChildCount() - 1)
                                .getBottom() <= ((AbsListView) listView).getHeight();
                    }
                } else if (offset < 0) {// 向下拉
                    pullStatus = Status.down.toString();
                    if (overWriteScrollView != null) {
                        interceptTouch = overWriteScrollView.getScrollY() <= 0;// 判断滚动到最顶部
                    } else if (expandableListView != null && expandableListView.getVisibility() == View.VISIBLE) {
                        interceptTouch = expandableListView.getFirstVisiblePosition() == 0;
                    } else if (listView != null && listView.getVisibility() == View.VISIBLE) {
                        interceptTouch = listView.getFirstVisiblePosition() == 0
                                && ((AbsListView) listView).getChildCount() == 0 ? true : ((AbsListView) listView)
                                .getChildAt(0).getTop() == 0;
                    } else if (webView != null) {
                        interceptTouch = webView.getScrollY() <= 0;
                    }

                }

                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        Log.d(TAG, pullStatus);

        if (interceptTouch && endablePull && Math.abs(offset) > 10) {// 拦截
            return true;
        }
        return false;
    }

    // @Override
    // public boolean dispatchTouchEvent(MotionEvent ev) {
    //
    // if(ev.getAction() == MotionEvent.ACTION_DOWN){
    // return true;
    // }
    // return super.dispatchTouchEvent(ev);
    // }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        if (lock) {
            xCurr = ev.getRawX();
            yCurr = ev.getRawY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touchStatus = false;
                    xLast = xCurr;
                    xCurr = ev.getRawX();
                    yLast = yCurr;
                    yCurr = ev.getRawY();
                    break;
                case MotionEvent.ACTION_UP:
                    touchStatus = true;
                    if (touchStatus) {
                        complatePull();
                    }

                    break;
                case MotionEvent.ACTION_MOVE:
                    touchStatus = false;
                    offset = yLast - ev.getRawY();
                    yLast = yCurr;
                    customHeadView.setHeadTime();
                    if (offset > 0) {
                        // 向上拉
                        directionXY = true;
                        if (Math.abs(getScrollY()) < customHeadView.getMeasuredHeight()) {
                            customHeadView.upHint();
                            customHeadView.setArrowUp(false);
                            customHeadView.setArrowdown(true);
                        }

                    } else if (offset < 0) {
                        // 向下拉
                        directionXY = false;
                        if (Math.abs(getScrollY()) >= customHeadView.getMeasuredHeight()) {
                            customHeadView.downhint();
                            customHeadView.setArrowUp(true);
                            customHeadView.setArrowdown(false);
                        }

                    }
                    if (pullStatus.equals(Status.down.toString()) && directionXY) {
                        if (getScrollY() >= 0) {
                            return false;
                        }
                    } else if (pullStatus.equals(Status.up.toString()) && !directionXY) {
                        if (getScrollY() <= 0) {
                            return false;
                        }
                    } else {

                    }
                    scrollBy(0, (int) offset / 2);

                    if (onScrollChangeListenerl != null) {
                        onScrollChangeListenerl.offset(directionXY, offset, getScrollY());
                    }
                    break;
                case MotionEvent.ACTION_CANCEL:
                    complatePull();
                    break;

            }
        }
        return true;
    }

    /***
     * autodown
     */
    public void startDownRefresh() {
        directionXY = true;
        pullStatus = Status.down.toString();
        scrollBy(0, -customHeadView.getMeasuredHeight());
        customHeadView.complateView();
        lock = false;
        touchStatus = true;
        refreshDownPull();
    }

    // 上拉下拉完成动作
    public void complatePull() {
        lock = false;
        customHeadView.complateView();
        interceptTouch = false;
        if (getScrollY() >= 0) {
            directionXY = true;
        } else if (getScrollY() < 0) {
            directionXY = false;
        }
        if (directionXY) {
            if (this.footView) {
                refreshUpPull();
            } else {
                pullUp();
            }
        } else {
            if (this.headView && Math.abs(getScrollY()) >= customHeadView.getMeasuredHeight()) {
                refreshDownPull();
            } else {
                pullDown();

            }

        }
    }

    // 下拉还原动作
    private void pullDown() {


        this.post(new Runnable() {

            @Override
            public void run() {

                interceptTouch = false;
                scroller.startScroll(0, getScrollY(), 0, Math.abs(getScrollY()), 500);
                postInvalidate();
            }
        });

        postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                lock = true;
            }
        }, 500);

    }

    public void setHeadTime() {
        customHeadView.setPast_fresh_time(System.currentTimeMillis(), modelName);
    }

    // 上拉还原动作
    private void pullUp() {


        this.post(new Runnable() {

            @Override
            public void run() {
                interceptTouch = false;
                scroller.startScroll(0, getScrollY(), 0, -getScrollY(), 500);
                postInvalidate();
            }
        });

        postDelayed(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                lock = true;
            }
        }, 500);


    }

    // 下拉刷新
    public void refreshDownPull() {
        setHeadTime();
        interceptTouch = true;
        if (Math.abs(getScrollY()) >= customHeadView.getMeasuredHeight()) {
            scroller.startScroll(0, getScrollY(), 0, Math.abs(getScrollY() + customHeadView.getMeasuredHeight()), 500);
            if (onRefreshHeadListener != null) {
                this.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        if (touchStatus) {
                            onRefreshHeadListener.headRefresh();
                        }

                    }
                }, 1000);

            }
        } else {
            pullDown();
        }

        postInvalidate();
    }

    public void refreshUpPull() {
        interceptTouch = true;
        if (Math.abs(getScrollY()) >= customFootView.getMeasuredHeight()) {
            if (onRefreshFooterListener != null) {
                this.postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        if (touchStatus) {
                            onRefreshFooterListener.refreshFooter();
                        }

                    }
                }, 1000);

            }
            scroller.startScroll(0, getScrollY(), 0, -(Math.abs(getScrollY()) - customFootView.getMeasuredHeight()),
                    500);
        } else {
            pullUp();
        }

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

    @Override
    public void offset(boolean upOrdown, float offset, int y) {

    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
        customHeadView.setTimeKey(modelName);
    }


    public void pullShow() {
        Log.d("rp168", "下拉完成----------");
        if (pullStatus.equals(CustomScrollView.Status.down.toString())) {
            pullDown();
        } else {
            pullUp();
        }
    }
}
