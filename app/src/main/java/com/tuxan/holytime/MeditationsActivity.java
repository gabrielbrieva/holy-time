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
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuxan.holytime.adapter.MeditationsAdapter;
import com.tuxan.holytime.adapter.MeditationsLoader;
import com.tuxan.holytime.data.provider.MeditationProvider;
import com.tuxan.holytime.sync.MeditationSyncAdapter;

import java.util.Calendar;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MeditationsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        AppBarLayout.OnOffsetChangedListener {

    private static final String LOG_TAG = "MeditationsActivity";

    private int LOADER_ID = 0;

    @BindView(R.id.rv_meditations)
    RecyclerView mRecyclerView;

    @BindView(R.id.tbSunset)
    Toolbar mToolbar;

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.fl_main_title_container)
    FrameLayout flMainTitleContainer;

    @BindView(R.id.tv_main_title_holy)
    TextView tvTitleHoly;

    @BindView(R.id.tv_main_title_time)
    TextView tvTitleTime;

    @BindView(R.id.tv_toolbar_main_title_holy)
    TextView tvToolbarTitleHoly;

    @BindView(R.id.tv_toolbar_main_title_time)
    TextView tvToolbarTitleTime;

    @BindDimen(R.dimen.main_sunset_valley_size) int mainToolbarHeight;
    @BindDimen(R.dimen.main_title_container_margin_top) int titleContainerMarginTop;
    @BindDimen(R.dimen.main_title_container_margin_left) int titleContainerMarginLeft;
    @BindDimen(R.dimen.main_title_time_height) int titleHeight;

    private static final String IS_VALLEY_VISIBLE = "IS_VALLEY_VISIBLE";

    private static final float PERCENTAGE_TO_ANIMATE_SUN = 0.3f;
    private static final int ANIMATION_DURATION = 200;

    private boolean mIsValleyVisible = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null)
            mIsValleyVisible = savedInstanceState.getBoolean(IS_VALLEY_VISIBLE);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

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

        getLoaderManager().initLoader(LOADER_ID, null, this);

        MeditationSyncAdapter.initializeSyncAdapter(this);
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
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MeditationsLoader(this, MeditationProvider.Meditations.MEDITATIONS);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        MeditationsAdapter adapter = new MeditationsAdapter(data, this);
        adapter.setHasStableIds(true);

        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

        updateTitleContainer(percentage);
    }

    private void updateTitleContainer(float percentage) {

        tvTitleTime.setPadding((int)Math.ceil(tvTitleHoly.getWidth() + (dpToPixels(5) * percentage)), 0, 0, 0);
        tvTitleTime.getLayoutParams().height = (int)Math.ceil((tvTitleHoly.getHeight() * 2) - (tvTitleHoly.getHeight() * percentage));

        int currentTileSize = (int)Math.ceil(30 - (10 * percentage));

        tvTitleTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentTileSize);
        tvTitleHoly.setTextSize(TypedValue.COMPLEX_UNIT_SP, currentTileSize);

        CoordinatorLayout.LayoutParams containerLayoutParams = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        containerLayoutParams.setMargins(titleContainerMarginLeft, (int)Math.ceil(((mainToolbarHeight - (tvTitleHoly.getWidth() * 2))/2) * (1 - percentage)) + dpToPixels(16), 0, 0);

        flMainTitleContainer.setLayoutParams(containerLayoutParams);

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

    private int dpToPixels(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void animateColor(TextView tv, long duration, int toColor) {
        ValueAnimator valueAnimator = ObjectAnimator.ofInt(tv, "textColor", tv.getCurrentTextColor(), toColor);
        valueAnimator.setDuration(duration);
        valueAnimator.setEvaluator(new ArgbEvaluator());
        valueAnimator.start();
    }
}
