package com.tuxan.holytime;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.LoaderManager;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.tuxan.holytime.adapter.MeditationsAdapter;
import com.tuxan.holytime.adapter.MeditationsLoader;
import com.tuxan.holytime.api.APIService;
import com.tuxan.holytime.api.APIServiceFactory;
import com.tuxan.holytime.data.dto.MeditationContent;
import com.tuxan.holytime.data.dto.Page;
import com.tuxan.holytime.data.provider.MeditationProvider;
import com.tuxan.holytime.sync.MeditationSyncAdapter;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class MeditationsActivity extends AppCompatActivity implements /*LoaderManager.LoaderCallbacks<Cursor>,*/
        AppBarLayout.OnOffsetChangedListener {

    private static final String LOG_TAG = "MeditationsActivity";

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

    static final String FRAGMENT_TAG = "FRAGMENT_TAG";
    MainListFragment mMainListFragment;

    private static final String IS_VALLEY_VISIBLE = "IS_VALLEY_VISIBLE";

    private static final float PERCENTAGE_TO_ANIMATE_SUN = 0.3f;
    private static final int ANIMATION_DURATION = 200;

    private boolean mIsValleyVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            mIsValleyVisible = savedInstanceState.getBoolean(IS_VALLEY_VISIBLE);

        } else {
            mMainListFragment = MainListFragment.newInstance();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_list_container, mMainListFragment, FRAGMENT_TAG)
                    .commit();

            MeditationSyncAdapter.initializeSyncAdapter(this);
        }

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        mAppBarLayout.addOnOffsetChangedListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            } else {
                calculateSunsetDateTime();
            }
        } else {
            calculateSunsetDateTime();
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(IS_VALLEY_VISIBLE, mIsValleyVisible);
        super.onSaveInstanceState(outState);
    }

    private void calculateSunsetDateTime() {
        Calendar c = Utils.getTimeOfSunset(this);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0 && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            calculateSunsetDateTime();
        }
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
