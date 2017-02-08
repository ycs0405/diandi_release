package com.cargps.android.utils;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.cargps.android.R;

public class TitleBar {
    private TextView titleTv, rightTv;
    private View rootView;
    private TitleCallBack titleCallBack;
    private ImageView leftImg;
    private ImageView rightImg;

    public void initView(View view, boolean showBack) {
        this.rootView = view;
        titleTv = (TextView) rootView.findViewById(R.id.titleTv);
        if (titleTv == null) return;
        rightTv = (TextView) rootView.findViewById(R.id.rightTv);

        leftImg = (ImageView) rootView.findViewById(R.id.backimg);
        leftImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (titleCallBack != null) {
                    titleCallBack.backClick((ImageView) rootView.findViewById(R.id.backimg));
                }

            }
        });

        rightImg = (ImageView) rootView.findViewById(R.id.rightimg);
        rightImg.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (titleCallBack != null) {
                    titleCallBack.rightClick((ImageView) rootView.findViewById(R.id.rightimg));
                }
            }
        });

        if (showBack) {
            rootView.findViewById(R.id.backimg).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.backimg).setVisibility(View.GONE);
        }
    }

    public void setTitle(String title) {
        titleTv.setText(title);
    }

    public void addTitleCallBack(TitleCallBack callBack) {
        this.titleCallBack = callBack;
        if (this.titleCallBack != null) {
            this.titleCallBack.leftImg(leftImg);
            this.titleCallBack.rightImg(rightImg);
            this.titleCallBack.rightTv(rightTv);
        }
    }

    public interface TitleCallBack {
        public void backClick(ImageView backImg);

        public void rightClick(ImageView rightImg);

        public void leftImg(ImageView backImg);

        public void rightImg(ImageView rightImg);

        public void rightTv(TextView rightTv);
    }

    public void showBack(boolean show) {
        if (show) {
            rootView.findViewById(R.id.backimg).setVisibility(View.VISIBLE);
        } else {
            rootView.findViewById(R.id.backimg).setVisibility(View.GONE);
        }
    }

    public TextView getTitleTv() {
        return titleTv;
    }


}
