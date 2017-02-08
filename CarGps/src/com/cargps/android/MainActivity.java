package com.cargps.android;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cargps.android.interfaces.ILogin;
import com.cargps.android.model.activity.SerichActivity;
import com.cargps.android.model.fragment.AllEleCarFrament;
import com.cargps.android.model.fragment.BaseFragment;
import com.cargps.android.model.fragment.ChargFargment;
import com.cargps.android.model.fragment.MyEleCarFragment;
import com.cargps.android.model.fragment.ScanEleCarFragment;
import com.cargps.android.model.view.LeftMenu;
import com.fu.baseframe.utils.LogUtils;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_main)
public class MainActivity extends FragmentActivity {
    @ViewById
    DrawerLayout drawerLayout;

    @ViewById
    View eleCarLayout, myEleCarLayout, sweepLayout, chargLayout;

    @ViewById
    TextView titleTv;

    ScanEleCarFragment scanEleCarFragmet;
    //MakeCarFragment makeCarFragment;
    AllEleCarFrament allEleCarFrament;
    ChargFargment chargFargment;
    MyEleCarFragment myEleCarFragment;

    private int currIndex = 0;
    private Fragment[] fragments;

    int[] selectRes = new int[]{R.drawable.icon_ele_car_s, R.drawable.icon_my_ele_car_s, R.drawable.icon_sweep_s, R.drawable.icon_make_car_s, R.drawable.icon_charg_s};
    int[] normalRes = new int[]{R.drawable.icon_ele_car, R.drawable.icon_my_ele_car, R.drawable.icon_sweep, R.drawable.icon_make_car, R.drawable.icon_charg};

    ImageView[] selectImgs;
    @ViewById
    LinearLayout bottomLayout;
    @ViewById
    ImageView myEleCarImg, eleCarImg, sweepImg, makeCarImg, chargImg, backimg, rightimg;


    @ViewById
    public LeftMenu leftMenu;

    public String city;

    private long mExitTime;

    public boolean menuTag = false;

    View[] pageVies;
    String[] pageTitle = new String[]{"附近电滴", "我的电滴", "扫描电滴", "借车点"};

    @SuppressWarnings({"static-access", "deprecation"})
    @AfterViews
    public void initViews() {

        pageVies = new View[]{eleCarLayout, myEleCarLayout, sweepLayout, chargLayout};
        //MyApplication.getInstance().setSystemBar(this);

        MyApplication.getInstance().mainActivity = this;

        scanEleCarFragmet = com.cargps.android.model.fragment.ScanEleCarFragment_.builder().build();
        //makeCarFragment = com.cargps.android.model.fragment.MakeCarFragment_.builder().build();
        allEleCarFrament = com.cargps.android.model.fragment.AllEleCarFrament_.builder().build();
        chargFargment = com.cargps.android.model.fragment.ChargFargment_.builder().build();
        myEleCarFragment = com.cargps.android.model.fragment.MyEleCarFragment_.builder().build();

        fragments = new Fragment[]{allEleCarFrament, myEleCarFragment, scanEleCarFragmet, chargFargment};
        selectImgs = new ImageView[]{eleCarImg, myEleCarImg, sweepImg, chargImg};

        getSupportFragmentManager().beginTransaction().add(R.id.homeLayout, allEleCarFrament).
                show(allEleCarFrament)
                .commit();

        setRightImg(true, R.drawable.icon_write_serich);
        setSelectImg(0);
        findTextViewSetColor(eleCarLayout);

        ImageView leftImg = (ImageView) findViewById(R.id.backimg);
        leftImg.setImageResource(R.drawable.icon_menu);

        leftImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                openMenu();
            }
        });

        setTitleText("所有电滴");

        drawerLayout.setDrawerListener(new DrawerListener() {

            @Override
            public void onDrawerStateChanged(int arg0) {
                LogUtils.logDug("State" + arg0);
            }

            @Override
            public void onDrawerSlide(View arg0, float offset) {
                LogUtils.logDug("onDrawerSlide" + offset);
                View mainView = drawerLayout.getChildAt(0);

                if (!drawerLayout.isDrawerOpen(leftMenu)) {
                    mainView.setTranslationX(leftMenu.getMeasuredWidth() * offset);
                    mainView.invalidate();
                } else {
                    mainView.setTranslationX(leftMenu.getMeasuredWidth() * offset);
                    mainView.invalidate();
                }

            }

            @Override
            public void onDrawerOpened(View arg0) {
                LogUtils.logDug("onDrawerOpened");
            }

            @Override
            public void onDrawerClosed(View arg0) {
                LogUtils.logDug("onDrawerClosed");
            }
        });
    }

    @Click
    public void rightimg() {
        Fragment showFragment = fragments[currIndex];
        if (!(showFragment instanceof ScanEleCarFragment)) {
            com.cargps.android.model.activity.SerichActivity_.intent(this).
                    startForResult(SerichActivity.ADDR_REQUEST_CODE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MyApplication.getInstance().rootUserInfo != null && myEleCarFragment != null) {
            myEleCarFragment.getListCar();
        }

        MyApplication.isActivity = true;

        leftMenu.showUserInfo();
    }

    public void openMenu() {
        if (drawerLayout.isDrawerOpen(leftMenu)) {
            drawerLayout.closeDrawer(leftMenu);
        } else {
            drawerLayout.openDrawer(leftMenu);
        }

    }

    public void openIsMenu() {

    }

    public void closeLeftMenu() {

    }

    @Click
    public void myEleCarLayout(View v) {
        switchFragment(1);

    }

    @Click
    public void eleCarLayout(View v) {
        switchFragment(0);
    }

    @Click
    public void sweepLayout(View v) {
        switchFragment(2);
    }

    //	@Click
//	public void makeCarLayout(View v){
//		switchFragment(3);
//		findTextViewSetColor(v);
//		setTitleDrawable("预约电滴",  ContextCompat.getDrawable(this,R.drawable.icon_address),  ContextCompat.getDrawable(this, R.drawable.icon_arraw_down_write), null, null);
//
//	}
    @Click
    public void chargLayout(View v) {
        switchFragment(3);
    }


    public void switchFragment(int index) {
        if (index != currIndex) {

            Fragment showFragment = fragments[index];

            if (showFragment instanceof ILogin) {
                ILogin login = (ILogin) showFragment;
                if (login.isLogin()) {
                    if (!MyApplication.getInstance().isLogin()) {
                        executeFragment(index, showFragment);
                    } else {
                        MyApplication.isShowLoginPage = true;
                        com.cargps.android.model.activity.LoginActivity_.intent(this).flags(Intent.FLAG_ACTIVITY_NEW_TASK).start();
                    }
                }
            } else {

                executeFragment(index, showFragment);
            }

        }
    }

    private void executeFragment(int index, Fragment showFragment) {
        setTitleText(pageTitle[index]);
        Fragment hideFragment = fragments[currIndex];

        getSupportFragmentManager().beginTransaction().hide(hideFragment).commit();

        if (hideFragment instanceof AllEleCarFrament) {
            ((AllEleCarFrament) hideFragment).cancelOnMapChangeListener();
        }

        if (hideFragment instanceof ChargFargment) {
            ((ChargFargment) hideFragment).cancelOnMapChangeListener();
        }

        if (hideFragment instanceof ScanEleCarFragment) {
            getSupportFragmentManager().beginTransaction().remove(hideFragment).commit();
        }

        setSelectImg(index);
        findTextViewSetColor(pageVies[index]);

        if (!showFragment.isAdded()) {
            getSupportFragmentManager().beginTransaction().add(R.id.homeLayout, showFragment).commit();
        }
        getSupportFragmentManager().beginTransaction().show(showFragment).commit();

        if (showFragment instanceof AllEleCarFrament) {
            setRightImg(true, R.drawable.icon_write_serich);
            ((AllEleCarFrament) showFragment).addOnMapChangeListener();
        }

        if (showFragment instanceof ChargFargment) {
            setRightImg(true, R.drawable.icon_write_serich);
            ((ChargFargment) showFragment).addOnMapChangeListener();
        }

        if (showFragment instanceof MyEleCarFragment) {
            setRightImg(true, R.drawable.icon_write_serich);
        }

        if (showFragment instanceof ScanEleCarFragment) {
            setRightImg(false, R.drawable.icon_write_serich);
        }

        currIndex = index;
    }

    private void findTextViewSetColor(View child) {
        for (int i = 0; i < bottomLayout.getChildCount(); i++) {
            LinearLayout layout = (LinearLayout) bottomLayout.getChildAt(i);
            for (int j = 0; j < layout.getChildCount(); j++) {
                View v = layout.getChildAt(j);
                if (v instanceof TextView) {
                    TextView tv = (TextView) v;
                    if (layout == child) {
                        tv.setTextColor(ContextCompat.getColor(this, R.color.bg));
                    } else {
                        tv.setTextColor(ContextCompat.getColor(this, R.color.home_normal));
                    }
                }
            }
        }
    }

    private void setSelectImg(int index) {
        for (int i = 0; i < selectImgs.length; i++) {
            ImageView img = selectImgs[i];
            if (i == index) {
                img.setImageResource(selectRes[index]);
            } else {
                img.setImageResource(normalRes[i]);
            }
        }
    }

    public void setTitleText(String str) {
        titleTv.setText(str);
    }

    public void setTitleDrawable(String str, Drawable left, Drawable right, Drawable top, Drawable bottom) {
        titleTv.setText(str);
//		if(left != null){
//			left.setBounds(0, 0, left.getIntrinsicWidth(), left.getIntrinsicHeight());
//		}
//		if(right != null){
//			right.setBounds(0, 0, right.getIntrinsicWidth(), right.getIntrinsicHeight());
//		}
//		if(top != null){
//			top.setBounds(0, 0, top.getIntrinsicWidth(), top.getIntrinsicHeight());
//		}
//		if(bottom != null){
//			bottom.setBounds(0, 0, bottom.getIntrinsicWidth(), bottom.getIntrinsicHeight());
//		}

//		titleTv.setCompoundDrawables(left, top, right, bottom);
        titleTv.setCompoundDrawables(null, null, null, null);
    }

    public void setBackImg(boolean show, int resId) {
        if (show) {
            backimg.setVisibility(View.VISIBLE);
            backimg.setImageResource(resId);
        } else {
            backimg.setVisibility(View.GONE);
        }
    }

    public void setRightImg(boolean show, int resId) {
        if (show) {
            rightimg.setVisibility(View.VISIBLE);
            rightimg.setImageResource(resId);
        } else {
            rightimg.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        MyApplication.isActivity = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (drawerLayout.isDrawerOpen(leftMenu)) {
                drawerLayout.closeDrawer(leftMenu);
                return true;
            }
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "在按一次退出", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
            }
            return true;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == SerichActivity.ADDR_REQUEST_CODE) {
            if (resultCode == SerichActivity.ADDR_RESULT_CODE) {
                BaseFragment showFragment = (BaseFragment) fragments[currIndex];
                showFragment.serchPOI(data.getStringExtra("addr"));
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

}
