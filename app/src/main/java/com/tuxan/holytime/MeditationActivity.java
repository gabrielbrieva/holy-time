package com.tuxan.holytime;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;

import com.tuxan.holytime.adapter.MeditationsLoader;
import com.tuxan.holytime.data.provider.MeditationProvider;

public class MeditationActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    String mMeditationId;
    String mStartId;

    Cursor mCursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_meditation_detail);

        getLoaderManager().initLoader(0, null, this);

        if (savedInstanceState == null) {
            if (getIntent() != null && getIntent().getData() != null) {
                mStartId = getIntent().getData().getPathSegments().get(1);
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null; //new MeditationsLoader(this, MeditationProvider.Meditations.withId(mMeditationId));
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    /*private class MeditationsPagerAdapter extends FragmentStatePagerAdapter {

        public MeditationsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mCursor.moveToPosition(position);
            return MeditationDetailFragment.newInstance(mCursor.getString(MeditationsLoader.Query._ID), position);
        }

        @Override
        public int getCount() {
            return (mCursor != null) ? mCursor.getCount() : 0;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem(container, position, object);
            mCurrentMeditationDetailFragment = (MeditationDetailFragment) object;
        }
    }*/
}
