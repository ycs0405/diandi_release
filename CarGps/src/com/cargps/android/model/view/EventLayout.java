package com.cargps.android.model.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import com.cargps.android.MyApplication;

public class EventLayout extends LinearLayout {
    public boolean isTouch = false;
    private float currX, currY, lastX, lastY;

    public EventLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    public EventLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public EventLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        int offset = 0;
//		if(leftRightSide != null){
//			leftRightSide.scrollByOffset(offset);
//		}

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = currX;
                currX = event.getRawX();
                lastY = currY;
                currY = event.getRawY();

                break;
            case MotionEvent.ACTION_UP:
                float x = lastX - event.getRawX();
                float y = lastY - event.getRawY();

                if (Math.abs(x) > Math.abs(y)) {
                    offset = (int) (lastX - event.getRawX());
                    if (offset > 0) {
                        if (isTouch) {
                            if (MyApplication.mainActivity != null) {
                                MyApplication.mainActivity.openMenu();
                                return true;
                            }
                        }


                    } else {

                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_CANCEL:
                break;

        }
        return false;
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        currX = ev.getRawX();
        currY = ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = currX;
                currX = ev.getRawX();
                lastY = currY;
                currY = ev.getRawY();

                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                float x = lastX - currX;
                float y = lastY - currY;
                if (Math.abs(y) < Math.abs(x)) {
                    if (isTouch) {
                        return true;
                    }
                }
                lastX = currX;
                lastY = currY;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }


}
