package com.widget.android.view;

import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.widget.android.R;
import com.widget.android.utils.ConvertUtils;

public class CustomFootView extends LinearLayout {

    public CustomFootView(Context context) {
        super(context);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER);
        ImageView exe = new ImageView(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ConvertUtils.dip2px(context, 22), ConvertUtils.dip2px(context, 22));
        exe.setLayoutParams(params);
        RotateAnimation animation = new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setInterpolator(new LinearInterpolator());
        animation.setRepeatCount(-1);
        animation.setDuration(1000);
        exe.setBackgroundResource(R.drawable.loading1_bar);
        exe.startAnimation(animation);
        addView(exe);

        TextView hint = new TextView(context);
        hint.setText("正在加载更多...");
        hint.setTextSize(14);
        hint.setTextColor(0xff000000);
        hint.setPadding((int) (context.getResources().getDisplayMetrics().density * 6 + 0.5f), 0, 0, 0);
        addView(hint);
    }

}
