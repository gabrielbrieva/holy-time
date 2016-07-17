package com.tuxan.holytime;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.tuxan.holytime.adapter.MeditationLoader;
import com.tuxan.holytime.api.APIService;
import com.tuxan.holytime.api.APIServiceFactory;
import com.tuxan.holytime.data.dto.MeditationContent;
import com.tuxan.holytime.data.dto.Page;
import com.tuxan.holytime.data.provider.MeditationProvider;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class MeditationActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String MEDITATION_TITLE_KEY = "MEDITATION_TITLE_KEY";
    private final int LOADER_ID = 1;

    @BindView(R.id.tbDetail)
    Toolbar mToolbar;

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    String mMeditationId;
    String mMeditationTitle;

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
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);

        } else {
            mMeditationTitle = savedInstanceState.getString(MEDITATION_TITLE_KEY);
            getSupportActionBar().setTitle(mMeditationTitle);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(MEDITATION_TITLE_KEY, mMeditationTitle);
    }

    private void startDetailFragment(MeditationContent meditationContent) {
        MeditationFragment fragment = MeditationFragment.newInstance();

        if (meditationContent != null)
        {
            mMeditationTitle = meditationContent.getTitle();
            getSupportActionBar().setTitle(mMeditationTitle);

            Bundle arguments = new Bundle();
            arguments.putParcelable(MeditationFragment.MEDITATION_CONTENT_KEY, meditationContent);

            fragment.setArguments(arguments);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.detail_container, fragment)
                .commit();
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

            MeditationContent m = new MeditationContent();
            m.setId(data.getString(MeditationLoader.DetailQuery._ID));
            m.setWeekNumber(data.getInt(MeditationLoader.DetailQuery.WEEK_NUMBER));
            m.setTitle(data.getString(MeditationLoader.DetailQuery.TITLE));
            m.setAuthor(data.getString(MeditationLoader.DetailQuery.AUTHOR));
            m.setVerse(data.getString(MeditationLoader.DetailQuery.VERSE));
            m.setBody(data.getString(MeditationLoader.DetailQuery.BODY));

            startDetailFragment(m);

        } else {
            loadFromAPI();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void loadFromAPI() {
        if (Utils.isNetworkConnected(this)) {
            new AsyncTask<Void, Void, MeditationContent>() {
                @Override
                protected MeditationContent doInBackground(Void... params) {

                    APIService apiService = APIServiceFactory.createService(getString(R.string.api_key));

                    try {
                        Response<MeditationContent> result = apiService.getContentDetail(mMeditationId).execute();

                        if (result.isSuccessful()) {
                            return result.body();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    return null;
                }

                @Override
                protected void onPostExecute(MeditationContent meditationContent) {
                    startDetailFragment(meditationContent);
                }
            }.execute();
        }
    }
}
