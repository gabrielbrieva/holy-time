package com.tuxan.holytime.ui;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuxan.holytime.R;

public class MainTitleBehavior extends CoordinatorLayout.Behavior<LinearLayout> {

    private static final String IS_VALLEY_VISIBLE = "IS_VALLEY_VISIBLE";
    private static final float PERCENTAGE_TO_ANIMATE_SUN = 0.3f;
    private static final int ANIMATION_DURATION = 200;
    private boolean mIsValleyVisible = true;

    int mainToolbarHeight;
    int mainTitleTimeHeight;
    int titleContainerMarginLeft;

    float mStartAppBarLayoutPosition;

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

        /*if (percentage >= PERCENTAGE_TO_ANIMATE_SUN) {
            if (mIsValleyVisible) {
                animateColor(tvTitleHoly, ANIMATION_DURATION, ContextCompat.getColor(mContext, R.color.mainTitleCollapsed));
                animateColor(tvTitleTime, ANIMATION_DURATION, ContextCompat.getColor(mContext, R.color.mainTitleCollapsed));
                mIsValleyVisible = false;
            }

        } else if (!mIsValleyVisible) {
            animateColor(tvTitleHoly, ANIMATION_DURATION, ContextCompat.getColor(mContext, R.color.mainTitleHoly));
            animateColor(tvTitleTime, ANIMATION_DURATION, ContextCompat.getColor(mContext, R.color.mainTitleTime));
            mIsValleyVisible = true;
        }

        if (percentage < 1) {
            //tvToolbarTitleHoly.setVisibility(View.INVISIBLE);
            //tvToolbarTitleTime.setVisibility(View.INVISIBLE);
            tvTitleHoly.setVisibility(View.VISIBLE);
            tvTitleTime.setVisibility(View.VISIBLE);
        } else {
            //tvToolbarTitleHoly.setVisibility(View.VISIBLE);
            //tvToolbarTitleTime.setVisibility(View.VISIBLE);
            tvTitleHoly.setVisibility(View.INVISIBLE);
            tvTitleTime.setVisibility(View.INVISIBLE);
        }*/

        return true;
    }

    private void animateColor(TextView tv, long duration, int toColor) {
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(tv, "textColor", tv.getCurrentTextColor(), toColor);
        valueAnimator.setDuration(duration);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.start();
    }

    private int dpToPixels(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, mContext.getResources().getDisplayMetrics());
    }
}
