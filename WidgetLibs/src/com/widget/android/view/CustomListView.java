package com.widget.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ListView;

public class CustomListView extends ListView {
    float currX;
    float lastX;
    float offset;
    float currY;
    float lastY;
    float downX;

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        currX = ev.getRawX();
        currY = ev.getRawY();
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = currX;
                lastY = currY;
                currX = ev.getRawX();
                currY = ev.getRawY();
                downX = currX;

                break;
            case MotionEvent.ACTION_MOVE:
                Log.d("touch", "move");
                float x = lastX - ev.getRawX();
                float y = lastY - ev.getRawY();
                if (Math.abs(y) < Math.abs(x)) {
                    return false;
                }
                break;
            case MotionEvent.ACTION_UP:

                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }

        return super.onInterceptTouchEvent(ev);
    }
}
