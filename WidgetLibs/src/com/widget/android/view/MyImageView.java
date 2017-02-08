package com.widget.android.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * @author panyan
 * @Create 2014-6-4 上午10:10:02
 * @Module core
 * @Description
 */
public class MyImageView extends ImageView {

    public MyImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public MyImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyImageView(Context context) {
        super(context);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onDetachedFromWindow() {
//		if(getDrawable()!=null){
//	Bitmap recycle_bitmap=((BitmapDrawable)getDrawable()).getBitmap();
//	if(recycle_bitmap!=null) recycle_bitmap.recycle();
//		}
        setImageDrawable(null);
//        setBackgroundDrawable(null);
        setImageBitmap(null);
        System.out.println("imageView---killed");
    }
}
