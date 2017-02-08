package com.cargps.android.model.activity;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.cargps.android.R;
import com.widget.android.utils.ConvertUtils;
import com.widget.android.utils.SettingShareData;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_guide_layout)
public class GuideActivity extends BaseActivity {
    @ViewById
    ViewPager viewPager;

    @ViewById
    Button startBtn;

    @ViewById
    LinearLayout pointLayout;

    int[] imgsId = new int[]{R.drawable.guid_2, R.drawable.guid_3, R.drawable.guid_4};

    View[] views = new View[imgsId.length];

    @Extra
    public boolean ishowBtn = true;

    @SuppressWarnings("deprecation")
    @AfterViews
    public void initViews() {
        startBtn.setEnabled(ishowBtn);

        SettingShareData.getInstance(app).setKeyValue("guide", true);

        createImgs();
        createPoint();

        viewPager.setAdapter(new MyPagerAdapter());
        viewPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                if (ishowBtn) {
                    if (arg0 == views.length - 1) {
                        startBtn.setVisibility(View.VISIBLE);
                    } else {
                        startBtn.setVisibility(View.INVISIBLE);
                    }
                }
                changePointState(arg0);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }
        });
    }


    private void createImgs() {
        for (int i = 0; i < imgsId.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setScaleType(ScaleType.FIT_XY);
            imageView.setImageResource(imgsId[i]);
            views[i] = imageView;
        }
    }

    private void createPoint() {
        for (int i = 0; i < imgsId.length; i++) {
            View p = new View(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ConvertUtils.dip2px(this, 10), ConvertUtils.dip2px(this, 10));

            if (i == 0) {
                p.setBackgroundResource(R.drawable.shape_point_select);
            } else {
                p.setBackgroundResource(R.drawable.shape_point_normal);
            }
            if (i < imgsId.length - 1) {
                params.setMargins(0, 0, ConvertUtils.dip2px(this, 2), 0);
            }
            p.setLayoutParams(params);
            pointLayout.addView(p);
        }
    }

    private void changePointState(int i) {
        for (int j = 0; j < pointLayout.getChildCount(); j++) {
            View v = pointLayout.getChildAt(j);
            if (i == j) {
                v.setBackgroundResource(R.drawable.shape_point_select);
            } else {
                v.setBackgroundResource(R.drawable.shape_point_normal);
            }
        }
    }

    class MyPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return views.length;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = views[position];
            container.addView(v);
            return v;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Click
    public void startBtn() {
        com.cargps.android.MainActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
        finish();
    }
}
