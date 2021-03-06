//
// DO NOT EDIT THIS FILE.Generated using AndroidAnnotations 3.3.
//  You can create a larger work that contains this file and distribute that work under terms of your choice.
//


package com.cargps.android.model.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.amap.api.maps2d.MapView;
import com.cargps.android.R.layout;
import org.androidannotations.api.builder.FragmentBuilder;
import org.androidannotations.api.view.HasViews;
import org.androidannotations.api.view.OnViewChangedListener;
import org.androidannotations.api.view.OnViewChangedNotifier;

public final class MakeCarFragment_
    extends com.cargps.android.model.fragment.MakeCarFragment
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
            contentView_ = inflater.inflate(layout.fragment_make_car_layout, container, false);
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

    public static MakeCarFragment_.FragmentBuilder_ builder() {
        return new MakeCarFragment_.FragmentBuilder_();
    }

    @Override
    public void onViewChanged(HasViews hasViews) {
        carControlImg = ((ImageView) hasViews.findViewById(com.cargps.android.R.id.carControlImg));
        mMapView = ((MapView) hasViews.findViewById(com.cargps.android.R.id.mMapView));
        chraglayout = hasViews.findViewById(com.cargps.android.R.id.chraglayout);
        chargInfoTv = ((TextView) hasViews.findViewById(com.cargps.android.R.id.chargInfoTv));
        myLocationImg = ((ImageView) hasViews.findViewById(com.cargps.android.R.id.myLocationImg));
        zoomMaxImg = ((ImageView) hasViews.findViewById(com.cargps.android.R.id.zoomMaxImg));
        zoomMinImg = ((ImageView) hasViews.findViewById(com.cargps.android.R.id.zoomMinImg));
        initViews();
    }

    public static class FragmentBuilder_
        extends FragmentBuilder<MakeCarFragment_.FragmentBuilder_, com.cargps.android.model.fragment.MakeCarFragment>
    {


        @Override
        public com.cargps.android.model.fragment.MakeCarFragment build() {
            MakeCarFragment_ fragment_ = new MakeCarFragment_();
            fragment_.setArguments(args);
            return fragment_;
        }

    }

}
