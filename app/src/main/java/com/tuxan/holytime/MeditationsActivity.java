package com.tuxan.holytime;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.tuxan.holytime.adapter.MeditationsAdapter;
import com.tuxan.holytime.adapter.MeditationsLoader;
import com.tuxan.holytime.data.provider.MeditationColumns;
import com.tuxan.holytime.data.provider.MeditationProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MeditationsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private int LOADER_ID = 0;

    @BindView(R.id.rv_meditations)
    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        getLoaderManager().initLoader(LOADER_ID, null, this);
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
}
