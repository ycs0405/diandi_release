package com.widget.android.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CustomProgress extends View {
    private float width;
    private int height;
    private int frameStrokeWidth = 2;
    private int cirR = 6;
    private Paint bg, paintBg, paintProgress, textPaint;

    private Context mContext;

    private float progress = 0;
    private float stopProgress = 0;
    private ProgressThread progressThread;
    private float left, right, top, bottom;
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            postInvalidate();
        }

        ;
    };

    public CustomProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mContext = context;
        init();
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        this.width = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        this.height = getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec);
    }

    @Override
    public void draw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.draw(canvas);
        Rect rectFbg = new Rect(dip2px(mContext, frameStrokeWidth), dip2px(mContext, frameStrokeWidth), (int) (width - dip2px(mContext, frameStrokeWidth)), height - dip2px(mContext, frameStrokeWidth));
        canvas.drawRect(rectFbg, bg);

//		if(progress > 0 && progress < 100 ){
//			RectF rect = new RectF(dip2px(mContext, frameStrokeWidth+2), dip2px(mContext, frameStrokeWidth)*2, (progress*(width / 100)-dip2px(mContext, frameStrokeWidth)-2), height-dip2px(mContext, frameStrokeWidth)*2);
//			canvas.drawRoundRect(rect,dip2px(mContext, cirR), dip2px(mContext, cirR), paintProgress);
//		}else if(progress ==100){
//			RectF rect = new RectF(dip2px(mContext, frameStrokeWidth+2), dip2px(mContext, frameStrokeWidth)*2, (progress*(width / 100)-dip2px(mContext, frameStrokeWidth)-2), height-dip2px(mContext, frameStrokeWidth)*2);
//			canvas.drawRoundRect(rect,dip2px(mContext, cirR), dip2px(mContext, cirR), paintProgress);
//		}else{
//			RectF rect = new RectF(0,0,0,0);
//			canvas.drawRoundRect(rect,dip2px(mContext, cirR), dip2px(mContext, cirR), paintProgress);
//		}
        left = dip2px(mContext, frameStrokeWidth + 2);
        right = progress * (width / 100);
        if (right < left) {
            right = left + right;
        }
        if (right >= width - dip2px(mContext, frameStrokeWidth + 2)) {
            right = right - dip2px(mContext, frameStrokeWidth + 2);
        }
        Rect rect = new Rect((int) left, (int) (dip2px(mContext, frameStrokeWidth) * 2), (int) right, height - dip2px(mContext, frameStrokeWidth) * 2);
//		RectF rect1 = new RectF(left,dip2px(mContext, frameStrokeWidth)*2,right,height-dip2px(mContext, frameStrokeWidth)*2);
        canvas.drawRect(rect, paintProgress);

//		canvas.drawRoundRect(rect,dip2px(mContext, cirR), dip2px(mContext, cirR), paintProgress);
//		RectF rectF = new RectF(dip2px(mContext, frameStrokeWidth), dip2px(mContext, frameStrokeWidth), width-dip2px(mContext, frameStrokeWidth), height-dip2px(mContext, frameStrokeWidth));
//		canvas.drawRoundRect(rectF,dip2px(mContext, cirR), dip2px(mContext, cirR), paintBg);

        drawText(canvas);

    }

    private void init() {
        bg = new Paint();
        bg.setStyle(Style.FILL);
        bg.setStrokeWidth(dip2px(mContext, frameStrokeWidth));
        bg.setColor(0xffeff0f0);
        bg.setAntiAlias(true);

        paintBg = new Paint();
        paintBg.setStyle(Style.STROKE);
        paintBg.setStrokeWidth(dip2px(mContext, frameStrokeWidth));
        paintBg.setColor(0xffdcdce8);
        paintBg.setAntiAlias(true);
//		paintBg.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

        paintProgress = new Paint();
        paintProgress.setStyle(Style.FILL);
        paintProgress.setStrokeWidth(dip2px(mContext, frameStrokeWidth));
        RadialGradient radialGradient = new RadialGradient(0, 0, 100, 0xff006ec3, 0xff006ec3, Shader.TileMode.REPEAT);
//		LinearGradient linearGradient = new LinearGradient(0, 0, 100, 100,Color.GREEN, Color.RED, TileMode.REPEAT);
        paintProgress.setShader(radialGradient);
        paintProgress.setAntiAlias(true);
        paintBg.setXfermode(new PorterDuffXfermode(Mode.SRC_ATOP));

        textPaint = new Paint();
        textPaint.setStyle(Style.STROKE);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(dip2px(mContext, 12));

    }


    private void drawText(Canvas canvas) {


        Rect rect = new Rect();
        textPaint.getTextBounds("" + progress, 0, ("" + progress).length(), rect);
        int textWidth = Math.abs(rect.right - rect.left);
        int textHeight = Math.abs(rect.bottom - rect.top);

        canvas.drawText(progress + "%", (width - textWidth) / 2, height - textHeight / 2 - dip2px(mContext, frameStrokeWidth) / 2, textPaint);

    }

    /**
     * 根据手机   的分辨率�?dp 的单�?转成�?px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private void startTest() {
        if (progressThread != null) {
            progressThread.setRun(false);
            progressThread = null;
        }
        progressThread = new ProgressThread(true);
        progressThread.start();
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
            while (isRun) {
                if (progress == stopProgress) {
                    isRun = false;
                    break;
                }
                ++progress;
                handler.sendEmptyMessage(0x10008);
                try {
                    if (progress == 1) {
                        Thread.sleep(500);
                    } else {
                        Thread.sleep(10);
                    }

                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    /***
     * @param stopProgress
     * @category stopProgress 取�? 0 ~ 100
     */
    public void animProgress(float stopProgress) {
        if (stopProgress < 0) {
            stopProgress = 0;
        }
        if (stopProgress >= 100) {
            stopProgress = 100;
        }
        this.stopProgress = stopProgress;

        startTest();
    }

    /***
     * @param currProgress
     * @category currProgress 取�? 0 ~ 100
     */
    public void setCurrProgress(float currProgress) {
        if (currProgress < 0) {
            progress = 0;
        }
        if (currProgress > 100) {
            progress = 100;
        }
        progress = currProgress;
        postInvalidate();
    }
}
