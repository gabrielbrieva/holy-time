package com.tuxan.holytime.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class DetailTitleBehavior extends CoordinatorLayout.Behavior<TextView> {

    Context mContext;

    public DetailTitleBehavior() {
        super();
    }

    public DetailTitleBehavior(@Nullable Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, TextView child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, TextView child, View dependency) {

        AppBarLayout appBarLayout = (AppBarLayout) dependency;

        int maxScrollDistance = appBarLayout.getTotalScrollRange();
        float percentage = Math.abs(dependency.getY()) / maxScrollDistance;

        child.setTextSize(30 - (10 * percentage));

        return true;
    }
}
