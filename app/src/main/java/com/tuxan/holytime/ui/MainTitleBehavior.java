package com.tuxan.holytime.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuxan.holytime.R;

public class MainTitleBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    int mainToolbarHeight;
    int mainTitleTimeHeight;
    int titleContainerMarginLeft;

    private Context mContext;

    public MainTitleBehavior() {
        super();
    }

    public MainTitleBehavior(@Nullable Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;

        mainToolbarHeight = mContext.getResources().getDimensionPixelSize(R.dimen.main_sunset_valley_size);
        titleContainerMarginLeft = mContext.getResources().getDimensionPixelSize(R.dimen.main_title_container_margin_left);
        mainTitleTimeHeight = mContext.getResources().getDimensionPixelSize(R.dimen.main_title_time_height);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, LinearLayout child, View dependency) {
        return dependency instanceof AppBarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, LinearLayout child, View dependency) {

        AppBarLayout appBarLayout = (AppBarLayout) dependency;

        int maxScrollDistance = appBarLayout.getTotalScrollRange();
        float percentage = Math.abs(dependency.getY()) / maxScrollDistance;

        TextView tvTitleHoly = (TextView) child.findViewById(R.id.tv_main_title_holy);
        TextView tvTitleTime = (TextView) child.findViewById(R.id.tv_main_title_time);

        tvTitleTime.getLayoutParams().height = (int) Math.ceil(mainTitleTimeHeight - ((mainTitleTimeHeight - tvTitleHoly.getHeight()) * percentage));

        float currentTileSize = 30 - (10 * percentage);

        tvTitleTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentTileSize);
        tvTitleHoly.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentTileSize);

        CoordinatorLayout.LayoutParams containerLayoutParams = (CoordinatorLayout.LayoutParams)child.getLayoutParams();
        containerLayoutParams.setMargins(titleContainerMarginLeft, (int) Math.ceil(((mainToolbarHeight - child.getHeight()) / 2) * (1 - percentage) + (percentage * dpToPixels(16))), 0, 0);

        child.setLayoutParams(containerLayoutParams);

        return true;
    }

    private int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
    }
}
