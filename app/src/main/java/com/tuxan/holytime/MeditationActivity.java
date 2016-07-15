package com.tuxan.holytime;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.tuxan.holytime.adapter.MeditationLoader;
import com.tuxan.holytime.data.provider.MeditationProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeditationActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final int LOADER_ID = 1;

    @BindView(R.id.tbDetail)
    Toolbar mToolbar;

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    String mMeditationId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.meditation_detail_activity);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mMeditationId = getIntent().getData().getPathSegments().get(1);
            }
        }

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mMeditationId != null)
            return new MeditationLoader(this, MeditationProvider.Meditations.withId(mMeditationId));

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
