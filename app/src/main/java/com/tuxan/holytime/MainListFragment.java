package com.tuxan.holytime;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tuxan.holytime.adapter.MeditationsAdapter;
import com.tuxan.holytime.adapter.MeditationsLoader;
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

public class MainListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "MainListFragment";

    private int LOADER_ID = 0;

    @BindView(R.id.rv_meditations)
    RecyclerView mRecyclerView;

    MeditationsAdapter mMeditationsAdapter;
    LinearLayoutManager mLinearLayoutManager;

    public MainListFragment() { }

    public static MainListFragment newInstance() {
        MainListFragment fragment = new MainListFragment();

        // TODO: set params ...

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(LOG_TAG, "onCreate with save instance " + (savedInstanceState == null ? "null" : "not null"));

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreateView with save instance " + (savedInstanceState == null ? "null" : "not null"));

        View view = inflater.inflate(R.layout.fragment_main_list, container, false);

        ButterKnife.bind(this, view);

        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mMeditationsAdapter = new MeditationsAdapter(getActivity());
        mMeditationsAdapter.setHasStableIds(true);

        mRecyclerView.setAdapter(mMeditationsAdapter);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(mLinearLayoutManager) {

            @Override
            public boolean isLoading() {
                return mMeditationsAdapter.isLoading();
            }

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (!mMeditationsAdapter.isLoading()) {
                    Log.d(LOG_TAG, "endless page = " + page + " totalCount = " + totalItemsCount);

                    loadMeditationsFromApi(page);
                }
            }
        });

        getLoaderManager().initLoader(LOADER_ID, null, this);

        return view;
    }

    private void loadMeditationsFromApi(final int page) {

        mMeditationsAdapter.setIsLoading(true);

        new AsyncTask<Void, Void, List<MeditationContent>>() {
            @Override
            protected List<MeditationContent> doInBackground(Void... params) {

                APIService apiService = APIServiceFactory.createService(getString(R.string.api_key));

                Calendar c = Calendar.getInstance();
                int weekNumber = c.get(Calendar.WEEK_OF_YEAR);

                try {
                    Response<Page<MeditationContent>> result = apiService.getPaginatedContent(weekNumber, page).execute();

                    if (result.isSuccessful()) {
                        return result.body().getItems();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(List<MeditationContent> meditationContents) {
                mMeditationsAdapter.setIsLoading(false);
                if (meditationContents != null)
                    mMeditationsAdapter.addMeditations(meditationContents);
            }
        }.execute();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MeditationsLoader(getActivity(), MeditationProvider.Meditations.MEDITATIONS);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMeditationsAdapter.setCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }
}
