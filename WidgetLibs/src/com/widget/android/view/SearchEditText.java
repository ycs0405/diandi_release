package com.widget.android.view;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class SearchEditText extends EditText {

    private Drawable leftDrawable;
    private Drawable rightDrawable;

    public SearchEditText(Context context) {
        super(context);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        leftDrawable = getCompoundDrawables()[0];
        rightDrawable = getCompoundDrawables()[2];
        setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null,
                rightDrawable, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (rightDrawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            // 判断触摸点是否在水平范围内
            boolean isInnerWidth = (x > (getWidth() - getTotalPaddingRight()))
                    && (x < (getWidth() - getPaddingRight()));
            // 获取删除图标的边界，返回一个Rect对象
            Rect rect = rightDrawable.getBounds();
            // 获取删除图标的高度
            int height = rect.height();
            int y = (int) event.getY();
            // 计算图标底部到控件底部的距离
            int distance = (getHeight() - height) / 2;
            // 判断触摸点是否在竖直范围内(可能会有点误差)
            // 触摸点的纵坐标在distance到（distance+图标自身的高度）之内，则视为点中删除图标
            boolean isInnerHeight = (y > distance) && (y < (distance + height));
            if (isInnerWidth && isInnerHeight) {
                if (onImageClickListener != null) {
                    onImageClickListener.onRightImageClick();
                }
            }
        }
        if (leftDrawable != null && event.getAction() == MotionEvent.ACTION_UP) {
            int x = (int) event.getX();
            // 判断触摸点是否在水平范围内
            boolean isInnerWidth = (x < getTotalPaddingLeft())
                    && (x > getPaddingLeft());
            // 获取删除图标的边界，返回一个Rect对象
            Rect rect = leftDrawable.getBounds();
            // 获取删除图标的高度
            int height = rect.height();
            int y = (int) event.getY();
            // 计算图标底部到控件底部的距离
            int distance = (getHeight() - height) / 2;
            // 判断触摸点是否在竖直范围内(可能会有点误差)
            // 触摸点的纵坐标在distance到（distance+图标自身的高度）之内，则视为点中删除图标
            boolean isInnerHeight = (y > distance) && (y < (distance + height));
            if (isInnerWidth && isInnerHeight) {
                if (onImageClickListener != null) {
                    onImageClickListener.onLeftImageClick();
                }
            }
        }
        return super.onTouchEvent(event);
    }

    public void setOnImageClickListener(
            OnImageClickListener onImageClickListener) {
        this.onImageClickListener = onImageClickListener;
    }

    private OnImageClickListener onImageClickListener;

    public static interface OnImageClickListener {
        void onRightImageClick();

        void onLeftImageClick();
    }

}
