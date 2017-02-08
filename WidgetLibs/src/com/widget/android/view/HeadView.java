package com.widget.android.view;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


public class HeadView extends LinearLayout {
    private ImageView exe;
    private ImageView arrow;
    private Animation restroAnimation = null;
    private Context mContext;
    private LinearLayout upLayout;
    private LinearLayout exeLayout;
    private TextView hint;
    private boolean UpAnimation = true;
    private boolean exeAnimation = true;
    private View infoMationHead;
    RotateAnimation upArrow;
    RotateAnimation upArrow2;

    public HeadView(Context context, int layout) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;
        LayoutInflater.from(mContext).inflate(layout, this, true);
    }

    public HeadView(Context context, View layout) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;
    }

    public HeadView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mContext = context;

        this.setOrientation(LinearLayout.VERTICAL);
        this.setGravity(Gravity.CENTER);
        upLayout = new LinearLayout(mContext);
        upLayout.setOrientation(LinearLayout.HORIZONTAL);
        upLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        upLayout.setGravity(Gravity.CENTER);
        arrow = new ImageView(mContext);
        arrow.setImageBitmap(BitmapFactory.decodeStream(this.getClass().getResourceAsStream("arrow2_ico.png")));
        upLayout.addView(arrow);
        hint = new TextView(mContext);
        hint.setText("下拉可以刷新");
        hint.setTextSize(18);
        hint.setTextColor(0xff000000);
        upLayout.addView(hint);
        exeLayout = new LinearLayout(mContext);
        exeLayout.setOrientation(LinearLayout.HORIZONTAL);
        exeLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        exeLayout.setGravity(Gravity.CENTER);
        exe = new ImageView(mContext);
        RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setRepeatCount(-1);
        exe.setImageBitmap(BitmapFactory.decodeStream(this.getClass().getResourceAsStream("loading1.png")));
        exe.startAnimation(animation);
        exeLayout.addView(exe);
        TextView t = new TextView(mContext);
        t.setText("正在刷新");
        t.setTextSize(18);
        t.setTextColor(0xff000000);
        exeLayout.addView(t);
        FrameLayout f = new FrameLayout(mContext);
        LinearLayout.LayoutParams upParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        upParams.gravity = Gravity.CENTER;
        LinearLayout.LayoutParams exeParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        exeParams.gravity = Gravity.CENTER;
        upLayout.setLayoutParams(upParams);
        exeLayout.setLayoutParams(exeParams);
        f.addView(upLayout);
        f.addView(exeLayout);
        exeLayout.setVisibility(View.GONE);
        this.addView(f);
        iniAnimation();
//		this.addView(infoMationHead=(LinearLayout) LayoutInflater.from(mContext).inflate(R.layout.infomation_head, null));
    }

    private void iniAnimation() {
        upArrow2 = new RotateAnimation(-180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        upArrow2.setDuration(500);
        upArrow2.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.anim.accelerate_interpolator));
        upArrow2.setFillAfter(true);

        upArrow = new RotateAnimation(0, -180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        upArrow.setDuration(500);
        upArrow.setInterpolator(AnimationUtils.loadInterpolator(mContext, android.R.anim.accelerate_interpolator));
        upArrow.setFillAfter(true);
    }

    /***
     * 添加内部View
     *
     * @param view
     */
    public void addInteriorView(View view) {
        this.infoMationHead = view;
        this.addView(view);
    }

    public boolean isAnimation() {
        return UpAnimation;
    }


    public void setAnimation(boolean b) {
        this.UpAnimation = b;
    }


    public boolean isExeAnimation() {
        return exeAnimation;
    }


    public void setExeAnimation(boolean exeAnimation) {
        this.exeAnimation = exeAnimation;
    }

    public View getInfohead() {
        return this.infoMationHead;
    }

    public LinearLayout getUpLayout() {
        return upLayout;
    }

    public LinearLayout getExeLayout() {
        return exeLayout;
    }

    public Animation getRestroAnimation() {
        return restroAnimation;
    }

    public void setRestroAnimation(Animation restroAnimation) {
        this.restroAnimation = restroAnimation;
    }

    public void StartArrowAnimation() {
        exeLayout.setVisibility(View.GONE);
        arrow.setImageBitmap(BitmapFactory.decodeStream(this.getClass().getResourceAsStream("arrow_ico.png")));
        arrow.startAnimation(upArrow2);
        hint.setText("松开即可刷新");
    }

    public void restroArrowAnimation() {
        exeLayout.setVisibility(View.GONE);
        arrow.startAnimation(upArrow);
        hint.setText("下拉可以刷新");
    }

    public void setArrow() {
        arrow.setImageBitmap(BitmapFactory.decodeStream(this.getClass().getResourceAsStream("arrow2_ico.png")));
        hint.setText("下拉可以刷新");
    }

    public void refreshSucceed() {
        hint.setText("刷新成功");
    }

    public void instanceAnimation() {
        UpAnimation = true;

    }

    public void instanceRestroAnimation() {
        exeAnimation = true;
    }

    public void executeView() {
        upLayout.setVisibility(View.INVISIBLE);
        exeLayout.setVisibility(View.VISIBLE);
    }

    public void restoreView() {
        upLayout.setVisibility(View.VISIBLE);
        exeLayout.setVisibility(View.GONE);
    }

    public void setUpLayout(int view) {
        upLayout.setVisibility(view);
    }
}
