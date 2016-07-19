package com.tuxan.holytime;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tuxan.holytime.adapter.MeditationLoader;
import com.tuxan.holytime.adapter.MeditationsLoader;
import com.tuxan.holytime.api.APIService;
import com.tuxan.holytime.api.APIServiceFactory;
import com.tuxan.holytime.data.dto.MeditationContent;
import com.tuxan.holytime.data.provider.MeditationProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Response;

public class MeditationFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,
        AppBarLayout.OnOffsetChangedListener {

    public static final String MEDITATION_ID_KEY = "MEDITATION_ID_KEY";
    public static final String MEDITATION_TITLE_KEY = "MEDITATION_TITLE_KEY";
    private static final String MEDITATION_CONTENT_KEY = "MEDITATION_CONTENT_KEY";

    private final int LOADER_ID = 1;

    @BindView(R.id.tbDetail)
    Toolbar mToolbar;

    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;

    @BindView(R.id.app_bar_layout)
    AppBarLayout mAppBarLayout;

    @BindView(R.id.toolbarBackground)
    View toolbarBackground;

    @BindView(R.id.tv_detail_title)
    TextView mTvDetailTitle;

    @BindView(R.id.tv_meditation_verse)
    TextView mTvDetailVerse;

    @BindView(R.id.tv_meditation_date)
    TextView mTvMeditationDate;

    @BindView(R.id.tv_meditation_content)
    TextView mTvMeditationContent;

    @BindView(R.id.tv_meditation_author)
    TextView mTvMeditationAuthor;


    @BindDimen(R.dimen.margin_left_detail_title) int titleLeft;
    @BindDimen(R.dimen.margin_right_detail_title) int titleRight;
    @BindDimen(R.dimen.margin_bottom_detail_title) int titleBottom;

    String mMeditationId;
    String mMeditationTitle;
    MeditationContent mMeditationContent;

    static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    int titleCollapsedSize = 45;

    public MeditationFragment() {
        setHasOptionsMenu(true);
    }

    public static MeditationFragment newInstance() {
        MeditationFragment fragment = new MeditationFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arguments = getArguments();
        if (arguments != null && savedInstanceState == null) {
            mMeditationTitle = arguments.getString(MEDITATION_TITLE_KEY);
            mMeditationId = arguments.getString(MEDITATION_ID_KEY);

            getLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meditation_detail_fragment, container, false);

        TypedValue tv = new TypedValue();
        if (getActivity().getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            titleCollapsedSize = TypedValue.complexToDimensionPixelSize(tv.data,getResources().getDisplayMetrics());

        if (savedInstanceState != null) {
            mMeditationContent = savedInstanceState.getParcelable(MEDITATION_CONTENT_KEY);
        }

        ButterKnife.bind(this, view);

        Typeface titleTypeFace = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Bold.ttf");
        Typeface typeFace = Typeface.createFromAsset(getActivity().getAssets(), "RobotoSlab-Regular.ttf");

        mTvDetailTitle.setTypeface(titleTypeFace);
        mTvDetailVerse.setTypeface(typeFace);
        mTvMeditationDate.setTypeface(typeFace);
        mTvMeditationContent.setTypeface(typeFace);
        mTvMeditationAuthor.setTypeface(typeFace);

        mAppBarLayout.addOnOffsetChangedListener(this);

        mCollapsingToolbarLayout.setTitleEnabled(false);
        mToolbar.setTitle("");

        ((AppCompatActivity) getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (mMeditationTitle != null) {
            mCollapsingToolbarLayout.setTitle(mMeditationTitle);
            mTvDetailTitle.setText(mMeditationTitle);
            // verse too ??

            // init transition ... using current title
        } else {
            fillMeditationContent();
        }

        return view;
    }

    private void fillMeditationContent() {
        if (mMeditationContent != null)
        {
            mTvDetailTitle.setText(mMeditationContent.getTitle());
            mCollapsingToolbarLayout.setTitle(mMeditationContent.getTitle());

            Calendar c = Calendar.getInstance();
            c.set(Calendar.DAY_OF_WEEK, Calendar.FRIDAY);
            c.set(Calendar.WEEK_OF_YEAR, mMeditationContent.getWeekNumber());
            mTvMeditationDate.setText(dateFormat.format(c.getTime()));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mTvDetailVerse.setText(Html.fromHtml(mMeditationContent.getVerse().trim(), Html.FROM_HTML_MODE_LEGACY));
                mTvMeditationContent.setText(Html.fromHtml(mMeditationContent.getBody().trim(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                mTvDetailVerse.setText(Html.fromHtml(mMeditationContent.getVerse().trim()));
                mTvMeditationContent.setText(Html.fromHtml(mMeditationContent.getBody().trim()));
            }

            mTvMeditationAuthor.setText(mMeditationContent.getAuthor());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MEDITATION_CONTENT_KEY, mMeditationContent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed(); //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (mMeditationId != null)
            return new MeditationLoader(getActivity(), MeditationProvider.Meditations.withId(mMeditationId));

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {

            mMeditationContent = new MeditationContent();
            mMeditationContent.setId(data.getString(MeditationLoader.DetailQuery._ID));
            mMeditationContent.setWeekNumber(data.getInt(MeditationLoader.DetailQuery.WEEK_NUMBER));
            mMeditationContent.setTitle(data.getString(MeditationLoader.DetailQuery.TITLE));
            mMeditationContent.setAuthor(data.getString(MeditationLoader.DetailQuery.AUTHOR));
            mMeditationContent.setVerse(data.getString(MeditationLoader.DetailQuery.VERSE));
            mMeditationContent.setBody(data.getString(MeditationLoader.DetailQuery.BODY));

            fillMeditationContent();

        } else {
            // if data is not locally must call the Web API
            loadFromAPI();
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void loadFromAPI() {
        if (Utils.isNetworkConnected(getActivity())) {
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
                    if (meditationContent != null) {
                        mMeditationContent = meditationContent;
                        fillMeditationContent();
                    }
                }
            }.execute();
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {

        if (mCollapsingToolbarLayout != null) {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

            mCollapsingToolbarLayout.setTitleEnabled(percentage == 1);

            toolbarBackground.setAlpha(percentage);
        }

        /*if (mTvDetailTitle != null) {
            int maxScroll = appBarLayout.getTotalScrollRange();
            float percentage = (float) Math.abs(verticalOffset) / (float) maxScroll;

            mTvDetailTitle.setTextSize(30 - (10 * percentage));
            CollapsingToolbarLayout.LayoutParams layoutParams = (CollapsingToolbarLayout.LayoutParams) mTvDetailTitle.getLayoutParams();

            int start = (titleLeft / 2) + (int)((titleLeft / 2) * percentage);
            layoutParams.setMargins(start, 0, titleRight, (int)(titleBottom * (1 - percentage)));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                layoutParams.setMarginStart(start);
                layoutParams.setMarginEnd(titleRight);
            }
        }*/
    }
}
