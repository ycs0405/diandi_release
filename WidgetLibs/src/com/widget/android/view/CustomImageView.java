package com.widget.android.view;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/****
 * @author fwc
 */
@SuppressLint("DrawAllocation")
public class CustomImageView extends ImageView {
    private Bitmap oldBitmap;
    private int vWidth;
    private int vHeight;
    private Context mContext;

    public CustomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mContext = context;
        ini();
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        ini();
    }

    public CustomImageView(Context context) {
        super(context);
        this.mContext = context;
        ini();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void ini() {
//		setLayerType(View.LAYER_TYPE_HARDWARE, null);
        setScaleType(ScaleType.FIT_XY);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            new Exception(getClass().getName() + "获取宽度出错");
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            new Exception(getClass().getName() + "获取高度出错");
        }
        vWidth = MeasureSpec.getSize(widthMeasureSpec);
        vHeight = MeasureSpec.getSize(heightMeasureSpec);


        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        oldBitmap = bm;
    }

    @Override
    public void setImageResource(int resId) {
        // TODO Auto-generated method stub
//		super.setImageResource(resId);
        oldBitmap = BitmapFactory.decodeResource(getResources(), resId);
    }

    private Bitmap handRoundBitmap(Bitmap bmp) {
        Bitmap newBmp = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), Config.ARGB_8888);
        Rect rect = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());
        RectF rectF = new RectF(rect);
        Canvas canvas = new Canvas(newBmp);
        Paint paint = new Paint();
        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(rectF, getRawSize(mContext, TypedValue.COMPLEX_UNIT_DIP, 15), getRawSize(mContext, TypedValue.COMPLEX_UNIT_DIP, 15), paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bmp, rect, rect, paint);
        return newBmp;
    }

    private Bitmap handRoundBitmap(Drawable draw) {
        Bitmap newBmp = Bitmap.createBitmap(draw.getIntrinsicWidth(), draw.getIntrinsicHeight(), Config.ARGB_8888);
        Rect rect = new Rect(0, 0, draw.getIntrinsicWidth(), draw.getIntrinsicHeight());
        RectF rectF = new RectF(rect);
        Canvas canvas = new Canvas(newBmp);
        draw.draw(canvas);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        canvas.drawARGB(0, 0, 0, 0);

        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(rectF, getRawSize(mContext, TypedValue.COMPLEX_UNIT_DIP, 15), getRawSize(mContext, TypedValue.COMPLEX_UNIT_DIP, 15), paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
//		canvas.drawBitmap(bmp, rect, rect, paint);
        return newBmp;
    }

    //	@Override
//	public void setImageDrawable(Drawable drawable) {
//		// TODO Auto-generated method stub
//		Bitmap newBmp=Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
//		Canvas canvas=new Canvas(newBmp);
//		drawable.draw(canvas);
//		oldBitmap=newBmp;
//	}
    private float getRawSize(Context context, int unit, float size) {
        Resources r;
        if (context == null)
            r = Resources.getSystem();
        else
            r = context.getResources();
        return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
    }


    public void handWidthLikeHeight(Bitmap bitmap) {
        Bitmap newBitmap = null;
        int imgWidht = bitmap.getWidth();
        if (imgWidht > vWidth) {
            int offset = imgWidht - vWidth;
            newBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight() - offset, true);
        } else if (imgWidht < vWidth) {
            int offset = vWidth - imgWidht;
            newBitmap = Bitmap.createScaledBitmap(bitmap, vWidth, offset + bitmap.getHeight(), true);
        }
        super.setImageBitmap(newBitmap);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        if (oldBitmap != null) {
            handWidthLikeHeight(oldBitmap);
        }
        super.onLayout(changed, left, top, right, bottom);

    }
}
