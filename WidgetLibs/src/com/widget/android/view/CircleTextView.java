package com.widget.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.widget.android.R;
import com.widget.android.utils.SystemUtil;

public class CircleTextView extends View {

    private String text = "";

    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 圆环的颜色
     */
    private int roundColor;

    /**
     * 中间字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 圆环的宽度
     */
    private float roundWidth;

    public CircleTextView(Context context) {
        this(context, null);
    }

    public CircleTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.CircleTextView);
        roundColor = mTypedArray
                .getColor(R.styleable.CircleTextView_roundColor,
                        Color.parseColor("#00acff"));
        textColor = mTypedArray.getColor(
                R.styleable.CircleTextView_roundTextColor, getContext()
                        .getResources().getColor(R.color.white));
        textSize = mTypedArray.getDimension(
                R.styleable.CircleTextView_roundTextSize,
                SystemUtil.dipToPx(getContext(), 0));
        roundWidth = mTypedArray.getDimension(
                R.styleable.CircleTextView_roundWidth, 0);
        text = mTypedArray.getString(R.styleable.CircleTextView_roundText);
        if (text == null) {
            text = "校";
        }
        setText(text);

        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (roundWidth == 0) {
            roundWidth = getHeight() / 2;
        }
        if (textSize == 0) {
            textSize = (float) (getHeight() * 0.8);
        }
        /**
         * 画最外层的大圆环
         */
        paint.setColor(roundColor); // 设置圆环的颜色
        paint.setStyle(Paint.Style.FILL); // 设置实心
        paint.setStrokeWidth(roundWidth); // 设置圆环的宽度
        paint.setAntiAlias(true); // 消除锯齿
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, roundWidth, paint); // 画出圆环
        /** 画里面的字 */
        paint.setStyle(Paint.Style.FILL); // 设置空心
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT); // 设置字体

        float textWidth = paint.measureText(text);
        if (isNum(text)) {
            canvas.drawText(text, getWidth() / 2 - textWidth / 2,
                    (float) (getHeight() / 2 + textWidth * 0.6), paint);
        } else if (isLetter(text)) {
            canvas.drawText(text, getWidth() / 2 - textWidth / 2,
                    (float) (getHeight() / 2 + textWidth * 0.5), paint);
        } else {
            canvas.drawText(text, getWidth() / 2 - textWidth / 2, getHeight()
                    / 2 + textWidth / 3, paint);
        }
    }

    public boolean isLetter(String str) {
        return str.matches("[a-zA-Z]");
    }

    public boolean isNum(String str) {
        return str.matches("^[-+]?(([0-9]+)([.]([0-9]+))?|([.]([0-9]+))?)$");
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        if (text == null) {
            this.text = "";
            return;
        }
        this.text = text.trim().substring(text.length() - 1);
    }

    public int getRoundColor() {
        return roundColor;
    }

    public void setRoundColor(int roundColor) {
        this.roundColor = roundColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

}
