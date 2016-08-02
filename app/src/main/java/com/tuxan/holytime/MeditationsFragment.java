package com.tuxan.holytime;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tuxan.holytime.adapter.MeditationsAdapter;
import com.tuxan.holytime.adapter.MeditationsLoader;
import com.tuxan.holytime.adapter.MeditationsPagerAdapter;
import com.tuxan.holytime.api.APIService;
import com.tuxan.holytime.api.APIServiceFactory;
import com.tuxan.holytime.data.dto.MeditationContent;
import com.tuxan.holytime.data.dto.Page;
import com.tuxan.holytime.data.provider.MeditationProvider;
import com.tuxan.holytime.utils.Utils;

import java.io.IOException;
import java.util.List;

import butterknife.BindInt;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class MeditationsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = "MeditationsFragment";
    static final String FRAGMENT_TYPE = "FRAGMENT_TYPE";

    private final int CURRENT_LOADER_ID = 0;
    private final int FAVORITE_LOADER_ID = 1;

    private int fragmentType;

    @BindView(R.id.tv_empty_msg)
    TextView mTvEmptyMsg;

    @BindView(R.id.rv_meditations)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindInt(R.integer.meditations_row)
    int meditationsRow;

    MeditationsAdapter mMeditationsAdapter;
    StaggeredGridLayoutManager mStaggeredGridLayoutManager;
    EndlessRecyclerViewScrollListener mEndlessScrollListener;
    SwipeRefreshLayout.OnRefreshListener mOnRefreshListener;

    public MeditationsFragment() { }

    public static MeditationsFragment newInstance(int type) {
        MeditationsFragment fragment = new MeditationsFragment();
        Bundle args = new Bundle();
        args.putInt(FRAGMENT_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        Bundle args = getArguments();

        if (args != null) {
            fragmentType = args.getInt(FRAGMENT_TYPE);
        }

        meditationsRow = getResources().getInteger(R.integer.meditations_row);

        mMeditationsAdapter = new MeditationsAdapter(getActivity());
        mMeditationsAdapter.setHasStableIds(true);
        mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(meditationsRow, StaggeredGridLayoutManager.VERTICAL);

        if (fragmentType == MeditationsPagerAdapter.CURRENT_LIST) {
            mEndlessScrollListener = new EndlessRecyclerViewScrollListener(mStaggeredGridLayoutManager) {

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
            };
        }

        mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (fragmentType == MeditationsPagerAdapter.CURRENT_LIST) {
                    mEndlessScrollListener.restart();
                    getLoaderManager().restartLoader(CURRENT_LOADER_ID, null, MeditationsFragment.this);
                } else if (fragmentType == MeditationsPagerAdapter.FAVORITE_LIST) {
                    getLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, MeditationsFragment.this);
                }
            }
        };

        if (fragmentType == MeditationsPagerAdapter.CURRENT_LIST) {
            getLoaderManager().restartLoader(CURRENT_LOADER_ID, null, this);
        } else if (fragmentType == MeditationsPagerAdapter.FAVORITE_LIST) {
            getLoaderManager().restartLoader(FAVORITE_LOADER_ID, null, this);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.meditations_fragment, container, false);

        ButterKnife.bind(this, view);

        // to fix transition issue
        mMeditationsAdapter.setContext(getActivity());

        mStaggeredGridLayoutManager.setSpanCount(meditationsRow);

        mRecyclerView.setAdapter(mMeditationsAdapter);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);

        if (fragmentType == MeditationsPagerAdapter.CURRENT_LIST)
            mRecyclerView.addOnScrollListener(mEndlessScrollListener);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRecyclerView.setAdapter(null);
        mRecyclerView.setLayoutManager(null);

        if (fragmentType == MeditationsPagerAdapter.CURRENT_LIST)
            mRecyclerView.removeOnScrollListener(mEndlessScrollListener);

        mSwipeRefreshLayout.setOnRefreshListener(null);
    }

    private void loadMeditationsFromApi(final int page) {

        if (!Utils.isNetworkConnected(getActivity()))
        {
            showNoInternetAccessToast();
            return;
        }

        mMeditationsAdapter.setIsLoading(true);

        new AsyncTask<Void, Void, List<MeditationContent>>() {
            @Override
            protected List<MeditationContent> doInBackground(Void... params) {

                APIService apiService = APIServiceFactory.createService(getString(R.string.api_key));

                int weekNumber = Utils.getCurrentWeekNumber();

                try {
                    Response<Page<MeditationContent>> result = apiService.getPaginatedContent(weekNumber, page).execute();

                    if (result.isSuccessful()) {
                        return result.body().getItems();
                    } else {
                        showNoInternetAccessToast();
                    }

                } catch (IOException e) {
                    Log.e(LOG_TAG, e.getMessage());
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

    private void showNoInternetAccessToast() {
        Toast.makeText(getActivity(), R.string.no_internet_connection, Toast.LENGTH_SHORT).show();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new MeditationsLoader(getActivity(), MeditationProvider.Meditations.meditationList, Utils.getCurrentWeekNumber(), id != CURRENT_LOADER_ID);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        boolean resetApiCursor = fragmentType == MeditationsPagerAdapter.CURRENT_LIST &&
                mEndlessScrollListener != null && mEndlessScrollListener.getCurrentPage() == 0;

        mMeditationsAdapter.swapCursor(data, resetApiCursor);

        if (data != null && data.getCount() > 0)
        {
            mTvEmptyMsg.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        } else {
            mTvEmptyMsg.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        }

        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (fragmentType == MeditationsPagerAdapter.CURRENT_LIST)
            mEndlessScrollListener.restart();

        mMeditationsAdapter.swapCursor(null, true);
    }
}
