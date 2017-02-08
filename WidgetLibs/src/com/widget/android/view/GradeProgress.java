package com.widget.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.widget.android.utils.ConvertUtils;

@SuppressLint("NewApi")
public class GradeProgress extends View {
    int width, height;

    int scaleTotal = 5;

    int maxGrade = 5;
    float currGrade = 3;

    Paint gradePaint, bgStrokePaint, bgPaint, progressPaint;

    int textHeight;

    float startX = 0;
    float vWidth;
    float scaleWidth = 0;
    ;
    float progressWidth;

    int padingTop = ConvertUtils.dip2px(getContext(), 1);
    int rectHeight = ConvertUtils.dip2px(getContext(), 23);

    private float progress = 0;
    private float stopProgress = 100;


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            postInvalidate();
        }

        ;
    };


    public GradeProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    private void init() {
        gradePaint = new Paint();
        gradePaint.setColor(0xffff7a67);
        gradePaint.setTextSize(ConvertUtils.sp2px(getContext(), 12));
        gradePaint.setStyle(Style.FILL_AND_STROKE);

        bgStrokePaint = new Paint();
        bgStrokePaint.setColor(0xffd2d2d2);
        bgStrokePaint.setStyle(Style.STROKE);
        bgStrokePaint.setStrokeWidth(ConvertUtils.sp2px(getContext(), 2));


        bgPaint = new Paint();
        bgPaint.setColor(0xffe6e6e6);
        bgPaint.setStyle(Style.FILL);

        progressPaint = new Paint();
        progressPaint.setStyle(Style.FILL);


        String str = "V";
        Rect rect = new Rect();
        gradePaint.getTextBounds(str, 0, str.length(), rect);
        textHeight = Math.abs(rect.bottom - rect.top);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width = getDefaultSize(getMinimumWidth(), widthMeasureSpec);
        height = getDefaultSize(getMinimumHeight(), heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawStrokeBg(canvas);
        drawGrade(canvas);
        drawProgress(canvas);

        invalidate();
    }

    private void drawGrade(Canvas canvas) {
        scaleWidth = width / scaleTotal;
        //float errorSize = ConvertUtils.dip2px(getContext(), 2);
        vWidth = (scaleTotal - 1) * scaleWidth;
        startX = (width - vWidth) / 2;

        for (int i = maxGrade - 5; i < maxGrade; i++) {
            String str = "V" + (i + 1);
            Rect rect = new Rect();
            gradePaint.getTextBounds(str, 0, str.length(), rect);
            int width = Math.abs(rect.right - rect.left);
            int height = Math.abs(rect.bottom - rect.top);
            int offset = 0;
            if (i < currGrade + 1) {
                gradePaint.setColor(0xffff7a67);
            } else {
                gradePaint.setColor(0xffd2d2d2);
            }
            if (i == maxGrade - 5) {
                offset = 0;
            } else if (i == maxGrade - 1) {
                offset = width;
            } else {
                offset = width / 2;
            }
            canvas.drawText(str, startX + i * scaleWidth - offset, this.height, gradePaint);
        }
    }

    private void drawStrokeBg(Canvas canvas) {
        RectF rect = new RectF(startX, padingTop, vWidth + startX, rectHeight);
        RectF rect1 = new RectF(startX, padingTop, vWidth + startX, rectHeight);
        canvas.drawRoundRect(rect1, (float) ConvertUtils.dip2px(getContext(), 15), (float) ConvertUtils.dip2px(getContext(), 15), bgPaint);
        canvas.drawRoundRect(rect, (float) ConvertUtils.dip2px(getContext(), 15), (float) ConvertUtils.dip2px(getContext(), 15), bgStrokePaint);
    }

    public void drawProgress(Canvas canvas) {

        float right = progress * (progressWidth / 100);

        LinearGradient linearGradient = new LinearGradient(startX, 0, vWidth + startX, 0, 0xffffaf67, 0xffff7a67, TileMode.MIRROR);
        RectF rect1 = new RectF(startX, padingTop, right + startX, rectHeight);
        progressPaint.setShader(linearGradient);
        canvas.drawRoundRect(rect1, (float) ConvertUtils.dip2px(getContext(), 15), (float) ConvertUtils.dip2px(getContext(), 15), progressPaint);

    }

    public void setGrade(float grade) {
        currGrade = grade;

        --currGrade;

        if (currGrade < 1) {
            currGrade = 1.2f;
        } else if (currGrade == 1) {
            currGrade = 1.2f;
        } else {

        }
        new ProgressThread(true).start();

    }

    class ProgressThread extends Thread {

        public boolean isRun() {
            return isRun;
        }

        public void setRun(boolean isRun) {
            this.isRun = isRun;
        }

        public ProgressThread(boolean isRun) {
            super();
            this.isRun = isRun;
        }

        private boolean isRun = false;

        @Override
        public void run() {
            super.run();
            while (scaleWidth == 0) ;

            progressWidth = currGrade * scaleWidth;

            while (isRun) {
                if (progress == stopProgress) {
                    isRun = false;
                    break;
                }
                progress += 2;
                handler.sendEmptyMessage(0x10008);
                try {
//					if(progress == 1){
//						Thread.sleep(500);
//					}else{
                    Thread.sleep(33);
//					}

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

}
