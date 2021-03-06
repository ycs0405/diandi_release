//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.cargps.android.model.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.amap.api.maps2d.MapView;
import com.cargps.android.R.layout;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class ChargFargment_
    extends com.cargps.android.model.fragment.ChargFargment
    implements HasViews, OnViewChangedListener
{

    private final OnViewChangedNotifier onViewChangedNotifier_ = new OnViewChangedNotifier();
    private View contentView_;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(onViewChangedNotifier_);
        init_(savedInstanceState);
        super.onCreate(savedInstanceState);
        OnViewChangedNotifier.replaceNotifier(previousNotifier);
    }

    @Override
    public View findViewById(int id) {
        if (contentView_ == null) {
            return null;
        }
        return contentView_.findViewById(id);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        contentView_ = super.onCreateView(inflater, container, savedInstanceState);
        if (contentView_ == null) {
            contentView_ = inflater.inflate(layout.fragment_charg_layout, container, false);
        }
        return contentView_;
    }

    @Override
    public void onDestroyView() {
        contentView_ = null;
        super.onDestroyView();
    }

    private void init_(Bundle savedInstanceState) {
        OnViewChangedNotifier.registerOnViewChangedListener(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onViewChangedNotifier_.notifyViewChanged(this);
    }

    public static ChargFargment_.FragmentBuilder_ builder() {
        return new ChargFargment_.FragmentBuilder_();
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        carControlImg = ((ImageView) hasViews.findViewById(com.cargps.android.R.id.carControlImg));
        currAddrTv = ((TextView) hasViews.findViewById(com.cargps.android.R.id.currAddrTv));
        disTv = ((TextView) hasViews.findViewById(com.cargps.android.R.id.disTv));
        chragCountTv = ((TextView) hasViews.findViewById(com.cargps.android.R.id.chragCountTv));
        zoomMaxImg = ((ImageView) hasViews.findViewById(com.cargps.android.R.id.zoomMaxImg));
        chragImg = ((ImageView) hasViews.findViewById(com.cargps.android.R.id.chragImg));
        CarCountTv = ((TextView) hasViews.findViewById(com.cargps.android.R.id.CarCountTv));
        zoomMinImg = ((ImageView) hasViews.findViewById(com.cargps.android.R.id.zoomMinImg));
        addressTv = ((TextView) hasViews.findViewById(com.cargps.android.R.id.addressTv));
        myLocationImg = ((ImageView) hasViews.findViewById(com.cargps.android.R.id.myLocationImg));
        mMapView = ((MapView) hasViews.findViewById(com.cargps.android.R.id.mMapView));
        windowInfo = ((FrameLayout) hasViews.findViewById(com.cargps.android.R.id.windowInfo));
        nameTv = ((TextView) hasViews.findViewById(com.cargps.android.R.id.nameTv));
        {
            View view = hasViews.findViewById(com.cargps.android.R.id.imgClose);
            if (view!= null) {
                view.setOnClickListener(new OnClickListener() {


                    @Override
                    public void onClick(View view) {
                        ChargFargment_.this.imgClose();
                    }

                }
                );
            }
        }
        initViews();
    }

    public static class FragmentBuilder_
        extends FragmentBuilder<ChargFargment_.FragmentBuilder_, com.cargps.android.model.fragment.ChargFargment>
    {


        @Override
        public com.cargps.android.model.fragment.ChargFargment build() {
            ChargFargment_ fragment_ = new ChargFargment_();
            fragment_.setArguments(args);
            return fragment_;
        }

    }

}
