package com.widget.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.widget.android.R;

public class RotateImageView extends ImageView {
    private Matrix matrix;
    private Bitmap bitmap;
    private float degrees = 0;

    public RotateImageView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public RotateImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.share_to_time_line_icon);
        matrix = new Matrix();
//		matrix.setTranslate(bitmap.getWidth()/2,bitmap.getHeight()/2);

    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        bitmap = bitmap;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, matrix, null);
        postInvalidate();
    }

    public void drawRotate() {
        matrix.postRotate(degrees, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
    }

    public void startRotate() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (degrees >= 360) {
                    degrees = 0;
                }
                degrees += 18;
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public float getDegrees() {
        return degrees;
    }

    public void setDegrees(float degrees, boolean ar) {
        if (degrees >= 360) {
            this.degrees = 0;
        }
        if (ar) {
            this.degrees = degrees;
        } else {
            this.degrees = degrees;
        }

        drawRotate();
    }
}
