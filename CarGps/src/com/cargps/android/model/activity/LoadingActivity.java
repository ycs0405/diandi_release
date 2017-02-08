package com.cargps.android.model.activity;

import android.content.Intent;
import android.os.Handler;

import com.cargps.android.R;
import com.widget.android.utils.SettingShareData;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;

/***
 * 启动加载
 *
 * @author fu
 * @create date 2016.05.11
 */
@EActivity(R.layout.activity_loading_layout)
public class LoadingActivity extends BaseActivity {
    Handler handler = new Handler();

    @AfterViews
    public void initViews() {
        handler.postDelayed(new Runnable() {
            public void run() {
                finish();

                if (!SettingShareData.getInstance(app).getKeyValueBoolean("guide", false)) {
                    com.cargps.android.model.activity.GuideActivity_.intent(LoadingActivity.this).ishowBtn(true).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
                } else {
                    com.cargps.android.MainActivity_.intent(LoadingActivity.this).flags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP).start();
                }

            }
        }, 3 * 1000);
    }
}
