package com.tuxan.holytime;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.tuxan.holytime.adapter.MeditationsPagerAdapter;
import com.tuxan.holytime.preferences.SettingsActivity;
import com.tuxan.holytime.sync.MeditationSyncAdapter;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MeditationsActivity extends AppCompatActivity implements
        AppBarLayout.OnOffsetChangedListener {

    private static final String LOG_TAG = "MeditationsActivity";

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.tbSunset)
    Toolbar mToolbar;

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.tv_main_title_holy)
    TextView tvTitleHoly;

    @BindView(R.id.tv_main_title_time)
    TextView tvTitleTime;

    @BindView(R.id.tv_toolbar_main_title_holy)
    TextView tvToolbarTitleHoly;

    @BindView(R.id.tv_toolbar_main_title_time)
    TextView tvToolbarTitleTime;

    @BindView(R.id.tabs)
    TabLayout mTabLayout;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    MeditationsPagerAdapter mPagerAdapter;

    private static final String IS_VALLEY_VISIBLE = "IS_VALLEY_VISIBLE";

    private static final float PERCENTAGE_TO_ANIMATE_SUN = 0.6f;
    private static final int ANIMATION_DURATION = 200;

    @BindDimen(R.dimen.main_scrim_height)
    int scrimHeight;

    private boolean mIsValleyVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meditations_activity);

        if (savedInstanceState != null){
            mIsValleyVisible = savedInstanceState.getBoolean(IS_VALLEY_VISIBLE);
        } else {
            MeditationSyncAdapter.initializeSyncAdapter(this);
        }

        ButterKnife.bind(this);

        mPagerAdapter = new MeditationsPagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        mCollapsingToolbarLayout.setScrimVisibleHeightTrigger(scrimHeight);

        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mAppBarLayout.addOnOffsetChangedListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_VALLEY_VISIBLE, mIsValleyVisible);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        updateTitleContainer(percentage);
    }

    private void updateTitleContainer(float percentage) {

        if (percentage >= PERCENTAGE_TO_ANIMATE_SUN) {
            if (mIsValleyVisible) {
                animateColor(tvTitleHoly, ANIMATION_DURATION, ContextCompat.getColor(this, R.color.mainTitleCollapsed));
                animateColor(tvTitleTime, ANIMATION_DURATION, ContextCompat.getColor(this, R.color.mainTitleCollapsed));
                mIsValleyVisible = false;
            }

        } else if (!mIsValleyVisible) {
            animateColor(tvTitleHoly, ANIMATION_DURATION, ContextCompat.getColor(this, R.color.mainTitleHoly));
            animateColor(tvTitleTime, ANIMATION_DURATION, ContextCompat.getColor(this, R.color.mainTitleTime));
            mIsValleyVisible = true;
        }

        if (percentage < 1) {
            tvToolbarTitleHoly.setVisibility(View.INVISIBLE);
            tvToolbarTitleTime.setVisibility(View.INVISIBLE);
            tvTitleHoly.setVisibility(View.VISIBLE);
            tvTitleTime.setVisibility(View.VISIBLE);
        } else {
            tvToolbarTitleHoly.setVisibility(View.VISIBLE);
            tvToolbarTitleTime.setVisibility(View.VISIBLE);
            tvTitleHoly.setVisibility(View.INVISIBLE);
            tvTitleTime.setVisibility(View.INVISIBLE);
        }

    }

    private void animateColor(TextView tv, long duration, int toColor) {
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(tv, "textColor", tv.getCurrentTextColor(), toColor);
        valueAnimator.setDuration(duration);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.start();
    }
}
