package com.widget.android.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.widget.android.R;
import com.widget.android.utils.SettingShareData;

public class CustomHeadView extends LinearLayout {
    private TextView downTextView;
    private TextView timeTextView;
    private ImageView refreshIndicatorView;
    private ProgressBar bar;
    private Context mContext;
    private boolean arrowUp = false;
    private boolean arrowdown = true;

    private String timeKey = "time";

    public CustomHeadView(Context context) {
        super(context);
        mContext = context;

//		setOrientation(HORIZONTAL);
//		setGravity(Gravity.CENTER);
//		ImageView exe=new ImageView(context);
//		RotateAnimation animation=new RotateAnimation(0f, 359f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//		animation.setInterpolator(new LinearInterpolator());
//		animation.setRepeatCount(-1);
//		animation.setDuration(1000);
//		exe.setImageBitmap(BitmapFactory.decodeStream(this.getClass().getResourceAsStream("loading1.png")));
//		exe.startAnimation(animation);
//		addView(exe);
//		
//		TextView hint = new TextView(context);
//		hint.setText("正在刷新");
//		hint.setTextSize(14);
//		hint.setTextColor(0xff000000);
//		hint.setPadding((int) (context.getResources().getDisplayMetrics().density*6+0.5f), 0, 0, 0);
//		addView(hint);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.refreshable_list_header, this, true);
        //指示器view`
        refreshIndicatorView = (ImageView) findViewById(R.id.refreshable_list_arrow);
        //刷新bar
        bar = (ProgressBar) findViewById(R.id.refreshable_list_progress);
        //下拉显示text
        downTextView = (TextView) findViewById(R.id.refreshable_list_text);
        //下来显示时间
        timeTextView = (TextView) findViewById(R.id.refresh_list_time_text);
//		LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, -refreshTargetTop);
//		lp.topMargin = refreshTargetTop;
//		lp.gravity = Gravity.CENTER;
//		addView(refreshView, lp);
//		downTextString = "下拉刷新";
//		releaseTextString = "松开刷新";	


    }

    public TextView getDownTextView() {
        return downTextView;
    }

    public void setDownTextView(TextView downTextView) {
        this.downTextView = downTextView;
    }

    public TextView getTimeTextView() {
        return timeTextView;
    }

    public void setTimeTextView(TextView timeTextView) {
        this.timeTextView = timeTextView;
    }

    public boolean isArrowUp() {
        return arrowUp;
    }

    public void setArrowUp(boolean arrowUp) {
        this.arrowUp = arrowUp;
    }

    public boolean isArrowdown() {
        return arrowdown;
    }

    public void setArrowdown(boolean arrowdown) {
        this.arrowdown = arrowdown;
    }

    public void downhint() {
        if (arrowdown) {
            bar.setVisibility(View.GONE);
            downTextView.setText("松开刷新");
            refreshIndicatorView.setVisibility(View.VISIBLE);
            refreshIndicatorView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate));
            rotateArrow();
        }
        System.out.println("arrowdown------------" + arrowdown);
    }

    public void upHint() {
        if (arrowUp) {
            bar.setVisibility(View.GONE);
            downTextView.setText("下拉刷新");
            refreshIndicatorView.setVisibility(View.VISIBLE);
            refreshIndicatorView.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotate));
            rotateArrow();
        }
        System.out.println("arrowUp------------" + arrowUp);
    }

    public void complateView() {
        downTextView.setText("正在刷新");
        setHeadTime();
        refreshIndicatorView.setVisibility(View.GONE);
        bar.setVisibility(View.VISIBLE);
        arrowdown = true;
        arrowUp = false;
    }

    private void rotateArrow() {
        Drawable drawable = refreshIndicatorView.getDrawable();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.save();
        canvas.rotate(180.0f, canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        canvas.restore();
        refreshIndicatorView.setImageBitmap(bitmap);
    }

    long past_fresh_time = 0;

    public void setHeadTime() {
        past_fresh_time = SettingShareData.getInstance(mContext).getKeyValueLong(timeKey, 0);

        if (past_fresh_time == 0) {
            timeTextView.setText("未更新");
            return;
        }
        long fresh_time = System.currentTimeMillis() - past_fresh_time;

        int day = (int) (fresh_time / (1000 * 60 * 60 * 24));
        if (day > 0) {
            timeTextView.setText(day + "天前更新");
            return;
        }
        int hour = (int) (fresh_time / (1000 * 60 * 60));
        if (hour > 0) {
            timeTextView.setText(hour + "小时前更");
            return;
        }
        int minite = (int) (fresh_time / (1000 * 60));
        if (minite > 0) {
            timeTextView.setText(minite + "分钟前更");
            return;
        }
        timeTextView.setText("刚刚更新");

    }

    public void setPast_fresh_time(long past_fresh_time, String key) {

        SettingShareData.getInstance(mContext).setKeyValue(key, past_fresh_time);
    }

    public String getTimeKey() {
        return timeKey;
    }

    public void setTimeKey(String timeKey) {
        this.timeKey = timeKey;
    }


}
