package com.widget.android.view;


import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


public class FooterView extends LinearLayout {
    LinearLayout upFooter;
    LinearLayout exeFooter;

    public FooterView(Context context) {
        super(context);
        upFooter = new LinearLayout(context);
        upFooter.setOrientation(LinearLayout.HORIZONTAL);
        upFooter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        upFooter.setGravity(Gravity.CENTER);
        TextView hint = new TextView(context);
        hint.setText("查看更多");
        hint.setTextSize(18);
        hint.setTextColor(0xff000000);
        upFooter.addView(hint);
        exeFooter = new LinearLayout(context);
        exeFooter.setOrientation(LinearLayout.HORIZONTAL);
        exeFooter.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        exeFooter.setGravity(Gravity.CENTER);
        ProgressBar exe = new ProgressBar(context);
        exeFooter.addView(exe);
        TextView t = new TextView(context);
        t.setText("正在加载更多");
        t.setTextSize(18);
        t.setTextColor(0xff000000);

        exeFooter.setVisibility(View.VISIBLE);
        FrameLayout f = new FrameLayout(context);
        f.addView(upFooter);
        f.addView(exeFooter);
        exeFooter.setVisibility(View.GONE);
        this.addView(f, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT));
    }

    public void exeFooter() {
        exeFooter.setVisibility(View.VISIBLE);
        upFooter.setVisibility(View.GONE);
    }

    public void restroFooter() {
        exeFooter.setVisibility(View.GONE);
        upFooter.setVisibility(View.VISIBLE);
    }
}
