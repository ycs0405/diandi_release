package com.cargps.android.utils;

import android.view.MotionEvent;

import com.amap.api.maps2d.AMap;
import com.cargps.android.MyApplication;

public class LeftTouch implements AMap.OnMapTouchListener {
    private float currX, currY, lastX, lastY;

    @Override
    public void onTouch(MotionEvent event) {
        int offset = 0;
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
                        if (MyApplication.mainActivity != null) {
                            MyApplication.mainActivity.openMenu();
                            return;
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
    }


}
